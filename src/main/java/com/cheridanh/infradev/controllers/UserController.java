package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.AssignBlocksRequest;
import com.cheridanh.infradev.dtos.request.UserUpdateRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.UserResponse;
import com.cheridanh.infradev.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Endpoints pour la gestion des utilisateurs (admin)")
public class UserController {

    private final UserService userService;

    /**
     * Récupère tous les utilisateurs.
     */
    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.debug("Requête de récupération de tous les utilisateurs reçue : HTTP GET /api/users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * Récupère un utilisateur par son identifiant avec ses blocs.
     *
     * @param id l'identifiant de l'utilisateur
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par son identifiant")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.debug("Requête de récupération d'un utilisateur reçue : HTTP GET /api/users/{}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * Met à jour les informations d'un utilisateur.
     *
     * @param id l'identifiant de l'utilisateur à modifier
     * @param request les nouvelles données
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.debug("Requête de mise à jour d'un utilisateur reçue : HTTP PUT /api/users/{}", id);
        UserResponse userResponse = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour avec succès", userResponse));
    }

    /**
     * Bascule le rôle d'un utilisateur entre ADMIN et USER.
     *
     * @param id l'identifiant de l'utilisateur
     */
    @PatchMapping("/{id}/role")
    @Operation(summary = "Basculer le rôle d'un utilisateur (ADMIN/USER)")
    public ResponseEntity<ApiResponse<UserResponse>> toggleRole(@PathVariable Long id) {
        log.debug("Requête de bascule du rôle d'un utilisateur reçue : HTTP PATCH /api/users/{}/role", id);
        UserResponse userResponse = userService.toggleRole(id);
        return ResponseEntity.ok(ApiResponse.success("Rôle de l'utilisateur mis à jour", userResponse));
    }

    /**
     * Bascule l'état actif/inactif d'un utilisateur.
     *
     * @param id l'identifiant de l'utilisateur
     */
    @PatchMapping("/{id}/enabled")
    @Operation(summary = "Activer ou désactiver un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> toggleEnabled(@PathVariable Long id) {
        log.debug("Requête de bascule de l'état d'un utilisateur reçue : HTTP PATCH /api/users/{}/enabled", id);
        UserResponse userResponse = userService.toggleEnabled(id);
        return ResponseEntity.ok(ApiResponse.success("État de l'utilisateur mis à jour", userResponse));
    }

    /**
     * Assigne une promotion à un utilisateur.
     *
     * @param userId l'identifiant de l'utilisateur
     * @param promotionId l'identifiant de la promotion
     */
    @PatchMapping("/{userId}/promotion/{promotionId}")
    @Operation(summary = "Assigner une promotion à un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> assignPromotion(
            @PathVariable Long userId,
            @PathVariable Long promotionId) {
        log.debug("Requête d'assignation de promotion reçue : HTTP PATCH /api/users/{}/promotion/{}", userId, promotionId);
        UserResponse userResponse = userService.assignPromotion(userId, promotionId);
        return ResponseEntity.ok(ApiResponse.success("Promotion assignée avec succès", userResponse));
    }

    /**
     * Retire la promotion d'un utilisateur.
     *
     * @param userId l'identifiant de l'utilisateur
     */
    @DeleteMapping("/{userId}/promotion")
    @Operation(summary = "Retirer la promotion d'un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> removePromotion(@PathVariable Long userId) {
        log.debug("Requête de retrait de promotion reçue : HTTP DELETE /api/users/{}/promotion", userId);
        UserResponse userResponse = userService.removePromotion(userId);
        return ResponseEntity.ok(ApiResponse.success("Promotion retirée avec succès", userResponse));
    }

    /**
     * Assigne des blocs à un utilisateur (remplace les blocs existants).
     *
     * @param userId l'identifiant de l'utilisateur
     * @param request la liste des identifiants de blocs
     */
    @PutMapping("/{userId}/blocks")
    @Operation(summary = "Assigner des blocs à un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> assignBlocks(
            @PathVariable Long userId,
            @Valid @RequestBody AssignBlocksRequest request) {
        log.debug("Requête d'assignation de blocs reçue : HTTP PUT /api/users/{}/blocks", userId);
        UserResponse userResponse = userService.assignBlocks(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Blocs assignés avec succès", userResponse));
    }

    /**
     * Supprime définitivement un utilisateur.
     *
     * @param id l'identifiant de l'utilisateur à supprimer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.debug("Requête de suppression d'un utilisateur reçue : HTTP DELETE /api/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé avec succès"));
    }
}
