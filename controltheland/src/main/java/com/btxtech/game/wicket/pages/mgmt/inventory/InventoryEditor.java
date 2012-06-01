package com.btxtech.game.wicket.pages.mgmt.inventory;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.inventory.DbBoxRegion;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.InventoryService;
import com.btxtech.game.services.inventory.impl.DbInventoryNewUser;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.InventoryArtifactPanel;
import com.btxtech.game.wicket.uiservices.InventoryImageResource;
import com.btxtech.game.wicket.uiservices.InventoryItemPanel;
import com.btxtech.game.wicket.uiservices.MinutePanel;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 05.06.2011
 * Time: 18:38:58
 */
public class InventoryEditor extends MgmtWebPage {
    @SpringBean
    private InventoryService inventoryService;

    public InventoryEditor() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        new CrudRootTableHelper<DbInventoryArtifact>("artifacts", "saveArtifacts", "createArtifact", false, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbInventoryArtifact> dbInventoryArtifactItem) {
                displayId(dbInventoryArtifactItem);
                dbInventoryArtifactItem.add(InventoryImageResource.createArtifactImage("image", dbInventoryArtifactItem.getModelObject()));
                super.extendedPopulateItem(dbInventoryArtifactItem);
                dbInventoryArtifactItem.add(new DropDownChoice<>("rareness", Arrays.asList(DbInventoryArtifact.Rareness.values())));
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

            }

            @Override
            protected CrudRootServiceHelper<DbInventoryArtifact> getCrudRootServiceHelperImpl() {
                return inventoryService.getArtifactCrud();
            }
        };

        new CrudRootTableHelper<DbInventoryItem>("items", "saveItems", "createItem", true, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbInventoryItem> dbInventoryItemItem) {
                displayId(dbInventoryItemItem);
                dbInventoryItemItem.add(InventoryImageResource.createItemImage("image", dbInventoryItemItem.getModelObject()));
                super.extendedPopulateItem(dbInventoryItemItem);
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

            }

            @Override
            protected CrudRootServiceHelper<DbInventoryItem> getCrudRootServiceHelperImpl() {
                return inventoryService.getItemCrud();
            }

            @Override
            protected void onEditSubmit(DbInventoryItem dbInventoryItem) {
                setResponsePage(new InventoryItemEditor(dbInventoryItem));
            }
        };

        new CrudRootTableHelper<DbBoxRegion>("boxRegions", "saveBoxRegions", "createBoxRegion", true, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbBoxRegion> dbBoxRegionItem) {
                displayId(dbBoxRegionItem);
                super.extendedPopulateItem(dbBoxRegionItem);
                dbBoxRegionItem.add(new MinutePanel("minInterval"));
                dbBoxRegionItem.add(new MinutePanel("maxInterval"));
                dbBoxRegionItem.add(new RectanglePanel("region"));
                dbBoxRegionItem.add(new TextField("itemFreeRange"));
            }

            @Override
            protected CrudRootServiceHelper<DbBoxRegion> getCrudRootServiceHelperImpl() {
                return inventoryService.getBoxRegionCrud();
            }

            @Override
            protected void onEditSubmit(DbBoxRegion dbBoxRegion) {
                setResponsePage(new BoxRegionEditor(dbBoxRegion));
            }
        };

        form.add(new Button("activateBoxRegion") {

            @Override
            public void onSubmit() {
                inventoryService.activate();
            }
        });

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
                return inventoryService.getNewUserCrud();
            }
        };
    }
}