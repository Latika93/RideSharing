# Live Location Tracking Integration Guide

This guide provides step-by-step instructions for integrating the live location tracking functionality into your frontend application.

## Overview

The live location tracking system uses WebSockets for real-time communication between drivers and riders. Drivers send location updates at regular intervals, and riders can subscribe to receive these updates in real-time.

## Backend Components Implemented

### 1. WebSocket Configuration

- **File**: `WebSocketConfig.java`
- **Endpoint**: `/ws` (with SockJS fallback)
- **Message Broker**: `/topic` and `/queue` prefixes
- **Application Prefix**: `/app`

### 2. WebSocket Controller

- **File**: `LocationWebSocketController.java`
- **Endpoints**:
  - `/app/location.update` - Driver location updates
  - `/app/driver.status` - Driver status updates
  - `/app/trip.subscribe` - Subscribe to trip updates
  - `/app/trip.unsubscribe` - Unsubscribe from trip updates

### 3. REST API Endpoints

#### Driver Endpoints

- `POST /driver/{id}/location` - Update driver location
- `GET /driver/{id}/location` - Get current driver location
- `GET /driver/{id}/location/history` - Get driver location history

#### Rider Endpoints

- `GET /rider/{id}/driver/{driverId}/location` - Get driver location for rider
- `GET /rider/{id}/drivers/nearby` - Get nearby drivers

## Frontend Integration Steps

### Step 1: Install WebSocket Client Library

For JavaScript/TypeScript frontend:

```bash
npm install sockjs-client stompjs
# or
npm install @stomp/stompjs sockjs-client
```

### Step 2: Create WebSocket Service

Create a WebSocket service to handle connections:

```javascript
// websocket-service.js
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
  }

  connect() {
    const socket = new SockJS("http://localhost:8080/ws");
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect(
      {},
      (frame) => {
        console.log("Connected: " + frame);
        this.connected = true;
      },
      (error) => {
        console.log("Connection error: " + error);
        this.connected = false;
      }
    );
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.connected = false;
    }
  }

  subscribeToTrip(tripId, callback) {
    if (this.stompClient && this.connected) {
      this.stompClient.subscribe(`/topic/trip/${tripId}/location`, callback);
    }
  }

  subscribeToDriverStatus(tripId, callback) {
    if (this.stompClient && this.connected) {
      this.stompClient.subscribe(`/topic/trip/${tripId}/status`, callback);
    }
  }

  sendLocationUpdate(locationUpdate) {
    if (this.stompClient && this.connected) {
      this.stompClient.send(
        "/app/location.update",
        {},
        JSON.stringify(locationUpdate)
      );
    }
  }

  sendDriverStatus(statusUpdate) {
    if (this.stompClient && this.connected) {
      this.stompClient.send(
        "/app/driver.status",
        {},
        JSON.stringify(statusUpdate)
      );
    }
  }

  subscribeToTripUpdates(tripId, userId, userType) {
    if (this.stompClient && this.connected) {
      const subscription = {
        tripId: tripId,
        userId: userId,
        userType: userType,
      };
      this.stompClient.send(
        "/app/trip.subscribe",
        {},
        JSON.stringify(subscription)
      );
    }
  }
}

export default new WebSocketService();
```

### Step 3: Driver Location Tracking Implementation

#### 3.1 Get User's Current Location

```javascript
// location-utils.js
export function getCurrentLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("Geolocation is not supported"));
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          accuracy: position.coords.accuracy,
          timestamp: new Date().toISOString(),
        });
      },
      (error) => {
        reject(error);
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 60000,
      }
    );
  });
}

export function watchLocation(callback) {
  if (!navigator.geolocation) {
    throw new Error("Geolocation is not supported");
  }

  return navigator.geolocation.watchPosition(
    (position) => {
      callback({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        accuracy: position.coords.accuracy,
        speed: position.coords.speed,
        heading: position.coords.heading,
        timestamp: new Date().toISOString(),
      });
    },
    (error) => {
      console.error("Location watch error:", error);
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 5000,
    }
  );
}
```

#### 3.2 Driver Location Update Component

