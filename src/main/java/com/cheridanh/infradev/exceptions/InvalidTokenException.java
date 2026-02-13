package com.cheridanh.infradev.exceptions;

/**
 * Exception levée lorsque le token est invalide, révoqué, vide ou n'existe pas dans la base de données.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
