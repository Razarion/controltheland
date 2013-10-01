package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.item.itemType.DbConsumerType;
import com.btxtech.game.services.item.itemType.DbHouseType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class HouseTypeEditor extends AbstractAbilityContainer<DbHouseType> {

    public HouseTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbHouseType createAbility() {
        return new DbHouseType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("space"));
    }

}
