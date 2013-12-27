package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.item.itemType.DbBuilderType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.wicket.uiservices.BaseItemTypeCollectionPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class BuilderTypeEditor extends AbstractAbilityContainer<DbBuilderType> {

    public BuilderTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbBuilderType createAbility() {
        return new DbBuilderType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("progress"));
        abilityContainer.add(new TextField("range"));
        abilityContainer.add(new BaseItemTypeCollectionPanel("ableToBuild"));
    }

}
