package com.qrcodeemargement.app.exception;

public class AttendanceSheetNotFound extends Exception {
    public AttendanceSheetNotFound(String idAttendanceSheet) {
        super("La feuille d'emergement avec l'id " + idAttendanceSheet + " n'a pas été trouvé.");
    }
}