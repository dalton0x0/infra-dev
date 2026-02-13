package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Recherche un refresh token valide (non révoqué) par sa valeur.
     *
     * @param token la valeur du refresh token
     * @return un Optional contenant le refresh token s'il existe et n'est pas révoqué
     */
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    /**
     * Révoque tous les refresh tokens actifs d'un utilisateur (logout global).
     *
     * @param userId l'identifiant de l'utilisateur
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user.id = :userId AND rt.revoked = false")
    void revokeAllByUserId(@Param("userId") Long userId);

    /**
     * Supprime les refresh tokens expirés ou révoqués avant une date donnée.
     *
     * @param expiryDate la date limite
     * @return le nombre de tokens supprimés
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true OR rt.expiryDate < :expiryDate")
    int deleteExpiredAndRevokedTokens(@Param("expiryDate") Instant expiryDate);
}
