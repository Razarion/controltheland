package com.btxtech.game.jsre.client.utg.tip;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StorySplashPanel extends Composite {
    interface StorySplashPopupPanelUiBinder extends UiBinder<Widget, StorySplashPanel> {
    }

    private static StorySplashPopupPanelUiBinder uiBinder = GWT.create(StorySplashPopupPanelUiBinder.class);
    @UiField
    Label titleLabel;
    @UiField
    Label mainTextLabel;
    @UiField
    Label taskTextLabel;
    @UiField
    Image taskImage;

    public StorySplashPanel(AbstractSplashPopupInfo abstractSplashPopupInfo) {
        initWidget(uiBinder.createAndBindUi(this));
        titleLabel.setText(abstractSplashPopupInfo.getTitle());
        if (abstractSplashPopupInfo instanceof StorySplashPopupInfo) {
            mainTextLabel.setText(((StorySplashPopupInfo) abstractSplashPopupInfo).getStoryText());
            taskImage.setUrl("/images/tips/tipQuest.png"); // TODO put image in code (resource bundle)
        } else if (abstractSplashPopupInfo instanceof PraiseSplashPopupInfo) {
            taskTextLabel.setText(((PraiseSplashPopupInfo) abstractSplashPopupInfo).getPraiseText());
            taskImage.setUrl("/images/tips/tick.png"); // TODO put image in code (resource bundle)
        }
        // TODO prevent icon drag
        // preventEvents(this);
        // preventEvents(titleLabel);
        // preventEvents(mainTextLabel);
        // preventEvents(taskTextLabel);
        //  preventEvents(taskImage);
    }

    public void setTaskText(String taskText) {
        taskTextLabel.setText(taskText);
    }

    /*
    static void preventEvents(Widget widget) {
        widget.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.preventDefault();
                GwtCommon.preventDefault(event);
            }
        }, MouseUpEvent.getType());

        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
                // GwtCommon.preventDefault(event);
            }
        }, MouseDownEvent.getType());
    } */
}
