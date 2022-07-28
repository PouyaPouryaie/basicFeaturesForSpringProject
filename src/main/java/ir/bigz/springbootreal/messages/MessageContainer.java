package ir.bigz.springbootreal.messages;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageContainer {

    private final Map<String, List<Message>> messages;
    public static final String GLOBAL_MESSAGE =
            "GLOBAL_MESSAGE";
    public static final String ERROR_MESSAGE =
            "ERROR_MESSAGE";

    private MessageContainer() {
        messages = new LinkedHashMap<>();
        messages.put(GLOBAL_MESSAGE, new ArrayList());
        messages.put(ERROR_MESSAGE, new ArrayList());
    }

    public static MessageContainer create() {
        return new MessageContainer();
    }

    public MessageContainer add(String messageKey, Object... messageParams) {
        return add(new Message(messageKey, messageParams));
    }

    public MessageContainer add(String property, String messageKey, Object... messageParams) {
        return add(property, new Message(messageKey, messageParams));
    }

    public MessageContainer addError(String messageKey, Object... messageParams) {
        return addError(new Message(messageKey, messageParams));
    }

    public MessageContainer add(Message message) {
        messages.get(GLOBAL_MESSAGE).add(message);
        return this;
    }

    public MessageContainer add(String property, Message message) {
        if(!messages.containsKey(property)) {
            messages.put(property, new ArrayList());
        }
        messages.get(property).add(message);
        return this;
    }

    public MessageContainer addError(Message message) {
        messages.get(ERROR_MESSAGE).add(message);
        return this;
    }

    public List<Message> getMessages() {
        return messages.keySet().stream()
                .map(k -> messages.get(k))
                .collect(ArrayList::new, List::addAll, List::addAll);
    }

    public List<Message> getErrorMessages() {
        return messages.get(ERROR_MESSAGE);
    }

    public List<Message> getGlobalMessages() {
        return messages.get(GLOBAL_MESSAGE);
    }

    public List<Message> getMessages(String key) {
        return messages.containsKey(key) ? messages.get(key) : new ArrayList();
    }

    public int errorSize() {
        return messages.get(ERROR_MESSAGE).size();
    }

    public int size() {
        return messages.keySet().stream()
                .map(k -> messages.get(k).size())
                .reduce(0, Integer::sum);
    }
}
