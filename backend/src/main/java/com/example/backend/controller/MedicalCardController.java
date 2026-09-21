package com.example.backend.controller;

import com.example.backend.dto.MedicalCardRequest;
import com.example.backend.dto.MedicalCardResponse;
import com.example.backend.model.MedicalCard;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

// endpointy dla karty medycznej
@RestController
@RequestMapping("/api/users/{userId}/medical-card")
public class MedicalCardController {

    private final UserRepository userRepository;

    public MedicalCardController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        // zapis do bazy dzieki cascade
        userRepository.save(user);

        return mapToResponse(card);
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