package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.layout.DbContentActivateQuestButton;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.lang.reflect.InvocationTargetException;

/**
 * User: beat Date: 25.07.2011 Time: 14:01:19
 */
public class ContentActivateQuestButton extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;

    public ContentActivateQuestButton(String id, DbContentActivateQuestButton dbcontentActivateQuestButton, Object bean) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        super(id);
        DbLevelTask dbLevelTask = (DbLevelTask) PropertyUtils.getProperty(bean, dbcontentActivateQuestButton.getExpression());
        boolean isDone = (Boolean) PropertyUtils.getProperty(bean, dbcontentActivateQuestButton.getDoneExpression());

        BookmarkablePageLink<Game> link;
        Image image;
        if (isDone) {
            link = new BookmarkablePageLink<>("link", Game.class);
            link.setVisible(false);
            image = CmsImageResource.createImage("image", dbcontentActivateQuestButton.getDoneImage());
        } else {
            boolean isActive = (Boolean) PropertyUtils.getProperty(bean, dbcontentActivateQuestButton.getActiveExpression());
            boolean isBlocked = (Boolean) PropertyUtils.getProperty(bean, dbcontentActivateQuestButton.getBlockedExpression());
            if (isActive) {
                link = new BookmarkablePageLink<>("link", CmsPage.class, cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.USER_PAGE));
                link.setParameter(CmsPage.QUEST_DEACTIVATE_ID, Integer.toString(dbLevelTask.getId()));
                link.add(CmsImageResource.createImage("linkImage", dbcontentActivateQuestButton.getAbortImage()));
                image = new Image("image");
                image.setVisible(false);
            } else if (isBlocked) {
                link = new BookmarkablePageLink<>("link", Game.class);
                link.setVisible(false);
                image = CmsImageResource.createImage("image", dbcontentActivateQuestButton.getBlockedImage());
            } else {
                link = new BookmarkablePageLink<>("link", CmsPage.class, cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.USER_PAGE));
                link.setParameter(CmsPage.QUEST_ACTIVATE_ID, Integer.toString(dbLevelTask.getId()));
                link.add(CmsImageResource.createImage("linkImage", dbcontentActivateQuestButton.getStartImage()));
                image = new Image("image");
                image.setVisible(false);
            }
        }
        add(link);
        add(image);
    }
}
