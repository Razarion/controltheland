package com.btxtech.game.services.finance;

import com.btxtech.game.jsre.client.dialogs.crystals.PaymentSource;
import com.btxtech.game.services.user.User;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 18:24
 */
@Entity(name = "PAYMENT_TRANSACTION")
public class DbPaymentTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    private Date date;
    private int userId;
    private String itemNumber;
    private String txnId;
    private String payerEmail;
    @Enumerated(EnumType.STRING)
    private PaymentSource paymentSource;

    /**
     * Used by hibernate
     */
    public DbPaymentTransaction() {
    }

    public DbPaymentTransaction(User user, String itemNumber, String txnId, String payerEmail, PaymentSource paymentSource) {
        this.paymentSource = paymentSource;
        date = new Date();
        this.userId = user.getId();
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

    public int getUserId() {
        return userId;
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

    public PaymentSource getPaymentSource() {
        return paymentSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbPaymentTransaction that = (DbPaymentTransaction) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

}
