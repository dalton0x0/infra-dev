package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.ModuleRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.ModuleResponse;
import com.cheridanh.infradev.services.ModuleService;
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
@RequestMapping("/api/modules")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Endpoints pour la gestion des modules")
public class ModuleController {

    private final ModuleService moduleService;

    /**
     * Récupère tous les modules.
     */
    @GetMapping
    @Operation(summary = "Lister tous les modules")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getAllModules() {
        log.debug("Requête de récupération de tous les modules reçue : HTTP GET /api/modules");
        List<ModuleResponse> modules = moduleService.getAllModules();
        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    /**
     * Récupère un module par son identifiant avec ses cours, exercices et quiz.
     *
     * @param id l'identifiant du module
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un module par son identifiant")
    public ResponseEntity<ApiResponse<ModuleResponse>> getModuleById(@PathVariable Long id) {
        log.debug("Requête de récupération d'un module reçue : HTTP GET /api/modules/{}", id);
        ModuleResponse module = moduleService.getModuleById(id);
        return ResponseEntity.ok(ApiResponse.success(module));
    }

    /**
     * Récupère tous les modules d'un bloc donné.
     *
     * @param blockId l'identifiant du bloc
     */
    @GetMapping("/bloc/{blockId}")
    @Operation(summary = "Lister les modules d'un bloc")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByBlockId(@PathVariable Long blockId) {
        log.debug("Requête de récupération des modules du bloc id {} reçue : HTTP GET /api/modules/block/{}", blockId, blockId);
        List<ModuleResponse> modules = moduleService.getModulesByBlockId(blockId);
        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    /**
     * Crée un nouveau module rattaché à un bloc.
     *
     * @param moduleRequest les données du module à créer
     */
    @PostMapping
    @Operation(summary = "Créer un module")
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(@Valid @RequestBody ModuleRequest moduleRequest) {
        log.debug("Requête de création d'un module reçue : HTTP POST /api/modules");
        ModuleResponse moduleResponse = moduleService.createModule(moduleRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Module créé avec succès", moduleResponse));
    }

    /**
     * Met à jour un module existant.
     *
     * @param id l'identifiant du module à modifier
     * @param moduleRequest les nouvelles données
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un module")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody ModuleRequest moduleRequest) {
        log.debug("Requête de mise à jour d'un module reçue : HTTP PUT /api/modules/{}", id);
        ModuleResponse moduleResponse = moduleService.updateModule(id, moduleRequest);
        return ResponseEntity.ok(ApiResponse.success("Module mis à jour avec succès", moduleResponse));
    }

    /**
     * Supprime définitivement un module.
     *
     * @param id l'identifiant du module à supprimer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un module")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable Long id) {
        log.debug("Requête de suppression d'un module reçue : HTTP DELETE /api/modules/{}", id);
        moduleService.deleteModule(id);
        return ResponseEntity.ok(ApiResponse.success("Module supprimé avec succès"));
    }
}
