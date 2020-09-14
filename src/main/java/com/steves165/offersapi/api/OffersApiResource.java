package com.steves165.offersapi.api;

import com.steves165.offersapi.entities.Offer;
import com.steves165.offersapi.services.DataStoreService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/offers-api")
@Produces(APPLICATION_JSON)
public class OffersApiResource {

    private DataStoreService dataStoreService;

    @Inject
    public OffersApiResource(DataStoreService dataStoreService) {
        this.dataStoreService = dataStoreService;
    }

    @GET
    @Path("/offers")
    public void getAllOffers(@Suspended AsyncResponse asyncResponse,
                             @QueryParam("showNonValid") boolean showNonValid) {
        Map<BigInteger, Offer> offers = dataStoreService.getOffers();

        if (!showNonValid) {
            offers = offers.entrySet().stream()
                    .filter(entry -> entry.getValue().isActive() && entry.getValue().getExpiry().after(new Date()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        asyncResponse.resume(offers);
    }

    @GET
    @Path("/offers/{id}")
    public void getOffer(@Suspended AsyncResponse asyncResponse,
                         @PathParam("id") BigInteger id) {
        Offer offer = dataStoreService.getOfferById(id);

        if (offer == null) {
            asyncResponse.resume(new WebApplicationException("NOT FOUND", NOT_FOUND));
        } else if (!offer.isActive()) {
            asyncResponse.resume(new WebApplicationException("CANCELLED", NOT_FOUND));
        } else if (offer.getExpiry().before(new Date())) {
            asyncResponse.resume(new WebApplicationException("EXPIRED", NOT_FOUND));
        } else {
            asyncResponse.resume(offer);
        }
    }

    @POST
    @Path("/offers/create")
    @Consumes(APPLICATION_JSON)
    public void createOffer(@Suspended AsyncResponse asyncResponse,
                            @Valid @NotNull Offer offer) {
        BigInteger offerKey = dataStoreService.createOffer(offer);

        if (offerKey == null) {
            asyncResponse.resume(new WebApplicationException("OFFER WAS NOT CREATED DUE TO ERROR", INTERNAL_SERVER_ERROR));

            return;
        }

        asyncResponse.resume(Response.status(CREATED).entity(format("CREATED OFFER %s", offerKey.toString())).build());
    }

    @DELETE
    @Path("/offers/{id}")
    public void cancelOffer(@Suspended AsyncResponse asyncResponse,
                            @PathParam("id") BigInteger id) {
        try {
            dataStoreService.cancelOffer(id);
        } catch (NullPointerException e) {
            asyncResponse.resume(new WebApplicationException("NOT FOUND", NOT_FOUND));

            return;
        }

        asyncResponse.resume(format("CANCELLED OFFER %s", id));
    }
}
