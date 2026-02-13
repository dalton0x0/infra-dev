package com.cheridanh.infradev.exceptions;

/**
 * Exception levée lorsque les informations d'identification ne sont pas valides.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Email ou mot de passe incorrect");
    }
}
