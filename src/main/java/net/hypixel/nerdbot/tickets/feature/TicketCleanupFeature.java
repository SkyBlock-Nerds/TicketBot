package net.hypixel.nerdbot.tickets.feature;

import lombok.extern.slf4j.Slf4j;
import net.hypixel.nerdbot.tickets.service.TicketService;
import net.hypixel.nerdbot.discord.api.feature.BotFeature;
import net.hypixel.nerdbot.discord.api.feature.SchedulableFeature;
import net.hypixel.nerdbot.discord.config.NerdBotConfig;

import java.util.concurrent.TimeUnit;

/**
 * Periodic feature that deletes old closed tickets that have exceeded
 * the configured retention period. Both the Discord thread and MongoDB
 * record are removed.
 */
@Slf4j
public class TicketCleanupFeature extends BotFeature implements SchedulableFeature {

    @Override
    public long defaultInitialDelayMs(NerdBotConfig config) {
        return TimeUnit.MINUTES.toMillis(5);
    }

    @Override
    public long defaultPeriodMs(NerdBotConfig config) {
        return TimeUnit.DAYS.toMillis(1);
    }

    @Override
    public void executeTask() throws Exception {
        TicketService.getInstance().deleteOldClosedTickets();
    }

    @Override
    public void onFeatureStart() {
        log.info("Ticket cleanup feature started");
    }

    @Override
    public void onFeatureEnd() {
        log.info("Ticket cleanup feature stopped");
    }
}
