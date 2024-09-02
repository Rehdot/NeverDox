package redot.neverdox.gui.field;

import lombok.Getter;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import redot.neverdox.model.Phrase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class PhraseField extends Field {

    private final Phrase phrase;

    public PhraseField(Phrase phrase, ArrayList<TextFieldWidget> textFieldWidgets, ArrayList<ButtonWidget> buttons) {
        super(textFieldWidgets, buttons);
        this.phrase = phrase;
    }

}
