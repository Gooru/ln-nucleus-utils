package org.gooru.nucleus.utils.bootstrap;

import java.sql.Timestamp;

import org.gooru.nucleus.utils.constants.MessageConstants;
import org.gooru.nucleus.utils.constants.MessagebusEndpoints;
import org.gooru.nucleus.utils.processors.command.executor.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class UserErrorVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(UserErrorVerticle.class);
    private static final Logger LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.utils.user.error");

    @Override
    public void start(Future<Void> voidFuture) throws Exception {
        EventBus eb = vertx.eventBus();
        eb.consumer(MessagebusEndpoints.MBEP_LOGGER, message -> {
            vertx.executeBlocking(future -> {
                //MessageResponse result = new ProcessorBuilder(message).build().process();
                JsonObject body = (JsonObject) message.body();
                JsonObject httpBody = body.getJsonObject(MessageConstants.MSG_HTTP_BODY);
                if (httpBody != null && !httpBody.isEmpty()) {
                    httpBody.put("server_timestamp", new Timestamp(System.currentTimeMillis()).toString());
                    LOGGER.info(httpBody.toString());
                } else {
                    LOG.debug("invalid http body received in message");
                }
                future.complete(new MessageResponse.Builder().setResponseBody(null).setStatusOkay().successful()
                    .build());
            }, res -> {
                MessageResponse result = (MessageResponse) res.result();
                message.reply(result.reply(), result.deliveryOptions());
            });

        }).completionHandler(result -> {
            if (result.succeeded()) {
                LOG.info("User Error end point ready to listen");
            } else {
                LOG.error("Error registering the user error handler. Halting the user machinery");
                Runtime.getRuntime().halt(1);
            }
        });
    }
}
