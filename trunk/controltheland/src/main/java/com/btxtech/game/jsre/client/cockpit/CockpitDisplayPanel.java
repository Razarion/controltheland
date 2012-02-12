package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 07.11.2011
 * Time: 18:15:38
 */
public class CockpitDisplayPanel extends AbstractControlPanel {
    private Label money;
    private HTML mission;
    private LevelPanel levelPanel;
    private Label itemLimit;
    private Label energy;

    public CockpitDisplayPanel(int width) {
        super(width);
        setup();
    }

    @Override
    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setHeight("100%");

        Grid grid = new Grid(5, 2);
        verticalPanel.add(grid);
        // Mission
        mission = new HTML();
        mission.setTitle(ToolTips.TOOL_TIP_MISSION);
        mission.setHTML("??????");
        verticalPanel.add(mission);
        grid.setWidget(0, 0, mission);
        grid.getCellFormatter().getElement(0, 0).setAttribute("colspan", "2");
        grid.getElement().getStyle().setColor("#C2D7EC");
        // Level
        levelPanel = new LevelPanel();
        grid.setWidget(1, 0, levelPanel);
        grid.getCellFormatter().getElement(1, 0).setAttribute("colspan", "2");
        // Money
        Image image = ImageHandler.getIcon16("money");
        image.setTitle(ToolTips.TOOL_TIP_MONEY);
        grid.setWidget(2, 0, image);
        money = new Label();
        money.setTitle(ToolTips.TOOL_TIP_MONEY);
        grid.setWidget(2, 1, money);
        // Item Limits
        image = ImageHandler.getIcon16("house");
        image.setTitle(ToolTips.TOOL_TIP_UNITS);
        grid.setWidget(3, 0, image);
        itemLimit = new Label();
        itemLimit.setTitle(ToolTips.TOOL_TIP_UNITS);
        grid.setWidget(3, 1, itemLimit);
        // Energy
        image = ImageHandler.getIcon16("energy");
        image.setTitle(ToolTips.TOOL_TIP_ENERGY);
        grid.setWidget(4, 0, image);
        energy = new Label("0/0");
        energy.setTitle(ToolTips.TOOL_TIP_ENERGY);
        grid.setWidget(4, 1, energy);

        return verticalPanel;
    }

    public void updateMoney(double accountBalance) {
        if (accountBalance < 0) {
            money.setText("0");
        } else {
            money.setText(Integer.toString((int) accountBalance));
        }
    }

    public void updateItemLimit() {
        StringBuilder builder = new StringBuilder();
        builder.append(ItemContainer.getInstance().getOwnItemCount());
        builder.append("/");
        builder.append(ClientBase.getInstance().getHouseSpace() + ClientLevelHandler.getInstance().getLevelScope().getHouseSpace());
        itemLimit.setText(builder.toString());
    }

    public void updateEnergy(int generating, int consuming) {
        energy.setText(Integer.toString(consuming) + "/" + Integer.toString(generating));
    }

    public LevelPanel getLevelPanel() {
        return levelPanel;
    }

    public void setMissionHtml(String html) {
        mission.setHTML(html);
    }
}
