package com.qrcodeemargement.app.controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrcodeemargement.app.models.fabrick.FabricUser;
import com.qrcodeemargement.app.models.Role;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.*;


import static com.qrcodeemargement.app.utils.UserData.*;
import static com.qrcodeemargement.app.controllers.AuthController.*;
import static com.qrcodeemargement.app.models.Role.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class TestAuthController {

    public static final String EMAIL_STR = "email";
    public static final String PASSWORD_STR = "password";
    public static final String FIRST_NAME_STR = "firstName";
    public static final String LAST_NAME_STR = "lastName";
    public static final String ROLE_STR = "role";
    public static final String NUM_ETU_STR = "numEtu";
    @Autowired
    MockMvc mvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    FabricUser fabricUser;

    @Autowired
    PasswordEncoder encoder;

    User user;
    Map<String, Object> userMapSignUp;
    Map<String, Object> userMapSignIn;

    String adminToken;

    @BeforeEach
    void init() throws Exception {
        user = new User(MAIL,encoder.encode(PASSWORD), FIRSTNAME, LASTNAME, ROLE_ADMIN);
        user.setId(UUID.randomUUID().toString());

        userMapSignUp = new HashMap<>();
        userMapSignUp.put(EMAIL_STR, user.getEmail());
        userMapSignUp.put(PASSWORD_STR, PASSWORD);
        userMapSignUp.put(FIRST_NAME_STR, user.getFirstName());
        userMapSignUp.put(LAST_NAME_STR, user.getLastName());
        userMapSignUp.put(ROLE_STR, user.getRole());

        userMapSignIn = new HashMap<>();
        userMapSignIn.put(EMAIL_STR, user.getEmail());
        userMapSignIn.put(PASSWORD_STR, PASSWORD);

        initUser();
    }

    private void initUser() throws Exception {
        User user = new User(mailAdmin1(), encoder.encode(PASSWORD), firstNameAdmin(), lastNameAdmin(), ROLE_ADMIN);
        user.setId(UUID.randomUUID().toString());

        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Map<String, Object> usermap = new HashMap<>();
        usermap.put(EMAIL_STR, user.getEmail());
        usermap.put(PASSWORD_STR, PASSWORD);

        MvcResult mvcResult = mvc.perform(post(URI.create(URL_AUTH + SIGNIN))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(usermap)))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        HashMap<String, Object> contentMap = new ObjectMapper().readValue(content, new TypeReference<>() {
        });
        adminToken = contentMap.get("token").toString();
    }

    private void changeUser(String champ, String value) {
        userMapSignUp.put(champ, value);

        switch (champ) {
            case EMAIL_STR -> user.setEmail(value);
            case FIRST_NAME_STR -> user.setFirstName(value);
            case LAST_NAME_STR -> user.setLastName(value);
            case NUM_ETU_STR -> user.setNumEtu(value);
        }
    }
    private void changeUser(Role value) {
        userMapSignUp.put(TestAuthController.ROLE_STR, value);
        user.setRole(value);
    }

    private void mockMethodeForUser() {
        User userWithOutId = new User(user);
        userWithOutId.setId(null);
        Mockito.when(fabricUser.build(
                eq(user.getEmail()),
                any(String.class), //obliger car encoder.encode(password) not equals to encoder.encode(password)
                eq(user.getFirstName()),
                eq(user.getLastName()),
                eq(user.getNumEtu()),
                eq(user.getRole())
        )).thenReturn(userWithOutId);

        Mockito.when(userRepository.save(userWithOutId)).thenReturn(user);
    }

    /**
     * test signup student 1 : (Admin) student succesfully created
    */
    @Test
    void testSignupStudent() throws Exception {
        changeUser(NUM_ETU_STR, NUMETU);
        changeUser(ROLE_STUDENT);

        mockMethodeForUser();

        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/user/"+user.getId()))
                .andDo(document("auth/signUp/student-successful-Auth", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }

    /**
     * test signup admin 1 : (Admin) Admin succesfully created (without numEtu)
   */
    @Test
    void testSignupAdmin() throws Exception {

        mockMethodeForUser();

        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/user/"+user.getId()))
                .andDo(document("auth/signUp/admin-successful-Auth",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));

    }

    /**
     * test signup teacher 1 : Teacher succesfully created (without numEtu)
     */
    @Test
    void testSignupTeacher() throws Exception {

        changeUser(ROLE_TEACHER);

        mockMethodeForUser();

        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/user/"+user.getId()))
                .andDo(document("auth/signUp/teacher-successful-Auth",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));

    }

    /**
     * test signup student 2 : mail already used
     */
    @Test
    void testSignup1() throws Exception {

        mockMethodeForUser();

        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is(409))
                .andExpect(content()
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("message",is(ERROR_EMAIL_IS_ALREADY_IN_USE)))
                .andDo(document("auth/signUp/signup-mail-already-used", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }

    /**
     * test signup student : without numEtu
     */
    @Test
    void testSignupStudentWithOurNumEtu() throws Exception {
        changeUser(ROLE_STUDENT);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("message",is(L_UTILISATEUR_AVEC_LE_ROLE_ETUDIENT_N_A_PAS_DE_NUMERO_ETUDIENT)))
                .andDo(document("auth/signUp/student-without-numEtu", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }

    /**
     * test signup no student : with numEtu
     */
    @Test
    void testSignupnoStudentWithNumEtu() throws Exception {

        changeUser(ROLE_TEACHER);
        changeUser(NUM_ETU_STR, NUMETU);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("message",is(UN_UTILISATEUR_SANS_LE_ROLE_ROLE_STUDENT_NE_PEUT_PAS_AVOIR_UN_NUMERO_ETUDIENT)))
                .andDo(document("auth/signUp/noStudent-with-numEtu", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }

    /**
     * test signup : missing parameters (roles)
     */
    @Test
    void testSignupMissingParameters_1() throws Exception {
        changeUser(null);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("message",is(ERROR_MISSING_ROLES)))
                .andDo(document("auth/signUp/signup-missing-paramaters-roles", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }

    /**
     * test signup : missing parameters (email)
     */
    @Test
    void testSignupMissingParameters_2() throws Exception {
        changeUser(EMAIL_STR, null);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andDo(document("auth/signUp/signup-missing-paramaters-email", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }

    /**
     * test signup : missing parameters (password)
     */
    @Test
    void testSignupMissingParameters_3() throws Exception {
        changeUser(PASSWORD_STR, null);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andDo(document("auth/signUp/signup-missing-paramaters-password", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }

    /**
     * test signup : missing parameters (firstName)
     */
    @Test
    void testSignupMissingParameters_4() throws Exception {
        changeUser(FIRST_NAME_STR, null);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andDo(document("auth/signUp/signup-missing-paramaters-firstName", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }

    /**
     * test signup : missing parameters (lastName)
     */
    @Test
    void testSignupMissingParameters_5() throws Exception {
        changeUser(LAST_NAME_STR, null);

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andDo(document("auth/signUp/signup-missing-paramaters-lastName", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }

    /**
     * test signup : BadRole
     */
    @Test
    void testSignupBadRole() throws Exception {
        userMapSignUp.put(ROLE_STR, "ROLE_UNKNOWN");

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andDo(document("signup-BadRole", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }

    /**
     * test signup  : (unConnected) isUnauthorized
     */
    @Test
    void testSignupUnconnected() throws Exception {
        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp)))
                .andExpect(status().isUnauthorized());

    }

    /**
     * test signup  : (Teacher) isForbidden
     */
    @Test
    void testSignupWithTeacher() throws Exception {
        User user = new User(mailTeacher1(), encoder.encode(PASSWORD), firstNameTeacher(), lastNameTeacher(), ROLE_TEACHER);
        user.setId(UUID.randomUUID().toString());

        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Map<String, Object> usermap = new HashMap<>();
        usermap.put(EMAIL_STR, user.getEmail());
        usermap.put(PASSWORD_STR, PASSWORD);

        MvcResult mvcResult = mvc.perform(post(URI.create(URL_AUTH + SIGNIN))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(usermap)))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        HashMap<String, Object> contentMap = new ObjectMapper().readValue(content, new TypeReference<>() {
        });
        String token = contentMap.get("token").toString();

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

    }

    /**
     * test signup  : (Student) isForbidden
     */
    @Test
    void testSignupWithStudent() throws Exception {
        User user = new User(mailStudent1(), encoder.encode(PASSWORD), firstNameStudent(), lastNameStudent(), ROLE_STUDENT);
        user.setId(UUID.randomUUID().toString());

        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Map<String, Object> usermap = new HashMap<>();
        usermap.put(EMAIL_STR, user.getEmail());
        usermap.put(PASSWORD_STR, PASSWORD);

        MvcResult mvcResult = mvc.perform(post(URI.create(URL_AUTH + SIGNIN))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(usermap)))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        HashMap<String, Object> contentMap = new ObjectMapper().readValue(content, new TypeReference<>() {
        });
        String token = contentMap.get("token").toString();

        mvc.perform(post(URI.create(URL_AUTH + SIGNUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignUp))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

    }




    /**
     * test signin 1 : user successfully logged
     */
    @Test
    void testSignin_1() throws Exception {

        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mvc.perform(post(URI.create(URL_AUTH + SIGNIN))
                    .contentType(MediaTypes.HAL_JSON)
                    .content(new ObjectMapper().writeValueAsString(userMapSignIn)))
            .andExpect(status().isOk())
            .andDo(document("auth/signIn/successful-login", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));


    }

    /**
     * test signin 2 : mail not found
    */
    @Test
    void testSignin_2() throws Exception {

        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        mvc.perform(post(URI.create(URL_AUTH + SIGNIN))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignIn)))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/signIn/mail-not-found",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())))
                .andReturn();
    }

    /**
     * test signin 3 : wrong password
    */
    @Test
    void testSignin_3() throws Exception {
        userMapSignIn.put(PASSWORD_STR, PASSWORD+"1");

        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mvc.perform(post(URI.create(URL_AUTH + SIGNIN))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignIn)))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/signIn/wrong-password",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

    /**
     * test signin 4 : missing parameters (email)
    */
    @Test
    void testSignin_4() throws Exception {
        userMapSignIn.remove(PASSWORD_STR);

        mvc.perform(post(URI.create(URL_AUTH + SIGNIN))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignIn)))
                .andExpect(status().isBadRequest())
                .andDo(document("auth/signIn/missing-parameters-email",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

    /**
     * test signin 5 : missing parameters (password)
     */
    @Test
    void testSignin_5() throws Exception {
        userMapSignIn.remove(EMAIL_STR);

        mvc.perform(post(URI.create(URL_AUTH + SIGNIN))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMapSignIn)))
                .andExpect(status().isBadRequest())
                .andDo(document("auth/signIn/missing-parameters-email",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

}
