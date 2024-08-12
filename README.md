NeverDox 1.0.6
-
NeverDox is a client-side Minecraft mod to keep players safe on public servers.
It's intended to be a moderation tool which reports in-game chat messages to discord webhooks based upon the filters & exemptions that its user provides.


To use NeverDox's features, you'll need:
- Fabric 1.20.4
- A discord webhook (https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks)

Commands
-
- /nd toggle <- Toggles logs for all webhooks (on by default)
- /nd open <- Opens the NeverDox directory, which holds all webhooks and phrases

Getting Started
-
- Download the mod (https://github.com/Rehdot/NeverDox/releases/)
- Put the mod into your mods folder
- Launch Minecraft
- Click Options -> NeverDox Settings -> Add Webhook
- Paste a discord webhook link into the text bar
- Click settings, and assign custom phrases to the webhook
- Log messages happily ever after

Exemptions
-
If I wanted to log for the word "house" but not "doghouse", I'd add "house" as a filtered phrase, and "doghouse" as an exempt phrase. NeverDox would not log instances of "doghouse".

Doubles
-
If I wanted to log messages containing **both** "baby" and "blue", I'd make a phrase containing "baby", click "Add Text", and add "blue".