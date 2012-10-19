package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.item.itemType.DbConsumerType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.wicket.pages.mgmt.items.abilities.AbstractAbilityContainer;
import com.btxtech.game.wicket.uiservices.BaseItemTypeCollectionPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class ConsumerTypeEditor extends AbstractAbilityContainer<DbConsumerType> {

    public ConsumerTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbConsumerType createAbility() {
        return new DbConsumerType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("wattage"));
    }

}
