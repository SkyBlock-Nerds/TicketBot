package net.hypixel.nerdbot.tickets.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.hypixel.nerdbot.discord.config.DiscordBotConfig;
import net.hypixel.nerdbot.tickets.config.TicketConfig;


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

}
