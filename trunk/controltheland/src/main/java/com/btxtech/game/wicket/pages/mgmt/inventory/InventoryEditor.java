package com.btxtech.game.wicket.pages.mgmt.inventory;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.inventory.impl.DbInventoryNewUser;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.InventoryArtifactPanel;
import com.btxtech.game.wicket.uiservices.InventoryImageResource;
import com.btxtech.game.wicket.uiservices.InventoryItemPanel;
import com.btxtech.game.wicket.uiservices.ItemTypesReadonlyPanel;
import com.btxtech.game.wicket.uiservices.PlanetsReadonlyPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 05.06.2011
 * Time: 18:38:58
 */
public class InventoryEditor extends MgmtWebPage {
    @SpringBean
    private GlobalInventoryService globalInventoryService;

    public InventoryEditor() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        new CrudRootTableHelper<DbInventoryArtifact>("artifacts", "saveArtifacts", "createArtifact", false, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbInventoryArtifact> dbInventoryArtifactItem) {
                displayId(dbInventoryArtifactItem);
                dbInventoryArtifactItem.add(new TextField("razarionCoast"));
                dbInventoryArtifactItem.add(InventoryImageResource.createArtifactImage("image", dbInventoryArtifactItem.getModelObject()));
                super.extendedPopulateItem(dbInventoryArtifactItem);
                dbInventoryArtifactItem.add(new DropDownChoice<>("rareness", Arrays.asList(DbInventoryArtifact.Rareness.values())));
                dbInventoryArtifactItem.add(new PlanetsReadonlyPanel("planets"));
                dbInventoryArtifactItem.add(new ItemTypesReadonlyPanel("baseItemTypes"));
                dbInventoryArtifactItem.add(new FileUploadField("upload", new IModel<FileUpload>() {

                    @Override
                    public FileUpload getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(FileUpload fileUpload) {
                        if (fileUpload == null) {
                            // Don't know why...
                            return;
                        }
                        DbInventoryArtifact dbInventoryArtifact = dbInventoryArtifactItem.getModelObject();
                        dbInventoryArtifact.setImageContentType(fileUpload.getContentType());
                        dbInventoryArtifact.setImageData(fileUpload.getBytes());
                    }

                    @Override
                    public void detach() {
                    }
                }));
                // alternating row color
                dbInventoryArtifactItem.add(new AttributeModifier("class", true, new Model<>(dbInventoryArtifactItem.getIndex() % 2 == 0 ? "even" : "odd")));
            }

            @Override
            protected CrudRootServiceHelper<DbInventoryArtifact> getCrudRootServiceHelperImpl() {
                return globalInventoryService.getArtifactCrud();
            }
        };

        new CrudRootTableHelper<DbInventoryItem>("items", "saveItems", "createItem", true, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbInventoryItem> dbInventoryItemItem) {
                displayId(dbInventoryItemItem);
                dbInventoryItemItem.add(new TextField("razarionCoast"));
                dbInventoryItemItem.add(InventoryImageResource.createItemImage("image", dbInventoryItemItem.getModelObject()));
                super.extendedPopulateItem(dbInventoryItemItem);
                dbInventoryItemItem.add(new Label("razarionCostViaArtifacts"));
                dbInventoryItemItem.add(new PlanetsReadonlyPanel("planetsViaArtifact"));
                dbInventoryItemItem.add(new ItemTypesReadonlyPanel("baseItemTypesViaArtifact"));
                dbInventoryItemItem.add(new PlanetsReadonlyPanel("planets"));
                dbInventoryItemItem.add(new ItemTypesReadonlyPanel("baseItemTypes"));
                dbInventoryItemItem.add(new FileUploadField("upload", new IModel<FileUpload>() {

                    @Override
                    public FileUpload getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(FileUpload fileUpload) {
                        if (fileUpload == null) {
                            // Don't know why...
                            return;
                        }
                        DbInventoryItem dbInventoryItem = dbInventoryItemItem.getModelObject();
                        dbInventoryItem.setImageContentType(fileUpload.getContentType());
                        dbInventoryItem.setImageData(fileUpload.getBytes());
                    }

                    @Override
                    public void detach() {
                    }
                }));
                // alternating row color
                dbInventoryItemItem.add(new AttributeModifier("class", true, new Model<>(dbInventoryItemItem.getIndex() % 2 == 0 ? "even" : "odd")));
            }

            @Override
            protected CrudRootServiceHelper<DbInventoryItem> getCrudRootServiceHelperImpl() {
                return globalInventoryService.getItemCrud();
            }

            @Override
            protected void onEditSubmit(DbInventoryItem dbInventoryItem) {
                setResponsePage(new InventoryItemEditor(dbInventoryItem));
            }
        };

        new CrudRootTableHelper<DbInventoryNewUser>("newUserItems", "saveNewUserItems", "createNewUserItem", false, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbInventoryNewUser> dbInventoryNewUserItem) {
                dbInventoryNewUserItem.add(new TextField("razarion"));
                dbInventoryNewUserItem.add(new TextField("count"));
                dbInventoryNewUserItem.add(new InventoryItemPanel("dbInventoryItem"));
                dbInventoryNewUserItem.add(new InventoryArtifactPanel("dbInventoryArtifact"));
            }

            @Override
            protected CrudRootServiceHelper<DbInventoryNewUser> getCrudRootServiceHelperImpl() {
                return globalInventoryService.getNewUserCrud();
            }
        };
    }
}