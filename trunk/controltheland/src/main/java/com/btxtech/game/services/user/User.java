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

import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.utg.DbFacebookSource;
import org.hibernate.annotations.Index;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity(name = "USER")
public class User implements UserDetails, Serializable, CrudParent {
    public enum SocialNet {
        FACEBOOK
    }

    @Id
    @GeneratedValue
    private Integer id;
    @Index(name = "USER_NAME")
    @Column(unique = true)
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
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<DbContentAccessControl> dbContentAccessControls;
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<DbPageAccessControl> dbPageAccessControls;
    @Enumerated(EnumType.STRING)
    private SocialNet socialNet;
    private String socialNetUserId;
    private String socialNetUserLink;
    private Date awaitingVerificationDate;
    private String verificationId;
    @Transient
    private CrudChildServiceHelper<DbContentAccessControl> contentCrud;
    @Transient
    private CrudChildServiceHelper<DbPageAccessControl> pageCrud;
    private Date lastNews;
    @Embedded
    private DbFacebookSource dbFacebookSource;
    @Embedded
    private DbInvitationInfo dbInvitationInfo;

    public Integer getId() {
        return id;
    }

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

    public String getSocialNetUserLink() {
        return socialNetUserLink;
    }

    public void registerUser(String name, String password, String email, DbInvitationInfo dbInvitationInfo) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.dbInvitationInfo = dbInvitationInfo;
        registerDate = new Date();
        lastNews = registerDate;
    }

    public void registerFacebookUser(FacebookSignedRequest facebookSignedRequest, String nickName, DbFacebookSource dbFacebookSource, DbInvitationInfo dbInvitationInfo) {
        name = nickName;
        this.dbFacebookSource = dbFacebookSource;
        this.dbInvitationInfo = dbInvitationInfo;
        socialNet = SocialNet.FACEBOOK;
        socialNetUserId = facebookSignedRequest.getUserId();
        email = facebookSignedRequest.getEmail();
        socialNetUserLink = facebookSignedRequest.getLink();
        registerDate = new Date();
        lastNews = registerDate;
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        User that = (User) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
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

    public boolean isRegistrationComplete() {
        return isAccountNonLocked();
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

    public SocialNet getSocialNet() {
        return socialNet;
    }

    public String getSocialNetUserId() {
        return socialNetUserId;
    }

    public CrudChildServiceHelper<DbContentAccessControl> getContentCrud() {
        if (dbContentAccessControls == null) {
            dbContentAccessControls = new ArrayList<>();
        }
        if (contentCrud == null) {
            contentCrud = new CrudChildServiceHelper<>(dbContentAccessControls, DbContentAccessControl.class, this);
        }
        return contentCrud;
    }

    public CrudChildServiceHelper<DbPageAccessControl> getPageCrud() {
        if (dbPageAccessControls == null) {
            dbPageAccessControls = new ArrayList<>();
        }
        if (pageCrud == null) {
            pageCrud = new CrudChildServiceHelper<>(dbPageAccessControls, DbPageAccessControl.class, this);
        }
        return pageCrud;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setAwaitingVerification() {
        accountNonLocked = false;
        awaitingVerificationDate = new Date();
        verificationId = UUID.randomUUID().toString().toUpperCase();
    }

    public void setVerified() {
        accountNonLocked = true;
        awaitingVerificationDate = null;
    }

    public boolean isVerified() {
        return awaitingVerificationDate == null;
    }

    public Date getAwaitingVerificationDate() {
        return awaitingVerificationDate;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public SimpleUser createSimpleUser() {
        return new SimpleUser(name, id, accountNonLocked, socialNet == SocialNet.FACEBOOK);
    }

    public Date getLastNews() {
        return lastNews;
    }

    public void updateLastNews() {
        lastNews = new Date();
    }

    public DbFacebookSource getDbFacebookSource() {
        return dbFacebookSource;
    }

    public DbInvitationInfo getDbInvitationInfo() {
        return dbInvitationInfo;
    }

    @Override
    public String toString() {
        return "User: '" + name + "' id: " + id;
    }
}
