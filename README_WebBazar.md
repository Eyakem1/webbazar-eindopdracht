# WebBazar – Boekverkoop & Verhuur Platform

## Projectstructuur

```
WebBazar/
├── backend/      ← Spring Boot backend
└── frontend/     ← React frontend
```

---

## Backend (Spring Boot)

### Vereisten

- Java 17 of hoger
- Maven
- (optioneel) IntelliJ IDEA / VS Code
- Postman (voor testen van endpoints)
- H2-console (ingeschakeld)

### Project starten

```bash
cd backend
./mvnw spring-boot:run
```

Of gebruik je IDE en start het project via `WebbazarApplication.java`.

#### Alternatief:

1. Project bouwen:
   ```
   mvn clean install
   ```
2. Applicatie starten:
   ```
   mvn spring-boot:run
   ```

De applicatie draait op: `http://localhost:8080`

### Testgebruikers

De volgende gebruikers zijn beschikbaar via `data.sql`:

| Rol     | E-mailadres           | Wachtwoord  |
|---------|------------------------|-------------|
| Admin   | admin@webbazar.com     | Admin123!   |
| User    | user@webbazar.com      | User123!    |
| Test    | test@webbazar.com      | Test123!    |

### H2 Console

- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:testdb`
- Gebruiker: `sa`
- Wachtwoord: *(leeg)*

### API Overzicht

| Methode | Endpoint               | Beschrijving                        |
|---------|------------------------|-------------------------------------|
| POST    | `/auth/login`          | Login en ontvang JWT token          |
| GET     | `/api/products`        | Haal alle boeken op                 |
| POST    | `/api/products`        | Voeg boek toe (alleen admin)        |
| POST    | `/api/orders`          | Plaats een bestelling               |
| GET     | `/api/orders`          | Bekijk eigen bestellingen           |

---

## Frontend (React)

### Vereisten

- Node.js versie 16 of hoger
- npm of yarn geïnstalleerd

### Installatie en starten

```bash
cd frontend
npm install
npm run dev
```

React draait op: [http://localhost:5173](http://localhost:5173)  
Zorg dat de backend actief is op `http://localhost:8080`.

### Belangrijke pagina’s

- `/login` – Inlogpagina
- `/dashboard` – Bestellingsoverzicht
- `/` – Hoofdpagina met boekenlijst en koop/huur opties

### Configuratie

- JWT-token wordt opgeslagen in `localStorage`
- Axios wordt gebruikt voor communicatie met de backend
- Backend is geconfigureerd met CORS-ondersteuning voor `http://localhost:5173`

---

## Bestanden Uploaden (Admins)

- Alleen admins kunnen boeken uploaden
- Upload via `multipart/form-data`:
  - Productgegevens als JSON (`product`)
  - Boekbestand als `file`
- Bestanden worden opgeslagen in de map `uploads/`
- Downloadlinkformaat:  
  `http://localhost:8080/files/{bestandsnaam}`

---

## Testen

Test de applicatie via de frontend of via Postman:

- Test het loginproces
- Plaats bestellingen
- Upload en beheer boeken als admin