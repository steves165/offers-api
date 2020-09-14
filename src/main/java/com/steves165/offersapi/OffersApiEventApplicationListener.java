package com.steves165.offersapi;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.inject.Inject;

/**
 * The application listener
 *
 */
public class OffersApiEventApplicationListener implements ApplicationEventListener {

    private final ServiceLocator locator;

    /**
     * Constructor
     *
     * @param locator the service locator
     */
    @Inject
    public OffersApiEventApplicationListener(ServiceLocator locator) {
        this.locator = locator;
    }

    @Override
    public void onEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent.getType() == ApplicationEvent.Type.INITIALIZATION_FINISHED) {
            Environment environment = locator.getService(Environment.class);
            locator.getAllServices(Task.class).forEach(environment.admin()::addTask);
            locator.getAllServices(HealthCheck.class).forEach(hc ->
                    environment.healthChecks().register(hc.getClass().getName(), hc));
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return event -> {};
    }
}
