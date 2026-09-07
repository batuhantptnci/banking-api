package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.CreateSavedRecipientRequest;
import com.batuhan.bankingapi.dto.SavedRecipientResponse;
import com.batuhan.bankingapi.service.SavedRecipientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/saved-recipients")
public class SavedRecipientController {

    private final SavedRecipientService
            savedRecipientService;

    public SavedRecipientController(
            SavedRecipientService savedRecipientService
    ) {
        this.savedRecipientService =
                savedRecipientService;
    }

    @GetMapping
    public List<SavedRecipientResponse>
    getMyRecipients(
            Principal principal
    ) {

        return savedRecipientService
                .getMyRecipients(
                        principal.getName()
                );
    }

    @PostMapping
    public ResponseEntity<SavedRecipientResponse>
    saveRecipient(
            @Valid
            @RequestBody
            CreateSavedRecipientRequest request,
            Principal principal
    ) {

        SavedRecipientResponse response =
                savedRecipientService
                        .saveRecipient(
                                principal.getName(),
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipient(
            @PathVariable Long id,
            Principal principal
    ) {

        savedRecipientService.deleteRecipient(
                id,
                principal.getName()
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}