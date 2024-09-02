package redot.neverdox.util;

import com.google.common.collect.Sets;
import redot.neverdox.action.CastAction;
import redot.neverdox.model.Filter;
import redot.neverdox.model.Phrase;
import redot.neverdox.model.Webhook;
import redot.neverdox.model.WebhookManager;

import java.io.IOException;
import java.lang.invoke.TypeDescriptor;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    .forEach(exemption -> exemption.getTexts()
                            .forEach(text -> {
                                if (exemption.isRegex()) {
                                    Matcher matcher = Pattern.compile(text).matcher(cleaned.get());

                                    if (matcher.find()) {
                                        cleaned.set(matcher.replaceAll(""));
                                        return;
                                    }
                                }

                                cleaned.set(cleaned.get().replace(text.toLowerCase(), ""));
                            }));

            // check remaining string for non-exempt, set filter specifications
            phrases.stream()
                    .filter(phrase -> !phrase.isExempt())
                    .filter(phrase -> phrase.getTexts().stream()
                            .allMatch(text -> {
                                if (phrase.isRegex()) {
                                    Matcher matcher = Pattern.compile(text).matcher(cleaned.get());

                                    if (matcher.find()) {
                                        return true;
                                    }
                                }

                                return cleaned.get().contains(text.toLowerCase());
                            }))
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

    public static URL newURL(String in) {
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

    // aims to be a more clean null check
    public static <T> T ifNull(T in, Runnable code) {
        if (in != null) return in;
        code.run();
        return null;
    }

    // allows running any code with an object within a lambda cleanly - and to return as any object
    public static <T, R> R apply(T in, CastAction<T, R> action) {
        return action.execute(in);
    }

    // returns collection plus elements provided
    @SafeVarargs
    public static <T, C extends Collection<T>> C with(C in, T... toAdd) {
        Collection<T> additionList = Arrays.asList(toAdd);
        in.addAll(additionList);
        return in;
    }

}
