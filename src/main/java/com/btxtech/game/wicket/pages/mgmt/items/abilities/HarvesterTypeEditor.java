package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.wicket.uiservices.ClipPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class HarvesterTypeEditor extends AbstractAbilityContainer<DbHarvesterType> {

    public HarvesterTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbHarvesterType createAbility() {
        return new DbHarvesterType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("range"));
        abilityContainer.add(new TextField("progress"));
    }

}
