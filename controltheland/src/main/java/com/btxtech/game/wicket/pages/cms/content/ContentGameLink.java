package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContentGameLink;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
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
    @SpringBean
    private UserGuidanceService userGuidanceService;
    private int contentId;

    public ContentGameLink(String id, DbContentGameLink dbContentGameLink) {
        super(id);
        contentId = dbContentGameLink.getId();
        BookmarkablePageLink<Game> pageLink = new BookmarkablePageLink<>("link", Game.class);
        if (dbContentGameLink.getDbCmsImage() != null) {
            pageLink.add(new Label("label", "").setVisible(false));
            pageLink.add(CmsImageResource.createImage("image", dbContentGameLink.getDbCmsImage()));
        } else {
            pageLink.add(new Label("label", dbContentGameLink.getDbI18nName().getString(getLocale())));
            pageLink.add(new Image("image", "").setVisible(false));
        }
        if (!userGuidanceService.isStartRealGame()) {
            pageLink.getPageParameters().set(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, userGuidanceService.getDefaultLevelTaskId());
        }

        add(pageLink);
        if (dbContentGameLink.getCssClass() != null) {
            pageLink.add(new AttributeModifier("class", dbContentGameLink.getCssClass()));
        }
    }


    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
