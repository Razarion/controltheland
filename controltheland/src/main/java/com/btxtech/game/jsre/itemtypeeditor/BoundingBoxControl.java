package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 16:32:21
 */
public class BoundingBoxControl extends DecoratorPanel {
    private Index middle;
    private Index start;
    private int width;
    private int height;
    private RotationControl rotationControl;

    public BoundingBoxControl(Index middle, Index start, int width, int height) {
        this.middle = middle;
        this.start = start;
        this.width = width;
        this.height = height;

        setupControls();
    }

    private void setupControls() {
        FlexTable flexTable = new FlexTable();
        flexTable.setCellSpacing(6);
        flexTable.setText(0, 0, "Middle X");
        flexTable.setWidget(0, 1, createIntegerBox(middle.getX(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> value) {
                middle.setX(value.getValue());
                rotationControl.update();
            }
        }));
        flexTable.setText(1, 0, "Middle Y");
        flexTable.setWidget(1, 1, createIntegerBox(middle.getX(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> value) {
                middle.setY(value.getValue());
                rotationControl.update();
            }
        }));
        flexTable.setText(2, 0, "Start X");
        flexTable.setWidget(2, 1, createIntegerBox(start.getX(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> value) {
                start.setX(value.getValue());
                rotationControl.update();
            }
        }));
        flexTable.setText(3, 0, "Start Y");
        flexTable.setWidget(3, 1, createIntegerBox(start.getX(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> value) {
                start.setY(value.getValue());
                rotationControl.update();
            }
        }));
        flexTable.setText(4, 0, "Width");
        flexTable.setWidget(4, 1, createIntegerBox(width, new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> value) {
                width = value.getValue();
                rotationControl.update();
            }
        }));
        flexTable.setText(5, 0, "Height");
        flexTable.setWidget(5, 1, createIntegerBox(height, new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> value) {
                height = value.getValue();
                rotationControl.update();
            }
        }));
        setWidget(flexTable);
    }

    public void setRotationControl(RotationControl rotationControl) {
        this.rotationControl = rotationControl;
    }

    private Widget createIntegerBox(int value, ValueChangeHandler<Integer> changeHandler) {
        IntegerBox integerBox = new IntegerBox();
        integerBox.setValue(value);
        integerBox.addValueChangeHandler(changeHandler);
        return integerBox;
    }

    public void boundingBox(int imageNr, int imageCount, Index offset, Context2d context2d) {
        double angel = (double) imageNr * 2.0 * Math.PI / (double) imageCount;
        double sin = Math.sin(angel);
        double cos = Math.cos(angel);
        Index boundingBox1 = offset.add(start);
        Index boundingBox2 = boundingBox1.add(new Index(0, height));
        Index boundingBox3 = boundingBox1.add(new Index(width, height));
        Index boundingBox4 = boundingBox1.add(new Index(width, 0));

        Index absoluteMiddle = middle.add(offset);
        Index rotBb1 = boundingBox1.rotateCounterClock(absoluteMiddle, sin, cos);
        Index rotBb2 = boundingBox2.rotateCounterClock(absoluteMiddle, sin, cos);
        Index rotBb3 = boundingBox3.rotateCounterClock(absoluteMiddle, sin, cos);
        Index rotBb4 = boundingBox4.rotateCounterClock(absoluteMiddle, sin, cos);
        context2d.save();
        context2d.beginPath();
        context2d.moveTo(rotBb1.getX(), rotBb1.getY());
        context2d.lineTo(rotBb2.getX(), rotBb2.getY());
        context2d.lineTo(rotBb3.getX(), rotBb3.getY());
        context2d.lineTo(rotBb4.getX(), rotBb4.getY());
        context2d.closePath();
        context2d.stroke();
        context2d.restore();
    }
}
