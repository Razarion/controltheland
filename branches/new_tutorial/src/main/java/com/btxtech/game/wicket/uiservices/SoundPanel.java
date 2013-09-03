package com.btxtech.game.wicket.uiservices;

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
public class SoundPanel extends Panel {
    @SpringBean
    private SoundService soundService;

    public SoundPanel(String id) {
        super(id);
        add(new TextField<>("soundId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbSound sound = (DbSound) getDefaultModelObject();
                if (sound != null) {
                    return sound.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbSound sound = soundService.getSoundLibraryCrud().readDbChild(integer);
                        setDefaultModelObject(sound);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, SoundPanel.this);
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
                DbSound sound = (DbSound) getDefaultModelObject();
                if (sound != null) {
                    return sound.getName();
                } else {
                    return null;
                }
            }
        }));


    }
}
