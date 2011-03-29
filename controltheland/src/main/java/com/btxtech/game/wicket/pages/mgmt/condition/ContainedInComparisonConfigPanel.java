package com.btxtech.game.wicket.pages.mgmt.condition;

import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbContainedInComparisonConfig;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 29.03.2011
 * Time: 23:00:28
 */
public class ContainedInComparisonConfigPanel extends Panel {
    public ContainedInComparisonConfigPanel(String id) {
        super(id);
        setDefaultModel(new CompoundPropertyModel<DbContainedInComparisonConfig>(new IModel<DbContainedInComparisonConfig>() {

            @Override
            public DbContainedInComparisonConfig getObject() {
                // TODO Why does not wicket do this?
                DbConditionConfig dbConditionConfig = (DbConditionConfig) getParent().getDefaultModelObject();
                return (DbContainedInComparisonConfig) dbConditionConfig.getDbAbstractComparisonConfig();
            }

            @Override
            public void setObject(DbContainedInComparisonConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
            }
        }));
        add(new CheckBox("isContainedIn"));
    }
}
