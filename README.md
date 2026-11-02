# Backend Architecture for Crisis Management System

> **Projekt Inżynierski (Część Serwerowa)**
> **Autor:** Kacper Janoska (Backend Developer & Cloud Infrastructure)
> 
> *Pełny projekt zrealizowano w zespole dwuosobowym. Aplikacja kliencka (Android/Kotlin) znajduje się w osobnym repozytorium.*

---

## O projekcie
Niniejsze repozytorium zawiera architekturę serwerową oraz bazodanową systemu do zarządzania kryzysowego. System służy do natychmiastowego przekazywania informacji o zagrożeniach, przetwarzania danych geoprzestrzennych oraz sprawnego przepływu informacji pomiędzy obywatelami a służbami ratunkowymi w czasie rzeczywistym.

## Kluczowe Funkcjonalności API
* **Przetwarzanie danych geoprzestrzennych (PostGIS):** Zapis i odczyt lokalizacji GPS dla zgłoszeń, optymalizacja zapytań przestrzennych dla interaktywnej mapy zagrożeń.
* **Zarządzanie zgłoszeniami:** Endpointy RESTful obsługujące pełny cykl życia zdarzenia (od zgłoszenia po weryfikację i rozwiązanie przez służby).
* **Szyfrowanie danych wrażliwych (AES/GCM):** Kryptograficzne zabezpieczenie po stronie bazy danych pól takich jak profil zdrowotny, grupa krwi, alergie czy przyjmowane leki (Karta Medyczna).
* **Autoryzacja (Sprint 2):** Rejestracja i logowanie tradycyjne (lokalne) z wykorzystaniem hashowania haseł BCrypt oraz uwierzytelnianie Google OAuth2.

## Stos Technologiczny
* **Język i framework:** Java 17, Spring Boot, Maven
* **Baza danych:** PostgreSQL + PostGIS (uruchamiana przez Docker)
* **Bezpieczeństwo:** Spring Security, szyfrowanie AES/GCM (Karta Medyczna)
* **Dokumentacja API:** Swagger UI / Springdoc OpenAPI
* **Wdrożenie i chmura:** Microsoft Azure (Virtual Machine), Docker Compose, Caddy (Reverse Proxy, automatyczne certyfikaty Let's Encrypt TLS)

---

## Uruchomienie projektu (Lokalnie)

### Wymagania wstępne
* Docker Desktop
* JDK 17+

### 1. Baza danych
W głównym katalogu repozytorium:
```bash
docker compose up -d
```
Uruchamia PostgreSQL z rozszerzeniem PostGIS na porcie `5432` (konfiguracja w `docker-compose.yml`).

### 2. Serwer Backendowy
```bash
cd backend
./mvnw spring-boot:run
```
Backend startuje na porcie `8080` i łączy się z bazą z kroku 1. Przy pierwszym uruchomieniu automatycznie wstawiane są dane testowe i schematy tabel.

### 3. Interfejs Dyspozytora / Dokumentacja API (Swagger UI)
System udostępnia interaktywną dokumentację API (Swagger UI), która pełni funkcję deweloperskiego panelu dla dyspozytorów. Pozwala na testowanie autoryzacji, przeglądanie zgłoszeń i zmianę ich statusu.
Dostęp po uruchomieniu lokalnym: `http://localhost:8080/swagger-ui.html`