package org.gooru.nucleus.utils.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

import org.gooru.nucleus.utils.constants.MessagebusEndpoints;
import org.gooru.nucleus.utils.processors.ProcessorBuilder;
import org.gooru.nucleus.utils.processors.command.executor.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(EmailVerticle.class);

    @Override
    public void start(Future<Void> voidFuture) throws Exception {
        EventBus eb = vertx.eventBus();
        eb.consumer(MessagebusEndpoints.MBEP_EMAIL, message -> {
            LOG.debug("Received message: " + message.body());
            vertx.executeBlocking(future -> {
                MessageResponse result = new ProcessorBuilder(message).build().process();
                future.complete(result);
            }, res -> {
                MessageResponse result = (MessageResponse) res.result();
                message.reply(result.reply(), result.deliveryOptions());
            });

        }).completionHandler(result -> {
            if (result.succeeded()) {
                LOG.info("Email end point ready to listen");
            } else {
                LOG.error("Error registering the email handler. Halting the user machinery");
                Runtime.getRuntime().halt(1);
            }
        });
    }

}
