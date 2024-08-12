package redot.neverdox;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import redot.neverdox.action.PhraseAction;
import redot.neverdox.util.Constants;
import redot.neverdox.util.Messenger;
import redot.neverdox.util.Serialization;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class NeverDox implements ClientModInitializer {
	public static boolean sentPopup = false, enabled = true;

	@Override
	public void onInitializeClient() {
		Constants.LOGGER.info("Initialized.");
		ClientCommandRegistrationCallback.EVENT.register(this::registerNeverDox);
	}

	private void registerNeverDox(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		final LiteralCommandNode<FabricClientCommandSource> neverDoxCommand = dispatcher.register(literal("nd")
				.then(registerExecutor("toggle", () -> {
					enabled = !enabled;
					Messenger.sendPopupText((enabled ? "En" : "Dis") + "abled Dispatch.");
				}))
				.then(registerPhraseExecutor("echo", Messenger::sendChatMessage))
				.then(registerExecutor("open", Serialization::openFile))
		);
		dispatcher.register(literal("neverdox").redirect(neverDoxCommand));
	}

	private static LiteralArgumentBuilder<FabricClientCommandSource> registerExecutor(String name, Runnable executor) {
		return literal(name).executes(context -> {
			executor.run();
			return 1;
		});
	}

	private static LiteralArgumentBuilder<FabricClientCommandSource> registerPhraseExecutor(String name, PhraseAction executor) {
		return literal(name).then(argument("phrase", StringArgumentType.greedyString()).executes(context -> {
			String phrase = StringArgumentType.getString(context, "phrase");
			executor.execute(phrase);
			return 1;
		}));
	}

}