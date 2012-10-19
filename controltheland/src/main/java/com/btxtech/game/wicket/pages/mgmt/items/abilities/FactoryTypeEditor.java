package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.wicket.uiservices.BaseItemTypeCollectionPanel;
import com.btxtech.game.wicket.uiservices.ClipPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class FactoryTypeEditor extends AbstractAbilityContainer<DbFactoryType> {

    public FactoryTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbFactoryType createAbility() {
        return new DbFactoryType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("progress"));
        abilityContainer.add(new BaseItemTypeCollectionPanel("ableToBuild"));
    }

}
