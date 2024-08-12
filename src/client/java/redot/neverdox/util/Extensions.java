package redot.neverdox.util;

import com.google.common.collect.Sets;
import redot.neverdox.model.Filter;
import redot.neverdox.model.Phrase;
import redot.neverdox.model.Webhook;
import redot.neverdox.model.WebhookManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class Extensions {

    public static Set<Filter> getFilters(String in) {
        final List<Webhook> webhooks = WebhookManager.getWebhooks();
        final Set<Filter> filters = Sets.newHashSet();

        for (Webhook webhook : webhooks) {
            final List<Phrase> phrases = webhook.getAllPhrases();
            AtomicReference<String> cleaned = new AtomicReference<>(in.toLowerCase());

            // clean string of exemptions
            phrases.stream()
                    .filter(Phrase::isExempt)
                    .map(Phrase::getTexts)
                    .forEach(exemptions -> exemptions
                            .forEach(exemption -> cleaned.set(cleaned.get().replace(exemption, ""))));

            // check remaining string for non-exempt, set filter specifications
            phrases.stream()
                    .filter(phrase -> !phrase.isExempt())
                    .filter(phrase -> phrase.getTexts().stream()
                            .allMatch(text -> cleaned.get().contains(text.toLowerCase())))
                    .forEach(phrase -> {
                        Filter filter = filters.stream()
                                .filter(f -> f.hasWebhook(webhook))
                                .findFirst()
                                .orElse(new Filter(webhook, phrase.getTexts(), phrase.isPinged()));

                        if (!filter.isPing() && phrase.isPinged()) {
                            filter.setPing(true);
                        }

                        filter.addTerms(phrase.getTexts());
                        filters.remove(filter);
                        filters.add(filter);
                    });
        }

        return filters;
    }

    public static URL makeURL(String in) {
        try {
            return new URL(in);
        } catch (MalformedURLException ignored) {
            return null;
        }
    }

    public static HttpURLConnection getConnection(URL in) {
        try {
            return (HttpURLConnection) in.openConnection();
        } catch (IOException ignored) {
            return null;
        }
    }

    public static <T> T ifNull(T in, Runnable code) {
        if (in == null) code.run();
        return in;
    }

}
