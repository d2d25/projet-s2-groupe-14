package com.qrcodeemargement.app.payload.request;

import com.qrcodeemargement.app.models.Emargement;

import java.util.List;

public class AttendanceSheetUpdateRequest {

    private String subject;

    private List<EmargementRequest> emargements;

    private boolean isValidate;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<EmargementRequest> getEmargements() {
        return emargements;
    }

    public void setEmargements(List<EmargementRequest> emargements) {
        this.emargements = emargements;
    }

    public boolean getIsValidate() {
        return isValidate;
    }

    public void setIsValidate(boolean validate) {
        isValidate = validate;
    }
}
