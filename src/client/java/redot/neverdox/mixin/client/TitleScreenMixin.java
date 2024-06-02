package redot.neverdox.mixin.client;

import redot.neverdox.NeverDox;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redot.neverdox.managers.MSGManager;

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