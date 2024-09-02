package redot.neverdox.gui.field;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.apache.commons.compress.utils.Lists;
import redot.neverdox.model.Webhook;
import redot.neverdox.util.Extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@ExtensionMethod(Extensions.class)
public class WebhookField extends Field {

    private final Webhook webhook;

    public WebhookField(Webhook webhook, TextFieldWidget textFieldWidget, ArrayList<ButtonWidget> buttons) {
        super(new ArrayList<>().with(textFieldWidget), buttons);
        this.webhook = webhook;
    }

}
