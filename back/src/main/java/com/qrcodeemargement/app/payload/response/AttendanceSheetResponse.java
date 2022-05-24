package com.qrcodeemargement.app.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qrcodeemargement.app.models.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class AttendanceSheetResponse {
    @Id
    private final String id;

    private final UserResponse teacher;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")

    private final LocalDateTime endAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")

    private final LocalDateTime beginsAt;

    private final Boolean isValidate;

    private final String subject;

    private final String idGroup;
    private final String nameGroup;

    private final String idSubGroup;
    private final String nameSubGroup;

    private final String token;

    private final Set<EmargementResponse> emargements;

    public AttendanceSheetResponse(AttendanceSheet attendanceSheet) {
        String idSubGroup1;
        String nameSubGroup1;
        this.id = attendanceSheet.getId();
        this.teacher = UserResponse.build(attendanceSheet.getTeacher());
        this.endAt = attendanceSheet.getEndAt();
        this.beginsAt = attendanceSheet.getBeginsAt();
        this.isValidate = attendanceSheet.isValidate();
        this.subject = attendanceSheet.getSubject();
        this.idGroup = attendanceSheet.getGroup().getId();
        this.nameGroup = attendanceSheet.getGroup().getName();
        idSubGroup1 = null;
        nameSubGroup1 = null;
        if (attendanceSheet.getSubGroup() != null){
            idSubGroup1 = attendanceSheet.getSubGroup().getId();
            nameSubGroup1 = attendanceSheet.getSubGroup().getName();
        }
        this.idSubGroup = idSubGroup1;
        this.nameSubGroup = nameSubGroup1;
        this.token = attendanceSheet.getToken();
        this.emargements = EmargementResponse.build(attendanceSheet.getEmargements());
    }

    public static AttendanceSheetResponse build(AttendanceSheet attendanceSheet){
        return new AttendanceSheetResponse(attendanceSheet);
    }

    public static Collection<AttendanceSheetResponse> build(Collection<AttendanceSheet> attendancesSheets){
        Collection<AttendanceSheetResponse> attendanceSheetResponses = new ArrayList<>();
        attendancesSheets.forEach(attendanceSheet -> attendanceSheetResponses.add(new AttendanceSheetResponse(attendanceSheet)));
        return attendanceSheetResponses;
    }

    public String getId() {
        return id;
    }

    public UserResponse getTeacher() {
        return teacher;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public LocalDateTime getBeginsAt() {
        return beginsAt;
    }

    public Boolean getIsValidate() {
        return isValidate;
    }

    public String getSubject() {
        return subject;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public String getIdSubGroup() {
        return idSubGroup;
    }

    public String getToken() {
        return token;
    }

    public Set<EmargementResponse> getEmargements() {
        return emargements;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public String getNameSubGroup() {
        return nameSubGroup;
    }
}
