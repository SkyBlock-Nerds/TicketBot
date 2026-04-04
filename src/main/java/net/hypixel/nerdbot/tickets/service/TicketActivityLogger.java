package net.hypixel.nerdbot.tickets.service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.hypixel.nerdbot.marmalade.discord.EmbedFactory;
import net.hypixel.nerdbot.tickets.config.TicketConfig;
import net.hypixel.nerdbot.tickets.model.Ticket;
import net.hypixel.nerdbot.tickets.model.TicketStatus;
import net.hypixel.nerdbot.discord.util.DiscordBotEnvironment;
import net.hypixel.nerdbot.discord.util.StringUtils;

import java.awt.Color;

/**
 * Logs ticket activity events to a configured Discord channel using embeds.
 */
@Slf4j
public class TicketActivityLogger {

    private static final Color COLOR_DELETED = new Color(0x99, 0xAA, 0xB5); // Gray - no preset match

    private final TicketConfig config;

    public TicketActivityLogger(TicketConfig config) {
        this.config = config;
    }

    public void logCreated(Ticket ticket, User creator) {
        String categoryName = config.getCategoryDisplayName(ticket.getTicketCategoryId());
        MessageEmbed embed = EmbedFactory.success("Ticket Created", ticket.getFormattedTicketId())
            .addField("Created By", creator.getAsMention(), true)
            .addField("Category", categoryName, true)
            .addField("Channel", "<#" + ticket.getChannelId() + ">", true)
            .build();
        sendEmbed(embed);
    }

    public void logClaimed(Ticket ticket, User staff) {
        MessageEmbed embed = EmbedFactory.info("Ticket Claimed", ticket.getFormattedTicketId())
            .addField("Claimed By", staff.getAsMention(), true)
            .addField("Channel", "<#" + ticket.getChannelId() + ">", true)
            .build();
        sendEmbed(embed);
    }

    public void logStatusChange(Ticket ticket, TicketStatus oldStatus, TicketStatus newStatus, User actor) {
        String oldName = config.getStatusDisplayName(oldStatus);
        String newName = config.getStatusDisplayName(newStatus);
        MessageEmbed embed = EmbedFactory.warning("Status Changed", ticket.getFormattedTicketId())
            .addField("Status", oldName + " -> " + newName, true)
            .addField("Changed By", actor.getAsMention(), true)
            .build();
        sendEmbed(embed);
    }

    public void logClosed(Ticket ticket, User staff, String reason) {
        String closedBy = staff != null ? staff.getAsMention() : "System (Auto-close)";
        String reasonText = reason != null && !reason.isBlank() ? StringUtils.truncate(reason, 200) : "No reason provided";
        MessageEmbed embed = EmbedFactory.error("Ticket Closed", ticket.getFormattedTicketId())
            .addField("Closed By", closedBy, true)
            .addField("Reason", reasonText, false)
            .build();
        sendEmbed(embed);
    }

    public void logReopened(Ticket ticket, User staff) {
        MessageEmbed embed = EmbedFactory.success("Ticket Reopened", ticket.getFormattedTicketId())
            .addField("Reopened By", staff.getAsMention(), true)
            .addField("Channel", "<#" + ticket.getChannelId() + ">", true)
            .build();
        sendEmbed(embed);
    }

    public void logTransferred(Ticket ticket, User from, User to, User actor) {
        String fromName = from != null ? from.getAsMention() : "Unclaimed";
        MessageEmbed embed = EmbedFactory.info("Ticket Transferred", ticket.getFormattedTicketId())
            .addField("From", fromName, true)
            .addField("To", to.getAsMention(), true)
            .addField("By", actor.getAsMention(), true)
            .build();
        sendEmbed(embed);
    }

    public void logAutoDeleted(Ticket ticket) {
        MessageEmbed embed = EmbedFactory.create("Ticket Deleted", ticket.getFormattedTicketId(), COLOR_DELETED)
            .addField("Reason", "Retention period expired", false)
            .build();
        sendEmbed(embed);
    }

    private void sendEmbed(MessageEmbed embed) {
        if (!config.isActivityLogEnabled()) {
            return;
        }

        TextChannel channel = getLogChannel();
        if (channel == null) {
            return;
        }

        channel.sendMessageEmbeds(embed).queue(
            success -> {},
            error -> log.warn("Failed to send activity log: {}", error.getMessage())
        );
    }

    /**
     * Get the configured activity log channel.
     *
     * @return the text channel, or null if not configured or not found
     */
    private TextChannel getLogChannel() {
        String channelId = config.getActivityLogChannelId();
        if (channelId == null || channelId.isEmpty()) {
            return null;
        }

        TextChannel channel = DiscordBotEnvironment.getBot().getJDA().getTextChannelById(channelId);
        if (channel == null) {
            log.debug("Activity log channel {} not found", channelId);
        }

        return channel;
    }
}
