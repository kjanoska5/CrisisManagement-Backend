##  Mobilny System Teleinformatyczny do Zarządzania Kryzysowego

> **Praca Inżynierska**
> **Uczelnia:** Politechnika Łódzka
> **Promotor:** dr inż. Paweł Marciniak
> **Autorzy:** Kacper Janoska (backend / serwer) & Marcel Podlecki (aplikacja mobilna / klient)

---

##  O projekcie

System służy do natychmiastowego przekazywania informacji o zagrożeniach, koordynacji działań w sytuacjach kryzysowych oraz sprawny przepływ informacji pomiędzy obywatelami a służbami ratunkowymi.

Aplikacja składa się z dwóch głównych modułów: **modułu dla obywatela** oraz **modułu dla służb** — oba zrealizowane w ramach jednej natywnej aplikacji Android, rozróżniane rolą użytkownika (uzasadnienie wyboru: [ADR-0001](docs/adr/0001-wybor-stosu-technologicznego.md)).

Aktualny, bieżący postęp prac śledzony jest na bieżąco w zakładkach **Issues** i **Milestones** tego repozytorium.

---

##  Makieta

https://www.figma.com/design/arjRIcCKMXEpYhwgz7Xjv1/Crisis-Management---Makieta?node-id=8-2&t=E7XJvaYB4f4fLV5l-1
Ścieżka do makiety w repozytorium docs/

---

##  Funkcjonalności

###  Moduł Obywatela (Android App)
**Zrealizowane:**
-  **Zgłaszanie zdarzeń:** zdjęcie, automatycznie pobrana lokalizacja GPS, wybór kategorii zagrożenia z listy, opcjonalny komentarz
-  **Interaktywna mapa zagrożeń:** pinezki kolorowane wg statusu (czerwony — aktywne, żółty — weryfikowane, zielony — rozwiązane), szczegóły zgłoszenia po kliknięciu, automatyczne odświeżanie w czasie zbliżonym do rzeczywistego
-  **Karta medyczna:** utworzenie, edycja i usunięcie profilu zdrowotnego (grupa krwi, alergie, przyjmowane leki, choroby przewlekłe, kontakt alarmowy); dane wrażliwe szyfrowane po stronie backendu

**Planowane:**
-  **Szybkie wezwanie pomocy (SOS):** przytrzymanie przycisku wysyła lokalizację GPS wraz z zapisaną kartą medyczną
-  **Alerty kryzysowe:** powiadomienia push wybudzające telefon, z sygnałem dźwiękowym i pop-upem
-  **Nawigacja do punktu zbiórki:** najkrótsza trasa do bezpiecznej strefy ewakuacyjnej po alercie
-  **Tryb offline:** lokalny zapis zgłoszeń przy braku zasięgu i automatyczna synchronizacja po odzyskaniu połączenia
-  **Poradniki offline:** instrukcje pierwszej pomocy i postępowania w sytuacjach kryzysowych, dostępne bez internetu
-  **Tryb oszczędzania zasobów:** przesyłanie wyłącznie priorytetowych danych (tekst + GPS) bez zdjęć

###  Moduł dla Służb & Algorytmy Serwerowe
**Zrealizowane:**
-  **Mapa zagrożeń** — ten sam, współdzielony komponent co w module obywatela
**Planowane:**
-  **Nanoszenie i weryfikacja zagrożeń:** zarządzanie statusem zgłoszenia (endpoint na backendzie już gotowy, interfejs w aplikacji w trakcie realizacji)
-  **System wiarygodności zgłoszeń:** automatyczne podnoszenie priorytetu przy wielu niezależnych zgłoszeniach tego samego zdarzenia w niewielkim promieniu
-  **Śledzenie jednostek ratunkowych:** pozycje służb widoczne na mapie obywatela

> Logowanie obecnie rozróżnia jedynie rolę (obywatel / służby) jako mechanizm tymczasowy — pełna autoryzacja użytkowników jest zaplanowana jako osobny etap prac.

---

##  Stos Technologiczny

**Zrealizowane:**
- **Aplikacja Mobilna:** Kotlin, Jetpack Compose, Google Maps SDK, Retrofit
- **Backend:** Java, Spring Boot, Maven
- **Baza Danych:** PostgreSQL + PostGIS (obliczenia geoprzestrzenne), uruchamiana przez Docker
- **Bezpieczeństwo:** AES/GCM — szyfrowanie pól wrażliwych karty medycznej na poziomie bazy danych

**Planowane:**
- **Powiadomienia:** Firebase Cloud Messaging (FCM)
- **Dane offline:** lokalna baza Room po stronie aplikacji Android

Pełne uzasadnienie wyboru technologii: [docs/adr/0001-wybor-stosu-technologicznego.md](docs/adr/0001-wybor-stosu-technologicznego.md)

---

##  Uruchomienie projektu

### Wymagania wstępne
- Docker Desktop
- JDK 17+
- Android Studio (najnowsza stabilna wersja) + Android SDK
- Klucz API do Google Maps SDK for Android (Google Cloud Console)

### 1. Baza danych
W głównym katalogu repozytorium:
```bash
docker compose up -d
```
Uruchamia PostgreSQL z rozszerzeniem PostGIS na porcie `5432` (konfiguracja: `docker-compose.yml`). W razie konfliktu portu na danym komputerze, port można nadpisać lokalnie zmienną środowiskową `DB_PORT`, bez modyfikowania pliku współdzielonego w repozytorium.

