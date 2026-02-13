package com.cheridanh.infradev.services;

import com.cheridanh.infradev.dtos.request.LoginRequest;
import com.cheridanh.infradev.dtos.request.LogoutRequest;
import com.cheridanh.infradev.dtos.request.RefreshTokenRequest;
import com.cheridanh.infradev.dtos.request.RegisterRequest;
import com.cheridanh.infradev.dtos.response.AuthResponse;

public interface AuthService {

    /**
     * Inscrit un nouvel utilisateur.
     *
     * @param request les données d'inscription
     * @return la réponse contenant les tokens et les infos utilisateur
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authentifie un utilisateur existant.
     *
     * @param request les identifiants de connexion
     * @return la réponse contenant les tokens et les infos utilisateur
     */
    AuthResponse login(LoginRequest request);

    /**
     * Renouvelle le access token à partir d'un refresh token valide.
     *
     * @param request le refresh token
     * @return la réponse contenant le nouveau access token et le même refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Déconnecte l'utilisateur en révoquant son refresh token.
     *
     * @param request le refresh token à révoquer
     */
    void logout(LogoutRequest request);
}
