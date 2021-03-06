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
     * Cr??ation d'une feuille d'??margement (Admin) isCreated avec un groupe et teacher
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
     * Cr??ation d'une feuille d'??margement (Admin) isCreated avec un groupe un sous groupe et teacher
     */
    @Test
    void postEmargement_1_1() throws Exception {
        String subject = "mon subject";
        User teacher = userMockAndData.get(UsersKey.TEACHER1);
        Group group = groupMockAndData.get(GROUP1);
        SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];
    }

    /**
     * Cr??ation d'une feuille d'??margement (Teacher) isCreated
     */
    @Test
    void postEmargement_2() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Student) isForbidden
     */
    @Test
    void postEmargement_3() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (unConnected) unAuthorized
     */
    @Test
    void postEmargement_4() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Admin) isBadRequest idTeacher = null
     */
    @Test
    void postEmargement_5() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Admin) isBadRequest idTeacher = ""
     */
    @Test
    void postEmargement_6() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Admin) isBadRequest subject = null
     */
    @Test
    void postEmargement_7() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Admin) isBadRequest subject = ""
     */
    @Test
    void postEmargement_8() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Admin) isBadRequest mauvais role
     */
    @Test
    void postEmargement_9() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Admin) isNotFound groupe innexistant
     */
    @Test
    void postEmargement_10() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Admin) isNotFound user innexistant
     */
    @Test
    void postEmargement_11() {

    }

    /**
     * Cr??ation d'une feuille d'??margement (Admin) isNotFound sous groupe innexistant
     */
    @Test
    void postEmargement_12() {

    }

    /**
     * R??cup??ration de tous les ??margements (Admin) OK
     */
    @Test
    void getAllEmargements_1() throws Exception {

    }

    /**
     * R??cup??ration de tous les ??margements (Teacher) OK
     */
    @Test
    void getAllEmargements_2() throws Exception {

    }

    /**
     * R??cup??ration de tous les ??margements (Student) isForbidden
     */
    @Test
    void getAllEmargements_3() throws Exception {

    }

    /**
     * R??cup??ration de tous les ??margements (unConnected) isUnauthorized
     */
    @Test
    void getAllEmargements_4() throws Exception {

    }

    /**
     * R??cup??ration de tous les ??margements d'un autre prof (Teacher) isForbidden
     */
    @Test
    void getAllEmargements_5() throws Exception {

    }

    /**
     * R??cup??ration de tous les ??margements (Admin) IsNotFound teacher innexistant
     */
    @Test
    void getAllEmargements_6() throws Exception {

    }

    /**
     * R??cup??ration d'un ??margement ?? partir de son ID (Admin) OK
     */
    @Test
    void getEmargementById_1() throws Exception {

    }

    /**
     * R??cup??ration d'un ??margement ?? partir de son ID (Teacher) OK
     */
    @Test
    void getEmargementById_2() throws Exception {

    }

    /**
     * R??cup??ration d'un ??margement d'un autre prof ?? partir de l'ID de l'??margement (Teacher) isForbidden
     */
    @Test
    void getEmargementById_3() throws Exception {

    }

    /**
     * R??cup??ration d'un ??margement ?? partir de son ID (Student) isForbidden
     */
    @Test
    void getEmargementById_4() throws Exception {

    }

    /**
     * R??cup??ration d'un ??margement ?? partir de son ID (unConnected) isUnauthorized
     */
    @Test
    void getEmargementById_5() throws Exception {

    }

    /**
     * R??cup??ration d'un ??margement ?? partir de son ID (Teacher) isNotFound attendanceSheet innexistante
     */
    @Test
    void getEmargementById_6() throws Exception {

    }

    /**
     * R??cup??ration d'un ??margement ?? partir de son ID (Admin) isNotFound attendanceSheet innexistante
     */
    @Test
    void getEmargementById_7() throws Exception {

    }

    /**
     * Suppression d'un ??margement (Admin) OK
     */
    @Test
    void deleteEmargement_1() throws Exception {

    }

    /**
     * Suppression d'un ??margement (Teacher) isForbidden
     */
    @Test
    void deleteEmargement_2() throws Exception {

    }

    /**
     * Suppression d'un ??margement (Student) isForbidden
     */
    @Test
    void deleteEmargement_3() throws Exception {

    }

    /**
     * Suppression d'un ??margement (unConnected) isUnauthorized
     */
    @Test
    void deleteEmargement_4() throws Exception {

    }

    /**
     * Suppression d'un ??margement (Admin) isNotFound attendanceSheet innexistante
     */
    @Test
    void deleteEmargement_5() throws Exception {

    }

    /**
     * Modification d'un ??margement (Admin) OK
     */
    @Test
    void patchEmargement_1() throws Exception {

    }

    /**
     * Modification d'un ??margement (Teacher) OK
     */
    @Test
    void patchEmargement_2() throws Exception {

    }

    /**
     * Modification d'un ??margement (Student) isForbidden
     */
    @Test
    void patchEmargement_3() throws Exception {

    }

    /**
     * Modification d'un ??margement (unConnected) isUnauthorized
     */
    @Test
    void patchEmargement_4() throws Exception {

    }

    /**
     * Modification d'un ??margement (Admin) isNotFound attendanceSheet innexistante
     */
    @Test
    void patchEmargement_5() throws Exception {

    }

    /**
     * Modification d'un ??margement (Teacher) isNotFound attendanceSheet innexistante
     */
    @Test
    void patchEmargement_6() throws Exception {

    }

    /**
     * Modification d'un ??margement (Admin) isNotFound user innexistant dans l'attendanceSheet
     */
    @Test
    void patchEmargement_7() throws Exception {

    }

    /**
     * Modification d'un ??margement (Teacher) isNotFound user innexistant dans l'attendanceSheet
     */
    @Test
    void patchEmargement_8() throws Exception {

    }

    /**
     * Modification d'un ??margement d'un autre prof (Teacher) isForbidden
     */
    @Test
    void patchEmargement_9() throws Exception {

    }

    /**
     * Emargement d'un ??tudiant (Student) OK
     */
    @Test
    void emarge_1() throws Exception {

    }

    /**
     * Emargement d'un ??tudiant (Teacher) isForbidden
     */
    @Test
    void emarge_2() throws Exception {

    }

    /**
     * Emargement d'un ??tudiant (Admin) isForbidden
     */
    @Test
    void emarge_3() throws Exception {

    }

    /**
     * Emargement d'un ??tudiant (unConnected) isUnauthorized
     */
    @Test
    void emarge_4() throws Exception {

    }

    /**
     * Emargement d'un ??tudiant (Student) isNotFound attendanceSheet innexistante
     */
    @Test
    void emarge_5() throws Exception {

    }

    /**
     * Emargement d'un ??tudiant (Student) isNotFound student innexistant dans l'attendanceSheet
     */
    @Test
    void emarge_6() throws Exception {

    }

    /**
     * Emargement d'un ??tudiant (Student) isBadRequest mauvais token
     */
    @Test
    void emarge_7() throws Exception {

    }

}
