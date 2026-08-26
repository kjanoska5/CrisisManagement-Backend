# 🚨 Mobilny System Teleinformatyczny do Zarządzania Kryzysowego

> **Praca Inżynierska**  
> **Uczelnia:** Politechnika Łódzka  
> **Promotor:** dr inż. Paweł Marciniak  
> **Autorzy:** Kacper & Marcel  

---

## 📌 O projekcie

System służy do natychmiastowego przekazywania informacji o zagrożeniach, koordynacji działań w sytuacjach kryzysowych oraz sprawny przepływ informacji pomiędzy obywatelami a służbami ratunkowymi.

Aplikacja składa się z dwóch głównych modułów: **modułu dla obywatela** (aplikacja mobilna Android) oraz **modułu dla służb** (panel zarządzania).

---

## 🔥 Kluczowe Funkcjonalności

### 📱 Moduł Obywatela (Android App)
* **Szybkie wezwanie pomocy (SOS):** Przytrzymanie przycisku wysyła pakiet danych z dokładną pozycją GPS oraz zapisaną kartą medyczną.
* **Zgłaszanie zdarzeń:** Dodawanie zgłoszeń ze zdjęciem, lokalizacją GPS, kategorią oraz opisem.
* **Interactive Hazard Map:** Interaktywna mapa z oznaczonymi statusami zagrożeń (aktywne, weryfikowane, rozwiązane).
* **Alerty kryzysowe:** Powiadomienia PUSH wybudzające telefon ze sygnałem dźwiękowym i pop-upem w nagłych sytuacjach.
* **Nawigacja do punktu zbiórki:** Wyznaczanie najkrótszej trasy do bezpiecznej strefy ewakuacyjnej.
* **Tryb Offline:** Automatyczny zapis zgłoszeń w lokalnej bazie danych w przypadku braku zasięgu i ich synchronizacja po powrocie do sieci.
* **Karta Medyczna & Poradniki:** Dostęp offline do instrukcji pierwszej pomocy oraz profili medycznych użytkownika.
* **Tryb oszczędzania zasobów:** Możliwość przesyłania wyłącznie priorytetowych danych (tekst + GPS) w celu oszczędzania baterii i transferu.

### 🛡️ Moduł dla Służb & Algorytmy Serwerowe
* **System wiarygodności zgłoszeń:** Algorytm analizujący powtarzalność zgłoszeń (np. 5 zgłoszeń w promieniu 100m) i podnoszący priorytet zdarzenia.
* **Nanoszenie i weryfikacja zagrożeń:** Zarządzanie statusami zdarzeń i obszarami objętymi niebezpieczeństwem.
* **Śledzenie jednostek ratunkowych:** Podgląd na mapie pozycji służb znajdujących się w pobliżu użytkownika.

---

## 🛠️ Stos Technologiczny

* **Aplikacja Mobilna:** Android Studio, Kotlin, Jetpack Compose, Room DB, Mapbox / Google Maps SDK
* **Backend:** Node.js (NestJS/Express) / Java (Spring Boot)
* **Baza Danych:** PostgreSQL + PostGIS (obliczenia geoprzestrzenne)
* **Powiadomienia & Real-time:** Firebase Cloud Messaging (FCM), WebSockets

---

## 🚀 Uruchomienie projektu

### Wymagania wstępne
- Android Studio (najnowsza stabilna) + JDK 17+
- Node.js 20+ / npm  **albo**  JDK 17+ + Maven (zależnie od wyboru backendu — do ustalenia w #2)
- PostgreSQL 15+ z rozszerzeniem PostGIS
- Konto Firebase (do FCM) — plik `google-services.json`

### Backend
```bash
cd backend
npm install
cp .env.example .env   # uzupełnij zmienne środowiskowe (baza danych, FCM)
npm run start:dev
```
*(jeśli finalnie Spring Boot: `./mvnw spring-boot:run`)*

### Baza danych
```bash
createdb crisis_management
psql -d crisis_management -c "CREATE EXTENSION postgis;"
```

### Aplikacja Android
1. Otwórz katalog `android/` w Android Studio.
2. Dodaj `google-services.json` do `android/app/`.
3. Zsynchronizuj Gradle i uruchom na emulatorze/urządzeniu.

---

## 📂 Struktura Repozytorium

```text
crisis-management-system/
├── android/            # Kod źródłowy aplikacji mobilnej (Android Studio / Kotlin)
├── backend/            # Kod źródłowy serwera i API
└── docs/               # Dokumentacja projektowa i schematy bazy danych
