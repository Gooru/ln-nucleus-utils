package org.gooru.utils.routes;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import org.gooru.utils.constants.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteFailureConfigurator implements RouteConfigurator {

  private static final Logger LOGGER = LoggerFactory.getLogger(RouteFailureConfigurator.class);

  @Override
  public void configureRoutes(Vertx vertx, Router router, JsonObject config) {

    router.put().failureHandler(frc -> handleFailures(frc));

    router.put().failureHandler(frc -> handleFailures(frc));
  }

  private void handleFailures(RoutingContext frc) {
    Throwable currentThrowable = frc.failure();
    if (currentThrowable instanceof io.vertx.core.json.DecodeException) {
      LOGGER.error("Caught registered exception", currentThrowable);
      frc.response().setStatusCode(HttpConstants.HttpStatus.BAD_REQUEST.getCode()).end("Invalid Json payload");
    } else {
      LOGGER.error("Caught unregistered exception, will send HTTP.500", currentThrowable);
      frc.response().setStatusCode(HttpConstants.HttpStatus.ERROR.getCode()).end("Internal error");
    }
  }

}
