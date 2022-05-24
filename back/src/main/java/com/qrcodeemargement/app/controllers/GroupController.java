package com.qrcodeemargement.app.controllers;

import com.qrcodeemargement.app.exception.*;
import com.qrcodeemargement.app.models.Group;
import com.qrcodeemargement.app.models.SubGroup;
import com.qrcodeemargement.app.payload.request.GroupRequest;
import com.qrcodeemargement.app.payload.request.GroupUpdateRequest;
import com.qrcodeemargement.app.payload.response.ApiErrorResponse;
import com.qrcodeemargement.app.payload.response.GroupResponse;
import com.qrcodeemargement.app.payload.response.MessageResponse;
import com.qrcodeemargement.app.payload.response.SubGroupResponse;
import com.qrcodeemargement.app.service.GroupService;
import com.qrcodeemargement.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(GroupController.URL_GROUP)
public class GroupController {

    public static final String URL_GROUP = "/api/group";
    @Autowired
    GroupService groupService;

    @Autowired
    UserService userService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Object> getAll() {
        return ResponseEntity.ok(GroupResponse.build(groupService.getAll()));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{idGroup}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Object> getById(@PathVariable String idGroup) {
        try {
            return ResponseEntity.ok(GroupResponse.build(groupService.getGroupById(idGroup)));
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{idGroup}/{idSubGroup}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Object> getSubGroupById(@PathVariable String idGroup, @PathVariable String idSubGroup) {
        try {
            return ResponseEntity.ok(SubGroupResponse.build(idGroup, groupService.getSubGroupById(idGroup, idSubGroup)));
        } catch (GroupNotFoundException | SubGroupNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createGroup(@Valid @RequestBody GroupRequest groupRequest) {
        try {
            Group group = groupService.saveGroup(groupRequest.getName(), groupRequest.getStudents());
            return ResponseEntity.created(URI.create(URL_GROUP + "/" + group.getId())).body(GroupResponse.build(group));
        } catch (UserIsNotStudentException e) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(BAD_REQUEST, e.getMessage()));
        } catch (NameAlreadyUseException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiErrorResponse(CONFLICT, e.getMessage()));
        }catch (UserNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/{idGroup}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createSubGroup(@PathVariable String idGroup ,@Valid @RequestBody GroupRequest groupRequest) {

        try {
            SubGroup subGroup = groupService.addSubGroupToGroup(idGroup, groupRequest.getName(), groupRequest.getStudents());
            return ResponseEntity.created(URI.create(URL_GROUP + "/" + idGroup + "/" + subGroup.getId())).body(SubGroupResponse.build(idGroup, subGroup));
        } catch (GroupNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        } catch (UserAddInSubGroupNotExistInGroup e) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(BAD_REQUEST, e.getMessage()));
        } catch (NameAlreadyUseException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiErrorResponse(CONFLICT, e.getMessage()));
        }
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/{idGroup}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> patchGroup(@PathVariable String idGroup,@Valid @RequestBody GroupUpdateRequest groupRequest) {
        try {
            Group group = groupService.patch(idGroup, groupRequest.getName(), groupRequest.getStudents());
            return ResponseEntity.ok(GroupResponse.build(group));
        } catch (UserIsNotStudentException e) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(BAD_REQUEST, e.getMessage()));
        } catch (GroupNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        } catch (NameAlreadyUseException e){
            return ResponseEntity.status(CONFLICT).body(new ApiErrorResponse(CONFLICT, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/{idGroup}/{idSubGroup}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> patchSubGroup(@PathVariable String idGroup, @PathVariable String idSubGroup,@Valid @RequestBody GroupUpdateRequest groupRequest) {
        try {
            SubGroup subGroup = groupService.patchSubGroup(idGroup, idSubGroup, groupRequest.getName(), groupRequest.getStudents());
            return ResponseEntity.ok(SubGroupResponse.build(idGroup, subGroup));
        } catch (UserAddInSubGroupNotExistInGroup e) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(BAD_REQUEST, e.getMessage()));
        } catch (UserNotFoundException | SubGroupNotFoundException | GroupNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }catch (NameAlreadyUseException e){
            return ResponseEntity.status(CONFLICT).body(new ApiErrorResponse(CONFLICT, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{idGroup}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteGroup(@PathVariable String idGroup) {
        try {
            groupService.delete(idGroup);
            return ResponseEntity.status(200).body(new MessageResponse("Le groupe avec l'id " + idGroup + " à étais supprimé."));
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{idGroup}/{idSubGroup}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteSubGroup(@PathVariable String idGroup, @PathVariable String idSubGroup) {

        try {
            groupService.deleteSubGroup(idGroup, idSubGroup);
            return ResponseEntity.status(200).body(new MessageResponse("Le sous groupe avec l'id " + idSubGroup + " à étais supprimé du groupe avec l'id " + idGroup + "."));
        } catch (GroupNotFoundException | SubGroupNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND, e.getMessage()));
        }
    }

}
