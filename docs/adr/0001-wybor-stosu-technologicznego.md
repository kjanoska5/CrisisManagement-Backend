# ADR-0001: Wybór stosu technologicznego

**Status:** Zaakceptowane
**Data:** 2026-08-27
**Autorzy:** Kacper, Marcel
**Powiązane issue:** #2

## Kontekst

Projekt "Mobilny System Teleinformatyczny do Zarządzania Kryzysowego" wymaga wyboru technologii dla trzech warstw systemu — aplikacji mobilnej (moduł obywatela i moduł służb), backendu oraz bazy danych — a także dostawcy usługi mapowej. Wybór musi uwzględniać kluczowe wymagania funkcjonalne z dokumentu projektowego:

- dostęp do GPS i aparatu (zgłaszanie zdarzeń ze zdjęciem i lokalizacją),
- niezawodne powiadomienia push wybudzające telefon (alerty kryzysowe),
- działanie w trybie offline (lokalny zapis zgłoszeń, synchronizacja po odzyskaniu sieci),
- interaktywna mapa z dynamicznie zmieniającymi się pinezkami (statusy zagrożeń),
- obliczenia geoprzestrzenne (system wiarygodności zgłoszeń — promień 100 m, wyszukiwanie najbliższej jednostki ratunkowej).

Dodatkowe ograniczenia: projekt realizowany jest przez 2-osobowy zespół w ramach pracy inżynierskiej, z ograniczonym czasem i bez budżetu na płatne usługi.

## Rozważane opcje

### Aplikacja mobilna

| Opcja | Zalety | Wady |
|---|---|---|
| **Natywna (Kotlin + Jetpack Compose)** | Pełny, bezpośredni dostęp do API Androida (GPS, kamera, powiadomienia w tle, wybudzanie ekranu) bez warstwy pośredniej; najlepsza niezawodność dla funkcji krytycznych (SOS, alerty) | Kod tylko na Androida (brak iOS) |
| Cross-platform (Flutter / React Native) | Potencjalnie też iOS | Dodatkowa warstwa abstrakcji utrudniająca niezawodne działanie w tle i wybudzanie telefonu — ryzykowne dla funkcji SOS/alertów |

### Backend

| Opcja | Zalety | Wady |
|---|---|---|
| **Spring Boot (Java, JDK 17+, Maven)** | Dojrzały ekosystem, dobre wsparcie dla PostgreSQL/PostGIS, znajomość technologii w zespole | Więcej boilerplate'u niż w Node.js |
| Node.js (NestJS/Express) | Szybszy start | Mniejsze doświadczenie zespołu z tą technologią |

### Baza danych

| Opcja | Zalety | Wady |
|---|---|---|
| **PostgreSQL + PostGIS** | Natywne wsparcie dla zapytań geoprzestrzennych (promień, najbliższy punkt) — kluczowe dla systemu wiarygodności zgłoszeń i śledzenia jednostek | — |

### Dostawca map

| Opcja | Zalety | Wady |
|---|---|---|
| **Google Maps SDK for Android** | SDK do wyświetlania mapy jest całkowicie darmowy i bez limitów; oficjalna, dojrzała integracja z Jetpack Compose (`maps-compose`); najlepsza dokumentacja i wsparcie społeczności | Trasowanie (Routes API) objęte darmowym limitem ok. 10 000 zapytań/mies. (od marca 2025 Google zastąpił dawny kredyt 200 USD/mies. darmowymi limitami per-usługa); wymaga karty płatniczej przy zakładaniu konta Google Cloud; ograniczone możliwości pełnego cachowania mapy offline |
| Mapbox | Lepsze natywne wsparcie map offline (pobieranie całych regionów do użytku bez sieci) | Darmowy limit ok. 50 000 wczytań mapy/mies. dotyczy głównie weba (limit dla SDK mobilnego wymaga weryfikacji przy wdrożeniu); mniejsza społeczność niż Google; również wymaga karty płatniczej |

## Decyzja

1. **Aplikacja mobilna:** natywna, **Kotlin + Jetpack Compose**.
2. **Backend:** **Spring Boot** (JDK 17+, Maven).
3. **Baza danych:** **PostgreSQL + PostGIS**.
4. **Dostawca map:** **Google Maps SDK for Android**.

## Uzasadnienie

- Natywny Kotlin daje pełną kontrolę nad funkcjami krytycznymi dla bezpieczeństwa (SOS, wybudzanie telefonu przy alercie) — bez ryzyka, że warstwa cross-platform ograniczy niezawodność tych funkcji.
- Spring Boot + PostgreSQL/PostGIS to sprawdzone, dobrze udokumentowane połączenie z natywnym wsparciem zapytań geoprzestrzennych, których projekt wymaga w kilku miejscach (promień 100 m, najbliższa jednostka ratunkowa).
- Google Maps SDK dla Androida jest darmowy i bez limitów w zakresie samego wyświetlania mapy — to pokrywa większość potrzeb projektu (interaktywna mapa zagrożeń, pinezki, śledzenie jednostek). Jedyna płatna funkcja, z jakiej skorzystamy (wyznaczanie trasy do punktu zbiórki), mieści się w darmowym miesięcznym limicie przy skali ruchu typowej dla pracy inżynierskiej.
- Ograniczone możliwości offline w Google Maps zaakceptowano świadomie: wymaganie "trybu offline" w projekcie dotyczy zapisu i synchronizacji zgłoszeń oraz poradników, a nie pełnej funkcjonalności mapy bez internetu.

## Konsekwencje

**Pozytywne:**
- Spójny, dobrze udokumentowany stos technologiczny na całej długości projektu.
- Brak kosztów przy skali typowej dla pracy inżynierskiej.
- Pełna kontrola nad funkcjami krytycznymi bezpieczeństwa dzięki natywnej implementacji.

**Negatywne / ryzyka:**
- Aplikacja działa wyłącznie na Androidzie — brak wersji na iOS (świadomie zaakceptowane, poza zakresem pracy).
- Wymagane skonfigurowanie limitów budżetowych (budget alerts) w Google Cloud Console, aby uniknąć nieoczekiwanych kosztów w razie przekroczenia darmowego limitu Routes API.
- Mapa nie będzie w pełni funkcjonalna bez połączenia z internetem — do ponownej oceny, jeśli w toku prac okaże się, że interaktywna mapa zagrożeń również powinna działać offline.
