package com.cheridanh.infradev.exceptions;

/**
 * Exception levée lorsqu'un utilisateur recherché n'est pas trouvé dans la base de données.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("Utilisateur non trouvé avec l'email : " + email);
    }
}
