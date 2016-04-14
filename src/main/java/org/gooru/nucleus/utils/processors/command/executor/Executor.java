package org.gooru.nucleus.utils.processors.command.executor;

import org.gooru.nucleus.utils.processors.messageProcessor.MessageContext;

public interface Executor {
    MessageResponse execute(MessageContext messageContext);
}
