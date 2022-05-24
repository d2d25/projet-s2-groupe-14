package com.qrcodeemargement.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrcodeemargement.app.exception.UserIsNotStudentException;
import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.SubGroup;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.models.fabrick.FabricGroup;
import com.qrcodeemargement.app.models.fabrick.FabricSubGroup;
import com.qrcodeemargement.app.repository.GroupRepository;
import com.qrcodeemargement.app.repository.UserRepository;
import com.qrcodeemargement.app.utils.GroupMockAndData;
import com.qrcodeemargement.app.utils.GroupsKey;
import com.qrcodeemargement.app.utils.UserMockAndData;
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


import static com.qrcodeemargement.app.utils.UserData.*;
import static com.qrcodeemargement.app.controllers.GroupController.*;
import static com.qrcodeemargement.app.utils.GroupsKey.*;
import static com.qrcodeemargement.app.utils.UsersKey.*;
import static com.qrcodeemargement.app.models.Role.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class TestGroupController {
    public static final String STUDENTS_STR = "students";
    public static final String NAME_STR = "name";


    @Autowired
    MockMvc mvc;

    @MockBean
    UserRepository userRepository;
    @MockBean
    GroupRepository groupRepository;

    @MockBean
    FabricGroup fabricGroup;

    @MockBean
    FabricSubGroup fabricSubGroup;
    @Autowired
    PasswordEncoder encoder;

    UserMockAndData userMockAndData;

    GroupMockAndData groupMockAndData;

    FieldDescriptor[] groupDescriptor;


    @BeforeEach
    private void init() {
        userMockAndData = new UserMockAndData(userRepository,encoder, mvc);
        groupMockAndData = new GroupMockAndData(groupRepository, userMockAndData);
        groupDescriptor = new FieldDescriptor[] {
                fieldWithPath("id").description("ID du groupe"),
                fieldWithPath("name").description("Nom du groupe"),
                fieldWithPath("students").description("étudiants du groupe"),
                fieldWithPath("students[].id").description("id des étudiants du groupe"),
                fieldWithPath("students[].email").description("id des étudiants du groupe"),
                fieldWithPath("students[].firstName").description("ID des étudiants du groupe"),
                fieldWithPath("students[].lastName").description("ID des étudiants du groupe"),
                fieldWithPath("students[].numEtu").description("ID des étudiants du groupe").optional(),
                fieldWithPath("students[].role").description("ID des étudiants du groupe"),
                fieldWithPath("subGroups").description("Sous groupes du groupe").optional(),
                fieldWithPath("subGroups[].id").description("ID des sous groupes").optional(),
                fieldWithPath("subGroups[].name").description("Noms des sous groupes").optional(),
                fieldWithPath("subGroups[].idGroup").description("ID du groupe des sous groupes").optional(),
                fieldWithPath("subGroups[].students").description("Etudiants des sous groupes").optional(),
                fieldWithPath("subGroups[].students[].id").description("ID de l'étudiant du sous groupe").optional(),
                fieldWithPath("subGroups[].students[].email").description("Email de l'étudiant du sous groupe").optional(),
                fieldWithPath("subGroups[].students[].firstName").description("Nom de l'étudiant du sous groupe").optional(),
                fieldWithPath("subGroups[].students[].lastName").description("Prénom de l'étudiant du sous groupe").optional(),
                fieldWithPath("subGroups[].students[].numEtu").description("Numéro de l'étudiant du sous groupe").optional(),
                fieldWithPath("subGroups[].students[].role").description("Role de l'étudiant du sous groupe").optional()

        };
    }

    /**
     * Création d'un groupe (Admin) isCreated
     */
    @Test
    void postGroup_1() throws Exception {
        String name = "mon groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        Group groupWithOutId = new Group(name, students);
        Group groupWithId = new Group(groupWithOutId);
        String id = UUID.randomUUID().toString();
        groupWithId.setId(id);

        Mockito.when(groupRepository.existsByName(name)).thenReturn(false);
        Mockito.when(fabricGroup.buid(name, students)).thenReturn(groupWithOutId);
        Mockito.when(groupRepository.save(groupWithOutId)).thenReturn(groupWithId);


        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/group/"+id));
                //.andDo(document("group/post",
                //        responseFields(groupDescriptor),
                //        requestFields(
                //                fieldWithPath(NAME_STR).description("Nom du groupe"),
                //                fieldWithPath(STUDENTS_STR).description("Liste des étudiants")
                //        )));

    }

    /**
     * Création d'un groupe (Teacher) isForbidden
     */
    @Test
    void postGroup_2() throws Exception {
        String name = "mon groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Création d'un groupe (Student) isForbidden
     */
    @Test
    void postGroup_3() throws Exception {
        String name = "mon groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Création d'un groupe (Unconnected) isUnauthorized
     */
    @Test
    void postGroup_4() throws Exception {
        String name = "mon groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Création d'un groupe (Admin) isBadRequest name = ""
     */
    @Test
    void postGroup_5() throws Exception {
        String name = "";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un groupe (Admin) isBadRequest name = null
     */
    @Test
    void postGroup_6() throws Exception {
        String name = "";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un groupe (Admin) isBadRequest students = null
     */
    @Test
    void postGroup_7() throws Exception {
        String name = "mon groupe";

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, null);
        groupMap.put(NAME_STR, name);

        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un groupe (Admin) isBadRequest students = ""
     */
    @Test
    void postGroup_8() throws Exception {
        String name = "mon groupe";

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, new HashSet<>());
        groupMap.put(NAME_STR, name);

        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un groupe (Admin) isConflict nom groupe déjà utilisé
     */
    @Test
    void postGroup_9() throws Exception {
        String name = "mon groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        Group groupWithOutId = new Group(name, students);
        Group groupWithId = new Group(groupWithOutId);
        String id = UUID.randomUUID().toString();
        groupWithId.setId(id);

        Mockito.when(groupRepository.existsByName(name)).thenReturn(true);

        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isConflict());
    }

    /**
     * Création d'un groupe (Admin) isNotFound User innexistant
     */
    @Test
    void postGroup_10() throws Exception {
        String name = "mon groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        User userNotInBd = new User(MAIL,PASSWORD, FIRSTNAME, LASTNAME, NUMETU, ROLE_STUDENT);
        userNotInBd.setId(UUID.randomUUID().toString());
        students.add(userNotInBd);

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        Group groupWithOutId = new Group(name, students);
        Group groupWithId = new Group(groupWithOutId);
        String id = UUID.randomUUID().toString();
        groupWithId.setId(id);

        Mockito.when(groupRepository.existsByName(name)).thenReturn(false);
        Mockito.when(fabricGroup.buid(name, students)).thenReturn(groupWithOutId);
        Mockito.when(groupRepository.save(groupWithOutId)).thenReturn(groupWithId);


        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Création d'un groupe (Admin) isBadRequest User n'est pas un étudiant
     */
    @Test
    void postGroup_11() throws Exception {
        String name = "mon groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(ADMIN1));


        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);
        groupMap.put(NAME_STR, name);

        Mockito.when(groupRepository.existsByName(name)).thenReturn(false);
        Mockito.when(fabricGroup.buid(name, students)).thenThrow(new UserIsNotStudentException(userMockAndData.get(ADMIN1).getId()));


        mvc.perform(post(URI.create(URL_GROUP))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un sous groupe (Admin) isCreated
     */
    @Test
    void postSubGroup_1() throws Exception {
        String name = "mon Sous groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        Group group = new Group("mon groupe", students);
        String idGroup = UUID.randomUUID().toString();
        group.setId(idGroup);

        Mockito.when(groupRepository.findById(idGroup)).thenReturn(Optional.of(group));

        String idSubGroup = UUID.randomUUID().toString();
        SubGroup subGroup = new SubGroup(idSubGroup, name, students);

        Mockito.when(fabricSubGroup.buid(name, students)).thenReturn(subGroup);

        Group groupWithSubGroup = new Group(group);
        groupWithSubGroup.addSubGroup(subGroup);

        Mockito.when(groupRepository.save(groupWithSubGroup)).thenReturn(groupWithSubGroup);


        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/group/"+idGroup + "/" + idSubGroup));
    }

    /**
     * Création d'un sous groupe (Teacher) isForbidden
     */
    @Test
    void postSubGroup_2() throws Exception {
        String name = "mon Sous groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        Group group = new Group("mon groupe", students);
        String idGroup = UUID.randomUUID().toString();
        group.setId(idGroup);

        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Création d'un sous groupe (Student) isForbidden
     */
    @Test
    void postSubGroup_3() throws Exception {
        String name = "mon Sous groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        Group group = new Group("mon groupe", students);
        String idGroup = UUID.randomUUID().toString();
        group.setId(idGroup);

        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Création d'un sous groupe (unConnected) isUnauthorized
     */
    @Test
    void postSubGroup_4() throws Exception {
        String name = "mon Sous groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        Group group = new Group("mon groupe", students);
        String idGroup = UUID.randomUUID().toString();
        group.setId(idGroup);

        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap)))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Création d'un sous groupe (Admin) isNotFound  groupe innexistant
     */
    @Test
    void postSubGroup_5() throws Exception {
        String name = "mon Sous groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        String idGroup = UUID.randomUUID().toString();


        Mockito.when(groupRepository.findById(idGroup)).thenReturn(Optional.empty());


        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Création d'un sous groupe (Admin) isNotFound student innexistant
     */
    @Test
    void postSubGroup_6() throws Exception {
        String name = "mon Sous groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        User userNotFound = userMockAndData.get(STUDENT2);
        userNotFound.setId(UUID.randomUUID().toString());
        students.add(userNotFound);

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        Group group = new Group("mon groupe", students);
        String idGroup = UUID.randomUUID().toString();
        group.setId(idGroup);

        Mockito.when(groupRepository.findById(idGroup)).thenReturn(Optional.of(group));

        String idSubGroup = UUID.randomUUID().toString();
        SubGroup subGroup = new SubGroup(idSubGroup, name, students);

        Mockito.when(fabricSubGroup.buid(name, students)).thenReturn(subGroup);

        Group groupWithSubGroup = new Group(group);
        groupWithSubGroup.addSubGroup(subGroup);

        Mockito.when(groupRepository.save(groupWithSubGroup)).thenReturn(groupWithSubGroup);


        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Création d'un sous groupe (Admin) isConflict nom sous groupe déjà utilisé
     */
    @Test
    void postSubGroup_7() throws Exception {
        String name = "mon Sous groupe";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);


        Group group = new Group("mon groupe", students);
        String idGroup = UUID.randomUUID().toString();
        group.setId(idGroup);
        group.addSubGroup(new SubGroup(UUID.randomUUID().toString(), name,students));

        Mockito.when(groupRepository.findById(idGroup)).thenReturn(Optional.of(group));

        String idSubGroup = UUID.randomUUID().toString();
        SubGroup subGroup = new SubGroup(idSubGroup, name, students);

        Mockito.when(fabricSubGroup.buid(name, students)).thenReturn(subGroup);

        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isConflict());
    }

    /**
     * Création d'un sous groupe (Admin) isBadRequest student ajouté dans un sous groupe Alors qu'il n'est pas dans le groupe
     */
    @Test
    void postSubGroup_8() throws Exception {
        String name = "mon Sous groupe";
        Set<User> studentsGroupe = new HashSet<>();
        studentsGroupe.add(userMockAndData.get(STUDENT1));
        Set<User> studentsSubGroupe = new HashSet<>();
        studentsSubGroupe.add(userMockAndData.get(STUDENT1));
        studentsSubGroupe.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        studentsSubGroupe.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        Group group = new Group("mon groupe", studentsGroupe);
        String idGroup = UUID.randomUUID().toString();
        group.setId(idGroup);

        Mockito.when(groupRepository.findById(idGroup)).thenReturn(Optional.of(group));

        String idSubGroup = UUID.randomUUID().toString();
        SubGroup subGroup = new SubGroup(idSubGroup, name, studentsSubGroupe);

        Mockito.when(fabricSubGroup.buid(name, studentsSubGroupe)).thenReturn(subGroup);

        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un sous groupe (Admin) isBadRequest name = null
     */
    @Test
    void postSubGroup_9() throws Exception {
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, null);

        String idGroup = UUID.randomUUID().toString();



        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un sous groupe (Admin) isBadRequest name = ""
     */
    @Test
    void postSubGroup_10() throws Exception {
        String name = "";
        Set<User> students = new HashSet<>();
        students.add(userMockAndData.get(STUDENT1));
        students.add(userMockAndData.get(STUDENT2));

        Set<String> studentsStr = new HashSet<>();
        students.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        String idGroup = UUID.randomUUID().toString();



        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un sous groupe (Admin) isBadRequest students = null
     */
    @Test
    void postSubGroup_11() throws Exception {
        String name = "Mon sous Groupe";
        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, null);
        subGroupMap.put(NAME_STR, name);

        String idGroup = UUID.randomUUID().toString();



        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Création d'un sous groupe (Admin) isBadRequest students empty
     */
    @Test
    void postSubGroup_12() throws Exception {
        String name = "Mon sous Groupe";

        Set<String> studentsStr = new HashSet<>();

        Map<String, Object> subGroupMap = new HashMap<>();
        subGroupMap.put(STUDENTS_STR, studentsStr);
        subGroupMap.put(NAME_STR, name);

        String idGroup = UUID.randomUUID().toString();



        mvc.perform(post(URI.create(URL_GROUP +"/" + idGroup))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(subGroupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Modification d'un groupe (Admin) isOk update name
     */
    @Test
    void patchGroup_1() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        String name = "Mon Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        Group group1 = new Group(group);
        group1.setName(name);
        Mockito.when(groupRepository.save(group1)).thenReturn(group1);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un groupe (Admin) isOk update students
     * Vérifier que les utilisateur supprimer ne sont plus dans les sous groupe
     */
    @Test
    void patchGroup_2() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        Group group1 = new Group(group);
        Set<User> studentsAdd = userMockAndData.getManyUser(5,ROLE_STUDENT);
        Set<User> studentsKeep = new HashSet<>();
        Set<User> studentsDelete = new HashSet<>();

        boolean listKeep = false;
        for (User user : group.getStudents()) {
            if (listKeep)
                studentsKeep.add(user);
            else
                studentsDelete.add(user);
            listKeep = !listKeep;
        }
        Set<User> newStudentsSet = new HashSet<>();
        newStudentsSet.addAll(studentsAdd);
        newStudentsSet.addAll(studentsKeep);

        group1.setStudents(newStudentsSet);

        group1.getSubGroups().forEach((s, subGroup) -> {
            Set<User> studentsSubGroupCopy = new HashSet<>(subGroup.getStudents());
            studentsSubGroupCopy.forEach(user -> {
                if (studentsDelete.contains(user))
                    subGroup.getStudents().remove(user);
            });
        });

        Set<String> studentsStr = new HashSet<>();
        newStudentsSet.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);

        Mockito.when(groupRepository.save(group1)).thenReturn(group1);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un groupe (Teacher) isForbidden
     */
    @Test
    void patchGroup_3() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        String name = "Mon Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Modification d'un groupe (Student) isForbidden
     */
    @Test
    void patchGroup_4() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        String name = "Mon Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Modification d'un groupe (unConnected) isUnauthorized
     */
    @Test
    void patchGroup_5() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        String name = "Mon Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                .content(new ObjectMapper().writeValueAsString(groupMap)))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Modification d'un groupe (Admin) isConflict nom déjà utilisé
     */
    @Test
    void patchGroup_6() throws Exception {
        Group group1 = groupMockAndData.get(GROUP1);
        Group group2 = groupMockAndData.get(GROUP2);

        String name = group2.getName();
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);


        mvc.perform(patch(URI.create(URL_GROUP + "/" + group1.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isConflict());
    }

    /**
     * Modification d'un groupe (Admin) isNotFound user innexistant
     */
    @Test
    void patchGroup_7() throws Exception {
        Group group = groupMockAndData.get(GROUP1);


        Set<String> studentsStr = new HashSet<>();
        group.getStudents().forEach(s -> studentsStr.add(s.getId()));
        studentsStr.add(UUID.randomUUID().toString());

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Modification d'un groupe (Admin) isBadRequest user n'est pas un étudiant
     */
    @Test
    void patchGroup_8() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        Set<String> studentsStr = new HashSet<>();
        group.getStudents().forEach(s -> studentsStr.add(s.getId()));
        studentsStr.add(userMockAndData.get(TEACHER1).getId());

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Modification d'un groupe (Admin) isNotFound groupe innexistant
     */
    @Test
    void patchGroup_9() throws Exception {

        String name = "Mon group";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + UUID.randomUUID()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Modification d'un groupe (Admin) isOk no update name = null
     */
    @Test
    void patchGroup_10() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, null);

        Mockito.when(groupRepository.save(group)).thenReturn(group);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un groupe (Admin) isOk no update name = ""
     */
    @Test
    void patchGroup_11() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, "");

        Mockito.when(groupRepository.save(group)).thenReturn(group);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un groupe (Admin) isOk no update students empty
     */
    @Test
    void patchGroup_12() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        Set<String> students = new HashSet<>();
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, students);

        Mockito.when(groupRepository.save(group)).thenReturn(group);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un groupe (Admin) isOk no update students null
     */
    @Test
    void patchGroup_13() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, null);

        Mockito.when(groupRepository.save(group)).thenReturn(group);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un sous groupe (Admin) isCreated
     */
    @Test
    void patchSubGroup_1() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        String name = "Mon Sous Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        Group group1 = new Group(group);
        subGroup.setName(name);
        Mockito.when(groupRepository.save(group1)).thenReturn(group1);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un sous groupe
     */
    @Test
    void patchSubGroup_2() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        Group group1 = new Group(group);
        SubGroup subGroup = (SubGroup) group1.getSubGroups().values().toArray()[0];
        Set<User> studentsNew = groupMockAndData.generateSubSet(group.getStudents(),3);
        subGroup.setStudents(studentsNew);

        Set<String> studentsStr = new HashSet<>();
        studentsNew.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);

        Mockito.when(groupRepository.save(group1)).thenReturn(group1);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un sous groupe (Teacher) isForbidden
     */
    @Test
    void patchSubGroup_3() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        String name = "Mon Sous Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        Group group1 = new Group(group);
        subGroup.setName(name);
        Mockito.when(groupRepository.save(group1)).thenReturn(group1);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Modification d'un sous groupe  (Student) isForbidden
     */
    @Test
    void patchSubGroup_4() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        String name = "Mon Sous Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        Group group1 = new Group(group);
        subGroup.setName(name);
        Mockito.when(groupRepository.save(group1)).thenReturn(group1);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Modification d'un sous groupe (unConnected) isUnauthorized
     */
    @Test
    void patchSubGroup_5() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        String name = "Mon Sous Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        Group group1 = new Group(group);
        subGroup.setName(name);
        Mockito.when(groupRepository.save(group1)).thenReturn(group1);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Modification d'un sous groupe (Admin) isOk no Update name = null
     */
    @Test
    void patchSubGroup_6() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];


        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, "");

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un sous groupe (Admin) isOk no Update  name = ""
     */
    @Test
    void patchSubGroup_7() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        String name = "";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un sous groupe (Admin) isOk no Update  students = null
     */
    @Test
    void patchSubGroup_8() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, null);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un sous groupe (Admin) isOk no Update  students empty
     */
    @Test
    void patchSubGroup_9() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, new HashSet<String>());

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Modification d'un sous groupe (Admin) isBadRequest nom déjà utilisé
     */
    @Test
    void patchSubGroup_10() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup1 = (SubGroup) group.getSubGroups().values().toArray()[0];
        SubGroup subGroup2 = (SubGroup) group.getSubGroups().values().toArray()[1];

        String name = subGroup2.getName();
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup1.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isConflict());
    }

    /**
     * Modification d'un sous groupe (Admin) isBadRequest student ajouté dans un sous groupe alors qu'il n'existe pas dans le groupe
     */
    @Test
    void patchSubGroup_11() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];
        Set<User> studentsNew = groupMockAndData.generateSubSet(group.getStudents(),3);
        studentsNew.add(userMockAndData.get(STUDENT1));
        subGroup.setStudents(studentsNew);

        Set<String> studentsStr = new HashSet<>();
        studentsNew.forEach(s -> studentsStr.add(s.getId()));

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Modification d'un sous groupe (Admin) isBadRequest groupe innexistant
     */
    @Test
    void patchSubGroup_12() throws Exception {
        String name = "Mon Sous Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + UUID.randomUUID()+"/" + UUID.randomUUID()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Modification d'un sous groupe (Admin) isNotFound sous groupe innexistant
     */
    @Test
    void patchSubGroup_13() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        String name = "Mon Sous Groupe";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(NAME_STR, name);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + UUID.randomUUID()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Modification d'un sous groupe (Admin) isNotFound student innexistant
     */
    @Test
    void patchSubGroup_14() throws Exception {
        Group group = groupMockAndData.get(GROUP1);

        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];
        Set<User> studentsNew = groupMockAndData.generateSubSet(group.getStudents(),3);
        subGroup.setStudents(studentsNew);

        Set<String> studentsStr = new HashSet<>();
        studentsNew.forEach(s -> studentsStr.add(s.getId()));
        studentsStr.add(UUID.randomUUID().toString());

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(STUDENTS_STR, studentsStr);

        mvc.perform(patch(URI.create(URL_GROUP + "/" + group.getId()+"/" + subGroup.getId()))
                        .contentType(MediaTypes.HAL_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupMap))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Suppression d'un groupe (Admin) isOk
     */
    @Test
    void deleteGroup_1() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        Mockito.doNothing().when(groupRepository).delete(group);
        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Suppression d'un groupe (Teacher) isForbidden
     */
    @Test
    void deleteGroup_2() throws Exception {
        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Suppression d'un groupe (Student) isForbidden
     */
    @Test
    void deleteGroup_3() throws Exception {
        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Suppression d'un groupe (unConnected) isUnauthorized
     */
    @Test
    void deleteGroup_4() throws Exception {
        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Suppression d'un groupe (Admin) isNotFound groupe innexistant
     */
    @Test
    void deleteGroup_5() throws Exception {
        mvc.perform(delete(URI.create(URL_GROUP + "/" + UUID.randomUUID()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Suppression d'un sous groupe (Admin) OK
     */
    @Test
    void deleteSubGroup_1() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        Group group1 = new Group(group);
        SubGroup subGroup = (SubGroup) group1.getSubGroups().values().toArray()[0];
        group1.getSubGroups().remove(subGroup.getId());
        Mockito.when(groupRepository.save(group1)).thenReturn(group1);

        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + subGroup.getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
    }

    /**
     * Suppression d'un sous groupe (Teacher) isForbidden
     */
    @Test
    void deleteSubGroup_2() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + subGroup.getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Suppression d'un sous groupe (Student) isForbidden
     */
    @Test
    void deleteSubGroup_3() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + subGroup.getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Suppression d'un sous groupe (unConnected) isUnauthorized
     */
    @Test
    void deleteSubGroup_4() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + subGroup.getId())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Suppression d'un sous groupe (Admin) isNotFound groupe innexistant
     */
    @Test
    void deleteSubGroup_5() throws Exception {
        mvc.perform(delete(URI.create(URL_GROUP + "/" + UUID.randomUUID() + "/" + UUID.randomUUID()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Suppression d'un sous groupe (Admin) isNotFound sous groupe innexistant
     */
    @Test
    void deleteSubGroup_6() throws Exception {
        mvc.perform(delete(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + UUID.randomUUID()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Récupération de tous les groupes (Admin) isOK
     */
    @Test
    void getAllGroups_1() throws Exception {
        Map<GroupsKey, Group> all = groupMockAndData.getAll();
        mvc.perform(get(URI.create(URL_GROUP))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath("$", hasSize(all.size())));
    }

    /**
     * Récupération de tous les groupes (Teacher) isOk
     */
    @Test
    void getAllGroups_2() throws Exception {
        Map<GroupsKey, Group> all = groupMockAndData.getAll();
        mvc.perform(get(URI.create(URL_GROUP))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect( jsonPath("$", hasSize(all.size())))
                .andDo(document("group/getAllGroup",
                        responseFields(
                                fieldWithPath("[]").description("Liste de tous les groupes"))
                                .andWithPrefix("[].", groupDescriptor)
                ));
    }

    /**
     * Récupération de tous les groupes (Student) isForbidden
     */
    @Test
    void getAllGroups_3() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupération de tous les groupes (unConnected) isUnauthorized
     */
    @Test
    void getAllGroups_4() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Récupération d'un groupe à partir de son ID (Admin) isOK
     */
    @Test
    void getGroupById_1() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk())
                .andDo(document("group/getById",
                        responseFields(groupDescriptor)
                ));
    }

    /**
     * Récupération d'un groupe à partir de son ID (Teacher) isOk
     */
    @Test
    void getGroupById_2() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isOk());
    }

    /**
     * Récupération d'un groupe à partir de son ID (Student) isForbidden
     */
    @Test
    void getGroupById_3() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupération d'un groupe à partir de son ID (unConnected) isUnauthorized
     */
    @Test
    void getGroupById_4() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Récupération d'un groupe à partir de son ID (Admin) isNotFound groupe innexistant
     */
    @Test
    void getGroupById_5() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP + "/" + UUID.randomUUID()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Récupération d'un sous groupe à partir de son ID (Admin) OK
     */
    @Test
    void getSubGroupById_1() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + subGroup.getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isOk());
                //.andDo(document("group/getSubGroupById",
                //        responseFields(groupDescriptor)
                //));
    }

    /**
     * Récupération d'un sous groupe à partir de son ID (Teacher) isOk
     */
    @Test
    void getSubGroupById_2() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + subGroup.getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(TEACHER1)))
                .andExpect(status().isOk());
    }

    /**
     * Récupération d'un sous groupe à partir de son ID (Student) isForbidden
     */
    @Test
    void getSubGroupById_3() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + subGroup.getId()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(STUDENT1)))
                .andExpect(status().isForbidden());
    }

    /**
     * Récupération d'un sous groupe à partir de son ID (unConnected) isUnauthorized
     */
    @Test
    void getSubGroupById_4() throws Exception {
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + subGroup.getId())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Récupération d'un sous groupe à partir de son ID (Admin) isNotFound sous groupe innexistant
     */
    @Test
    void getSubGroupById_5() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP + "/" + groupMockAndData.get(GROUP1).getId() + "/" + UUID.randomUUID()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Récupération d'un sous groupe à partir de son ID (Admin) groupe innexistant
     */
    @Test
    void getSubGroupById_6() throws Exception {
        mvc.perform(get(URI.create(URL_GROUP + "/" + UUID.randomUUID() + "/" + UUID.randomUUID()))
                        .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
                .andExpect(status().isNotFound());
    }

}
