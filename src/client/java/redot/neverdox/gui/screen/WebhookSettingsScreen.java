package redot.neverdox.gui.screen;

import com.google.common.collect.Sets;
import lombok.experimental.ExtensionMethod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import redot.neverdox.gui.field.PhraseField;
import redot.neverdox.gui.util.NDButtonWidget;
import redot.neverdox.model.Phrase;
import redot.neverdox.model.Webhook;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.NDException;
import redot.neverdox.util.Serialization;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@ExtensionMethod(Extensions.class)
public class WebhookSettingsScreen extends PaginatedScreen<PhraseField> {

    private final Webhook webhook;

    public WebhookSettingsScreen(Webhook webhook, Screen parent) {
        super(Text.literal("Filter Phrases"), parent);
        this.webhook = webhook;
    }

    @Override
    protected void init() {
        super.init();
        this.redraw();
    }

    @Override
    public void close() {
        super.close();
        this.saveInfo();
    }

    @Override
    protected void saveInfo() {
        this.fields.forEach(field -> field.getPhrase().setTexts(field.getTextFieldWidgets().stream()
                .map(TextFieldWidget::getText)
                .collect(Collectors.toCollection(LinkedList::new))));

        List<Phrase> newPhrases = this.fields.stream()
                .map(PhraseField::getPhrase)
                .filter(phrase -> !this.webhook.hasPhrase(phrase))
                .toList();

        this.webhook.addPhrases(newPhrases);
        Serialization.serializeWebhooks();
    }

    @Override
    protected NDButtonWidget getAddElementButton() {
        return new NDButtonWidget(this.width / 2 - 50, 40, 100, 20, Text.literal("Add Phrase"), button -> {
            this.addPhraseField(new Phrase("New Filter", false, false));
        });
    }

    private PhraseField initPhraseField(Phrase phrase) {
        AtomicInteger xValue = new AtomicInteger(20);
        Set<TextFieldWidget> phraseTextFields = Sets.newHashSet();

        if (this.elementY > 410) this.elementY = 70;

        phrase.getTexts().forEach(text -> {
            TextFieldWidget phraseTextField = new TextFieldWidget(this.textRenderer, xValue.getAndAdd(160), this.elementY, 150, 20, Text.literal("Phrase"));
            phraseTextField.setMaxLength(150);
            phraseTextField.setText(text);
            phraseTextFields.add(phraseTextField);
        });

        NDButtonWidget pingsButton = new NDButtonWidget(xValue.getAndAdd(110), this.elementY, 100, 20, Text.literal("Pings " + (phrase.isPinged() ? "En" : "Dis") + "abled"), button -> {
            phrase.setPinged(!phrase.isPinged());
            this.saveInfo();
            this.redraw();
        });

        NDButtonWidget exemptButton = new NDButtonWidget(xValue.getAndAdd(110), this.elementY, 100, 20, Text.literal((phrase.isExempt() ? "Is" : "Not") + " Exempt"), button -> {
            phrase.setExempt(!phrase.isExempt());
            this.saveInfo();
            this.redraw();
        });

        NDButtonWidget addTextToPhraseButton = new NDButtonWidget(xValue.getAndAdd(110), this.elementY, 100, 20, Text.literal("Add Text"), button -> {
            this.saveInfo();
            phrase.addText("");
            this.redraw();
            this.saveInfo();
        });
        addTextToPhraseButton.setTooltip(Tooltip.of(Text.literal("Chat messages containing EVERY text element specified will be filtered.")));

        NDButtonWidget deleteButton = new NDButtonWidget(xValue.getAndAdd(110), this.elementY, 100, 20, Text.literal("Delete"), button -> {
            this.removePhraseField(phrase);
            saveInfo();
            this.redraw();
        });

        this.elementY += 30;
        return new PhraseField(phrase, phraseTextFields, pingsButton, exemptButton, addTextToPhraseButton, deleteButton);
    }

    private void addPhraseField(Phrase phrase) {
        this.fields.addFirst(initPhraseField(phrase));
        this.webhook.addPhrase(phrase);

        this.saveInfo();
        this.redraw();
    }

    private void removePhraseField(Phrase phrase) {
        UUID uuid = phrase.getIdentifier();
        PhraseField targetField = this.fields.stream()
                .filter(field -> field.getPhrase().getIdentifier() == uuid)
                .findFirst()
                .orElse(null).ifNull(() -> {
                    throw new NDException("Removal Failure - An element returned null.");
                });

        this.fields.remove(targetField);
        this.webhook.removePhrase(targetField.getPhrase());
    }

    @Override
    protected void redraw() {
        this.resetY();

        this.fields = this.webhook.getAllPhrases().stream()
                .map(this::initPhraseField)
                .collect(Collectors.toCollection(LinkedList::new));

        super.redraw();
    }

}
