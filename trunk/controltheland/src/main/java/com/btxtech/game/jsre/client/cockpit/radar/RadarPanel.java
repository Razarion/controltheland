/*
 * Copyright (c) 2010.
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

package com.btxtech.game.jsre.client.cockpit.radar;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.terrain.TerrainScrollListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:26:58
 */
public class RadarPanel implements TerrainScrollListener {
    private static final int RADAR_X = 10;
    private static final int RADAR_Y = 18;
    private static final int RADAR_WIDTH = 150;
    private static final int RADAR_HEIGHT = 150;
    private static final int ZOOM_IN_X = 13;
    private static final int ZOOM_IN_Y = 1;
    private static final int ZOOM_OUT_X = 29;
    private static final int ZOOM_OUT_Y = 1;
    private static final int ZOOM_HOME_X = 122;
    private static final int ZOOM_HOME_Y = 1;
    private static final int ZOOM_QUEST_X = 138;
    private static final int ZOOM_QUEST_Y = 1;
    private static final int PAGE_UP_X = 70;
    private static final int PAGE_UP_Y = 4;
    private static final int PAGE_DOWN_X = 70;
    private static final int PAGE_DOWN_Y = 164;
    private static final int PAGE_RIGHT_X = 161;
    private static final int PAGE_RIGHT_Y = 78;
    private static final int PAGE_LEFT_X = 1;
    private static final int PAGE_LEFT_Y = 78;

    private static final RadarPanel INSTANCE = new RadarPanel();
    private MiniTerrain miniTerrain;
    private RadarFrameView radarFrameView;
    private RadarHintView radarHintView;
    private RadarItemView radarItemView;
    private AttackVisualisation attackVisualisation;
    private HTML noRadarPanel;
    private boolean hasEnergy = false;
    private RadarMode levelRadarMode = RadarMode.DISABLED;
    private RadarMode itemRadarMode = RadarMode.DISABLED;
    private Set<SyncBaseItem> radarModeItems = new HashSet<SyncBaseItem>();
    private ExtendedCustomButton showAttack;
    private Index questHint;
    private ExtendedCustomButton left;
    private ExtendedCustomButton right;
    private ExtendedCustomButton down;
    private ExtendedCustomButton up;
    private ExtendedCustomButton zoomIn;
    private ExtendedCustomButton zoomOut;

    public static RadarPanel getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private RadarPanel() {
        if (!TerrainView.uglySuppressRadar) {
            TerrainView.getInstance().addTerrainScrollListener(this);
        }
    }

    public AbsolutePanel createWidget(int width, int height) {
        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setPixelSize(width, height);
        setupControlPanel(absolutePanel);

        // No radar Panel
        noRadarPanel = new HTML();
        noRadarPanel.setPixelSize(RADAR_WIDTH, RADAR_HEIGHT);
        noRadarPanel.getElement().getStyle().setColor("#FFFFFF");
        noRadarPanel.getElement().getStyle().setBackgroundColor("#000000");
        absolutePanel.add(noRadarPanel, RADAR_X, RADAR_Y);

        // Terrain
        miniTerrain = new MiniTerrain(RADAR_WIDTH, RADAR_HEIGHT);
        miniTerrain.getCanvas().getElement().getStyle().setZIndex(1);
        miniTerrain.getCanvas().setVisible(false);
        absolutePanel.add(miniTerrain.getCanvas(), RADAR_X, RADAR_Y);

        // Item view
        radarItemView = new RadarItemView(RADAR_WIDTH, RADAR_HEIGHT);
        radarItemView.getCanvas().getElement().getStyle().setZIndex(2);
        radarItemView.getCanvas().setVisible(false);
        absolutePanel.add(radarItemView.getCanvas(), RADAR_X, RADAR_Y);

        // Attack visualisation
        attackVisualisation = new AttackVisualisation(RADAR_WIDTH, RADAR_HEIGHT);
        attackVisualisation.getCanvas().getElement().getStyle().setZIndex(3);
        attackVisualisation.getCanvas().setVisible(false);
        absolutePanel.add(attackVisualisation.getCanvas(), RADAR_X, RADAR_Y);

        // Hint view
        radarHintView = new RadarHintView(RADAR_WIDTH, RADAR_HEIGHT);
        radarHintView.getCanvas().getElement().getStyle().setZIndex(4);
        radarHintView.getCanvas().setVisible(false);
        absolutePanel.add(radarHintView.getCanvas(), RADAR_X, RADAR_Y);

        // Frame view
        radarFrameView = new RadarFrameView(RADAR_WIDTH, RADAR_HEIGHT);
        radarFrameView.getCanvas().getElement().getStyle().setZIndex(5);
        radarFrameView.getCanvas().setVisible(false);
        absolutePanel.add(radarFrameView.getCanvas(), RADAR_X, RADAR_Y);

        return absolutePanel;
    }

