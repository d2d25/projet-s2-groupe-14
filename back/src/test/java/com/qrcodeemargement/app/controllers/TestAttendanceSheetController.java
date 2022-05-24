package com.qrcodeemargement.app.controllers;


import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.SubGroup;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.repository.AttendanceSheetRepository;
import com.qrcodeemargement.app.repository.GroupRepository;
import com.qrcodeemargement.app.repository.UserRepository;
import com.qrcodeemargement.app.utils.AttendanceSheetMockAndData;
import com.qrcodeemargement.app.utils.GroupMockAndData;
import com.qrcodeemargement.app.utils.UserMockAndData;
import com.qrcodeemargement.app.utils.UsersKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.qrcodeemargement.app.utils.GroupsKey.GROUP1;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class TestAttendanceSheetController {
    public static final String USER_STR = "user";
    public static final Boolean IS_REGISTERED_BOOL = false;

    @Autowired
    MockMvc mvc;
    @MockBean
    UserRepository userRepository;
    @MockBean
    GroupRepository groupRepository;
    @MockBean
    AttendanceSheetRepository attendanceSheetRepository;

    @Autowired
    PasswordEncoder encoder;

    UserMockAndData userMockAndData;
    GroupMockAndData groupMockAndData;
    AttendanceSheetMockAndData attendanceSheetMockAndData;


    @BeforeEach
    private void init() {
        userMockAndData = new UserMockAndData(userRepository, encoder, mvc);
        groupMockAndData = new GroupMockAndData(groupRepository,userMockAndData);
        attendanceSheetMockAndData = new AttendanceSheetMockAndData(userMockAndData,groupMockAndData,attendanceSheetRepository);
    }


    /**
     * Création d'une feuille d'émargement (Admin) isCreated avec un groupe et teacher
     */
    @Test
    void postEmargement_1() throws Exception {
//        String subject = "mon subject";
//        User teacher = userMockAndData.get(UsersKey.TEACHER1);
//        Group group = groupMockAndData.get(GROUP1);
//
//
//        Map<String,Object> map = new HashMap<>();
//
//
//        mvc.perform(post(URI.create(URL_ATTENDANCESHEET))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(map))
//                .header("Authorization", "Bearer " + userMockAndData.getToken(ADMIN1)))
//                .andExpect(status().isCreated())
//                .andExpect(redirectedUrl(URL_ATTENDANCESHEET + "/"+ "id"));
    }

    /**
     * Création d'une feuille d'émargement (Admin) isCreated avec un groupe un sous groupe et teacher
     */
    @Test
    void postEmargement_1_1() throws Exception {
        String subject = "mon subject";
        User teacher = userMockAndData.get(UsersKey.TEACHER1);
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];
    }

    /**
     * Création d'une feuille d'émargement (Teacher) isCreated
     */
    @Test
    void postEmargement_2() {

    }

    /**
     * Création d'une feuille d'émargement (Student) isForbidden
     */
    @Test
    void postEmargement_3() {

    }

    /**
     * Création d'une feuille d'émargement (unConnected) unAuthorized
     */
    @Test
    void postEmargement_4() {

    }

    /**
     * Création d'une feuille d'émargement (Admin) isBadRequest idTeacher = null
     */
    @Test
    void postEmargement_5() {

    }

    /**
     * Création d'une feuille d'émargement (Admin) isBadRequest idTeacher = ""
     */
    @Test
    void postEmargement_6() {

    }

    /**
     * Création d'une feuille d'émargement (Admin) isBadRequest subject = null
     */
    @Test
    void postEmargement_7() {

    }

    /**
     * Création d'une feuille d'émargement (Admin) isBadRequest subject = ""
     */
    @Test
    void postEmargement_8() {

    }

    /**
     * Création d'une feuille d'émargement (Admin) isBadRequest mauvais role
     */
    @Test
    void postEmargement_9() {

    }

    /**
     * Création d'une feuille d'émargement (Admin) isNotFound groupe innexistant
     */
    @Test
    void postEmargement_10() {

    }

    /**
     * Création d'une feuille d'émargement (Admin) isNotFound user innexistant
     */
    @Test
    void postEmargement_11() {

    }

    /**
     * Création d'une feuille d'émargement (Admin) isNotFound sous groupe innexistant
     */
    @Test
    void postEmargement_12() {

    }

    /**
     * Récupération de tous les émargements (Admin) OK
     */
    @Test
    void getAllEmargements_1() throws Exception {

    }

    /**
     * Récupération de tous les émargements (Teacher) OK
     */
    @Test
    void getAllEmargements_2() throws Exception {

    }

    /**
     * Récupération de tous les émargements (Student) isForbidden
     */
    @Test
    void getAllEmargements_3() throws Exception {

    }

    /**
     * Récupération de tous les émargements (unConnected) isUnauthorized
     */
    @Test
    void getAllEmargements_4() throws Exception {

    }

    /**
     * Récupération de tous les émargements d'un autre prof (Teacher) isForbidden
     */
    @Test
    void getAllEmargements_5() throws Exception {

    }

    /**
     * Récupération de tous les émargements (Admin) IsNotFound teacher innexistant
     */
    @Test
    void getAllEmargements_6() throws Exception {

    }

    /**
     * Récupération d'un émargement à partir de son ID (Admin) OK
     */
    @Test
    void getEmargementById_1() throws Exception {

    }

    /**
     * Récupération d'un émargement à partir de son ID (Teacher) OK
     */
    @Test
    void getEmargementById_2() throws Exception {

    }

    /**
     * Récupération d'un émargement d'un autre prof à partir de l'ID de l'émargement (Teacher) isForbidden
     */
    @Test
    void getEmargementById_3() throws Exception {

    }

    /**
     * Récupération d'un émargement à partir de son ID (Student) isForbidden
     */
    @Test
    void getEmargementById_4() throws Exception {

    }

    /**
     * Récupération d'un émargement à partir de son ID (unConnected) isUnauthorized
     */
    @Test
    void getEmargementById_5() throws Exception {

    }

    /**
     * Récupération d'un émargement à partir de son ID (Teacher) isNotFound attendanceSheet innexistante
     */
    @Test
    void getEmargementById_6() throws Exception {

    }

    /**
     * Récupération d'un émargement à partir de son ID (Admin) isNotFound attendanceSheet innexistante
     */
    @Test
    void getEmargementById_7() throws Exception {

    }

    /**
     * Suppression d'un émargement (Admin) OK
     */
    @Test
    void deleteEmargement_1() throws Exception {

    }

    /**
     * Suppression d'un émargement (Teacher) isForbidden
     */
    @Test
    void deleteEmargement_2() throws Exception {

    }

    /**
     * Suppression d'un émargement (Student) isForbidden
     */
    @Test
    void deleteEmargement_3() throws Exception {

    }

    /**
     * Suppression d'un émargement (unConnected) isUnauthorized
     */
    @Test
    void deleteEmargement_4() throws Exception {

    }

    /**
     * Suppression d'un émargement (Admin) isNotFound attendanceSheet innexistante
     */
    @Test
    void deleteEmargement_5() throws Exception {

    }

    /**
     * Modification d'un émargement (Admin) OK
     */
    @Test
    void patchEmargement_1() throws Exception {

    }

    /**
     * Modification d'un émargement (Teacher) OK
     */
    @Test
    void patchEmargement_2() throws Exception {

    }

    /**
     * Modification d'un émargement (Student) isForbidden
     */
    @Test
    void patchEmargement_3() throws Exception {

    }

    /**
     * Modification d'un émargement (unConnected) isUnauthorized
     */
    @Test
    void patchEmargement_4() throws Exception {

    }

    /**
     * Modification d'un émargement (Admin) isNotFound attendanceSheet innexistante
     */
    @Test
    void patchEmargement_5() throws Exception {

    }

    /**
     * Modification d'un émargement (Teacher) isNotFound attendanceSheet innexistante
     */
    @Test
    void patchEmargement_6() throws Exception {

    }

    /**
     * Modification d'un émargement (Admin) isNotFound user innexistant dans l'attendanceSheet
     */
    @Test
    void patchEmargement_7() throws Exception {

    }

    /**
     * Modification d'un émargement (Teacher) isNotFound user innexistant dans l'attendanceSheet
     */
    @Test
    void patchEmargement_8() throws Exception {

    }

    /**
     * Modification d'un émargement d'un autre prof (Teacher) isForbidden
     */
    @Test
    void patchEmargement_9() throws Exception {

    }

    /**
     * Emargement d'un étudiant (Student) OK
     */
    @Test
    void emarge_1() throws Exception {

    }

    /**
     * Emargement d'un étudiant (Teacher) isForbidden
     */
    @Test
    void emarge_2() throws Exception {

    }

    /**
     * Emargement d'un étudiant (Admin) isForbidden
     */
    @Test
    void emarge_3() throws Exception {

    }

    /**
     * Emargement d'un étudiant (unConnected) isUnauthorized
     */
    @Test
    void emarge_4() throws Exception {

    }

    /**
     * Emargement d'un étudiant (Student) isNotFound attendanceSheet innexistante
     */
    @Test
    void emarge_5() throws Exception {

    }

    /**
     * Emargement d'un étudiant (Student) isNotFound student innexistant dans l'attendanceSheet
     */
    @Test
    void emarge_6() throws Exception {

    }

    /**
     * Emargement d'un étudiant (Student) isBadRequest mauvais token
     */
    @Test
    void emarge_7() throws Exception {

    }

}
