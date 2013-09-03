package com.btxtech.game.wicket.pages.cms.content.plugin.emailverification;

import com.btxtech.game.services.user.User;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 13:19
 */
public class ConversionTrackingPanel extends Panel {
    public ConversionTrackingPanel(String id, User user) {
        super(id);
        // TODO insert conversion tracking here
        // PackagedTextTemplate jsTemplate = new PackagedTextTemplate(EmailVerificationPanel.class, ...);
        // Map<String, Object> parameters = new HashMap<>();
        // parameters.put("REFERENCE", user.getId());
        // add(new Label(..., new Model<>(jsTemplate.asString(parameters))).setEscapeModelStrings(false));
        // add(new ExternalImage(..., ...)));
    }
}
