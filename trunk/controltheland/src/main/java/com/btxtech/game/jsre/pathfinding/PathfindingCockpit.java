package com.btxtech.game.jsre.pathfinding;

import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.cockpit.radar.MiniTerrain;
import com.btxtech.game.jsre.client.cockpit.radar.ScaleStep;
import com.btxtech.game.jsre.client.common.Index;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 02.11.2011
 * Time: 16:55:55
 */
public class PathfindingCockpit extends TopMapPanel {
    private Label mouseX;
    private Label mouseY;
    private TextBox startBoxX;
    private TextBox startBoxY;
    private TextBox destinationBoxX;
    private TextBox destinationBoxY;
    private PathMiniMap pathMiniMap;
    private MiniTerrain miniTerrain;
    private FlexTable pathTable;
    private Label zoom;

    public PathfindingCockpit(PathMiniMap pathMiniMap, MiniTerrain miniTerrain) {
        this.pathMiniMap = pathMiniMap;
        this.miniTerrain = miniTerrain;
        pathMiniMap.setPathfindingCockpit(this);
    }

    @Override
    protected Widget createBody() {
        getElement().getStyle().clearBackgroundImage();
        getElement().getStyle().setBackgroundColor("#AAAAAA");

        FlexTable flexTable = new FlexTable();

        flexTable.setText(1, 1, "Mouse X");
        mouseX = new Label();
        flexTable.setWidget(1, 2, mouseX);

        flexTable.setText(2, 1, "Mouse Y");
        mouseY = new Label();
        flexTable.setWidget(2, 2, mouseY);

        flexTable.setText(3, 1, "Start X");
        startBoxX = new TextBox();
        flexTable.setWidget(3, 2, startBoxX);

        flexTable.setText(4, 1, "Start Y");
        startBoxY = new TextBox();
        flexTable.setWidget(4, 2, startBoxY);

        flexTable.setText(5, 1, "Destination X");
        destinationBoxX = new TextBox();
        flexTable.setWidget(5, 2, destinationBoxX);

        flexTable.setText(6, 1, "Destination Y");
        destinationBoxY = new TextBox();
        flexTable.setWidget(6, 2, destinationBoxY);

        flexTable.setText(7, 1, "ItemType Id");
        final IntegerBox itemTypeBox = new IntegerBox();
        HorizontalPanel itemTypePanel = new HorizontalPanel();
        itemTypePanel.add(itemTypeBox);
        itemTypePanel.add(new Button("Load", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                pathMiniMap.loadBoundingBox(itemTypeBox.getValue());
            }
        }));
        flexTable.setWidget(7, 2, itemTypePanel);

        flexTable.setWidget(8, 2, new Button("Go!", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                pathMiniMap.findPath(getIndex(startBoxX, startBoxY), getIndex(destinationBoxX, destinationBoxY));
            }
        }));

        flexTable.setText(9, 1, "Zoom");
        HorizontalPanel zoomPanel = new HorizontalPanel();
        zoomPanel.add(new Button("+", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ScaleStep newScale = ScaleStep.zoomIn(miniTerrain.getScale());
                if (newScale != null) {
                    miniTerrain.setScale(newScale);
                    pathMiniMap.setScale(newScale);
                    updateZoomValue();
                }
            }
        }));
        zoomPanel.add(new Button("-", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ScaleStep newScale = ScaleStep.zoomOut(miniTerrain.getScale());
                if (newScale != null) {
                    miniTerrain.setScale(newScale);
                    pathMiniMap.setScale(newScale);
                    updateZoomValue();
                }
            }
        }));
        zoom = new Label();
        zoomPanel.add(zoom);
        flexTable.setWidget(9, 2, zoomPanel);

        Grid scrollPanel = new Grid(3, 3);
        scrollPanel.setWidget(0, 1, new Button("Up", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveViewRect(0, -100);
            }
        }));
        scrollPanel.setWidget(1, 0, new Button("Left", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveViewRect(-100, 0);
            }
        }));
        scrollPanel.setWidget(1, 2, new Button("Right", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveViewRect(100, 0);
            }
        }));
        scrollPanel.setWidget(2, 1, new Button("Down", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveViewRect(0, 100);
            }
        }));
        flexTable.setText(10, 1, "Scroll");
        startBoxX = new TextBox();
        flexTable.setWidget(10, 2, scrollPanel);

        pathTable = new FlexTable();
        flexTable.setWidget(11, 1, pathTable);
        flexTable.getFlexCellFormatter().setColSpan(11, 1, 2);

        return flexTable;
    }


    private void moveViewRect(int deltaX, int deltaY) {
        miniTerrain.setAbsoluteViewRect(miniTerrain.getViewOrigin().add(deltaX, deltaY));
        pathMiniMap.setAbsoluteViewRect(pathMiniMap.getViewOrigin().add(deltaX, deltaY));
    }


    private Index getIndex(TextBox textBoxX, TextBox textBoxY) {
        return new Index(Integer.parseInt(textBoxX.getText().trim()), Integer.parseInt(textBoxY.getText().trim()));
    }

    public void showMousePosition(int absX, int absY) {
        mouseX.setText(Integer.toString(absX));
        mouseY.setText(Integer.toString(absY));
    }

    public void clearPathTable() {
        pathTable.removeAllRows();
    }

    public void addPathTable(Index index) {
        int row = pathTable.getRowCount() + 1;
        pathTable.setWidget(row, 1, new Label(Integer.toString(index.getX())));
        pathTable.setWidget(row, 2, new Label(Integer.toString(index.getY())));
    }

    public void updateZoomValue() {
        zoom.setText(Double.toString(miniTerrain.getScale().getZoom()));
    }
}
