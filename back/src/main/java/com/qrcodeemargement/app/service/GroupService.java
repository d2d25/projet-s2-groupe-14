package com.qrcodeemargement.app.service;

import com.qrcodeemargement.app.exception.*;
import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.SubGroup;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.models.fabrick.FabricGroup;
import com.qrcodeemargement.app.models.fabrick.FabricSubGroup;
import com.qrcodeemargement.app.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserService userService;

    @Autowired
    FabricGroup fabricGroup;

    @Autowired
    FabricSubGroup fabricSubGroup;


    public Group saveGroup(String name, Set<String> studentsStr ) throws NameAlreadyUseException, UserNotFoundException, UserIsNotStudentException {
        if(Boolean.TRUE.equals(groupRepository.existsByName(name))){
            throw new  NameAlreadyUseException(name);
        }
        Set<User> students = new HashSet<>();
        for (String s : studentsStr) {
            students.add(userService.getById(s));
        }
        Group group = fabricGroup.buid(name, students);
        return groupRepository.save(group);
    }

    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    public Group getGroupById(String id) throws GroupNotFoundException {
        Optional<Group> group = groupRepository.findById(id);
        if (group.isEmpty()) {
            throw new GroupNotFoundException(id);
        }
        return group.get();
    }

    public SubGroup getSubGroupById(String idGroup, String idSubGroup) throws GroupNotFoundException, SubGroupNotFoundException {
        Group group = getGroupById(idGroup);
        return group.getSubGroup(idSubGroup);
    }


    public SubGroup addSubGroupToGroup(String idGroup, String nameSubGroup, Set<String> studentsStr) throws GroupNotFoundException, UserNotFoundException, NameAlreadyUseException, UserAddInSubGroupNotExistInGroup {
        Group group = getGroupById(idGroup);
        Set<User> students = new HashSet<>();
        for (String s : studentsStr) {
            students.add(userService.getById(s));
        }
        SubGroup subGroup = fabricSubGroup.buid(nameSubGroup, students);
        group.addSubGroup(subGroup);
        groupRepository.save(group);
        return subGroup;
    }

    public Group patch(String idGroup, String name, Set<String> studentsStr) throws NameAlreadyUseException, GroupNotFoundException, UserNotFoundException, UserIsNotStudentException {
        Group group = getGroupById(idGroup);
        if (name != null && !name.isEmpty()){
            List<Group> groups = groupRepository.findByName(name);
            for (Group group1 : groups) {
                if (!group1.getId().equals(group.getId()))
                    throw new NameAlreadyUseException(name);

            }
            group.setName(name);
        }
        if (studentsStr != null && !studentsStr.isEmpty()){
            Set<User> students = new HashSet<>();
            for (String s : studentsStr) {
                students.add(userService.getById(s));
            }
            group.setStudents(students);
        }
        return groupRepository.save(group);
    }

    public SubGroup patchSubGroup(String idGroup,String idSubGroup, String name, Set<String> studentsStr) throws NameAlreadyUseException, GroupNotFoundException, UserNotFoundException, SubGroupNotFoundException, UserAddInSubGroupNotExistInGroup {
        Group group = getGroupById(idGroup);
        if (name != null && !name.isEmpty()){
            group.setNameInSubGroup(idSubGroup,name);
        }

        if (studentsStr != null && !studentsStr.isEmpty()){
            Set<User> students = new HashSet<>();
            for (String s : studentsStr) {
                students.add(userService.getById(s));
            }
            group.setStudentsInSubGroup(idSubGroup, students);
        }
        groupRepository.save(group);
        return getSubGroupById(idGroup, idSubGroup);
    }

    public void delete(String idGroup) throws GroupNotFoundException {
        groupRepository.delete(getGroupById(idGroup));
    }

    public void deleteSubGroup(String idGroup,String idSubGroup) throws GroupNotFoundException, SubGroupNotFoundException {
        Group group = getGroupById(idGroup);
        group.removeSubGroup(idSubGroup);
        groupRepository.save(group);
    }
}
