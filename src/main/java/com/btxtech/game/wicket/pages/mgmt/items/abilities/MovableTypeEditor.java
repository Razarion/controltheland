package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.item.itemType.DbMovableType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class MovableTypeEditor extends AbstractAbilityContainer<DbMovableType> {
    public MovableTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbMovableType createAbility() {
        return new DbMovableType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("speed"));
    }
}
