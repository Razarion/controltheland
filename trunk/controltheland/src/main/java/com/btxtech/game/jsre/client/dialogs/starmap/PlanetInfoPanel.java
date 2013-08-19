package com.btxtech.game.jsre.client.dialogs.starmap;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Hyperlink;

public class PlanetInfoPanel extends Composite {
    private static PlanetInfoPanelUiBinder uiBinder = GWT.create(PlanetInfoPanelUiBinder.class);
    @UiField
    Label planetName;
    @UiField
    Label levelLabel;
    @UiField
    Label sizeLabel;
    @UiField
    Label basesLabel;
    @UiField
    Label botLabel;
    @UiField
    Button landingButton;
    @UiField
    Label wrongLevelLabel;
    @UiField Hyperlink hyperlink;

    interface PlanetInfoPanelUiBinder extends UiBinder<Widget, PlanetInfoPanel> {
    }

    public PlanetInfoPanel(StarMapPlanetInfo starMapPlanetInfo) {
        initWidget(uiBinder.createAndBindUi(this));
        planetName.setText(starMapPlanetInfo.getPlanetLiteInfo().getName());
        levelLabel.setText(Integer.toString(starMapPlanetInfo.getMinLevel()));
        sizeLabel.setText(Integer.toString(starMapPlanetInfo.getSize()));
        basesLabel.setText(Integer.toString(starMapPlanetInfo.getBases()));
        botLabel.setText(Integer.toString(starMapPlanetInfo.getBots()));
        if (ClientLevelHandler.getInstance().getLevelScope().getNumber() >= starMapPlanetInfo.getMinLevel()) {
            wrongLevelLabel.setVisible(false);
            landingButton.setVisible(true);
        } else {
            wrongLevelLabel.setVisible(true);
            landingButton.setVisible(false);
        }
        addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                event.stopPropagation();
            }
        }, MouseMoveEvent.getType());
    }

    @UiHandler("landingButton")
    void onLandingButtonClick(ClickEvent event) {

    }

	@UiHandler("hyperlink")
	void onHyperlinkClick(ClickEvent event) {
        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.featureComingSoon(), ClientI18nHelper.CONSTANTS.featureNextRelease()), DialogManager.Type.STACK_ABLE);
	}
}
