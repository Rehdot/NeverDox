package redot.neverdox.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.Messenger;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@ExtensionMethod(Extensions.class)
public class Phrase {

    @Setter
    private LinkedList<String> texts;
    @Setter
    private boolean exempt, pinged;
    private final UUID identifier = UUID.randomUUID();

    public Phrase(String text, boolean exempt, boolean pinged) {
        this.texts = Lists.newLinkedList(List.of(text));
        this.exempt = exempt;
        this.pinged = pinged;
    }

    public Phrase addText(String text) {
        if (texts.size() > 2) {
            Messenger.sendErrorText("The maximum text count is 3!");
            return this;
        }
        texts.addFirst(text);
        return this;
    }

}
