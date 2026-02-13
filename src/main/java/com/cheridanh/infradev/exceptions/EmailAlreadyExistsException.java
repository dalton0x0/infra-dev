package com.cheridanh.infradev.exceptions;

/**
 * Exception levée lorsqu'une adresse mail existe déjà dans la base de données.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Votre adresse mail : " + email + " n'est pas disponible ou est invalide.");
    }
}
