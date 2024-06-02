NeverDox 1.0.5
-

NeverDox is a client-side Minecraft mod to keep players safe on public servers.
It's intended to be a moderation tool which reports in-game chat messages to discord webhooks based upon the filters & exemptions that its user provides.


To use _all_ of NeverDox's features, you'll need:
- Fabric 1.20.4
- Windows (to open & write files)
- A discord webhook (https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks)


Commands
-
- /nd toggle <- Toggles webhook logs for all messages (on by default)
- /nd open <- Opens the NeverDox directory, which holds the webhook along with all phrases
- /nd help <- Help message
- /nd add (phrase) <- Adds a phrase to the logger
- /nd addping (phrase) <- Adds a phrase (which pings @everyone through the webhook) to the logger
- /nd addexempt (phrase) <- Adds an exempt phrase to the logger
- /nd remove (phrase) <- Removes a phrase of any kind from the logger
- Reporting its current status ('/nd status')
- Sending a test webhook message ('/nd sendwebhook')


Getting Started
-
- Download the mod (https://github.com/Rehdot/NeverDox/releases/)
- Put the mod into your mods folder
- Launch Minecraft
- Log into any world and type '/nd open' in chat
- Paste a discord webhook link into the file, and save it
- Test the webhook with '/nd sendwebhook'
- Add a filtered phrase using '/nd add (phrase)'
- Log messages happily ever after


Exemptions
-

In filtering, we exempt words to make sure that some filtered phrases aren't confused with whitelisted words.
As an example of this, if we wanted to log for the word "house" but not "doghouse", we would add "doghouse" as exempt, and NeverDox would not log instances of "doghouse".
