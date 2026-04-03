<div align="center">
    <h1>Ticket Bot</h1>
    <p><i>Discord ticket/support system for the SkyBlock Nerds server</i></p>
    <br>
    <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21"/>
<img src="https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker" alt="Docker"/>
    <img src="https://img.shields.io/badge/MongoDB-database-47A248?style=for-the-badge&logo=mongodb" alt="MongoDB"/>
    <br><br>
    <img src="https://img.shields.io/github/issues/SkyBlock-Nerds/TicketBot?style=for-the-badge" alt="Issues"/>
    <img src="https://img.shields.io/github/issues-pr/SkyBlock-Nerds/TicketBot?style=for-the-badge" alt="Pull Requests"/>
    <img src="https://img.shields.io/github/last-commit/SkyBlock-Nerds/TicketBot?style=for-the-badge" alt="Last Commit"/>
    <img src="https://img.shields.io/github/contributors/SkyBlock-Nerds/TicketBot?style=for-the-badge" alt="Contributors"/>
</div>

---

A standalone Discord bot for managing a support ticket system. Users create tickets via modal forms, and staff manage
them through slash commands and button-based controls across configurable lifecycle statuses.

# Features

## Ticket Lifecycle

Tickets progress through configurable statuses (Open, In Progress, Awaiting Response, Closed) with button-based
controls. Staff can claim, transfer, close, reopen, and change the status of tickets through an interactive control
panel.

## Categories & Templates

Multiple ticket categories (General, Bug Report, Appeal, etc.) with customizable modal form templates and fields per
category.

## Commands

| Command                             | Description                         |
|-------------------------------------|-------------------------------------|
| `/ticket setup`                     | Post the ticket creation panel      |
| `/ticket close`                     | Close a ticket                      |
| `/ticket reopen`                    | Reopen a closed ticket              |
| `/ticket claim`                     | Claim a ticket                      |
| `/ticket transfer`                  | Transfer to another staff member    |
| `/ticket status`                    | Change ticket status                |
| `/ticket find`                      | Find tickets for a specific user    |
| `/ticket new`                       | Create a ticket on behalf of a user |
| `/ticket info`                      | View ticket information             |
| `/ticket export`                    | Export tickets to CSV               |
| `/ticket search`                    | Search tickets with filters         |
| `/ticket stats`                     | View ticket statistics              |
| `/ticket blacklist-add/remove/list` | Manage user blacklist               |

## Automation

- Auto-close tickets after a configurable inactivity period
- Automatic status transitions based on who replies (user reply -> Open, staff reply -> Awaiting Response)
- Configurable reminders for inactive tickets
- Automatic cleanup of old closed tickets

## Transcripts

Message history is archived (up to 500 messages with overflow to MongoDB) and transcripts can be generated and uploaded
when tickets are closed.

## Metrics

Prometheus metrics on a configurable port (default `9191`) including created/closed ticket counts, response times,
resolution times, staff actions, and active ticket gauges.

# Running

**Prerequisites:** Java 21+, MongoDB, a Discord bot token

```bash
mvn clean package
java -Dbot.token=YOUR_TOKEN \
     -Ddb.mongodb.uri=mongodb://localhost:27017/ \
     -Dbot.environment=DEVELOPMENT \
     -Dbot.config=config.json \
     -jar target/TicketBot.jar
```

## Docker

```bash
docker build --build-arg BRANCH_NAME=main -t ticket-bot:latest .
docker run -d \
  -p 9191:9191 \
  -v /path/to/config.json:/app/config.json \
  -e "JAVA_OPTS=-Dbot.token=YOUR_TOKEN -Ddb.mongodb.uri=mongodb://mongo:27017/" \
  ticket-bot:latest
```

# Configuration

Configured via a JSON file with ticket categories, status definitions and allowed transitions, reminder thresholds,
and auto-close settings. See [`config.example.json`](config.example.json) for all available options.
---

<div align="center">
    <b>Supporting the Project</b>
    <br><br>
    <a href="https://github.com/sponsors/Aerhhh"><img src="https://img.shields.io/static/v1?label=Sponsor&message=%E2%9D%A4&logo=GitHub&color=%23fe8e86" height="20px" alt="Aerh's GitHub Sponsor Profile"></a>
    <a href="https://www.buymeacoffee.com/aaerh"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" height="20px" alt="Aerh's Buy Me A Coffee Profile"></a>
    <a href="https://ko-fi.com/A0A81MQI3"><img src="https://ko-fi.com/img/githubbutton_sm.svg" height="20px" alt="Aerh's Ko-Fi Profile"></a>
</div>
