package com.btxtech.game.services.connection;

import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;

/**
 * User: beat
 * Date: 02.05.13
 * Time: 12:51
 */
public class OnlineUserDTO {
    private String sessionId;
    private String baseName;
    private String planetName;
    private User user;

    public OnlineUserDTO(UserState userState, String planetName) {
        this.planetName = planetName;
        sessionId = userState.getSessionId();
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPlanetName() {
        return planetName;
    }

    public boolean isRegistered() {
        return user != null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
