package com.cheridanh.infradev.services;

import com.cheridanh.infradev.entities.RefreshToken;
import com.cheridanh.infradev.entities.User;

public interface RefreshTokenService {

    /**
     * Crée un nouveau refresh token pour l'utilisateur.
     *
     * @param user l'utilisateur pour lequel créer le token
     * @return le refresh token créé et persisté
     */
    RefreshToken createRefreshToken(User user);

    /**
     * Valide un refresh token et retourne l'entité correspondante.
     * Vérifie que le token existe, n'est pas révoqué et n'est pas expiré.
     *
     * @param token la valeur du refresh token
     * @return le refresh token valide
     * @throws com.cheridanh.infradev.exceptions.InvalidTokenException si le token est introuvable ou révoqué
     * @throws com.cheridanh.infradev.exceptions.RefreshTokenExpiredException si le token est expiré
     */
    RefreshToken validateRefreshToken(String token);

    /**
     * Effectue une rotation du refresh token : révoque l'ancien et en crée un nouveau.
     *
     * @param oldRefreshToken le refresh token à remplacer
     * @return le nouveau refresh token
     */
    RefreshToken rotateRefreshToken(RefreshToken oldRefreshToken);

    /**
     * Révoque tous les refresh tokens actifs d'un utilisateur.
     *
     * @param userId l'identifiant de l'utilisateur
     */
    void revokeAllUserTokens(Long userId);

    /**
     * Supprime les tokens expirés et révoqués de la base de données.
     *
     * @return le nombre de tokens supprimés
     */
    int deleteExpiredAndRevokedTokens();
}
