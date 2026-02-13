package com.cheridanh.infradev.configs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(

        @NotBlank(message = "La clé secrète JWT est obligatoire")
        String secret,

        @Positive(message = "La durée d'expiration du token doit être positive")
        long expiration,

        @Positive(message = "La durée d'expiration du refresh token doit être positive")
        long refreshExpiration

) {
}
