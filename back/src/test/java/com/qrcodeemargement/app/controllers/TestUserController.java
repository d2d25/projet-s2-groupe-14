package com.qrcodeemargement.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.repository.UserRepository;
import com.qrcodeemargement.app.utils.UserMockAndData;
import com.qrcodeemargement.app.utils.UsersKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.*;

import static com.qrcodeemargement.app.utils.UserData.wrongPassword;
import static com.qrcodeemargement.app.controllers.UserController.*;
import static com.qrcodeemargement.app.utils.UsersKey.*;
import static com.qrcodeemargement.app.models.Role.*;
import static com.qrcodeemargement.app.utils.UserData.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class TestUserController {
    public static final String EMAIL_STR = "email";
    public static final String PASSWORD_STR = "password";
    public static final String FIRS_NAME_STR = "firstName";
    public static final String LAST_NAME_STR = "lastName";
    public static final String NUM_ETU_STR = "numEtu";
    public static final String ROLE_STR = "role";
    


    @Autowired
    MockMvc mvc;

    @MockBean
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    
    UserMockAndData userMockAndData;

    FieldDescriptor[] userDescriptor;

    @BeforeEach
    private void init() {
        userMockAndData = new UserMockAndData(userRepository,encoder, mvc);
        userDescriptor = new FieldDescriptor[] {
                fieldWithPath("id").description("ID de l'utilisateur"),
                fieldWithPath("email").description("Email de l'utilisateur"),
                fieldWithPath("firstName").description("Prénom de l'utilisateur"),
                fieldWithPath("lastName").description("Nom de l'utilisateur"),
                fieldWithPath("role").description("Role de l'utilisateur"),
                fieldWithPath("numEtu").description("Numéro Etudiant de l'utilisateur").optional(),
        };


    }

    /**
     * Récupérer tous les users (Admin) : isOK
     */
    @Test
    void getAll_1() throws Exception {
        Map<UsersKey, User> allUser = userMockAndData.getAll();
        mvc.perform(get(URI.create(URL_USER))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath("$", hasSize(allUser.size())))
                .andDo(document("user/getAllUser",
                        responseFields(
                                fieldWithPath("[]").description("Liste d'utilisateur"))
                                .andWithPrefix("[].", userDescriptor)
                ));
    }

    /**
     * Récupérer tous les utilisateurs (Student) : isForbidden
     */
    @Test
    void getAll_2() throws Exception {
        mvc.perform(get(URI.create(URL_USER))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupérer tous les utilisateurs (Teacher) : isForbidden
     */
    @Test
    void getAllByTeacher() throws Exception {
        mvc.perform(get(URI.create(URL_USER))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupérer tous les utilisateurs (unConnected) :  isUnauthorized
     */
    @Test
    void getAllByNoConnectedUser() throws Exception {
        mvc.perform(get(URI.create(URL_USER)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Récupérer tous les utilisateurs par role (unConnected) : isUnauthorized
     */
    @Test
    void getAllByRole_1() throws Exception {
        mvc.perform(get(URI.create(URL_USER + BY_ROLE)))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Récupérer tous les utilisateurs par role (Admin) : isOk
     * Params: ROLE_STUDENT
     */
    @Test
    void getAllByRole_2() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(userMockAndData.get(STUDENT1));
        userList.add(userMockAndData.get(STUDENT2));
        Mockito.when(userRepository.findByRole(ROLE_STUDENT)).thenReturn(userList);
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_STUDENT.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath("$", hasSize(userList.size())))
                .andDo(document("user/getAllByRole",
                        requestParameters(parameterWithName(ROLE_STR).description("Role des utilisateurs demandés"))))
                .andDo(document("user/getAllByRole",
                        responseFields(
                                fieldWithPath("[]").description("Liste d'utilisateur par role"))
                                .andWithPrefix("[].", userDescriptor)
                ));
    }

    /**
     * Récupérer tous les utilisateurs par role (Admin) : isOk
     * Params: ROLE_AMDIN
     */
    @Test
    void getAllByRole_3() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(userMockAndData.get(ADMIN2));
        userList.add(userMockAndData.get(ADMIN1));
        Mockito.when(userRepository.findByRole(ROLE_ADMIN)).thenReturn(userList);
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_ADMIN.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath("$", hasSize(userList.size())));
    }

    /**
     * Récupérer tous les utilisateurs par role (Admin) : isOk
     * Params: ROLE_TEACHER
     */
    @Test
    void getAllByRole_4() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(userMockAndData.get(TEACHER1));
        userList.add(userMockAndData.get(TEACHER2));
        Mockito.when(userRepository.findByRole(ROLE_TEACHER)).thenReturn(userList);
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_TEACHER.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath("$", hasSize(userList.size())));
    }

    /**
     * Récupérer tous les utilisateurs par role (Teacher) : isOk
     * Params: ROLE_STUDENT
     */
    @Test
    void getAllByRole_5() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(userMockAndData.get(STUDENT1));
        userList.add(userMockAndData.get(STUDENT2));
        Mockito.when(userRepository.findByRole(ROLE_STUDENT)).thenReturn(userList);
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_STUDENT.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath("$", hasSize(userList.size())));
    }

    /**
     * Récupérer tous les utilisateurs par role (Teacher) : isForbidden
     * Params: ROLE_ADMIN
     */
    @Test
    void getAllByRole_6() throws Exception {
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_ADMIN.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupérer tous les utilisateurs par role (Teacher) : isForbidden
     * Params: ROLE_TEACHER
     */
    @Test
    void getAllByRole_7() throws Exception {
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_TEACHER.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupérer tous les utilisateurs par role (Student) : isForbidden
     * Params: ROLE_STUDENT
     */
    @Test
    void getAllByRole_8() throws Exception {
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_STUDENT.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupérer tous les utilisateurs par role (Student) : isForbidden
     * Params: ROLE_ADMIN
     */
    @Test
    void getAllByRole_9() throws Exception {
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_ADMIN.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupérer tous les utilisateurs par role (Student) : isForbidden
     * Params: ROLE_TEACHER
     */
    @Test
    void getAllByRole_10() throws Exception {
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, ROLE_STUDENT.toString())
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupérer tous les utilisateurs par role (Admin) : isBadRequest
     * Params: role = null
     */
    @Test
    void getAllByRole_11() throws Exception {
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Récupérer tous les utilisateurs par role (Admin) : isBadRequest
     * Params: role = ""
     */
    @Test
    void getAllByRole_12() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(userMockAndData.get(ADMIN2));
        userList.add(userMockAndData.get(ADMIN1));
        Mockito.when(userRepository.findByRole(ROLE_ADMIN)).thenReturn(userList);
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, "")
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Récupérer tous les utilisateurs par role (Admin) : isBadRequest role innexistant
     */
    @Test
    void getAllByRole_13() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(userMockAndData.get(ADMIN2));
        userList.add(userMockAndData.get(ADMIN1));
        Mockito.when(userRepository.findByRole(ROLE_ADMIN)).thenReturn(userList);
        mvc.perform(get(URI.create(URL_USER + BY_ROLE))
                        .param(ROLE_STR, "ROLE_INEXISTANT")
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }


    /**
     * Récupérer un utilisateur par son ID (Student) : isOk
     * Params: id (son id, lui meme)
     */
    @Test
    void getById_1() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(STUDENT1).getEmail())))
                .andDo(document("user/getById",
                        responseFields(userDescriptor)
                ));
    }

    /**
     * Récupérer un utilisateur par son ID (Student) : isForbidden
     * Params: id (id d'un autre student)
     */
    @Test
    void getById_2() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(STUDENT2).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    /**
     * Récupérer un utilisateur par son ID (Student) : isForbidden
     * Params: id (id d'un admin)
     */
    @Test
    void getById_3() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(ADMIN1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    /**
     * Récupérer un utilisateur par son ID (Student) : isForbidden
     * Params: id (id d'un professeur)
     */
    @Test
    void getById_4() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(TEACHER1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    /**
     * Récupérer un utilisateur par son ID (Student) : isOk
     * Params: id (son id, lui meme)
     */
    @Test
    void getById_5() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(TEACHER1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(TEACHER1).getEmail())));
    }

    /**
     * test getById 6 : forbidden (Teacher to other Teacher)
     */
    @Test
    void getById_6() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(TEACHER2).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    /**
     * test getById 7 : success (Teacher to Student)
     */
    @Test
    void getById_7() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(STUDENT1).getEmail())));
    }

    /**
     * test getById 8 : forbidden (Teacher to Admin)
     */
    @Test
    void getById_8() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(ADMIN1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    /**
     * test getById 9 : success (Admin to Self)
     */
    @Test
    void getById_9() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(ADMIN1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(ADMIN1).getEmail())));
    }

    /**
     * test getById 10 : success (Admin to other Admin)
     */
    @Test
    void getById_10() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(ADMIN2).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(ADMIN2).getEmail())));
    }

    /**
     * test getById 11 : success (Admin to Student)
     */
    @Test
    void getById_11() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(STUDENT1).getEmail())));
    }

    /**
     * test getById 12 : forbidden (Admin to Teacher)
     */
    @Test
    void getById_12() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(TEACHER1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(TEACHER1).getEmail())));
    }

    /**
     * test getById 13 : success (Unconected to Admin)
     */
    @Test
    void getById_13() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(ADMIN1).getId())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * test getById 14 : success (Unconected to Teacher)
     */
    @Test
    void getById_14() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(TEACHER1).getId())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * test getById 15 : success (Unconected to Student)
     */
    @Test
    void getById_15() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + userMockAndData.get(STUDENT1).getId())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * test getById 16 : notFound (Admin to unkows)
     */
    @Test
    void getById_16() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ "/" + "1"))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     *test getCurrent 1 : success (Admin)
     */
    @Test
    void getCurrent_1() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ CURRENT))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(ADMIN1).getEmail())));
    }

    /**
     *test getCurrent 2 : success (Teacher)
     */
    @Test
    void getCurrent_2() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ CURRENT))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(TEACHER1).getEmail())));
    }

    /**
     *test getCurrent 3 : success (Student)
     */
    @Test
    void getCurrent_3() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ CURRENT))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath(EMAIL_STR, equalTo(userMockAndData.get(STUDENT1).getEmail())))
                .andDo(document("user/getCurrent", responseFields(userDescriptor)));
    }

    /**
     *test getCurrent 4 : isUnauthorized (unconnected)
     */
    @Test
    void getCurrent_4() throws Exception {
        mvc.perform(get(URI.create(URL_USER+ CURRENT)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * test patch : isUnauthorized (unconnected)
     */
    @Test
    void patch_1() throws Exception {
        mvc.perform(patch(URI.create(URL_USER+ "/" + userMockAndData.get(ADMIN1).getId())))
                .andExpect(status().isUnauthorized());

    }

    /**
     * test patch : NotFound (Admin)
     */
    @Test
    void patch_2() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(EMAIL_STR, unknownMail());
        mvc.perform(patch(URI.create(URL_USER + "/" + "1"))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isNotFound());
    }

    /**
     * test patch : isForbidden (Student)
     */
    @Test
    void patch_3() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(EMAIL_STR, userMockAndData.get(STUDENT1).getEmail());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isForbidden());

    }

    /**
     * test patch : isForbidden (Teacher)
     */
    @Test
    void patch_4() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(EMAIL_STR, userMockAndData.get(STUDENT1).getEmail());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isForbidden());

    }

    /**
     * test patch : isOk (Admin) Student To Teacher
     * verifier si num etu pas la et role changer
     */
    @Test
    void patch_5() throws Exception {
        User userModdifier = new User(userMockAndData.get(STUDENT1));
        userModdifier.setRole(ROLE_TEACHER);
        userModdifier.setNumEtu(null);
        Mockito.when(userRepository.save(userModdifier)).thenReturn(userModdifier);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, userModdifier.getRole());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk());
    }

    /**
     * test patch : isBadRequest (Admin) Student To Teacher
     * Avec num Etu
     */
    @Test
    void patch_6() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, ROLE_TEACHER);
        updateMap.put(NUM_ETU_STR, NUMETU);
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isOk (Admin) Student To Admin
     * verifier si num etu pas la et role changer
     */
    @Test
    void patch_7() throws Exception {
        User userModdifier = new User(userMockAndData.get(STUDENT1));
        userModdifier.setRole(ROLE_ADMIN);
        userModdifier.setNumEtu(null);
        Mockito.when(userRepository.save(userModdifier)).thenReturn(userModdifier);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, userModdifier.getRole());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk());
    }

    /**
     * test patch : isBadRequest (Admin) Student To Admin
     * avec num Etu
     */
    @Test
    void patch_8() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, ROLE_ADMIN);
        updateMap.put(NUM_ETU_STR, NUMETU);
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) Admin To Student
     * Num Etu = null
     */
    @Test
    void patch_9() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, ROLE_STUDENT);
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(ADMIN2).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) Admin To Student
     * Num Etu = ""
     */
    @Test
    void patch_10() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, ROLE_STUDENT);
        updateMap.put(NUM_ETU_STR, "");
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(ADMIN2).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) Teacher To Student
     * NumEtu = null
     */
    @Test
    void patch_11() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, STUDENT1);
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(TEACHER1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) Teacher To Student
     * NumEtu = ""
     */
    @Test
    void patch_12() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, STUDENT1);
        updateMap.put(NUM_ETU_STR, "");
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(TEACHER1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isOk (Admin) Admin To Student
     * Verifier le num etu et role
     */
    @Test
    void patch_13() throws Exception {
        User userModdifier = new User(userMockAndData.get(ADMIN2));
        userModdifier.setRole(ROLE_STUDENT);
        userModdifier.setNumEtu(NUMETU);
        Mockito.when(userRepository.save(userModdifier)).thenReturn(userModdifier);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, userModdifier.getRole());
        updateMap.put(NUM_ETU_STR, userModdifier.getNumEtu());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(ADMIN2).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk());
    }

    /**
     * test patch : isOk (Admin) Teacher To Student
     * Verifier le num etu et role
     */
    @Test
    void patch_14() throws Exception {
        User userModdifier = new User(userMockAndData.get(TEACHER1));
        userModdifier.setRole(ROLE_STUDENT);
        userModdifier.setNumEtu(NUMETU);
        Mockito.when(userRepository.save(userModdifier)).thenReturn(userModdifier);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(ROLE_STR, userModdifier.getRole());
        updateMap.put(NUM_ETU_STR, userModdifier.getNumEtu());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(TEACHER1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk());
    }

    /**
     * test patch : isOk (Admin) User Change Email, firstName, lastName, numEtu
     * verifier email, firstName, lastName
     */
    @Test
    void patch_15() throws Exception {
        User userModdifier = new User(userMockAndData.get(STUDENT1));
        userModdifier.setEmail(unknownMail());
        userModdifier.setFirstName(FIRSTNAME);
        userModdifier.setLastName(LASTNAME);
        userModdifier.setNumEtu(NUMETU);
        Mockito.when(userRepository.save(userModdifier)).thenReturn(userModdifier);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(EMAIL_STR, userModdifier.getEmail());
        updateMap.put(FIRS_NAME_STR, userModdifier.getFirstName());
        updateMap.put(LAST_NAME_STR, userModdifier.getLastName());
        updateMap.put(NUM_ETU_STR, userModdifier.getNumEtu());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk());
    }

    /**
     * test patch : isBadRequest (Admin) User Change Email mal former
     * verifier si email pas changer
     */

    @Test
    void patch_16() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(EMAIL_STR, wrongMail());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) User Change Email vide
     * verifier si email pas changer
     */
    @Test
    void patch_17() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(EMAIL_STR, "");
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) Ajouter à admin numEtu
     * verifier si email pas changer
     */
    @Test
    void patch_18() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(NUM_ETU_STR, NUMETU);
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(ADMIN2).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Teacher) Ajouter à teacher numEtu
     * verifier si email pas changer
     */
    @Test
    void patch_19() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(NUM_ETU_STR, NUMETU);
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(ADMIN2).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) User FirstName empty
     * verifier si firstName pas changer
     */
    @Test
    void patch_20() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(FIRS_NAME_STR, "");
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) User numEtu empty
     * verifier si numEtu pas changer
     */
    @Test
    void patch_21() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(NUM_ETU_STR, "");
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isBadRequest (Admin) User LastName empty
     * verifier si lastNameChanger
     */
    @Test
    void patch_22() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(LAST_NAME_STR, "");
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test patch : isOk (Admin) Teacher Change Email, firstName, lastName, numEtu, role
     * verifier email, firstName, lastName
     */
    @Test
    void patch_23() throws Exception {
        User userModdifier = new User(userMockAndData.get(TEACHER1));
        userModdifier.setEmail(unknownMail());
        userModdifier.setFirstName(FIRSTNAME);
        userModdifier.setLastName(LASTNAME);
        userModdifier.setRole(ROLE_STUDENT);
        userModdifier.setNumEtu(NUMETU);
        Mockito.when(userRepository.save(userModdifier)).thenReturn(userModdifier);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(EMAIL_STR, userModdifier.getEmail());
        updateMap.put(FIRS_NAME_STR, userModdifier.getFirstName());
        updateMap.put(LAST_NAME_STR, userModdifier.getLastName());
        updateMap.put(NUM_ETU_STR, userModdifier.getNumEtu());
        updateMap.put(ROLE_STR, userModdifier.getRole());
        mvc.perform(patch(URI.create(URL_USER + "/" + userMockAndData.get(TEACHER1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk())
                .andDo(document("user/patch",
                        responseFields(userDescriptor),
                        requestFields(
                                fieldWithPath(EMAIL_STR).description("Nouvel Email de l'utilisateur"),
                                fieldWithPath(FIRS_NAME_STR).description("Nouveau prénom de l'utilisateur"),
                                fieldWithPath(LAST_NAME_STR).description("Nouveau nom de l'utilisateur"),
                                fieldWithPath(NUM_ETU_STR).description("Nouveau numéro étudiant de l'utilisateur"),
                                fieldWithPath(ROLE_STR).description("Nouveau role de l'utilisateur")
                        )));
    }

    /**
     * test delete : isOK (Admin)
     */
    @Test
    void delete_1() throws Exception {
        User user = userMockAndData.get(STUDENT1);
        Mockito.doNothing().when(userRepository).delete(user);
        mvc.perform(delete(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andDo(document("user/delete",
                        responseFields(
                                fieldWithPath("message").description("message de confirmation de la suppression de l'utilisateur")
                        )));
    }

    /**
     * test delete : isUnauthorized (unconnected)
     */
    @Test
    void delete_2() throws Exception {
        mvc.perform(delete(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * test delete : NotFound (Admin)
     */
    @Test
    void delete_3() throws Exception {
        mvc.perform(delete(URI.create(URL_USER + "/" + "1"))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * test delete : isForbidden (Student)
     */
    @Test
    void delete_4() throws Exception {
        mvc.perform(delete(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * test delete : isForbidden (Teacher)
     */
    @Test
    void delete_5() throws Exception {
        mvc.perform(delete(URI.create(URL_USER + "/" + userMockAndData.get(STUDENT1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * test UpdatePassword : isUnauthorized (unconnected)
     */
    @Test
    void updatePassword_1() throws Exception {
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * test UpdatePassword : isOk (Student)
     */
    @Test
    void updatePassword_2() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, PASSWORD);
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk())
                .andDo(document("user/updatePassword",
                        requestFields(fieldWithPath(PASSWORD_STR).description("Nouveau mot de passe")),
                        responseFields(fieldWithPath("message").description("message de confirmation de modification"))));
    }

    /**
     * test UpdatePassword : isOk (Teacher)
     */
    @Test
    void updatePassword_3() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, PASSWORD);
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk());
    }

    /**
     * test UpdatePassword : isOk (Admin)
     */
    @Test
    void updatePassword_4() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, PASSWORD);
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN2))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isOk());
    }

    /**
     * test UpdatePassword : isBadRequest (Student) longeur password < 8
     */
    @Test
    void updatePassword_5() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, wrongPassword());
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test UpdatePassword : isBadRequest (Teacher) longeur password < 8
     */
    @Test
    void updatePassword_6() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, wrongPassword());
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test UpdatePassword : isBadRequest (Admin) longeur password < 8
     */

    @Test
    void updatePassword_7() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, wrongPassword());
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN2))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }
    /**
     * test UpdatePassword : isBadRequest (Student) longeur password == null
     */

    @Test
    void updatePassword_8() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, null);
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test UpdatePassword : isBadRequest (Teacher) password == null
     */
    @Test
    void updatePassword_9() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, null);
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test UpdatePassword : isBadRequest (Admin) password == null
     */
    @Test
    void updatePassword_10() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, null);
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN2))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }
    /**
     * test UpdatePassword : isBadRequest (Student) password == ""
     */
    @Test
    void updatePassword_11() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, "");
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test UpdatePassword : isBadRequest (Teacher) password == ""
     */
    @Test
    void updatePassword_12() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, "");
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test UpdatePassword : isBadRequest (Admin) password == ""
     */
    @Test
    void updatePassword_13() throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(PASSWORD_STR, "");
        mvc.perform(patch(URI.create(URL_USER + UPDATE_PASSWORD))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN2))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest());
    }

}