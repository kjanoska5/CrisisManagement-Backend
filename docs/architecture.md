# Architektura systemu

**Powiązane issue:** #3
**Zależności:** ADR-0001 (`docs/adr/0001-wybor-stosu-technologicznego.md`)

Dokument realizuje kryteria akceptacji issue #3: diagram komponentów systemu oraz opis przepływu danych dla dwóch kluczowych scenariuszy.

> **Założenie do potwierdzenia w #5:** poniższy diagram traktuje moduł obywatela i moduł służb jako część **jednej natywnej aplikacji Android** (Kotlin + Jetpack Compose, zgodnie z ADR-0001), rozróżnianej rolą użytkownika po zalogowaniu — a nie jako osobne aplikacje/platformy. Jeśli w #5 zapadnie inna decyzja (np. osobny panel webowy dla służb), ten diagram wymaga aktualizacji.

---

## 1. Diagram komponentów

```mermaid
graph TB
    subgraph Klienci["Klienci — Android (Kotlin + Jetpack Compose)"]
        OBYWATEL[Aplikacja obywatela]
        SLUZBY[Aplikacja służb]
    end

    subgraph Backend["Backend — Spring Boot"]
        API[REST API]
        GEO[Moduł geolokalizacji]
        PUSHMOD[Moduł powiadomień push]
        SYNCMOD[Moduł synchronizacji offline]
    end

    DB[(PostgreSQL + PostGIS)]
    MAPS[Google Maps SDK]
    FCM[Firebase Cloud Messaging]
    LOCAL[(Lokalna baza Room<br/>na urządzeniu obywatela)]

    OBYWATEL -->|REST / HTTPS| API
    SLUZBY -->|REST / HTTPS| API
    OBYWATEL --> MAPS
    SLUZBY --> MAPS
    OBYWATEL --> LOCAL

    API --> GEO
    API --> PUSHMOD
    API --> SYNCMOD
    GEO --> DB
    PUSHMOD --> DB
    SYNCMOD --> DB
    PUSHMOD --> FCM
    FCM -->|powiadomienie push, budzi telefon| OBYWATEL
```

### Opis komponentów

| Komponent | Odpowiedzialność |
|---|---|
| Aplikacja obywatela | Zgłaszanie zdarzeń, SOS, mapa zagrożeń, alerty, tryb offline, karta medyczna, poradniki |
| Aplikacja służb | Oznaczanie zdarzeń, zmiana statusów, wysyłka alertów, podgląd jednostek ratunkowych |
| Backend / REST API | Warstwa pośrednicząca między klientami a bazą danych i usługami zewnętrznymi |
| Moduł geolokalizacji | Zapytania geoprzestrzenne: promień 100 m (system wiarygodności), najbliższa jednostka ratunkowa |
| Moduł powiadomień push | Wyzwala i formatuje wysyłkę alertów przez FCM |
| Moduł synchronizacji offline | Odbiera i porządkuje zgłoszenia wysłane z lokalnej bazy po odzyskaniu sieci |
| PostgreSQL + PostGIS | Trwałe przechowywanie danych, obliczenia geoprzestrzenne |
| Google Maps SDK | Wyświetlanie interaktywnej mapy po stronie klienta |
| Firebase Cloud Messaging | Dostarczanie powiadomień push do urządzeń, także gdy aplikacja jest w tle |
| Lokalna baza Room | Bufor zgłoszeń u obywatela, gdy brak zasięgu (issue 7.1) |

---

## 2. Przepływ: zgłoszenie obywatela → backend → widoczność u służb

```mermaid
sequenceDiagram
    actor Obywatel
    participant App as Aplikacja obywatela
    participant API as Backend (REST API)
    participant DB as Baza danych
    participant AppSluzby as Aplikacja służb

    Obywatel->>App: Robi zdjęcie, wybiera kategorię, dodaje komentarz
    App->>App: Pobiera lokalizację GPS

    alt Jest połączenie z siecią
        App->>API: POST /zgloszenia (zdjęcie, GPS, kategoria, komentarz)
        API->>DB: Zapisz zgłoszenie (status: aktywne)
        API-->>App: 201 Created
    else Brak zasięgu
        App->>App: Zapisz zgłoszenie lokalnie (Room DB)
        Note over App: Synchronizacja automatyczna po odzyskaniu sieci — issue 7.1
        App->>API: POST /zgloszenia (po odzyskaniu połączenia)
        API->>DB: Zapisz zgłoszenie
    end

    AppSluzby->>API: GET /zgloszenia
    API->>DB: Pobierz aktywne zgłoszenia
    DB-->>API: Lista zgłoszeń
    API-->>AppSluzby: Zwróć zgłoszenia (widoczne na mapie służb)
```

**Kluczowe punkty:**
- Zgłoszenie trafia do backendu tą samą ścieżką niezależnie od trybu (online/offline) — różni się jedynie moment wysyłki.
- Backend nie rozróżnia zgłoszeń offline od online po stronie logiki biznesowej — to upraszcza implementację modułu synchronizacji.
- Widoczność u służb realizowana jest przez odpytywanie API (do rozstrzygnięcia w kolejnych issues, czy pull, czy WebSocket/push — patrz sekcja "Otwarte pytania").

---

## 3. Przepływ: alert kryzysowy — służby → backend → push do obywateli

```mermaid
sequenceDiagram
    actor Sluzby as Pracownik służb
    participant AppSluzby as Aplikacja służb
    participant API as Backend (REST API)
    participant GEO as Moduł geolokalizacji
    participant DB as Baza danych
    participant FCM as Firebase Cloud Messaging
    participant AppObywatel as Aplikacja obywatela

    Sluzby->>AppSluzby: Definiuje obszar i treść alertu
    AppSluzby->>API: POST /alerty (obszar, treść)
    API->>DB: Zapisz alert
    API->>GEO: Znajdź użytkowników w zadanym obszarze
    GEO->>DB: Zapytanie geoprzestrzenne (PostGIS)
    DB-->>GEO: Lista urządzeń w obszarze
    GEO-->>API: Lista odbiorców
    API->>FCM: Wyślij powiadomienie push do listy urządzeń
    FCM-->>AppObywatel: Dostarcz powiadomienie (także gdy app w tle)
    AppObywatel->>AppObywatel: Wybudzenie ekranu, pop-up, sygnał dźwiękowy
```

**Kluczowe punkty:**
- Wyszukanie odbiorców odbywa się po stronie backendu (moduł geolokalizacji), nie na urządzeniu — obywatel nie musi mieć otwartej aplikacji, żeby dostać alert.
- Dostarczenie i wybudzenie telefonu zależy od FCM oraz od uprawnień systemowych Androida (kwestia do doprecyzowania w issue 5.2 — ograniczenia trybu Doze/oszczędzania baterii mogą wymagać wysokiego priorytetu wiadomości FCM).

---

## Otwarte pytania (do rozstrzygnięcia w kolejnych issues)

- Czy aplikacja służb pobiera nowe zgłoszenia przez odpytywanie (polling), czy przez WebSocket/subskrypcję push — wpływa na "czas rzeczywisty" mapy zagrożeń (issue 4.1).
- Ostateczny podział modułu obywatela / służb w ramach jednej aplikacji (routing, współdzielone komponenty) — issue #5.
- Format i struktura zapytań REST API (kontrakty endpointów) — do ustalenia przy pierwszych issues implementacyjnych (1.1, 3.1).
