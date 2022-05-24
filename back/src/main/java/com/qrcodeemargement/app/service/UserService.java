package com.qrcodeemargement.app.service;

import com.qrcodeemargement.app.exception.PrincipaleNotFoundException;
import com.qrcodeemargement.app.exception.UserNotFoundException;
import com.qrcodeemargement.app.models.Role;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

import static com.qrcodeemargement.app.models.Role.*;

@Service
public class UserService {
    public static final String ADMIN = "admin";
    public static final String TEACHER = "teacher";
    public static final String STUDENT = "student";
    public static final String ERROR_ROLE_IS_NOT_FOUND = "Error: Role is not found.";

    @Autowired
    UserRepository userRepository;


    public boolean haveAccess(User principal, User resource) {
        if(principal.getRole().equals(ROLE_ADMIN))
            return true;
        if (principal.getRole().equals(ROLE_TEACHER)) {
            if (resource.getRole().equals(ROLE_STUDENT))
                return true;
            if (resource.equals(principal))
                return true;
        }
        return principal.getRole().equals(ROLE_STUDENT) && resource.equals(principal);
    }

    public User principalToUser(Principal principal) throws PrincipaleNotFoundException {
        Optional<User> principalUserOptional = userRepository.findByEmail(principal.getName());
        if (principalUserOptional.isEmpty()){
            throw new PrincipaleNotFoundException();
        }
        return principalUserOptional.get();
    }

    public User getById(String id) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw  new UserNotFoundException(id);
        }
        return userOptional.get();
    }

    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    public User put(User user){
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw  new UserNotFoundException(email);
        }
        return userOptional.get();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Collection<User> getAllByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
