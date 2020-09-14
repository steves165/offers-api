package com.steves165.offersapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steves165.offersapi.entities.Offer;
import org.apache.commons.io.IOUtils;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.max;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class DataStoreServiceImpl implements DataStoreService {

    private Logger LOGGER = getLogger(DataStoreServiceImpl.class);

    private Map<BigInteger, Offer> dataStore;

    @Inject
    public DataStoreServiceImpl(ObjectMapper objectMapper) throws IOException {
        ClassLoader classLoader = DataStoreServiceImpl.class.getClassLoader();
        dataStore = objectMapper.readValue(
            IOUtils.toString(
                requireNonNull(
                    classLoader.getResourceAsStream("data-store/offers.json")), UTF_8),
                    new TypeReference<Map<BigInteger, Offer>>() {});
    }

    @Override
    public Map<BigInteger, Offer> getOffers() {
        return dataStore;
    }

    @Override
    public Offer getOfferById(BigInteger id) {
        return dataStore.get(id);
    }

    @Override
    public void cancelOffer(BigInteger id) throws NullPointerException {
        Offer offer = getOfferById(id);

        if (offer == null) {
            throw new NullPointerException();
        }

        offer.setActive(false);
    }

    @Override
    public BigInteger createOffer(Offer offer) {
        if (offer == null) {
            LOGGER.error("Offer was null and not saved to Data Store.");

            return null;
        }

        BigInteger highestKey = max(dataStore.keySet());
        BigInteger newKey = highestKey.add(BigInteger.ONE);

        Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<Offer>> validate = javaxValidator.validate(offer);

        if (validate.isEmpty()) {
            dataStore.put(newKey, offer);

            return newKey;
        } else {
            LOGGER.error("Offer was invalid and not saved to Data Store.");

            return null;
        }
    }
}
