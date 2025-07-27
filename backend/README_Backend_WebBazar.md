# WebBazar – Backend (Spring Boot)

Dit is de backend van de WebBazar applicatie. De backend is gebouwd met Spring Boot en biedt RESTful API-endpoints voor gebruikersauthenticatie, productbeheer, bestellingen en bestandshandelingen (upload/download van e-books).

## Vereisten

- Java 17 of hoger
- Maven
- (optioneel) IntelliJ IDEA / VS Code
- Postman voor testen
- H2-console (ingeschakeld voor lokale database-inzicht)

## Project starten

Via terminal:

```bash
cd backend
./mvnw spring-boot:run
```

Of via IDE (start `WebbazarApplication.java`)

Alternatief via Maven:

```bash
mvn clean install
mvn spring-boot:run
```

Toegang via: [http://localhost:8080](http://localhost:8080)

## H2 Console

- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:testdb`
- Gebruikersnaam: `sa`
- Wachtwoord: *(leeg)*

## Beschikbare Testgebruikers (uit `data.sql`)

| Rol     | Email                | Wachtwoord  |
|---------|-----------------------|-------------|
| Admin   | admin@webbazar.com    | Admin123!   |
| User    | user@webbazar.com     | User123!    |
| Test    | test@webbazar.com     | Test123!    |

## API Endpoints

| Methode | Endpoint             | Beschrijving                            |
|---------|----------------------|-----------------------------------------|
| POST    | `/auth/login`        | Inloggen en ontvangen van JWT token     |
| GET     | `/api/products`      | Alle boeken ophalen                     |
| POST    | `/api/products`      | Nieuw boek toevoegen (alleen admin)     |
| POST    | `/api/orders`        | Bestelling plaatsen (user)              |
| GET     | `/api/orders`        | Bestelgeschiedenis ophalen              |
| GET     | `/files/{bestandsnaam}` | Boek downloaden                        |

## Upload Functionaliteit

- Admins kunnen boeken uploaden met:
  - JSON (via veld `product`)
  - Bestand (via veld `file`)
- Upload via `multipart/form-data`
- Bestanden worden lokaal opgeslagen in `uploads/`

## Beveiliging

- JWT authenticatie
- Role-based autorisatie
- CORS ingesteld op `http://localhost:5173`
- Alleen admins kunnen producten beheren

## Testen

Gebruik Postman of frontend om te testen:

- Inloggen via `/auth/login`
- Bestelling plaatsen via `/api/orders`
- Boeken toevoegen (als admin) via `/api/products`