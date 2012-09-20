package com.btxtech.game.wicket.pages.mgmt.items;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.wicket.pages.mgmt.ItemTypeImageEditor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 10.08.12
 * Time: 16:05
 */
public class ItemTypeImagePanel extends Panel {
    @SpringBean
    private ServerItemTypeService serverItemTypeService;

    public ItemTypeImagePanel(String id, final int itemTypeId) {
        super(id);
        add(new Label("imageSize", new AbstractReadOnlyModel<Double>() {
            @Override
            public Double getObject() {
                try {
                    return serverItemTypeService.getItemTypeSpriteMap(itemTypeId).getData().length / 1000.0;
                } catch (Exception e) {
                    return 0.0;
                }
            }
        }));
        add(new ExternalLink("viewSpriteMapLink", ImageHandler.getItemTypeSpriteMapUrl(itemTypeId)));

        add(new Button("editImages") {
            @Override
            public void onSubmit() {
                setResponsePage(new ItemTypeImageEditor(itemTypeId));
            }
        });

    }
}
