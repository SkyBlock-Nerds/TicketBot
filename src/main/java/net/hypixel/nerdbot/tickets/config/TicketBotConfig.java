package net.hypixel.nerdbot.tickets.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.hypixel.nerdbot.discord.config.DiscordBotConfig;
import net.hypixel.nerdbot.discord.config.FeatureConfig;
import net.hypixel.nerdbot.tickets.config.TicketConfig;

import java.util.List;

/**
 * Configuration specific to the standalone Ticket Bot.
 * Extends the base Discord bot config and includes ticket-specific settings.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class TicketBotConfig extends DiscordBotConfig {

    /**
     * Configuration for the ticket system
     */
    private TicketConfig ticketConfig = new TicketConfig();

    /**
     * Feature configuration. If present and not empty,
     * the bot will instantiate and start the listed features in order.
     */
    private List<FeatureConfig> features;
}