```javascript
// DriverLocationTracker.jsx
import React, { useEffect, useState, useRef } from "react";
import WebSocketService from "./websocket-service";
import { watchLocation } from "./location-utils";

const DriverLocationTracker = ({ driverId, tripId, isActive }) => {
  const [currentLocation, setCurrentLocation] = useState(null);
  const [isTracking, setIsTracking] = useState(false);
  const watchIdRef = useRef(null);
  const lastUpdateRef = useRef(null);

  useEffect(() => {
    if (isActive && tripId) {
      startLocationTracking();
    } else {
      stopLocationTracking();
    }

    return () => {
      stopLocationTracking();
    };
  }, [isActive, tripId]);

  const startLocationTracking = () => {
    setIsTracking(true);

    // Connect to WebSocket
    WebSocketService.connect();

    // Subscribe to trip updates
    WebSocketService.subscribeToTripUpdates(tripId, driverId, "driver");

    // Start watching location
    watchIdRef.current = watchLocation((location) => {
      setCurrentLocation(location);
      sendLocationUpdate(location);
    });
  };

  const stopLocationTracking = () => {
    setIsTracking(false);

    if (watchIdRef.current) {
      navigator.geolocation.clearWatch(watchIdRef.current);
      watchIdRef.current = null;
    }

    WebSocketService.disconnect();
  };

  const sendLocationUpdate = (location) => {
    const now = Date.now();

    // Throttle updates to every 3 seconds
    if (lastUpdateRef.current && now - lastUpdateRef.current < 3000) {
      return;
    }

    const locationUpdate = {
      driverId: driverId.toString(),
      tripId: tripId,
      latitude: location.latitude,
      longitude: location.longitude,
      speed: location.speed || 0,
      heading: location.heading || 0,
      timestamp: location.timestamp,
    };

    WebSocketService.sendLocationUpdate(locationUpdate);
    lastUpdateRef.current = now;
  };

  const sendDriverStatus = (status) => {
    const statusUpdate = {
      tripId: tripId,
      driverId: driverId.toString(),
      status: status,
    };
    WebSocketService.sendDriverStatus(statusUpdate);
  };

  return (
    <div className="driver-location-tracker">
      <h3>Driver Location Tracking</h3>
      <p>Status: {isTracking ? "Tracking" : "Stopped"}</p>
      {currentLocation && (
        <div>
          <p>Lat: {currentLocation.latitude.toFixed(6)}</p>
          <p>Lng: {currentLocation.longitude.toFixed(6)}</p>
          <p>Speed: {currentLocation.speed || 0} m/s</p>
        </div>
      )}
      <div>
        <button onClick={() => sendDriverStatus("ARRIVED")}>
          Mark as Arrived
        </button>
        <button onClick={() => sendDriverStatus("STARTED_TRIP")}>
          Start Trip
        </button>
        <button onClick={() => sendDriverStatus("COMPLETED")}>
          Complete Trip
        </button>
      </div>
    </div>
  );
};

export default DriverLocationTracker;
```

### Step 4: Rider Location Subscription Implementation

#### 4.1 Rider Location Viewer Component

