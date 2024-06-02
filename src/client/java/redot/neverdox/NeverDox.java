package redot.neverdox;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import redot.neverdox.interfaces.PhraseExecutor;
import redot.neverdox.managers.JSONManager;
import redot.neverdox.managers.MSGManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class NeverDox implements ClientModInitializer {

	public static final String appDataDir = System.getenv("APPDATA"), minecraftDir = appDataDir + "/.minecraft";
	public static boolean sentSetup = false, sentPopup = false, enabled = true;

	@Override
	public void onInitializeClient() {
		System.out.println("NeverDox initialized.");
		ClientCommandRegistrationCallback.EVENT.register(this::registerNeverDox);
	}

	private void registerNeverDox(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		final LiteralCommandNode<FabricClientCommandSource> neverDoxCommand = dispatcher.register(literal("nd")
				.then(registerExecutor("help", () -> {
					MSGManager.helpText();
				}))
				.then(registerExecutor("toggle", () -> {
					enabled = !enabled;
					MSGManager.sendPopupText((NeverDox.enabled ? "En" : "Dis") + "abled Dispatch.");
				}))
				.then(registerExecutor("status", () -> {
					MSGManager.sendCheckupMessage("Reporting Status...");
					MSGManager.sendStatusUpdate();
				}))
				.then(registerExecutor("open", () -> {
					MSGManager.sendPopupText("Attempting to open ND directory...");
					NeverDox.openFile(JSONManager.neverDoxConfig);
				}))
				.then(registerExecutor("sendwebhook", () -> {
					MSGManager.sendPopupText("Attempting to send test webhook...");
					List<String> testItems = List.of("This is a test message from NeverDox.");
					NeverDox.sendDiscordWebhook(testItems.get(0), testItems, false);
				}))
				.then(registerPhraseIntakeExecutor("add", phrase -> {
					JSONManager.addPhraseToJSON(phrase, false, false);
				}))
				.then(registerPhraseIntakeExecutor("addping", phrase -> {
					JSONManager.addPhraseToJSON(phrase, false, true);
				}))
				.then(registerPhraseIntakeExecutor("addexempt", phrase -> {
					JSONManager.addPhraseToJSON(phrase, true, false);
				}))
				.then(registerPhraseIntakeExecutor("remove", phrase -> {
					if (phrase != null) { JSONManager.removePhrase(phrase.toLowerCase()); }
					else { MSGManager.sendCheckupMessage("Specify a phrase to remove!"); }
				}))
		);
		dispatcher.register(literal("neverdox").redirect(neverDoxCommand));
	}

	private static LiteralArgumentBuilder<FabricClientCommandSource> registerExecutor(String name, Runnable executor) {
		return literal(name).executes(context -> {
			executor.run();
			return 1;
		});
	}

	private static LiteralArgumentBuilder<FabricClientCommandSource> registerPhraseIntakeExecutor(String name, PhraseExecutor executor) {
		return literal(name).then(argument("phrase", StringArgumentType.greedyString()).executes(context -> {
			String phrase = StringArgumentType.getString(context, "phrase");
			executor.execute(phrase);
			return 1;
		}));
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

	public static boolean processExemption(String message, JSONManager.PhraseDetails matchedItem) {
		String txt = matchedItem.getText();
		String newMsg = message.replace(txt, "");

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

	public static void sendDiscordWebhook(String message, List<String> matches, boolean ping) {

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

			String payload = "{\"content\":\"" + (ping ? "@everyone\\n" : "") + "**NeverDox Dispatch #"
					+ JSONManager.incrementDispatchNumber() + "**\\nFiltered terms: *" + matchesString + "*\\n```"
					+ message + "```\", \"username\":\"NeverDox\"}";

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

	public static boolean webhookCheck() { // there's probably a better solution to this
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