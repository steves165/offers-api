package com.steves165.offersapi;

import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.ComponentProvider;

import java.io.IOException;
import java.util.Set;

public class OffersApiComponentProvider implements ComponentProvider {

    @Override
    public void initialize(InjectionManager injectionManager) {
        ServiceLocator locator = injectionManager.getInstance(ServiceLocator.class);
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);

        try {
            dcs.getPopulator().populate();
        } catch (IOException e) {
            throw new MultiException(e);
        }
    }

    @Override
    public boolean bind(Class<?> component, Set<Class<?>> providerContracts) {
        return false;
    }

    @Override
    public void done() {
    }
}
