package com.btxtech.game.wicket.pages.mgmt.items.abilities;

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 14.10.12
 * Time: 19:12
 */
public abstract class AbstractAbilityContainer<T> extends Panel {
    @SpringBean
    private RuServiceHelper<DbBaseItemType> ruServiceHelper;

    protected abstract T createAbility();

    protected abstract void setupAbility(WebMarkupContainer abilityContainer);

    public AbstractAbilityContainer(String id) {
        super(id);

        add(new Button("create") {
            @Override
            public void onSubmit() {
                AbstractAbilityContainer.this.setDefaultModelObject(createAbility());
                DbBaseItemType dbBaseItemType = (DbBaseItemType) AbstractAbilityContainer.this.getParent().getDefaultModelObject();
                ruServiceHelper.updateDbEntity(dbBaseItemType);
            }

            @Override
            public boolean isVisible() {
                return AbstractAbilityContainer.this.getDefaultModelObject() == null;
            }
        });
        add(new Button("delete") {
            @Override
            public void onSubmit() {
                AbstractAbilityContainer.this.setDefaultModelObject(null);
                DbBaseItemType dbBaseItemType = (DbBaseItemType) AbstractAbilityContainer.this.getParent().getDefaultModelObject();
                ruServiceHelper.updateDbEntity(dbBaseItemType);
            }

            @Override
            public boolean isVisible() {
                return AbstractAbilityContainer.this.getDefaultModelObject() != null;
            }
        });
        WebMarkupContainer abilityContainer = new WebMarkupContainer("ability") {
            @Override
            public boolean isVisible() {
                return getDefaultModelObject() != null;
            }
        };
        abilityContainer.setDefaultModel(new CompoundPropertyModel<>(new PropertyModel<>(new AbstractReadOnlyModel<Object>() {
            @Override
            public Object getObject() {
                return AbstractAbilityContainer.this.getParent().getDefaultModel();
            }
        }, id)));

        setupAbility(abilityContainer);
        add(abilityContainer);
    }

    protected RuServiceHelper<DbBaseItemType> getRuServiceHelper() {
        return ruServiceHelper;
    }
}
