package com.steves165.offersapi.api;

import com.steves165.offersapi.entities.Offer;
import com.steves165.offersapi.factory.ObjectMapperFactory;
import com.steves165.offersapi.services.DataStoreService;
import com.steves165.offersapi.services.DataStoreServiceImpl;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OffersApiResourceTest {

    private DataStoreService dataStoreService;

    private OffersApiResource offersApiResource;

    private AsyncResponse asyncResponse;

    @BeforeEach
    public void beforeEach() throws IOException {
        dataStoreService = new DataStoreServiceImpl(new ObjectMapperFactory().provide());
        offersApiResource = new OffersApiResource(dataStoreService);
        asyncResponse = mock(AsyncResponse.class);
    }

    @Test
    void testGetAllOffersValidOfferOnly() {
        // When
        offersApiResource.getAllOffers(asyncResponse, false);

        // Then
        verify(asyncResponse, times(1)).resume(dataStoreService.getOffers().entrySet().stream()
                .filter(entry -> entry.getValue().isActive() && entry.getValue().getExpiry().after(new Date()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Test
    void testGetAllOffers() {
        // When
        offersApiResource.getAllOffers(asyncResponse, true);

        // Then
        verify(asyncResponse, times(1)).resume(dataStoreService.getOffers());
    }

    @Test
    void testGetOffer() {
        // When
        offersApiResource.getOffer(asyncResponse, BigInteger.valueOf(3L));

        // Then
        verify(asyncResponse, times(1)).resume(dataStoreService.getOfferById(BigInteger.valueOf(3L)));
    }

    @Test
    void testGetOfferExpired() {
        // Given
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.getOffer(asyncResponse, BigInteger.ONE);

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals("EXPIRED", argument.getValue().getMessage());
        assertEquals(404, argument.getValue().getResponse().getStatus());
    }

    @Test
    void testGetOfferCancelled() {
        // Given
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.getOffer(asyncResponse, BigInteger.TWO);

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals("CANCELLED", argument.getValue().getMessage());
        assertEquals(404, argument.getValue().getResponse().getStatus());
    }

    @Test
    void testGetOfferNotFound() {
        // Given
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.getOffer(asyncResponse, BigInteger.valueOf(6000L));

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals("NOT FOUND", argument.getValue().getMessage());
        assertEquals(404, argument.getValue().getResponse().getStatus());
    }

    @Test
    void testGetOfferNullId() {
        // Given
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.getOffer(asyncResponse, null);

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals("NOT FOUND", argument.getValue().getMessage());
        assertEquals(404, argument.getValue().getResponse().getStatus());
    }

    @Test
    void testCreateOffer() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("Test create offer")
                .setActive(true)
                .setPrice(new BigDecimal(2134))
                .setCurrency("GBP")
                .setExpiry(new Date("04/02/2050"));
        ArgumentCaptor<OutboundJaxrsResponse> argument = ArgumentCaptor.forClass(OutboundJaxrsResponse.class);

        // When
        offersApiResource.createOffer(asyncResponse, offer);

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals(CREATED.getStatusCode(), argument.getValue().getStatusInfo().getStatusCode());
        assertEquals("CREATED OFFER 5", argument.getValue().getEntity());
    }

    @Test
    void testCreateOfferInvalidDescription() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("")
                .setActive(true)
                .setPrice(new BigDecimal(2134))
                .setCurrency("GBP")
                .setExpiry(new Date("04/02/2050"));
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.createOffer(asyncResponse, offer);

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), argument.getValue().getResponse().getStatus());
        assertEquals("OFFER WAS NOT CREATED DUE TO ERROR", argument.getValue().getMessage());
    }

    @Test
    void testCreateOfferInvalidCurrency() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("test")
                .setActive(true)
                .setPrice(new BigDecimal(2134))
                .setCurrency("")
                .setExpiry(new Date("04/02/2050"));
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.createOffer(asyncResponse, offer);

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), argument.getValue().getResponse().getStatus());
        assertEquals("OFFER WAS NOT CREATED DUE TO ERROR", argument.getValue().getMessage());
    }

    @Test
    void testCreateOfferNoDate() {
        // Given
        Offer offer = Offer.newInstance()
                .setDescription("test")
                .setActive(true)
                .setPrice(new BigDecimal(2134))
                .setCurrency("GBP")
                .setExpiry(null);
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.createOffer(asyncResponse, offer);

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), argument.getValue().getResponse().getStatus());
        assertEquals("OFFER WAS NOT CREATED DUE TO ERROR", argument.getValue().getMessage());
    }

    @Test
    void testCreateOfferNull() {
        // Given
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.createOffer(asyncResponse, null);

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), argument.getValue().getResponse().getStatus());
        assertEquals("OFFER WAS NOT CREATED DUE TO ERROR", argument.getValue().getMessage());
    }

    @Test
    void testCancelOffer() {
        // When
        offersApiResource.cancelOffer(asyncResponse, BigInteger.valueOf(3L));

        // Then
        verify(asyncResponse, times(1)).resume("CANCELLED OFFER 3");
    }

    @Test
    void testCancelOfferNotFound() {
        // Given
        ArgumentCaptor<WebApplicationException> argument = ArgumentCaptor.forClass(WebApplicationException.class);

        // When
        offersApiResource.cancelOffer(asyncResponse, BigInteger.valueOf(600L));

        // Then
        verify(asyncResponse, times(1)).resume(argument.capture());
        assertEquals(NOT_FOUND.getStatusCode(), argument.getValue().getResponse().getStatus());
        assertEquals("NOT FOUND", argument.getValue().getMessage());
    }
}