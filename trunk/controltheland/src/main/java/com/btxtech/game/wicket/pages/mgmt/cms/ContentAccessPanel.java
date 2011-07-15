package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbContent;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Arrays;

/**
 * User: beat
 * Date: 12.07.2011
 * Time: 10:40:36
 */
public class ContentAccessPanel extends Panel {
    public ContentAccessPanel(String id, boolean showRead, boolean showWrite, boolean showCreate, boolean showDelete) {
        super(id);
        addSelectField("readRestricted", showRead);
        addSelectField("writeRestricted", showWrite);
        addSelectField("createRestricted", showCreate);
        addSelectField("deleteRestricted", showDelete);
    }

    private void addSelectField(String id, final boolean visible) {
        add(new DropDownChoice<DbContent.Access>(id, Arrays.asList(DbContent.Access.values()), new IChoiceRenderer<DbContent.Access>() {

            @Override
            public Object getDisplayValue(DbContent.Access pluginEnum) {
                return pluginEnum.name();
            }

            @Override
            public String getIdValue(DbContent.Access pluginEnum, int index) {
                return pluginEnum.name();
            }
        }) {
            @Override
            public boolean isVisible() {
                return visible;
            }
        });
    }
}
