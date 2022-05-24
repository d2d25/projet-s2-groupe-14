package com.qrcodeemargement.app.payload.request;

import javax.validation.constraints.Size;

public class PasswordRequest {
    @Size(min = 8)
    private String password;

    public String getPassword() {
        return password;
    }
}
