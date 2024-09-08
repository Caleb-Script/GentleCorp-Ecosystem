# GentleCorp-Ecosystem

Welcome to the GentleCorp-Ecosystem! This project is a comprehensive suite of microservices designed to meet various needs in a modern digital environment. The ecosystem encompasses a wide range of services, from online shopping and travel booking to financial services, social networking, and more.

## Table of Contents

1. [Overview](#overview)
2. [Dienste](#dienste)
3. [Microservices](#microservices)
4. [Datenbanktechnologie für Microservice](#datenbanktechnologie-für-microservices)
5. [Tech Stack](#tech-stack)
   - [Frontend](#frontend)
   - [Backend](#backend)
   - [Technologies Used](#technologies-used)
6. [Repository Structure](#repository-structure)
7. [Getting Started](#getting-started)
   - [Using Docker](#Starting with Docker)
   - [Starting Individually](#Starting Individual Services)
8. [Contributing](#contributing)
9. [License](#license)

---

## Overview

**GentleCorp-Ecosystem** is a modular, scalable, and flexible microservices-based platform. It offers a variety of services including online shopping, travel booking, banking, real estate management, food delivery, and more. The platform leverages modern web technologies to deliver a seamless user experience.

## Dienste

### GentleStore

**Beschreibung**: Eine umfassende Plattform für Online-Shopping, die es Nutzern ermöglicht, Produkte aus verschiedenen Kategorien zu durchsuchen, zu kaufen und zu verwalten.

**Funktionen**:

- **Produktkatalog**: Nutzer können Produkte in verschiedenen Kategorien durchsuchen und filtern, um genau das zu finden, was sie suchen.
- **Warenkorb**: Produkte können dem Warenkorb hinzugefügt, daraus entfernt und vor dem Kauf überprüft werden.
- **Bestellmanagement**: Nutzer können ihre Bestellungen nachverfolgen und verwalten, von der Aufgabe bis zur Lieferung.
- **Zahlungsgateway**: Die Plattform unterstützt mehrere Zahlungsmethoden, einschließlich der Integration sicherer Zahlungsdienste.

### GentleBank

**Beschreibung**: Eine Online-Banking-App, die es Nutzern ermöglicht, ihre Bankkonten effizient zu verwalten, Transaktionen durchzuführen und Kredite zu beantragen.

**Funktionen**:

- **Kontoverwaltung**: Nutzer können ihre Kontostände und -aktivitäten in Echtzeit einsehen und verwalten.
- **Überweisungen**: Die App ermöglicht es, sichere Inlands- und Auslandsüberweisungen durchzuführen.
- **Transaktionsübersicht**: Alle Kontoaktivitäten können detailliert verfolgt und analysiert werden.
- **Kreditvergabe**: Nutzer können Kredite direkt über die App beantragen und verwalten.

### GentleTravel

**Beschreibung**: Eine vielseitige Plattform, die es Nutzern ermöglicht, Reisen zu planen und zu buchen, einschließlich Flügen, Unterkünften, Transportmitteln und Aktivitäten.

**Funktionen**:

- **Flugbuchungen**: Nutzer können Flüge weltweit suchen und buchen.
- **Hotelreservierungen**: Die Plattform ermöglicht die Buchung von Hotels und anderen Unterkünften.
- **Reiseplanung**: Komplettlösungen für die Planung von Reisen, einschließlich der Organisation von Transport und Aktivitäten.
- **Buchungsmanagement**: Nutzer können ihre Buchungen verwalten, anpassen und nachverfolgen.

### GentleEstate

**Beschreibung**: Eine spezialisierte Plattform für den Kauf und die Miete von Immobilien. Sie bietet umfassende Tools zur Immobiliensuche und Vertragsverwaltung.

**Funktionen**:

- **Immobiliensuche**: Nutzer können Immobilienangebote durchsuchen und nach verschiedenen Kriterien filtern.
- **Kauf und Miete**: Die Plattform bietet Optionen zum Kauf oder zur Miete von Wohn- und Gewerbeimmobilien.
- **Vertragsmanagement**: Nutzer können ihre Miet- und Kaufverträge verwalten und einsehen.
- **Besichtigungstermine**: Termine für Immobilienbesichtigungen können einfach vereinbart und verwaltet werden.

### GentleMaps

**Beschreibung**: Ein Kartendienst, der umfangreiche Funktionen zur Standortsuche und Navigation bietet, basierend auf einer fortschrittlichen Karten-API.

**Funktionen**:

- **Navigation**: Die Plattform bietet detaillierte Routenplanung und Navigation für verschiedene Verkehrsmittel.
- **Standortsuche**: Nutzer können nach Orten, Unternehmen und Sehenswürdigkeiten suchen.
- **Verkehrsinformationen**: Die Plattform liefert aktuelle Verkehrsdaten und Alternativrouten.
- **Street View**: Nutzer können Standorte durch virtuelle Ansichten erkunden.

### GentleChat

**Beschreibung**: Eine Kommunikations-App, die es Nutzern ermöglicht, miteinander zu chatten und Nachrichten auszutauschen, sowohl für private als auch für geschäftliche Zwecke.

**Funktionen**:

- **Einzel- und Gruppenchats**: Nutzer können private Unterhaltungen und Gruppenchats erstellen und verwalten.
- **Multimedia-Nachrichten**: Die App unterstützt den Austausch von Texten, Bildern, Videos und Sprachnachrichten.
- **End-to-End-Verschlüsselung**: Nachrichten werden durch verschlüsselte Kommunikation gesichert.
- **Status-Updates**: Nutzer können Statusmeldungen mit ihren Kontakten teilen.
- **Kundensupport**: Die App bietet ein System für Live-Chats, Ticketing und Feedback-Management für Kunden.

### GentleFood

**Beschreibung**: Eine Plattform für die Bestellung von Essen, die es Nutzern ermöglicht, Mahlzeiten von Restaurants in ihrer Nähe zu bestellen und liefern zu lassen.

**Funktionen**:

- **Restaurantsuche**: Nutzer können Restaurants nach verschiedenen Kriterien durchsuchen, wie Küche, Preis und Bewertung.
- **Speisekarte**: Die Plattform bietet Zugriff auf die Speisekarten der Restaurants, aus denen direkt bestellt werden kann.
- **Bestellverfolgung**: Nutzer können ihre Bestellungen in Echtzeit verfolgen.
- **Zahlungsoptionen**: Die Plattform unterstützt mehrere sichere Zahlungsmethoden für die Bestellung.

### GentleDating

**Beschreibung**: Eine App, die Menschen miteinander verbindet und es ihnen ermöglicht, Beziehungen aufzubauen, basierend auf gemeinsamen Interessen und Standortpräferenzen.

**Funktionen**:

- **Profile**: Nutzer können persönliche Profile erstellen und pflegen, um sich anderen vorzustellen.
- **Matching**: Die App schlägt potenzielle Partner basierend auf gemeinsamen Interessen und Standort vor.
- **Nachrichten**: Nach einem erfolgreichen Match können Nutzer private Nachrichten austauschen.
- **Events**: Die App ermöglicht es Nutzern, lokale Veranstaltungen für Singles zu organisieren oder daran teilzunehmen.

### GentleMarket

**Beschreibung**: Eine Plattform, die Nutzern ermöglicht, Produkte zu kaufen, zu verkaufen und an Auktionen teilzunehmen, mit einem Schwerpunkt auf benutzergenerierten Inhalten.

**Funktionen**:

- **Produktverkauf**: Nutzer können Produkte einstellen und verkaufen.
- **Auktionen**: Die Plattform bietet Funktionen zur Teilnahme an Auktionen und zum Verwalten eigener Angebote.
- **Bewertungssystem**: Nutzer können Käufer und Verkäufer bewerten, um Vertrauen in der Community aufzubauen.
- **Zahlungsabwicklung**: Sichere Zahlungsoptionen sind für alle Transaktionen integriert.

### GentleNetwork

**Beschreibung**: Ein soziales Netzwerk, das sowohl berufliche als auch persönliche Verbindungen fördert und die Möglichkeit bietet, Inhalte zu teilen und zu kommentieren.

**Funktionen**:

- **Benutzerprofile**: Nutzer können ihre Profile gestalten und anpassen, um ihre Interessen und beruflichen Fähigkeiten darzustellen.
- **Posts und Kommentare**: Die Plattform ermöglicht es, Inhalte zu erstellen, zu teilen und Kommentare zu hinterlassen.
- **Netzwerkaufbau**: Nutzer können Verbindungen zu anderen aufbauen und pflegen, um berufliche und persönliche Netzwerke zu erweitern.
- **Jobangebote**: Die Plattform unterstützt das Suchen und Posten von Jobangeboten.

### GentleAnalytics

**Beschreibung**: Ein Analyse- und Reporting-Tool, das umfassende Einblicke in die Nutzung und Performance der Dienste im GentleCorp-Ecosystem bietet. Dieses Tool ist ausschließlich für Administratoren sowie exklusive und Supreme-Kunden verfügbar.

**Funktionen**:

- **Datenvisualisierung**: Erstellung von Diagrammen und Grafiken zur Visualisierung von Geschäftsdaten und Nutzerverhalten.
- **Nutzungsstatistiken**: Die Plattform bietet einen umfassenden Überblick über die Nutzung der verschiedenen Dienste.
- **Geschäftsanalyse**: Detaillierte Analyse von Umsatz, Gewinn und anderen wichtigen Geschäftskennzahlen.
- **Berichtserstellung**: Die Plattform generiert detaillierte Berichte über diverse Metriken und Schlüsselindikatoren.

## Microservices

Das GentleCorp-Ecosystem setzt auf eine Reihe spezialisierter Microservices, die jeweils bestimmte Aufgaben erfüllen und somit eine modulare, skalierbare Architektur ermöglichen. Jeder Microservice ist für bestimmte Aspekte der Plattform verantwortlich und gewährleistet eine nahtlose Integration und einen reibungslosen Betrieb der Dienste.

### 1. Auction Service

**Funktion**: Verwalten von Auktionen.

**Beschreibung**: Dieser Service steuert alle Aspekte von Auktionen innerhalb des Systems. Er ermöglicht die Erstellung neuer Auktionen, das Überwachen von Geboten, die Abwicklung von Geboten und die Ermittlung der Gewinner. Er sorgt für die Transparenz und Fairness der Auktionsprozesse.

### 2. Property Service

**Funktion**: Verwaltung von Immobilieninformationen.

**Beschreibung**: Der Property Service kümmert sich um die Verwaltung von Immobilienangeboten. Er speichert Informationen zu verfügbaren Immobilien, verwaltet Preise und Verfügbarkeiten und ermöglicht die Anzeige von Immobilienangeboten auf der GentleEstate-Plattform.

### 3. Notification Service

**Funktion**: Verwaltung und Zustellung von Benachrichtigungen.

**Beschreibung**: Dieser Service sorgt dafür, dass Nutzer über wichtige Ereignisse informiert werden. Er sendet Benachrichtigungen über verschiedene Kanäle wie E-Mail, SMS und In-App-Mitteilungen und ermöglicht so eine zeitnahe Information der Nutzer über Bestellstatus, Zahlungserinnerungen und Sonderaktionen.

### 4. Order Service

**Funktion**: Verwaltung von Bestellungen.

**Beschreibung**: Der Order Service ist für das Erstellen, Bearbeiten und Verfolgen von Bestellungen zuständig. Er arbeitet eng mit anderen Diensten zusammen, um den aktuellen Status von Bestellungen zu aktualisieren und sicherzustellen, dass alle Bestellungen ordnungsgemäß abgewickelt werden.

### 5. Inventory Service

**Funktion**: Verwaltung des Lagerbestands.

**Beschreibung**: Dieser Service überwacht und verwaltet den Lagerbestand an Produkten. Er sorgt dafür, dass die Produktverfügbarkeit in Echtzeit aktualisiert wird, um Überbestände oder Fehlbestände zu vermeiden und eine genaue Verfügbarkeit für die Nutzer bereitzustellen.

### 6. Menu Service

**Funktion**: Verwaltung von Menüs und Speisekarten.

**Beschreibung**: Der Menu Service ermöglicht die Erstellung, Aktualisierung und Anpassung von Menüs und Speisekarten, die in verschiedenen Diensten, insbesondere in der GentleFood-Plattform, verwendet werden. Er sorgt dafür, dass die Speisekarten immer aktuell und korrekt sind.

### 7. Payment Service

**Funktion**: Abwicklung von Zahlungen.

**Beschreibung**: Dieser Service ist für die sichere Abwicklung von Zahlungen verantwortlich. Er integriert verschiedene Zahlungsmethoden, um eine zuverlässige und sichere Zahlungsabwicklung für alle Transaktionen innerhalb des Systems zu gewährleisten.

### 8. Customer Service

**Funktion**: Verwaltung von Kundeninformationen und -profilen.

**Beschreibung**: Der Customer Service pflegt und verwaltet die Daten von Kunden. Er unterstützt die Verwaltung von Kundenkontakten, Präferenzen und Interaktionen, um eine personalisierte und effektive Benutzererfahrung zu bieten.

### 9. Account Service

**Funktion**: Verwaltung von Bankkonten.

**Beschreibung**: Der Account Service ermöglicht es Nutzern, ihre Bankkonten zu überprüfen, Transaktionen durchzuführen und Kontobewegungen zu überwachen. Er ist ein wesentlicher Bestandteil des GentleBank-Dienstes und bietet umfassende Bankmanagement-Funktionen.

### 10. Invoice Service

**Funktion**: Erstellung und Verwaltung von Rechnungen.

**Beschreibung**: Dieser Service erstellt und verwaltet Rechnungen für abgeschlossene Transaktionen. Er sorgt für die korrekte Abwicklung und Dokumentation von Rechnungen und stellt sicher, dass alle finanziellen Transaktionen ordnungsgemäß erfasst werden.

### 11. Activity Log Service

**Funktion**: Protokollierung und Verfolgung von Benutzeraktivitäten.

**Beschreibung**: Der Activity Log Service speichert und verfolgt alle relevanten Benutzeraktivitäten innerhalb des Systems. Er ermöglicht die spätere Analyse oder Auditierung von Aktionen, was für Sicherheits- und Compliance-Zwecke wichtig ist.

### 12. Recommendation Service

**Funktion**: Bereitstellung von personalisierten Empfehlungen.

**Beschreibung**: Dieser Service analysiert das Nutzerverhalten und bietet personalisierte Empfehlungen für Produkte, Inhalte oder Dienstleistungen. Er verbessert die Benutzererfahrung, indem er relevante Vorschläge basierend auf den Interessen und Präferenzen der Nutzer bereitstellt.

### 13. Product Service

**Funktion**: Verwaltung des Produktkatalogs.

**Beschreibung**: Der Product Service ist für die Speicherung und Aktualisierung aller Produktinformationen zuständig, die in den verschiedenen Diensten des GentleCorp-Ecosystems angeboten werden. Er stellt sicher, dass der Produktkatalog immer aktuell und genau ist.

### 14. Transaction Service

**Funktion**: Abwicklung und Verfolgung von finanziellen Transaktionen.

**Beschreibung**: Dieser Service kümmert sich um die sichere und effiziente Abwicklung aller finanziellen Transaktionen innerhalb des Systems. Er sorgt dafür, dass alle Zahlungen und Überweisungen korrekt durchgeführt und dokumentiert werden.

### 15. Booking Service

**Funktion**: Verwaltung von Buchungen.

**Beschreibung**: Der Booking Service organisiert und verfolgt alle Buchungen, sei es für Reisen, Hotels oder Aktivitäten. Er ermöglicht es den Nutzern, Buchungen zu tätigen, zu ändern oder zu stornieren und stellt sicher, dass alle Buchungsdetails korrekt verwaltet werden.

### 16. Reviews Service

**Funktion**: Verwaltung von Bewertungen und Rezensionen.

**Beschreibung**: Dieser Service ermöglicht es Nutzern, Bewertungen und Rezensionen für Produkte, Dienstleistungen und Anbieter abzugeben und zu verwalten. Er unterstützt das Feedback-Management und trägt zur Vertrauensbildung innerhalb der Community bei.

### 17. ShoppingCart Service

**Funktion**: Verwaltung des Warenkorbs.

**Beschreibung**: Der ShoppingCart Service verwaltet die Warenkorbfunktionalität, einschließlich des Hinzufügens, Entfernens und Überprüfens von Artikeln. Er ermöglicht eine reibungslose Verwaltung des Warenkorbs vor dem Abschluss einer Bestellung.

### 18. Transport Service

**Funktion**: Organisation und Verwaltung von Transportoptionen.

**Beschreibung**: Der Transport Service organisiert und verwaltet verschiedene Transportoptionen für Nutzer. Er integriert Logistikdienste für den Versand von Bestellungen oder die Buchung von Transportmitteln und sorgt für eine effiziente Logistik.

### 19. Entertainment Service

**Funktion**: Bereitstellung von Unterhaltungsinhalten.

**Beschreibung**: Dieser Service bietet und verwaltet Inhalte zur Unterhaltung, wie Filme, Musik, Spiele und andere Multimedia-Angebote. Er sorgt für eine breite Palette an Unterhaltungsoptionen für die Nutzer.

### 20. Activity Service

**Funktion**: Verwaltung von Freizeitaktivitäten.

**Beschreibung**: Der Activity Service verwaltet Aktivitäten, die von Nutzern geplant oder gebucht werden können. Er unterstützt die Organisation und Buchung von Events, Ausflügen oder anderen Freizeitaktivitäten und sorgt für eine umfassende Planung und Nachverfolgung.

## Kundenkategorien

- **Basic**: Standardzugriff auf die grundlegenden Funktionen und Dienste.
- **Exclusive**: Erweiteter Zugriff mit zusätzlichen Funktionen und personalisiertem Support.
- **Supreme**: Vollständiger Zugriff auf alle Dienste, einschließlich exklusiver Funktionen und Zugang zu GentleAnalytics.

## Datenbanktechnologie für Microservices

Die Auswahl der Datenbanktechnologie für die verschiedenen Microservices im GentleCorp-Ecosystem ist entscheidend für die Performance, Skalierbarkeit und Zuverlässigkeit des Systems. Nachfolgend sind die empfohlenen Datenbanktechnologien für jeden Microservice aufgelistet:

1. **Auction Service**
   - **Technologie**: PostgreSQL
   - **Begründung**: Bietet starke Unterstützung für Transaktionen, ideal für Gebotsmanagement.

2. **Property Service**
   - **Technologie**: MySQL
   - **Begründung**: Hohe Leistung bei der Verarbeitung von strukturierten Daten, ideal für Immobilieninformationen.

3. **Notification Service**
   - **Technologie**: Redis
   - **Begründung**: Geeignet für schnelle, temporäre In-Memory-Datenverarbeitung, ideal für Echtzeit-Benachrichtigungen.

4. **Order Service**
   - **Technologie**: PostgreSQL
   - **Begründung**: Robuste Unterstützung für ACID-Transaktionen, erforderlich für die Verwaltung von Bestellungen.

5. **Inventory Service**
   - **Technologie**: MongoDb
   - **Begründung**: Leistungsstark und stabil, ideal für die Verwaltung von Lagerbeständen.

6. **Menu Service**
   - **Technologie**: MySQL
   - **Begründung**: Effizient für die Verwaltung von strukturierten Daten wie Menüs und Speisekarten.

7. **Payment Service**
   - **Technologie**: PostgreSQL
   - **Begründung**: Hohe Zuverlässigkeit und Unterstützung für Transaktionen, ideal für Finanztransaktionen.

8. **Customer Service**
   - **Technologie**: MySQL

9. **Account Service**
   - **Technologie**: MySQL

10. **Invoice Service**
    - **Technologie**: PostgreSQL
    - **Begründung**: Präzise Datenverfolgung und Speicherung sind erforderlich, was PostgreSQL gut erfüllt.

11. **Activity Log Service**
    - **Technologie**: MongoDB
    - **Begründung**: Eignet sich für die Speicherung großer Mengen an Protokolldaten, die flexibel und schnell zugänglich sein müssen.

12. **Recommendation Service**
    - **Technologie**: Neo4j
    - **Begründung**: Optimal für die Darstellung und Analyse von Netzwerken und Beziehungen zwischen Entitäten.

13. **Product Service**
    - **Technologie**:MongoDb
    - **Begründung**: Bewährte Lösung für die Verwaltung eines umfangreichen Produktkatalogs.

14. **Transaction Service**
    - **Technologie**: PostgreSQL
    - **Begründung**: Stabile Wahl für Finanztransaktionen mit Unterstützung für Transaktionen.

15. **Booking Service**
    - **Technologie**: PostgreSQL
    - **Begründung**: Benötigt eine relationale Datenbank mit starker Unterstützung für Transaktionen und Datenintegrität.

16. **Reviews Service**
    - **Technologie**: MongoDB
    - **Begründung**: Flexibel für das Speichern unstrukturierter Benutzerbewertungen.

17. **ShoppingCart Service**
    - **Technologie**: Redis
    - **Begründung**: Optimal für die schnelle, temporäre Speicherung von Warenkorbdaten.

18. **Transport Service**
    - **Technologie**: MySQL
    - **Begründung**: Effiziente Verwaltung von Transportoptionen durch eine relationale Datenbank.

19. **Entertainment Service**
    - **Technologie**: MongoDB
    - **Begründung**: Geeignet für unstrukturierte Unterhaltungselemente wie Filme und Musik.

20. **Activity Service**
    - **Technologie**: PostgreSQL
    - **Begründung**: Zuverlässig für die Planung und Buchung von Aktivitäten durch die Unterstützung von relationalen Daten.

Diese Empfehlungen basieren auf den spezifischen Anforderungen der jeweiligen Microservices und den Stärken der vorgeschlagenen Datenbanktechnologien.

## Tech Stack

Das GentleCorp-Ecosystem basiert auf einer Vielzahl spezialisierter Microservices, die jeweils spezifische Funktionen erfüllen, um die nahtlose Integration und den reibungslosen Betrieb der Dienste zu gewährleisten. Hier ist eine Übersicht der relevanten Microservices:

### Frontend

- **Framework**: [Next.js](https://nextjs.org/) (using the App Router)
- **Language**: TypeScript
- **Styling**: [Bootstrap](https://getbootstrap.com/)

### Backend

- **Python (FastAPI)**:

  - Notification Service
  - Recommendation Service
  - Activity Log Service
  - Reviews Service
  - Transport Service

- **TypeScript (NestJS)**:

  - Order Service
  - Inventory Service
  - Menu Service
  - Payment Service
  - ShoppingCart Service
  - Auction Service
  - Product Service

- **Java (Spring Boot)**:

  - **Gradle**:

    - Customer Service
    - Account Service
    - Transaction Service
    - Booking Service
    - Invoice Service

  - **Maven**:
    - Property Service
    - Activity Service
    - Entertainment Service

### Technologies Used

- **Datenbanken**: MySQL, PostgreSQL
- **Messaging**: [Kafka](https://kafka.apache.org/)
- **Authentifizierung**: [Keycloak](https://www.keycloak.org/)
- **Verteiltes Tracing**: [Zipkin](https://zipkin.io/)
- **Containerisierung**: [Docker](https://www.docker.com/)
- **Orchestrierung**: [Kubernetes](https://kubernetes.io/)

## Repository Structure

Die Struktur des Repositories ist so gestaltet, dass sie die verschiedenen Komponenten des GentleCorp-Ecosystems klar trennt und die Verwaltung der einzelnen Dienste vereinfacht. Hier ist eine detaillierte Übersicht der Verzeichnisstruktur:

```plaintext
GentleCorp-Ecosystem/
│
├── frontend/                # Frontend-Anwendung (Next.js mit TypeScript)
│   ├── public/              # Statische Assets (z.B. Bilder, Icons)
│   ├── src/                 # Quellcode
│   │   ├── components/      # React-Komponenten
│   │   ├── pages/           # Next.js-Seiten
│   │   ├── styles/          # CSS/Bootstrap-Stile
│   │   └── ...              # Weitere TypeScript-Dateien
│   └── package.json         # Abhängigkeiten und Skripte für das Frontend
│
├── backend/                 # Backend-Dienste
│   ├── common/              # Gemeinsame Bibliotheken und Utilities
│   ├── python/              # Python-basierte Mikroservices
│   │   ├── notification/    # Notification Service (FastAPI)
│   │   ├── recommendation/  # Recommendation Service (FastAPI)
│   │   ├── activity-log/    # Activity Log Service (FastAPI)
│   │   ├── reviews/         # Reviews Service (FastAPI)
│   │   ├── transport/       # Transport Service (FastAPI)
│   │   └── ...              # Weitere Python-basierte Dienste
│   ├── typescript/          # TypeScript-basierte Mikroservices
│   │   ├── order/           # Order Service (NestJS)
│   │   ├── inventory/       # Inventory Service (NestJS)
│   │   ├── menu/            # Menu Service (NestJS)
│   │   ├── payment/         # Payment Service (NestJS)
│   │   ├── shopping-cart/   # ShoppingCart Service (NestJS)
│   │   ├── customer/        # Customer Service (NestJS)
│   │   ├── auction/         # Auction Service (NestJS)
│   │   └── ...              # Weitere TypeScript-basierte Dienste
│   ├── java/                # Java-basierte Mikroservices
│   │   ├── gradle/          # Mit Gradle verwaltete Java-Dienste
│   │   │   ├── account/     # Account Service (Spring Boot)
│   │   │   ├── transaction/ # Transaction Service (Spring Boot)
│   │   │   ├── booking/     # Booking Service (Spring Boot)
│   │   │   └── invoice/     # Invoice Service (Spring Boot)
│   │   ├── maven/           # Mit Maven verwaltete Java-Dienste
│   │   │   ├── property/    # Property Service (Spring Boot)
│   │   │   ├── activity/    # Activity Service (Spring Boot)
│   │   │   └── entertainment/ # Entertainment Service (Spring Boot)
│   │   └── ...              # Weitere Java-basierte Dienste
│
├── docker-compose.yml       # Docker Compose-Datei für die Container-Orchestrierung
├── k8s/                     # Kubernetes-Manifestdateien für die Bereitstellung
└── README.md                # Projektdokumentation


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

5. For Kubernetes deployment, apply the Kubernetes manifests:

   ```bash
   kubectl apply -f k8s/
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

**Python (FastAPI):**

1. Navigate to the specific backend service directory:

   ```bash
   cd backend/python/notification
   cd backend/python/recommendation
   cd backend/python/activity-log
   cd backend/python/reviews
   cd backend/python/transport
   ```

2. Install dependencies (preferably in a virtual environment):

   ```bash
   pip install -r requirements.txt
   ```

3. Start the FastAPI service:

   ```bash
   uvicorn main:app --reload
   ```

## Contributing

Feel free to contribute to the project by creating issues or submitting pull requests.

## License

This project is licensed under the MIT License.

---
