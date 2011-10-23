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
import com.btxtech.game.wicket.pages.mgmt.cms.Cms;
import com.btxtech.game.wicket.pages.mgmt.tracking.SessionTable;
import com.btxtech.game.wicket.pages.mgmt.tutorial.TutorialTable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:37:14 PM
 */
public class MgmtPage extends MgmtWebPage {
    private static final ArrayList<LinkAndName> toolPages;
    @SpringBean
    private MgmtService mgmtService;

    public MgmtPage() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
        add(new Label("sysstart", simpleDateFormat.format(mgmtService.getStartTime())));
        add(new Label("systemTime", simpleDateFormat.format(new Date())));
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        add(new Label("heapInit", bytesToMega(heapMemoryUsage.getInit())));
        add(new Label("heapUsed", bytesToMega(heapMemoryUsage.getUsed())));
        add(new Label("heapCommitted", bytesToMega(heapMemoryUsage.getCommitted())));
        add(new Label("heapMax", bytesToMega(heapMemoryUsage.getMax())));
        MemoryUsage noHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        add(new Label("noHeapInit", bytesToMega(noHeapMemoryUsage.getInit())));
        add(new Label("noHeapUsed", bytesToMega(noHeapMemoryUsage.getUsed())));
        add(new Label("noHeapCommitted", bytesToMega(noHeapMemoryUsage.getCommitted())));
        add(new Label("noHeapMax", bytesToMega(noHeapMemoryUsage.getMax())));

        Form form = new Form("gcForm");
        add(form);
        form.add(new Button("runGc") {

            @Override
            public void onSubmit() {
                Runtime.getRuntime().gc();
                setResponsePage(MgmtPage.class);
            }
        });

        setupToolList();
    }

    private String bytesToMega(long bytes) {
        double value = (double) bytes / 1000000.0;
        return String.format("%.2f", value);
    }

    private void setupToolList() {
        ListView<LinkAndName> tools = new ListView<LinkAndName>("toolLinkList", toolPages) {
            @Override
            protected void populateItem(final ListItem<LinkAndName> listItem) {
                Link link = new Link("toolLink") {

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
        private Class<? extends MgmtWebPage> clazz;

        LinkAndName(String name, Class<? extends MgmtWebPage> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public String getName() {
            return name;
        }

        public Class<? extends MgmtWebPage> getClazz() {
            return clazz;
        }
    }

    static {
        toolPages = new ArrayList<LinkAndName>();
        toolPages.add(new LinkAndName("Startup", Startup.class));
        toolPages.add(new LinkAndName("CMS", CmsEditor.class));
        toolPages.add(new LinkAndName("DB View", DbView.class));
        toolPages.add(new LinkAndName("Log View", LogView.class));
        toolPages.add(new LinkAndName("Map Editor", TerrainSettingsTable.class));
        toolPages.add(new LinkAndName("Terrain Tile Editor", TerrainTileEditor.class));
        toolPages.add(new LinkAndName("Backup/Restore", BackupRestore.class));
        toolPages.add(new LinkAndName("Item Type Editpr", ItemTypeTable.class));
        toolPages.add(new LinkAndName("Attack Matrix", AttackMatrix.class));
        toolPages.add(new LinkAndName("Resource", ResourceEditor.class));
        toolPages.add(new LinkAndName("Market", MarketEntryEditor.class));
        toolPages.add(new LinkAndName("User Tracking", SessionTable.class));
        toolPages.add(new LinkAndName("Bases", BasesTable.class));
        toolPages.add(new LinkAndName("XP Settings", XpSettingsEditor.class));
        toolPages.add(new LinkAndName("Levels", DbLevelTable.class));
        toolPages.add(new LinkAndName("Bot editor", BotTable.class));
        toolPages.add(new LinkAndName("TerritoryEditor", TerritoryEditor.class));
        toolPages.add(new LinkAndName("Tutorial", TutorialTable.class));
        toolPages.add(new LinkAndName("User States", UserStateTable.class));
        toolPages.add(new LinkAndName("New CMS", Cms.class));

    }
}
