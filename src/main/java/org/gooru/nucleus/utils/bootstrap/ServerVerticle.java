package org.gooru.nucleus.utils.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;

import org.gooru.nucleus.utils.constants.ConfigConstants;
import org.gooru.nucleus.utils.routes.RouteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

    @Override
    public void start() throws Exception {

        LOG.info("Starting ServerVerticle...");
        final HttpServer httpServer = vertx.createHttpServer();
        final Router router = Router.router(vertx);
        deployVerticles();

        // Register the routes
        RouteConfiguration rc = new RouteConfiguration();
        rc.forEach(configurator -> configurator.configureRoutes(vertx, router, config()));

        // If the port is not present in configuration then we end up
        // throwing as we are casting it to int. This is what we want.
        final int port = config().getInteger(ConfigConstants.HTTP_PORT);
        LOG.info("Http server starting on port {}", port);
        httpServer.requestHandler(router::accept).listen(port, result -> {
            if (result.succeeded()) {
                LOG.info("HTTP Server started successfully");
            } else {
                // Can't do much here, Need to Abort. However, trying to exit
                // may have
                // us blocked on other threads that we may have spawned, so we
                // need to
                // use
                // brute force here
            LOG.error("Not able to start HTTP Server", result.cause());
            Runtime.getRuntime().halt(1);
        }
    }   );
    }

    private void deployVerticles() {
        LOG.debug("Starting to deploy other verticles...");
        final JsonArray verticlesList = config().getJsonArray(ConfigConstants.VERTICLES_DEPLOY_LIST);
        DeploymentOptions options = new DeploymentOptions().setConfig(config());
        verticlesList.forEach(verticle -> {
            final String verticleName = verticle.toString();
            vertx.deployVerticle(verticleName, options, res -> {
                LOG.debug("Starting verticle: {}", verticleName);
                if (res.succeeded()) {
                    LOG.info("Deploying :  " + verticleName + res.result());
                } else {
                    LOG.info("Deployment of " + verticleName + " failed !");
                }
            });
        });
    }
}
