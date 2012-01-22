package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.LevelScope;
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
    private Label level;
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
        // Mission
        mission = new HTML();
        mission.setTitle(ToolTips.TOOL_TIP_LEVEL_TARGET);
        verticalPanel.add(mission);

        Grid grid = new Grid(5, 2);
        verticalPanel.add(grid);
        grid.setWidget(0, 0, mission);
        grid.getCellFormatter().getElement(0, 0).setAttribute("colspan", "2");
        grid.getElement().getStyle().setColor("#C2D7EC");
        // Level
        Image image = ImageHandler.getIcon16("medal");
        image.setTitle(ToolTips.TOOL_TIP_LEVEL);
        grid.setWidget(1, 0, image);
        level = new Label();
        level.setTitle(ToolTips.TOOL_TIP_LEVEL);
        grid.setWidget(1, 1, level);
        // Money
        image = ImageHandler.getIcon16("money");
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
        money.setText(Integer.toString((int) Math.round(accountBalance)));
    }

    public void setLevel(LevelScope levelScope) {
        this.level.setText("??????"); // TODO
        // TODO mission.setHTML("??????" + " " + CmsUtil.getUrl4LevelPage(levelScope, "More")); // TODO Link to where
        mission.setHTML("??????"); // TODO Remove
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
}
