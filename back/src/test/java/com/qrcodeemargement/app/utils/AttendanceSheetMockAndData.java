package com.qrcodeemargement.app.utils;

import com.qrcodeemargement.app.models.AttendanceSheet;
import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.SubGroup;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.repository.AttendanceSheetRepository;
import org.mockito.Mockito;

import java.util.*;

import static com.qrcodeemargement.app.utils.GroupsKey.GROUP1;
import static com.qrcodeemargement.app.utils.GroupsKey.GROUP2;
import static com.qrcodeemargement.app.utils.UsersKey.TEACHER1;
import static com.qrcodeemargement.app.utils.UsersKey.TEACHER2;

public class AttendanceSheetMockAndData {

    private final Map<AttendanceSheetKey, AttendanceSheet> attendancesSheets;
    private final UserMockAndData userMockAndData;
    private final GroupMockAndData groupMockAndData;
    private final AttendanceSheetRepository attendanceSheetRepository;

    public AttendanceSheetMockAndData(UserMockAndData userMockAndData, GroupMockAndData groupMockAndData, AttendanceSheetRepository attendanceSheetRepository) {
        this.userMockAndData = userMockAndData;
        this.groupMockAndData = groupMockAndData;
        this.attendanceSheetRepository = attendanceSheetRepository;
        attendancesSheets = new HashMap<>();
    }

    public AttendanceSheet get(AttendanceSheetKey key) throws Exception {
        switch (key)  {
            case ATTENDANCE_SHEET1 -> {
                if (attendancesSheets.get(key) == null){
                    Group group = groupMockAndData.get(GROUP1);

                    init(key,userMockAndData.get(TEACHER1), key.toString(),group,null);
                }
                return attendancesSheets.get(key);
            }
            case ATTENDANCE_SHEET2 -> {
                if (attendancesSheets.get(key) == null){
                    Group group = groupMockAndData.get(GROUP1);
                    SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

                    init(key,userMockAndData.get(TEACHER1), key.toString(),group,subGroup);
                }
                return attendancesSheets.get(key);
            }
            case ATTENDANCE_SHEET3 -> {
                if (attendancesSheets.get(key) == null){
                    Group group = groupMockAndData.get(GROUP2);

                    init(key,userMockAndData.get(TEACHER1), key.toString(),group,null);
                }
                return attendancesSheets.get(key);
            }
            case ATTENDANCE_SHEET4 -> {
                if (attendancesSheets.get(key) == null){
                    Group group = groupMockAndData.get(GROUP1);
                    SubGroup subGroup = (SubGroup) group.getSubGroups().values().toArray()[0];

                    init(key,userMockAndData.get(TEACHER2), key.toString(),group,subGroup);
                }
                return attendancesSheets.get(key);
            }
            default -> throw new Exception();
        }
    }

    public Map<AttendanceSheetKey, AttendanceSheet> getAll() throws Exception {
        AttendanceSheetKey[] values = AttendanceSheetKey.values();
        for (AttendanceSheetKey value : values) {
            get(value);
        }
        return attendancesSheets;
    }

    private void init(AttendanceSheetKey key, User teacher, String subject, Group group, SubGroup subGroup) throws Exception {
        if (subGroup == null)
            attendancesSheets.put(key, new AttendanceSheet(teacher, subject,group,null,null));
        else
            attendancesSheets.put(key, new AttendanceSheet(teacher, subject,group,subGroup,null,null));
        attendancesSheets.get(key).setId(UUID.randomUUID().toString());

        Mockito.when(attendanceSheetRepository.findById(attendancesSheets.get(key).getId())).thenReturn(Optional.of(attendancesSheets.get(key)));

        Mockito.when(attendanceSheetRepository.findAll()).thenReturn(new ArrayList<>(attendancesSheets.values()));
    }

}
