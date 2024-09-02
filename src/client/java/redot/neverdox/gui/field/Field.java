package redot.neverdox.gui.field;

import lombok.Getter;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.ArrayList;

@Getter
public abstract class Field {

    private final ArrayList<TextFieldWidget> textFieldWidgets;
    private final ArrayList<ButtonWidget> buttons;

    public Field(ArrayList<TextFieldWidget> textFieldWidgets, ArrayList<ButtonWidget> buttons) {
        this.textFieldWidgets = textFieldWidgets;
        this.buttons = buttons;
    }

}