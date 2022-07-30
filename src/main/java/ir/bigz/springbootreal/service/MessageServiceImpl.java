package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.messages.MessageContainer;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {



    @Override
    public MessageContainer getErrorMessage(String messageReason, String... params) {
        MessageContainer messageContainer = MessageContainer.create();
        messageContainer.addError(messageReason, params);
        return messageContainer;
    }

    @Override
    public MessageContainer getNormalMessage(String messageReason, String... params) {
        MessageContainer messageContainer = MessageContainer.create();
        messageContainer.add(messageReason, params);
        return messageContainer;
    }
}
