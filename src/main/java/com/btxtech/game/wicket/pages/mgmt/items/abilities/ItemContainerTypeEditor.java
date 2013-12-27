package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.item.itemType.DbItemContainerType;
import com.btxtech.game.wicket.uiservices.BaseItemTypeCollectionPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;

import java.util.Arrays;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class ItemContainerTypeEditor extends AbstractAbilityContainer<DbItemContainerType> {

    public ItemContainerTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbItemContainerType createAbility() {
        return new DbItemContainerType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("maxCount"));
        abilityContainer.add(new DropDownChoice<>("operationSurfaceType", Arrays.asList(SurfaceType.values())));
        abilityContainer.add(new TextField("range"));
        abilityContainer.add(new BaseItemTypeCollectionPanel("ableToContain"));
    }

}
