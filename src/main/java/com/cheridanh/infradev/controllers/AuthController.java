package com.cheridanh.infradev.controllers;

import com.cheridanh.infradev.dtos.request.LoginRequest;
import com.cheridanh.infradev.dtos.request.LogoutRequest;
import com.cheridanh.infradev.dtos.request.RefreshTokenRequest;
import com.cheridanh.infradev.dtos.request.RegisterRequest;
import com.cheridanh.infradev.dtos.response.ApiResponse;
import com.cheridanh.infradev.dtos.response.AuthResponse;
import com.cheridanh.infradev.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour l'authentification des utilisateurs")
public class AuthController {

    private final AuthService authService;

    /**
     * Inscrit un nouvel utilisateur.
     *
     * @param request les données d'inscription validées
     * @return les tokens et les informations de l'utilisateur (201 Created)
     */
    @PostMapping("/register")
    @Operation(
            summary = "Inscription",
            description = "Crée un nouveau compte utilisateur avec le rôle USER par défaut. " +
                    "Retourne un token pour directement connecter l'utilisateur après l'inscription"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("Requête d'inscription reçue : HTTP POST /auth/register");
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Inscription réussie.", response));
    }

    /**
     * Connecte un utilisateur existant.
     *
     * @param request les identifiants de connexion valides
     * @return les tokens et les informations de l'utilisateur
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion", description = "Authentifie un utilisateur et retourne les tokens JWT")
    public ResponseEntity<ApiResponse<AuthResponse>>  login(@Valid @RequestBody LoginRequest request) {
        log.debug("Requête de connexion reçue : HTTP POST /auth/login");
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie.", response));
    }

    /**
     * Renouvelle le access token à partir d'un refresh token valide.
     * Effectue également une rotation du refresh token (sécurité).
     *
     * @param request le refresh token
     * @return le nouveau access token et le nouveau refresh token
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Rafraîchir le token",
            description = "Génère un nouveau access token et un nouveau refresh token (rotation). " +
                    "L'ancien refresh token est révoqué pour des raisons de sécurité. " +
                    "Le client doit stocker le nouveau refresh token retourné."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Requête de renouvellement de token reçue : HTTP POST /auth/refresh");
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token rafraîchi avec succès", response));
    }

    /**
     * Déconnecte l'utilisateur en révoquant ses refresh tokens.
     *
     * @param request le refresh token à révoquer
     * @return un message de confirmation
     */
    @PostMapping("/logout")
    @Operation(
            summary = "Déconnexion",
            description = "Révoque le refresh token. Le token ne pourra plus être utilisé pour " +
                    "générer de nouveaux access tokens et la session active sera invalide."
    )
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        log.debug("Requête de déconnexion reçue : HTTP POST /auth/logout");
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Déconnexion réussie"));
    }
}
