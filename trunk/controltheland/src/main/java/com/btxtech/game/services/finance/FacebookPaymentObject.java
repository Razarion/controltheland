package com.btxtech.game.services.finance;

import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import java.util.Date;

/**
 * User: beat
 * Date: 16.02.14
 * Time: 19:46
 */
public class FacebookPaymentObject {
    private String id;
    private User user;
    private Application application;
    private Action[] actions;
    private RefundableAmount refundable_amount;
    private Item[] items;
    private String country;
    private Date created_time;
    private int payout_foreign_exchange_rate;

    public static class User {
        private String name;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class Application {
        private String name;
        private String namespace;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        @Override
        public String toString() {
            return "Application{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", namespace='" + namespace + '\'' +
                    '}';
        }
    }

    public static class Action {
        private String type;
        private String status;
        private String currency;
        private double amount;
        private Date time_created;
        private Date time_updated;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Date getTime_created() {
            return time_created;
        }

        public void setTime_created(Date time_created) {
            this.time_created = time_created;
        }

        public Date getTime_updated() {
            return time_updated;
        }

        public void setTime_updated(Date time_updated) {
            this.time_updated = time_updated;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Action{" +
                    "amount=" + amount +
                    ", type='" + type + '\'' +
                    ", status='" + status + '\'' +
                    ", currency='" + currency + '\'' +
                    ", time_created=" + time_created +
                    ", time_updated=" + time_updated +
                    '}';
        }
    }

    public static class RefundableAmount {
        private String currency;
        private double amount;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        @Override
        public String toString() {
            return "RefundableAmount{" +
                    "amount=" + amount +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }

    public static class Item {
        private String type;
        private String  product;
        private int quantity;

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "product='" + product + '\'' +
                    ", type='" + type + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    public Action[] getActions() {
        return actions;
    }

    public void setActions(Action[] actions) {
        this.actions = actions;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public int getPayout_foreign_exchange_rate() {
        return payout_foreign_exchange_rate;
    }

    public void setPayout_foreign_exchange_rate(int payout_foreign_exchange_rate) {
        this.payout_foreign_exchange_rate = payout_foreign_exchange_rate;
    }

    public RefundableAmount getRefundable_amount() {
        return refundable_amount;
    }

    public void setRefundable_amount(RefundableAmount refundable_amount) {
        this.refundable_amount = refundable_amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "FacebookPaymentObject{" +
                "actions=" + Arrays.toString(actions) +
                ", id='" + id + '\'' +
                ", user=" + user +
                ", application=" + application +
                ", refundable_amount=" + refundable_amount +
                ", items=" + Arrays.toString(items) +
                ", country='" + country + '\'' +
                ", created_time=" + created_time +
                ", payout_foreign_exchange_rate=" + payout_foreign_exchange_rate +
                '}';
    }
}
