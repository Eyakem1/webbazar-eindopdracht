# WebBazar – Frontend (React)

Dit is de frontend van de WebBazar applicatie, gemaakt met React. De frontend zorgt voor de interactie met de gebruiker: inloggen, boeken bekijken, kopen of huren, profiel beheren, bestellingen inzien en (als admin) gebruikers en producten beheren.  


---

##  Vereisten

- Node.js 16+
- npm of yarn
- De backend moet draaien op: http://localhost:8080

---

##  Project starten

```bash
cd frontend
npm install
npm run dev
Frontend draait vervolgens op:
http://localhost:5173

 Testgebruikers
Rol	E-mailadres	Wachtwoord
Admin	admin@webbazar.com	Admin123!
User	user@webbazar.com	User123!
Test	test@webbazar.com	Test123!

## Pagina’s
/ – Startpagina met aanbevolen boeken

/products – Boekenlijst + zoekfunctie

/products/:id – Productdetail + koop/huur functionaliteit

/login – Inloggen

/register – Registreren

/dashboard – Profiel en eigen bestelgeschiedenis

/orders – Besteloverzicht

/orders/:id – Orderdetail

/admin – Admin-dashboard

/admin/products – Productbeheer

/admin/users – Gebruikersbeheer

/admin/orders – Alle bestellingen (admin only)

## Authenticatie
Inloggen via backend /auth/login

JWT wordt opgeslagen in localStorage

AuthContext beheert de user state en rol

UI past zich aan:

admin ziet admin-opties

user alleen zijn eigen gegevens

Bij ongeldig token → automatisch uitloggen of redirect naar login

## Functionaliteiten
Voor gebruikers:
Inloggen / registreren

Boeken bekijken

Boeken zoeken op titel / auteur

Bestellen via winkelmand

Boeken kopen OF huren (met huurperiode)

Factuur downloaden (PDF)

Orderbestand downloaden (XML)

Gekochte/gehuurde e-books downloaden

Profiel aanpassen (naam, adres, wachtwoord)

Overzicht van eigen bestellingen in dashboard

Voor admin:
Overzicht van alle gebruikers

Nieuwe gebruikers aanmaken

Gebruikers bewerken & blokkeren

Nieuw wachtwoord instellen voor gebruiker

Gebruikers verwijderen

Producten toevoegen (multipart upload: JSON + PDF/PNG)

Producten verwijderen

Alle bestellingen van alle gebruikers bekijken

Status / informatie per bestelling controleren

## Winkelmand & Checkout
Toevoegen via productdetailpagina

BUY en RENT logica

Niet mogelijk om koop- en huuritems tegelijk te bestellen

Checkout stuurt order naar backend

Backend retourneert order, waarna:

factuur automatisch downloadt

XML downloadt

downloads voor orderitems worden gestart

Fallback bij backend-fout
Als checkout geen response geeft:

Frontend maakt zelf een orderobject

Genereert eigen concept-factuur (PDF)

Genereert eigen concept-XML

Items worden uit winkelmand verwijderd

## Structuur & logica
React function components

Routing via react-router-dom

API-requests met Axios

AuthContext → gebruiker & rolbeheer

CartContext → winkelmand logica

Gebruik van componenten zoals:

ProductCard

ProductDetail

Orders

OrderDetail

UserDashboard

AdminProducts

AdminUsers

AdminOrders

Per-pagina specifieke CSS (zoals Orders.css, Admin.css, etc.)

## Backend communicatie
Basis-URL in Axios:

js
Code kopiëren
http://localhost:8080
Headers worden automatisch toegevoegd:

http
Code kopiëren
Authorization: Bearer <token>
Files downloaden als BLOB:

js
Code kopiëren
responseType: "blob"
Bij error:

melding op scherm

geen crash

## Testen
Suggesties om te testen:

✔ Inloggen werkt met admin & user
✔ Producten worden geladen en getoond
✔ Zoekbalk filtert lijst correct
✔ Productdetailpagina werkt
✔ Winkelmand kan items toevoegen/verwijderen
✔ Checkout werkt
✔ Factuur downloaden werkt
✔ XML downloaden werkt
✔ Downloaden van e-books werkt
✔ Dashboard werkt
✔ Profiel wijzigen werkt
✔ Admin ziet extra opties
✔ Admin-productbeheer werkt
✔ Admin-gebruikersbeheer werkt
✔ Admin kan orders zien van alle gebruikers


## Reflectie op het project
Tijdens dit project heb ik gewerkt met:

React & Hooks

Context API

JWT-auth met frontend opslag

PDF-generatie aan clientzijde

XML-generatie aan clientzijde

Communicatie met een Spring Boot backend

Rolgebaseerde UI

Bestandsdownloads en blob responses

Samenvatting
Deze frontend vormt de gebruikersinterface voor het WebBazar platform.
Gebruikers kunnen boeken kopen of huren, bestellingen beheren en downloads uitvoeren.
Admins hebben extra beheeropties voor zowel gebruikers als producten.
Het resultaat is een volledig functionele webapplicatie die frontend en backend combineert in één systeem.

