package redot.neverdox.model;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import redot.neverdox.util.Extensions;

import java.util.Collection;
import java.util.LinkedHashSet;

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

    public Filter setWebhook(Webhook webhook) {
        this.webhook = webhook;
        return this;
    }

    public Filter addTerm(String term) {
        terms.add(term);
        return this;
    }

    public Filter addTerms(Collection<String> terms) {
        this.terms.addAll(terms);
        return this;
    }

    public Filter setPing(boolean ping) {
        this.ping = ping;
        return this;
    }

}
