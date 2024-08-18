package redot.neverdox.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redot.neverdox.gui.screen.WebhookScreen;
import redot.neverdox.gui.util.NDButtonWidget;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public class SettingsMenuMixin extends Screen {

    protected SettingsMenuMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void addModsButton(CallbackInfo ci) {
        this.addDrawableChild(this.createScreenButton(Text.literal("NeverDox Settings"),
                () -> new WebhookScreen(Text.literal("Webhooks"), this)));
    }

    private ButtonWidget createScreenButton(Text message, Supplier<Screen> screenSupplier) {
        return new NDButtonWidget(this.width / 2 - 75, this.height / 6 + 16, 150, 20, message, button -> this.client.setScreen(screenSupplier.get()));
    }
}
