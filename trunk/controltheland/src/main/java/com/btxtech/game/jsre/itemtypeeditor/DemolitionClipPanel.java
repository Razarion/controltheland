package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeEditorModel.UpdateListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DemolitionClipPanel extends Composite implements UpdateListener {
    private static DemolitionClipPanelUiBinder uiBinder = GWT.create(DemolitionClipPanelUiBinder.class);
    @UiField
    CaptionPanel clipPositionPanel;
    @UiField
    Button createClipButton;
    @UiField
    Button deleteClipButton;
    @UiField
    Label currentClipLabel;
    @UiField
    Button previousClipButton;
    @UiField
    Button nextClipButton;
    private int currentClip = 0;

    interface DemolitionClipPanelUiBinder extends UiBinder<Widget, DemolitionClipPanel> {
    }

    public DemolitionClipPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
    }

    @Override
    public void onModelUpdate() {
        if (ItemTypeEditorModel.getInstance().getCurrentDemolitionClipSize() > 0) {
            currentClipLabel.setText((currentClip + 1) + "/" + ItemTypeEditorModel.getInstance().getCurrentDemolitionClipSize());
            ItemClipPosition itemClipPosition = ItemTypeEditorModel.getInstance().getCurrentDemolitionItemClipPosition(currentClip);
            if (itemClipPosition != null) {
                clipPositionPanel.setContentWidget(new DemolitionClipPosition(itemClipPosition));
            } else {
                clipPositionPanel.clear();
            }
            previousClipButton.setEnabled(true);
            nextClipButton.setEnabled(true);
            deleteClipButton.setEnabled(true);
        } else {
            currentClipLabel.setText("-");
            clipPositionPanel.clear();
            previousClipButton.setEnabled(false);
            nextClipButton.setEnabled(false);
            deleteClipButton.setEnabled(false);
        }
        createClipButton.setEnabled(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getDemolitionStepCount() > 0);
    }

    @UiHandler("deleteClipButton")
    void onDeleteClipButtonClick(ClickEvent event) {
        currentClip = ItemTypeEditorModel.getInstance().deleteCurrentDemolitionClip(currentClip);
        onModelUpdate();
    }

    @UiHandler("createClipButton")
    void onCreateClipButtonClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().createCurrentDemolitionClip();
        onModelUpdate();
    }

    @UiHandler("nextClipButton")
    void onNextClipButtonClick(ClickEvent event) {
        currentClip++;
        if (currentClip >= ItemTypeEditorModel.getInstance().getCurrentDemolitionClipSize()) {
            currentClip = 0;
        }
        onModelUpdate();
    }

    @UiHandler("previousClipButton")
    void onPreviousClipButtonClick(ClickEvent event) {
        currentClip--;
        if (currentClip < 0) {
            currentClip = ItemTypeEditorModel.getInstance().getCurrentDemolitionClipSize() - 1;
        }
        onModelUpdate();
    }
}
