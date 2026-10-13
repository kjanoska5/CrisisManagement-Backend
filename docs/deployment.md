# Wdrożenie produkcyjne (Azure)
- **Serwer:** Maszyna wirtualna Ubuntu (Azure)
- **Konteneryzacja:** Docker Compose
- **Baza danych:** PostgreSQL + PostGIS 
- **Zmienne środowiskowe:** Hasła i `ENCRYPTION_KEY` są wstrzykiwane bezpiecznie przez lokalny plik `.env` na serwerze.
- **Dostęp:** Aplikacja działa publicznie na porcie 80. Odpytywanie API nie wymaga już dopisywania portu 8080.
-**Adres:** [http://9.205.17.123](http://9.205.17.123)