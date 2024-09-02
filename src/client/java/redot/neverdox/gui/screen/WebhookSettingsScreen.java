package redot.neverdox.gui.screen;

import com.google.common.collect.Sets;
import lombok.experimental.ExtensionMethod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.apache.commons.compress.utils.Lists;
import redot.neverdox.gui.field.Field;
import redot.neverdox.gui.field.PhraseField;
import redot.neverdox.gui.util.NDButtonWidget;
import redot.neverdox.gui.util.SmartBoolean;
import redot.neverdox.model.Phrase;
import redot.neverdox.model.Webhook;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.NDException;
import redot.neverdox.util.Serialization;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@ExtensionMethod(Extensions.class)
public class WebhookSettingsScreen extends PaginatedScreen<PhraseField> {

    private final Webhook webhook;

    public WebhookSettingsScreen(Webhook webhook, Screen parent) {
        super(Text.literal("Filtered Phrases"), parent);
        this.webhook = webhook;
    }

    @Override
    protected void init() {
        super.init();
        this.redraw();
    }

    @Override
    protected void saveInfo() {
        this.fields.forEach(field -> field.getPhrase().setTexts(field.getTextFieldWidgets().stream()
                .map(TextFieldWidget::getText)
                .collect(Collectors.toCollection(ArrayList::new))));

        List<Phrase> newPhrases = this.fields.stream()
                .map(PhraseField::getPhrase)
                .filter(phrase -> !this.webhook.hasPhrase(phrase))
                .toList();

        this.webhook.addPhrases(newPhrases);
        Serialization.serializeWebhooks();
    }

    @Override
    protected ButtonWidget getAddElementButton() {
        return new NDButtonWidget(this.width / 2 - 50, 40, 100, 20, Text.literal("Add Phrase"), button -> {
            this.addPhraseField(new Phrase("New Filter", false, false));
        });
    }

    private PhraseField initPhraseField(Phrase phrase) {
        AtomicInteger xValue = new AtomicInteger(20);
        ArrayList<TextFieldWidget> textFields = new ArrayList<>();
        ArrayList<ButtonWidget> buttons = new ArrayList<>();

        if (this.elementY > 410) this.resetY();

        phrase.getTexts().forEach(text -> {
            new TextFieldWidget(this.textRenderer, xValue.getAndAdd(110), this.elementY, 100, 20, Text.literal("Phrase")).apply(widget -> {
                widget.setMaxLength(300);
                widget.setText(text);
                textFields.add(widget);
                return true;
            });
        });

        new NDButtonWidget(xValue.get(), this.elementY, 80, 20, Text.literal("Ping " + (phrase.isPinged() ? "En" : "Dis") + "abled"), button -> {
            phrase.pinged.invert();
            this.saveInfo();
            this.redraw();
        }).apply(button -> {
            if (!phrase.isExempt()) {
                xValue.getAndAdd(90);
                buttons.add(button);
            }
            return true;
        });

        new NDButtonWidget(xValue.get(), this.elementY, 80, 20, Text.literal((phrase.isExempt() ? "Is" : "Not") + " Exempt"), button -> {
            phrase.exempt.invert();
            this.saveInfo();
            this.redraw();
        }).apply(button -> {
            if (!phrase.isPinged()) {
                xValue.getAndAdd(90);
                buttons.add(button);
            }
            return true;
        });

        new NDButtonWidget(xValue.getAndAdd(100), this.elementY, 90, 20, Text.literal("Regex " + (phrase.isRegex() ? "En" : "Dis") + "abled"), button -> {
            phrase.regex.invert();
            this.saveInfo();
            this.redraw();
        }).apply(button -> {
            button.setTooltip(Tooltip.of(Text.literal("Parses this phrase using Regex.")));
            buttons.add(button);
            return true;
        });

        new NDButtonWidget(xValue.getAndAdd(70), this.elementY, 60, 20, Text.literal("Add Text"), button -> {
            this.saveInfo();
            phrase.addText("");
            this.redraw();
            this.saveInfo();
        }).apply(button -> {
            button.setTooltip(Tooltip.of(Text.literal("Chat messages containing EVERY text element specified will be filtered.")));
            buttons.add(button);
            return true;
        });

        new NDButtonWidget(xValue.getAndAdd(70), this.elementY, 60, 20, Text.literal("Delete"), button -> {
            this.removePhraseField(phrase);
            this.saveInfo();
            this.redraw();
        }).apply(buttons::add);

        this.elementY += 30;
        return new PhraseField(phrase, textFields, buttons);
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
