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

package com.btxtech.game.services.mgmt;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 2:11:28 PM
 */
public class DbViewDTO {
    private List<String> header = new ArrayList<String>();
    private List<List<String>> rows = new ArrayList<List<String>>();
    private List<String> currentRow;

    public List<String> getHeader() {
        return header;
    }

    public void addHeader(String columnName) {
       header.add(columnName); 
    }

    public void newRow() {
        currentRow = new ArrayList<String>();
        rows.add(currentRow);
    }

    public void addDataCell(String data) {
        currentRow.add(data);
    }

    public List<List<String>> getRows() {
        return rows;
    }
}
