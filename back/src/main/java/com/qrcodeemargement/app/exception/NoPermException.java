package com.qrcodeemargement.app.exception;

public class NoPermException extends Exception {
    public NoPermException(String roleHave, String roleNeed) {
        super("L'utilisateur n'a pas le bon role il est " + roleHave + " alors que il doit etre " + roleNeed + ".");
    }
}
