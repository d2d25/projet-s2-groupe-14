package com.qrcodeemargement.app.controllers;

import com.qrcodeemargement.app.exception.*;
import com.qrcodeemargement.app.models.AttendanceSheet;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.payload.request.AttendanceSheetRequest;
import com.qrcodeemargement.app.payload.request.AttendanceSheetUpdateRequest;
import com.qrcodeemargement.app.payload.request.TokenRequest;
import com.qrcodeemargement.app.payload.response.ApiErrorResponse;
import com.qrcodeemargement.app.payload.response.AttendanceSheetResponse;
import com.qrcodeemargement.app.payload.response.MessageResponse;
import com.qrcodeemargement.app.service.AttendanceSheetService;
import com.qrcodeemargement.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import static com.qrcodeemargement.app.controllers.AttendanceSheetController.URL_ATTENDANCESHEET;
import static com.qrcodeemargement.app.models.Role.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(URL_ATTENDANCESHEET)
public class AttendanceSheetController {
    public static final String URL_ATTENDANCESHEET = "/api/emargement";

    @Autowired
    AttendanceSheetService attendanceSheetService;

    @Autowired
    UserService userService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Object>  getAll(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") @RequestParam Optional<String> idTeacher, Principal principal) throws PrincipaleNotFoundException, UserNotFoundException {
        User principalUser = userService.principalToUser(principal);
        if (principalUser.getRole() == ROLE_TEACHER) {
            if (idTeacher.isPresent() && !principalUser.getId().equals(idTeacher.get()))
                return ResponseEntity.status(FORBIDDEN).body(new ApiErrorResponse(FORBIDDEN, "Un professeur ne peut pas recuperer les feuilles d'emergements de d'autre professeur"));
            try {
                 return ResponseEntity.ok(AttendanceSheetResponse.build(attendanceSheetService.getAllByTeacher(principalUser.getId())));
            } catch (UserNotFoundException e) {
                throw new PrincipaleNotFoundException();
            }
        }
        if (idTeacher.isEmpty())
            return ResponseEntity.ok(AttendanceSheetResponse.build(attendanceSheetService.getAll()));

        return ResponseEntity.ok(AttendanceSheetResponse.build(attendanceSheetService.getAllByTeacher(idTeacher.get())));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Object>  getById(@PathVariable String id, Principal principal) throws PrincipaleNotFoundException {
        try {
            AttendanceSheet attendanceSheet = attendanceSheetService.getById(id);
            User principalUser = userService.principalToUser(principal);
            if (principalUser.getRole() != ROLE_ADMIN && !principalUser.equals(attendanceSheet.getTeacher()))
                return ResponseEntity.status(FORBIDDEN).body(new ApiErrorResponse(FORBIDDEN, "Un professeur ne peut pas consulter une feuilles d'emergements d'un autre professeur"));
            return ResponseEntity.ok(AttendanceSheetResponse.build(attendanceSheet));
        } catch (AttendanceSheetNotFound e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Object>  post(@Valid @RequestBody AttendanceSheetRequest attendanceSheetRequest, Principal principal) throws PrincipaleNotFoundException {
        LocalDateTime beginsAt;
        LocalDateTime endAt;
        try{
            DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            beginsAt = LocalDateTime.parse(attendanceSheetRequest.getBeginsAt(), localDateFormatter);
            endAt = LocalDateTime.parse(attendanceSheetRequest.getEndAt(), localDateFormatter);
        } catch (NullPointerException e) {
            beginsAt = null;
            endAt = null;
        }

        User principalUser = userService.principalToUser(principal);
        try {
            if (principalUser.getRole() == ROLE_ADMIN) {
                if (attendanceSheetRequest.getIdTeacher() == null || attendanceSheetRequest.getIdTeacher().isEmpty())
                    return ResponseEntity.status(BAD_REQUEST).body(new ApiErrorResponse(BAD_REQUEST, "Un admin doit renseigner l'id d'un professeur pour creer une feuille d'émargement"));

                AttendanceSheet attendanceSheet = attendanceSheetService.save(attendanceSheetRequest.getIdTeacher(), attendanceSheetRequest.getSubject(), attendanceSheetRequest.getIdGroup(), attendanceSheetRequest.getIdSubGroup(),endAt, beginsAt);
                return ResponseEntity.created(URI.create(URL_ATTENDANCESHEET+ "/" + attendanceSheet.getId())).body(AttendanceSheetResponse.build(attendanceSheet));

            }
            AttendanceSheet attendanceSheet = attendanceSheetService.save(principalUser.getId(), attendanceSheetRequest.getSubject(), attendanceSheetRequest.getIdGroup(), attendanceSheetRequest.getIdSubGroup(), endAt, beginsAt);
            return ResponseEntity.created(URI.create(URL_ATTENDANCESHEET+ "/" + attendanceSheet.getId())).body(AttendanceSheetResponse.build(attendanceSheet));
        } catch (ParamNeedNullOrEmptyException | BadRoleException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiErrorResponse(BAD_REQUEST, e.getMessage()));
        } catch (GroupNotFoundException | UserNotFoundException | SubGroupNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object>  delete(@PathVariable String id) {
        try {
            attendanceSheetService.delete(id);
            return ResponseEntity.ok().body(new MessageResponse("La feuille d'emergement avec l'id " + id + " a été supprimé"));
        } catch (AttendanceSheetNotFound e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Object>  patch(@PathVariable String id, @Valid @RequestBody AttendanceSheetUpdateRequest attendanceSheetUpdateRequest, Principal principal) throws PrincipaleNotFoundException {
        try {
            Map<String, Boolean> emargementMap = new HashMap<>();
            attendanceSheetUpdateRequest.getEmargements().forEach(emargement -> emargementMap.put(emargement.getUser().getId(), emargement.getIsRegistered()));
            AttendanceSheet attendanceSheet = attendanceSheetService.patch(principal, id, attendanceSheetUpdateRequest.getSubject(), emargementMap, attendanceSheetUpdateRequest.getIsValidate());
            return ResponseEntity.ok().body(AttendanceSheetResponse.build(attendanceSheet));
        } catch (AttendanceSheetNotFound | UserNotFoundInAttendanceSheetException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        } catch (NoPermException e) {
            return ResponseEntity.status(FORBIDDEN).body(new ApiErrorResponse(FORBIDDEN, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/{id}/emarge")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Object>  emarge(@PathVariable String id, @RequestBody TokenRequest token, Principal principal) throws PrincipaleNotFoundException {
        try {
            attendanceSheetService.emerge(principal, id, token.getToken());
            return ResponseEntity.ok().body(new MessageResponse("L'utilisateur à bien valider son emargement"));
        } catch (AttendanceSheetNotFound | UserNotFoundInAttendanceSheetException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        } catch (BadTokenException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiErrorResponse(BAD_REQUEST, "Mauvais token"));
        }
    }


}
