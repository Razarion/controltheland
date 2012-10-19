package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.item.itemType.DbLauncherType;
import com.btxtech.game.wicket.uiservices.ClipPanel;
import com.btxtech.game.wicket.uiservices.ProjectileItemTypePanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;

/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class LauncherTypeEditor extends AbstractAbilityContainer<DbLauncherType> {

    public LauncherTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbLauncherType createAbility() {
        return new DbLauncherType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("progress"));
        abilityContainer.add(new ProjectileItemTypePanel("dbProjectileItemType"));
    }

}
