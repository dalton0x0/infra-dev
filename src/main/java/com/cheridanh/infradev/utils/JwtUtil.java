package com.cheridanh.infradev.utils;

import com.cheridanh.infradev.configs.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey signingKey;

    /**
     * Initialise et met en cache la clé de signature HMAC au démarrage.
     */
    @PostConstruct
    void initSigningKey() {
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
        log.info("Clé de signature JWT initialisée avec succès");
    }

    /**
     * Génère un token JWT pour un utilisateur authentifié.
     * Inclut les roles dans les claims du token.
     *
     * @param userDetails les détails de l'utilisateur Spring Security
     * @return le token JWT généré
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);

        log.debug("Génération du token JWT pour : {} avec les roles : {}", userDetails.getUsername(), roles);

        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Extrait le nom d'utilisateur (email) du token JWT.
     *
     * @param token le token JWT
     * @return l'email de l'utilisateur
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token JWT.
     *
     * @param token le token JWT
     * @return la date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait un claim spécifique du token via une fonction de résolution.
     *
     * @param token le token JWT
     * @param claimsResolver la fonction pour extraire le claim souhaité
     * @return la valeur du claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Valide un token JWT : vérifie la signature, l'expiration et la correspondance utilisateur.
     *
     * @param token le token JWT à valider
     * @param userDetails les détails de l'utilisateur attendu
     * @return true si le token est valide
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

            if (!isValid) {
                log.warn("Token JWT invalide : correspondance utilisateur ou expiration échouée");
            }

            return isValid;
        } catch (ExpiredJwtException ex) {
            log.warn("Token JWT expiré lors de la validation");
            log.debug("ExpiredJwtException : {}", ex.getMessage());
            return false;
        } catch (JwtException ex) {
            log.warn("Token JWT malformé lors de la validation : {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Crée un token JWT signé avec les claims, le sujet et la date d'expiration.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtProperties.expiration());

        log.debug("Création du token JWT pour le sujet : {}, expiration : {}", subject, expirationDate);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extrait tous les claims d'un token JWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Vérifie si le refresh token n'est plus valide.
     *
     * @param token le token JWT à vérifier
     * @return true si le token a expiré
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
