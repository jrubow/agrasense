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


    // Getters and setters.
    public long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(long subscriptionId) { this.subscriptionId = subscriptionId; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public SubscriptionCatalog getTier() { return tier; }
    public void setTier(SubscriptionCatalog tier) { this.tier = tier; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPrimaryAddress() { return primaryAddress; }
    public void setPrimaryAddress(String primaryAddress) { this.primaryAddress = primaryAddress; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public int getBillingCyclesRemaining() { return billingCyclesRemaining; }
    public void setBillingCyclesRemaining(int billingCyclesRemaining) { this.billingCyclesRemaining = billingCyclesRemaining; }

    public float getTotalCharged() { return totalCharged; }
    public void setTotalCharged(float totalCharged) { this.totalCharged = totalCharged; }

    public List<String> getSentinelId() { return sentinelId; }
    public void setSentinelId(List<String> sentinelId) { this.sentinelId = sentinelId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
