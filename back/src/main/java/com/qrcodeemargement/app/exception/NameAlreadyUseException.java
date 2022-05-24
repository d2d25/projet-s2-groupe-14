package com.qrcodeemargement.app.exception;

public class NameAlreadyUseException extends Exception {
    public NameAlreadyUseException(String name) {
        super("Le nom " +name + " est deja utilisé.");
    }
}
