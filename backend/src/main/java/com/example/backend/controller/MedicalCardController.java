package com.example.backend.controller;

import com.example.backend.dto.MedicalCardRequest;
import com.example.backend.dto.MedicalCardResponse;
import com.example.backend.model.MedicalCard;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.backend.repository.MedicalCardRepository;
import java.util.UUID;

// endpointy dla karty medycznej
@RestController
@RequestMapping("/api/users/{userId}/medical-card")
public class MedicalCardController {

    private final UserRepository userRepository;
    private final MedicalCardRepository medicalCardRepository;

    public MedicalCardController(UserRepository userRepository, MedicalCardRepository medicalCardRepository) {
        this.userRepository = userRepository;
        this.medicalCardRepository=medicalCardRepository;
    }

    // pobieranie karty
    @GetMapping
    public MedicalCardResponse getCard(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        MedicalCard card = user.getMedicalCard();
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "medical card not found");
        }

        return mapToResponse(card);
    }

    // tworzenie lub nadpisywanie karty
    @PutMapping
    public MedicalCardResponse updateCard(@PathVariable UUID userId, @RequestBody MedicalCardRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        MedicalCard card = user.getMedicalCard();
        if (card == null) {
            card = new MedicalCard();
            // polaczenie karty z uzytkownikiem
            card.setUser(user);
        }

        card.setBloodType(request.getBloodType());
        card.setAllergies(request.getAllergies());
        card.setChronicDiseases(request.getChronicDiseases());
        card.setEmergencyContact(request.getEmergencyContact());
        card.setMedications(request.getMedications());

        user.setMedicalCard(card);
        userRepository.save(user);

        return mapToResponse(card);
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteCard(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        MedicalCard card = user.getMedicalCard();
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "medical card not found");
        }

        user.setMedicalCard(null);
        userRepository.save(user);
        medicalCardRepository.delete(card);

        return ResponseEntity.noContent().build();
    }


    // przepisywanie danych z encji do dto
    private MedicalCardResponse mapToResponse(MedicalCard card) {
        MedicalCardResponse response = new MedicalCardResponse();
        response.setId(card.getId());
        response.setBloodType(card.getBloodType());
        response.setAllergies(card.getAllergies());
        response.setChronicDiseases(card.getChronicDiseases());
        response.setEmergencyContact(card.getEmergencyContact());
        response.setMedications(card.getMedications());
        return response;
    }
}