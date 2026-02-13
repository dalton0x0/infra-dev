package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.configs.JwtProperties;
import com.cheridanh.infradev.entities.RefreshToken;
import com.cheridanh.infradev.entities.User;
import com.cheridanh.infradev.exceptions.InvalidTokenException;
import com.cheridanh.infradev.exceptions.RefreshTokenExpiredException;
import com.cheridanh.infradev.repositories.RefreshTokenRepository;
import com.cheridanh.infradev.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken savedToken = buildAndSaveRefreshToken(user);
        log.debug("Refresh token créé pour l'utilisateur id : {}", user.getId());
        return savedToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token introuvable ou déjà révoqué");
                    return new InvalidTokenException("Refresh token invalide ou révoqué");
                });

        if (refreshToken.isExpired()) {
            log.warn("Refresh token expiré pour l'utilisateur id : {}", refreshToken.getUser().getId());
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new RefreshTokenExpiredException();
        }

        log.debug("Refresh token validé pour l'utilisateur id : {}", refreshToken.getUser().getId());
        return refreshToken;
    }

    @Override
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldRefreshToken) {
        oldRefreshToken.setRevoked(true);
        refreshTokenRepository.save(oldRefreshToken);
        log.debug("Ancien refresh token révoqué pour rotation, utilisateur id : {}", oldRefreshToken.getUser().getId());
        RefreshToken newToken = buildAndSaveRefreshToken(oldRefreshToken.getUser());
        log.debug("Nouveau refresh token créé pour rotation, utilisateur id : {}", oldRefreshToken.getUser().getId());
        return newToken;
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        log.debug("Tous les refresh tokens révoqués pour l'utilisateur id : {}", userId);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${TOKEN_CLEANUP_CRON}")
    public int deleteExpiredAndRevokedTokens() {
        log.info("Nettoyage des tokens expirés et révoqués");
        int deletedTokenCount = refreshTokenRepository.deleteExpiredAndRevokedTokens(Instant.now());
        log.info("{} tokens expirés et révoqués supprimés", deletedTokenCount);
        return deletedTokenCount;
    }

    private RefreshToken buildAndSaveRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtProperties.refreshExpiration()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }
}
