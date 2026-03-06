package com.cheridanh.infradev.exceptions;

/**
 * Exception levée lorsqu'une ressource existe déjà dans la base de données.
 */
public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s existe déjà avec %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
