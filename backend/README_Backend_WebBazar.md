# WebBazar – Backend (Spring Boot)

Dit is de backend van de WebBazar applicatie. De backend is gebouwd met Spring Boot en bevat alle server-functionaliteit voor authenticatie, gebruikersbeheer, productbeheer, bestellingen en het downloaden/uploaden van e-books.  
Deze backend wordt gebruikt door de React frontend van de WebBazar webshop.

---

## Vereisten
- Java 17+
- Maven
- Postman (voor API testen)
- H2 database (meegeleverd, geen installatie nodig)

---

## Project starten

Via terminal:

    mvn spring-boot:run

Of:

    mvn clean install && mvn spring-boot:run

Of via IDE:
- Run `WebBazarApplication.java`

Backend draait op:
- http://localhost:8080

---

# Testen via Postman

Gebruik:
- `WebBazar.postman_collection.json`
- `WebBazar.postman.environment.json`

Deze bestanden bevatten:
✔ alle API endpoints  
✔ automatische JWT-token verwerking  
✔ admin + user workflows  
✔ voorbeeld request payloads  

---

## 👥 Testgebruikers

| Rol     | Email                | Wachtwoord  |
|---------|----------------------|-------------|
| Admin   | admin@webbazar.com   | Admin123!   |
| User    | user@webbazar.com    | User123!    |
| Test    | test@webbazar.com    | Test123!    |

---

## 🔐 Authenticatie (JWT)
- Gebruiker logt in via `/auth/login`
- Backend genereert een JWT token
- Frontend of Postman stuurt die mee in headers:


Token bevat o.a.:
- user id
- e-mailadres
- rol (USER of ADMIN)

---

## 👤 Gebruikersprofiel Endpoints

| Methode | Endpoint              | Beschrijving                                  |
|---------|-----------------------|-----------------------------------------------|
| GET     | `/api/auth/me`        | Eigen profiel ophalen                         |
| PUT     | `/api/auth/me`        | Eigen naam, adres en wachtwoord aanpassen     |

---

## Product Endpoints

| Methode | Endpoint         | Beschrijving                                   |
|---------|------------------|------------------------------------------------|
| GET     | `/api/products`  | Alle beschikbare boeken ophalen                |
| GET     | `/api/products/{id}` | Boekdetails ophalen                         |
| POST    | `/api/products`  | Nieuw boek toevoegen + PDF upload (admin)     |
| DELETE  | `/api/products/{id}` | Product verwijderen (admin)                 |

**Upload via multipart:**
- `product` → JSON
- `file` → PDF bestand

PDF-bestanden worden opgeslagen in:


---

## Bestel Endpoints

| Methode | Endpoint                         | Beschrijving                       |
|---------|----------------------------------|------------------------------------|
| POST    | `/api/orders/checkout`           | Bestelling plaatsen (koop of huur) |
| GET     | `/api/orders`                    | Eigen bestelhistorie ophalen       |
| GET     | `/api/orders/{id}`               | Details van één bestelling         |
| GET     | `/api/orders/{id}/invoice`       | PDF factuur downloaden             |
| GET     | `/api/downloads/{orderItemId}`   | E-book downloaden (indien toegestaan) |

---

## 🛠️ Admin Endpoints

| Methode | Endpoint                         | Beschrijving                           |
|---------|----------------------------------|----------------------------------------|
| GET     | `/api/admin/users`               | Alle gebruikers ophalen                 |
| POST    | `/api/admin/users`               | Nieuwe gebruiker aanmaken               |
| GET     | `/api/admin/users/{id}`          | Specifieke gebruiker opvragen           |
| PUT     | `/api/admin/users/{id}`          | Gebruiker aanpassen (incl. password)   |
| PATCH   | `/api/admin/users/{id}/enabled`  | Gebruiker blokkeren / deblokkeren      |
| DELETE  | `/api/admin/users/{id}`          | Gebruiker verwijderen                  |

---

#  H2 database console (optioneel)

    http://localhost:8080/h2-console

JDBC:
- jdbc:h2:file:./data/webbazar  
User:
- sa  
Pass:
- *(leeg)*

Database bevat o.a. tabellen:
- users  
- roles  
- products  
- orders  
- order_items  

Bij eerste start worden demo gegevens geladen via:


---

#  Functionaliteiten van de backend

###  Authenticatie
- JWT token authenticatie  
- rolgebaseerde autorisatie  
- eigen profiel ophalen  
- wachtwoord en accountgegevens aanpassen  

###  Gebruikersbeheer
- Admin kan alle gebruikers beheren  
- User kan alleen zijn eigen profiel beheren  

### Productbeheer
- Admin kan producten toevoegen en verwijderen  
- PDF upload voor digitale boeken  
- Metadata in JSON  
- Bestanden opgeslagen in `/uploads/`  

### Bestellingen
- kopen & huren  
- orderregistratie  
- orderitems  
- automatische ID’s  
- factuur in PDF  
- downloadlinks voor e-books  

###  Database
- H2 file-based database  
- data persistent tussen herstarts  
- lokale opslag in `/data/`  

### Security
- Spring Security  
- JWT filtering  
- CORS configuratie voor frontend  
- alleen geautoriseerde endpoints toegankelijk  

---

#  Verwacht gedrag

- backend start direct  
- demo data beschikbaar  
- ID sequencing correct  
- Postman requests falen niet  
- security werkt correct  
- rechten worden afgedwongen  
- downloads worden veilig aangeboden  
- admin kan meer endpoints gebruiken dan user  

---

##  Opmerking

Voor volledige API demonstratie:
 gebruik de Postman collectie  
Deze bevat automatisch:
- login flow  
- token opslag  
- correct ingestelde headers  
- body voorbeelden  
- voorbeeld responses  

