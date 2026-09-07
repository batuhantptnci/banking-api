package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.CardResponse;
import com.batuhan.bankingapi.dto.CreateCardRequest;
import com.batuhan.bankingapi.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(
            CardService cardService
    ) {
        this.cardService =
                cardService;
    }

    @GetMapping
    public List<CardResponse> getMyCards(
            Principal principal
    ) {

        return cardService.getMyCards(
                principal.getName()
        );
    }

    @PostMapping
    public ResponseEntity<CardResponse>
    createCard(
            @Valid
            @RequestBody
            CreateCardRequest request,
            Principal principal
    ) {

        CardResponse card =
                cardService
                        .createDebitCard(
                                request.getAccountId(),
                                principal.getName()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(card);
    }

    @PostMapping("/{id}/freeze")
    public CardResponse freezeCard(
            @PathVariable Long id,
            Principal principal
    ) {

        return cardService.freezeCard(
                id,
                principal.getName()
        );
    }

    @PostMapping("/{id}/unfreeze")
    public CardResponse unfreezeCard(
            @PathVariable Long id,
            Principal principal
    ) {

        return cardService.unfreezeCard(
                id,
                principal.getName()
        );
    }
}