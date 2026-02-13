package com.cheridanh.infradev.exceptions;

/**
 * Exception levée lorsque le refresh token a expiré.
 */
public class RefreshTokenExpiredException extends RuntimeException {

    public RefreshTokenExpiredException() {
        super("Le refresh token a expiré, veuillez vous reconnecter");
    }
}
