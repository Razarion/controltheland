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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity(name = "USER")
public class User implements UserDetails, Serializable {
    @Id
    private String name;
    @Column(name = "passwordHash")
    private String password;
    private String email;
    private boolean accountNonExpired = true;
    private Date registerDate;
    private Date lastLoginDate;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    @ElementCollection
    @CollectionTable(name = "USER_SECURITY_ROLE")
    @Column(name = "role")
    private Set<String> roles;

    public String getUsername() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
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
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl(SecurityRoles.ROLE_USER));
        if (roles != null) {
            for (String role : roles) {
                grantedAuthorities.add(new GrantedAuthorityImpl(role));
            }
        }
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "User: '" + name + "'";
    }
}
