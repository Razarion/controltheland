package com.btxtech.game.wicket.pages.mgmt.tracking;

import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.GameHistoryFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Arrays;

/**
 * User: beat
 * Date: 14.04.13
 * Time: 11:57
 */
public class GameHistoryFilterView extends Panel {
    private static final int TYPES_PER_ROW = 5;

    public GameHistoryFilterView(String id) {
        super(id);
        add(new CheckBox("showCommands"));
        GridView gridView = new GridView<DbHistoryElement.Type>("types", new ListDataProvider<>(Arrays.asList(DbHistoryElement.Type.values()))) {
            @Override
            protected void populateEmptyItem(Item<DbHistoryElement.Type> listItem) {
                listItem.add(new CheckBox("typeCheck").setVisible(false));
                listItem.add(new Label("typeCheckName").setVisible(false));
            }

            @Override
            protected void populateItem(final Item<DbHistoryElement.Type> listItem) {
                listItem.add(new CheckBox("typeCheck", new IModel<Boolean>() {

                    @Override
                    public Boolean getObject() {
                        return ((GameHistoryFilter) GameHistoryFilterView.this.getParent().getDefaultModelObject()).isType(listItem.getModelObject());
                    }

                    @Override
                    public void setObject(Boolean enabled) {
                        ((GameHistoryFilter) GameHistoryFilterView.this.getParent().getDefaultModelObject()).setType(listItem.getModelObject(), enabled);
                    }

                    @Override
                    public void detach() {
                    }
                }));
                listItem.add(new Label("typeCheckName", listItem.getModelObject().name()));
            }
        };
        gridView.setColumns(TYPES_PER_ROW);
        // gridView.setRows((int) Math.ceil((double) DbHistoryElement.Type.values().length / (double) TYPES_PER_ROW));
        add(gridView);
    }
}
