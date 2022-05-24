package com.qrcodeemargement.app.models;

import java.util.Objects;
import java.util.Set;

public class SubGroup {
    private String id;
    private String name;
    private Set<User> students;

    public SubGroup() {
    }

    public SubGroup(String id, String name, Set<User> students) {
        this.id = id;
        this.name = name;
        this.students = students;
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

    public Set<User> getStudents() {
        return students;
    }

    public void setStudents(Set<User> students) {
        this.students = students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubGroup subGroup = (SubGroup) o;
        return Objects.equals(id, subGroup.id) && Objects.equals(name, subGroup.name) && Objects.equals(students, subGroup.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, students);
    }
}
