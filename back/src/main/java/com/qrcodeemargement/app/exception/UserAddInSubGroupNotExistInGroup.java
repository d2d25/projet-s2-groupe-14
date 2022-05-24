package com.qrcodeemargement.app.exception;

public class UserAddInSubGroupNotExistInGroup extends Exception {
    public UserAddInSubGroupNotExistInGroup(String idUser) {
        super("L'utilisateur avec l'id " + idUser + " ne peut pas etre ajouter dans le sous groupe car il n'est pas present dans le groupe parent.");
    }
}
