package redot.neverdox.model;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.Serialization;

import java.util.List;

@ExtensionMethod(Extensions.class)
public class WebhookManager {

    @Getter
    private static List<Webhook> webhooks = Serialization.getSerializedWebhooks();

    public static void addWebhook(Webhook webhook) {
        webhooks.add(webhook);
        Serialization.serializeWebhooks();
    }

    public static void addWebhooks(List<Webhook> whs) {
        webhooks.addAll(whs);
        Serialization.serializeWebhooks();
    }

    public static void removeWebhook(Webhook webhook) {
        webhooks.remove(webhook);
        Serialization.serializeWebhooks();
    }

    public static boolean hasWebhook(Webhook webhook) {
        return webhooks.stream().anyMatch(wh -> wh.getIdentifier() == webhook.getIdentifier());
    }

}
