package com.qrcodeemargement.app.exception;

public class BadRoleException extends Exception {
    public BadRoleException(String roleHave, String roleNeed) {
        super("L'utilisateur n'a pas le bon role il est " + roleHave + " alors que il doit etre " + roleNeed + ".");
    }
}
