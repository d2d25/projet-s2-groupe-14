package com.qrcodeemargement.app.payload.request;

import java.util.Set;

public class GroupUpdateRequest {

    private String name;

    private Set<String> students;

    public String getName() {
        return name;
    }

    public Set<String> getStudents() {
        return students;
    }
}
