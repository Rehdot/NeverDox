package com.redot.neverdox;

import java.util.List;

public class NDCommand {

    public static void processCommand(String subCommand, String phrase) {
        switch (subCommand.toLowerCase()) {
            case "toggle":
                NeverDox.enabled = !NeverDox.enabled;
                String msg = NeverDox.enabled ? "Enabled NeverDox Dispatch." : "Disabled NeverDox Dispatch.";
                MSGManager.sendCheckupMessage(msg);
                break;
            case "add":
                JSONManager.addPhraseToJSON(phrase, false, false);
                break;
            case "addping":
                JSONManager.addPhraseToJSON(phrase, false, true);
                break;
            case "addexempt":
                JSONManager.addPhraseToJSON(phrase, true, false);
                break;
            case "remove":
                if (phrase != null) {JSONManager.removePhrase(phrase.toLowerCase());}
                else {MSGManager.sendCheckupMessage("Specify a phrase to remove!");}
                break;
            case "open":
                MSGManager.sendCheckupMessage("Attempting to open NeverDox directory...");
                NeverDox.openFile(JSONManager.neverDoxConfig);
                break;
            case "echo":
                if (phrase != null) {MSGManager.sendCheckupMessage(phrase);}
                break;
            case "sendwebhook":
                MSGManager.sendCheckupMessage("Attempting to send test webhook...");
                List<String> testItems = List.of("This is a test message from NeverDox.");
                NeverDox.sendDiscordWebhook(testItems.get(0), testItems, false);
                break;
            case "help":
                try {MSGManager.helpText();}
                catch (InterruptedException ignored) {}
                break;
            default:
                MSGManager.sendCheckupMessage("Command not recognized! Try '?nd help'.");
                break;
        }
    }
}