package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.SubscriptionCatalog;
import com.asterlink.rest.repository.SubscriptionCatalogRepository;
import com.asterlink.rest.service.SubscriptionCatalogService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *
 *
 */

@Service
public class SubscriptionCatalogServiceImpl implements SubscriptionCatalogService {

    // Repository access.
    private SubscriptionCatalogRepository subscriptionCatalogRepository;

    public SubscriptionCatalogServiceImpl(SubscriptionCatalogRepository subscriptionCatalogRepository) {
        this.subscriptionCatalogRepository = subscriptionCatalogRepository;
    }

    // Method implementations.

    // Create new entry.
    @Override
    public int createNewSubscriptionCatalogEntry(SubscriptionCatalog subscriptionCatalog) {
        if (subscriptionCatalogRepository.existsById(subscriptionCatalog.getTierId())) {
            return 1; // Code for existing tier id.
        }
        subscriptionCatalogRepository.save(subscriptionCatalog);
        return 0;
    }

    // Delete entry by id.
    @Override
    public int deleteSubscriptionCatalogEntry(int tierId) {
        if (subscriptionCatalogRepository.existsById(tierId)) {
            subscriptionCatalogRepository.deleteById(tierId);
            return 0;
        }
        return 1; // Code for id not found.
    }

    // Pull details for a specific tier id.
    @Override
    public SubscriptionCatalog pullDetails(int tierId) {
        return subscriptionCatalogRepository.findById(tierId)
                .orElseThrow(() -> new EntityNotFoundException("Tier ID " + tierId + " not found."));
    }

    // Pull details for all current tiers.
    @Override
    public List<SubscriptionCatalog> pullAllTierDetails() {
        return subscriptionCatalogRepository.findAll();
    }

    // Edit details for a specific tier id.
    @Override
    public SubscriptionCatalog editDetails(int tierId, SubscriptionCatalog updatedData) {
        SubscriptionCatalog existing = subscriptionCatalogRepository.findById(tierId)
                .orElseThrow(() -> new EntityNotFoundException("Tier ID " + tierId + " not found."));

        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setDevicesLimit(updatedData.getDevicesLimit());
        existing.setPrice(updatedData.getPrice());
        existing.setTierId(tierId);
        return subscriptionCatalogRepository.save(existing);
    }
}