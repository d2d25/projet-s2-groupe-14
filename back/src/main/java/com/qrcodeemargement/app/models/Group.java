package com.qrcodeemargement.app.models;

import com.qrcodeemargement.app.exception.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "group")
public class Group {
    private String id;
    private String name;
    private Set<User> students;
    private Map<String,SubGroup> subGroups;


    public Group() {
    }

    public Group(String name, Set<User> students) throws UserIsNotStudentException {
        this.name = name;
        this.subGroups = new HashMap<>();
        this.setStudents(students);
    }

    public Group(Group group) {
        this.name = group.getName();
        this.id = group.getId();
        this.students = new HashSet<>(group.getStudents());
        this.subGroups = new HashMap<>(group.getSubGroups());
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

    public void setStudents(Set<User> students) throws UserIsNotStudentException {
        for (User student : students) {
            if (student.getRole() != Role.ROLE_STUDENT)
                throw new UserIsNotStudentException(student.getId());
        }
        this.students = students;

        this.subGroups.forEach((s, subGroup) -> {
            Set<User> studentsSubGroupCopy = new HashSet<>(subGroup.getStudents());
            studentsSubGroupCopy.forEach(user -> {
                if (!this.students.contains(user))
                    subGroup.getStudents().remove(user);
            });
        });
    }

    public Map<String, SubGroup> getSubGroups() {
        return subGroups;
    }

    public void addSubGroup(SubGroup subGroup) throws NameAlreadyUseException, UserAddInSubGroupNotExistInGroup {
        for (SubGroup subGroup1 : subGroups.values()){
            if (subGroup1.getName().equals(subGroup.getName()))
                throw new NameAlreadyUseException(subGroup.getName());
        }
        for (User student : subGroup.getStudents()){
            if (!students.contains(student))
                throw new UserAddInSubGroupNotExistInGroup(student.getId());
        }
        this.subGroups.put(subGroup.getId(), subGroup);
    }

    public void removeSubGroup(String idSubGroup) throws SubGroupNotFoundException {
        getSubGroup(idSubGroup);
        this.subGroups.remove(idSubGroup);
    }

    public void setStudentsInSubGroup(String idSubGroup, Set<User> studentsSubGroup) throws SubGroupNotFoundException, UserAddInSubGroupNotExistInGroup {
        SubGroup subGroup = getSubGroup(idSubGroup);

        for (User student : studentsSubGroup){
            if (!students.contains(student))
                throw new UserAddInSubGroupNotExistInGroup(student.getId());
        }
        subGroup.setStudents(studentsSubGroup);
    }

    public void setNameInSubGroup(String idSubGroup, String nameSubGroup) throws SubGroupNotFoundException, NameAlreadyUseException {
        SubGroup subGroup = getSubGroup(idSubGroup);

        for (SubGroup subGroup1 : subGroups.values()){
            if (!idSubGroup.equals(subGroup1.getId()) && subGroup1.getName().equals(nameSubGroup))
                throw new NameAlreadyUseException(nameSubGroup);
        }
        subGroup.setName(nameSubGroup);
    }

    public SubGroup getSubGroup(String idSubGroup) throws SubGroupNotFoundException {
        if (!this.subGroups.containsKey(idSubGroup))
            throw new SubGroupNotFoundException(id, idSubGroup);
        return this.subGroups.get(idSubGroup);
    }

    public boolean containsSubGroup(SubGroup subGroup){
        return subGroups.containsValue(subGroup);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id) && Objects.equals(name, group.name) && Objects.equals(students, group.students) && Objects.equals(subGroups, group.subGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, students, subGroups);
    }
}
