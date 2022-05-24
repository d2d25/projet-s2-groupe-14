package com.qrcodeemargement.app.exception;

public class UnknownRoleException extends Exception {
    private final String role;
    public UnknownRoleException(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}
