package com.btxtech.game.services.common;

/**
 * User: beat
 * Date: 04.06.13
 * Time: 18:23
 */
public enum PropertyServiceEnum {
    GUILD_RAZARION_COST("Razarion cost for creating a guild", Integer.class),
    FACEBOOK_OPTIONAL_AD_URL_KEY("Facebook optional ad key", String.class);

    private String displayName;
    private Class type;

    private PropertyServiceEnum(String displayName, Class type) {
        this.displayName = displayName;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PropertyServiceEnum{" +
                "displayName='" + displayName + '\'' +
                ", type=" + type +
                '}';
    }
}
