package com.qrcodeemargement.app.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Emargement {
    private User user;
    private boolean isRegistered;
    private LocalDateTime dateTime;

    public Emargement() {}

    public Emargement(User user) {
        this.user = user;
        this.isRegistered = false;
        this.dateTime = null;
    }

    public User getUser() {
        return user;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void registration() {
        this.isRegistered = true;
        this.dateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emargement that = (Emargement) o;
        return isRegistered == that.isRegistered && Objects.equals(user, that.user) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, isRegistered, dateTime);
    }

    public void setRegistered(Boolean registered) {
        if (registered == null)
            return;
        if (isRegistered == registered)
            return;
        if (registered)
            dateTime = LocalDateTime.now();
        else
            dateTime = null;
        isRegistered = registered;
    }
}
