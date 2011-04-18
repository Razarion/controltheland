package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.territory.DbTerritory;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.utg.DbResurrection;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 19.04.2011
 * Time: 20:58:03
 */
public class ResurrectionPanel extends Panel {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public ResurrectionPanel(String id) {
        super(id);
        add(new TextField<Integer>("resurrectionId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbResurrection dbResurrection = (DbResurrection) getDefaultModelObject();
                if (dbResurrection != null) {
                    return dbResurrection.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbResurrection dbResurrection = userGuidanceService.getCrudRootDbResurrection().readDbChild(integer);
                        setDefaultModelObject(dbResurrection);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, ResurrectionPanel.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }
}
