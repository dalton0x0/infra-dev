package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.PromoRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.PromoResponse;
import com.cheridanh.infradev.services.PromoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/promos")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Endpoints pour la gestion des promotions")
public class PromoController {

    private final PromoService promoService;

    /**
     * Récupère toutes les promotions triées par date de début.
     */
    @GetMapping
    @Operation(summary = "Lister toutes les promotions")
    public ResponseEntity<ApiResponse<List<PromoResponse>>> getAllPromos() {
        log.debug("Requête de récupération de toutes les promotions reçue : HTTP GET /api/promos");
        List<PromoResponse> promoResponses = promoService.getAllPromos();
        return ResponseEntity.ok(ApiResponse.success(promoResponses));
    }

    /**
     * Récupère le détail d'une promotion avec la liste de ses utilisateurs.
     *
     * @param id l'identifiant de la promotion
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une promotion par son identifiant")
    public ResponseEntity<ApiResponse<PromoResponse>> getPromoById(@PathVariable Long id) {
        log.debug("Requête de récupération d'une promotion reçue : HTTP GET /api/promos/{id}");
        PromoResponse promoResponse = promoService.getPromoById(id);
        return ResponseEntity.ok(ApiResponse.success(promoResponse));
    }

    /**
     * Crée une nouvelle promotion.
     *
     * @param promoRequest les données de la promotion à créer
     */
    @PostMapping
    @Operation(summary = "Créer une promotion")
    public ResponseEntity<ApiResponse<PromoResponse>> createPromo(@Valid @RequestBody PromoRequest promoRequest) {
        log.debug("Requête de création d'une promotion reçue : HTTP POST /api/promos");
        PromoResponse promoResponse = promoService.createPromo(promoRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Promotion créée avec succès", promoResponse));
    }

    /**
     * Met à jour une promotion existante.
     *
     * @param id l'identifiant de la promotion à modifier
     * @param promoRequest les nouvelles données
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une promotion")
    public ResponseEntity<ApiResponse<PromoResponse>> updatePromo(
            @PathVariable Long id,
            @Valid @RequestBody PromoRequest promoRequest) {
        log.debug("Requête de mise à jour d'une promotion reçue : HTTP PUT /api/promos");
        PromoResponse promoResponse = promoService.updatePromo(id, promoRequest);
        return ResponseEntity.ok(ApiResponse.success("Promotion mise à jour avec succès", promoResponse));
    }

    /**
     * Supprime définitivement une promotion.
     *
     * @param id l'identifiant de la promotion à supprimer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une promotion")
    public ResponseEntity<ApiResponse<Void>> deletePromo(@PathVariable Long id) {
        log.debug("Requête de suppression d'une promotion reçue : HTTP DELETE /api/promos/{id}");
        promoService.deletePromo(id);
        return ResponseEntity.ok(ApiResponse.success("Promotion supprimée avec succès"));
    }

    /**
     * Bascule l'état actif/inactif d'une promotion.
     *
     * @param id l'identifiant de la promotion
     */
    @PatchMapping("/{id}/active")
    @Operation(summary = "Activer ou désactiver une promotion")
    public ResponseEntity<ApiResponse<PromoResponse>> toggleActive(@PathVariable Long id) {
        log.debug("Requête d'activation ou de désactivation d'une promotion reçue : HTTP PATCH /api/promos/{id}/active");
        PromoResponse promoResponse = promoService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success("État de la promotion mis à jour", promoResponse));
    }
}
