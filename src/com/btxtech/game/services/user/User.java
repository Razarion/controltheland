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

import com.btxtech.game.services.itemTypeAccess.impl.UserItemTypeAccess;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity(name = "USER")
public class User implements Serializable {
    @Id
    private String name;
    private String password;
    private Date registerDate;
    private Date lastLoginDate;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserItemTypeAccess userItemTypeAccess;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ARQ",
            joinColumns = @JoinColumn(name = "user_name"),
            inverseJoinColumns = @JoinColumn(name = "arq_name")
    )
    private Set<Arq> arqs;

    public String getName() {
        return name;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return name.equals(user.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void setRegisterDate(Date date) {
        registerDate = date;
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

    @Override
    public String toString() {
        return "User: '" + name + "'";
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
}
