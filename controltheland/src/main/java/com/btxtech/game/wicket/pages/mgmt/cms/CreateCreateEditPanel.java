package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentCreateEdit;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 15.07.2011
 * Time: 11:20:15
 */
public abstract class CreateCreateEditPanel extends Panel {
    public CreateCreateEditPanel(String id) {
        super(id);
        add(new Button("create") {

            @Override
            public void onSubmit() {
                createDbContentCreateEdit();
            }

            @Override
            public boolean isVisible() {
                return CreateCreateEditPanel.this.getDefaultModelObject() == null;
            }
        });
        add(new Button("edit") {

            @Override
            public void onSubmit() {
                setResponsePage(new ContentCreateEditEditor((DbContentCreateEdit) CreateCreateEditPanel.this.getDefaultModelObject()));
            }

            @Override
            public boolean isVisible() {
                return CreateCreateEditPanel.this.getDefaultModelObject() != null;
            }
        });

        add(new Button("delete") {

            @Override
            public void onSubmit() {
                deleteDbContentCreateEdit((DbContentCreateEdit) CreateCreateEditPanel.this.getDefaultModelObject());
            }

            @Override
            public boolean isVisible() {
                return CreateCreateEditPanel.this.getDefaultModelObject() != null;
            }
        });

    }

    protected abstract void createDbContentCreateEdit();

    protected abstract void deleteDbContentCreateEdit(DbContentCreateEdit dbContentCreateEdit);
}
