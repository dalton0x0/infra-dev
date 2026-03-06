package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.BlockRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.BlockResponse;
import com.cheridanh.infradev.services.BlockService;
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
@RequestMapping("/api/blocks")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Blocs", description = "Endpoints pour la gestion des blocs")
public class BlockController {

    private final BlockService blockService;

    @GetMapping
    @Operation(summary = "Récupère tous les blocs")
    public ResponseEntity<ApiResponse<List<BlockResponse>>> getAllBlocks() {
        log.debug("Requête de récupération de tous les blocs reçue : HTTP GET /api/blocks");
        List<BlockResponse> blocks = blockService.getAllBlocks();
        return ResponseEntity.ok(ApiResponse.success(blocks));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un bloc par son identifiant")
    public ResponseEntity<ApiResponse<BlockResponse>> getBlockById(@PathVariable Long id) {
        log.debug("Requête de récupération d'un bloc reçue : HTTP GET /api/blocks/{id}");
        BlockResponse block = blockService.getBlockById(id);
        return ResponseEntity.ok(ApiResponse.success(block));

    }

    @PostMapping
    @Operation(summary = "Créer un bloc")
    public ResponseEntity<ApiResponse<BlockResponse>> createBlock(@Valid @RequestBody BlockRequest blockRequest) {
        log.debug("Requête de création d'un blog reçue : HTTP POST /api/blocks");
        BlockResponse blockResponse = blockService.createBlock(blockRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bloc crée avec succès", blockResponse));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un bloc")
    public ResponseEntity<ApiResponse<BlockResponse>> updateBlock(
            @PathVariable Long id,
            @Valid @RequestBody BlockRequest blockRequest) {
        log.debug("Requête de mise à jour d'un bloc reçue : HTTP PUT /api/blocks/{id}");
        BlockResponse blockResponse = blockService.updateBlock(id, blockRequest);
        return ResponseEntity.ok(ApiResponse.success(blockResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Suppression d'un bloc")
    public ResponseEntity<ApiResponse<BlockResponse>> deleteBlock(@PathVariable Long id) {
        log.debug("Requête de suppression d'un bloc reçue : HTTP DELETE /api/blocks/{id}");
        blockService.deleteBlock(id);
        return ResponseEntity.ok(ApiResponse.success("Bloc supprimé avec succès"));
    }
}
