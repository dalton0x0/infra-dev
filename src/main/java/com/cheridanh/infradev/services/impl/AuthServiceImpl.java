package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.LoginRequest;
import com.cheridanh.infradev.dtos.request.LogoutRequest;
import com.cheridanh.infradev.dtos.request.RefreshTokenRequest;
import com.cheridanh.infradev.dtos.request.RegisterRequest;
import com.cheridanh.infradev.dtos.response.AuthResponse;
import com.cheridanh.infradev.entities.Promotion;
import com.cheridanh.infradev.entities.RefreshToken;
import com.cheridanh.infradev.entities.Role;
import com.cheridanh.infradev.entities.User;
import com.cheridanh.infradev.exceptions.EmailAlreadyExistsException;
import com.cheridanh.infradev.exceptions.InvalidCredentialsException;
import com.cheridanh.infradev.exceptions.UserNotFoundException;
import com.cheridanh.infradev.repositories.PromotionRepository;
import com.cheridanh.infradev.repositories.UserRepository;
import com.cheridanh.infradev.security.UserDetailsImpl;
import com.cheridanh.infradev.services.AuthService;
import com.cheridanh.infradev.services.RefreshTokenService;
import com.cheridanh.infradev.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentative d'inscription d'un nouvel utilisateur");

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Optional<Promotion> activePromotion = promotionRepository.findActiveByDate(LocalDate.now());

        if (activePromotion.isPresent()) {
            log.debug("Promotion active trouvée pour l'inscription : {}", activePromotion.get().getName());
        } else {
            log.debug("Aucune promotion active trouvée pour la date du jour");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .promotion(activePromotion.orElse(null))
                .build();

        userRepository.save(user);

        log.info("Nouvel utilisateur inscrit avec succès, id : {}", user.getId());

        String accessToken = jwtUtil.generateToken(UserDetailsImpl.build(user));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return buildAuthResponse(accessToken, refreshToken.getToken(), user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Tentative de connexion");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            refreshTokenService.revokeAllUserTokens(user.getId());
            String accessToken = jwtUtil.generateToken(UserDetailsImpl.build(user));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            log.info("Connexion réussie pour l'utilisateur id : {}", user.getId());

            return buildAuthResponse(accessToken, refreshToken.getToken(), user);

        } catch (BadCredentialsException ex) {
            log.warn("Échec de connexion : identifiants invalides");
            log.debug("BadCredentialsException : {}", ex.getMessage());
            throw new InvalidCredentialsException();
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.debug("Tentative de renouvellement de token");

        RefreshToken validatedToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        User user = validatedToken.getUser();
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(validatedToken);
        String newAccessToken = jwtUtil.generateToken(UserDetailsImpl.build(user));

        log.info("Tokens renouvelés pour l'utilisateur id : {}", user.getId());

        return buildAuthResponse(newAccessToken, newRefreshToken.getToken(), user);
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        log.debug("Tentative de déconnexion");

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        refreshTokenService.revokeAllUserTokens(refreshToken.getUser().getId());

        log.info("Déconnexion réussie pour l'utilisateur id : {}", refreshToken.getUser().getId());
    }

    /**
     * Construit la réponse d'authentification avec les tokens et les informations utilisateur.
     *
     * @param accessToken le token d'accès JWT
     * @param refreshToken le refresh token
     * @param user l'utilisateur authentifié
     * @return la réponse d'authentification complète
     */
    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .type("Bearer")
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }
}
