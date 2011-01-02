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

package com.btxtech.game.services.user;

import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.utg.UserLevelStatus;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity(name = "USER")
public class User implements Serializable {
    @Id
    private String name;
    private String password;
    private String email;
    private Date registerDate;
    private Date lastLoginDate;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ARQ",
            joinColumns = @JoinColumn(name = "user_name"),
            inverseJoinColumns = @JoinColumn(name = "arq_name")
    )
    private Set<Arq> arqs;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserItemTypeAccess userItemTypeAccess;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserLevelStatus userLevelStatus;
    @Transient
    private boolean loggedIn;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public UserLevelStatus getUserLevelStatus() {
        return userLevelStatus;
    }

    public void setUserLevelStatus(UserLevelStatus userLevelStatus) {
        this.userLevelStatus = userLevelStatus;
    }

    public void registerUser(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        registerDate = new Date();
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setLastLoginDate(Date date) {
        lastLoginDate = date;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public UserItemTypeAccess getUserItemTypeAccess() {
        return userItemTypeAccess;
    }

    public void setUserItemTypeAccess(UserItemTypeAccess userItemTypeAccess) {
        this.userItemTypeAccess = userItemTypeAccess;
    }

    public boolean hasArq(Arq arq) {
        return arqs != null && arqs.contains(arq);
    }

    public void addArq(Arq arq) {
        if (arqs == null) {
            arqs = new HashSet<Arq>();
        }
        arqs.add(arq);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return name != null && name.equals(user.name);
    }

    @Override
    public int hashCode() {
        if (name != null) {
            return name.hashCode();
        } else {
            return System.identityHashCode(this);
        }
    }

    @Override
    public String toString() {
        return "User: '" + name + "'";
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
