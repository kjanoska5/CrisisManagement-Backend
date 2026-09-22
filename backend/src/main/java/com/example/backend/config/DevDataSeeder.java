package com.example.backend.config;

import com.example.backend.model.Role;
import com.example.backend.model.ThreatType;
import com.example.backend.model.User;
import com.example.backend.repository.ThreatTypeRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// dane testowe ladowane przy starcie aplikacji
@Configuration
public class DevDataSeeder {

    @Bean
    CommandLineRunner loadTestData(
            UserRepository userRepository,
            ThreatTypeRepository threatTypeRepository
    ) {
        return args -> {
            // tworzenie uzytkownika jesli baza pusta
            if (userRepository.count() == 0) {
                User testUser = new User();
                testUser.setEmail("test.citizen@example.com");
                testUser.setPasswordHash("stub");
                testUser.setRole(Role.CITIZEN);
                testUser.setFirstName("john");
                testUser.setLastName("doe");
                userRepository.save(testUser);
                System.out.println("test user created: " + testUser.getId());
            }

            // tworzenie typow zagrozien
            if (threatTypeRepository.count() == 0) {
                List<ThreatType> types = threatTypeRepository.saveAll(List.of(
                        newType("fire"),
                        newType("flood"),
                        newType("traffic accident"),
                        newType("infrastructure failure")
                ));
                types.forEach(t -> System.out.println("threat type: " + t.getName() + " -> " + t.getId()));
            }
        };
    }

    // pomocnicza metoda do tworzenia typu
    private ThreatType newType(String name) {
        ThreatType type = new ThreatType();
        type.setName(name);
        return type;
    }
}