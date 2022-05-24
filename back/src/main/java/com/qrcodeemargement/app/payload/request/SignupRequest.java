package com.qrcodeemargement.app.payload.request;

import com.qrcodeemargement.app.models.Role;

import javax.validation.constraints.*;

public class SignupRequest {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    private Role role;
    
    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String numEtu;
 
    public String getEmail() {
        return email;
    }
 

    public String getPassword() {
        return password;
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
