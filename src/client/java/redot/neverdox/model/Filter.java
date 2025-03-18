package redot.neverdox.model;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import redot.neverdox.util.Extensions;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@ExtensionMethod(Extensions.class)
public class Filter {

    private Webhook webhook;
    private final LinkedHashSet<String> terms;
    private boolean ping;

    public Filter(Webhook webhook, Collection<String> terms, boolean ping) {
        this.webhook = webhook;
        this.terms = Sets.newLinkedHashSet(terms);
        this.ping = ping;
    }

    public boolean hasWebhook(Webhook webhook) {
        return this.webhook.getIdentifier() == webhook.getIdentifier();
    }

    public Filter addTerms(Collection<String> terms) {
        this.terms.addAll(terms);
        return this;
    }

    public Filter setPing(boolean ping) {
        this.ping = ping;
        return this;
    }

    public static Set<Filter> allFiltersContaining(String in) {
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

}
