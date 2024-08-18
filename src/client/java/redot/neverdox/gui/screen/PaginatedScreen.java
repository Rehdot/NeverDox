package redot.neverdox.gui.screen;

import lombok.experimental.ExtensionMethod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.text.Text;
import redot.neverdox.gui.field.Field;
import redot.neverdox.gui.util.NDButtonWidget;
import redot.neverdox.util.Constants;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.NDException;

import java.util.LinkedList;

@Environment(EnvType.CLIENT)
@ExtensionMethod(Extensions.class)
public abstract class PaginatedScreen<T extends Field> extends Screen {

    public int elementY = this.resetY(), pageIndex;
    public final Screen parent;
    public ButtonWidget backButton;
    private PageTurnWidget pageLeft, pageRight;
    public LinkedList<T> fields;

    protected PaginatedScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.pageIndex = 0;

        this.backButton = new NDButtonWidget(this.width / 2 - 50, this.height - 30, 100, 20, Text.literal("Back"), button -> {
            this.saveInfo();
            Constants.client.setScreen(this.parent);
        });

        this.pageLeft = new PageTurnWidget(this.width / 2 - 150, this.height - 30, false, button -> {
            this.pageIndex--;
            this.saveInfo();
            this.redraw();
        }, false);

        this.pageRight = new PageTurnWidget(this.width / 2 + 125, this.height - 30, true, button -> {
            this.pageIndex++;
            this.saveInfo();
            this.redraw();
        }, false);

    }

    protected void redraw() {
        this.clearChildren();
        this.addDrawableChild(this.backButton);
        this.addDrawableChild(this.getAddElementButton());

        int size = this.fields.size();
        int index = this.pageIndex * 12;

        for (int i = index; i < index + 12; i++) {
            if (i + 1 > size) break;

            Field field = this.fields.get(i).ifNull(() -> {
                throw new NDException("Screen Failure - An element returned null.");
            });

            field.getTextFieldWidgets().forEach(this::addDrawableChild);
            field.getButtons().forEach(this::addDrawableChild);
        }

        if (!(index + 13 > size)) {
            this.addDrawableChild(this.pageRight);
        }

        if (!(index - 1 < 0)) {
            this.addDrawableChild(this.pageLeft);
        }
    }

    protected int resetY() {
        this.elementY = 70;
        return this.elementY;
    }

    protected abstract void saveInfo();
    protected abstract NDButtonWidget getAddElementButton();

}
