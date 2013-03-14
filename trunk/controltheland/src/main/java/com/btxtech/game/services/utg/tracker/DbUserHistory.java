/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.utg.tracker;

import com.btxtech.game.services.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * User: beat
 * Date: 07.03.2010
 * Time: 14:59:44
 */
@Entity(name = "TRACKER_USER_HISTORY")
public class DbUserHistory {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "user_name")
    private String user;
    private Date created;
    private Date loggedIn;
    private Date loggedOut;
    private Date baseCreated;
    private Date baseDefeated;
    private Date baseSurrender;
    private Date gameEntered;
    private Date gameLeft;
    private String baseName;
    private String sessionId;
    private String cookieId;
    private String verificationId;
    private Date awaitingVerificationDate;
    private Date verified;
    private Date deleteUnverifiedUser;
    private Date forgotPasswordRequest;
    private Date forgotPasswordRequestRemoved;
    private Date passwordChanged;
    private String forgotPasswordUuid;

    /**
     * Used by hibernate
     */
    protected DbUserHistory() {
    }

    public DbUserHistory(User user) {
        this.user = user.getUsername();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated() {
        created = new Date();
    }

    public Date getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn() {
        loggedIn = new Date();
    }

    public Date getLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut() {
        loggedOut = new Date();
    }

    public Date getBaseCreated() {
        return baseCreated;
    }

    public void setBaseCreated() {
        baseCreated = new Date();
    }

    public Date getBaseDefeated() {
        return baseDefeated;
    }

    public void setBaseDefeated() {
        baseDefeated = new Date();
    }

    public Date getBaseSurrender() {
        return baseSurrender;
    }

    public void setBaseSurrender() {
        baseSurrender = new Date();
    }

    public Date getGameEntered() {
        return gameEntered;
    }

    public void setGameEntered() {
        gameEntered = new Date();
    }

    public Date getGameLeft() {
        return gameLeft;
    }

    public void setGameLeft() {
        gameLeft = new Date();
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

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public String getUser() {
        return user;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public Date getAwaitingVerificationDate() {
        return awaitingVerificationDate;
    }

    public void setAwaitingVerificationDate(Date awaitingVerificationDate) {
        this.awaitingVerificationDate = awaitingVerificationDate;
    }

    public Date getVerified() {
        return verified;
    }

    public void setVerified() {
        this.verified = new Date();
    }

    public Date getDeleteUnverifiedUser() {
        return deleteUnverifiedUser;
    }

    public void setDeleteUnverifiedUser() {
        deleteUnverifiedUser = new Date();
    }

    public Date getForgotPasswordRequest() {
        return forgotPasswordRequest;
    }

    public void setForgotPasswordRequest() {
        forgotPasswordRequest = new Date();
    }

    public Date getForgotPasswordRequestRemoved() {
        return forgotPasswordRequestRemoved;
    }

    public void setForgotPasswordRequestRemoved() {
        forgotPasswordRequestRemoved = new Date();
    }

    public Date getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged() {
        passwordChanged = new Date();
    }

    public String getForgotPasswordUuid() {
        return forgotPasswordUuid;
    }

    public void setForgotPasswordUuid(String forgotPasswordUuid) {
        this.forgotPasswordUuid = forgotPasswordUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbUserHistory that = (DbUserHistory) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
