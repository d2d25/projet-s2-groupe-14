package com.qrcodeemargement.app.repository;

import com.qrcodeemargement.app.models.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String> {
    boolean existsByName(String name);

    List<Group> findByName(String name);
}
