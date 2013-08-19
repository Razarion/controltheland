package com.btxtech.game.jsre.client.dialogs.starmap;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.starmap.images.StarMapImages;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 12.08.13
 * Time: 11:32
 */
public class StarMapDialog extends Dialog {
    private static final StarMapImages STAR_MAP_IMAGES = GWT.create(StarMapImages.class);
    private AbsolutePanel solarSystemPanel;
    private PlanetInfoPanel planetInfoPopUp;

    public StarMapDialog() {
        super(ClientI18nHelper.CONSTANTS.starMapDialogTitle());
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalStateException("StarMapDialog is only allowed in real game: " + Connection.getInstance().getGameEngineMode());
        }
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        StarMapInfo starMapInfo = createStarMapInfo();
        solarSystemPanel = new AbsolutePanel();
        solarSystemPanel.setPixelSize(starMapInfo.getWidth(), starMapInfo.getHeight());
        solarSystemPanel.getElement().getStyle().setBackgroundImage("url(" + STAR_MAP_IMAGES.starBg().getSafeUri().asString() + ")");
        dialogVPanel.add(solarSystemPanel);
        setupPlanets(starMapInfo.getStarMapPlanetInfos());
        solarSystemPanel.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (planetInfoPopUp != null) {
                    solarSystemPanel.remove(planetInfoPopUp);
                    planetInfoPopUp = null;
                }
            }
        }, MouseMoveEvent.getType());
    }

    private void setupPlanets(Collection<StarMapPlanetInfo> starMapPlanetInfos) {
        for (final StarMapPlanetInfo starMapPlanetInfo : starMapPlanetInfos) {
            final Image image = ImageHandler.getStarMapPlanetImage(starMapPlanetInfo.getPlanetLiteInfo().getPlanetId());
            image.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    if (planetInfoPopUp == null) {
                        planetInfoPopUp = new PlanetInfoPanel(starMapPlanetInfo);
                        solarSystemPanel.add(planetInfoPopUp, 0, 0);
                        Index planetPanelPosition = planetInfoPanelPlacer(image, starMapPlanetInfo.getPosition());
                        solarSystemPanel.setWidgetPosition(planetInfoPopUp, planetPanelPosition.getX(), planetPanelPosition.getY());
                    }
                }
            });
            image.addMouseMoveHandler(new MouseMoveHandler() {
                @Override
                public void onMouseMove(MouseMoveEvent event) {
                    event.stopPropagation();
                }
            });
            solarSystemPanel.add(image, starMapPlanetInfo.getPosition().getX(), starMapPlanetInfo.getPosition().getY());
        }
    }

    private Index planetInfoPanelPlacer(Image image, Index position) {
        Index panelPosition = new Index(position.getX() + image.getWidth() / 2, position.getY());
        Index bottom = panelPosition.add(planetInfoPopUp.getOffsetWidth(), planetInfoPopUp.getOffsetHeight());
        if (bottom.getX() > solarSystemPanel.getOffsetWidth()) {
            panelPosition.setX(panelPosition.getX() - (bottom.getX() - solarSystemPanel.getOffsetWidth()));
        }
        if (bottom.getY() > solarSystemPanel.getOffsetHeight()) {
            panelPosition.setY(panelPosition.getY() - (bottom.getY() - solarSystemPanel.getOffsetHeight()));
        }
        return panelPosition;
    }

    private StarMapInfo createStarMapInfo() {
        StarMapInfo starMapInfo = new StarMapInfo();
        starMapInfo.setWidth(600);
        starMapInfo.setHeight(500);
        Collection<StarMapPlanetInfo> starMapPlanetInfos = new ArrayList<StarMapPlanetInfo>();
        starMapInfo.setStarMapPlanetInfos(starMapPlanetInfos);
        // Planet 1
        StarMapPlanetInfo starMapPlanetInfo = new StarMapPlanetInfo();
        starMapPlanetInfo.setPosition(new Index(100, 100));
        starMapPlanetInfo.setPlanetLiteInfo(new PlanetLiteInfo(1, "Planet 1", 0));
        starMapPlanetInfo.setBases(12);
        starMapPlanetInfo.setBots(5);
        starMapPlanetInfo.setMinLevel(1);
        starMapPlanetInfo.setSize(300);
        starMapPlanetInfos.add(starMapPlanetInfo);
        // Planet 2
        starMapPlanetInfo = new StarMapPlanetInfo();
        starMapPlanetInfo.setPosition(new Index(500, 400));
        starMapPlanetInfo.setPlanetLiteInfo(new PlanetLiteInfo(1, "Planet 1", 0));
        starMapPlanetInfo.setBases(22);
        starMapPlanetInfo.setBots(15);
        starMapPlanetInfo.setMinLevel(0);
        starMapPlanetInfo.setSize(1300);
        starMapPlanetInfos.add(starMapPlanetInfo);
        return starMapInfo;
    }
}
