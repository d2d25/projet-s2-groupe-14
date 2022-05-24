package com.qrcodeemargement.app.payload.response;

import com.qrcodeemargement.app.models.SubGroup;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SubGroupResponse {
    private final String id;
    private final String name;
    private final Set<UserResponse> students;

    private final String idGroup;

    private SubGroupResponse(String idGroup, SubGroup subGroup) {
        this.id = subGroup.getId();
        this.name = subGroup.getName();
        this.students = new HashSet<>(UserResponse.build(subGroup.getStudents()));
        this.idGroup = idGroup;
    }

    public static SubGroupResponse build(String idGroup, SubGroup subGroup){
        return new SubGroupResponse(idGroup, subGroup);
    }

    public static Collection<SubGroupResponse> build(String idGroup, Collection<SubGroup> values) {
        Collection<SubGroupResponse> subGroupResponses = new HashSet<>();
        values.forEach(value -> subGroupResponses.add(new SubGroupResponse(idGroup,value)));
        return subGroupResponses;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<UserResponse> getStudents() {
        return students;
    }

    public String getIdGroup() {
        return idGroup;
    }
}
