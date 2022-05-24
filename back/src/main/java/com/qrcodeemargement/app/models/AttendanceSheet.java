package com.qrcodeemargement.app.models;

import com.qrcodeemargement.app.exception.BadRoleException;
import com.qrcodeemargement.app.exception.BadTokenException;
import com.qrcodeemargement.app.exception.UserNotFoundInAttendanceSheetException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "attendances_sheets")
public class AttendanceSheet {

    @Id
    private String id;

    private User teacher;

    private LocalDateTime endAt;

    private LocalDateTime beginsAt;

    private Boolean isValidate;

    private String subject;

    private Group group;

    private SubGroup subGroup;

    private String token;

    private Set<Emargement> emargements;

    public AttendanceSheet(){}

    public AttendanceSheet(AttendanceSheet attendanceSheet) {
        this.id = attendanceSheet.id;
        this.teacher = new User(attendanceSheet.teacher);
        this.subject = attendanceSheet.subject;
        if (attendanceSheet.subGroup == null)
            this.subGroup = null;
        else
            this.subGroup = attendanceSheet.subGroup;
        this.group = attendanceSheet.group;
        this.endAt = attendanceSheet.endAt;
        this.beginsAt = attendanceSheet.beginsAt;
        this.isValidate = attendanceSheet.isValidate;
        this.token = attendanceSheet.token;
        this.emargements = new HashSet<>(attendanceSheet.emargements);
    }

    public AttendanceSheet(User teacher, String subject, Group group, LocalDateTime endAt, LocalDateTime beginsAt) throws BadRoleException {
        if (teacher.getRole() != Role.ROLE_TEACHER)
            throw new BadRoleException(teacher.getRole().toString(), Role.ROLE_TEACHER.toString());
        this.teacher = teacher;
        this.subject = subject;
        this.group = group;
        this.subGroup = null;
        this.endAt = endAt;
        this.beginsAt = beginsAt;
        if (beginsAt == null)
            this.beginsAt = LocalDateTime.now();
        this.isValidate = false;
        this.token = UUID.randomUUID().toString();
        this.emargements = new HashSet<>();
        for (User student: group.getStudents()) {
            emargements.add(new Emargement(student));
        }
    }

    public AttendanceSheet(User teacher, String subject, Group group, SubGroup subGroup, LocalDateTime endAt, LocalDateTime beginsAt) throws BadRoleException {
        if (teacher.getRole() != Role.ROLE_TEACHER)
            throw new BadRoleException(teacher.getRole().toString(), Role.ROLE_TEACHER.toString());
        this.teacher = teacher;
        this.subject = subject;
        this.subGroup = subGroup;
        this.group = group;
        this.endAt = endAt;
        this.beginsAt = beginsAt;
        if (beginsAt == null)
            this.beginsAt = LocalDateTime.now();
        this.isValidate = false;
        this.token = UUID.randomUUID().toString();
        this.emargements = new HashSet<>();
        for (User student: subGroup.getStudents()) {
            emargements.add(new Emargement(student));
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getId() {
        return id;
    }

    public User getTeacher() {
        return teacher;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public Boolean isValidate() {
        return isValidate;
    }

    public String getSubject() {
        return subject;
    }

    public Group getGroup() {
        return group;
    }

    public SubGroup getSubGroup() {
        return subGroup;
    }

    public String getToken() {
        return token;
    }

    public Set<Emargement> getEmargements() {
        return emargements;
    }

    public Emargement getEmargement(String idStudent) throws UserNotFoundInAttendanceSheetException {
        for (Emargement emargement : emargements) {
            if (emargement.getUser().getId().equals(idStudent))
                return emargement;
        }
        throw new UserNotFoundInAttendanceSheetException(idStudent);
    }


    public void validate(Boolean isValidate){
        if (isValidate == null || Objects.equals(isValidate, this.isValidate))
            return;
        if (Boolean.TRUE.equals(isValidate))
            this.endAt = LocalDateTime.now();
        this.isValidate = isValidate;
    }

    public void registerUser(User user, String token) throws UserNotFoundInAttendanceSheetException, BadTokenException {
        for (Emargement emargement : emargements) {
            if (emargement.getUser().equals(user)) {
                if (!this.token.equals(token))
                    throw new BadTokenException();
                emargement.registration();
                return;
            }
        }
        throw new UserNotFoundInAttendanceSheetException(user.getId());
    }

    public LocalDateTime getBeginsAt() {
        return beginsAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceSheet that = (AttendanceSheet) o;
        return Objects.equals(id, that.id) && Objects.equals(teacher, that.teacher) && Objects.equals(endAt, that.endAt) && Objects.equals(beginsAt, that.beginsAt) && Objects.equals(isValidate, that.isValidate) && Objects.equals(subject, that.subject) && Objects.equals(group, that.group) && Objects.equals(subGroup, that.subGroup) && Objects.equals(token, that.token) && Objects.equals(emargements, that.emargements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teacher, endAt, beginsAt, isValidate, subject, group, subGroup, token, emargements);
    }

    public void updateEmargement(Map<String, Boolean> emargements) throws UserNotFoundInAttendanceSheetException {
        for (Map.Entry<String, Boolean> entry : emargements.entrySet()) {
            String idUser = entry.getKey();
            Boolean aBoolean = entry.getValue();
            Emargement emargement = getEmargement(idUser);
            emargement.setRegistered(aBoolean);
        }
    }
}
