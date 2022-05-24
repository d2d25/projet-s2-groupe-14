package com.qrcodeemargement.app.utils;

import com.github.javafaker.Faker;
import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.SubGroup;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.repository.GroupRepository;
import org.mockito.Mockito;

import java.util.*;

import static com.qrcodeemargement.app.models.Role.ROLE_STUDENT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class GroupMockAndData {


    private final Map<GroupsKey, Group> groups;
    private final UserMockAndData userMockAndData;
    private final GroupRepository groupRepository;
    private final Faker faker;

    public GroupMockAndData(GroupRepository groupRepository, UserMockAndData userMockAndData) {
        this.groupRepository = groupRepository;
        this.userMockAndData = userMockAndData;
        this.faker = new Faker(new Locale("fr-FR"));
        groups = new HashMap<>();
    }

    public Group get(GroupsKey key) throws Exception {
        switch (key)  {
            case GROUP1 -> {
                if (groups.get(key) == null){
                    Set<User> studentsInGroup = userMockAndData.getManyUser(faker.number().numberBetween(5,20), ROLE_STUDENT);
                    Map<String, Set<User>> subGroups = new HashMap<>();
                    subGroups.put("Tp1", generateSubSet(studentsInGroup, 4));
                    subGroups.put("Tp2", generateSubSet(studentsInGroup, 4));
                    subGroups.put("Tp3", generateSubSet(studentsInGroup, 4));
                    subGroups.put("Tp4", generateSubSet(studentsInGroup, 4));
                    init(key,"Master Miage M1",studentsInGroup, subGroups);
                }
                return groups.get(key);
            }
            case GROUP2 -> {
                if (groups.get(key) == null){
                    Set<User> studentsInGroup = userMockAndData.getManyUser(faker.number().numberBetween(5,20), ROLE_STUDENT);
                    Map<String, Set<User>> subGroups = new HashMap<>();
                    subGroups.put("Tp1", generateSubSet(studentsInGroup, 4));
                    subGroups.put("Tp2", generateSubSet(studentsInGroup, 4));
                    subGroups.put("Tp3", generateSubSet(studentsInGroup, 4));
                    subGroups.put("Tp4", generateSubSet(studentsInGroup, 4));
                    init(key,"Master Miage M2",studentsInGroup, subGroups);
                }
                return groups.get(key);
            }
            default -> throw new Exception();
        }
    }

    public Set<User> generateSubSet(Set<User> set, int min){
        List<User> list = new ArrayList<>(set);
        Collections.shuffle(list);
        return new HashSet<>(list.subList(0,faker.number().numberBetween(min, list.size())-1));
    }

    public Map<GroupsKey, Group> getAll() throws Exception {
        GroupsKey[] values = GroupsKey.values();
        for (GroupsKey value : values) {
            get(value);
        }
        return groups;
    }

    private void init(GroupsKey key, String name, Set<User> students, Map<String, Set<User>> subGroups) throws Exception {
        groups.put(key, new Group(name, students));
        groups.get(key).setId(UUID.randomUUID().toString());
        for (Map.Entry<String, Set<User>> entry : subGroups.entrySet()) {
            String subGroupName = entry.getKey();
            Set<User> studentsSubGroup = entry.getValue();
            groups.get(key).addSubGroup(new SubGroup(UUID.randomUUID().toString(), subGroupName, studentsSubGroup));
        }

        Mockito.when(groupRepository.findById(groups.get(key).getId())).thenReturn(Optional.of(groups.get(key)));
        Mockito.when(groupRepository.existsByName(name)).thenReturn(true);
        List<Group> groupList = new ArrayList<>();
        groupList.add(groups.get(key));
        Mockito.when(groupRepository.findByName(name)).thenReturn(groupList);

        Mockito.when(groupRepository.findAll()).thenReturn(new ArrayList<>(groups.values()));
    }



}
