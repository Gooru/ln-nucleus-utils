package org.gooru.nucleus.utils.processors.messageProcessor;

import org.gooru.nucleus.utils.processors.command.executor.MessageResponse;

public interface Processor {
  MessageResponse process();
}
