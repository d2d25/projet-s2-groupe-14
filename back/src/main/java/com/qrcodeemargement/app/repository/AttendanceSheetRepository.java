package com.qrcodeemargement.app.repository;

import com.qrcodeemargement.app.models.AttendanceSheet;
import com.qrcodeemargement.app.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AttendanceSheetRepository extends MongoRepository<AttendanceSheet, String> {
    List<AttendanceSheet> findByTeacher(User teacher);
}
