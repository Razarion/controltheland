package com.btxtech.game.wicket.pages.mgmt.items;

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.SoundPanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 14.08.12
 * Time: 15:23
 */
public class ItemTypeSoundEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbItemType> ruServiceHelper;

    public ItemTypeSoundEditor(DbItemType itemType) {
        add(new FeedbackPanel("msgs"));

        final Form<DbItemType> form = new Form<>("form", new CompoundPropertyModel<DbItemType>(new RuModel<DbItemType>(itemType, DbItemType.class) {
            @Override
            protected RuServiceHelper<DbItemType> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new SoundPanel("selectionSound"));
        form.add(new SoundPanel("buildupSound"));
        form.add(new SoundPanel("commandSound"));
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
                setResponsePage(ItemTypeTable.class);
            }
        });
        form.add(new Button("cancel") {
            @Override
            public void onSubmit() {
                setResponsePage(ItemTypeTable.class);
            }
        });

    }
}
