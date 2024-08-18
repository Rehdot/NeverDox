package redot.neverdox.mixin.client;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redot.neverdox.NeverDox;
import redot.neverdox.util.Constants;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatHud.class)
public class ReceiveChatMessageMixin {
	@Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
	private void addMessage(Text message, CallbackInfo ci) {
		// async for performance... I wouldn't call handleMessage without it.
		if (NeverDox.enabled) {
			CompletableFuture.runAsync(() -> Constants.handleMessage(message.getString()));
		}
	}

}