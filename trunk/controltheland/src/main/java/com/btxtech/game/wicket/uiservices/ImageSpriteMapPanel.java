package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbClip;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.media.SoundService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.10.2011
 * Time: 16:51:55
 */
public class ImageSpriteMapPanel extends Panel {
    @SpringBean
    private ClipService clipService;

    public ImageSpriteMapPanel(String id) {
        super(id);
        add(new TextField<>("imageSpriteMapId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbImageSpriteMap dbImageSpriteMap = (DbImageSpriteMap) getDefaultModelObject();
                if (dbImageSpriteMap != null) {
                    return dbImageSpriteMap.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbImageSpriteMap dbImageSpriteMap = clipService.getImageSpriteMapCrud().readDbChild(integer);
                        setDefaultModelObject(dbImageSpriteMap);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, ImageSpriteMapPanel.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new Label("name", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                DbImageSpriteMap dbImageSpriteMap = (DbImageSpriteMap) getDefaultModelObject();
                if (dbImageSpriteMap != null) {
                    return dbImageSpriteMap.getName();
                } else {
                    return null;
                }
            }
        }));


    }
}
