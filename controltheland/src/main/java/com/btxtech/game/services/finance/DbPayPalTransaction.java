package com.btxtech.game.services.finance;

import com.btxtech.game.services.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 18:24
 */
@Entity(name = "PAYPAL_TRANSACTION")
public class DbPayPalTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    private Date date;
    private int user;
    private String itemNumber;
    private String txnId;
    private String payerEmail;

    /**
     * Used by hibernate
     */
    public DbPayPalTransaction() {
    }

    public DbPayPalTransaction(User user, String itemNumber, String txnId, String payerEmail) {
        date = new Date();
        this.user = user.getId();
        this.itemNumber = itemNumber;
        this.txnId = txnId;
        this.payerEmail = payerEmail;
    }

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getUser() {
        return user;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbPayPalTransaction that = (DbPayPalTransaction) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

}
