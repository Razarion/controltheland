package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.common.LevelScope;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class SideCockpitRealGame extends Composite {

    private static SideCockpitRealGameUiBinder uiBinder = GWT.create(SideCockpitRealGameUiBinder.class);
    @UiField
    InlineLabel planetNameLabel;
    @UiField
    InlineLabel levelLabel;
    @UiField
    InlineLabel xpLabel;

    interface SideCockpitRealGameUiBinder extends UiBinder<Widget, SideCockpitRealGame> {
    }

    public SideCockpitRealGame() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setLevel(LevelScope levelScope) {
        levelLabel.setText(Integer.toString(levelScope.getNumber()));
    }

    public void setXp(int xp, int xp2LevelUp) {
        xpLabel.setText(xp + " / " + xp2LevelUp);
    }

    public void setPlanetName(String planetName) {
        planetNameLabel.setText(planetName);
    }

}
