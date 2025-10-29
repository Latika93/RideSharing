package com.RideSharing.RideSharing.controller;

import com.RideSharing.RideSharing.entity.LocationUpdateDTO;
import com.RideSharing.RideSharing.service.LocationTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class LocationWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LocationTrackingService locationTrackingService;

    // Store active subscriptions for trips
    private final Map<String, String> tripSubscriptions = new ConcurrentHashMap<>();

    /**
     * Handle location updates from drivers
     * Endpoint: /app/location.update
     */
    @MessageMapping("/location.update")
    public void handleLocationUpdate(@Payload LocationUpdateDTO locationUpdate) {
        try {
            // Validate the location update
            if (locationUpdate.getDriverId() == null || locationUpdate.getTripId() == null ||
                locationUpdate.getLatitude() == null || locationUpdate.getLongitude() == null) {
                return;
            }

            // Store the location update
            locationTrackingService.storeLocationUpdate(locationUpdate.toLocationUpdate());

            // Broadcast to all subscribers of this trip
            String topic = "/topic/trip/" + locationUpdate.getTripId() + "/location";
            messagingTemplate.convertAndSend(topic, locationUpdate);

            // Also broadcast to driver-specific topic for driver status updates
            String driverTopic = "/topic/driver/" + locationUpdate.getDriverId() + "/status";
            messagingTemplate.convertAndSend(driverTopic, locationUpdate);

        } catch (Exception e) {
            System.err.println("Error handling location update: " + e.getMessage());
        }
    }

    /**
     * Handle driver status updates (arrived, started trip, etc.)
     * Endpoint: /app/driver.status
     */
    @MessageMapping("/driver.status")
    public void handleDriverStatus(@Payload Map<String, Object> statusUpdate) {
        try {
            String tripId = (String) statusUpdate.get("tripId");
            String driverId = (String) statusUpdate.get("driverId");
            String status = (String) statusUpdate.get("status");

            if (tripId != null && driverId != null && status != null) {
                // Broadcast status update to trip subscribers
                String topic = "/topic/trip/" + tripId + "/status";
                messagingTemplate.convertAndSend(topic, statusUpdate);
            }
        } catch (Exception e) {
            System.err.println("Error handling driver status update: " + e.getMessage());
        }
    }

    /**
     * Handle trip subscription requests
     * Endpoint: /app/trip.subscribe
     */
    @MessageMapping("/trip.subscribe")
    public void handleTripSubscription(@Payload Map<String, String> subscription) {
        try {
            String tripId = subscription.get("tripId");
            String userId = subscription.get("userId");
            String userType = subscription.get("userType"); // "rider" or "driver"

            if (tripId != null && userId != null) {
                // Store the subscription
                String subscriptionKey = tripId + ":" + userId;
                tripSubscriptions.put(subscriptionKey, userType);

                // Send confirmation
                Map<String, Object> response = Map.of(
                    "type", "SUBSCRIPTION_CONFIRMED",
                    "tripId", tripId,
                    "userId", userId,
                    "userType", userType
                );

                String userTopic = "/queue/user/" + userId;
                messagingTemplate.convertAndSend(userTopic, response);
            }
        } catch (Exception e) {
            System.err.println("Error handling trip subscription: " + e.getMessage());
        }
    }

    /**
     * Handle trip unsubscription requests
     * Endpoint: /app/trip.unsubscribe
     */
    @MessageMapping("/trip.unsubscribe")
    public void handleTripUnsubscription(@Payload Map<String, String> unsubscription) {
        try {
            String tripId = unsubscription.get("tripId");
            String userId = unsubscription.get("userId");

            if (tripId != null && userId != null) {
                String subscriptionKey = tripId + ":" + userId;
                tripSubscriptions.remove(subscriptionKey);

                // Send confirmation
                Map<String, Object> response = Map.of(
                    "type", "UNSUBSCRIPTION_CONFIRMED",
                    "tripId", tripId,
                    "userId", userId
                );

                String userTopic = "/queue/user/" + userId;
                messagingTemplate.convertAndSend(userTopic, response);
            }
        } catch (Exception e) {
            System.err.println("Error handling trip unsubscription: " + e.getMessage());
        }
    }

    /**
     * Get active subscriptions (for debugging)
     */
    public Map<String, String> getActiveSubscriptions() {
        return new ConcurrentHashMap<>(tripSubscriptions);
    }
}

