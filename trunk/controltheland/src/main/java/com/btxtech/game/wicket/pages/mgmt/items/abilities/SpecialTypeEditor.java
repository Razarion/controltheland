package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.services.item.itemType.DbConsumerType;
import com.btxtech.game.services.item.itemType.DbSpecialType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class SpecialTypeEditor extends AbstractAbilityContainer<DbSpecialType> {

    public SpecialTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbSpecialType createAbility() {
        return new DbSpecialType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new DropDownChoice<>("radarMode", RadarMode.getList()));
    }

}
