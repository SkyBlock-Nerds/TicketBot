package net.hypixel.nerdbot.tickets;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import lombok.extern.slf4j.Slf4j;
import net.hypixel.nerdbot.marmalade.metrics.MetricsRegistry;

@Slf4j
public final class TicketMetrics {

    private static final int DEFAULT_PORT = 9191;

    public static final Counter TICKETS_CREATED = MetricsRegistry.counter(
        "tickets_created_total", "Total tickets created", "category");

    public static final Counter TICKETS_CLOSED = MetricsRegistry.counter(
        "tickets_closed_total", "Total tickets closed", "close_type");

    public static final Counter TICKETS_REOPENED = MetricsRegistry.counter(
        "tickets_reopened_total", "Total tickets reopened");

    public static final Counter TICKET_MESSAGES = MetricsRegistry.counter(
        "ticket_messages_total", "Messages in tickets", "author_type");

    public static final Counter TICKET_STAFF_ACTIONS = MetricsRegistry.counter(
        "ticket_staff_actions_total", "Staff actions on tickets", "action", "staff_id");

    public static final Counter TICKET_REMINDERS_SENT = MetricsRegistry.counter(
        "ticket_reminders_sent_total", "Reminders sent for stale tickets");

    public static final Gauge TICKETS_OPEN = MetricsRegistry.gauge(
        "tickets_open_current", "Currently open tickets", "status");

    public static final Gauge TICKETS_CLAIMED = MetricsRegistry.gauge(
        "tickets_claimed_current", "Currently claimed tickets per staff", "staff_id");

    public static final Histogram TICKET_FIRST_RESPONSE_TIME = MetricsRegistry.histogram(
        "ticket_first_response_seconds", "Time to first staff response",
        new double[]{300, 900, 1_800, 3_600, 7_200, 14_400, 28_800, 86_400});

    public static final Histogram TICKET_RESOLUTION_TIME = MetricsRegistry.histogram(
        "ticket_resolution_seconds", "Time from creation to closure",
        new double[]{1_800, 3_600, 14_400, 86_400, 259_200, 604_800});

    public static final Histogram TICKET_MESSAGE_COUNT = MetricsRegistry.histogram(
        "ticket_message_count", "Messages per ticket at closure",
        new double[]{1, 3, 5, 10, 20, 50, 100});

    private static HTTPServer server;

    private TicketMetrics() {
    }

    public static void startMetricsServer() {
        int port = Integer.getInteger("metrics.port", DEFAULT_PORT);

        try {
            if (server != null) {
                server.close();
            }

            server = MetricsRegistry.startServer(port);
            log.info("Prometheus metrics server started on port {}", port);
        } catch (Exception e) {
            log.error("Failed to start Prometheus metrics server on port {}", port, e);
        }
    }

    public static void stopMetricsServer() {
        if (server != null) {
            server.close();
            server = null;
            log.info("Prometheus metrics server stopped");
        }
    }
}
