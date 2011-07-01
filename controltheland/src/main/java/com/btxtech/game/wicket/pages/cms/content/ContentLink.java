package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DbContentLink;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 00:17:38
 */
public class ContentLink extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;

    public ContentLink(String id, DbContentLink dbContentLink) {
        super(id);
        contentId = dbContentLink.getId();
        ExternalLink externalLink = new ExternalLink("link", dbContentLink.getUrl());
        if (dbContentLink.getDbCmsImage() != null) {
            externalLink.add(new Label("label", "").setVisible(false));
            externalLink.add(CmsImageResource.createImage("image", dbContentLink.getDbCmsImage()));
        } else {
            externalLink.add(new Label("label", dbContentLink.getName()));
            externalLink.add(new Image("image").setVisible(false));
        }
        add(externalLink);
        if (dbContentLink.getCssClass() != null) {
            add(new SimpleAttributeModifier("class", dbContentLink.getCssClass()));
        }
    }


    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
