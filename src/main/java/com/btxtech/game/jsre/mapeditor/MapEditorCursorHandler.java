package com.btxtech.game.jsre.mapeditor;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;

/**
 * User: beat
 * Date: 17.09.12
 * Time: 20:53
 */
public class MapEditorCursorHandler {
    private Canvas canvas;

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setSurfaceCursor(EditMode editMode) {
        if (editMode.isNr() && editMode.isEr()) {
            setCursor(Style.Cursor.NE_RESIZE);
        } else if (editMode.isEr() && editMode.isSr()) {
            setCursor(Style.Cursor.SE_RESIZE);
        } else if (editMode.isSr() && editMode.isWr()) {
            setCursor(Style.Cursor.SW_RESIZE);
        } else if (editMode.isWr() && editMode.isNr()) {
            setCursor(Style.Cursor.NW_RESIZE);
        } else if (editMode.isNr()) {
            setCursor(Style.Cursor.N_RESIZE);
        } else if (editMode.isEr()) {
            setCursor(Style.Cursor.E_RESIZE);
        } else if (editMode.isSr()) {
            setCursor(Style.Cursor.S_RESIZE);
        } else if (editMode.isWr()) {
            setCursor(Style.Cursor.W_RESIZE);
        } else {
            setCursor(Style.Cursor.MOVE);
        }
    }

    private void setCursor(Style.Cursor cursor) {
        canvas.getElement().getStyle().setCursor(cursor);
    }

    public void clearCursor() {
        canvas.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
    }
}
