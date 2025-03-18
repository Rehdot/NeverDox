package redot.neverdox.model;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MessageQueue {

    private final Deque<String> messages;
    private final int maxSize;

    public MessageQueue(int maxSize) {
        this.messages = new ConcurrentLinkedDeque<>();
        this.maxSize = maxSize;
    }

    public void addMessage(String message) {
        messages.offerFirst(message);

        if (messages.size() > maxSize) {
            messages.pollLast();
        }
    }

    public int getMatchCountFor(final String message) {
        int matchingCount = 0;

        for (String queueMessage : messages) {
            if (message.equalsIgnoreCase(queueMessage)) {
                matchingCount++;
            }
        }

        return matchingCount;
    }

}

