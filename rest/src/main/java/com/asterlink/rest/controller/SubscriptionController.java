package com.asterlink.rest.controller;

import com.asterlink.rest.model.SubscriptionCatalog;
import com.asterlink.rest.service.SubscriptionCatalogService;
import com.asterlink.rest.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Combined class for all subscription-related routes, including subscription catalogues.
 *
 * @author gl3bert
 */

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    // Service access.
    private final SubscriptionCatalogService subscriptionCatalogService;
    private final SubscriptionService subscriptionService;
    public SubscriptionController(SubscriptionCatalogService subscriptionCatalogService, SubscriptionService subscriptionService) {
        this.subscriptionCatalogService = subscriptionCatalogService;
        this.subscriptionService = subscriptionService;
    }

    // API routes.

    // Retrieve all tiers for the users to see.
    @GetMapping("/tiers/all")
    public ResponseEntity<List<SubscriptionCatalog>> getAllTiers() {
        List tiers = subscriptionCatalogService.pullAllTierDetails();
        if (tiers == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subscriptionCatalogService.pullAllTierDetails());
    }

    // Retrieve specific tier information
    @GetMapping("/tiers/{tierId}")
    public ResponseEntity<SubscriptionCatalog> getTierById(@PathVariable int tierId) {
        SubscriptionCatalog tier = subscriptionCatalogService.pullDetails(tierId);
        if (tier == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tier);
    }

    // Create new tier.
    @PostMapping("/tiers")
    public ResponseEntity<String> createTier(@RequestBody SubscriptionCatalog tier) {
        return subscriptionCatalogService.createNewSubscriptionCatalogEntry(tier) == 0 ?
                ResponseEntity.ok("Tier created.") :
                ResponseEntity.badRequest().body("Tier already exists.");
    }

    // Delete existing tier.
    @DeleteMapping("/tiers/{tierId}")
    public ResponseEntity<String> deleteTier(@PathVariable int tierId) {
        return subscriptionCatalogService.deleteSubscriptionCatalogEntry(tierId) == 0 ?
                ResponseEntity.ok("Tier deleted.") :
                ResponseEntity.badRequest().body("Tier not found.");
    }

    // Update existing tier.
    @PutMapping("/tiers/{tierId}")
    public ResponseEntity<SubscriptionCatalog> updateTier(@PathVariable int tierId, @RequestBody SubscriptionCatalog tier) {
        SubscriptionCatalog updated = subscriptionCatalogService.editDetails(tierId, tier);
        return ResponseEntity.ok(updated);
    }
}
