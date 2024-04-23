package com.redot.neverdox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class MSGManager {

    public static void sendCheckupMessage(String message) {
        MutableText bracketL = Text.literal("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x333333)));
        MutableText bracketR = Text.literal("]").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x333333)));
        MutableText neverDox = Text.literal("NeverDox").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xD3B53D)).withItalic(true));
        MutableText messageComponent = Text.literal(" " + message).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAAAA)));

        MutableText messageText = bracketL.append(neverDox).append(bracketR).append(messageComponent);
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(messageText);
    }

    public static void sendPlayerMessage(String message) {
        MutableText msg = Text.literal(message)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAAAA)));
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(msg);
    }

    public static void sendStatusMessage(String statusID, MutableText statusActual) {
        MutableText bracketL = Text.literal("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x333333)));
        MutableText bracketR = Text.literal("]").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x333333)));
        MutableText neverDox = Text.literal("ND").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xD3B53D)).withItalic(true));
        MutableText statusIdentifier = Text.literal(statusID).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAAAA)));

        MutableText messageComponent = Text.literal(" ").append(statusIdentifier).append(statusActual);
        MutableText messageText = bracketL.append(neverDox).append(bracketR).append(messageComponent);
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(messageText);
    }

    public static void sendStatusUpdate() {
        MutableText enabled = Text.literal("Enabled").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x2EFF2E)));
        MutableText disabled = Text.literal("Disabled").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xC80814)));

        sendStatusMessage("Dispatch: ", NeverDox.enabled ? enabled : disabled);
        sendStatusMessage("Webhook Config: ", NeverDox.webhookCheck() ? enabled : disabled);
    }

    public static void sendPopupText(String text) {
        MutableText neverDox = Text.literal("NeverDox").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xD3B53D)).withItalic(true));
        SystemToast popup = SystemToast.create(MinecraftClient.getInstance(), SystemToast.Type.PERIODIC_NOTIFICATION, neverDox, Text.literal(text));
        MinecraftClient.getInstance().getToastManager().add(popup);
    }

    public static void setupText() {
        if (!NeverDox.webhookCheck()) {
            sendPlayerMessage("Hey! I'm NeverDox, a mod to keep Minecraft players safe.");
            sendPlayerMessage("I'll log your filtered messages & send them directly to your Discord webhook!");
            sendPlayerMessage("To get started, type '?nd open phrases' and paste your webhook in!");
            sendPlayerMessage("Other than that, use '?nd help' for a list of my commands!");
        }
    }

    public static void helpText() {
        sendCheckupMessage("My commands include:");
        sendPlayerMessage("?nd toggle <- Toggles logging on/off");
        sendPlayerMessage("?nd add (phrase) <- Adds a phrase to the logger");
        sendPlayerMessage("?nd addping (phrase) <- Adds a pinged phrase to the logger");
        sendPlayerMessage("?nd addexempt (phrase) <- Adds an exempt phrase to the logger");
        sendPlayerMessage("?nd remove (phrase) <- Removes a phrase from the logger");
        sendPlayerMessage("?nd open <- Opens the NeverDox config file");
        sendPlayerMessage("?nd sendwebhook <- Sends a webhook message to your link");
        sendPlayerMessage("?nd status <- Reports the current status to you");
        sendPlayerMessage("?nd help <- See this message again");
    }

}