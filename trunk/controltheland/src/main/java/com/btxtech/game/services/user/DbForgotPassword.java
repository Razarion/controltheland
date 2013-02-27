package com.btxtech.game.services.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * User: beat
 * Date: 26.02.13
 * Time: 12:56
 */
@Entity(name = "USER_FORGOT_PASSWORD")
public class DbForgotPassword {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(optional = false)
    private User user;
    private String uuid;
    private Date date;

    /**
     * Used by hibernate
     */
    DbForgotPassword() {
    }

    public DbForgotPassword(User user, String uuid) {
        this.user = user;
        this.uuid = uuid;
        date = new Date();
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbForgotPassword that = (DbForgotPassword) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

}
