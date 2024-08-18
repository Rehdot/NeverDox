package redot.neverdox.gui.field;

import lombok.Getter;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.List;
import java.util.Set;

@Getter
public abstract class Field {

    private final ButtonWidget deleteButton;
    private final Set<TextFieldWidget> textFieldWidgets;

    public Field(ButtonWidget deleteButton, Set<TextFieldWidget> textFieldWidgets) {
        this.deleteButton = deleteButton;
        this.textFieldWidgets = textFieldWidgets;
    }

    public abstract List<? extends ButtonWidget> getButtons();

}
