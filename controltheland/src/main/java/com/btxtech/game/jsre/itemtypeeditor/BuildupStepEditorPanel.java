package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BuildupStep;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 15.08.2011
 * Time: 20:27:37
 */
public class BuildupStepEditorPanel extends DecoratorPanel {
    private FlexTable flexTable;
    private static BuildupStepEditorPanel staticInstance;
    private List<BuildupStep> buildupSteps;
    private FlexTable mainTable;
    private double progress = 1.0;
    private BaseItemType baseItemType;

    public BuildupStepEditorPanel(ItemType itemType, final ItemTypeSimulation itemTypeSimulation, BoundingBoxControl boundingBoxControl) {
        if (itemType instanceof BaseItemType) {
            setupBaseItemType((BaseItemType) itemType, itemTypeSimulation);
            boundingBoxControl.setBuilupEditorPanel(this);
        } else {
            setupNoBaseItemType();
        }
    }

    private void setupBaseItemType(BaseItemType baseItemType, final ItemTypeSimulation itemTypeSimulation) {
        if (baseItemType.getBuildupStep() != null) {
            buildupSteps = baseItemType.getBuildupStep();
        } else {
            buildupSteps = new ArrayList<BuildupStep>();
            baseItemType.setBuildupStep(buildupSteps);
        }
        this.baseItemType = baseItemType;
        itemTypeSimulation.setBuilupEditorPanel(this);
        mainTable = new FlexTable();
        setWidget(mainTable);

        CheckBox checkBox = new CheckBox("Show Buildup");
        mainTable.setWidget(0, 0, checkBox);
        mainTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                itemTypeSimulation.doBuildup(booleanValueChangeEvent.getValue());
            }
        });

        mainTable.setText(1, 0, "Builder Progress");
        DoubleBox builderProgress = new DoubleBox();
        builderProgress.setValue(progress);
        mainTable.setWidget(1, 1, builderProgress);
        builderProgress.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> doubleValueChangeEvent) {
                if (doubleValueChangeEvent.getValue() != null) {
                    progress = doubleValueChangeEvent.getValue();
                }
            }
        });

        mainTable.setText(2, 0, "Buildup");
        mainTable.setText(2, 1, "");

        flexTable = new FlexTable();
        flexTable.setBorderWidth(1);
        mainTable.setWidget(3, 0, flexTable);
        mainTable.getFlexCellFormatter().setColSpan(3, 0, 2);
        staticInstance = this;
        drawTable();

        flexTable.addDropHandler(new DropHandler() {
            @Override
            public void onDrop(DropEvent event) {
                event.stopPropagation();
                event.preventDefault();
                DataTransfer dataTransfer = event.getDataTransfer();
                handleDroppedData(dataTransfer);

            }
        });
        flexTable.addDragOverHandler(new DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                event.stopPropagation();
                event.preventDefault();
            }
        });
        flexTable.addDragEnterHandler(new DragEnterHandler() {
            @Override
            public void onDragEnter(DragEnterEvent event) {
                event.stopPropagation();
                event.preventDefault();
            }
        });
    }

    private void setupNoBaseItemType() {
        mainTable = new FlexTable();
        setWidget(mainTable);
        mainTable.setText(1, 0, "No Buildup because no Base Item Type");
    }

    private void onImageDropped(String imageData) {
        BuildupStep buildupStep = new BuildupStep(imageData);
        buildupSteps.add(buildupStep);
        recalculateFromAndTo();
        drawTable();
    }

    private void drawTable() {
        flexTable.removeAllRows();
        if (buildupSteps.isEmpty()) {
            flexTable.setText(0, 0, "Darg an image to here");
        } else {
            for (int i = 0; i < buildupSteps.size(); i++) {
                final int index = i;
                final BuildupStep buildupStep = buildupSteps.get(i);
                int newRowNumber = flexTable.getRowCount();
                if (buildupStep.getBase64ImageData() != null) {
                    flexTable.setWidget(newRowNumber, 0, new Image(buildupStep.getBase64ImageData()));
                } else {
                    flexTable.setWidget(newRowNumber, 0, new Image(ImageHandler.getBuildupStepImageUrl(baseItemType, buildupStep)));
                }

                DoubleBox deltaBox = new DoubleBox();
                deltaBox.setVisibleLength(3);
                deltaBox.setValue(buildupStep.getDelta());
                deltaBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Double> doubleValueChangeEvent) {
                        recalculateFromAndToAfter(doubleValueChangeEvent.getValue(), buildupStep);
                        drawTable();
                    }
                });
                flexTable.setWidget(newRowNumber, 1, deltaBox);

                if (i > 0) {
                    flexTable.setWidget(newRowNumber, 2, new Button("Up", new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            Collections.swap(buildupSteps, index, index - 1);
                            recalculateFromToOrder();
                            drawTable();
                        }
                    }));
                }
                if (i + 1 < buildupSteps.size()) {
                    flexTable.setWidget(newRowNumber, 3, new Button("Down", new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            Collections.swap(buildupSteps, index, index + 1);
                            recalculateFromToOrder();
                            drawTable();
                        }
                    }));
                }
                flexTable.setWidget(newRowNumber, 4, new Button("Delete", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        buildupSteps.remove(index);
                        recalculateFromAndTo();
                        drawTable();
                    }
                }));

            }
        }
    }

    private void recalculateFromToOrder() {
        double lastValue = 0;
        for (BuildupStep buildupStep : buildupSteps) {
            double delta = buildupStep.getDelta();
            buildupStep.setFrom(lastValue);
            lastValue += delta;
            buildupStep.setToExclusive(lastValue);
        }
    }

    private void recalculateFromAndToAfter(Double range, BuildupStep buildupStep) {
        if (range == null) {
            return;
        }
        int index = buildupSteps.indexOf(buildupStep);
        if (index < 0) {
            throw new IllegalArgumentException("Unknown BuildupStep: " + buildupStep);
        }
        double lastValue;
        if (index == 0) {
            lastValue = 0;
        } else {
            lastValue = buildupSteps.get(index - 1).getToExclusive();
        }
        buildupStep.setFrom(lastValue);
        if (lastValue + range > 1.0) {
            range = 1.0 - lastValue;
        }
        lastValue += range;
        buildupStep.setToExclusive(lastValue);
        if (buildupSteps.size() - index - 1 > 0) {
            double deltaStep = (1.0 - lastValue) / (buildupSteps.size() - index - 1);
            if (deltaStep < 0) {
                deltaStep = 0;
            }
            for (int i = index + 1; i < buildupSteps.size(); i++) {
                BuildupStep step = buildupSteps.get(i);
                step.setFrom(lastValue);
                lastValue += deltaStep;
                step.setToExclusive(lastValue);
            }
        }
    }

    private void recalculateFromAndTo() {
        int stepCount = buildupSteps.size();
        double deltaStep = 1.0 / (double) stepCount;
        double lastValue = 0;
        for (BuildupStep buildupStep : buildupSteps) {
            buildupStep.setFrom(lastValue);
            lastValue += deltaStep;
            buildupStep.setToExclusive(lastValue);
        }
    }

    public List<BuildupStep> getBuildupStepDatas() {
        return buildupSteps;
    }

    public static void onStaticImageDropped(String imageData) {
        staticInstance.onImageDropped(imageData);
    }

    private native void handleDroppedData(DataTransfer dataTransfer)  /*-{
      for (var i = 0; i < dataTransfer.files.length; i++) {
        var file = dataTransfer.files[i];
        if (!file.type.match('image.*')) {
          continue;
        }
        var reader = new FileReader();
        // Closure to capture the file information.
        reader.onload = (function(theFile) {
          return function(e) {
            // Render thumbnail.
            $entry(@com.btxtech.game.jsre.itemtypeeditor.BuildupStepEditorPanel::onStaticImageDropped(Ljava/lang/String;)(e.target.result));
          };
          })(file);
          reader.readAsDataURL(file);
      }
    }-*/;

    public void onBuildupProgress(double buildup) {
        mainTable.setText(2, 1, Double.toString(buildup));
    }

    public double getProgress() {
        return progress;
    }
}
