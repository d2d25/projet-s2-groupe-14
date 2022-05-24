package com.qrcodeemargement.app.controllers;

import com.qrcodeemargement.app.exception.PrincipaleNotFoundException;
import com.qrcodeemargement.app.exception.UserNotFoundException;
import com.qrcodeemargement.app.models.Role;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.payload.request.PasswordRequest;
import com.qrcodeemargement.app.payload.request.UserRequest;
import com.qrcodeemargement.app.payload.response.ApiErrorResponse;
import com.qrcodeemargement.app.payload.response.MessageResponse;
import com.qrcodeemargement.app.payload.response.UserResponse;
import com.qrcodeemargement.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

import static com.qrcodeemargement.app.models.Role.ROLE_STUDENT;
import static com.qrcodeemargement.app.models.Role.ROLE_TEACHER;

@RestController
@RequestMapping(UserController.URL_USER)
public class UserController {
    private static final String UTILISATEUR_AVEC_L_EMAIL = "Utilisateur avec l'email : ";
    private static final String N_A_PAS_LES_AUTORISATION_POUR_ACCEDER_A_CETTE_RESSOURCE = " n'a pas les autorisation pour accéder à cette ressource.";
    public static final String UTILISATEUR_AVEC_L_ID = "Utilisateur avec l'id : ";
    public static final String N_EXISTE_PAS = "n'existe pas.";
    public static final String ET_L_EMAIL = " et l'email : ";
    public static final String BIEN_ETAIT_SUPPRIMER = " à bien était supprimer";
    public static final String URL_USER = "/api/user";
    public static final String CURRENT = "/current";
    public static final String UPDATE_PASSWORD = "/updatePassword";
    public static final String NE_PEUT_PAS_ETRE_VIDE = " ne peut pas etre vide";
    public static final String L_EMAIL = "L'email ";
    public static final String LE_FIRST_NAME = "Le firstName";
    public static final String LE_LAST_NAME = "Le lastName";
    public static final String LE_NUM_ETU = "Le numEtu";
    public static final String MOT_DE_PASSE_DE_L_UTILISATEUR = "Mot de passe de l'utilisateur ";
    public static final String BIEN_MODIFIER = " bien modifier";
    public static final String LE_MOT_DE_PASSE_NE_PEUT_PAS_ETRE_NULL = "le mot de passe ne peut pas etre null";
    public static final String SEUL_UN_ETUDIENT_PEUT_AVOIR_UN_NUM_ETU = "Seul un Etudient peut avoir un numEtu";
    public static final String UN_ETUDIENT_A_BESOIN_D_UN_NUM_ETU = "Un Etudient à besoin d'un numEtu";
    public static final String BY_ROLE = "/byRole";
    public static final String IL_DOIT_Y_AVOIR_UN_ROLE_NON_NULL_ET_NON_VIDE = "Il doit y avoir un role non null et non vide";

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getAll() {
        return ResponseEntity.ok(UserResponse.build(userService.getAll()));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id, Principal principal) throws PrincipaleNotFoundException {
        User user;
        try {
            user = userService.getById(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(HttpStatus.NOT_FOUND, UTILISATEUR_AVEC_L_ID + id + N_EXISTE_PAS));
        }
        return getIfHaveAccess(principal, user);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(CURRENT)
    public ResponseEntity<Object> getCurrent(Principal principal) throws PrincipaleNotFoundException {
        return ResponseEntity.ok(UserResponse.build(userService.principalToUser(principal)));
    }

    private ResponseEntity<Object> getIfHaveAccess(Principal principal, User user) throws PrincipaleNotFoundException {
        if (userService.haveAccess(userService.principalToUser(principal), user)) {
            return ResponseEntity.ok(UserResponse.build(user));
        }else {
            return ResponseEntity.status(HttpStatus.valueOf(403)).body(new ApiErrorResponse(HttpStatus.valueOf(403), UTILISATEUR_AVEC_L_EMAIL + principal.getName() + N_A_PAS_LES_AUTORISATION_POUR_ACCEDER_A_CETTE_RESSOURCE));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> patch(@PathVariable String id, @Valid @RequestBody UserRequest userRequest){
        User user;
        try {
            user = userService.getById(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(HttpStatus.NOT_FOUND, UTILISATEUR_AVEC_L_ID + id + N_EXISTE_PAS));
        }

        if (userRequest.getEmail() != null){
            if (userRequest.getEmail().isEmpty())
                return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, L_EMAIL + NE_PEUT_PAS_ETRE_VIDE));
            user.setEmail(userRequest.getEmail());
        }

        if (userRequest.getFirstName() != null){
            if (userRequest.getFirstName().isEmpty())
                return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, LE_FIRST_NAME + NE_PEUT_PAS_ETRE_VIDE));
            user.setFirstName(userRequest.getFirstName());
        }

