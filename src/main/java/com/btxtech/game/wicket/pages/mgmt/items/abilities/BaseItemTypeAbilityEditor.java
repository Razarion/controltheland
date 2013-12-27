package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.items.ItemTypeTable;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 14.10.12
 * Time: 18:55
 */
public class BaseItemTypeAbilityEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbBaseItemType> ruServiceHelper;

    public BaseItemTypeAbilityEditor(DbBaseItemType dbBaseItemType) {
        add(new FeedbackPanel("msgs"));

        final Form<DbBaseItemType> form = new Form<>("form", new CompoundPropertyModel<DbBaseItemType>(new RuModel<DbBaseItemType>(dbBaseItemType, DbBaseItemType.class) {
            @Override
            protected RuServiceHelper<DbBaseItemType> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new MovableTypeEditor("dbMovableType"));
        form.add(new WeaponTypeEditor("dbWeaponType"));
        form.add(new LauncherTypeEditor("dbLauncherType"));
        form.add(new FactoryTypeEditor("dbFactoryType"));
        form.add(new HarvesterTypeEditor("dbHarvesterType"));
        form.add(new BuilderTypeEditor("dbBuilderType"));
        form.add(new ConsumerTypeEditor("dbConsumerType"));
        form.add(new GeneratorTypeEditor("dbGeneratorType"));
        form.add(new ItemContainerTypeEditor("dbItemContainerType"));
        form.add(new HouseTypeEditor("dbHouseType"));
        form.add(new SpecialTypeEditor("dbSpecialType"));

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                setResponsePage(ItemTypeTable.class);
            }
        });

    }
}
