package com.redot.neverdox.mixin.client;

import com.redot.neverdox.MSGManager;
import com.redot.neverdox.NeverDox;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("RETURN"))
    private void onMainMenuInit(CallbackInfo info) {
        if (!NeverDox.sentPopup) {
            MSGManager.sendPopupText("Welcome.");
            NeverDox.sentPopup = true;
        }
    }
}