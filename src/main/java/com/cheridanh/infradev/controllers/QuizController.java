package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.QuizRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.QuizResponse;
import com.cheridanh.infradev.services.QuizService;
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
@RequestMapping("/api/quizzes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "Endpoints pour la gestion des quiz")
public class QuizController {

    private final QuizService quizService;

    /**
     * Récupère tous les quiz.
     */
    @GetMapping
    @Operation(summary = "Lister tous les quiz")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getAllQuizzes() {
        log.debug("Requête de récupération de tous les quiz reçue : HTTP GET /api/quizzes");
        List<QuizResponse> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }

    /**
     * Récupère un quiz par son identifiant.
     *
     * @param id l'identifiant du quiz
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un quiz par son identifiant")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizById(@PathVariable Long id) {
        log.debug("Requête de récupération d'un quiz reçue : HTTP GET /api/quizzes/{}", id);
        QuizResponse quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(ApiResponse.success(quiz));
    }

    /**
     * Crée un nouveau quiz rattaché à un module (un seul quiz par module).
     *
     * @param quizRequest les données du quiz à créer
     */
    @PostMapping
    @Operation(summary = "Créer un quiz")
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(@Valid @RequestBody QuizRequest quizRequest) {
        log.debug("Requête de création d'un quiz reçue : HTTP POST /api/quizzes");
        QuizResponse quizResponse = quizService.createQuiz(quizRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Quiz créé avec succès", quizResponse));
    }

    /**
     * Met à jour un quiz existant.
     *
     * @param id l'identifiant du quiz à modifier
     * @param quizRequest les nouvelles données
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un quiz")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizRequest quizRequest) {
        log.debug("Requête de mise à jour d'un quiz reçue : HTTP PUT /api/quizzes/{}", id);
        QuizResponse quizResponse = quizService.updateQuiz(id, quizRequest);
        return ResponseEntity.ok(ApiResponse.success("Quiz mis à jour avec succès", quizResponse));
    }

    /**
     * Supprime définitivement un quiz.
     *
     * @param id l'identifiant du quiz à supprimer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un quiz")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long id) {
        log.debug("Requête de suppression d'un quiz reçue : HTTP DELETE /api/quizzes/{}", id);
        quizService.deleteQuiz(id);
        return ResponseEntity.ok(ApiResponse.success("Quiz supprimé avec succès"));
    }

    /**
     * Bascule l'état complété/non complété d'un quiz.
     *
     * @param id l'identifiant du quiz
     */
    @PatchMapping("/{id}/completed")
    @Operation(summary = "Marquer un quiz comme complété ou non complété")
    public ResponseEntity<ApiResponse<QuizResponse>> toggleCompleted(@PathVariable Long id) {
        log.debug("Requête de bascule de l'état complété d'un quiz reçue : HTTP PATCH /api/quizzes/{}/completed", id);
        QuizResponse quizResponse = quizService.toggleCompleted(id);
        return ResponseEntity.ok(ApiResponse.success("État du quiz mis à jour", quizResponse));
    }
}
