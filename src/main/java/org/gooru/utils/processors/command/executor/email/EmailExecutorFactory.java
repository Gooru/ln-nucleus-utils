package org.gooru.utils.processors.command.executor.email;

import org.gooru.utils.processors.command.executor.Executor;

public class EmailExecutorFactory {

  public static Executor SendEmailExecutor() {
    return SendEmailExecutor.getInstance();
  }

  private EmailExecutorFactory() {
    throw new AssertionError();
  }

}
