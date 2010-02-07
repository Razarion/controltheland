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

package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncResourceItemView;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: May 20, 2009
 * Time: 7:50:02 PM
 */
public class CockpitPanel extends TopMapPanel implements SelectionListener {
    private HorizontalPanel detailPanel;
    private BuildupItemPanel buildupItemPanel;

    @Override
    protected Widget createBody() {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        horizontalPanel.setSpacing(10);
        // SelectionPanel
        horizontalPanel.add(new SelectionPanel());
        // Detail Panel
        detailPanel = new HorizontalPanel();
        horizontalPanel.add(detailPanel);
        // Build up panel
        buildupItemPanel = new BuildupItemPanel();
        horizontalPanel.add(buildupItemPanel);

        SelectionHandler.getInstance().addSelectionListener(this);
        setVisible(false);
        return horizontalPanel;
    }

    public void onSelectionCleared() {
        detailPanel.clear();
        setVisible(false);
    }

    public void onTargetSelectionChanged(ClientSyncItemView selection) {
        detailPanel.clear();
        if (selection instanceof ClientSyncBaseItemView) {
            StringBuilder builder = new StringBuilder();
            builder.append("This item belongs to <b>");
            builder.append(((ClientSyncBaseItemView) selection).getSyncBaseItem().getBase().getName());
            builder.append("</b>. This is your enemy!<br/></b>Attack it!</b>");
            setupDescrBox(builder.toString());
        } else if (selection instanceof ClientSyncResourceItemView) {
            setupDescrBox(selection.getSyncItem().getItemType().getDescription());
        } else {
            throw new IllegalArgumentException(this + " can not set details for: " + selection);
        }
        setVisible(true);
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        detailPanel.clear();
        if (selectedGroup.count() == 1) {
            setupDescrBox(selectedGroup.getFirst().getSyncItem().getItemType().getDescription());
        } else if (selectedGroup.canAttack()) {
            setupDescrBox(selectedGroup.getFirst().getSyncItem().getItemType().getDescription());
        } else if (selectedGroup.canCollect()) {
            setupDescrBox(selectedGroup.getFirst().getSyncItem().getItemType().getDescription());
        }
        setVisible(true);
    }

    private void setupDescrBox(String descr) {
        HTML label = new HTML(descr);
        label.setWidth("100px");
        label.getElement().getStyle().setColor("darkorange");
        detailPanel.add(label);
    }

    public BuildupItemPanel getBuildupItemPanel() {
        return buildupItemPanel;
    }
}