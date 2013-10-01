package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Arrays;

/**
 * User: beat
 * Date: 12.07.2011
 * Time: 10:40:36
 */
public class ContentCommonPanel extends Panel {
    public ContentCommonPanel(String id, boolean showRead, boolean showWrite, boolean showCreate, boolean showDelete) {
        super(id);
        add(createSelectField("readRestricted", showRead));
        add(createSelectField("writeRestricted", showWrite));
        add(createSelectField("createRestricted", showCreate));
        add(createSelectField("deleteRestricted", showDelete));
        add(new TextField("cssClass"));
        add(new TextField("borderCss"));
        add(new TextField("aboveBorderCss"));
    }

    public static DropDownChoice createSelectField(String id) {
        return (createSelectField(id, true));
    }

    public static DropDownChoice createSelectField(String id, final boolean visible) {
        return new DropDownChoice<DbContent.Access>(id, Arrays.asList(DbContent.Access.values()), new IChoiceRenderer<DbContent.Access>() {

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
        };
    }
}
