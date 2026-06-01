
# create event
curl -X POST http://localhost:8080/events -H "Content-Type: application/json" -d "{\"name\":\"Show A\",\"eventDate\":\"2026-09-01T20:00:00\",\"price\":150.00,\"availableQuantity\":2}"

# list events
curl http://localhost:8080/events

# reserve ticket
curl -X POST http://localhost:8080/events/1/reserve


###

# register a user
curl -X POST http://localhost:8081/users -H "Content-Type: application/json" -d "{\"email\":\"leo@test.com\",\"name\":\"Leonardo\"}"

# create an event with 1 ticket (on event-service)
curl -X POST http://localhost:8080/events -H "Content-Type: application/json" -d "{\"name\":\"Show B\",\"eventDate\":\"2026-09-15T21:00:00\",\"price\":200.00,\"availableQuantity\":1}"

# buy the ticket
curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d "{\"userId\":1,\"eventId\":1}"

# try to buy again = 409
curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d "{\"userId\":1,\"eventId\":1}"



ticket-system/
├── docker-compose.yml
├── prometheus/
│   └── prometheus.yml
├── event-service/
└── order-service/          ← new
    ├── Dockerfile
    ├── pom.xml
    └── src/main/
        ├── java/com/tickets/orderservice/
        │   ├── OrderServiceApplication.java
        │   ├── GlobalExceptionHandler.java
        │   ├── user/
        │   │   ├── User.java
        │   │   ├── UserRepository.java
        │   │   ├── UserService.java
        │   │   └── UserController.java
        │   └── order/
        │       ├── Order.java
        │       ├── OrderRepository.java
        │       ├── OrderService.java
        │       ├── OrderController.java
        │       ├── CreateOrderRequest.java
        │       └── EventServiceClient.java
        └── resources/
            application.yml