package com.qrcodeemargement.app.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String idUserOrMail) {
        super("L'Utilisateur avec l'id ou l'email " + idUserOrMail + "nexiste pas");
    }
}
