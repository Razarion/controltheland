package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.StartPointInfo;
import com.btxtech.game.jsre.client.control.task.RealDeltaStartupTask;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientDeadEndProtection;
import com.btxtech.game.jsre.common.packets.BaseLostPacket;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 30.04.13
 * Time: 19:41
 */
public class StartPointMode {
    private static final StartPointMode INSTANCE = new StartPointMode();
    private StartPointItemPlacer startPointItemPlacer;
    private Logger log = Logger.getLogger(StartPointMode.class.getName());

    public static StartPointMode getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private StartPointMode() {
    }

    public void activateIfNeeded() {
        if (((RealGameInfo) Connection.getInstance().getGameInfo()).getStartPointInfo() != null) {
            activate(((RealGameInfo) Connection.getInstance().getGameInfo()).getStartPointInfo());
        }
    }

    private void activate(StartPointInfo startPointInfo) {
        if (startPointInfo.getSuggestedPosition() != null) {
            TerrainView.getInstance().moveToMiddle(startPointInfo.getSuggestedPosition());
        }
        startPointItemPlacer = new StartPointItemPlacer(startPointInfo);
        RadarPanel.getInstance().setLevelRadarMode(RadarMode.MAP_AND_UNITS);
        ClientDeadEndProtection.getInstance().stop();
    }

    public void deactivate() {
        startPointItemPlacer = null;
        RadarPanel.getInstance().setLevelRadarMode(ClientPlanetServices.getInstance().getPlanetInfo().getRadarMode());
        ClientDeadEndProtection.getInstance().start();
    }

    public boolean isActive() {
        return startPointItemPlacer != null;
    }

    public StartPointItemPlacer getStartPointPlacer() {
        return startPointItemPlacer;
    }

    public void execute(int relativeX, int relativeY, int absoluteX, int absoluteY) {
        startPointItemPlacer.onMove(relativeX, relativeY, absoluteX, absoluteY);
        if (startPointItemPlacer.isPositionValid()) {
            if (Connection.getMovableServiceAsync() != null) {
                Connection.getMovableServiceAsync().createBase(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(), new Index(absoluteX, absoluteY), new AsyncCallback<RealGameInfo>() {
                    @Override
                    public void onFailure(Throwable e) {
                        if (e instanceof PositionInBotException) {
                            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.createBase(), ClientI18nHelper.CONSTANTS.createBaseInBotFailed()), DialogManager.Type.PROMPTLY);
                        } else {
                            log.log(Level.SEVERE, "MovableServiceAsync.createBase()", e);
                }
                    }

                    @Override
                    public void onSuccess(RealGameInfo realGameInfo) {
                        if (realGameInfo != null) {
                            RealDeltaStartupTask.setCommon(realGameInfo);
                            ClientBase.getInstance().recalculateOnwItems();
                            deactivate();
                        }
                    }
                });
            }
        }
    }

    public void onBaseLost(BaseLostPacket baseLostPacket) {
        if (isActive()) {
            log.warning("StartPointMode.onBaseLost() is already active");
        }

        RealDeltaStartupTask.setCommon(baseLostPacket.getRealGameInfo());
        ClientBase.getInstance().recalculateOnwItems();
        SideCockpit.getInstance().updateItemLimit();
        activate(baseLostPacket.getRealGameInfo().getStartPointInfo());
    }
}
