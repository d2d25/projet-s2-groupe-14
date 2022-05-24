package com.qrcodeemargement.app.models.fabrick;

import com.qrcodeemargement.app.exception.BadRoleException;
import com.qrcodeemargement.app.models.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FabricAttendanceSheet {

    public AttendanceSheet build(User teacher, String subject, Group group,SubGroup subGroup, LocalDateTime endAt, LocalDateTime beginsAt) throws BadRoleException {
        if (subGroup == null)
            return new AttendanceSheet(teacher, subject, group, endAt, beginsAt);

        return new AttendanceSheet(teacher, subject, group, subGroup, endAt, beginsAt);
    }
}
