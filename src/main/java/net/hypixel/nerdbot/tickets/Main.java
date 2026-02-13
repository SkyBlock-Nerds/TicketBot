package net.hypixel.nerdbot.tickets;

import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import net.hypixel.nerdbot.discord.BotEnvironment;
import net.hypixel.nerdbot.discord.api.bot.Bot;
import sun.misc.Signal;

import javax.security.auth.login.LoginException;

/**
 * Main entrypoint for the standalone Ticket Bot application.
 * Uses the same bot token as the main NerdBot and connects to the same MongoDB.
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Initializing NerdBot Ticket System...");

        TicketBot bot = new TicketBot();
        BotEnvironment.setBot(bot);

        registerShutdownHooks(bot);

        log.info("Starting ticket bot...");

        try {
            bot.create(args);
            log.info("Ticket bot successfully started!");
        } catch (LoginException exception) {
            log.error("Failed to authenticate with Discord! Check your bot token.", exception);
        } catch (MongoException exception) {
            log.error("Failed to connect to MongoDB! Continuing without database connectivity.", exception);
        } catch (RuntimeException exception) {
            log.error("Unexpected runtime error during bot startup!", exception);
            System.exit(1);
        }
    }

    private static void registerShutdownHooks(Bot bot) {
        for (String signal : new String[]{"INT", "TERM"}) {
            Signal.handle(new Signal(signal), sig -> {
                log.info("Received shutdown signal: {}", signal);
                bot.onEnd();
                System.exit(0);
            });
        }
    }
}
