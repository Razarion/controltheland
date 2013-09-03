package com.btxtech.game.wicket.pages.mgmt.items.abilities;

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
public class WeaponTypeEditor extends AbstractAbilityContainer<DbWeaponType> {

    public WeaponTypeEditor(String id) {
        super(id);
    }

    @Override
    protected DbWeaponType createAbility() {
        return new DbWeaponType();
    }

    @Override
    protected void setupAbility(WebMarkupContainer abilityContainer) {
        abilityContainer.add(new TextField("damage"));
        abilityContainer.add(new TextField("range"));
        abilityContainer.add(new TextField("reloadTime"));
        abilityContainer.add(new ClipPanel("muzzleFlashClip"));
        abilityContainer.add(new ClipPanel("projectileClip"));
        abilityContainer.add(new TextField("projectileSpeed"));
        abilityContainer.add(new ClipPanel("projectileDetonationClip"));
    }

}
