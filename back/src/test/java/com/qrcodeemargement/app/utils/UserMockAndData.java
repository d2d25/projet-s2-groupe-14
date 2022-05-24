package com.qrcodeemargement.app.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.qrcodeemargement.app.models.Role;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.repository.UserRepository;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.*;

import static com.qrcodeemargement.app.utils.UserData.*;
import static com.qrcodeemargement.app.utils.UserData.lastNameAdmin;
import static com.qrcodeemargement.app.controllers.AuthController.URL_AUTH;
import static com.qrcodeemargement.app.controllers.AuthController.SIGNIN;
import static com.qrcodeemargement.app.utils.UsersKey.*;
import static com.qrcodeemargement.app.models.Role.*;
import static com.qrcodeemargement.app.models.Role.ROLE_ADMIN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserMockAndData {

    public static final String EMAIL_STR = "email";
    public static final String PASSWORD_STR = "password";
    private final Map<UsersKey, String> tokens;
    private final Map<UsersKey, User> users;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final MockMvc mvc;

    private final Faker faker;

    public UserMockAndData(UserRepository userRepository, PasswordEncoder encoder, MockMvc mvc) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.mvc = mvc;
        users = new HashMap<>();
        tokens = new HashMap<>();
        this.faker = new Faker(new Locale("fr-FR"));
    }

    public User get(UsersKey key) throws Exception {
        switch (key)  {
            case STUDENT1 -> {
                if (users.get(key) == null)
                    initAndConnect(mailStudent1(), PASSWORD,firstNameStudent(),lastNameStudent(),numEtu(),ROLE_STUDENT, STUDENT1);
                return users.get(key);
            }
            case STUDENT2 -> {
                if (users.get(key) == null)
                    initAndConnect(mailStudent2(), PASSWORD,firstNameStudent(),lastNameStudent(),numEtu(),ROLE_STUDENT, STUDENT2);
                return users.get(key);
            }
            case TEACHER1 -> {
                if (users.get(key) == null)
                    initAndConnect(mailTeacher1(), PASSWORD, firstNameTeacher(), lastNameTeacher(), null, ROLE_TEACHER, TEACHER1);
                return users.get(key);
            }
            case TEACHER2 -> {
                if (users.get(key) == null)
                    initAndConnect(mailTeacher2(), PASSWORD, firstNameTeacher(), lastNameTeacher(), null, ROLE_TEACHER, TEACHER2);
                return users.get(key);
            }
            case ADMIN1 -> {
                if (users.get(key) == null)
                    initAndConnect(mailAdmin1(), PASSWORD, firstNameAdmin(), lastNameAdmin(), null, ROLE_ADMIN, ADMIN1);
                return users.get(key);
            }
            case ADMIN2 -> {
                if (users.get(key) == null)
                    initAndConnect(mailAdmin2(), PASSWORD, firstNameAdmin(), lastNameAdmin(), null, ROLE_ADMIN, ADMIN2);
                return users.get(key);
            }
            default -> throw new Exception();
        }
    }

    public String getToken(UsersKey key) throws Exception {
        switch (key)  {
            case STUDENT1 -> {
                if (tokens.get(key) == null)
                    initAndConnect(mailStudent1(), PASSWORD,firstNameStudent(),lastNameStudent(),numEtu(),ROLE_STUDENT, STUDENT1);
                return tokens.get(key);
            }
            case STUDENT2 -> {
                if (tokens.get(key) == null)
                    initAndConnect(mailStudent2(), PASSWORD,firstNameStudent(),lastNameStudent(),numEtu(),ROLE_STUDENT, STUDENT2);
                return tokens.get(key);
            }
            case TEACHER1 -> {
                if (tokens.get(key) == null)
                    initAndConnect(mailTeacher1(), PASSWORD, firstNameTeacher(), lastNameTeacher(), null, ROLE_TEACHER, TEACHER1);
                return tokens.get(key);
            }
            case TEACHER2 -> {
                if (tokens.get(key) == null)
                    initAndConnect(mailTeacher2(), PASSWORD, firstNameTeacher(), lastNameTeacher(), null, ROLE_TEACHER, TEACHER2);
                return tokens.get(key);
            }
            case ADMIN1 -> {
                if (tokens.get(key) == null)
                    initAndConnect(mailAdmin1(), PASSWORD, firstNameAdmin(), lastNameAdmin(), null, ROLE_ADMIN, ADMIN1);
                return tokens.get(key);
            }
            case ADMIN2 -> {
                if (tokens.get(key) == null)
                    initAndConnect(mailAdmin2(), PASSWORD, firstNameAdmin(), lastNameAdmin(), null, ROLE_ADMIN, ADMIN2);
                return tokens.get(key);
            }
            default -> throw new Exception();
        }
    }

    public Map<UsersKey, User> getAll() throws Exception {
        UsersKey[] values = UsersKey.values();
        for (UsersKey value : values) {
            get(value);
        }
        return users;
    }

    public Set<User> getManyUser(int number, Role role) {
        Set<User> users = new HashSet<>();
        for (int i = 0; i < number; i++) {
            String email = faker.bothify("????##@etu.univ-orleans.fr");
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String password = faker.internet().password(8,20);
            String numEtu = null;
            if (role == ROLE_STUDENT)
                numEtu = Integer.toString(faker.number().numberBetween(2100000,2199999));
            users.add(init(email,password,firstName,lastName,numEtu, role));
        }
        return users;
    }

    private void initAndConnect(String mail, String password, String firstName, String lastName, String numEtu, Role role, UsersKey userKey) throws Exception {
        User user = new User(mail, encoder.encode(password), firstName, lastName, numEtu, role);
        user.setId(UUID.randomUUID().toString());

        Mockito.when(userRepository.findByEmail(mail)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsByEmail(mail)).thenReturn(true);
        users.put(userKey, user);

        Map<String, Object> usermap = new HashMap<>();
        usermap.put(EMAIL_STR, mail);
        usermap.put(PASSWORD_STR, password);

        MvcResult mvcResult = mvc.perform(post(URI.create(URL_AUTH +SIGNIN))
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usermap)))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        HashMap<String, Object> contentMap = new ObjectMapper().readValue(content, new TypeReference<>() {
        });
        tokens.put(userKey,contentMap.get("token").toString());

        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>(users.values()));
    }

    private User init(String mail, String password, String firstName, String lastName, String numEtu, Role role){

        User user = new User(mail, encoder.encode(password), firstName, lastName, numEtu, role);
        user.setId(UUID.randomUUID().toString());
        Mockito.when(userRepository.findByEmail(mail)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsByEmail(mail)).thenReturn(true);
        return user;
    }

}
