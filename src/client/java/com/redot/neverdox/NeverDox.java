package com.redot.neverdox;

import net.fabricmc.api.ClientModInitializer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class NeverDox implements ClientModInitializer {

	public static final String appDataDir = System.getenv("APPDATA"), minecraftDir = appDataDir + "/.minecraft";
	public static boolean sentSetup = false, sentPopup = false, enabled = true;

	@Override
	public void onInitializeClient() {
		System.out.println("[NeverDox] Initialized.");
	}

	public static void handleChatMessage(String message) throws IOException {

		// to fix backslashes not sending in discord webhooks
		message = message.replace("\\", "\\\\");
		String lowerCaseMsg = message.toLowerCase();
		boolean ping = false;

		MSGManager.setupText();
		java.util.List<String> matches = new ArrayList<>();
		for (JSONManager.PhraseDetails phraseDetail : Objects.requireNonNull(JSONManager.findMatchingPhrases(lowerCaseMsg))) {
			if (phraseDetail.getPings()) ping = true;
			if (phraseDetail.isExempt()) {
				if (processExemption(lowerCaseMsg, phraseDetail)) return;
			} else {
				matches.add(phraseDetail.getText());
			}
		}

		if (!matches.isEmpty()) sendDiscordWebhook(message, matches, ping);
	}

	public static boolean processExemption(String msg, JSONManager.PhraseDetails matchedItem) {
		String txt = matchedItem.getText();
		String newMsg = msg.replace(txt, "");

		// remove all exempt phrases from the string
		for (JSONManager.PhraseDetails phrase : JSONManager.getAllFullPhrases()) {
			if (phrase.isExempt() && newMsg.contains(phrase.getText())) {
				newMsg = newMsg.replace(phrase.getText(), "");
			}
		}

		// find all filtered phrases in the string if there are any left, and return false to send the webhook.
		for (JSONManager.PhraseDetails phrase : JSONManager.getAllFullPhrases()) {
			if (!phrase.isExempt() && newMsg.contains(phrase.getText())) {
				return false;
			}
		}

		// by this point, if we can return true, then the string contains nothing harmful.
		return true;
	}

	public static void sendDiscordWebhook(String message, java.util.List<String> matches, boolean ping) {

		// to fix quotation marks not sending in discord webhooks
		message = message.replace("\"", "\\\"");
		String matchesString = String.join(", ", matches);

		try {
			URL url = new URL(Objects.requireNonNull(JSONManager.getWebhook()));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("User-Agent", "Java HTTP Request");
			connection.setDoOutput(true);

			String pingString = ping ? "@everyone\\n" : "";
			String payload = "{\"content\":\"" + pingString + "**NeverDox Dispatch #" + JSONManager.incrementDispatchNumber() + "**\\nFiltered terms: *"
					+ matchesString + "*\\n```" + message + "```\", \"username\":\"NeverDox\"}";

			OutputStream outputStream = null;
			try {
				outputStream = connection.getOutputStream();
				byte[] input = payload.getBytes(StandardCharsets.UTF_8);
				outputStream.write(input, 0, input.length);

			} catch (IOException ignored) {
				MSGManager.sendCheckupMessage("Webhook failure [1]");
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException ignored) {
						MSGManager.sendCheckupMessage("Webhook failure [2]");
					}
				}
			}

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {System.out.println("[NeverDox] Webhook sent successfully.");}
			else {System.out.println("[NeverDox] Webhook response code: " + responseCode);}

			connection.disconnect();
		} catch (IOException ignored) {
			MSGManager.sendCheckupMessage("Webhook failure [3]");
		}
	}

	public static void formatTextAndRunCommand(String msg) throws IOException {
		if (!msg.toLowerCase().contains("?nd")) return;

		int startIndex = msg.toLowerCase().indexOf("?nd");
		if (startIndex == -1) {
			MSGManager.sendCheckupMessage("How did we get here?");
			return;
		}

		startIndex += 4;
		while (startIndex < msg.length() && Character.isWhitespace(msg.charAt(startIndex))) {
			startIndex++;
		}

		String subCommand = "";
		String phrase = "";

		int endIndex = msg.indexOf(' ', startIndex);
		if (endIndex == -1) {
			endIndex = msg.length();
		}

		subCommand = msg.substring(startIndex, endIndex).toLowerCase();
		if (endIndex < msg.length()) {
			phrase = msg.substring(endIndex + 1).trim();
		}

		NDCommand.processCommand(subCommand, phrase);
	}

	// there's probably a better solution to this
	public static boolean webhookCheck() {
		if (sentSetup) return true;
		sentSetup = true;
		return Objects.requireNonNull(JSONManager.getWebhook()).toLowerCase().contains("discord");
	}

	public static void openFile(File file) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("cmd", "/c", "start", file.getAbsolutePath());
			pb.start();
		} catch (IOException ignored) {}
	}
}