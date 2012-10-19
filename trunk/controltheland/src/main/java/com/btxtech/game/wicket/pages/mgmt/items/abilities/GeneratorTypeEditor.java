package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.item.itemType.DbConsumerType;
import com.btxtech.game.services.item.itemType.DbGeneratorType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class GeneratorTypeEditor extends AbstractAbilityContainer<DbGeneratorType> {

    public GeneratorTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbGeneratorType createAbility() {
        return new DbGeneratorType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("wattage"));
    }

}
