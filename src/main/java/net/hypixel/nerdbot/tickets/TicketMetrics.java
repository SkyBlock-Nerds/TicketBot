package net.hypixel.nerdbot.tickets;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TicketMetrics {

    private static final int DEFAULT_PORT = 9191;

    public static final Counter TICKETS_CREATED = Counter.build()
        .name("tickets_created_total")
        .help("Total tickets created")
        .labelNames("category")
        .register();

    public static final Counter TICKETS_CLOSED = Counter.build()
        .name("tickets_closed_total")
        .help("Total tickets closed")
        .labelNames("close_type")
        .register();

    public static final Counter TICKETS_REOPENED = Counter.build()
        .name("tickets_reopened_total")
        .help("Total tickets reopened")
        .register();

    public static final Counter TICKET_MESSAGES = Counter.build()
        .name("ticket_messages_total")
        .help("Messages in tickets")
        .labelNames("author_type")
        .register();

    public static final Counter TICKET_STAFF_ACTIONS = Counter.build()
        .name("ticket_staff_actions_total")
        .help("Staff actions on tickets")
        .labelNames("action", "staff_id")
        .register();

    public static final Counter TICKET_REMINDERS_SENT = Counter.build()
        .name("ticket_reminders_sent_total")
        .help("Reminders sent for stale tickets")
        .register();

    public static final Gauge TICKETS_OPEN = Gauge.build()
        .name("tickets_open_current")
        .help("Currently open tickets")
        .labelNames("status")
        .register();

    public static final Gauge TICKETS_CLAIMED = Gauge.build()
        .name("tickets_claimed_current")
        .help("Currently claimed tickets per staff")
        .labelNames("staff_id")
        .register();

    public static final Histogram TICKET_FIRST_RESPONSE_TIME = Histogram.build()
        .name("ticket_first_response_seconds")
        .help("Time to first staff response")
        .buckets(300, 900, 1_800, 3_600, 7_200, 14_400, 28_800, 86_400)
        .register();

    public static final Histogram TICKET_RESOLUTION_TIME = Histogram.build()
        .name("ticket_resolution_seconds")
        .help("Time from creation to closure")
        .buckets(1_800, 3_600, 14_400, 86_400, 259_200, 604_800)
        .register();

    public static final Histogram TICKET_MESSAGE_COUNT = Histogram.build()
        .name("ticket_message_count")
        .help("Messages per ticket at closure")
        .buckets(1, 3, 5, 10, 20, 50, 100)
        .register();

    private static HTTPServer server;

    private TicketMetrics() {
    }

    public static void startMetricsServer() {
        int port = Integer.getInteger("metrics.port", DEFAULT_PORT);

        try {
            if (server != null) {
                server.close();
            }

            server = new HTTPServer.Builder()
                .withPort(port)
                .build();

            DefaultExports.initialize();
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
