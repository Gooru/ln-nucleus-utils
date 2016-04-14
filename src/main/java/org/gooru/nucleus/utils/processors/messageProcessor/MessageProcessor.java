package org.gooru.nucleus.utils.processors.messageProcessor;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import org.gooru.nucleus.utils.constants.CommandConstants;
import org.gooru.nucleus.utils.processors.command.executor.MessageResponse;
import org.gooru.nucleus.utils.processors.command.executor.email.EmailExecutorFactory;
import org.gooru.nucleus.utils.processors.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final Message<Object> message;

    public MessageProcessor(Message<Object> message) {
        this.message = message;
    }

    @Override
    public MessageResponse process() {
        MessageResponse result = null;
        try {
            if (message == null || !(message.body() instanceof JsonObject)) {
                LOG.error("Invalid message received, either null or body of message is not JsonObject ");
                throw new InvalidRequestException();
            }
            MessageContext messageContext = new MessageContextHolder(message);
            switch (messageContext.command()) {
            case CommandConstants.SEND_EMAIL:
                result = EmailExecutorFactory.SendEmailExecutor().execute(messageContext);
                break;
            default:
                LOG.error("Invalid command type passed in, not able to handle");
                throw new InvalidRequestException();
            }
            return result;
        } catch (Throwable throwable) {
            LOG.warn("Caught unexpected exception here", throwable);
            return new MessageResponse.Builder().setThrowable(throwable).build();
        }
    }

}
