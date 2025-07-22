package com.asterlink.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 *
 *
 *
 */

@Entity
@Table(name = "subscription_catalog")
public class SubscriptionCatalog {

    @Id
    @Column(name = "tier_id")
    private int tierId;

    @Column(name = "name", length = 32, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 128, nullable = false)
    private String description;

    @Column(name = "devices_limit", nullable = false)
    private int devicesLimit;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "renewal_period_days", nullable = false)
    private int renewalPeriodDays;

    // Default constructor
    public SubscriptionCatalog() {}

    // Getters and Setters
    public int getTierId() { return tierId; }
    public void setTierId(int tierId) { this.tierId = tierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDevicesLimit() { return devicesLimit; }
    public void setDevicesLimit(int devicesLimit) { this.devicesLimit = devicesLimit; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public int getRenewalPeriodDays() { return renewalPeriodDays; }
    public void setRenewalPeriodDays(int renewalPeriodDays) { this.renewalPeriodDays = renewalPeriodDays; }
}
