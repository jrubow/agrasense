package com.asterlink.rest.service;

import com.asterlink.rest.model.SubscriptionCatalog;

import java.util.List;

/**
 *
 *
 *
 */

public interface SubscriptionCatalogService {

    // Create new entry.
    int createNewSubscriptionCatalogEntry(SubscriptionCatalog subscriptionCatalog);


    // Delete entry by id.
    int deleteSubscriptionCatalogEntry(int tierId);

    // Pull details for a specific tier id.
    SubscriptionCatalog pullDetails(int tierId);

    // Pull details for all current tiers.
    List<SubscriptionCatalog> pullAllTierDetails();

    // Edit details for a specific tier id.
    SubscriptionCatalog editDetails(int tierId, SubscriptionCatalog updatedData);
}
