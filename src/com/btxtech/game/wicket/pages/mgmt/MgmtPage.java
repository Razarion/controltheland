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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.mgmt.tracking.UserTracking;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:37:14 PM
 */
public class MgmtPage extends WebPage {
    private static final ArrayList<LinkAndName> toolPages;
    @SpringBean
    private MgmtService mgmtService;

    public MgmtPage() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
        add(new Label("sysstart", simpleDateFormat.format(mgmtService.getStartTime())));
        add(new Label("systemTime", simpleDateFormat.format(new Date())));
        Runtime runtime = Runtime.getRuntime();
        add(new Label("usedMem", Long.toString(runtime.totalMemory() / 1000000L)));
        add(new Label("maxMem", Long.toString(runtime.maxMemory() / 1000000L)));

        setupToolList();
    }

    private void setupToolList() {
        ListView<LinkAndName> tools = new ListView<LinkAndName>("toolLinkList", toolPages) {
            @Override
            protected void populateItem(final ListItem<LinkAndName> listItem) {
                Link link = new Link("toolLink"){

                    @Override
                    public void onClick() {
                        setResponsePage(listItem.getModelObject().getClazz());
                    }
                };
                listItem.add(link);
                link.add(new Label("toolLinkName", listItem.getModelObject().getName()));
            }
        };
        add(tools);
    }

    static class LinkAndName implements Serializable {
        private String name;
        private Class clazz;

        LinkAndName(String name, Class clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public String getName() {
            return name;
        }

        public Class getClazz() {
            return clazz;
        }
    }

    static {
        toolPages = new ArrayList<LinkAndName>();
        toolPages.add(new LinkAndName("DB View", DbView.class));
        toolPages.add(new LinkAndName("Log View", LogView.class));
        toolPages.add(new LinkAndName("Map Editor", TerrainFieldEditor.class));
        toolPages.add(new LinkAndName("Terrain Tile Editor", TerrainTileEditor.class));
        toolPages.add(new LinkAndName("Backup/Restore", BackupRestore.class));
        toolPages.add(new LinkAndName("Item Type Editpr", ItemTypeTable.class));
        toolPages.add(new LinkAndName("Market", ItemTypeAccessEntryEditor.class));
        toolPages.add(new LinkAndName("User Tracking", UserTracking.class));
        toolPages.add(new LinkAndName("Pathfinding", Pathfinding.class));
    }
}
