package redot.neverdox.mixin.client;

import redot.neverdox.NeverDox;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Mixin(ChatHud.class)
public class ReceiveChatMessageMixin {
	@Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
	private void interceptReceivedChatMessage(Text message, CallbackInfo ci) {
		String msg = message.getString();

		// async for performance... I wouldn't call handleChatMessage without it.
		if (NeverDox.enabled && !msg.contains("[NeverDox]")) {
			CompletableFuture.runAsync(() -> {
				try {NeverDox.handleChatMessage(msg);}
				catch (IOException ignored) {}
			});
		}
	}
}