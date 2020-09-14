package com.steves165.offersapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steves165.offersapi.entities.Offer;
import com.steves165.offersapi.factory.ObjectMapperFactory;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataStoreServiceImplTest {

    private DataStoreService dataStoreService;

    private Map<BigInteger, Offer> dataStore;

    private ClassLoader classLoader = DataStoreServiceImplTest.class.getClassLoader();

    @BeforeEach
    public void beforeEach() throws IOException {
        ObjectMapper objectMapper = new ObjectMapperFactory().provide();
        dataStoreService = new DataStoreServiceImpl(objectMapper);
        dataStore = objectMapper.readValue(
                IOUtils.toString(
                        requireNonNull(
                                classLoader.getResourceAsStream("data-store/offers.json")), UTF_8),
                new TypeReference<Map<BigInteger, Offer>>() {});
    }

    @Test
    public void testGetOffers() {
        // When
        Map<BigInteger, Offer> offers = dataStoreService.getOffers();

        // Then
        assertEquals(dataStore, offers);
    }

    @Test
    public void testGetOffer() {
        // When
        Offer result = dataStoreService.getOfferById(BigInteger.ONE);

        // Then
        assertEquals(dataStore.get(BigInteger.ONE), result);
    }

    @Test
    public void testCancelOffer() {
        // When
        BigInteger id = BigInteger.valueOf(3L);
        assertTrue(dataStoreService.getOfferById(id).isActive());
        dataStoreService.cancelOffer(id);

        // Then
        assertFalse(dataStoreService.getOfferById(id).isActive());
    }

    @Test
    public void testCancelOfferNotFound() {
        // Then
        assertThrows(NullPointerException.class, () -> dataStoreService.cancelOffer(BigInteger.valueOf(400L)));
    }

    @Test
    public void testCreateOfferValid() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("Test")
                .setActive(true)
                .setCurrency("test")
                .setPrice(new BigDecimal(1200))
                .setExpiry(new Date());

        // When
        BigInteger id = dataStoreService.createOffer(offer);

        // Then
        assertEquals(offer, dataStoreService.getOfferById(id));
    }

    @Test
    public void testCreateOfferDescriptionInvalid() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("")
                .setActive(true)
                .setCurrency("test")
                .setPrice(new BigDecimal(1200))
                .setExpiry(new Date());

        // When
        BigInteger id = dataStoreService.createOffer(offer);

        // Then
        assertNull(id);
    }

    @Test
    public void testCreateOfferCurrencyInvalid() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("test")
                .setActive(true)
                .setCurrency("")
                .setPrice(new BigDecimal(1200))
                .setExpiry(new Date());

        // When
        BigInteger id = dataStoreService.createOffer(offer);

        // Then
        assertNull(id);
    }

    @Test
    public void testCreateOfferPriceInvalid() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("test")
                .setActive(true)
                .setCurrency("")
                .setPrice(null)
                .setExpiry(new Date());

        // When
        BigInteger id = dataStoreService.createOffer(offer);

        // Then
        assertNull(id);
    }

    @Test
    public void testCreateOfferExpiryInvalid() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("test")
                .setActive(true)
                .setCurrency("")
                .setPrice(new BigDecimal(1200))
                .setExpiry(null);

        // When
        BigInteger id = dataStoreService.createOffer(offer);

        // Then
        assertNull(id);
    }
}