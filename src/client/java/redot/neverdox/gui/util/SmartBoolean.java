package redot.neverdox.gui.util;

// A non-atomic boolean that can survive lambda expressions
public final class SmartBoolean {
    public boolean value;

    public SmartBoolean(boolean value) {
        this.value = value;
    }

    public boolean invert() {
        this.value = !value;
        return value;
    }

}
