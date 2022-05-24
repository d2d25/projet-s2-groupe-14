package com.qrcodeemargement.app.exception;

public class GroupNotFoundException extends Exception {
    public GroupNotFoundException(String idGroup) {
        super("Le groupe avec l'id " + idGroup + " n'existe pas.");
    }
}
