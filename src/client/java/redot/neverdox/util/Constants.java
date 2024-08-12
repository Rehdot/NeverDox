package redot.neverdox.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redot.neverdox.model.Filter;
import redot.neverdox.model.Webhook;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

@ExtensionMethod(Extensions.class)
public class Constants {

    public static final Logger LOGGER = LoggerFactory.getLogger("NeverDox");
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Type webhookListType = new TypeToken<List<Webhook>>() {}.getType();
    public static final String appDataDir = System.getenv("APPDATA"),
            minecraftDir = appDataDir + "/.minecraft",
            filePath = minecraftDir + "/NeverDoxConfig.json";

    public static void handleMessage(String message) {
        Set<Filter> filters = message.getFilters();

        if (filters.isEmpty()) return;

        filters.forEach(filter -> filter.getWebhook().sendToDiscord(message, filter));
    }

}
