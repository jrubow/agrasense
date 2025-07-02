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

    @Column(name = "total_billing_cycles", nullable = false)
    private int totalBillingCycles;

    // Default constructor
    public SubscriptionCatalog() {}
}
