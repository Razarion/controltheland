package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:29:18
 */
public class RotationControl extends DecoratorPanel {
    private static final int HORIZONTAL_SPACING = 5;
    private static final int VERTICAL_SPACING = 5;
    private int currentImage = 1;
    private BoundingBox boundingBox;
    private ItemTypeView itemTypeView;
    private HTML stepLabel;
    private Timer timer;
    private CheckBox clockwise;
    private int timerScheduleRepeating = 100;


    public RotationControl(BoundingBox boundingBox, ItemTypeView itemTypeView) {
        this.boundingBox = boundingBox;
        this.itemTypeView = itemTypeView;
        VerticalPanel verticalPanel = new VerticalPanel();
        setWidget(verticalPanel);
        verticalPanel.setSpacing(VERTICAL_SPACING);
        setupSingleStep(verticalPanel);
        setupAutoRotation(verticalPanel);
        update();
    }

    private void setupSingleStep(VerticalPanel verticalPanel) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing(HORIZONTAL_SPACING);
        hPanel.add(new Button("Left", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                stopTimer();
                nextImage();
            }
        }));
        stepLabel = new HTML();
        hPanel.add(stepLabel);
        hPanel.add(new Button("Right", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                stopTimer();
                previousImage();
            }
        }));
        verticalPanel.add(hPanel);
    }

    private void setupAutoRotation(VerticalPanel verticalPanel) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing(HORIZONTAL_SPACING);
        hPanel.add(new Button("Rotate", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                stopTimer();
                timer = new Timer() {

                    @Override
                    public void run() {
                        if (clockwise.getValue()) {
                            previousImage();
                        } else {
                            nextImage();
                        }
                    }
                };
                timer.scheduleRepeating(timerScheduleRepeating);
            }
        }));
        hPanel.add(new Button("Faster", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (timer != null) {
                    timerScheduleRepeating -= 20;
                    if (timerScheduleRepeating < 20) {
                        timerScheduleRepeating = 20;
                    }
                    timer.scheduleRepeating(timerScheduleRepeating);
                }
            }
        }));
        hPanel.add(new Button("Slower", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (timer != null) {
                    timerScheduleRepeating += 20;
                    if (timerScheduleRepeating > 2000) {
                        timerScheduleRepeating = 2000;
                    }
                    timer.scheduleRepeating(timerScheduleRepeating);
                }
            }
        }));
        clockwise = new CheckBox("Clockwise");
        hPanel.add(clockwise);
        verticalPanel.add(hPanel);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void nextImage() {
        currentImage++;
        if (currentImage > boundingBox.getImageCount()) {
            currentImage = 1;
        }
        update();
    }

    private void previousImage() {
        currentImage--;
        if (currentImage < 1) {
            currentImage = boundingBox.getImageCount();
        }
        update();
    }

    public void update() {
        int grad = (int) ((double) (currentImage - 1) / (double) boundingBox.getImageCount() * 360.0);
        stepLabel.setHTML(currentImage + " of " + boundingBox.getImageCount() + " (" + grad + "&deg;)");
        itemTypeView.draw(currentImage - 1);
    }

}
