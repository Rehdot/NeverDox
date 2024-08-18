package redot.neverdox.gui.field;

import lombok.Getter;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import redot.neverdox.gui.util.NDButtonWidget;
import redot.neverdox.model.Phrase;

import java.util.List;
import java.util.Set;

@Getter
public class PhraseField extends Field {

    private final Phrase phrase;
    private final NDButtonWidget pingsButton, exemptButton, addTextButton;

    public PhraseField(Phrase phrase, Set<TextFieldWidget> textFieldWidgets, NDButtonWidget pingsButton, NDButtonWidget exemptButton, NDButtonWidget addTextButton, NDButtonWidget deleteButton) {
        super(deleteButton, textFieldWidgets);
        this.phrase = phrase;
        this.pingsButton = pingsButton;
        this.exemptButton = exemptButton;
        this.addTextButton = addTextButton;
    }

    @Override
    public List<? extends ButtonWidget> getButtons() {
        return List.of(pingsButton, exemptButton, addTextButton, this.getDeleteButton());
    }

}
