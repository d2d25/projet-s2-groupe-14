package com.qrcodeemargement.app.repository;

import com.qrcodeemargement.app.models.Role;
import com.qrcodeemargement.app.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);

  List<User> findByRole(Role role);
}