        if (userRequest.getLastName() != null){
            if (userRequest.getLastName().isEmpty())
                return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, LE_LAST_NAME + NE_PEUT_PAS_ETRE_VIDE));
            user.setLastName(userRequest.getLastName());
        }

        //Gestion des role et du num Etu
        if (userRequest.getRole() != null) {

            user.setRole(userRequest.getRole());

            if (userRequest.getRole() == ROLE_STUDENT){

                if (userRequest.getNumEtu() == null)
                    return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, UN_ETUDIENT_A_BESOIN_D_UN_NUM_ETU));

                if (userRequest.getNumEtu().isEmpty())
                    return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, LE_NUM_ETU + NE_PEUT_PAS_ETRE_VIDE));

                user.setNumEtu(userRequest.getNumEtu());

            }else if (userRequest.getNumEtu() != null)
                return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, SEUL_UN_ETUDIENT_PEUT_AVOIR_UN_NUM_ETU));

            else
                user.setNumEtu(null);

        }else {

            if (userRequest.getNumEtu() != null){

                if (user.getRole() == ROLE_STUDENT){

                    if (userRequest.getNumEtu().isEmpty())
                        return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, LE_NUM_ETU + NE_PEUT_PAS_ETRE_VIDE));

                    user.setNumEtu(userRequest.getNumEtu());
                }else
                    return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, SEUL_UN_ETUDIENT_PEUT_AVOIR_UN_NUM_ETU));

            }
        }
        return ResponseEntity.ok(UserResponse.build(userService.put(user)));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteById(@PathVariable String id) {
        try {
            User user = userService.getById(id);
            userService.delete(user);
            return ResponseEntity.ok(new MessageResponse(UTILISATEUR_AVEC_L_ID + user.getId() + ET_L_EMAIL + user.getEmail() + BIEN_ETAIT_SUPPRIMER));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(HttpStatus.NOT_FOUND, UTILISATEUR_AVEC_L_ID + id + N_EXISTE_PAS));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping(UPDATE_PASSWORD)
    public ResponseEntity<Object> updatePassword(@Valid @RequestBody PasswordRequest passwordRequest, Principal principal) throws PrincipaleNotFoundException {
        if (passwordRequest.getPassword() == null)
            return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, LE_MOT_DE_PASSE_NE_PEUT_PAS_ETRE_NULL));
        User user = userService.principalToUser(principal);
        user.setPassword(encoder.encode(passwordRequest.getPassword()));
        userService.put(user);
        return ResponseEntity.ok(new MessageResponse(MOT_DE_PASSE_DE_L_UTILISATEUR + user.getEmail() + BIEN_MODIFIER));

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(BY_ROLE)
    public ResponseEntity<Object> getAllByRole(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") @RequestParam Optional<Role> role, Principal principal) throws PrincipaleNotFoundException {
        if (role.isEmpty())
            return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST, IL_DOIT_Y_AVOIR_UN_ROLE_NON_NULL_ET_NON_VIDE));
        User user = userService.principalToUser(principal);
        if (user.getRole() == ROLE_STUDENT)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if (user.getRole() == ROLE_TEACHER && role.get() != ROLE_STUDENT )
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(UserResponse.build(userService.getAllByRole(role.get())));

    }
}
