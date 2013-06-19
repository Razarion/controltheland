package com.btxtech.game.jsre.client.common.info;

import java.io.Serializable;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 11:56
 */
public class SimpleUser implements Serializable {
    private String name;
    private int id;
    private boolean verified;
    private boolean facebook;

    /**
     * Used by GWT
     */
    SimpleUser() {
    }

    public SimpleUser(String name, int id, boolean verified, boolean facebook) {
        this.name = name;
        this.id = id;
        this.verified = verified;
        this.facebook = facebook;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isFacebook() {
        return facebook;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleUser that = (SimpleUser) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "SimpleUser{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", verified=" + verified +
                ", facebook=" + facebook +
                '}';
    }
}
