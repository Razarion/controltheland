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

import com.btxtech.game.jsre.client.TopMapPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 9:30:48 PM
 */
public class TileSelector  extends TopMapPanel {
    private TileSelectorItem selection;
    private FlexTable flexTable;

    @Override
    protected Widget createBody() {
        flexTable = new FlexTable();
        flexTable.setCellSpacing(5);
        flexTable.setCellPadding(3);
        flexTable.addStyleName("tile-selector");
        return flexTable;
    }

    public void setupTiles(Collection<Integer> tileIds) {
        for (Integer tileId : tileIds) {
            int numRows = flexTable.getRowCount();
            flexTable.setWidget(numRows, 0, new TileSelectorItem(this, tileId));
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
        return selection.getImageId();
    }
}
