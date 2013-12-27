package com.btxtech.game.jsre.client.dialogs.starmap;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.starmap.images.StarMapImages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Collection;

/**
 * User: beat
 * Date: 12.08.13
 * Time: 11:32
 */
public class StarMapDialog extends Dialog {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;
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
        solarSystemPanel = new AbsolutePanel();
        solarSystemPanel.setPixelSize(WIDTH, HEIGHT);
        solarSystemPanel.getElement().getStyle().setBackgroundImage("url(" + STAR_MAP_IMAGES.starBg().getSafeUri().asString() + ")");
        solarSystemPanel.getElement().getStyle().setProperty("display", "table");
        solarSystemPanel.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (planetInfoPopUp != null) {
                    removePlanetPopUp();
                }
            }
        }, MouseMoveEvent.getType());
        dialogVPanel.add(solarSystemPanel);
        setupSplashText(ClientI18nHelper.CONSTANTS.loading());
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().getStarMapInfo(new AsyncCallback<StarMapInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.getStarMapInfo()", caught);
                }

                @Override
                public void onSuccess(StarMapInfo starMapInfo) {
                    solarSystemPanel.clear();
                    if (starMapInfo != null) {
                        setupPlanets(starMapInfo.getStarMapPlanetInfos());
                    } else {
                        setupSplashText(ClientI18nHelper.CONSTANTS.serverError());
                    }
                }
            });
        }
    }

    private void setupPlanets(Collection<StarMapPlanetInfo> starMapPlanetInfos) {
        for (final StarMapPlanetInfo starMapPlanetInfo : starMapPlanetInfos) {
            final Image image = ImageHandler.getStarMapPlanetImage(starMapPlanetInfo.getPlanetLiteInfo().getPlanetId());
            image.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    if (planetInfoPopUp == null) {
                        createPlanetPopUp(starMapPlanetInfo, image);
                    } else if(!planetInfoPopUp.isSame(starMapPlanetInfo)) {
                        removePlanetPopUp();
                        createPlanetPopUp(starMapPlanetInfo, image);
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

    private void createPlanetPopUp(StarMapPlanetInfo starMapPlanetInfo, Image image) {
        planetInfoPopUp = new PlanetInfoPanel(this, starMapPlanetInfo);
        solarSystemPanel.add(planetInfoPopUp, 0, 0);
        Index planetPanelPosition = planetInfoPanelPlacer(image, starMapPlanetInfo.getPosition());
        solarSystemPanel.setWidgetPosition(planetInfoPopUp, planetPanelPosition.getX(), planetPanelPosition.getY());
    }

    private void removePlanetPopUp() {
        solarSystemPanel.remove(planetInfoPopUp);
        planetInfoPopUp = null;
    }

    private void setupSplashText(String text) {
        Label loadingLabel = new Label(text);
        loadingLabel.getElement().getStyle().setProperty("display", "table-cell");
        loadingLabel.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
        loadingLabel.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        loadingLabel.getElement().getStyle().setFontSize(20, Style.Unit.PX);
        solarSystemPanel.add(loadingLabel);
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
}
