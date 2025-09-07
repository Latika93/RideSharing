1. Authentication & User Management


#Factory Method
POST /auth/register/rider → Register a new rider with username, password, email, phone, etc.

POST /auth/register/driver → Register a new driver with username, password, license, vehicle details.

POST /auth/login → Authenticate rider/driver and return JWT (used for all secure APIs).

POST /auth/logout → Invalidate current session/token

2. Driver Availability

PATCH /driver/{id}/status → Driver marks themselves online/offline.

GET /driver/{id} → Fetch driver profile and current availability.

3. Rider Profile

GET /rider/{id} → Fetch rider profile (default payment method, preferences).

PATCH /rider/{id} → Update profile (preferred vehicle, payment method, etc.).

4. Trip Management

POST /trip/request

Rider sends pickup & dropoff location.

System finds a nearby driver and creates a new trip in REQUESTED state.

PATCH /trip/{id}/accept

Driver accepts assigned trip. Trip moves to ACCEPTED.

PATCH /trip/{id}/start

Driver marks trip as started when rider gets in. State → STARTED.

PATCH /trip/{id}/end

Driver ends trip when completed. State → COMPLETED, fare calculated.

PATCH /trip/{id}/cancel

Rider or driver cancels before completion. State → CANCELLED.

GET /trip/{id} → Get trip details (status, rider, driver, fare).

5. Matching & Location

PATCH /driver/{id}/location → Driver updates their current GPS coordinates.

PATCH /rider/{id}/location → Rider updates pickup location (optional if not passed in trip request).

GET /drivers/nearby?lat=..&lng=.. → Return a list of available drivers sorted by proximity.

6. Payments

POST /payment/{tripId} → Trigger payment for a completed trip (mock first, later integrate Stripe/Razorpay).

GET /payment/{tripId} → Fetch payment status for a trip.

7. History & Reports

GET /rider/{id}/trips → Rider’s past rides with status & fare.

GET /driver/{id}/trips → Driver’s past rides with status & fare.

GET /driver/{id}/earnings → Summary of completed trips and earnings.

8. Ratings & Reviews (Stretch)

POST /trip/{id}/rating → Rider rates driver (stars, comments).

GET /driver/{id}/ratings → Average rating & reviews for driver.

9. Admin / System (Optional)

GET /admin/drivers/pending → List of drivers pending verification.

PATCH /admin/driver/{id}/verify → Approve/reject driver.