```javascript
// RiderLocationViewer.jsx
import React, { useEffect, useState } from "react";
import WebSocketService from "./websocket-service";

const RiderLocationViewer = ({ riderId, tripId, driverId }) => {
  const [driverLocation, setDriverLocation] = useState(null);
  const [connectionStatus, setConnectionStatus] = useState("Disconnected");

  useEffect(() => {
    if (tripId && driverId) {
      connectToLocationUpdates();
    }

    return () => {
      WebSocketService.disconnect();
    };
  }, [tripId, driverId]);

  const connectToLocationUpdates = () => {
    // Connect to WebSocket
    WebSocketService.connect();

    // Subscribe to trip location updates
    WebSocketService.subscribeToTrip(tripId, (message) => {
      try {
        const locationUpdate = JSON.parse(message.body);
        setDriverLocation(locationUpdate);
        setConnectionStatus("Connected");
      } catch (error) {
        console.error("Error parsing location update:", error);
      }
    });

    // Subscribe to driver status updates
    WebSocketService.subscribeToDriverStatus(tripId, (message) => {
      try {
        const statusUpdate = JSON.parse(message.body);
        console.log("Driver status update:", statusUpdate);
        // Handle status updates (arrived, started trip, etc.)
      } catch (error) {
        console.error("Error parsing status update:", error);
      }
    });

    // Subscribe to trip updates
    WebSocketService.subscribeToTripUpdates(tripId, riderId, "rider");
  };

  return (
    <div className="rider-location-viewer">
      <h3>Driver Location</h3>
      <p>Connection: {connectionStatus}</p>
      {driverLocation ? (
        <div>
          <p>Driver ID: {driverLocation.driverId}</p>
          <p>Latitude: {driverLocation.latitude}</p>
          <p>Longitude: {driverLocation.longitude}</p>
          <p>Speed: {driverLocation.speed || 0} km/h</p>
          <p>
            Last Update:{" "}
            {new Date(driverLocation.timestamp).toLocaleTimeString()}
          </p>
        </div>
      ) : (
        <p>Waiting for driver location updates...</p>
      )}
    </div>
  );
};

export default RiderLocationViewer;
```

### Step 5: Map Integration (Optional)

#### 5.1 Using Google Maps

```javascript
// MapComponent.jsx
import React, { useEffect, useRef, useState } from "react";

const MapComponent = ({ driverLocation, riderLocation }) => {
  const mapRef = useRef(null);
  const [map, setMap] = useState(null);
  const [driverMarker, setDriverMarker] = useState(null);
  const [riderMarker, setRiderMarker] = useState(null);

  useEffect(() => {
    if (mapRef.current && !map) {
      const googleMap = new window.google.maps.Map(mapRef.current, {
        center: { lat: 40.7128, lng: -74.006 }, // Default to NYC
        zoom: 15,
      });
      setMap(googleMap);
    }
  }, []);

  useEffect(() => {
    if (map && driverLocation) {
      const position = {
        lat: driverLocation.latitude,
        lng: driverLocation.longitude,
      };

      if (driverMarker) {
        driverMarker.setPosition(position);
      } else {
        const marker = new window.google.maps.Marker({
          position: position,
          map: map,
          title: "Driver",
          icon: {
            url: "https://maps.google.com/mapfiles/ms/icons/blue-dot.png",
          },
        });
        setDriverMarker(marker);
      }

      // Center map on driver location
      map.setCenter(position);
    }
  }, [map, driverLocation]);

  useEffect(() => {
    if (map && riderLocation) {
      const position = {
        lat: riderLocation.latitude,
        lng: riderLocation.longitude,
      };

      if (riderMarker) {
        riderMarker.setPosition(position);
      } else {
        const marker = new window.google.maps.Marker({
          position: position,
          map: map,
          title: "You",
          icon: {
            url: "https://maps.google.com/mapfiles/ms/icons/red-dot.png",
          },
        });
        setRiderMarker(marker);
      }
    }
  }, [map, riderLocation]);

  return (
    <div className="map-container">
      <div ref={mapRef} style={{ width: "100%", height: "400px" }} />
    </div>
  );
};

export default MapComponent;
```

### Step 6: Complete Integration Example

#### 6.1 Driver App Integration

```javascript
// DriverApp.jsx
import React, { useState } from "react";
import DriverLocationTracker from "./DriverLocationTracker";

const DriverApp = () => {
  const [driverId] = useState(123); // Get from authentication
  const [currentTrip, setCurrentTrip] = useState(null);
  const [isOnline, setIsOnline] = useState(false);

  const startTrip = (tripId) => {
    setCurrentTrip({ id: tripId });
    setIsOnline(true);
  };

  const endTrip = () => {
    setCurrentTrip(null);
    setIsOnline(false);
  };

  return (
    <div className="driver-app">
      <h1>Driver Dashboard</h1>

      {currentTrip ? (
        <div>
          <h2>Trip in Progress</h2>
          <p>Trip ID: {currentTrip.id}</p>
          <DriverLocationTracker
            driverId={driverId}
            tripId={currentTrip.id}
            isActive={isOnline}
          />
          <button onClick={endTrip}>End Trip</button>
        </div>
      ) : (
        <div>
          <h2>No Active Trip</h2>
          <button onClick={() => startTrip("trip-123")}>Start Test Trip</button>
        </div>
      )}
    </div>
  );
};

export default DriverApp;
```

