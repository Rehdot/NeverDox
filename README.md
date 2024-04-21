**NeverDox 1.0.3**

NeverDox is a client-side Minecraft mod to keep players safe on public servers.
It's intended to be a moderation tool which reports in-game chat messages to discord webhooks based upon the filters & exemptions that its user provides.


To use all of NeverDox's features, you'll need:
- Fabric 1.20.4
- Windows (to open & write files)
- A discord webhook (https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks)


NeverDox has an in-game command override which uses the prefix '**?nd**'.
It has command support for:
- Toggling webhook logs for all messages (on by default, use '?nd toggle')
- Adding a phrase to the logger ('?nd add (phrase)')
- Adding a phrase which pings @everyone through the webhook to the logger ('?nd addping (phrase)')
- Adding an exempt phrase to the logger ('?nd addexempt (phrase)')
- Removing a phrase of any kind from the logger ('?nd remove (phrase)')
- Opening the NeverDox directory which holds the webhook along with all phrases ('?nd open')
- Repeating what you say for testing ('?nd echo (message)')
- Reporting its current status ('?nd status')
- Sending a test webhook message ('?nd sendwebhook')
- Help ('?nd help')


**To get started,**
- Download the mod (https://github.com/Rehdot/NeverDox/releases/tag/release)
- Put the mod into your mods folder (also supports LabyMod)
- Launch Minecraft
- Log onto a **server** and type '?nd open' in chat
- Paste a discord webhook link into the file, and save it
- Test the webhook with '?nd sendwebhook'
- Add a filtered phrase using '?nd add (phrase)'
- Freely log messages happily ever after


**Exemptions**

In filtering, we exempt words to make sure that some filtered phrases aren't confused with whitelisted words.
As an example of this, if we wanted to log for the word "house" but not "doghouse", we would add "doghouse" as exempt, and NeverDox would not log instances of "doghouse".


Reach out to me on discord (@redots) if you have questions, feedback, or suggestions. Happy logging!
