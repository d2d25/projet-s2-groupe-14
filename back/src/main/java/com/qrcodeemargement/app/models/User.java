package com.qrcodeemargement.app.models;

import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Document(collection = "users")
public class User {
  @Id
  private String id;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  @JsonIgnore
  private String password;

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  private String numEtu;

  private String role;

  public User() {
  }


  public User(String email, String password, String firstName, String lastName, String numEtu, Role role) {
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.numEtu = numEtu;
    this.role = role.name();
  }

  public User(String email, String password, String firstName, String lastName, Role role) {
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.numEtu = null;
    this.role = role.name();
  }

  public User(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.numEtu = user.getNumEtu();
    if (user.getRole() == null)
      this.role = null;
    else
      this.role = user.getRole().toString();
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public Role getRole() {
    if (role == null)
      return null;
    return Role.valueOf(role);
  }

  public void setRole(Role role) {
    if (role == null)
      this.role = null;
    else
      this.role = role.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(numEtu, user.numEtu) && Objects.equals(role, user.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, firstName, lastName, numEtu, role);
  }
}