#### 6.2 Rider App Integration

```javascript
// RiderApp.jsx
import React, { useState } from "react";
import RiderLocationViewer from "./RiderLocationViewer";
import MapComponent from "./MapComponent";

const RiderApp = () => {
  const [riderId] = useState(456); // Get from authentication
  const [currentTrip, setCurrentTrip] = useState(null);
  const [riderLocation, setRiderLocation] = useState(null);

  const requestRide = () => {
    // Simulate trip creation
    const tripId = "trip-123";
    const driverId = 123;
    setCurrentTrip({ id: tripId, driverId: driverId });
  };

  const getCurrentLocation = () => {
    navigator.geolocation.getCurrentPosition((position) => {
      setRiderLocation({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      });
    });
  };

  return (
    <div className="rider-app">
      <h1>Rider Dashboard</h1>

      <button onClick={getCurrentLocation}>Get My Location</button>
      <button onClick={requestRide}>Request Ride</button>

      {currentTrip && (
        <div>
          <h2>Active Trip</h2>
          <p>Trip ID: {currentTrip.id}</p>
          <RiderLocationViewer
            riderId={riderId}
            tripId={currentTrip.id}
            driverId={currentTrip.driverId}
          />
          <MapComponent riderLocation={riderLocation} />
        </div>
      )}
    </div>
  );
};

export default RiderApp;
```

## Testing the Integration

### 1. Start the Backend

```bash
./gradlew bootRun
```

### 2. Test WebSocket Connection

Open browser console and test the connection:

```javascript
// Test WebSocket connection
const socket = new SockJS("http://localhost:8080/ws");
const stompClient = Stomp.over(socket);
stompClient.connect({}, (frame) => {
  console.log("Connected: " + frame);
});
```

### 3. Test Location Updates

```javascript
// Test sending location update
const locationUpdate = {
  driverId: "123",
  tripId: "trip-123",
  latitude: 40.7128,
  longitude: -74.006,
  speed: 25.5,
  heading: 90,
};

stompClient.send("/app/location.update", {}, JSON.stringify(locationUpdate));
```

## Security Considerations

1. **Authentication**: Ensure WebSocket connections are authenticated
2. **Authorization**: Verify users can only access their own data
3. **Rate Limiting**: Implement rate limiting for location updates
4. **Data Validation**: Validate all incoming location data
5. **HTTPS**: Use secure WebSocket connections (WSS) in production

## Performance Optimization

1. **Throttling**: Limit location updates to reasonable intervals (2-5 seconds)
2. **Distance Filtering**: Only send updates when driver moves significant distance
3. **Connection Management**: Properly close WebSocket connections
4. **Memory Management**: Clean up old location data periodically

## Troubleshooting

### Common Issues

1. **WebSocket Connection Failed**

   - Check if backend is running on correct port
   - Verify CORS settings
   - Check browser console for errors

2. **Location Updates Not Received**

   - Verify subscription to correct topic
   - Check if driver is sending updates
   - Verify trip ID matches

3. **Geolocation Permission Denied**
   - Request location permission from user
   - Handle permission denied gracefully
   - Provide fallback options

### Debug Commands

```javascript
// Check WebSocket connection status
console.log("Connected:", WebSocketService.connected);

// Check active subscriptions
console.log(
  "Active subscriptions:",
  WebSocketService.stompClient.subscriptions
);

// Test location update
WebSocketService.sendLocationUpdate({
  driverId: "123",
  tripId: "test-trip",
  latitude: 40.7128,
  longitude: -74.006,
});
```

This integration guide provides a complete implementation of live location tracking functionality. The system supports real-time location updates from drivers and allows riders to subscribe to these updates for a seamless ride-sharing experience.