    private void setupControlPanel(AbsolutePanel absolutePanel) {
        zoomIn = new ExtendedCustomButton("zoom-in", false, ClientI18nHelper.CONSTANTS.tooltipZoomIn(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ScaleStep newScale = ScaleStep.zoomIn(getScale());
                if (newScale != null) {
                    setScale(newScale);
                }
            }
        });
        absolutePanel.add(zoomIn, ZOOM_IN_X, ZOOM_IN_Y);
        zoomOut = new ExtendedCustomButton("zoom-out", false, ClientI18nHelper.CONSTANTS.tooltipZoomOut(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ScaleStep newScale = ScaleStep.zoomOut(getScale());
                if (newScale != null) {
                    setScale(newScale);
                }
            }
        });
        absolutePanel.add(zoomOut, ZOOM_OUT_X, ZOOM_OUT_Y);
        left = new ExtendedCustomButton("arrowleft", false, ClientI18nHelper.CONSTANTS.tooltipRadarPageLeft(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                move(-1, 0);
            }
        });
        absolutePanel.add(left, PAGE_LEFT_X, PAGE_LEFT_Y);
        right = new ExtendedCustomButton("arrowright", false, ClientI18nHelper.CONSTANTS.tooltipRadarPageRight(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                move(1, 0);
            }
        });
        absolutePanel.add(right, PAGE_RIGHT_X, PAGE_RIGHT_Y);
        up = new ExtendedCustomButton("arrowup", false, ClientI18nHelper.CONSTANTS.tooltipRadarPageUp(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                move(0, -1);
            }
        });
        absolutePanel.add(up, PAGE_UP_X, PAGE_UP_Y);
        down = new ExtendedCustomButton("arrowdown", false, ClientI18nHelper.CONSTANTS.tooltipRadarPageDown(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                move(0, 1);
            }
        });
        absolutePanel.add(down, PAGE_DOWN_X, PAGE_DOWN_Y);
        absolutePanel.add(new ExtendedCustomButton("zoom-home", false, ClientI18nHelper.CONSTANTS.tooltipZoomHome(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TerrainView.getInstance().moveToHome();
                moveToMainWindowMiddle();
            }
        }), ZOOM_HOME_X, ZOOM_HOME_Y);
        showAttack = new ExtendedCustomButton("zoom-attack", true, ClientI18nHelper.CONSTANTS.tooltipZoomAttack(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                attackVisualisation.activate(showAttack.isDown());
            }
        });
        showAttack.setDownState(true);
        absolutePanel.add(showAttack, ZOOM_QUEST_X, ZOOM_QUEST_Y);
    }

    private void moveToMainWindowMiddle() {
        moveToMiddle(TerrainView.getInstance().getViewOriginLeft(),
                TerrainView.getInstance().getViewOriginTop(),
                TerrainView.getInstance().getViewWidth(),
                TerrainView.getInstance().getViewHeight());
    }

    private void move(int horizontal, int vertical) {
        Index viewOrigin = miniTerrain.getViewOrigin();
        if (horizontal > 0) {
            viewOrigin = viewOrigin.add((int) (miniTerrain.getAbsoluteVisibleWidth() * 0.9), 0);
        } else if (horizontal < 0) {
            viewOrigin = viewOrigin.sub((int) (miniTerrain.getAbsoluteVisibleWidth() * 0.9), 0);
        }
        if (vertical > 0) {
            viewOrigin = viewOrigin.add(0, (int) (miniTerrain.getAbsoluteVisibleHeight() * 0.9));
        } else if (vertical < 0) {
            viewOrigin = viewOrigin.sub(0, (int) (miniTerrain.getAbsoluteVisibleHeight() * 0.9));
        }
        setViewRect(viewOrigin);
    }

    private void moveToMiddle(Index middle) {
        miniTerrain.setAbsoluteViewRectMiddle(middle);
        radarFrameView.setAbsoluteViewRectMiddle(middle);
        radarHintView.setAbsoluteViewRectMiddle(middle);
        radarItemView.setAbsoluteViewRectMiddle(middle);
        attackVisualisation.setAbsoluteViewRectMiddle(middle);
    }

    private void setViewRect(Index viewOrigin) {
        miniTerrain.setAbsoluteViewRect(viewOrigin);
        radarFrameView.setAbsoluteViewRect(viewOrigin);
        radarHintView.setAbsoluteViewRect(viewOrigin);
        radarItemView.setAbsoluteViewRect(viewOrigin);
        attackVisualisation.setAbsoluteViewRect(viewOrigin);
    }

    private ScaleStep getScale() {
        return miniTerrain.getScale();
    }

    private void setScale(ScaleStep scale) {
        miniTerrain.setScale(scale);
        radarFrameView.setScale(scale);
        radarHintView.setScale(scale);
        radarItemView.setScale(scale);
        attackVisualisation.setScale(scale);
    }

    private void handleRadarState() {
        RadarMode mode;
        if (levelRadarMode == RadarMode.DISABLED) {
            mode = RadarMode.DISABLED;
        } else {
            if (hasEnergy) {
                mode = RadarMode.getHigher(itemRadarMode, levelRadarMode);
            } else {
                mode = levelRadarMode;
            }
        }

        boolean showMap = RadarMode.MAP.sameOrHigher(mode);

        if (miniTerrain != null) {
            miniTerrain.getCanvas().setVisible(showMap);
        }
        if (radarFrameView != null) {
            radarFrameView.getCanvas().setVisible(showMap);
        }
        if (radarHintView != null) {
            radarHintView.getCanvas().setVisible(showMap);
        }
        if (attackVisualisation != null) {
            attackVisualisation.getCanvas().setVisible(showMap);
        }

        boolean showUnits = RadarMode.MAP_AND_UNITS.sameOrHigher(mode);
        if (radarItemView != null) {
            radarItemView.getCanvas().setVisible(showUnits);
        }

        if (noRadarPanel != null) {
            if (mode == RadarMode.DISABLED) {
                noRadarPanel.setVisible(false);
            } else if (showMap) {
                noRadarPanel.setVisible(false);
            } else {
                if (!hasEnergy) {
                    noRadarPanel.setVisible(true);
                    noRadarPanel.setHTML("<br/>" + ClientI18nHelper.CONSTANTS.radarNoPower());
                } else {
                    noRadarPanel.setVisible(true);
                    noRadarPanel.setHTML("<br/>" + ClientI18nHelper.CONSTANTS.radarNoRadarBuilding());
                }
            }
        }
    }

    public void updateEnergy(int generating, int consuming) {
        boolean state = generating >= consuming;
        if (hasEnergy == state) {
            return;
        }
        hasEnergy = state;
        handleRadarState();
    }

    public void onTerrainSettings(TerrainSettings terrainSettings) {
        miniTerrain.onTerrainSettings(terrainSettings);
        radarFrameView.onTerrainSettings(terrainSettings);
        radarHintView.onTerrainSettings(terrainSettings);
        radarItemView.onTerrainSettings(terrainSettings);
        attackVisualisation.onTerrainSettings(terrainSettings);
        // set defaults
        if (Connection.getInstance().getGameEngineMode() == null) {
            // In editors
            setScale(ScaleStep.WHOLE_MAP);
            moveToMainWindowMiddle();
            left.setVisible(true);
            right.setVisible(true);
            up.setVisible(true);
            down.setVisible(true);
            zoomIn.setVisible(true);
            zoomOut.setVisible(true);
        } else if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE) {
            setScale(ScaleStep.DEFAULT);
            moveToMainWindowMiddle();
            left.setVisible(true);
            right.setVisible(true);
            up.setVisible(true);
            down.setVisible(true);
            zoomIn.setVisible(true);
            zoomOut.setVisible(true);
        } else {
            setScale(ScaleStep.WHOLE_MAP_MISSION);
            left.setVisible(false);
            right.setVisible(false);
            up.setVisible(false);
            down.setVisible(false);
            zoomIn.setVisible(false);
            zoomOut.setVisible(false);
        }
    }

    public RadarFrameView getRadarFrameView() {
        return radarFrameView;
    }

    public void setLevelRadarMode(RadarMode levelRadarMode) {
        this.levelRadarMode = levelRadarMode;
        handleRadarState();
    }

    public void onRadarModeItemChanged(SyncBaseItem syncBaseItem) {
        radarModeItems.add(syncBaseItem);
        handleItemRadarState();
    }

    public void onRadarModeItemRemoved(SyncBaseItem syncBaseItem) {
        radarModeItems.remove(syncBaseItem);
        handleItemRadarState();
    }

    public void onItemTypeChanged(SyncItem syncItem) {
        if (syncItem instanceof SyncBaseItem) {
            if (radarModeItems.contains(syncItem)) {
                handleItemRadarState();
            }
        }
    }

    private void handleItemRadarState() {
        itemRadarMode = findHighestRadarMode();
        handleRadarState();
    }

    public void cleanup() {
        radarModeItems.clear();
        itemRadarMode = RadarMode.DISABLED;
        levelRadarMode = RadarMode.DISABLED;

        if (radarFrameView != null) {
            radarFrameView.cleanup();
        }
        if (radarHintView != null) {
            radarHintView.cleanup();
        }
        if (radarItemView != null) {
            radarItemView.cleanup();
        }
        if (attackVisualisation != null) {
            attackVisualisation.cleanup();
        }
    }

    private RadarMode findHighestRadarMode() {
        RadarMode radarMode = RadarMode.NONE;
        for (SyncBaseItem radarModeItem : radarModeItems) {
            if (!radarModeItem.isReady()) {
                continue;
            }
            if (radarMode.sameOrHigher(radarModeItem.getBaseItemType().getSpecialType().getRadarMode())) {
                radarMode = radarModeItem.getBaseItemType().getSpecialType().getRadarMode();
            }
        }
        return radarMode;
    }

    @Deprecated
    public void showHint(SyncBaseItem enemyBaseItem) {
        questHint = null;
        radarHintView.showHint(enemyBaseItem);
    }

    public void showHint(Index position) {
        questHint = position;
        radarHintView.showHint(position);
        setScale(ScaleStep.DEFAULT);
        moveToMiddle(questHint);
    }

    public void hideHint() {
        questHint = null;
        radarHintView.hideHint();
    }

    public void blinkHint() {
        radarHintView.blinkHint();
    }

    public MiniTerrain getMiniTerrain() {
        return miniTerrain;
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        moveToMiddle(left, top, width, height);
    }

    private void moveToMiddle(int left, int top, int width, int height) {
        if (miniTerrain == null || miniTerrain.getTerrainSettings() == null) {
            // Browser-resize during startup
            return;
        }
        Index middle = new Index(left + width / 2, top + height / 2);
        if (!miniTerrain.getAbsoluteViewRectangle().contains(new Index(left, top))) {
            moveToMiddle(middle);
        } else if (!miniTerrain.getAbsoluteViewRectangle().contains(new Index(left + width, top))) {
            moveToMiddle(middle);
        } else if (!miniTerrain.getAbsoluteViewRectangle().contains(new Index(left, top + height))) {
            moveToMiddle(middle);
        } else if (!miniTerrain.getAbsoluteViewRectangle().contains(new Index(left + width, top + height))) {
            moveToMiddle(middle);
        }
    }

    public void onwItemUnderAttack(SyncBaseItem target) {
        if (attackVisualisation != null) {
            attackVisualisation.onwItemUnderAttack(target);
        }
        if (showAttack.isDown() && getScale() != ScaleStep.WHOLE_MAP) {
            setScale(ScaleStep.WHOLE_MAP);
        }
    }
}
