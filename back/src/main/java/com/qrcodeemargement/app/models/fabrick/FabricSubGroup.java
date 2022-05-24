package com.qrcodeemargement.app.models.fabrick;

import com.qrcodeemargement.app.models.SubGroup;
import com.qrcodeemargement.app.models.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class FabricSubGroup {
    public SubGroup buid(String name, Set<User> students) {
        String id = UUID.randomUUID().toString();
        if (students == null)
            students = new HashSet<>();
        return new SubGroup(id, name, students);
    }
}
