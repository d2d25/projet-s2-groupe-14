package com.qrcodeemargement.app.models.fabrick;

import com.qrcodeemargement.app.models.Role;
import com.qrcodeemargement.app.models.User;
import org.springframework.stereotype.Component;

@Component
public class FabricUser {
    public User build(String email, String password, String firstName, String lastName, String numEtu, Role role){
        return new User(email,password,firstName,lastName,numEtu,role);
    }
}
