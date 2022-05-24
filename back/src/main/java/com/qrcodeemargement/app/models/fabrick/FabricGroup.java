package com.qrcodeemargement.app.models.fabrick;

import com.qrcodeemargement.app.exception.UserIsNotStudentException;
import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.User;
import org.springframework.stereotype.Component;


import java.util.HashSet;
import java.util.Set;

@Component
public class FabricGroup {
    public Group buid(String name, Set<User> students) throws UserIsNotStudentException {
        if (students == null)
            students = new HashSet<>();
        return new Group( name, students);
    }
}
