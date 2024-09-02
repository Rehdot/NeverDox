package redot.neverdox.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import redot.neverdox.util.Constants;
import redot.neverdox.util.Extensions;
import redot.neverdox.util.NDException;
import redot.neverdox.util.Serialization;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static redot.neverdox.util.Serialization.tryIO;

@ExtensionMethod(Extensions.class)
public class Webhook {

    @Getter @Setter
    private String webhookLink;
    @Getter @Setter
    private long dispatchCount;
    @Getter
    private final UUID identifier = UUID.randomUUID();
    private final LinkedList<Phrase> allPhrases;

    public Webhook(String link, Set<Phrase> phrases) {
        this.webhookLink = link;
        this.allPhrases = new LinkedList<>(phrases);
        this.dispatchCount = 1;
    }

    public void addPhrase(Phrase phrase) {
        allPhrases.addFirst(phrase);
        savePhrases();
    }

    public void addPhrases(List<Phrase> phrases) {
        allPhrases.addAll(phrases);
        savePhrases();
    }

    public void removePhrase(Phrase phrase) {
        allPhrases.remove(phrase);
        savePhrases();
    }

    public boolean hasPhrase(Phrase phrase) {
        return allPhrases.stream().anyMatch(phr -> phr.getIdentifier() == phrase.getIdentifier());
    }

    public void savePhrases() {
        Serialization.serializeWebhooks();
    }

    public List<Phrase> getAllPhrases() {
        return new ArrayList<>(allPhrases);
    }

    public void sendToDiscord(String message, Filter filter) {
        message = message.replace("\"", "\\\"");
        String filters = String.join(", ", filter.getTerms());
        URL url = this.webhookLink.newURL().ifNull(() -> {
            throw new NDException("Webhook Failure - Bad URL: " + this.webhookLink);
        });
        HttpURLConnection connection = url.getConnection().ifNull(() -> {
            throw new NDException("Webhook Failure - Failed to establish URL connection.");
        });

        tryIO(() -> {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("User-Agent", "Java HTTP Request");
			connection.setDoOutput(true);
        });

        String payload = "{\"content\":\"" + (filter.isPing() ? "@everyone\\n" : "") + "**NeverDox Dispatch #"
					+ this.dispatchCount++ + "**\\nFiltered terms: *" + filters + "*\\n```"
					+ message + "```\", \"username\":\"NeverDox\"}";

        tryIO(() -> {
            OutputStream outStream = connection.getOutputStream();
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
			outStream.write(input, 0, input.length);
            outStream.close();
        }).ifNull(() -> {
            throw new NDException("Webhook Failure - Failed to send content.");
        });

        tryIO(() -> {
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                Constants.LOGGER.info("Sent webhook successfully.");
            }
        }).ifNull(() -> {
            throw new NDException("Webhook Failure - Error getting response code.");
        });

        connection.disconnect();
        Serialization.serializeWebhooks();
    }

}
