# BancoBase Challenge - Payment Service API

This project implements a Payment Service using **Spring Boot 3.2+**, **Java 17**, and **MongoDB 6.0+**, integrated with **RabbitMQ 3.12+** for asynchronous event handling.
The service allows creating, retrieving, and updating payment records and publishes events to RabbitMQ when payment statuses change. 
The application is containerized using Docker and orchestrated via Docker Compose for easy setup.


## Features

- Create a new payment with key attributes (concept, quantity, payer, payee, amount, status).
- Retrieve payment details by ID.
- Update the payment status.
- Notify RabbitMQ on status updates, enabling support for multiple asynchronous consumers.
- API documentation available through Swagger UI.
- Basic code coverage and test support with JUnit.


## Prerequisites

- Docker and Docker Compose installed on your machine.
    - [Install Docker](https://docs.docker.com/get-docker/)
    - [Install Docker Compose](https://docs.docker.com/compose/install/)
- Optional: Postman to test REST endpoints
- Optional: MongoDB Compass to explore the MongoDB database


## 1. Setup docker services with Docker Compose
Clone this project to your local machine:

```bash
git clone https://github.com/JAlayon/bancobase-challenge.git
```

Move to the corresponding directory on your machine:
```bash
cd [path-to-your-cloned-project]/bancobase-challenge
```


## 2. Setup docker services with Docker Compose
This project is fully containerized. Use the following steps to build and run everything.

```bash
docker-compose up --build
```

This will build the payment-service Docker image and spin up containers for:
 - MongoDB
 - RabbitMQ (with admin credentials and preconfigured queues/exchanges)
 - payment-service


#### a. Verify services are running:

Once the containers are up and running, you can access to and verify:

* Payment Service: The service will be available at http://localhost:8080/payments-service, and you can check out available endpoints at: http://localhost:8080/payments-service/swagger-ui/index.html
  * Check if is healthy at: http://localhost:8080/payments-service/management/health
  
* RabbitMQ: RabbitMQ will be available at http://localhost:15672 with the following credentials:
  * UserName: admin
  * Password: admin123


## 3. Test service operations
To test our service operations, we can use postman collection under `postman` folder. 
Just import the json file in postman to start testing the different endpoints.


## 4. Accessing Database
To connect to the MongoDB database running in the Docker container, you can use the following connection details:

* Host: localhost or mongodb (for services running within the Docker network)
* Port: 27017

We don't need any authentication credentials so far, just these information. We can use some scripts or Compass for UI easy visualization.

You can find out the schema specification under `db`folder.


## 5. Messaging Configuration (RabbitMQ)
This service uses RabbitMQ to publish events when a payment status is updated.

###  Exchange Definition

| Name              | Type  | Durable | Auto Delete |
|-------------------|-------|---------|-------------|
| `payment.exchange` | topic | true    | false       |

### Queue Definition

| Name                  | Durable | Auto Delete |
|------------------------|---------|-------------|
| `payment.status.queue` | true    | false       |

### Binding

| Exchange          | Queue                  | Routing Key              |
|-------------------|------------------------|---------------------------|
| `payment.exchange` | `payment.status.queue` | `payment.status.changed` |

### Example Event Message

```json
{
  "id": "ea9b7a3e-c152-4bc0-a1a1-49e3a1bcbf3e",
  "status": "COMPLETED",
  "concept": "Product Purchase",
  "payer": "Alice",
  "recipient": "Bob",
  "totalAmount": "1200.00",
  "createdAt": "2024-05-28T12:34:56.123"
}
```
You can find out the whole definition under `rabbit` folder.
