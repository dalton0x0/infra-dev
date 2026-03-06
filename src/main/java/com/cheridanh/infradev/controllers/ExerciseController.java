package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.ExerciseRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.ExerciseResponse;
import com.cheridanh.infradev.services.ExerciseService;
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
@RequestMapping("/api/exercises")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Exercices", description = "Endpoints pour la gestion des exercices")
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * Récupère tous les exercices.
     */
    @GetMapping
    @Operation(summary = "Lister tous les exercices")
    public ResponseEntity<ApiResponse<List<ExerciseResponse>>> getAllExercises() {
        log.debug("Requête de récupération de tous les exercices reçue : HTTP GET /api/exercises");
        List<ExerciseResponse> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(ApiResponse.success(exercises));
    }

    /**
     * Récupère un exercice par son identifiant.
     *
     * @param id l'identifiant de l'exercice
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un exercice par son identifiant")
    public ResponseEntity<ApiResponse<ExerciseResponse>> getExerciseById(@PathVariable Long id) {
        log.debug("Requête de récupération d'un exercice reçue : HTTP GET /api/exercises/{}", id);
        ExerciseResponse exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(ApiResponse.success(exercise));
    }

    /**
     * Récupère tous les exercices d'un module donné.
     *
     * @param moduleId l'identifiant du module
     */
    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Lister les exercices d'un module")
    public ResponseEntity<ApiResponse<List<ExerciseResponse>>> getExercisesByModuleId(@PathVariable Long moduleId) {
        log.debug("Requête de récupération des exercices du module id {} reçue : HTTP GET /api/exercises/module/{}", moduleId, moduleId);
        List<ExerciseResponse> exercises = exerciseService.getExercisesByModuleId(moduleId);
        return ResponseEntity.ok(ApiResponse.success(exercises));
    }

    /**
     * Crée un nouvel exercice rattaché à un module.
     *
     * @param exerciseRequest les données de l'exercice à créer
     */
    @PostMapping
    @Operation(summary = "Créer un exercice")
    public ResponseEntity<ApiResponse<ExerciseResponse>> createExercise(@Valid @RequestBody ExerciseRequest exerciseRequest) {
        log.debug("Requête de création d'un exercice reçue : HTTP POST /api/exercises");
        ExerciseResponse exerciseResponse = exerciseService.createExercise(exerciseRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exercice créé avec succès", exerciseResponse));
    }

    /**
     * Met à jour un exercice existant.
     *
     * @param id l'identifiant de l'exercice à modifier
     * @param exerciseRequest les nouvelles données
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un exercice")
    public ResponseEntity<ApiResponse<ExerciseResponse>> updateExercise(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseRequest exerciseRequest) {
        log.debug("Requête de mise à jour d'un exercice reçue : HTTP PUT /api/exercises/{}", id);
        ExerciseResponse exerciseResponse = exerciseService.updateExercise(id, exerciseRequest);
        return ResponseEntity.ok(ApiResponse.success("Exercice mis à jour avec succès", exerciseResponse));
    }

    /**
     * Supprime définitivement un exercice.
     *
     * @param id l'identifiant de l'exercice à supprimer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un exercice")
    public ResponseEntity<ApiResponse<Void>> deleteExercise(@PathVariable Long id) {
        log.debug("Requête de suppression d'un exercice reçue : HTTP DELETE /api/exercises/{}", id);
        exerciseService.deleteExercise(id);
        return ResponseEntity.ok(ApiResponse.success("Exercice supprimé avec succès"));
    }

    /**
     * Bascule l'état complété/non complété d'un exercice.
     *
     * @param id l'identifiant de l'exercice
     */
    @PatchMapping("/{id}/completed")
    @Operation(summary = "Marquer un exercice comme complété ou non complété")
    public ResponseEntity<ApiResponse<ExerciseResponse>> toggleCompleted(@PathVariable Long id) {
        log.debug("Requête de bascule de l'état complété d'un exercice reçue : HTTP PATCH /api/exercises/{}/completed", id);
        ExerciseResponse exerciseResponse = exerciseService.toggleCompleted(id);
        return ResponseEntity.ok(ApiResponse.success("État de l'exercice mis à jour", exerciseResponse));
    }
}
