package redot.neverdox.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.Messenger;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

@Getter
@ExtensionMethod(Extensions.class)
public class Phrase {

    @Setter
    private LinkedBlockingDeque<String> texts;
    @Setter
    private boolean exempt, pinged;
    private final UUID identifier = UUID.randomUUID();

    public Phrase(String text, boolean exempt, boolean pinged) {
        this.texts = new LinkedBlockingDeque<>();
        this.exempt = exempt;
        this.pinged = pinged;
        texts.addFirst(text);
    }

    public Phrase(Set<String> text, boolean exempt, boolean pinged) {
        this.texts = new LinkedBlockingDeque<>(text);
        this.exempt = exempt;
        this.pinged = pinged;
    }

    public Phrase addText(String text) {
        if (texts.size() > 2) {
            Messenger.sendErrorText("The maximum phrase count is 3!");
            return this;
        }
        texts.addLast(text);
        return this;
    }

}
