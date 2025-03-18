package redot.neverdox.mixin.client;

import com.google.inject.Singleton;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redot.neverdox.NeverDox;
import redot.neverdox.model.Filter;
import redot.neverdox.model.MessageQueue;
import redot.neverdox.model.WebhookManager;
import redot.neverdox.util.Constants;
import redot.neverdox.util.Extensions;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
@Mixin(ChatHud.class)
@ExtensionMethod(Extensions.class)
public class ReceiveChatMessageMixin {

	private final MessageQueue messageQueue = new MessageQueue(15);

	@Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
	private void addMessage(Text message, CallbackInfo ci) {
		if (!NeverDox.enabled) {
			return;
		}

		String msg = message.getString();

		CompletableFuture.runAsync(() -> { // async for performance
			Constants.handleMessage(msg);
			this.messageQueue.addMessage(msg);

			if (this.messageQueue.getMatchCountFor(msg) == 3) {
				WebhookManager.getWebhooks().forEach(webhook -> {
					if (webhook.isSpamDetecting()) {
						webhook.sendToDiscord(msg, new Filter(webhook, List.of("Likely Spam"), false));
					}
				});
			}
		});
	}

}