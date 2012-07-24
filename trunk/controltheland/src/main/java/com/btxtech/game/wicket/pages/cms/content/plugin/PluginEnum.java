package com.btxtech.game.wicket.pages.cms.content.plugin;

import com.btxtech.game.wicket.pages.cms.content.plugin.login.LoginBox;
import com.btxtech.game.wicket.pages.cms.content.plugin.nickname.ChooseNickname;
import com.btxtech.game.wicket.pages.cms.content.plugin.register.Register;
import org.apache.wicket.Component;

/**
 * User: beat
 * Date: 02.07.2011
 * Time: 11:35:11
 */
public enum PluginEnum {
    LOGIN("Login Box") {
        @Override
        public Component createComponent(String componentId) {
            return new LoginBox(componentId, false);
        }},
    REGISTER("Register") {
        @Override
        public Component createComponent(String componentId) {
            return new Register(componentId);
        }},
    NICK_NAME("Nick name") {
        @Override
        public Component createComponent(String componentId) {
            return new ChooseNickname(componentId);
        }};
    private String displayName;

    PluginEnum(String displayName) {
        this.displayName = displayName;
    }

    public abstract Component createComponent(String componentId);

    public String getDisplayName() {
        return displayName;
    }
}
