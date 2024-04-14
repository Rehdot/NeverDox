package com.redot.neverdox.mixin.client;

import com.redot.neverdox.NeverDox;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientPlayNetworkHandler.class)
public class SendChatMessageMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void interceptSendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith("?nd")) {
            ci.cancel();
            CompletableFuture.runAsync(() -> {
                try {NeverDox.formatTextAndRunCommand(message);}
                catch (IOException ignored) {}
            });
        }
    }
}
