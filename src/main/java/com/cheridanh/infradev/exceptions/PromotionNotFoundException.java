package com.cheridanh.infradev.exceptions;

/**
 * Exception levée lorsqu'une promotion n'est pas trouvée en base de données
 */
public class PromotionNotFoundException extends RuntimeException {

    public PromotionNotFoundException(Long id) {
        super(String.format("Promotion introuvable avec l'id : %d", id));
    }
}
