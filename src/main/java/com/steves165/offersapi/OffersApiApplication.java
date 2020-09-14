package com.steves165.offersapi;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class OffersApiApplication extends Application<OffersApiConfiguration> {

    public static void main(final String[] args) throws Exception {
        new OffersApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "OffersApi";
    }

    @Override
    public void initialize(final Bootstrap<OffersApiConfiguration> bootstrap) { }

    @Override
    public void run(final OffersApiConfiguration configuration,
                    final Environment environment) {
        // Register the controllers.
        environment.jersey().packages("com.steves165.offersapi");

        // add the config into jersey (HK2)
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(environment).to(Environment.class);
                bind(configuration).to(OffersApiConfiguration.class);
            }
        });

        // register the application listener
        environment.jersey().register(OffersApiEventApplicationListener.class);

        // suppress the logging, health checks are added when the app starts
        environment.healthChecks().register("empty", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
    }

}
