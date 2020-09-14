package com.steves165.offersapi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

/**
 * Representation of an offer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Offer {

    @NotNull
    @NotEmpty
    private String description;

    private boolean active;

    @NotNull
    @NotEmpty
    @DefaultValue("GBP")
    private String currency;

    @NotNull
    private BigDecimal price;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date expiry;

    public Date getExpiry() {
        return expiry;
    }

    public Offer setExpiry(Date expiry) {
        this.expiry = expiry;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Offer setDescription(String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Offer setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public Offer setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public Offer setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public static Offer newInstance() {
        return new Offer();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Offer) {
            Offer b = (Offer) o;

            return description.equals(b.getDescription()) &&
                    currency.equals(b.getCurrency()) &&
                    price.equals(b.getPrice()) &&
                    expiry.equals(b.getExpiry()) &&
                    active == b.isActive();
        }

        return false;
    }
}
