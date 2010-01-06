/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.mapeditor;

import com.google.gwt.user.client.ui.FlexTable;
import java.util.List;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 9:30:48 PM
 */
public class TileSelector extends FlexTable {
    private TileSelectorItem selection;

    public TileSelector() {
        setWidth("64px");
        setCellSpacing(5);
        setCellPadding(3);
        addStyleName("tile-selector");
    }

    public void setupTiles(List<Integer> tileIds) {
        for (Integer tileId : tileIds) {
            int numRows = getRowCount();
            setWidget(numRows, 0, new TileSelectorItem(this, tileId));
        }
    }

    public void onSelectionChanged(TileSelectorItem newSelection) {
        if (selection == newSelection) {
            return;
        }
        if (selection != null) {
            selection.setSelected(false);
        }

        selection = newSelection;
        selection.setSelected(true);
    }

    public int getSelection() {
        if(selection == null) {
            return -1;
        }
        return selection.getTileId();
    }
}
