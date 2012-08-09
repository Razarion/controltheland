package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.renderer.Renderer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemTypeEditorPanel extends Composite {
    private static ItemTypeEditorPanelUiBinder uiBinder = GWT.create(ItemTypeEditorPanelUiBinder.class);
    @UiField
    SimplePanel configurationPanel;
    @UiField
    SimplePanel simulationPanel;
    @UiField
    SimplePanel simulationControlPanel;
    @UiField
    Button saveButton;
    @UiField
    ListBox configurationSelector;

    interface ItemTypeEditorPanelUiBinder extends UiBinder<Widget, ItemTypeEditorPanel> {
    }

    public ItemTypeEditorPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        configurationSelector.addItem("Bounding Box");
        configurationSelector.addItem("Buildup");
        configurationSelector.addItem("Runtime");
        configurationSelector.addItem("Demolition");
        configurationSelector.addItem("Muzzle Flash");
        configurationSelector.setSelectedIndex(0);
        configurationPanel.setWidget(new BoundingBoxPanel());
        simulationControlPanel.setWidget(new SimulationControlPanel());
        ItemTypeEditorModel.getInstance().addLoadedListener(new ItemTypeEditorModel.LoadedListener() {
            @Override
            public void onModelLoaded() {
                simulationPanel.setWidget(MapWindow.getAbsolutePanel());
                Renderer.getInstance().start();
                MapWindow.getInstance().setMinimalSize(ItemTypeEditorModel.SIM_WIDTH, ItemTypeEditorModel.SIM_HEIGHT);
            }
        });
    }

    @UiHandler("saveButton")
    void onSaveButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().saveItemType(saveButton);
    }

    @UiHandler("configurationSelector")
    void onConfigurationSelectorChange(ChangeEvent event) {
        ItemTypeEditorModel.getInstance().clearUpdateListeners();
        switch (configurationSelector.getSelectedIndex()) {
            case 0:
                configurationPanel.setWidget(new BoundingBoxPanel());
                break;
            case 1:
                configurationPanel.setWidget(new BuildupPanel());
                break;
            case 2:
                configurationPanel.setWidget(new RuntimePanel());
                break;
            case 3:
                configurationPanel.setWidget(new DemolitionPanel());
                break;
            case 4:
                configurationPanel.setWidget(new MuzzleFlashPanel());
                break;
        }
        ItemTypeEditorModel.getInstance().fireUpdate();
    }
}
