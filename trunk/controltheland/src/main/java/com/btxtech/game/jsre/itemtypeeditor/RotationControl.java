package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DoubleBox;
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
    private int currentImage = 0;
    private BoundingBox boundingBox;
    private ItemTypeView itemTypeView;
    private HTML stepLabel;
    private Timer timer;
    private CheckBox clockwise;
    private int timerScheduleRepeating = 100;
    private DoubleBox doubleBox;
    private ItemTypeSimulation itemTypeSimulation;
    private MuzzleFlashControl muzzleFlashControl;


    public RotationControl(BoundingBox boundingBox, ItemTypeView itemTypeView, ItemTypeSimulation itemTypeSimulation, MuzzleFlashControl muzzleFlashControl) {
        this.boundingBox = boundingBox;
        this.itemTypeView = itemTypeView;
        this.itemTypeSimulation = itemTypeSimulation;
        this.muzzleFlashControl = muzzleFlashControl;
        VerticalPanel verticalPanel = new VerticalPanel();
        setWidget(verticalPanel);
        verticalPanel.setSpacing(VERTICAL_SPACING);
        setupSingleStep(verticalPanel);
        setupAutoRotation(verticalPanel);
        setupAngel(verticalPanel);
        setupMove(verticalPanel);
        update();
    }

    private void setupMove(VerticalPanel verticalPanel) {
        CheckBox checkBox = new CheckBox("Move");
        verticalPanel.add(checkBox);
        checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                itemTypeSimulation.doMove(booleanValueChangeEvent.getValue());
            }
        });
    }

    private void setupAngel(VerticalPanel verticalPanel) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        doubleBox = new DoubleBox();
        doubleBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> doubleValueChangeEvent) {
                boundingBox.getAngels()[currentImage] = MathHelper.gradToRad(doubleValueChangeEvent.getValue());
            }
        });
        horizontalPanel.add(doubleBox);
        horizontalPanel.add(new Button("+", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doubleBox.setValue(doubleBox.getValue() + 1, true);
                update();
            }
        }));
        horizontalPanel.add(new Button("-", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doubleBox.setValue(doubleBox.getValue() - 1, true);
                update();
            }
        }));
        verticalPanel.add(horizontalPanel);
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
                timer = new TimerPerfmon(PerfmonEnum.IGNORE) {

                    @Override
                    public void runPerfmon() {
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
        if (currentImage > boundingBox.getAngels().length - 1) {
            currentImage = 0;
        }
        update();
    }

    private void previousImage() {
        currentImage--;
        if (currentImage < 0) {
            currentImage = boundingBox.getAngels().length - 1;
        }
        update();
    }

    public void update() {
        stepLabel.setHTML((currentImage + 1) + " of " + boundingBox.getAngels().length);
        doubleBox.setValue(MathHelper.radToGrad(boundingBox.imageNumberToAngel(currentImage)));
        itemTypeView.draw(currentImage);
        itemTypeSimulation.onImageChanged(currentImage);
        muzzleFlashControl.onImageChanged(currentImage);
    }

}
