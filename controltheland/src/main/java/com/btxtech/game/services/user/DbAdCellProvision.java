package com.btxtech.game.services.user;

import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 12:13
 */
@Entity(name = "AD_CELL_PROVISION")
public class DbAdCellProvision {
    @Id
    @GeneratedValue
    private Integer id;
    private Date date;
    @Index(name = "AD_CELL_PROVISION_PID_INDEX")
    @Column(length = 500)
    private String adCellPid;
    private int userId;

    /**
     * Used by hibernate
     */
    public DbAdCellProvision() {
    }

    public DbAdCellProvision(User user) {
        date = new Date();
        adCellPid = user.getAdCellBid();
        userId = user.getId();
    }

    public Date getDate() {
        return date;
    }

    public String getAdCellPid() {
        return adCellPid;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbAdCellProvision that = (DbAdCellProvision) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
