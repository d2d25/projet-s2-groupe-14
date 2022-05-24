package com.qrcodeemargement.app.exception;

public class UserNotFoundInAttendanceSheetException extends Exception {
    public UserNotFoundInAttendanceSheetException(String id) {
        super("L'utilisateur avec l'id " + id + " n'est pas present dans cette feuille d'emargement");
    }
}
