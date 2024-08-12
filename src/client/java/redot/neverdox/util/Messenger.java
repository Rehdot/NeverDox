package redot.neverdox.util;

import lombok.experimental.ExtensionMethod;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

@ExtensionMethod(Extensions.class)
public class Messenger {

    private static final MutableText ND_TEXT = Text.literal("NeverDox")
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00C0A3))
            .withItalic(true));

    public static void sendPopupText(String text) {
        sendPopupText(Text.literal(text));
    }

    public static void sendPopupText(Text text) {
        SystemToast popup = SystemToast.create(Constants.client, SystemToast.Type.PERIODIC_NOTIFICATION, ND_TEXT, text);
        Constants.client.getToastManager().add(popup);
    }

    public static void sendErrorText(String text) {
        Messenger.sendPopupText(Text.literal(text)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xD3B53D))
                .withItalic(true)));
    }

    public static void sendChatMessage(String message) {
        Constants.client.player.ifNull(() -> {
            throw new RuntimeException("Player was null, could not send designated message: " + message);
        }).sendMessage(ND_TEXT.copy()
                .append(Text.literal(" | ")
                        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x111111)).withItalic(false))
                .append(Text.literal(message)
                        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xADD8E6)).withItalic(false)))));
    }

}