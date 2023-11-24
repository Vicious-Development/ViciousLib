# ViciousLib
====================

# Information
Provides:
command - Provides a system similar to SpongeForge's command system but for more generic purposes. This system assumes there is some sort of User and Channel types in your project, however what is a user and and channel is up to you.
How to implement:
- Create a CommandHandler implementation
- Register commands by using handler#register. Use Command.CommandBuilder to create commands.
- Provide that command handler user input by calling handler#processCommand

In a discord implementation, the USERTYPE is User and CHANNELTYPE is MessageChannel. Generally you'd want to listen for user messaging events and pass through the raw text data.

# Data Serialization
ViciousLib provides a highly versatile annotation driven data serialization system.

