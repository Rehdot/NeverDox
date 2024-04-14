package com.redot.neverdox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class MSGManager {

    public static void sendCheckupMessage(String message) {
        MutableText bracketL = Text.literal("[")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x333333)));

        MutableText bracketR = Text.literal("]")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x333333)));

        MutableText playerNameComponent = Text.literal("NeverDox")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x8B0000)).withBold(true));

        MutableText messageComponent = Text.literal(" " + message)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAAAA)));

        MutableText messageText = bracketL.append(playerNameComponent).append(bracketR).append(messageComponent);
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(messageText);
    }

    public static void sendPlayerMessage(String message) {
        MutableText msg = Text.literal(message)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAAAA)));
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(msg);
    }

    public static void setupText() {
        if (!NeverDox.webhookCheck()) {
            sendPlayerMessage("Hey! I'm NeverDox, a mod to keep Minecraft players safe.");
            sendPlayerMessage("I'll log your filtered messages & send them directly to your Discord webhook!");
            sendPlayerMessage("To get started, type '?nd open phrases' and paste your webhook in!");
            sendPlayerMessage("Other than that, use '?nd help' for a list of my commands!");
        }
    }

    public static void helpText() throws InterruptedException {
        sendCheckupMessage("My commands include:");
        sendPlayerMessage("?nd toggle <- Toggles logging on/off");
        sendPlayerMessage("?nd add (phrase) <- Adds a phrase to the logger");
        sendPlayerMessage("?nd addping (phrase) <- Adds a pinged phrase to the logger");
        sendPlayerMessage("?nd addexempt (phrase) <- Adds an exempt phrase to the logger");
        sendPlayerMessage("?nd remove (phrase) <- Removes a phrase from the logger");
        sendPlayerMessage("?nd open <- Opens the NeverDox config file");
        sendPlayerMessage("?nd sendwebhook <- Sends a webhook message to your link");
        sendPlayerMessage("?nd help <- See this message again");
    }

}
