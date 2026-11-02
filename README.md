Backend Architecture for Crisis Management System
> **Projekt Inżynierski (Część Serwerowa)**
> **Autor:** Kacper Janoska (Backend Developer & Cloud Infrastructure)
> **Pełny projekt zrealizowano w zespole dwuosobowym:** Aplikacja kliencka (Android/Kotlin) znajduje się w osobnym repozytorium.
---
O projekcie
Niniejsze repozytorium zawiera architekturę serwerową oraz bazodanową systemu do zarządzania kryzysowego. System służy do natychmiastowego przekazywania informacji o zagrożeniach, przetwarzania danych geoprzestrzennych oraz sprawnego przepływu informacji pomiędzy obywatelami a służbami ratunkowymi w czasie rzeczywistym.
---
Kluczowe Funkcjonalności API
Przetwarzanie danych geoprzestrzennych (PostGIS): Zapis i odczyt lokalizacji GPS dla zgłoszeń, optymalizacja zapytań przestrzennych dla mapy zagrożeń.
Zarządzanie zgłoszeniami: Endpointy RESTful obsługujące pełny cykl życia zdarzenia (od zgłoszenia po weryfikację i rozwiązanie przez służby).
Szyfrowanie danych wrażliwych (AES/GCM): Kryptograficzne zabezpieczenie po stronie bazy danych pól takich jak profil zdrowotny, grupa krwi, alergie czy przyjmowane leki (Karta Medyczna).
Autoryzacja (Sprint 2):
Rejestracja i logowanie tradycyjne (lokalne) z wykorzystaniem hashowania haseł BCrypt.
Uwierzytelnianie Google OAuth2 poprzez weryfikację `idToken`.
Rozróżnianie ról (CITIZEN / SERVICES).
Interfejs Dyspozytora: Interaktywna dokumentacja Swagger UI pełniąca funkcję panelu operacyjnego dla służb.
---
Stos Technologiczny
Język i Framework: Java 17, Spring Boot, Maven
Baza Danych: PostgreSQL + rozszerzenie PostGIS
Zabezpieczenia: Spring Security (BCrypt), Google API Client (OAuth2)
Infrastruktura i Wdrożenie: Docker, Docker Compose, maszyna wirtualna Microsoft Azure, reverse proxy Caddy (certyfikaty Let's Encrypt TLS)
Dokumentacja API: OpenAPI 3.0 / Swagger UI
---
Uruchomienie projektu (Środowisko Lokalne)
Wymagania wstępne
Docker Desktop
JDK 17+
1. Baza danych (Docker)
W głównym katalogu repozytorium znajduje się plik konfiguracyjny. Uruchom kontener poleceniem:
```bash
docker compose up -d
```
Uruchamia to silnik PostgreSQL z rozszerzeniem PostGIS na porcie `5432`.
2. Serwer Spring Boot
```bash
cd backend
./mvnw spring-boot:run
```
Backend startuje na porcie `8080` i automatycznie łączy się z bazą danych z kroku 1. Przy pierwszym uruchomieniu mechanizm Hibernate/Flyway (w zależności od konfiguracji) przygotuje struktury tabel.
> **Uwaga dot. bezpieczeństwa:** Zmienna środowiskowa `ENCRYPTION_KEY` (szyfrowanie karty medycznej) ma ustawioną testową wartość w `application.yml` wyłącznie na potrzeby lokalnego developmentu. W środowisku produkcyjnym Azure jest ona nadpisywana.
3. Panel Dyspozytora / Dokumentacja API (Swagger UI)
System udostępnia automatycznie generowaną dokumentację, z poziomu której można testować autoryzację oraz zarządzać zgłoszeniami:
Lokalnie: `http://localhost:8080/swagger-ui.html`
Produkcyjnie (Azure): `https://crisis-backend.denmarkeast.cloudapp.azure.com/swagger-ui.html`
Aby przetestować zmianę statusu zgłoszenia:
Pobierz ID zgłoszenia (`GET /api/reports`).
Użyj endpointu `PATCH /api/reports/{id}/status`.
Wklej ID, ustaw status na np. `IN_PROGRESS` i wykonaj zapytanie.
---
Struktura Repozytorium
```text
CrisisManagement-Backend/
├── backend/                 # Kod źródłowy aplikacji Spring Boot
├── docs/                    # Dokumentacja projektowa i decyzje architektoniczne
├── docker-compose.yml       # Konfiguracja środowiskowa bazy danych
└── Caddyfile                # Reguły reverse proxy (HTTPS) dla wdrożenia Azure
```