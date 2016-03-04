package org.gooru.nucleus.utils.routes;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import org.gooru.nucleus.utils.constants.CommandConstants;
import org.gooru.nucleus.utils.constants.ConfigConstants;
import org.gooru.nucleus.utils.constants.MessageConstants;
import org.gooru.nucleus.utils.constants.MessagebusEndpoints;
import org.gooru.nucleus.utils.constants.RouteConstants;
import org.gooru.nucleus.utils.routes.utils.RouteRequestUtility;
import org.gooru.nucleus.utils.routes.utils.RouteResponseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RouteEmailConfigurator implements RouteConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger("org.gooru.nucleus.utils.bootstrap.ServerVerticle");

  private EventBus eb = null;
  private long mbusTimeout;

  @Override
  public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
    eb = vertx.eventBus();
    mbusTimeout = config.getLong(ConfigConstants.MBUS_TIMEOUT, RouteConstants.DEFAULT_TIMEOUT);
    router.post(RouteConstants.EP_NUCLEUS_UTILS_EMAIL).handler(this::sendEmail);
  }

  private void sendEmail(RoutingContext routingContext) {
    final DeliveryOptions options =
      new DeliveryOptions().setSendTimeout(mbusTimeout).addHeader(MessageConstants.MSG_HEADER_OP, CommandConstants.SEND_EMAIL);
    eb.send(MessagebusEndpoints.MBEP_EMAIL, RouteRequestUtility.getBodyForMessage(routingContext), options,
      reply -> RouteResponseUtility.responseHandler(routingContext, reply, LOG));
  }

}
