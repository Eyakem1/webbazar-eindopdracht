## Overzicht  
WebBazar is een webapplicatie voor het kopen en huren van e-books. De backend bevat authenticatie, gebruikers-, product- en orderbeheer. De frontend biedt een gebruiksvriendelijke webshop met rolgebaseerde functionaliteit.

---

## Installatie & Starten

### Backend starten (Spring Boot)
```bash
cd backend
mvn clean install
mvn spring-boot:run
Backend draait op:
http://localhost:8080

Frontend starten (React)
bash
Code kopiëren
cd frontend
npm install
npm run dev
Frontend draait op:
http://localhost:5173

Testgebruikers
Rol	Email	Wachtwoord
Admin	admin@webbazar.com	Admin123!
User	user@webbazar.com	User123!
Test	test@webbazar.com	Test123!

Belangrijkste Functionaliteiten
Voor gebruikers
Inloggen / registreren (JWT)

Boeken bekijken & zoeken

Bestellen: kopen of huren

Factuur downloaden (PDF)

XML download

E-book downloaden

Profiel wijzigen (naam, adres, wachtwoord)

Eigen bestelgeschiedenis bekijken

Voor admin
Alle gebruikers zien

Nieuwe gebruiker aanmaken

Gebruiker bewerken / blokkeren / verwijderen

Wachtwoord resetten

Producten toevoegen (JSON + PDF upload)

Producten verwijderen

Orders van alle gebruikers bekijken

Backend API (kort)
Authenticatie
POST /auth/login

GET /api/auth/me

Producten
GET /api/products

POST /api/products (admin)

DELETE /api/products/{id} (admin)

Orders
POST /api/orders/checkout

GET /api/orders

GET /api/orders/{id}

GET /api/orders/{id}/invoice

GET /api/downloads/{orderItemId}

Admin endpoints
GET /api/admin/users

POST /api/admin/users

PUT /api/admin/users/{id}

PATCH /api/admin/users/{id}/enabled

DELETE /api/admin/users/{id}

Frontend Pagina’s
/ – Home / productenoverzicht

/products/:id – Productdetail + koop/huur

/login – Inloggen

/register – Registreren

/dashboard – Gebruikersdashboard

/orders – Mijn bestellingen

/admin – Admin-dashboard

/admin/products – Productbeheer

/admin/users – Gebruikersbeheer

Technische Implementatie
Backend
Spring Boot

Spring Security + JWT

H2 database (file-based)

Upload & opslag van PDF e-books

PDF-generatie voor facturen

Frontend
React function components

Context API (AuthContext / CartContext)

Routing via react-router-dom

Axios API-communicatie

Blob file download handling (PDF/XML/e-books)

Werking in praktijk
Gebruiker logt in

Ontvangen JWT wordt in localStorage opgeslagen

API-requests bevatten Authorization: Bearer <token>

UI past zich aan op basis van rol

Admin: beheerfuncties

User: eigen profiel en orders

Bij checkout genereert backend:

Order record

Download permissies

Factuur (PDF)

XML-orderbestand

Testtips
✔ Inloggen werkt
✔ Zoeken werkt
✔ Checkout werkt
✔ Factuur downloaden werkt
✔ Boeken downloaden werkt
✔ Gebruikersbeheer werkt (admin)
✔ Productbeheer werkt (admin)

Eindresultaat
WebBazar is een volledig functionele webapplicatie waarin frontend en backend veilig samenwerken. Het systeem ondersteunt gebruikersbeheer, digitale producten, bestellingen en documentgeneratie en maakt gebruik van moderne webstandaarden.


