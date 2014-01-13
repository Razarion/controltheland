package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.item.itemType.DbWeaponTypeItemTypeFactor;
import com.btxtech.game.wicket.uiservices.BaseItemTypeCollectionPanel;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.ClipPanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;


/**
 * User: beat
 * Date: 15.10.12
 * Time: 00:59
 */
public class WeaponTypeEditor extends AbstractAbilityContainer<DbWeaponType> {
    @SpringBean
    private RuServiceHelper<DbWeaponType> ruServiceHelper;

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
        abilityContainer.add(new BaseItemTypeCollectionPanel("disallowedItemTypes"));
        new CrudChildTableHelper<DbWeaponType, DbWeaponTypeItemTypeFactor>("factors", null, "createFactor", false, abilityContainer, false) {
            @Override
            protected RuServiceHelper<DbWeaponType> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbWeaponType getParent() {
                return ((DbBaseItemType) getDefaultModelObject()).getDbWeaponType();
            }

            @Override
            protected CrudChildServiceHelper<DbWeaponTypeItemTypeFactor> getCrudChildServiceHelperImpl() {
                return getParent().getFactorCrud();
            }

            @Override
            protected void extendedPopulateItem(Item<DbWeaponTypeItemTypeFactor> item) {
                item.add(new BaseItemTypePanel("dbBaseItemType"));
                item.add(new TextField("factor"));
            }
        };
    }
}
