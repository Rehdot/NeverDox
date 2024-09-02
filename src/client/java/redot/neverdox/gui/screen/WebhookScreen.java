package redot.neverdox.gui.screen;

import lombok.experimental.ExtensionMethod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import redot.neverdox.NeverDox;
import redot.neverdox.gui.field.WebhookField;
import redot.neverdox.gui.util.NDButtonWidget;
import redot.neverdox.model.Webhook;
import redot.neverdox.model.WebhookManager;
import redot.neverdox.util.Constants;
import redot.neverdox.util.Extensions;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@ExtensionMethod(Extensions.class)
public class WebhookScreen extends PaginatedScreen<WebhookField> {

    public WebhookScreen(Text title, Screen parent) {
        super(title, parent);
    }

    @Override
    protected void init() {
        super.init();
        this.redraw();
    }

    @Override
    protected void saveInfo() {
        fields.forEach(field -> field.getWebhook().setWebhookLink(field.getTextFieldWidgets().stream()
                .findFirst()
                .get()
                .getText()));

        List<Webhook> newWebhooks = fields.stream()
                .map(WebhookField::getWebhook)
                .filter(webhook -> !WebhookManager.hasWebhook(webhook))
                .toList();

        WebhookManager.addWebhooks(newWebhooks);
    }

    @Override
    protected ButtonWidget getAddElementButton() {
        return new NDButtonWidget(this.width / 2 - 50, 40, 100, 20, Text.literal("Add Webhook"), button -> {
            this.saveInfo();
            this.addWebhookField(new Webhook("", Set.of()));
        });
    }

    private ButtonWidget getToggleButton() {
        return new NDButtonWidget(this.width / 2 - 200, 40, 100, 20, Text.literal("NeverDox "+(NeverDox.enabled?"En":"Dis")+"abled"), button -> {
            NeverDox.enabled = !NeverDox.enabled;
            this.saveInfo();
            this.redraw();
        });
    }

    private void addWebhookField(Webhook webhook) {
        fields.add(initWebhookField(webhook));
        WebhookManager.addWebhook(webhook);

        this.saveInfo();
        this.redraw();
    }

    private void removeWebhookField(Webhook webhook) {
        UUID uuid = webhook.getIdentifier();
        WebhookField targetField = this.fields.stream()
                .filter(field -> field.getWebhook().getIdentifier() == uuid)
                .findFirst()
                .get();

        this.fields.remove(targetField);
        WebhookManager.removeWebhook(targetField.getWebhook());
    }

    private WebhookField initWebhookField(Webhook webhook) {
        if (this.elementY > 410) this.resetY();

        TextFieldWidget webhookTextField = new TextFieldWidget(this.textRenderer, 20, this.elementY, 200, 20, Text.literal("Webhook"));
        ArrayList<ButtonWidget> buttons = new ArrayList<>();

        webhookTextField.setMaxLength(150);
        webhookTextField.setText(webhook.getWebhookLink());

        new NDButtonWidget(230, this.elementY, 50, 20, Text.literal("Settings"), button -> {
            this.saveInfo();
            Constants.client.setScreen(new WebhookSettingsScreen(webhook, this));
        }).apply(button -> {
            buttons.add(button);
            return true;
        });

        new NDButtonWidget(290, this.elementY, 50, 20, Text.literal("Delete"), button -> {
            removeWebhookField(webhook);
            this.saveInfo();
            this.redraw();
        }).apply(button -> {
            buttons.add(button);
            return true;
        });

        this.elementY += 30;
        return new WebhookField(webhook, webhookTextField, buttons);
    }

    @Override
    public void redraw() {
        this.resetY();

        this.fields = WebhookManager.getWebhooks().stream()
                .map(this::initWebhookField)
                .collect(Collectors.toCollection(LinkedList::new));

        super.redraw();
        this.addDrawableChild(this.getToggleButton());
    }
}
