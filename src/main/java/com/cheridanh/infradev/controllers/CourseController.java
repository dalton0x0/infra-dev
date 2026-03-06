package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.CourseRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.CourseResponse;
import com.cheridanh.infradev.services.CourseService;
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
@RequestMapping("/api/courses")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Cours", description = "Endpoints pour la gestion des cours")
public class CourseController {

    private final CourseService courseService;

    /**
     * Récupère tous les cours.
     */
    @GetMapping
    @Operation(summary = "Lister tous les cours")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        log.debug("Requête de récupération de tous les cours reçue : HTTP GET /api/courses");
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    /**
     * Récupère un cours par son identifiant.
     *
     * @param id l'identifiant du cours
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un cours par son identifiant")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        log.debug("Requête de récupération d'un cours reçue : HTTP GET /api/courses/{}", id);
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    /**
     * Récupère tous les cours d'un module donné.
     *
     * @param moduleId l'identifiant du module
     */
    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Lister les cours d'un module")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByModuleId(@PathVariable Long moduleId) {
        log.debug("Requête de récupération des cours du module id {} reçue : HTTP GET /api/courses/module/{}", moduleId, moduleId);
        List<CourseResponse> courses = courseService.getCoursesByModuleId(moduleId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    /**
     * Crée un nouveau cours rattaché à un module.
     *
     * @param courseRequest les données du cours à créer
     */
    @PostMapping
    @Operation(summary = "Créer un cours")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        log.debug("Requête de création d'un cours reçue : HTTP POST /api/courses");
        CourseResponse courseResponse = courseService.createCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cours créé avec succès", courseResponse));
    }

    /**
     * Met à jour un cours existant.
     *
     * @param id l'identifiant du cours à modifier
     * @param courseRequest les nouvelles données
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un cours")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest courseRequest) {
        log.debug("Requête de mise à jour d'un cours reçue : HTTP PUT /api/courses/{}", id);
        CourseResponse courseResponse = courseService.updateCourse(id, courseRequest);
        return ResponseEntity.ok(ApiResponse.success("Cours mis à jour avec succès", courseResponse));
    }

    /**
     * Supprime définitivement un cours.
     *
     * @param id l'identifiant du cours à supprimer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un cours")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        log.debug("Requête de suppression d'un cours reçue : HTTP DELETE /api/courses/{}", id);
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Cours supprimé avec succès"));
    }

    /**
     * Bascule l'état complété/non complété d'un cours.
     *
     * @param id l'identifiant du cours
     */
    @PatchMapping("/{id}/completed")
    @Operation(summary = "Marquer un cours comme complété ou non complété")
    public ResponseEntity<ApiResponse<CourseResponse>> toggleCompleted(@PathVariable Long id) {
        log.debug("Requête de bascule de l'état complété d'un cours reçue : HTTP PATCH /api/courses/{}/completed", id);
        CourseResponse courseResponse = courseService.toggleCompleted(id);
        return ResponseEntity.ok(ApiResponse.success("État du cours mis à jour", courseResponse));
    }
}
