package com.example.backend.controller;

import com.example.backend.dto.MedicalCardRequest;
import com.example.backend.dto.MedicalCardResponse;
import com.example.backend.model.MedicalCard;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.backend.repository.MedicalCardRepository;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/medical-card")
@Tag(name = "Medical Card", description = "User medical card and emergency information management")
public class MedicalCardController {

    private final UserRepository userRepository;
    private final MedicalCardRepository medicalCardRepository;

    public MedicalCardController(UserRepository userRepository, MedicalCardRepository medicalCardRepository) {
        this.userRepository = userRepository;
        this.medicalCardRepository=medicalCardRepository;
    }

    @GetMapping
    @Operation(summary = "Get medical card", description = "Retrieves the medical card details for a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medical card successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "User or medical card not found")
    })
    public MedicalCardResponse getCard(
            @Parameter(description = "UUID of the user") @PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        MedicalCard card = user.getMedicalCard();
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "medical card not found");
        }

        return mapToResponse(card);
    }

    @PutMapping
    @Operation(summary = "Create or update medical card", description = "Creates a new medical card or overwrites an existing one for the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medical card successfully updated/created"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public MedicalCardResponse updateCard(
            @Parameter(description = "UUID of the user") @PathVariable UUID userId,
            @RequestBody MedicalCardRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        MedicalCard card = user.getMedicalCard();
        if (card == null) {
            card = new MedicalCard();
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
    @Operation(summary = "Delete medical card", description = "Removes the medical card associated with the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Medical card successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User or medical card not found")
    })
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "UUID of the user") @PathVariable UUID userId) {
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