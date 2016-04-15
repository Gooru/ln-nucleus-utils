package org.gooru.nucleus.utils.processors.command.executor.email;

import org.gooru.nucleus.utils.processors.command.executor.Executor;

public final class EmailExecutorFactory {

    public static Executor SendEmailExecutor() {
        return SendEmailExecutor.getInstance();
    }

    private EmailExecutorFactory() {
        throw new AssertionError();
    }

}
