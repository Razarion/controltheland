package com.btxtech.game.wicket.pages.cms.content.plugin;

import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.pages.cms.content.plugin.changepassword.ChangePassword;
import com.btxtech.game.wicket.pages.cms.content.plugin.changepassword.ForgotPassword;
import com.btxtech.game.wicket.pages.cms.content.plugin.emailverification.EmailVerificationPanel;
import com.btxtech.game.wicket.pages.cms.content.plugin.login.LoginBox;
import com.btxtech.game.wicket.pages.cms.content.plugin.login.LoginFailed;
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
        public Component createComponent(String componentId, ContentContext contentContext) {
            return new LoginBox(componentId, false);
        }
    },
    REGISTER("Register") {
        @Override
        public Component createComponent(String componentId, ContentContext contentContext) {
            return new Register(componentId);
        }
    },
    @Deprecated
    NICK_NAME("Nick name") {
        @Override
        public Component createComponent(String componentId, ContentContext contentContext) {
            return null;
        }
    },
    EMAIL_VERIFICATION("Email verification") {
        @Override
        public Component createComponent(String componentId, ContentContext contentContext) {
            return new EmailVerificationPanel(componentId, contentContext);
        }
    },
    FORGOT_PASSWORD_REQUEST("Forgot password") {
        @Override
        public Component createComponent(String componentId, ContentContext contentContext) {
            return new ForgotPassword(componentId);
        }
    },
    FORGOT_PASSWORD_CHANGE("Change password") {
        @Override
        public Component createComponent(String componentId, ContentContext contentContext) {
            return new ChangePassword(componentId, contentContext);
        }
    },
    LOGIN_FAILED("Login failed") {
        @Override
        public Component createComponent(String componentId, ContentContext contentContext) {
            return new LoginFailed(componentId, contentContext);
        }
    };

    private String displayName;

    PluginEnum(String displayName) {
        this.displayName = displayName;
    }

    public abstract Component createComponent(String componentId, ContentContext contentContext);

    public String getDisplayName() {
        return displayName;
    }
}
