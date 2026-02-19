package com.cheridanh.infradev.exceptions;

import com.cheridanh.infradev.dtos.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les erreurs de validation des champs ({@code @Valid}).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Erreur de validation sur {} : {}", request.getRequestURI(), errors);

        ErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST, "Erreur de validation",
                "Un ou plusieurs champs sont invalides", request
        );
        response.setValidationErrors(errors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Gère les erreurs de conversion de type sur les paramètres de requête.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        log.warn("Erreur de type sur {} : {}", request.getRequestURI(), ex.getMessage());

        String message = String.format("Le paramètre '%s' a une valeur invalide: '%s'",
                ex.getName(), ex.getValue());

        return buildErrorResponseEntity(
                HttpStatus.BAD_REQUEST,
                "Erreur de type",
                message,
                request);
    }

    /**
     * Gère les requêtes vers des ressources inexistantes (route non mappée).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpServletRequest request) {

        log.warn("Ressource introuvable sur {} : {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.NOT_FOUND,
                "Ressource introuvable",
                String.format("Aucune ressource trouvée pour %s %s", ex.getHttpMethod(), ex.getResourcePath()),
                request
        );
    }

    /**
     * Gère les requêtes utilisant une méthode HTTP non supportée sur l'endpoint ciblé.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        log.warn("Méthode HTTP non supportée sur {} : {}", request.getRequestURI(), ex.getMessage());

        String message = String.format(
                "La méthode '%s' n'est pas supportée sur cet endpoint. Méthodes acceptées : %s",
                ex.getMethod(),
                ex.getSupportedHttpMethods()
        );

        return buildErrorResponseEntity(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Méthode non supportée",
                message,
                request
        );
    }

    /**
     * Gère les tentatives de connexion avec des identifiants invalides.
     */
    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            RuntimeException ex, HttpServletRequest request) {

        log.warn("Échec d'authentification sur {} : {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.UNAUTHORIZED, "Authentification échouée",
                "Email ou mot de passe incorrect", request
        );
    }

    /**
     * Gère les tentatives d'accès aux ressources non autorisées.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied sur {} : {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.FORBIDDEN,
                "Vous n'avez pas les permissions requises.",
                ex.getMessage(),
                request
        );
    }

    /**
     * Gère les refresh tokens invalides ou révoqués.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request) {

        log.warn("Token invalide sur {} : {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.UNAUTHORIZED, "Token invalide",
                ex.getMessage(), request
        );
    }

    /**
     * Gère les refresh tokens expirés.
     */
    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenExpired(
            RefreshTokenExpiredException ex, HttpServletRequest request) {

        log.warn("Refresh token expiré sur {}", request.getRequestURI());

        return buildErrorResponseEntity(
                HttpStatus.UNAUTHORIZED, "Token expiré",
                ex.getMessage(), request
        );
    }

    /**
     * Gère les comptes désactivés (enabled = false).
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(
            DisabledException ex, HttpServletRequest request) {

        log.warn("Tentative de connexion avec un compte désactivé sur {}", request.getRequestURI());
        log.debug("DisabledException : {}", ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.FORBIDDEN, "Compte désactivé",
                "Votre compte a été désactivé. Veuillez contacter l'administrateur.", request
        );
    }

    /**
     * Gère les comptes verrouillés (accountNonLocked = false).
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(
            LockedException ex, HttpServletRequest request) {

        log.warn("Tentative de connexion avec un compte verrouillé sur {}", request.getRequestURI());
        log.debug("LockedException : {}", ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.FORBIDDEN, "Compte verrouillé",
                "Votre compte est temporairement verrouillé. Veuillez réessayer plus tard.", request
        );
    }

    /**
     * Gère les comptes expirés (accountNonExpired = false).
     */
    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<ErrorResponse> handleAccountExpiredException(
            AccountExpiredException ex, HttpServletRequest request) {

        log.warn("Tentative de connexion avec un compte expiré sur {}", request.getRequestURI());
        log.debug("AccountExpiredException : {}", ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.FORBIDDEN, "Compte expiré",
                "Votre compte a expiré. Veuillez contacter l'administrateur.", request
        );
    }

    /**
     * Gère les identifiants expirés (credentialsNonExpired = false).
     */
    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ErrorResponse> handleCredentialsExpiredException(
            CredentialsExpiredException ex, HttpServletRequest request) {

        log.warn("Tentative de connexion avec des identifiants expirés sur {}", request.getRequestURI());
        log.debug("CredentialsExpiredException : {}", ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.FORBIDDEN, "Identifiants expirés",
                "Votre mot de passe a expiré. Veuillez le réinitialiser.", request
        );
    }

    /**
     * Gère les tentatives d'inscription avec un email déjà existant.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex, HttpServletRequest request) {

        log.warn("Tentative d'inscription avec un email existant sur {}", request.getRequestURI());

        return buildErrorResponseEntity(
                HttpStatus.CONFLICT, "Conflit",
                ex.getMessage(), request
        );
    }

    /**
     * Gère les utilisateurs non trouvés.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {

        log.warn("Utilisateur non trouvé sur {} : {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.NOT_FOUND, "Utilisateur non trouvé",
                ex.getMessage(), request
        );
    }

    /**
     * Gère les promotions non trouvées.
     */
    @ExceptionHandler(PromotionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePromotionNotFound(
            PromotionNotFoundException ex, HttpServletRequest request) {

        log.warn("Promotion non trouvée sur {} : {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.NOT_FOUND, "Promotion non trouvée",
                ex.getMessage(), request
        );
    }

    /**
     * Gère les duplications de ressources.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex, HttpServletRequest request) {

        log.warn("Duplication de ressource sur {} : {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponseEntity(
                HttpStatus.CONFLICT,
                "Conflit",
                ex.getMessage(),
                request
        );
    }

    /**
     * Gère toutes les exceptions non prévues.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Erreur interne sur {} : {}", request.getRequestURI(), ex.getMessage(), ex);

        return buildErrorResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne",
                "Une erreur inattendue s'est produite", request
        );
    }

    /**
     * Construit un {@link ErrorResponse} avec les informations communes.
     */
    private ErrorResponse buildErrorResponse(
            HttpStatus status, String error, String message, HttpServletRequest request) {

        return ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Construit un {@link ResponseEntity} contenant un {@link ErrorResponse}.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponseEntity(
            HttpStatus status, String error, String message, HttpServletRequest request) {

        return ResponseEntity.status(status).body(
                buildErrorResponse(status, error, message, request)
        );
    }
}
