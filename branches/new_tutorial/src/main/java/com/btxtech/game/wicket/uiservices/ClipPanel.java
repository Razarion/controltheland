package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbClip;
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
public class ClipPanel extends Panel {
    @SpringBean
    private ClipService clipService;

    public ClipPanel(String id) {
        super(id);
        add(new TextField<>("clipId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbClip dbClip = (DbClip) getDefaultModelObject();
                if (dbClip != null) {
                    return dbClip.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbClip dbClip = clipService.getClipLibraryCrud().readDbChild(integer);
                        setDefaultModelObject(dbClip);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, ClipPanel.this);
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
                DbClip dbClip = (DbClip) getDefaultModelObject();
                if (dbClip != null) {
                    return dbClip.getName();
                } else {
                    return null;
                }
            }
        }));


    }
}
