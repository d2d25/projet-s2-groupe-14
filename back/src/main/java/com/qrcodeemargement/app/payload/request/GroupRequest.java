package com.qrcodeemargement.app.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class GroupRequest {
    @NotBlank
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private Set<String> students;

    public String getName() {
        return name;
    }

    public Set<String> getStudents() {
        return students;
    }
}
