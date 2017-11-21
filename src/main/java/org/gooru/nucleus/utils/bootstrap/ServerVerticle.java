package org.gooru.nucleus.utils.bootstrap;

import java.util.ArrayList;
import java.util.List;

import org.gooru.nucleus.utils.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.utils.bootstrap.shutdown.Finalizers;
import org.gooru.nucleus.utils.bootstrap.startup.Initializer;
import org.gooru.nucleus.utils.bootstrap.startup.Initializers;
import org.gooru.nucleus.utils.constants.ConfigConstants;
import org.gooru.nucleus.utils.routes.RouteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;

public class ServerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        LOG.info("Starting ServerVerticle...");

        List<Future> applicationStartupStatus = new ArrayList<>();

        applicationStartupStatus.add(deployVerticles());
        applicationStartupStatus.add(startApplication());
        applicationStartupStatus.add(initializeHttpMachinery());

        CompositeFuture.all(applicationStartupStatus).setHandler(ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
                Runtime.getRuntime().halt(1);
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        Future<Void> result = shutDownApplication();
        result.setHandler(ar -> {
            if (ar.succeeded()) {
                stopFuture.complete();
            } else {
                stopFuture.fail(ar.cause());
            }
        });
    }

    private Future initializeHttpMachinery() {
        Future outcome = Future.future();
        final HttpServer httpServer = vertx.createHttpServer();
        final Router router = Router.router(vertx);
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
                outcome.complete();
            } else {
                // Can't do much here, Need to Abort. However, trying to exit may have
                // us blocked on other threads that we may have spawned, so we need to use brute force here
                LOG.error("Not able to start HTTP Server", result.cause());
                outcome.fail(result.cause());
                Runtime.getRuntime().halt(1);
            }
        });
        return outcome;
    }

    private Future startApplication() {
        Future result = Future.future();
        vertx.executeBlocking(future -> {
            Initializers initializers = new Initializers();
            try {
                for (Initializer initializer : initializers) {
                    initializer.initializeComponent(vertx, config());
                }
                future.complete();
            } catch (IllegalStateException ie) {
                LOG.error("Error initializing application", ie);
                future.fail(ie);
                Runtime.getRuntime().halt(1);
            }
        }, ar -> {
            if (ar.succeeded()) {
                result.complete();
            } else {
                result.fail(ar.cause());
            }
        });
        return result;
    }

    private Future<Void> shutDownApplication() {
        Future result = Future.future();
        vertx.executeBlocking(future -> {
            Finalizers finalizers = new Finalizers();
            for (Finalizer finalizer : finalizers) {
                finalizer.finalizeComponent();
            }
            future.complete();
        }, ar -> {
            if (ar.succeeded()) {
                result.complete();
            } else {
                result.fail(ar.cause());
            }
        });
        return result;
    }

    private Future deployVerticles() {
        Future result = Future.future();
        List<Future> deploymentResult = new ArrayList<>();
        final JsonArray verticlesList = config().getJsonArray(ConfigConstants.VERTICLES_DEPLOY_LIST);
        DeploymentOptions options = new DeploymentOptions().setConfig(config());
        verticlesList.forEach(verticle -> {
            final String verticleName = verticle.toString();
            Future deploymentResultItem = Future.future();
            deploymentResult.add(deploymentResultItem);
            vertx.deployVerticle(verticleName, options, res -> {
                LOG.debug("Starting verticle: {}", verticleName);
                if (res.succeeded()) {
                    LOG.info("Deploying :  " + verticleName + res.result());
                    deploymentResultItem.complete();
                } else {
                    LOG.info("Deployment of " + verticleName + " failed !");
                    deploymentResultItem.fail(res.cause());
                }
            });
        });
        CompositeFuture.all(deploymentResult).setHandler(ar -> {
            if (ar.succeeded()) {
                result.complete();
            } else {
                result.fail(ar.cause());
            }
        });
        return result;
    }
}
