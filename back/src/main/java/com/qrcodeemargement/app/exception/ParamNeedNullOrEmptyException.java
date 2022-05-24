package com.qrcodeemargement.app.exception;

public class ParamNeedNullOrEmptyException extends Exception {
    public ParamNeedNullOrEmptyException(String paramName, String in) {
        super("Le parametre " + paramName + " dans " + in + " ne doit pa etre null ou vide.");
    }
}
