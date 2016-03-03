package org.gooru.utils.processors.command.executor;

import org.gooru.utils.processors.messageProcessor.MessageContext;


public interface Executor {
  MessageResponse execute(MessageContext messageContext);
}
