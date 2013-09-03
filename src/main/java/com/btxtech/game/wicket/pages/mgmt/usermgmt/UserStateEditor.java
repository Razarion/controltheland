/*
 * Copyright (c) 2011.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.wicket.pages.mgmt.usermgmt;

import com.btxtech.game.services.finance.FinanceService;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.statistics.StatisticsEntry;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.BaseItemTypeCollectionPanel;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.LevelReadonlyPanel;
import com.btxtech.game.wicket.uiservices.PlanetCollectionPanel;
import com.btxtech.game.wicket.uiservices.QuestCollectionPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 26.01.2011
 * Time: 16:30:59
 */
public class UserStateEditor extends MgmtWebPage {
    public static final String USER_STATE_HASH = "userSateHash";
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private XpService xpService;
    @SpringBean
    private GlobalInventoryService globalInventoryService;
    @SpringBean
    private StatisticsService statisticsService;
    @SpringBean
    private FinanceService financeService;
    @SpringBean
    private ServerUnlockService unlockService;
    private Integer dbLevelId;
    private Integer xp;
    private Integer razarionBought;

    public UserStateEditor(PageParameters pageParameters) {
        final int userStateHash = pageParameters.get(USER_STATE_HASH).toInt();
        add(new FeedbackPanel("msgs"));

        final Form<UserState> form = new Form<>("form", new CompoundPropertyModel<>(new IModel<UserState>() {
            private UserState userState;

            @Override
            public UserState getObject() {
                if (userState == null) {
                    userState = userService.getUserState4Hash(userStateHash);
                }
                return userState;
            }

            @Override
            public void setObject(UserState object) {
                // Ignore
            }

            @Override
            public void detach() {
                userState = null;
            }
        }));
        add(form);

        form.add(new Label("sessionId"));
        form.add(new Label("userName", userService.getUserName(userService.getUserState4Hash(userStateHash))));
        form.add(new Label("userId"));
        setupRazarionBought(form);
        setupLevel(form);
        setupInventoryItem(form);
        setupInventoryArtifact(form);
        setupRazarion(form);
        setupUnlockedItems(form);
        setupUnlockedQuests(form);
        setupUnlockedPlanet(form);
        setupQuests(form);
        setupHighScore(form);
    }

