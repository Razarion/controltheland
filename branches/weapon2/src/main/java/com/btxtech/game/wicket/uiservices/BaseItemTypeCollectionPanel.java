package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.wicket.pages.mgmt.items.ItemsUtil;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collection;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 14.03.2011
 * Time: 20:33:57
 */
public class BaseItemTypeCollectionPanel extends Panel {
    @SpringBean
    private ServerItemTypeService serverItemTypeService;

    public BaseItemTypeCollectionPanel(String id) {
        this(id, null);
    }

    public BaseItemTypeCollectionPanel(String id, IModel<Collection<DbBaseItemType>> model) {
        super(id, model);
        add(new TextField<>("baseItemTypeIds", new IModel<String>() {

            @Override
            public String getObject() {
                Collection<DbBaseItemType> dbBaseItemTypes = (Collection<DbBaseItemType>) getDefaultModelObject();
                return ItemsUtil.itemTypesToString(dbBaseItemTypes);
            }

            @Override
            public void setObject(String itemIdString) {
                Collection<DbBaseItemType> dbBaseItemTypes = (Collection<DbBaseItemType>) getDefaultModelObject();
                dbBaseItemTypes.clear();
                if (itemIdString != null) {
                    StringTokenizer st = new StringTokenizer(itemIdString, ItemsUtil.DELIMITER);
                    while (st.hasMoreTokens()) {
                        int id = Integer.parseInt(st.nextToken());
                        dbBaseItemTypes.add((DbBaseItemType) serverItemTypeService.getDbItemType(id));
                    }
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
    }
}
