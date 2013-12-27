package com.btxtech.game.services.common.db;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 05.06.13
 * Time: 01:04
 */
@Entity(name = "PROPERTY")
public class DbProperty implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(EnumType.STRING)
    private PropertyServiceEnum propertyServiceEnum;
    private Integer integerValue;
    private String stringValue;

    /**
     * Used by hibernate
     */
    public DbProperty() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return propertyServiceEnum.getDisplayName();
    }

    public PropertyServiceEnum getPropertyServiceEnum() {
        return propertyServiceEnum;
    }

    public void setPropertyServiceEnum(PropertyServiceEnum propertyServiceEnum) {
        this.propertyServiceEnum = propertyServiceEnum;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(UserService userService) {
        // Unused
    }

    @Override
    public void setParent(Object o) {
        // Unused
    }

    @Override
    public Object getParent() {
        return null;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getValueAsString() {
        if (integerValue != null) {
            return integerValue.toString();
        } else if (stringValue != null) {
            return stringValue;
        } else {
            return "";
        }
    }

    public void setValueAsString(String string) {
        if (propertyServiceEnum == null) {
            throw new IllegalStateException("propertyServiceEnum is not set");
        }
        if (Integer.class.isAssignableFrom(propertyServiceEnum.getType())) {
            integerValue = Integer.parseInt(string);
        } else if (String.class.isAssignableFrom(propertyServiceEnum.getType())) {
            stringValue = string;
        } else {
            throw new IllegalArgumentException("Unsupported property type: " + propertyServiceEnum);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbProperty)) {
            return false;
        }

        DbProperty that = (DbProperty) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

}
