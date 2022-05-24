package com.qrcodeemargement.app.exception;

public class SubGroupNotFoundException extends Exception {
    public SubGroupNotFoundException(String idGroup, String idSubGroup) {
        super("Le sous groupe avec l'id " + idSubGroup + " n'est pas present dans le groupe avec l'id "+ idGroup + ".");
    }
}
