package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.layout.DbContentGameLink;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 00:17:38
 */
public class ContentGameLink extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;

    public ContentGameLink(String id, DbContentGameLink dbContentGameLink) {
        super(id);
        contentId = dbContentGameLink.getId();
        BookmarkablePageLink<Game> pageLink = new BookmarkablePageLink<Game>("link", Game.class);
        if (dbContentGameLink.getDbCmsImage() != null) {
            pageLink.add(new Label("label", "").setVisible(false));
            pageLink.add(CmsImageResource.createImage("image", dbContentGameLink.getDbCmsImage()));
        } else {
            pageLink.add(new Label("label", dbContentGameLink.getLinkText()));
            pageLink.add(new Image("image").setVisible(false));
        }
        pageLink.add(new SimpleAttributeModifier("target", CmsUtil.TARGET_GAME));
        add(pageLink);
        if (dbContentGameLink.getCssClass() != null) {
            add(new SimpleAttributeModifier("class", dbContentGameLink.getCssClass()));
        }
    }


    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
