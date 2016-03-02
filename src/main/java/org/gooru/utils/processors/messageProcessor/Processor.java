package org.gooru.utils.processors.messageProcessor;

import org.gooru.utils.processors.command.executor.MessageResponse;

public interface Processor {
  MessageResponse process();
}
