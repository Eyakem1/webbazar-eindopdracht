# WebBazar – Frontend (React)

Dit is de frontend van de WebBazar applicatie, gebouwd met React. De frontend biedt gebruikers en admins de mogelijkheid om in te loggen, boeken te bekijken, kopen of huren, en beheert de gebruikersinterface voor de applicatie.

## Vereisten

- Node.js (versie 16 of hoger)
- npm of yarn

## Project starten

```bash
cd frontend
npm install
npm run dev



| Rol     | E-mailadres           | Wachtwoord  |
|---------|------------------------|-------------|
| Admin   | admin@webbazar.com     | Admin123!   |
| User    | user@webbazar.com      | User123!    |
| Test    | test@webbazar.com      | Test123!    |

### H2 Console
```

De applicatie draait op: [http://localhost:5173](http://localhost:5173)  
De backend moet draaien op: [http://localhost:8080](http://localhost:8080)

## Pagina’s

- `/login` – Inlogpagina voor alle gebruikers
- `/register` – Registratieformulier
- `/dashboard` – Persoonlijk overzicht van bestellingen
- `/` – Hoofdpagina met boekenlijst (met koop/huur opties)
- `/admin` – Alleen zichtbaar voor admins (boekbeheer)

## Belangrijke Functionaliteiten

- Inloggen met JWT (token opgeslagen in `localStorage`)
- Authenticatie en autorisatie via React Context
- API-aanroepen met Axios
- Foutafhandeling met try/catch
- Automatische doorverwijzing bij ongeldig token

## Bestanden

- Axios is ingesteld op basis-URL: `http://localhost:8080`
- Uploaden van boeken (door admins) gebeurt via `multipart/form-data`
- Downloadlinks worden opgehaald vanuit de backend

## Configuratie

- JWT wordt opgeslagen in `localStorage`
- CORS is correct geconfigureerd in backend om frontend toegang te geven
- Gebruik van `react-router-dom` voor pagina-navigatie
- Condities op basis van user roles (admin / user)

## Testen

- Test frontend door in te loggen met bestaande testgebruikers
- Bekijk de reactie op succesvolle of mislukte logins
- Controleer of de juiste componenten worden geladen afhankelijk van de gebruikersrol
- Controleer of boeken correct worden weergegeven en interacties goed verlopen