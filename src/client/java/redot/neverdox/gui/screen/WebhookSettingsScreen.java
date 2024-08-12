package redot.neverdox.gui.screen;

import com.google.common.collect.Sets;
import lombok.experimental.ExtensionMethod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import redot.neverdox.model.Phrase;
import redot.neverdox.model.Webhook;
import redot.neverdox.gui.field.PhraseField;
import redot.neverdox.gui.util.NDButtonWidget;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.Serialization;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@ExtensionMethod(Extensions.class)
public class WebhookSettingsScreen extends Screen {

    private final Webhook webhook;
    private LinkedBlockingDeque<PhraseField> fields;
    private ButtonWidget backButton, addPhraseButton;
    private PageTurnWidget pageLeft, pageRight;
    private int pageIndex, phraseY;
    private final Screen parent;

    public WebhookSettingsScreen(Webhook webhook, Screen parent) {
        super(Text.literal("Filter Phrases"));
        this.webhook = webhook;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        pageIndex = 0;

        this.backButton = new NDButtonWidget(this.width / 2 - 50, this.height - 30, 100, 20, Text.literal("Back"), button -> {
            saveInfo();
            this.client.setScreen(parent);
        });

        this.addPhraseButton = new NDButtonWidget(this.width / 2 - 50, 40, 100, 20, Text.literal("Add Phrase"), button -> {
            addPhraseField(new Phrase("", false, false));
        });

        this.pageLeft = new PageTurnWidget(this.width / 2 - 150, this.height - 30, false, button -> {
            this.pageIndex--;
            this.saveInfo();
            this.reinitializeFields();
        }, false);

        this.pageRight = new PageTurnWidget(this.width / 2 + 125, this.height - 30, true, button -> {
            this.pageIndex++;
            this.saveInfo();
            this.reinitializeFields();
        }, false);

        this.reinitializeFields();
    }

    @Override
    public void close() {
        super.close();
        saveInfo();
    }

    private void saveInfo() {
        this.fields.forEach(field -> field.getPhrase().setTexts(field.getTextFieldWidgets().stream()
                .map(TextFieldWidget::getText)
                .collect(Collectors.toCollection(LinkedBlockingDeque::new))));

        List<Phrase> newPhrases = this.fields.stream()
                .map(PhraseField::getPhrase)
                .filter(phrase -> !this.webhook.hasPhrase(phrase))
                .toList();

        this.webhook.addPhrases(newPhrases);
        Serialization.serializeWebhooks();
    }

    private PhraseField initPhraseField(Phrase phrase) {
        AtomicInteger xValue = new AtomicInteger(20);
        Set<TextFieldWidget> phraseTextFields = Sets.newHashSet();

        if (this.phraseY > 410) this.phraseY = 70;

        phrase.getTexts().forEach(text -> {
            TextFieldWidget phraseTextField = new TextFieldWidget(this.textRenderer, xValue.getAndAdd(160), phraseY, 150, 20, Text.literal("Phrase"));
            phraseTextField.setMaxLength(150);
            phraseTextField.setText(text);
            phraseTextFields.add(phraseTextField);
        });

        NDButtonWidget pingsButton = new NDButtonWidget(xValue.getAndAdd(110), phraseY, 100, 20, Text.literal("Pings " + (phrase.isPinged() ? "En" : "Dis") + "abled"), button -> {
            phrase.setPinged(!phrase.isPinged());
            saveInfo();
            this.reinitializeFields();
        });

        NDButtonWidget exemptButton = new NDButtonWidget(xValue.getAndAdd(110), phraseY, 100, 20, Text.literal("Is" + (phrase.isExempt() ? "" : " Not") + " Exempt"), button -> {
            phrase.setExempt(!phrase.isExempt());
            saveInfo();
            this.reinitializeFields();
        });

        // this trick only works by calling saveInfo() twice
        NDButtonWidget addTextToPhraseButton = new NDButtonWidget(xValue.getAndAdd(110), phraseY, 100, 20, Text.literal("Add Text"), button -> {
            saveInfo();
            phrase.addText("");
            this.reinitializeFields();
            saveInfo();
        });
        addTextToPhraseButton.setTooltip(Tooltip.of(Text.literal("Chat messages containing EVERY text element specified will be filtered.")));

        NDButtonWidget deleteButton = new NDButtonWidget(xValue.getAndAdd(110), phraseY, 100, 20, Text.literal("Delete"), button -> {
            removePhraseField(phrase);
        });

        phraseY += 30;
        return new PhraseField(phrase, phraseTextFields, pingsButton, exemptButton, addTextToPhraseButton, deleteButton);
    }

    private void removePhraseField(Phrase phrase) {
        UUID uuid = phrase.getIdentifier();
        PhraseField targetField = this.fields.stream()
                .filter(field -> field.getPhrase().getIdentifier() == uuid)
                .findFirst()
                .orElse(null);

        if (targetField != null) {
            this.fields.remove(targetField);
            this.webhook.removePhrase(targetField.getPhrase());

            saveInfo();
            this.reinitializeFields();
        }
    }

    private void addPhraseField(Phrase phrase) {
        this.fields.offerFirst(initPhraseField(phrase));
        this.webhook.addPhrase(phrase);

        this.saveInfo();
        this.reinitializeFields();
    }

    private void reinitializeFields() {
        this.clearChildren();
        this.phraseY = 70;

        this.fields = this.webhook.getAllPhrases().stream()
                .map(this::initPhraseField)
                .collect(Collectors.toCollection(LinkedBlockingDeque::new));

        this.addDrawableChild(backButton);
        this.addDrawableChild(addPhraseButton);

        int index = pageIndex * 12;
        List<PhraseField> fieldList = new ArrayList<>(this.fields);

        for (int i = index; i < index + 12; i++) {
            if (i + 1 > fieldList.size()) continue;

            PhraseField field = fieldList.get(i);

            field.getTextFieldWidgets().forEach(this::addDrawableChild);
            this.addDrawableChild(field.getPingsButton());
            this.addDrawableChild(field.getExemptButton());
            this.addDrawableChild(field.getAddTextButton());
            this.addDrawableChild(field.getDeleteButton());
        }

        if (!(index + 13 > fieldList.size())) {
            this.addDrawableChild(pageRight);
        }

        if (!(index - 1 < 0)) {
            this.addDrawableChild(pageLeft);
        }

    }

}
