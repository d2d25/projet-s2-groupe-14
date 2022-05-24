package com.qrcodeemargement.app.payload.response;

import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.SubGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GroupResponse {

    private String id;

    private String name;

    private Set<UserResponse> students;

    private Set<SubGroupResponse> subGroups;

    private GroupResponse(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.students = new HashSet<>(UserResponse.build(group.getStudents()));
        this.subGroups = new HashSet<>(SubGroupResponse.build(id,group.getSubGroups().values()));
    }

    public static Collection<GroupResponse> build(Collection<Group> groups){
        Collection<GroupResponse> res = new ArrayList<>();
        groups.forEach(group -> res.add(new GroupResponse(group)));
        return res;
    }

    public static GroupResponse build(Group group){
        return new GroupResponse(group);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserResponse> getStudents() {
        return students;
    }

    public void setStudents(Set<UserResponse> students) {
        this.students = students;
    }

    public Set<SubGroupResponse> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(Set<SubGroupResponse> subGroups) {
        this.subGroups = subGroups;
    }
}
