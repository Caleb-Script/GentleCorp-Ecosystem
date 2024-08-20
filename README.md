# GentleCorp-Ecosystem

Welcome to the GentleCorp-Ecosystem! This project is a comprehensive suite of microservices designed to meet various needs in a modern digital environment. The ecosystem encompasses a wide range of services, from online shopping and travel booking to financial services, social networking, and more.

## Table of Contents

1. [Overview](#overview)
2. [Tech Stack](#tech-stack)
   - [Frontend](#frontend)
   - [Backend](#backend)
   - [Technologies Used](#technologies-used)
3. [Repository Structure](#repository-structure)
4. [Getting Started](#getting-started)
   - [Using Docker](#Starting with Docker)
   - [Starting Individually](#Starting Individual Services)
5. [Contributing](#contributing)
6. [License](#license)

---

## Overview

**GentleCorp-Ecosystem** is a modular, scalable, and flexible microservices-based platform. It offers a variety of services including online shopping, travel booking, banking, real estate management, food delivery, and more. The platform leverages modern web technologies to deliver a seamless user experience.

## Tech Stack

### Frontend

- **Framework**: [Next.js](https://nextjs.org/) (using the App Router)
- **Language**: TypeScript
- **Styling**: [Bootstrap](https://getbootstrap.com/)

### Backend

- **Java (Spring Boot)**

  - **Gradle**:
    - GentleBank
    - GentleAnalytics
    - GentleTravel
  - **Maven**:
    - GentleEstate
    - GentleMaps

- **TypeScript (NestJS)**
  - GentleShop
  - GentleChat
  - GentleFood
  - GentleDating
  - GentleMarket
  - GentleNetwork

### Technologies Used

- **Databases**: MySQL and PostgreSQL
- **Messaging**: Kafka
- **Authentication**: Keycloak
- **Distributed Tracing**: Zipkin
- **Containerization**: Docker

## Repository Structure

```plaintext
GentleCorp-Ecosystem/
│
├── frontend/                # Frontend application (Next.js with TypeScript)
│   ├── public/              # Static assets
│   ├── src/                 # Source files
│   │   ├── components/      # React components
│   │   ├── pages/           # Next.js pages
│   │   ├── styles/          # CSS/Bootstrap styles
│   │   └── ...              # Other TypeScript files
│   └── package.json         # Frontend dependencies and scripts
│
├── backend/                 # Backend services
│   ├── gentle-shop/         # GentleShop service (TypeScript/NestJS)
│   ├── gentle-travel/       # GentleTravel service (Java/Spring Boot with Maven)
│   ├── gentle-chat/         # GentleChat service (TypeScript/NestJS)
│   ├── gentle-food/         # GentleFood service (TypeScript/NestJS)
│   ├── gentle-maps/         # GentleMaps service (Java/Spring Boot with Maven)
│   ├── gentle-dating/       # GentleDating service (TypeScript/NestJS)
│   ├── gentle-market/       # GentleMarket service (TypeScript/NestJS)
│   ├── gentle-network/      # GentleNetwork service (TypeScript/NestJS)
│   ├── gentle-analytics/    # GentleAnalytics service (Java/Spring Boot with Gradle)
│   ├── gentle-estate/       # GentleEstate service (Java/Spring Boot with Maven)
│   └── gentle-bank/         # GentleBank service (Java/Spring Boot with Gradle)
│
├── docker-compose.yml       # Docker Compose file for container orchestration
└── README.md                # Project documentation
```

## Getting Started

### Starting with Docker

1. Ensure Docker and Docker Compose are installed.

2. Clone the repository:

   ```bash
   git clone https://github.com/Caleb-Script/GentleCorp-Ecosystem.git
   ```

3. Navigate to the project directory:

   ```bash
   cd GentleCorp-Ecosystem
   ```

4. Start the project using Docker:

   ```bash
   docker compose up
   ```

### Starting Individual Services

**Frontend:**

1. Navigate to the frontend directory:

   ```bash
   cd frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the frontend service:

   ```bash
   npm run dev
   ```

**Backend Services:**

For Java-based services, you can start them individually. The commands depend on whether the service uses Maven or Gradle:

**Gradle:**

1. Navigate to the specific backend service directory:

   ```bash
   cd backend/gentle-bank
   cd backend/gentle-travel
   cd backend/gentle-analytics
   ```

2. Build and run the service:

   ```bash
   ./gradlew bootRun
   ```

**Maven:**

1. Navigate to the specific backend service directory:

   ```bash
   cd backend/gentle-maps
   cd backend/gentle-estate
   ```

2. Build and run the service:

   ```bash
   ./mvnw spring-boot:run
   ```

**TypeScript (NestJS):**

1. Navigate to the specific backend service directory:

   ```bash
   cd backend/gentle-shop
   cd backend/gentle-chat
   cd backend/gentle-food
   cd backend/gentle-dating
   cd backend/gentle-market
   cd backend/gentle-network
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the frontend service:

   ```bash
   npm run dev
   ```

## Contributing

Feel free to contribute to the project by creating issues or submitting pull requests.

## License

This project is licensed under the MIT License.

---
