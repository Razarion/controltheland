package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 20:58:03
 */
public class TutorialPanel extends Panel {
    @SpringBean
    private TutorialService tutorialService;

    public TutorialPanel(String id) {
        super(id);
        add(new TextField<Integer>("tutorialId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbTutorialConfig dbTutorialConfig =  (DbTutorialConfig) getDefaultModelObject();
                if (dbTutorialConfig != null) {
                    return dbTutorialConfig.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(integer);
                        setDefaultModelObject(dbTutorialConfig);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, TutorialPanel.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
    }
}
