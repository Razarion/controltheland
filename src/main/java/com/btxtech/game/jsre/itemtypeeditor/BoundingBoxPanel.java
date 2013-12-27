package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeEditorModel.UpdateListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class BoundingBoxPanel extends Composite implements UpdateListener {

    private static BoundingBoxPanelUiBinder uiBinder = GWT.create(BoundingBoxPanelUiBinder.class);
    @UiField
    Label imageWidthLabel;
    @UiField
    Label imageHeightLabel;
    @UiField
    Label currentAngelIndex;
    @UiField
    Button left;
    @UiField
    Button right;
    @UiField
    IntegerBox radiusField;
    @UiField
    Button applyButton;
    @UiField
    IntegerBox angels;
    @UiField
    Button radiusPlus;
    @UiField
    Button radiusMinus;
    @UiField
    IntegerBox currentAngelField;
    @UiField
    Button currentAngelPlus;
    @UiField
    Button currentAngelMinus;
    @UiField
    SimplePanel itemTypeView;

    interface BoundingBoxPanelUiBinder extends UiBinder<Widget, BoundingBoxPanel> {
    }

    public BoundingBoxPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        ItemTypeEditorModel.getInstance().addUpdateListener(this);
        BoundingBoxVisualisation boundingBoxVisualisation = new BoundingBoxVisualisation();
        itemTypeView.setWidget(boundingBoxVisualisation.getCanvas());
        ItemTypeEditorModel.getInstance().addUpdateListener(boundingBoxVisualisation);
    }

    @UiHandler("applyButton")
    void onApplyButtonClick(ClickEvent event) {
        int angelCount = angels.getValue();
        if (angelCount < 1) {
            angelCount = 1;
        }
        ItemTypeEditorModel.getInstance().setNewAngels(angelCount);
    }

    @UiHandler("radiusPlus")
    void onWidthPlusClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().getBoundingBox().setRadius(ItemTypeEditorModel.getInstance().getBoundingBox().getRadius() + 1);
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("radiusMinus")
    void onWidthMinusClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().getBoundingBox().setRadius(ItemTypeEditorModel.getInstance().getBoundingBox().getRadius() - 1);
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("currentAngelPlus")
    void onCurrentAngelPlusClick(ClickEvent event) {
        int grad = (int) Math.round(MathHelper.radToGrad(ItemTypeEditorModel.getInstance().getBoundingBox().getAngels()[ItemTypeEditorModel.getInstance().getCurrentAngelIndex()]));
        ItemTypeEditorModel.getInstance().getBoundingBox().getAngels()[ItemTypeEditorModel.getInstance().getCurrentAngelIndex()] = MathHelper.gradToRad(grad + 1);
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("currentAngelMinus")
    void onCurrentAngelMinusClick(ClickEvent event) {
        int grad = (int) Math.round(MathHelper.radToGrad(ItemTypeEditorModel.getInstance().getBoundingBox().getAngels()[ItemTypeEditorModel.getInstance().getCurrentAngelIndex()]));
        ItemTypeEditorModel.getInstance().getBoundingBox().getAngels()[ItemTypeEditorModel.getInstance().getCurrentAngelIndex()] = MathHelper.gradToRad(grad - 1);
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("currentAngelField")
    void onCurrentAngelFieldChange(ChangeEvent event) {
        ItemTypeEditorModel.getInstance().getBoundingBox().getAngels()[ItemTypeEditorModel.getInstance().getCurrentAngelIndex()] = MathHelper.gradToRad(currentAngelField.getValue());
        ItemTypeEditorModel.getInstance().fireUpdate();
    }

    @UiHandler("left")
    void onLeftClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().increaseCurrentAngelIndex();
    }

    @UiHandler("right")
    void onRightClick(ClickEvent event) {
        ItemTypeEditorModel.getInstance().decreaseCurrentAngelIndex();
    }

    @Override
    public void onModelUpdate() {
        angels.setValue(ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount());
        currentAngelIndex.setText((ItemTypeEditorModel.getInstance().getCurrentAngelIndex() + 1) + " of " + (ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount()));
        radiusField.setValue(ItemTypeEditorModel.getInstance().getBoundingBox().getRadius());
        currentAngelField.setValue((int) Math.round(MathHelper.radToGrad(ItemTypeEditorModel.getInstance().getBoundingBox().getAngels()[ItemTypeEditorModel.getInstance().getCurrentAngelIndex()])));
        imageWidthLabel.setText(Integer.toString(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getImageWidth()));
        imageHeightLabel.setText(Integer.toString(ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getImageHeight()));
    }
}
