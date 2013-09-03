package com.btxtech.game.services.common;

/**
 * User: beat
 * Date: 04.06.13
 * Time: 18:23
 */
public enum PropertyServiceEnum {
    GUILD_RAZARION_COST("Razarion cost for creating a guild", Integer.class, null),
    FACEBOOK_OPTIONAL_AD_URL_KEY("Facebook optional ad key", String.class, null),
    REGISTER_DIALOG_DELAY("Register dialog delay in milliseconds", Integer.class, 240000);

    private String displayName;
    private Class type;
    private Object fallbackValue;

    private <T>PropertyServiceEnum(String displayName, Class<T> type, T fallbackValue) {
        this.displayName = displayName;
        this.type = type;
        this.fallbackValue = fallbackValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class getType() {
        return type;
    }

    public Object getFallbackValue() {
        return fallbackValue;
    }

    @Override
    public String toString() {
        return "PropertyServiceEnum{" +
                "displayName='" + displayName + '\'' +
                ", type=" + type +
                ", fallbackValue=" + fallbackValue +
                '}';
    }
}
