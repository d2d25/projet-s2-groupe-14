package com.qrcodeemargement.app.payload.request;

import com.qrcodeemargement.app.models.User;

public class EmargementRequest {
    private User user;
    private Boolean isRegistered;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(Boolean registered) {
        isRegistered = registered;
    }
}
