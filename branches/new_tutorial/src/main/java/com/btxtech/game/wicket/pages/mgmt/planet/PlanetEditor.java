package com.btxtech.game.wicket.pages.mgmt.planet;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbBoxRegion;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.db.DbPlanetItemTypeLimitation;
import com.btxtech.game.services.planet.db.DbRegionResource;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.bot.BotEditor;
import com.btxtech.game.wicket.pages.mgmt.inventory.BoxRegionEditor;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.IndexPanel;
import com.btxtech.game.wicket.uiservices.LevelPanel;
import com.btxtech.game.wicket.uiservices.MinutePanel;
import com.btxtech.game.wicket.uiservices.RegionPanel;
import com.btxtech.game.wicket.uiservices.ResourceItemTypePanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import com.btxtech.game.wicket.uiservices.TerrainPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.swing.*;
import java.util.List;

/**
 * User: beat
 * Date: 09.11.2011
 * Time: 15:10:15
 */
public class PlanetEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbPlanet> serviceHelper;
    @SpringBean
    private PlanetSystemService planetSystemService;

    public PlanetEditor(DbPlanet dbPlanet) {
        add(new FeedbackPanel("msgs"));
        // Scope and limitation
        final Form<DbPlanet> form = new Form<>("form", new CompoundPropertyModel<>(new RuModel<DbPlanet>(dbPlanet, DbPlanet.class) {
            @Override
            protected RuServiceHelper<DbPlanet> getRuServiceHelper() {
                return serviceHelper;
            }
        }));
        add(form);
        form.add(new LevelPanel("minLevel"));
        form.add(new TextField("maxMoney"));
        form.add(new TextField("houseSpace"));
        form.add(new DropDownChoice<>("radarMode", RadarMode.getList()));
        form.add(new RegionPanel("startRegion", new TerrainLinkHelper(dbPlanet)) {
            @Override
            protected void updateDependentModel() {
                serviceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new IndexPanel("startPosition"));
        form.add(new BaseItemTypePanel("startItemType"));
        form.add(new TextField("startItemFreeRange"));
        form.add(new TextField("startMoney"));
        form.add(new Label("consumedHouseSpace", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                int consumedHouseSpace = 0;
                for (DbPlanetItemTypeLimitation dbPlanetItemTypeLimitation : form.getModelObject().getItemLimitationCrud().readDbChildren()) {
                    if (dbPlanetItemTypeLimitation.getDbBaseItemType() != null) {
                        consumedHouseSpace += dbPlanetItemTypeLimitation.getDbBaseItemType().getConsumingHouseSpace() * dbPlanetItemTypeLimitation.getCount();
                    }
                }
                return consumedHouseSpace;
            }
        }));
        form.add(new TextField("unlockRazarion"));
        form.add(new FileUploadField("starMapPlanetUpload", new IModel<List<FileUpload>>() {

            @Override
            public List<FileUpload> getObject() {
                return null;
            }

            @Override
            public void setObject(List<FileUpload> list) {
                if (list == null) {
                    // Don't know why...
                    return;
                }
                DbPlanet dbPlanet = form.getModelObject();
                dbPlanet.setStarMapImageContentType(list.get(0).getContentType());
                dbPlanet.setStarMapImageData(list.get(0).getBytes());
            }

            @Override
            public void detach() {
            }
        }));
        form.add(new ExternalLink("starMapPlanetView", ImageHandler.getStarMapPlanetImageUrl(dbPlanet.getId())).setPopupSettings(new PopupSettings().setWidth(300).setHeight(300)));
        form.add(new IndexPanel("starMapImagePosition"));
        // Item Limit
        new CrudChildTableHelper<DbPlanet, DbPlanetItemTypeLimitation>("itemTypeLimitation", null, "createItemTypeLimitation", false, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbPlanetItemTypeLimitation> dbPlanetItemTypeLimitationItem) {
                dbPlanetItemTypeLimitationItem.add(new BaseItemTypePanel("dbBaseItemType"));
                dbPlanetItemTypeLimitationItem.add(new TextField("count"));
            }

            @Override
            protected RuServiceHelper<DbPlanet> getRuServiceHelper() {
                return serviceHelper;
            }

            @Override
            protected DbPlanet getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbPlanetItemTypeLimitation> getCrudChildServiceHelperImpl() {
                return getParent().getItemLimitationCrud();
            }
        };
        // Terrain
        form.add(new TerrainPanel("dbTerrainSetting", new TerrainLinkHelper(dbPlanet)));
        // Region Resource
        new CrudChildTableHelper<DbPlanet, DbRegionResource>("regionResources", null, "createRegionResource", false, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbRegionResource> dbRegionResourceItem) {
                super.extendedPopulateItem(dbRegionResourceItem);
                dbRegionResourceItem.add(new TextField<String>("count"));
                dbRegionResourceItem.add(new ResourceItemTypePanel("resourceItemType"));
                dbRegionResourceItem.add(new RegionPanel("region", new TerrainLinkHelper(form.getModelObject())) {
                    @Override
                    protected void updateDependentModel() {
                        serviceHelper.updateDbEntity(form.getModelObject());
                    }
                });
                dbRegionResourceItem.add(new TextField<String>("minDistanceToItems"));
            }

            @Override
            protected RuServiceHelper<DbPlanet> getRuServiceHelper() {
                return serviceHelper;
            }

            @Override
            protected DbPlanet getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbRegionResource> getCrudChildServiceHelperImpl() {
                return getParent().getRegionResourceCrud();
            }
        };
        form.add(new Button("reactivateRegionResource") {
            @Override
            public void onSubmit() {
                planetSystemService.getServerPlanetServices(form.getModelObject().getId()).getResourceService().reactivate(form.getModelObject());
            }
        });
        // Bot
        new CrudChildTableHelper<DbPlanet, DbBotConfig>("bots", null, "createBot", true, form, false) {

            @Override
            protected CrudChildServiceHelper<DbBotConfig> getCrudChildServiceHelperImpl() {
                return getParent().getBotCrud();
            }

            @Override
            protected void onEditSubmit(DbBotConfig dbBotConfig) {
                setResponsePage(new BotEditor(dbBotConfig, new TerrainLinkHelper(form.getModelObject())));
            }

            @Override
            protected void extendedPopulateItem(Item<DbBotConfig> dbBotConfigItem) {
                displayId(dbBotConfigItem);
                super.extendedPopulateItem(dbBotConfigItem);
            }


            @Override
            protected RuServiceHelper<DbPlanet> getRuServiceHelper() {
                return serviceHelper;
            }

            @Override
            protected DbPlanet getParent() {
                return form.getModelObject();
            }
        };
        form.add(new Button("reactivateBot") {
            @Override
            public void onSubmit() {
                planetSystemService.getServerPlanetServices(form.getModelObject().getId()).getBotService().reactivate(form.getModelObject());
            }
        });
        // Inventory box regions
        new CrudChildTableHelper<DbPlanet, DbBoxRegion>("boxRegions", null, "createBoxRegion", true, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbBoxRegion> dbBoxRegionItem) {
                displayId(dbBoxRegionItem);
                super.extendedPopulateItem(dbBoxRegionItem);
                dbBoxRegionItem.add(new MinutePanel("minInterval"));
                dbBoxRegionItem.add(new MinutePanel("maxInterval"));
                dbBoxRegionItem.add(new RegionPanel("region", new TerrainLinkHelper(form.getModelObject())) {
                    @Override
                    protected void updateDependentModel() {
                        serviceHelper.updateDbEntity(form.getModelObject());
                    }
                });
                dbBoxRegionItem.add(new TextField("itemFreeRange"));
            }

            @Override
            protected RuServiceHelper<DbPlanet> getRuServiceHelper() {
                return serviceHelper;
            }

            @Override
            protected DbPlanet getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbBoxRegion> getCrudChildServiceHelperImpl() {
                return getParent().getBoxRegionCrud();
            }

            @Override
            protected void onEditSubmit(DbBoxRegion dbBoxRegion) {
                setResponsePage(new BoxRegionEditor(dbBoxRegion));
            }
        };
        form.add(new Button("reactivateBoxRegion") {
            @Override
            public void onSubmit() {
                planetSystemService.getServerPlanetServices(form.getModelObject().getId()).getInventoryService().reactivate(form.getModelObject());
            }
        });

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                serviceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new Button("activate") {
            @Override
            public void onSubmit() {
                planetSystemService.activatePlanet(form.getModelObject().getId());
            }
        });
        form.add(new Button("deactivate") {
            @Override
            public void onSubmit() {
                planetSystemService.deactivatePlanet(form.getModelObject().getId());
            }
        });
        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                setResponsePage(new PlanetTable());
            }
        });
    }
}
