package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.territory.DbTerritory;
import com.btxtech.game.services.territory.TerritoryService;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 20:58:03
 */
public class TerritoryPanel extends Panel {
    @SpringBean
    private TerritoryService territoryService;

    public TerritoryPanel(String id) {
        super(id);
        add(new TextField<Integer>("territoryId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbTerritory dbTerritory = (DbTerritory) getDefaultModelObject();
                if (dbTerritory != null) {
                    return dbTerritory.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbTerritory dbTerritory = territoryService.getDbTerritoryCrudServiceHelper().readDbChild(integer);
                        setDefaultModelObject(dbTerritory);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, TerritoryPanel.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }
}
