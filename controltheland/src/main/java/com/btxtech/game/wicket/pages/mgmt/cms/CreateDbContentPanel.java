package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.Arrays;

/**
 * User: beat
 * Date: 20.06.2011
 * Time: 12:55:46
 */
public abstract class CreateDbContentPanel extends Panel {
    private ContentEditorFactory.DbContentEnum choice;

    public CreateDbContentPanel(String id) {
        super(id);
        add(new DropDownChoice<ContentEditorFactory.DbContentEnum>("dropDown", new IModel<ContentEditorFactory.DbContentEnum>() {
            @Override
            public ContentEditorFactory.DbContentEnum getObject() {
                return choice;
            }

            @Override
            public void setObject(ContentEditorFactory.DbContentEnum object) {
                choice = object;
            }

            @Override
            public void detach() {
            }
        }, Arrays.asList(ContentEditorFactory.DbContentEnum.values()), new IChoiceRenderer<ContentEditorFactory.DbContentEnum>() {

            @Override
            public Object getDisplayValue(ContentEditorFactory.DbContentEnum dbContentEnum) {
                return dbContentEnum.getDisplayName();
            }

            @Override
            public String getIdValue(ContentEditorFactory.DbContentEnum dbContentEnum, int index) {
                return dbContentEnum.name();
            }
        }));
        add(new Button("createButton") {

            @Override
            public void onSubmit() {
                if (choice != null) {
                    onDbContentSelected(choice.getCreateClass());
                } else {
                    error("Please select a Content to be created");
                }
            }
        });
    }

    public abstract void onDbContentSelected(Class<? extends DbContent> selected);
}
