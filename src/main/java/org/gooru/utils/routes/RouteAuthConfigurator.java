package org.gooru.utils.routes;

import io.netty.handler.codec.http.HttpMethod;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import org.gooru.utils.constants.*;
import org.gooru.utils.responses.auth.AuthResponseContextHolder;
import org.gooru.utils.responses.auth.AuthResponseContextHolderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteAuthConfigurator implements RouteConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger("org.gooru.utils.bootstrap.ServerVerticle");

  private long mbusTimeout;
  private EventBus eBus = null;

  @Override
  public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
    eBus = vertx.eventBus();
    mbusTimeout = config.getLong(ConfigConstants.MBUS_TIMEOUT, RouteConstants.DEFAULT_TIMEOUT);
    router.route(RouteConstants.API_CONVERTER_AUTH_ROUTE).handler(this::validateAccessToken);
  }

  private void validateAccessToken(RoutingContext routingContext) {
    HttpServerRequest request = routingContext.request();
    HttpServerResponse response = routingContext.response();

    String authorization = request.getHeader(HttpConstants.HEADER_AUTH);
    String token = null;
    if (authorization != null && authorization.startsWith(HttpConstants.HEADER_TOKEN)) {
      token = authorization.substring(HttpConstants.HEADER_TOKEN.length()).trim();
    }
    if (token == null) {
      response.setStatusCode(HttpConstants.HttpStatus.UNAUTHORIZED.getCode()).setStatusMessage(HttpConstants.HttpStatus.UNAUTHORIZED.getMessage())
          .end();
    } else {
      DeliveryOptions options = new DeliveryOptions().setSendTimeout(mbusTimeout).addHeader(MessageConstants.MSG_HEADER_TOKEN, token);
      eBus.send(
          MessagebusEndpoints.MBEP_AUTH,
          null,
          options,
          reply -> {
            if (reply.succeeded()) {
              AuthResponseContextHolder responseHolder = new AuthResponseContextHolderBuilder(reply.result()).build();

              if (responseHolder.isAuthorized()) {
                if ((!request.method().name().equals(HttpMethod.GET.name()) && responseHolder.isAnonymous())) {
                  routingContext.response().setStatusCode(HttpConstants.HttpStatus.FORBIDDEN.getCode())
                      .setStatusMessage(HttpConstants.HttpStatus.FORBIDDEN.getMessage()).end();
                } else {
                  routingContext.put(MessageConstants.MSG_USER_CONTEXT_HOLDER, responseHolder.getUserContext());
                  routingContext.next();
                }
              } else {
                routingContext.response().setStatusCode(HttpConstants.HttpStatus.UNAUTHORIZED.getCode())
                    .setStatusMessage(HttpConstants.HttpStatus.UNAUTHORIZED.getMessage()).end();
              }
            } else {
              LOG.error("Not able to send message", reply.cause());
              routingContext.response().setStatusCode(HttpConstants.HttpStatus.ERROR.getCode()).end();
            }
          });
    }

  }
}
