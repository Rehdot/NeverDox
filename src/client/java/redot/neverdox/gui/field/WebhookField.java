package redot.neverdox.gui.field;

import lombok.Getter;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import redot.neverdox.model.Webhook;

import java.util.List;
import java.util.Set;

@Getter
public class WebhookField extends Field {

    private final Webhook webhook;
    private final ButtonWidget settingsButton;

    public WebhookField(Webhook webhook, TextFieldWidget textFieldWidget, ButtonWidget settingsButton, ButtonWidget deleteButton) {
        super(deleteButton, Set.of(textFieldWidget));
        this.webhook = webhook;
        this.settingsButton = settingsButton;
    }

    @Override
    public List<? extends ButtonWidget> getButtons() {
        return List.of(this.settingsButton, this.getDeleteButton());
    }

}
