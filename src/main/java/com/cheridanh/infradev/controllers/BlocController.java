package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.BlocRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.BlocResponse;
import com.cheridanh.infradev.services.BlocService;
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
@RequestMapping("/api/blocs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Blocs", description = "Endpoints pour la gestion des blocs")
public class BlocController {

    private final BlocService blocService;

    @GetMapping
    @Operation(summary = "Récupère tous les blocs")
    public ResponseEntity<ApiResponse<List<BlocResponse>>> getAllBlocs() {
        log.debug("Requête de récupération de tous les blocs reçue : HTTP GET /api/blocs");
        List<BlocResponse> blocs = blocService.getAllBlocs();
        return ResponseEntity.ok(ApiResponse.success(blocs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un bloc par son identifiant")
    public ResponseEntity<ApiResponse<BlocResponse>> getBlocById(@PathVariable Long id) {
        log.debug("Requête de récupération d'un bloc reçue : HTTP GET /api/blocs/{id}");
        BlocResponse bloc = blocService.getBlocById(id);
        return ResponseEntity.ok(ApiResponse.success(bloc));

    }

    @PostMapping
    @Operation(summary = "Créer un bloc")
    public ResponseEntity<ApiResponse<BlocResponse>> createBloc(@Valid @RequestBody BlocRequest blocRequest) {
        log.debug("Requête de création d'un blog reçue : HTTP POST /api/blocs");
        BlocResponse blocResponse = blocService.createBloc(blocRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bloc crée avec succès", blocResponse));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un  bloc")
    public ResponseEntity<ApiResponse<BlocResponse>> updateBloc(
            @PathVariable Long id,
            @Valid @RequestBody BlocRequest blocRequest) {
        log.debug("Requête de mise à jour d'un bloc reçue : HTTP PUT /api/blocs/{id}");
        BlocResponse blocResponse = blocService.updateBloc(id, blocRequest);
        return ResponseEntity.ok(ApiResponse.success(blocResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Suppression d'un bloc")
    public ResponseEntity<ApiResponse<BlocResponse>> deleteBloc(@PathVariable Long id) {
        log.debug("Requête de suppression d'un bloc reçue : HTTP DELETE /api/blocs/{id}");
        blocService.deleteBloc(id);
        return ResponseEntity.ok(ApiResponse.success("Bloc supprimé avec succès"));
    }
}
