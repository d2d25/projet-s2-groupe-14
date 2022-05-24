package com.qrcodeemargement.app.service;

import com.qrcodeemargement.app.exception.*;
import com.qrcodeemargement.app.models.*;
import com.qrcodeemargement.app.models.fabrick.FabricAttendanceSheet;
import com.qrcodeemargement.app.repository.AttendanceSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static com.qrcodeemargement.app.models.Role.ROLE_ADMIN;


@Service
public class AttendanceSheetService {

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    FabricAttendanceSheet fabricAttendanceSheet;

    @Autowired
    AttendanceSheetRepository attendanceSheetRepository;

    public List<AttendanceSheet> getAll() {
        return attendanceSheetRepository.findAll();
    }

    public List<AttendanceSheet> getAllByTeacher(String idTeacher) throws UserNotFoundException {
        User teacher = userService.getById(idTeacher);
        return attendanceSheetRepository.findByTeacher(teacher);
    }

    public AttendanceSheet getById(String idAttendanceSheet) throws AttendanceSheetNotFound {
        Optional<AttendanceSheet> attendanceSheet = attendanceSheetRepository.findById(idAttendanceSheet);
        if (attendanceSheet.isEmpty())
            throw new  AttendanceSheetNotFound(idAttendanceSheet);
        return attendanceSheet.get();
    }

    public AttendanceSheet save(String idTeacher, String subject, String idGroup, String idSubGroup,LocalDateTime endAt, LocalDateTime beginsAt) throws ParamNeedNullOrEmptyException, GroupNotFoundException, BadRoleException, UserNotFoundException, SubGroupNotFoundException {
        if (subject == null || subject.isEmpty())
            throw new ParamNeedNullOrEmptyException("subject", "AttendanceSheet");
        User teacher = userService.getById(idTeacher);
        Group group = groupService.getGroupById(idGroup);
        SubGroup subGroup = null;
        if (idSubGroup != null)
            subGroup = groupService.getSubGroupById(idGroup, idSubGroup);

        AttendanceSheet attendanceSheet = fabricAttendanceSheet.build(teacher, subject, group, subGroup, endAt, beginsAt);
        return attendanceSheetRepository.save(attendanceSheet);
    }

    public void delete(String idAttendanceSheet) throws AttendanceSheetNotFound {
        AttendanceSheet attendanceSheet = getById(idAttendanceSheet);
        attendanceSheetRepository.delete(attendanceSheet);
    }

    public AttendanceSheet patch(Principal principal, String idAttendanceSheet, String subject, Map<String, Boolean> emargements, Boolean isValidate) throws AttendanceSheetNotFound, PrincipaleNotFoundException, UserNotFoundInAttendanceSheetException, NoPermException {
        AttendanceSheet attendanceSheet = getById(idAttendanceSheet);
        User principaleUser = userService.principalToUser(principal);
        if (principaleUser.getRole() != ROLE_ADMIN && !principaleUser.equals(attendanceSheet.getTeacher()))
                throw new NoPermException(principaleUser.getRole().toString(), ROLE_ADMIN +" ou l'utilisateur qui à creer la feuille emargement");
        if (subject != null && !subject.isEmpty())
            attendanceSheet.setSubject(subject);
        if (isValidate != null)
            attendanceSheet.validate(isValidate);

        attendanceSheet.updateEmargement(emargements);
        return attendanceSheetRepository.save(attendanceSheet);
    }

    public void emerge(Principal principal, String idAttendanceSheet, String token) throws PrincipaleNotFoundException, AttendanceSheetNotFound, UserNotFoundInAttendanceSheetException, BadTokenException {
        User principalUser = userService.principalToUser(principal);
        AttendanceSheet attendanceSheet = getById(idAttendanceSheet);
        attendanceSheet.registerUser(principalUser,token);
        attendanceSheetRepository.save(attendanceSheet);
    }
}
