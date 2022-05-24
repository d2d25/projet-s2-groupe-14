package com.qrcodeemargement.app.exception;

import com.qrcodeemargement.app.models.Role;

public class UserIsNotStudentException extends Exception {
    public UserIsNotStudentException(String idUser) {
        super("L'utilisateur avec l'id " + idUser + "n'est pas un "+ Role.ROLE_STUDENT +".");
    }
}
