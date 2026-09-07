package com.example.backend.config;

import com.example.backend.model.Rola;
import com.example.backend.model.TypZagrozenia;
import com.example.backend.model.Uzytkownik;
import com.example.backend.repository.TypZagrozeniaRepository;
import com.example.backend.repository.UzytkownikRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Dane testowe wstawiane automatycznie przy starcie aplikacji,
 * TYLKO jeśli tabele są puste — potrzebne do developmentu, dopóki
 * nie ma prawdziwej rejestracji użytkowników.
 *
 * Po starcie backendu sprawdzić w bazie (albo w logach) wygenerowane
 * UUID testowego użytkownika i typów zagrożeń — będą potrzebne
 * do przetestowania POST /api/zgloszenia przez Postman/curl.
 */
@Configuration
public class DevDataSeeder {

    @Bean
    CommandLineRunner zaladujDaneTestowe(
            UzytkownikRepository uzytkownikRepository,
            TypZagrozeniaRepository typZagrozeniaRepository
    ) {
        return args -> {
            if (uzytkownikRepository.count() == 0) {
                Uzytkownik testowy = new Uzytkownik();
                testowy.setEmail("test.obywatel@example.com");
                testowy.setHasloHash("stub");
                testowy.setRola(Rola.OBYWATEL);
                testowy.setImie("Jan");
                testowy.setNazwisko("Testowy");
                uzytkownikRepository.save(testowy);
                System.out.println("[DevDataSeeder] Utworzono testowego użytkownika: " + testowy.getId());
            }

            if (typZagrozeniaRepository.count() == 0) {
                List<TypZagrozenia> typy = typZagrozeniaRepository.saveAll(List.of(
                        nowyTyp("Pożar"),
                        nowyTyp("Powódź"),
                        nowyTyp("Wypadek drogowy"),
                        nowyTyp("Awaria infrastruktury")
                ));
                typy.forEach(t -> System.out.println("[DevDataSeeder] Typ zagrożenia: " + t.getNazwa() + " -> " + t.getId()));
            }
        };
    }

    private TypZagrozenia nowyTyp(String nazwa) {
        TypZagrozenia typ = new TypZagrozenia();
        typ.setNazwa(nazwa);
        return typ;
    }
}
