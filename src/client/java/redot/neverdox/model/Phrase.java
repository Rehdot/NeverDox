package redot.neverdox.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import redot.neverdox.gui.util.SmartBoolean;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.Messenger;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@ExtensionMethod(Extensions.class)
public class Phrase {

    @Setter
    private ArrayList<String> texts;
    public SmartBoolean exempt, pinged, regex;
    private final UUID identifier = UUID.randomUUID();

    public Phrase(String text, boolean exempt, boolean pinged) {
        this.texts = new ArrayList<>().with(text);
        this.exempt = new SmartBoolean(exempt);
        this.pinged = new SmartBoolean(pinged);
        this.regex = new SmartBoolean(false);
    }

    public Phrase addText(String text) {
        if (texts.size() > 2) {
            Messenger.sendErrorText("The maximum text count is 3!");
            return this;
        }
        texts.add(text);
        return this;
    }

    public boolean isExempt() {
        return this.exempt.value;
    }

    public boolean isPinged() {
        return this.pinged.value;
    }

    public boolean isRegex() {
        return this.regex.value;
    }

}
