package redot.neverdox.util;

import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Nullable;
import redot.neverdox.action.Action;
import redot.neverdox.action.ExceptionAction;
import redot.neverdox.model.Webhook;
import redot.neverdox.model.WebhookManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static redot.neverdox.util.Constants.*;

@ExtensionMethod(Extensions.class)
public class Serialization {

    public static File getWebhookFile() {
        File file = new File(filePath);
        if (file.exists()) return file;

        tryIO(file::createNewFile);
        return file;
    }

    public static List<Webhook> getSerializedWebhooks() {
        List<Webhook> webhooks = new ArrayList<>();

        tryRead(reader -> {
            List<Webhook> deserializedWebhooks = gson.fromJson(reader, webhookListType);
            if (deserializedWebhooks != null) {
                webhooks.addAll(deserializedWebhooks);
            }
        });

        return webhooks;
    }

    public static void openFile() {
        tryIO(() -> {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("cmd", "/c", "start", getWebhookFile().getAbsolutePath());
            pb.start();
        });
    }

    public static void serializeWebhooks() {
        tryWrite(writer -> gson.toJson(WebhookManager.getWebhooks(), writer));
    }

    static void tryWrite(Action<FileWriter> writerAction) {
        try (FileWriter writer = new FileWriter(getWebhookFile())) {
            writerAction.execute(writer);
        } catch (IOException ignored) {}
    }

    static void tryRead(Action<FileReader> readerAction) {
        try (FileReader reader = new FileReader(getWebhookFile())) {
            readerAction.execute(reader);
        } catch (IOException ignored) {}
    }

    @Nullable
    public static String tryIO(ExceptionAction<IOException> ioAction) {
        try {
            ioAction.execute();
            return "Success";
        } catch (IOException ignored) {
            return null;
        }
    }

}