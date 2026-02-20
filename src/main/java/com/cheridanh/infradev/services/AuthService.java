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
     * Renouvelle le token d'accès à partir d'un refresh token valide.
     * Effectue une rotation du refresh token : l'ancien est révoqué et un nouveau est émis.
     *
     * @param request le refresh token courant
     * @return la réponse contenant le nouveau token d'accès et le nouveau refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Déconnecte l'utilisateur en révoquant tous ses refresh tokens actifs.
     *
     * @param request le refresh token à révoquer
     */
    void logout(LogoutRequest request);
}
