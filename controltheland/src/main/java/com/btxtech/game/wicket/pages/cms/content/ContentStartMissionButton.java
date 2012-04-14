package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContentStartMissionButton;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

import java.lang.reflect.InvocationTargetException;

/**
 * User: beat
 * Date: 25.07.2011
 * Time: 14:01:19
 */
public class ContentStartMissionButton extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;

    public ContentStartMissionButton(String id, DbContentStartMissionButton dbContentStartMissionButton, Object bean) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        super(id);
        DbLevelTask dbLevelTask = (DbLevelTask) PropertyUtils.getProperty(bean, dbContentStartMissionButton.getExpression());
        boolean isDone = (Boolean) PropertyUtils.getProperty(bean, dbContentStartMissionButton.getDoneExpression());

        BookmarkablePageLink<Game> link = new BookmarkablePageLink<Game>("link", Game.class);
        Image doneImage;
        if (isDone) {
            link.setVisible(false);
            doneImage = new Image("doneImage", new ResourceReference(CmsImageResource.CMS_SHARED_IMAGE_RESOURCES), new ValueMap(CmsImageResource.ID + "=" + Integer.toString(dbContentStartMissionButton.getDoneImage().getId())));
        } else {
            link.setParameter(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, Integer.toString(dbLevelTask.getId()));
            doneImage = new Image("doneImage");
            doneImage.setVisible(false);
            link.add(new Image("linkImage", new ResourceReference(CmsImageResource.CMS_SHARED_IMAGE_RESOURCES), new ValueMap(CmsImageResource.ID + "=" + Integer.toString(dbContentStartMissionButton.getStartImage().getId()))));
        }
        add(link);
        add(doneImage);
    }
}
