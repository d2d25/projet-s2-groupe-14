package com.qrcodeemargement.app;

import com.github.javafaker.Faker;
import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.Role;
import com.qrcodeemargement.app.models.SubGroup;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.repository.GroupRepository;
import com.qrcodeemargement.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.qrcodeemargement.app.UserKey.*;
import static com.qrcodeemargement.app.models.Role.*;

enum UserKey{
    STUDENT_DENEZ,
    STUDENT_BENJAMIN,
    STUDENT_LEO,
    STUDENT_AZIZ,

    TEACHER_CHAPELLE,
    TEACHER_BOICHUT,

    ADMIN_ADMIN,
}

enum GroupKey{

}

@Component
public class InitBdWithFakeData implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    PasswordEncoder encoder;

    private Faker faker;

    private Map<UserKey, User> users;
    private List<User> userList;

    private Map<GroupKey, Group> groups;



    @Override
    public void run(String... args) {
        this.faker = new Faker(new Locale("fr-FR"));

        users = new EnumMap<>(UserKey.class);
        userList = new ArrayList<>();
        initUser(STUDENT_DENEZ, "denez.fauchon@etu.univ-orleans.fr", "123456789", "denez", "fauchon","2195479", ROLE_STUDENT);
        initUser(STUDENT_BENJAMIN, "benjamin.blot@etu.univ-orleans.fr", "123456789", "benjamin", "blot","2190000", ROLE_STUDENT);
        initUser(STUDENT_LEO, "leo.alvarez@etu.univ-orleans.fr", "123456789", "leo", "alvarez","2196714", ROLE_STUDENT);
        initUser(STUDENT_AZIZ, "aziz.benjazia@etu.univ-orleans.fr", "123456789", "aziz", "benjazia","2190003", ROLE_STUDENT);
        initUser(TEACHER_CHAPELLE, "matthieu.chapelle@univ-orleans.fr", "123456789", "matthieu", "chapelle",null, ROLE_TEACHER);
        initUser(TEACHER_BOICHUT, "yohan.boichut@univ-orleans.fr", "123456789", "yohan", "boichut",null, ROLE_TEACHER);
        initUser(ADMIN_ADMIN, "admin.admin@univ-orleans.fr", "123456789", "admin", "admin",null, ROLE_ADMIN);

        groups = new EnumMap<>(GroupKey.class);


    }

    private void initUser(UserKey key, String mail, String password, String firstName, String lastName, String numEtu, Role role){
        if (Boolean.FALSE.equals(userRepository.existsByEmail(mail))){
            User user;
            if (numEtu != null)
                user = new User(mail,encoder.encode(password),firstName,lastName,numEtu,role);
            else
                user = new User(mail,encoder.encode(password),firstName,lastName,role);
            userRepository.save(user);
        }
        if (key == null)
            userList.add(userRepository.findByEmail(mail).get());
        else
            users.put(key,userRepository.findByEmail(mail).get());
    }

    private void initGroup(GroupKey key, String name, Set<User> students, Map<String, Set<User>> subGroups) throws Exception {
        if (Boolean.FALSE.equals(groupRepository.existsByName(name))){
            Group group = new Group(name, students);
            group.setId(UUID.randomUUID().toString());
            for (Map.Entry<String, Set<User>> entry : subGroups.entrySet()) {
                String subGroupName = entry.getKey();
                Set<User> studentsSubGroup = entry.getValue();
                group.addSubGroup(new SubGroup(UUID.randomUUID().toString(), subGroupName, studentsSubGroup));
            }
            groupRepository.save(group);
        }
        groups.put(key,groupRepository.findByName(name).get(0));
    }
    public void getManyUser(int number, Role role) {
        for (int i = 0; i < number; i++) {
            String email = faker.bothify("????##@etu.univ-orleans.fr");
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String password = faker.internet().password(8,20);
            String numEtu = null;
            if (role == ROLE_STUDENT)
                numEtu = Integer.toString(faker.number().numberBetween(2100000,2199999));
            initUser(null,email,password,firstName,lastName,numEtu, role);
        }
    }
}