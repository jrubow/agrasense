package com.asterlink.rest.model;

import com.asterlink.rest.converter.StringListToJsonConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Subscription class for managing user subscriptions.
 *
 * @author gl3bert
 */

@Table(name="subscriptions")
@Entity
public class Subscription {

    // Identifying column. Unique subscription identifier.
    @Id
    @Column(name="subscription_id")
    private long subscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private SubscriptionCatalog tier;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "primary_address", length = 255)
    private String primaryAddress;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "billing_cycles_remaining")
    private int billingCyclesRemaining;

    @Column(name = "total_charged")
    private float totalCharged = 0;

    @Column(name = "sentinel_id", columnDefinition = "json")
    @Convert(converter = StringListToJsonConverter.class)
    private List<String> sentinelId;

    @Column(name = "active", nullable = false)
    private boolean active = false;

    // Default empty constructor.
    public Subscription() {};

    // Parameterized constructor.


}
