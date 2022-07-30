package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.messages.MessageContainer;

public interface MessageService {

    MessageContainer getErrorMessage(String messageReason, String... params);
    MessageContainer getNormalMessage(String messageReason, String... params);
}
