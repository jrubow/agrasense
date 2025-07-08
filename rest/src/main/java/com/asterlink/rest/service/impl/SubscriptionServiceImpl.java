package com.asterlink.rest.service.impl;

import com.asterlink.rest.repository.SubscriptionRepository;
import com.asterlink.rest.service.SubscriptionService;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 */

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    // Repository access.
    private SubscriptionRepository subscriptionCatalogRepository;
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionCatalogRepository) {
        this.subscriptionCatalogRepository = subscriptionCatalogRepository;
    }

    // Method implementations.
}
