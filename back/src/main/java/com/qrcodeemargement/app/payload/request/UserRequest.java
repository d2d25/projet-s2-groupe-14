package com.qrcodeemargement.app.payload.request;

import com.qrcodeemargement.app.models.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UserRequest {

    @Size(max = 50)
    @Email
    private String email;

    private Role role;

    private String firstName;

    private String lastName;

    private String numEtu;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNumEtu() {
        return numEtu;
    }

    public Role getRole() {
        return role;
    }
}
