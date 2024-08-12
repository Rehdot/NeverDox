package redot.neverdox.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import redot.neverdox.NeverDox;
import redot.neverdox.model.Webhook;
import redot.neverdox.model.WebhookManager;
import redot.neverdox.gui.field.WebhookField;
import redot.neverdox.gui.util.NDButtonWidget;
import redot.neverdox.util.Serialization;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class WebhookScreen extends Screen {

    private int webhookY = 0;
    private LinkedHashSet<WebhookField> fields;
    private ButtonWidget backButton, addButton, toggleButton;
    private final Screen parent;

    public WebhookScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.webhookY = 80;

        this.addButton = new NDButtonWidget(this.width / 2 - 50, 40, 100, 20, Text.literal("Add Webhook"), button -> {
            saveInfo();
            addWebhookField(new Webhook("", Set.of()));
        });

        this.backButton = new NDButtonWidget(this.width / 2 - 50, this.height - 30, 100, 20, Text.literal("Back"), button -> {
            saveInfo();
            this.client.setScreen(parent);
        });

        this.reinitializeFields();
    }

    @Override
    public void close() {
        super.close();
        saveInfo();
    }

    private void saveInfo() {
        // save all webhook texts to their objects
        fields.forEach(field -> field.getWebhook().setWebhookLink(field.getTextFieldWidget().getText()));

        // find all webhooks that aren't currently in directory
        List<Webhook> newWebhooks = fields.stream()
                .map(WebhookField::getWebhook)
                .filter(webhook -> !WebhookManager.hasWebhook(webhook))
                .toList();

        // add missing webhooks; save all
        WebhookManager.addWebhooks(newWebhooks);
        Serialization.serializeWebhooks();
    }

    private void addWebhookField(Webhook webhook) {
        fields.add(initWebhookField(webhook));
        WebhookManager.addWebhook(webhook);

        saveInfo();
        this.reinitializeFields();
    }

    private void removeWebhookField(Webhook webhook) {
        UUID uuid = webhook.getIdentifier();
        WebhookField targetField = fields.stream()
                .filter(field -> field.getWebhook().getIdentifier() == uuid)
                .findFirst()
                .get();

        fields.remove(targetField);
        WebhookManager.removeWebhook(targetField.getWebhook());

        this.reinitializeFields();
    }

    private WebhookField initWebhookField(Webhook webhook) {
        TextFieldWidget webhookTextField = new TextFieldWidget(this.textRenderer, 20, webhookY, 200, 20, Text.literal("Webhook"));
        webhookTextField.setMaxLength(150);
        webhookTextField.setText(webhook.getWebhookLink());

        ButtonWidget settingsButton = new NDButtonWidget(230, webhookY, 50, 20, Text.literal("Settings"), button -> {
            saveInfo();
            this.client.setScreen(new WebhookSettingsScreen(webhook, this));
        });

        ButtonWidget deleteButton = new NDButtonWidget(290, webhookY, 50, 20, Text.literal("Delete"), button -> {
            removeWebhookField(webhook);
            saveInfo();
        });

        webhookY += 30;
        return new WebhookField(webhook, webhookTextField, settingsButton, deleteButton);
    }

    private void reinitializeFields() {
        this.clearChildren();

        webhookY = 80;
        this.fields = WebhookManager.getWebhooks().stream()
                .map(this::initWebhookField)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        this.toggleButton = new NDButtonWidget(this.width / 2 - 200, 40, 100, 20, Text.literal("NeverDox "+(NeverDox.enabled?"En":"Dis")+"abled"), button -> {
            NeverDox.enabled = !NeverDox.enabled;
            this.reinitializeFields();
        });

        this.addDrawableChild(addButton);
        this.addDrawableChild(backButton);
        this.addDrawableChild(toggleButton);

        fields.forEach(field -> {
            this.addDrawableChild(field.getTextFieldWidget());
            this.addDrawableChild(field.getSettingsButton());
            this.addDrawableChild(field.getDeleteButton());
        });
    }
}
