package com.qrcodeemargement.app.payload.response;

import com.qrcodeemargement.app.models.Role;

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String id;
	private String email;
	private String role;

	public JwtResponse(String accessToken, String id, String email, Role role) {
		this.token = accessToken;
		this.id = id;
		this.email = email;
		this.role = role.toString();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
