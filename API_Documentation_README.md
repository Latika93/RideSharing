# RideSharing API Documentation

This document provides comprehensive information about the RideSharing API, including how to access Swagger documentation and use the Postman collection.

## Table of Contents

- [Swagger Documentation](#swagger-documentation)
- [Postman Collection](#postman-collection)
- [API Endpoints Overview](#api-endpoints-overview)
- [Authentication](#authentication)
- [Getting Started](#getting-started)
- [WebSocket Support](#websocket-support)

## Swagger Documentation

### Accessing Swagger UI

Once your Spring Boot application is running, you can access the Swagger UI at:

```
http://localhost:9800/swagger-ui.html
```

Or alternatively:

```
http://localhost:9800/swagger-ui/index.html
```

### Features

- **Interactive API Documentation**: Test all endpoints directly from the browser
- **Request/Response Examples**: See example payloads for all endpoints
- **Authentication Support**: JWT Bearer token authentication
- **Schema Documentation**: Complete data models and DTOs
- **Error Response Documentation**: Detailed error response examples

### Using Swagger UI

1. **Authentication**: Click the "Authorize" button and enter your JWT token in the format: `Bearer your_jwt_token_here`
2. **Testing Endpoints**: Click on any endpoint to expand it, then click "Try it out"
3. **Request Bodies**: Use the provided examples or modify them as needed
4. **Execute**: Click "Execute" to send the request and see the response

## Postman Collection

### Importing the Collection

1. Open Postman
2. Click "Import" button
3. Select the `RideSharing_API_Collection.postman_collection.json` file
4. The collection will be imported with all endpoints organized by category

### Environment Variables

The collection uses the following environment variables:

| Variable             | Default Value           | Description                  |
| -------------------- | ----------------------- | ---------------------------- |
| `base_url`           | `http://localhost:9800` | Base URL for the API         |
| `jwt_token`          | (empty)                 | JWT token for authentication |
| `driver_id`          | `1`                     | Sample driver ID             |
| `rider_id`           | `1`                     | Sample rider ID              |
| `trip_id`            | `1`                     | Sample trip ID               |
| `user_id`            | `1`                     | Sample user ID               |
| `coupon_id`          | `1`                     | Sample coupon ID             |
| `coupon_code`        | `SAVE10`                | Sample coupon code           |
| `verification_token` | (empty)                 | Email verification token     |

### Setting Up Environment

1. Create a new environment in Postman
2. Add the variables listed above
3. Set the `base_url` to your server URL
4. After login, copy the JWT token and set it in the `jwt_token` variable

## API Endpoints Overview

### 1. Authentication (`/auth/*`)

- **POST** `/auth/register/rider` - Register a new rider
- **POST** `/auth/register/driver` - Register a new driver
- **POST** `/auth/login` - User login
- **GET** `/auth/verifyRegistrationToken` - Verify email registration
- **POST** `/auth/logout` - User logout

### 2. Fare Calculation (`/auth/fare/*`)

- **POST** `/auth/fare/calculate` - Calculate ride fare
- **POST** `/auth/fare/coupons` - Create coupon
- **GET** `/auth/fare/coupons/valid` - Get valid coupons
- **GET** `/auth/fare/coupons/{code}` - Get coupon by code
- **PUT** `/auth/fare/coupons/{id}` - Update coupon
- **DELETE** `/auth/fare/coupons/{id}` - Delete coupon
- **GET** `/auth/fare/health` - Health check

### 3. Trip Management (`/api/trip/*`)

- **POST** `/api/trip/request` - Create trip request
- **PATCH** `/api/trip/{id}/accept` - Accept trip
- **PATCH** `/api/trip/{id}/start` - Start trip
- **PATCH** `/api/trip/{id}/end` - Complete trip
- **PATCH** `/api/trip/{id}/cancel` - Cancel trip
- **GET** `/api/trip/{id}` - Get trip details
- **GET** `/api/trip/rider/{id}/active` - Get rider's active trips
- **GET** `/api/trip/driver/{id}/active` - Get driver's active trips
- **GET** `/api/trip/rider/{id}/history` - Get rider's trip history
- **GET** `/api/trip/driver/{id}/history` - Get driver's trip history

### 4. Driver Operations (`/driver/*`)

- **PATCH** `/driver/{id}/status` - Update driver availability
- **GET** `/driver/{id}` - Get driver profile
- **POST** `/driver/{id}/location` - Update driver location
- **GET** `/driver/{id}/location` - Get driver location
- **GET** `/driver/{id}/location/history` - Get location history

### 5. Rider Operations (`/rider/*`)

- **GET** `/rider/{id}` - Get rider profile
- **PATCH** `/rider/{id}` - Update rider profile
- **GET** `/rider/{id}/driver/{driverId}/location` - Get driver location
- **GET** `/rider/{id}/drivers/nearby` - Get nearby drivers

### 6. Driver-Rider Matching (`/api/matching/*`)

- **PATCH** `/api/matching/driver/{id}/location` - Update driver location
- **PATCH** `/api/matching/rider/{id}/location` - Update rider location
- **GET** `/api/matching/drivers/nearby` - Get nearby drivers
- **POST** `/api/matching/match/nearest` - Match nearest driver
- **POST** `/api/matching/match/least-busy` - Match least busy driver
- **POST** `/api/matching/match/high-rating` - Match high-rated driver

## Authentication

The API uses JWT (JSON Web Token) for authentication. Here's how to use it:

### 1. Login to Get Token

```bash
POST /auth/login
Content-Type: application/json

{
  "username": "your_username",
  "password": "your_password"
}
```

### 2. Use Token in Requests

Include the token in the Authorization header:

```
Authorization: Bearer your_jwt_token_here
```

### 3. Token Expiration

Tokens expire after a certain period. You'll need to login again to get a new token.

## Getting Started

### Prerequisites

- Java 17 or higher
- MySQL database
- Spring Boot application running on port 9800

### Step-by-Step Guide

1. **Start the Application**

   ```bash
   ./gradlew bootRun
   ```

2. **Access Swagger UI**

   - Open browser and go to `http://localhost:9800/swagger-ui.html`

3. **Register a User**

   - Use the `/auth/register/rider` or `/auth/register/driver` endpoint
   - Check console logs for verification link

4. **Verify Email**

   - Click the verification link from console logs
   - Or use the `/auth/verifyRegistrationToken` endpoint

5. **Login**

   - Use the `/auth/login` endpoint to get JWT token

6. **Test Protected Endpoints**
   - Use the JWT token in Authorization header
   - Test various endpoints like fare calculation, trip creation, etc.

### Sample Workflow

1. **Register a Rider**

   ```json
   POST /auth/register/rider
   {
     "username": "john_rider",
     "password": "password123",
     "email": "john@example.com",
     "firstName": "John",
     "lastName": "Doe",
     "phoneNumber": "+1234567890",
     "paymentMethod": "CREDIT_CARD",
     "preferences": "QUIET_RIDE"
   }
   ```

2. **Register a Driver**

   ```json
   POST /auth/register/driver
   {
     "username": "jane_driver",
     "password": "password123",
     "email": "jane@example.com",
     "firstName": "Jane",
     "lastName": "Smith",
     "licenseNumber": "DL123456789",
     "vehicleMake": "Toyota",
     "vehicleModel": "Camry",
     "vehicleYear": 2020,
     "vehicleColor": "Silver",
     "vehiclePlateNumber": "ABC123"
   }
   ```

3. **Login as Rider**

   ```json
   POST /auth/login
   {
     "username": "john_rider",
     "password": "password123"
   }
   ```

4. **Create Trip Request**

   ```json
   POST /api/trip/request
   {
     "riderId": 1,
     "pickupLocation": {
       "latitude": 40.7128,
       "longitude": -74.0060,
       "address": "123 Main St, New York, NY"
     },
     "dropoffLocation": {
       "latitude": 40.7589,
       "longitude": -73.9851,
       "address": "456 Broadway, New York, NY"
     },
     "matchingStrategy": "nearest"
   }
   ```

5. **Calculate Fare**
   ```json
   POST /auth/fare/calculate
   {
     "distance": 5.5,
     "duration": 15,
     "rideTime": "2024-01-15T10:30:00",
     "weatherCondition": "CLEAR",
     "couponCode": "SAVE10",
     "rideType": "ECONOMY",
     "baseRate": 2.5
   }
   ```

## WebSocket Support

The API supports real-time communication through WebSocket connections:

### WebSocket URL

```
ws://localhost:9800/ws
```

### Available Endpoints

1. **Location Updates**

   - Endpoint: `/app/location.update`
   - Purpose: Send real-time location updates from drivers
   - Subscribe to: `/topic/trip/{tripId}/location`

2. **Driver Status Updates**

   - Endpoint: `/app/driver.status`
   - Purpose: Send driver status updates (arrived, started, etc.)
   - Subscribe to: `/topic/trip/{tripId}/status`

3. **Trip Subscriptions**

   - Endpoint: `/app/trip.subscribe`
   - Purpose: Subscribe to trip updates
   - Subscribe to: `/queue/user/{userId}`

4. **Trip Unsubscriptions**
   - Endpoint: `/app/trip.unsubscribe`
   - Purpose: Unsubscribe from trip updates

### WebSocket Message Examples

**Location Update:**

```json
{
  "driverId": "1",
  "tripId": "1",
  "latitude": 40.7128,
  "longitude": -74.006,
  "speed": 25.5,
  "heading": 90.0
}
```

**Driver Status Update:**

```json
{
  "tripId": "1",
  "driverId": "1",
  "status": "ARRIVED"
}
```

**Trip Subscription:**

```json
{
  "tripId": "1",
  "userId": "1",
  "userType": "rider"
}
```

## Error Handling

The API returns consistent error responses:

```json
{
  "error": "Error Type",
  "message": "Detailed error message"
}
```

Common HTTP status codes:

- `200` - Success
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

## Rate Limiting

Currently, there are no rate limits implemented, but they may be added in future versions.

## Support

For issues or questions:

- Check the Swagger documentation for detailed endpoint information
- Use the Postman collection for testing
- Review the console logs for debugging information

## Version History

- **v1.0.0** - Initial release with complete API documentation
