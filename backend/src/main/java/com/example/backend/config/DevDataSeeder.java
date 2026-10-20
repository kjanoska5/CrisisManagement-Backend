package com.example.backend.config;

import com.example.backend.model.Role;
import com.example.backend.model.ThreatType;
import com.example.backend.model.User;
import com.example.backend.repository.ThreatTypeRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

// dane testowe ladowane przy starcie aplikacji
@Configuration
public class DevDataSeeder {

    @Bean
    CommandLineRunner loadTestData(
            UserRepository userRepository,
            ThreatTypeRepository threatTypeRepository,
            JdbcTemplate jdbcTemplate
    ) {
        return args -> {
            if (userRepository.count() == 0) {
                jdbcTemplate.update(
                        "INSERT INTO users (id, email, password_hash, role, first_name, last_name, created_at) VALUES (?::uuid, ?, ?, ?, ?, ?, NOW())",
                        "a4bab6f2-60fa-461a-b7b5-746b0e883896", "test.citizen@example.com", "stub", "CITIZEN", "john", "doe"
                );
                System.out.println("test user created directly via SQL");
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