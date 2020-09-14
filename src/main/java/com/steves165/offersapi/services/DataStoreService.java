package com.steves165.offersapi.services;

import com.steves165.offersapi.entities.Offer;
import org.jvnet.hk2.annotations.Contract;

import java.math.BigInteger;
import java.util.Map;

@Contract
public interface DataStoreService {

    public Map<BigInteger, Offer> getOffers();

    public Offer getOfferById(BigInteger id);

    public void cancelOffer(BigInteger id) throws NullPointerException;

    public BigInteger createOffer(Offer offer);
}
