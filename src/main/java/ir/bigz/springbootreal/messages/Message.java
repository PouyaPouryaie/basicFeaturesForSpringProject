package ir.bigz.springbootreal.messages;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Message {

    private final String messageKey;
    private final Object[] messageParams;

    public Message(String messageKey, Object... messageParams) {
        this.messageKey = messageKey;
        this.messageParams = messageParams;
    }
}
