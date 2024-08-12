package redot.neverdox.gui.field;

import lombok.Getter;
import net.minecraft.client.gui.widget.TextFieldWidget;
import redot.neverdox.model.Phrase;
import redot.neverdox.gui.util.NDButtonWidget;

import java.util.Set;

@Getter
public class PhraseField {

    private final Phrase phrase;
    private final Set<TextFieldWidget> textFieldWidgets;
    private final NDButtonWidget pingsButton, exemptButton, addTextButton, deleteButton;

    public PhraseField(Phrase phrase, Set<TextFieldWidget> textFieldWidgets, NDButtonWidget pingsButton, NDButtonWidget exemptButton, NDButtonWidget addTextButton, NDButtonWidget deleteButton) {
        this.phrase = phrase;
        this.textFieldWidgets = textFieldWidgets;
        this.pingsButton = pingsButton;
        this.exemptButton = exemptButton;
        this.addTextButton = addTextButton;
        this.deleteButton = deleteButton;
    }

}
