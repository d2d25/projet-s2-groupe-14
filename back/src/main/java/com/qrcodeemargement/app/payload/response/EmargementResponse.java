package com.qrcodeemargement.app.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qrcodeemargement.app.models.Emargement;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EmargementResponse {
    private final UserResponse user;
    private final boolean isRegistered;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private final LocalDateTime dateTime;

    private EmargementResponse(Emargement emargement) {
        this.user = UserResponse.build(emargement.getUser());
        this.isRegistered = emargement.isRegistered();
        this.dateTime = emargement.getDateTime();
    }

    public static Set<EmargementResponse> build(Set<Emargement> emargements){
        Set<EmargementResponse> res = new HashSet<>();
        emargements.forEach(emargement -> res.add(new EmargementResponse(emargement)));
        return res;
    }

    public UserResponse getUser() {
        return user;
    }

    public boolean getIsRegistered() {
        return isRegistered;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