    private void setupRazarionBought(final Form<UserState> form) {
        form.add(new TextField<>("razarionBought", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return razarionBought;
            }

            @Override
            public void setObject(Integer integer) {
                razarionBought = integer;
            }

            @Override
            public void detach() {
                razarionBought = null;
            }
        }, Integer.class));
        form.add(new Button("activateRazarionBought") {

            @Override
            public void onSubmit() {
                if (razarionBought != null) {
                    financeService.razarionBought(razarionBought, form.getModelObject());
                    razarionBought = null;
                }
            }
        });
    }

    private void setupLevel(final Form<UserState> form) {
        form.add(new LevelReadonlyPanel("dbLevelId"));
        form.add(new TextField<>("newDbLevelId", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return null;
            }

            @Override
            public void setObject(Integer integer) {
                dbLevelId = integer;
            }

            @Override
            public void detach() {
                dbLevelId = null;
            }
        }, Integer.class));
        form.add(new Label("xp"));
        form.add(new Label("xpToLevelUp", new LoadableDetachableModel<Integer>() {
            @Override
            protected Integer load() {
                return userGuidanceService.getXp2LevelUp(form.getModelObject());
            }
        }));
        form.add(new TextField<>("addXp", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return null;
            }

            @Override
            public void setObject(Integer value) {
                xp = value;
            }

            @Override
            public void detach() {
                xp = null;
            }
        }, Integer.class));

        form.add(new Button("activateLevel") {

            @Override
            public void onSubmit() {
                if (dbLevelId != null) {
                    userGuidanceService.promote((UserState) form.getDefaultModelObject(), dbLevelId);
                }
            }
        });
        form.add(new Button("activateXp") {

            @Override
            public void onSubmit() {
                if (xp != null) {
                    xpService.onReward((UserState) form.getDefaultModelObject(), xp);
                }
            }
        });
    }

    private void setupInventoryItem(final Form<UserState> form) {
        final DetachHashListProvider<InventoryHelperEntry> inventoryItemProvider = new DetachHashListProvider<InventoryHelperEntry>() {

            @Override
            protected List<InventoryHelperEntry> createList() {
                Map<Integer, Integer> idCountMap = new HashMap<>();
                for (Integer itemId : form.getModelObject().getInventoryItemIds()) {
                    Integer count = idCountMap.get(itemId);
                    if (count == null) {
                        count = 0;
                    }
                    idCountMap.put(itemId, count + 1);
                }

                List<InventoryHelperEntry> inventoryHelperEntries = new ArrayList<>();
                for (Map.Entry<Integer, Integer> entry : idCountMap.entrySet()) {
                    DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().readDbChild(entry.getKey());
                    inventoryHelperEntries.add(new InventoryHelperEntry(dbInventoryItem.getName(), dbInventoryItem.getId(), entry.getValue()));
                }
                return inventoryHelperEntries;
            }
        };

        form.add(new DataView<InventoryHelperEntry>("inventoryItems", inventoryItemProvider) {
            public void populateItem(final Item<InventoryHelperEntry> item) {
                final int id = item.getModelObject().getId();
                item.add(new Label("name"));
                item.add(new TextField("count"));
                item.add(new Button("delete") {
                    @Override
                    public void onSubmit() {
                        form.getModelObject().removeAllInventoryItemId(id);
                    }
                }.setDefaultFormProcessing(false));
            }
        });
        final TextField<Integer> addInventoryItemId = new TextField<>("addItemId", new Model<Integer>(null), Integer.class);
        form.add(addInventoryItemId);
        form.add(new Button("addItemIdBtn") {
            @Override
            public void onSubmit() {
                if (addInventoryItemId.getModelObject() != null) {
                    // Verify
                    globalInventoryService.getItemCrud().readDbChild(addInventoryItemId.getModelObject());
                    form.getModelObject().addInventoryItem(addInventoryItemId.getModelObject());
                    inventoryItemProvider.forceReload();
                }
                addInventoryItemId.setModelObject(null);
            }
        });
        form.add(new Button("activateItemIdBtn") {

            @Override
            public void onSubmit() {
                form.getModelObject().getInventoryItemIds().clear();
                for (InventoryHelperEntry inventoryHelperEntry : inventoryItemProvider.getList()) {
                    for (int i = 0; i < inventoryHelperEntry.getCount(); i++) {
                        form.getModelObject().addInventoryItem(inventoryHelperEntry.getId());
                    }
                }
            }
        });
    }

    private void setupInventoryArtifact(final Form<UserState> form) {
        final DetachHashListProvider<InventoryHelperEntry> inventoryArtifactProvider = new DetachHashListProvider<InventoryHelperEntry>() {

            @Override
            protected List<InventoryHelperEntry> createList() {
                Map<Integer, Integer> idCountMap = new HashMap<>();
                for (Integer itemId : form.getModelObject().getInventoryArtifactIds()) {
                    Integer count = idCountMap.get(itemId);
                    if (count == null) {
                        count = 0;
                    }
                    idCountMap.put(itemId, count + 1);
                }

                List<InventoryHelperEntry> inventoryHelperEntries = new ArrayList<>();
                for (Map.Entry<Integer, Integer> entry : idCountMap.entrySet()) {
                    DbInventoryArtifact dbInventoryArtifact = globalInventoryService.getArtifactCrud().readDbChild(entry.getKey());
                    inventoryHelperEntries.add(new InventoryHelperEntry(dbInventoryArtifact.getName(), dbInventoryArtifact.getId(), entry.getValue()));
                }
                return inventoryHelperEntries;
            }
        };

        form.add(new DataView<InventoryHelperEntry>("inventoryArtifacts", inventoryArtifactProvider) {
            public void populateItem(final Item<InventoryHelperEntry> item) {
                final int id = item.getModelObject().getId();
                item.add(new Label("name"));
                item.add(new TextField("count"));
                item.add(new Button("delete") {
                    @Override
                    public void onSubmit() {
                        form.getModelObject().removeAllInventoryArtifactId(id);
                    }
                }.setDefaultFormProcessing(false));
            }
        });
        final TextField<Integer> addInventoryArtifactId = new TextField<>("addArtifactId", new Model<Integer>(null), Integer.class);
        form.add(addInventoryArtifactId);
        form.add(new Button("addArtifactIdBtn") {
            @Override
            public void onSubmit() {
                if (addInventoryArtifactId.getModelObject() != null) {
                    // Verify
                    globalInventoryService.getArtifactCrud().readDbChild(addInventoryArtifactId.getModelObject());
                    form.getModelObject().addInventoryArtifact(addInventoryArtifactId.getModelObject());
                    inventoryArtifactProvider.forceReload();
                }
                addInventoryArtifactId.setModelObject(null);
            }
        });
        form.add(new Button("activateArtifactIdBtn") {

            @Override
            public void onSubmit() {
                form.getModelObject().getInventoryArtifactIds().clear();
                for (InventoryHelperEntry inventoryHelperEntry : inventoryArtifactProvider.getList()) {
                    for (int i = 0; i < inventoryHelperEntry.getCount(); i++) {
                        form.getModelObject().addInventoryArtifact(inventoryHelperEntry.getId());
                    }
                }
            }
        });
    }

    private void setupRazarion(final Form<UserState> form) {
        final TextField<Integer> field = new TextField<>("razarion", new IModel<Integer>() {
            Integer razarion;

            @Override
            public Integer getObject() {
                if (razarion == null) {
                    razarion = form.getModelObject().getRazarion();
                }
                return razarion;
            }

            @Override
            public void setObject(Integer integer) {
                razarion = integer;
            }

            @Override
            public void detach() {
                razarion = null;
            }
        }, Integer.class);
        form.add(field);
        form.add(new Button("activateRazarion") {
            @Override
            public void onSubmit() {
                form.getModelObject().setRazarion(field.getModelObject());
            }
        });
    }

    private void setupHighScore(final Form<UserState> userStateForm) {
        final Form<StatisticsEntry> form = new Form<>("formStatistics", new CompoundPropertyModel<>(new IModel<StatisticsEntry>() {
            private StatisticsEntry statisticsEntry;

            @Override
            public StatisticsEntry getObject() {
                if (statisticsEntry == null) {
                    statisticsEntry = statisticsService.getStatisticsEntryAccess(userStateForm.getModelObject());
                }
                return statisticsEntry;
            }

            @Override
            public void setObject(StatisticsEntry object) {
                // Ignore
            }

            @Override
            public void detach() {
                statisticsEntry = null;
            }
        }));
        add(form);
        form.add(new TextField<>("killedStructureBot"));
        form.add(new TextField<>("killedUnitsBot"));
        form.add(new TextField<>("killedStructurePlayer"));
        form.add(new TextField<>("killedUnitsPlayer"));
        form.add(new TextField<>("lostStructureBot"));
        form.add(new TextField<>("lostUnitsBot"));
        form.add(new TextField<>("lostStructurePlayer"));
        form.add(new TextField<>("lostUnitsPlayer"));
        form.add(new TextField<>("builtStructures"));
        form.add(new TextField<>("builtUnits"));
        form.add(new TextField<>("basesDestroyedBot"));
        form.add(new TextField<>("basesDestroyedPlayer"));
        form.add(new TextField<>("basesLostBot"));
        form.add(new TextField<>("basesLostPlayer"));
        form.add(new Button("activate"));
    }

    private void setupUnlockedItems(final Form<UserState> form) {
        final LoadableDetachableModel<Collection<DbBaseItemType>> model = new LoadableDetachableModel<Collection<DbBaseItemType>>() {
            @Override
            protected Collection<DbBaseItemType> load() {
                return unlockService.getUnlockDbBaseItemTypes(form.getModelObject());
            }
        };
        form.add(new BaseItemTypeCollectionPanel("unlockedItems", model));
        form.add(new Button("saveUnlockedItems") {
            @Override
            public void onSubmit() {
                unlockService.setUnlockedBaseItemTypesBackend(model.getObject(), form.getModelObject());
            }
        });
    }

    private void setupUnlockedQuests(final Form<UserState> form) {
        final LoadableDetachableModel<Collection<DbLevelTask>> model = new LoadableDetachableModel<Collection<DbLevelTask>>() {
            @Override
            protected Collection<DbLevelTask> load() {
                return unlockService.getUnlockQuests(form.getModelObject());
            }
        };
        form.add(new QuestCollectionPanel("unlockedQuests", model));
        form.add(new Button("saveUnlockedQuests") {
            @Override
            public void onSubmit() {
                unlockService.setUnlockedQuestsBackend(model.getObject(), form.getModelObject());
            }
        });
    }

    private void setupUnlockedPlanet(final Form<UserState> form) {
        final LoadableDetachableModel<Collection<DbPlanet>> model = new LoadableDetachableModel<Collection<DbPlanet>>() {
            @Override
            protected Collection<DbPlanet> load() {
                return unlockService.getUnlockPlanets(form.getModelObject());
            }
        };
        form.add(new PlanetCollectionPanel("unlockedPlanets", model));
        form.add(new Button("saveUnlockedPlanets") {
            @Override
            public void onSubmit() {
                unlockService.setUnlockedPlanetsBackend(model.getObject(), form.getModelObject());
            }
        });
    }


    public void setupQuests(final Form<UserState> form) {
        final LoadableDetachableModel<Collection<DbLevelTask>> model = new LoadableDetachableModel<Collection<DbLevelTask>>() {
            @Override
            protected Collection<DbLevelTask> load() {
                return userGuidanceService.getDoneDbLevelTasks(form.getModelObject());
            }
        };
        form.add(new QuestCollectionPanel("questsDone", model));
        form.add(new Button("saveQuestsDone") {
            @Override
            public void onSubmit() {
                userGuidanceService.setDoneDbLevelTasks(model.getObject(), form.getModelObject());
            }
        });

        DetachHashListProvider<DbLevelTask> provider = new DetachHashListProvider<DbLevelTask>() {
            @Override
            protected List<DbLevelTask> createList() {
                return userGuidanceService.getDbLevel(form.getModelObject()).getLevelTaskCrud().readDbChildren();
            }
        };

        form.add(new DataView<DbLevelTask>("questTable", provider) {
            @Override
            protected void populateItem(final Item<DbLevelTask> item) {
                item.add(new Label("id"));
                item.add(new Label("name"));
                item.add(new Label("type", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        return item.getModelObject().getDbTutorialConfig() != null ? "Mission" : "Quest";
                    }
                }));
                item.add(new Label("state", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        switch (userGuidanceService.getLevelTaskState(item.getModelObject().getId(), form.getModelObject())) {
                            case OPEN:
                                return "Open";
                            case ACTIVE:
                                return "Active";
                            case DONE:
                                return "Done";
                            default:
                                return "???";
                        }
                    }
                }));
                switch (userGuidanceService.getLevelTaskState(item.getModelObject().getId(), form.getModelObject())) {
                    case OPEN:
                        item.add(new AttributeModifier("bgcolor", new Model<>("#FFBBBB")));
                        break;
                    case ACTIVE:
                        item.add(new AttributeModifier("bgcolor", new Model<>("#FFFFBB")));
                        break;
                    case DONE:
                        item.add(new AttributeModifier("bgcolor", new Model<>("#BBFFBB")));
                        break;
                }
            }
        });

    }

}
