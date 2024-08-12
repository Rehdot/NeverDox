package redot.neverdox.gui.field;

import lombok.Getter;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import redot.neverdox.model.Webhook;

@Getter
public class WebhookField {

    private final Webhook webhook;
    private final TextFieldWidget textFieldWidget;
    private final ButtonWidget settingsButton, deleteButton;

    public WebhookField(Webhook webhook, TextFieldWidget textFieldWidget, ButtonWidget settingsButton, ButtonWidget deleteButton) {
        this.webhook = webhook;
        this.textFieldWidget = textFieldWidget;
        this.settingsButton = settingsButton;
        this.deleteButton = deleteButton;
    }

}
