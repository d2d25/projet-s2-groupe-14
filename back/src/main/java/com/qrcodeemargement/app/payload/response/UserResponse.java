package com.qrcodeemargement.app.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qrcodeemargement.app.models.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

public class UserResponse {

    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String numEtu;

    @DBRef
    private String role;

    private UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.numEtu = user.getNumEtu();
        this.role = user.getRole().toString();
    }

    public static Collection<UserResponse> build(Collection<User> users){
        Collection<UserResponse> res = new ArrayList<>();
        users.forEach(user -> res.add(new UserResponse(user)));
        return res;
    }

    public static UserResponse build(User user){
        return new UserResponse(user);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumEtu() {
        return numEtu;
    }

    public void setNumEtu(String numEtu) {
        this.numEtu = numEtu;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
