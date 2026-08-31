# Schemat bazy danych

**Powiązane issue:** #4
**Zależności:** #3 (`docs/architecture.md`)

Dokument realizuje kryteria akceptacji issue #4: diagram ERD, zdefiniowane statusy zdarzenia, zdefiniowana struktura karty medycznej.

---

## Kluczowa decyzja projektowa: zgłoszenie ≠ zdarzenie

Rozdzielamy dwie encje, które łatwo pomylić:

- **Zgłoszenie** (`ZGLOSZENIA`) — surowy, pojedynczy raport od jednego obywatela (zdjęcie, lokalizacja, kategoria, komentarz). To dane wejściowe.
- **Zdarzenie** (`ZDARZENIA`) — zweryfikowany incydent widoczny na mapie (pinezka, status, kolor). To dane wyjściowe, które faktycznie ogląda użytkownik.

Bez tego podziału nie da się sensownie zaimplementować systemu wiarygodności zgłoszeń (issue 6.3): kilka niezależnych zgłoszeń tego samego zagrożenia w promieniu 100 m musi się dać **zgrupować** w jedno zdarzenie i wspólnie wpływać na jego status. Jedno zdarzenie może więc mieć wiele powiązanych zgłoszeń (`zdarzenie_id` w tabeli `ZGLOSZENIA`).

Zdarzenie może też powstać od razu, bez żadnego zgłoszenia obywatela — gdy pracownik służb nanosi je ręcznie na mapę (issue 3.1).

---

## Diagram ERD

```mermaid
erDiagram
    UZYTKOWNICY ||--o| KARTY_MEDYCZNE : posiada
    UZYTKOWNICY ||--o{ ZGLOSZENIA : tworzy
    UZYTKOWNICY ||--o{ ZDARZENIA : oznacza
    UZYTKOWNICY ||--o{ ALERTY_KRYZYSOWE : wysyla
    UZYTKOWNICY ||--o{ SOS_WEZWANIA : wywoluje
    ZDARZENIA ||--o{ ZGLOSZENIA : grupuje
    TYPY_ZAGROZEN ||--o{ ZGLOSZENIA : kategoryzuje
    TYPY_ZAGROZEN ||--o{ ZDARZENIA : kategoryzuje
    PUNKTY_ZBIORKI ||--o{ ALERTY_KRYZYSOWE : wskazuje

    UZYTKOWNICY {
        uuid id PK
        string email
        string haslo_hash
        enum rola "obywatel, sluzby"
        string imie
        string nazwisko
        timestamp data_utworzenia
    }

    KARTY_MEDYCZNE {
        uuid id PK
        uuid uzytkownik_id FK
        string grupa_krwi
        text alergie
        text leki
        text choroby_przewlekle
        string kontakt_alarmowy_imie
        string kontakt_alarmowy_telefon
        timestamp data_aktualizacji
    }

    TYPY_ZAGROZEN {
        uuid id PK
        string nazwa
        string ikona
    }

    ZGLOSZENIA {
        uuid id PK
        uuid uzytkownik_id FK
        uuid zdarzenie_id FK
        uuid typ_zagrozenia_id FK
        geography lokalizacja
        string zdjecie_url
        text komentarz
        enum zrodlo "online, offline_sync"
        timestamp data_utworzenia
    }

    ZDARZENIA {
        uuid id PK
        uuid typ_zagrozenia_id FK
        enum status "aktywne, weryfikowane, prawdopodobne, rozwiazane"
        geography lokalizacja
        uuid oznaczone_przez_sluzby_id FK
        int liczba_potwierdzajacych_zgloszen
        timestamp data_utworzenia
        timestamp data_aktualizacji
    }

    ALERTY_KRYZYSOWE {
        uuid id PK
        uuid sluzby_id FK
        string typ
        text tresc
        geography obszar
        uuid punkt_zbiorki_id FK
        timestamp data_wyslania
    }

    PUNKTY_ZBIORKI {
        uuid id PK
        string nazwa
        geography lokalizacja
        text opis
        boolean aktywny
    }

    JEDNOSTKI_RATUNKOWE {
        uuid id PK
        enum typ "radiowoz, woz_strazacki, karetka"
        string oznaczenie
        geography lokalizacja
        enum status "dostepna, w_akcji"
        timestamp data_aktualizacji_pozycji
    }

    SOS_WEZWANIA {
        uuid id PK
        uuid uzytkownik_id FK
        geography lokalizacja
        enum status "nowe, w_trakcie, zakonczone"
        timestamp data_utworzenia
    }
```

