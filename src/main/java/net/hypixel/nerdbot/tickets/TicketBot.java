package net.hypixel.nerdbot.tickets;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.hypixel.nerdbot.discord.AbstractDiscordBot;
import net.hypixel.nerdbot.discord.api.feature.BotFeature;
import net.hypixel.nerdbot.discord.api.feature.SchedulableFeature;
import net.hypixel.nerdbot.discord.config.DiscordBotConfig;
import net.hypixel.nerdbot.discord.config.FeatureConfig;
import net.hypixel.nerdbot.discord.config.NerdBotConfig;
import net.hypixel.nerdbot.marmalade.exception.RepositoryException;
import net.hypixel.nerdbot.marmalade.storage.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hypixel.nerdbot.discord.util.DiscordBotEnvironment;
import net.hypixel.nerdbot.tickets.listener.TicketListener;
import net.hypixel.nerdbot.tickets.service.TicketService;
import net.hypixel.nerdbot.tickets.config.TicketBotConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Standalone ticket bot that handles ticket creation, management, and lifecycle.
 * Uses the same bot token as the main NerdBot but runs as a separate process
 * focused exclusively on the ticket system.
 */
@Slf4j
public class TicketBot extends AbstractDiscordBot {

    private static final Logger TICKET_LOG = LoggerFactory.getLogger(TicketBot.class);

    @Override
    protected @NotNull Class<? extends DiscordBotConfig> getConfigClass() {
        return TicketBotConfig.class;
    }

    @Override
    public TicketBotConfig getConfig() {
        return (TicketBotConfig) super.getConfig();
    }

    /**
     * Static helper to get the TicketBotConfig from the current bot instance.
     */
    public static TicketBotConfig config() {
        return (TicketBotConfig) DiscordBotEnvironment.getBot().getConfig();
    }

    @Override
    protected @NotNull Database createDatabase() {
        String mongoUri = System.getProperty("db.mongodb.uri", "mongodb://localhost:27017/");
        String databaseName = "skyblock_nerds";
        Database database = new Database(mongoUri, databaseName);

        if (database.isConnected()) {
            try {
                database.getRepositoryManager().registerRepositoriesFromPackage(
                    "net.hypixel.nerdbot.tickets.repository", database.getMongoClient(), databaseName);
            } catch (RepositoryException e) {
                TICKET_LOG.error("Failed to register ticket repositories!", e);
            }
        }

        return database;
    }

    @Override
    protected @NotNull Collection<Object> getEventListeners() {
        List<Object> listeners = new ArrayList<>();

        // Always register the ticket listener - this bot exists solely for tickets
        listeners.add(new TicketListener());

        return listeners;
    }

    @Override
    protected @NotNull Collection<? extends BotFeature> createFeatures() {
        TicketBotConfig config = getConfig();
        List<BotFeature> features = new ArrayList<>();

        if (config.getFeatures() != null) {
            log.info("Loading features from config ({} entries)", config.getFeatures().size());

            config.getFeatures().stream()
                .filter(FeatureConfig::isEnabled)
                .forEach(featureConfig -> {
                    try {
                        if (!isAllowed(featureConfig.getClassName())) {
                            log.warn("Feature class {} not permitted by class allowlist", featureConfig.getClassName());
                            return;
                        }

                        Class<?> clazz = Class.forName(featureConfig.getClassName());
                        if (!BotFeature.class.isAssignableFrom(clazz)) {
                            log.warn("Feature class {} does not implement BotFeature", featureConfig.getClassName());
                            return;
                        }

                        BotFeature feature = (BotFeature) clazz.getDeclaredConstructor().newInstance();
                        feature.setScheduleOverrides(featureConfig.getInitialDelayMs(), featureConfig.getPeriodMs());
                        features.add(feature);
                        log.info("Added feature from config: {}", featureConfig.getClassName());

                        if (feature instanceof SchedulableFeature schedulable) {
                            // SchedulableFeature expects NerdBotConfig, so we pass null-safe defaults
                            long defaultInitial = 60_000; // 1 minute default
                            long defaultPeriod = 300_000; // 5 minutes default

                            try {
                                // Try to use the interface methods with a NerdBotConfig-compatible config
                                defaultInitial = schedulable.defaultInitialDelayMs(null);
                                defaultPeriod = schedulable.defaultPeriodMs(null);
                            } catch (Exception e) {
                                log.debug("Using default scheduling for {}", featureConfig.getClassName());
                            }

                            feature.scheduleAtFixedRate(schedulable.buildTask(), defaultInitial, defaultPeriod);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to instantiate feature {}", featureConfig.getClassName(), e);
                    }
                });
        } else {
            log.info("No feature config present");
        }

        return features;
    }

    @Override
    protected String getSlashCommandBasePackage() {
        return "net.hypixel.nerdbot.tickets.command";
    }

    @Override
    protected @Nullable Activity buildActivity(@NotNull DiscordBotConfig config) {
        return Activity.of(
            Activity.ActivityType.valueOf(config.getActivityType().name()),
            config.getActivity()
        );
    }

    @Override
    protected void onReady(@NotNull JDA jda) {
        super.onReady(jda);

        TicketBotConfig config = getConfig();

        // Start metrics server
        TicketMetrics.startMetricsServer();

        // Initialize ticket system
        if (config.getTicketConfig() != null && !config.getTicketConfig().getTicketCategoryId().isEmpty()) {
            TicketService.getInstance();
            log.info("Ticket system initialized");
        } else {
            log.warn("Ticket config not found or ticketCategoryId is empty!");
        }
    }

    @Override
    protected void onShutdown() {
        log.info("Ticket bot shutting down...");
        TicketMetrics.stopMetricsServer();
    }

    private static boolean isAllowed(String className) {
        return className != null && className.startsWith("net.hypixel.nerdbot.");
    }
}