### 2. Backend
```bash
cd backend
./mvnw spring-boot:run
```
Backend startuje na porcie `8080` i łączy się z bazą z kroku 1. Przy pierwszym uruchomieniu automatycznie wstawiane są dane testowe (przykładowy użytkownik i kategorie zagrożeń).

Zmienna środowiskowa `ENCRYPTION_KEY` (szyfrowanie karty medycznej) ma ustawioną bezpieczną wartość domyślną wyłącznie do developmentu (`application.yml`) — do nadpisania na docelowym środowisku.

### 3. Aplikacja Android
1. Otwórz katalog `android/` w Android Studio.
2. W pliku `android/local.properties` dodaj:
   ```
   MAPS_API_KEY=twój_klucz_api
   ```
3. Zsynchronizuj Gradle i uruchom na emulatorze lub urządzeniu fizycznym.

> Backend (krok 2) musi działać lokalnie, żeby aplikacja mogła się z nim komunikować. Na emulatorze Androida adres `10.0.2.2` wskazuje na `localhost` komputera-hosta — to oczekiwane zachowanie, nie błąd konfiguracji.

### 4. Interfejs Dyspozytora (Swagger UI)
System udostępnia interaktywną dokumentację API (Swagger UI), która pełni funkcję panelu dla dyspozytorów. Pozwala na przeglądanie zgłoszeń i zmianę ich statusu bezpośrednio z poziomu przeglądarki na serwerze produkcyjnym.

1. Otwórz w przeglądarce panel dyspozytora: `https://crisis-backend.denmarkeast.cloudapp.azure.com/swagger-ui.html`
2. Aby pobrać ID konkretnego zgłoszenia, rozwiń sekcję `GET /api/reports` i kliknij **Try it out** -> **Execute**.
3. Aby zmienić status zgłoszenia, rozwiń `PATCH /api/reports/{id}/status` i kliknij **Try it out**.
4. Wklej ID zgłoszenia, wybierz z listy nowy status (np. `IN_PROGRESS`, `VERIFIED`, `RESOLVED`) i kliknij **Execute**. 

Zmiana natychmiast zaktualizuje rekord w chmurowej bazie danych na serwerze Azure, a nowy status (wraz z odpowiednim kolorem pinezki) zaktualizuje się automatycznie u wszystkich użytkowników aplikacji mobilnej.

---

##  Struktura Repozytorium
```text
crisis-management-system/
├── android/                 # Kod źródłowy aplikacji mobilnej (Kotlin, Jetpack Compose)
├── backend/                 # Kod źródłowy serwera i API (Spring Boot)
├── docs/                    # Dokumentacja projektowa
│   ├── adr/                  # Decyzje architektoniczne (Architecture Decision Records)
│   ├── architecture.md       # Diagram komponentów i kluczowych przepływów danych
│   └── database-schema.md    # Schemat bazy danych (ERD)
├── docker-compose.yml       # Konfiguracja lokalnej bazy danych i chmurowego środowiska (Caddy)
└── Caddyfile                # Konfiguracja serwera Caddy (HTTPS i Reverse Proxy) dla Azure
```

Przełączanie środowiska (Android)
Adres API jest konfigurowany przez buildConfigField w android/app/build.gradle.kts:

| Wariant | Adres | Użycie |
|---|---|---|
| debug | `http://10.0.2.2:8080/` (emulator + lokalny backend) | codzienny development |
| release | `https://crisis-backend.denmarkeast.cloudapp.azure.com/` (serwer Azure, Let's Encrypt TLS) | wersja dla prowadzących |

Aby w wariancie debug połączyć się z serwerem produkcyjnym (np. z Azure z telefonu), dopisz w android/local.properties (plik nie jest w repozytorium):

API_BASE_URL=https://crisis-backend.denmarkeast.cloudapp.azure.com/


Następnie wykonaj Sync i uruchom aplikację ponownie.


## Logowanie przez Google (Android): konfiguracja

1. Poproś o **Web Client ID** (klient OAuth typu "Web application" w projekcie Google Cloud). To ten sam identyfikator, który backend ma w zmiennej środowiskowej `GOOGLE_CLIENT_ID`. Nie commitujemy go do repozytorium.
2. Dopisz go w `android/local.properties`:
   `GOOGLE_WEB_CLIENT_ID=<id>.apps.googleusercontent.com`
   Aplikacja czyta go przez `BuildConfig.GOOGLE_WEB_CLIENT_ID` (klasa `GoogleAuthConfig`).
3. **Odcisk SHA-1 na nowym komputerze:**
   - w folderze `android` uruchom `.\gradlew signingReport` i skopiuj `SHA1` z wariantu `debug`,
   - w Google Cloud Console (APIs & Services → Credentials) utwórz klienta OAuth typu **Android** z pakietem `com.example.crisismanagementapp` i tym odciskiem (każdy komputer ma własny klucz debug, więc każdy potrzebuje osobnego klienta; wymaga to uprawnień w projekcie),
   - dla wersji release używamy osobnego klienta z odciskiem klucza release.
4. Emulator musi mieć obraz z **Google Play** i zalogowane konto Google, które jest na liście Test users na ekranie zgody OAuth.