> **Uwaga:** `JEDNOSTKI_RATUNKOWE` celowo nie ma bezpośredniej relacji z `UZYTKOWNICY` — na tym etapie pozycje jednostek traktujemy jako niezależny strumień danych (np. z osobnego systemu/API służb), a nie jako coś przypisanego do konkretnego konta w naszej aplikacji. Jeśli to się zmieni, łatwo dodać FK do `UZYTKOWNICY`.

---

## Statusy zdarzenia (kryterium akceptacji nr 2)

| Status | Znaczenie | Kto/co go nadaje |
|---|---|---|
| `weryfikowane` | Zdarzenie zgłoszone, ale jeszcze niepotwierdzone — domyślny status dla pojedynczego zgłoszenia obywatela | System, automatycznie przy pierwszym zgłoszeniu |
| `prawdopodobne` | Co najmniej 5 niezależnych zgłoszeń w promieniu 100 m | System, automatycznie (issue 6.3) |
| `aktywne` | Zdarzenie potwierdzone jako realne | Służby — ręcznie, albo od razu przy oznaczeniu zdarzenia na mapie (issue 3.1) |
| `rozwiazane` | Zagrożenie zakończone/usunięte | Służby — ręcznie (issue 3.3) |

Kolory pinezek na mapie (issue 4.1) mapują się na te statusy: `aktywne` → czerwony, `weryfikowane`/`prawdopodobne` → żółty, `rozwiazane` → zielony.

### Przejścia między statusami

```mermaid
stateDiagram-v2
    [*] --> weryfikowane: nowe pojedyncze zgłoszenie obywatela
    [*] --> aktywne: zdarzenie oznaczone bezpośrednio przez służby
    weryfikowane --> prawdopodobne: 5+ niezależnych zgłoszeń w promieniu 100m (automatycznie)
    weryfikowane --> aktywne: służby potwierdzają ręcznie
    prawdopodobne --> aktywne: służby potwierdzają ręcznie
    weryfikowane --> rozwiazane: zdarzenie odrzucone jako nieprawdziwe
    aktywne --> rozwiazane: służby zamykają zdarzenie
    rozwiazane --> [*]
```

---

## Struktura karty medycznej (kryterium akceptacji nr 3)

| Pole | Typ | Opis |
|---|---|---|
| `grupa_krwi` | string | np. "A+", "0-" |
| `alergie` | text | wolny tekst, np. "penicylina, orzechy" |
| `leki` | text | aktualnie przyjmowane leki |
| `choroby_przewlekle` | text | np. cukrzyca, epilepsja, choroby serca |
| `kontakt_alarmowy_imie` | string | osoba do powiadomienia w razie wypadku |
| `kontakt_alarmowy_telefon` | string | numer telefonu do tej osoby |
| `data_aktualizacji` | timestamp | kiedy dane były ostatnio zmieniane przez użytkownika |

Relacja z `UZYTKOWNICY` jest **1:0..1** (`||--o|`) — nie każdy obywatel musi mieć wypełnioną kartę medyczną (funkcja opcjonalna, priorytet obniżony w rozmowie o issue 1.2), ale jeśli istnieje, jest dokładnie jedna na użytkownika. Dane te są odczytywane przy aktywacji SOS (issue 6.1).

---

## Pozostałe encje — krótki opis

| Encja | Do czego służy |
|---|---|
| `TYPY_ZAGROZEN` | Słownik kategorii (pożar, powódź, wypadek drogowy...) — zamiast wolnego tekstu, żeby kategorie były spójne w całej aplikacji i łatwe do filtrowania na mapie |
| `ALERTY_KRYZYSOWE` | Powiadomienia wysyłane przez służby do obywateli w danym obszarze (issue 5.1) |
| `PUNKTY_ZBIORKI` | Bezpieczne lokalizacje wskazywane w alertach, do których prowadzi nawigacja (issue 5.3) |
| `JEDNOSTKI_RATUNKOWE` | Aktualne pozycje pojazdów służb, widoczne na mapie obywatela (issue 4.2) |
| `SOS_WEZWANIA` | Osobna tabela dla wezwań SOS (issue 6.1) — rozdzielona od `ZGLOSZENIA`, bo to inny, krytyczny czasowo przepływ (bez zdjęcia, z automatycznym dołączeniem danych z karty medycznej) |

---
