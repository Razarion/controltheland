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

import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.wicket.pages.mgmt.cms.Cms;
import com.btxtech.game.wicket.pages.mgmt.inventory.InventoryEditor;
import com.btxtech.game.wicket.pages.mgmt.items.ItemTypeTable;
import com.btxtech.game.wicket.pages.mgmt.level.LevelTable;
import com.btxtech.game.wicket.pages.mgmt.planet.PlanetTable;
import com.btxtech.game.wicket.pages.mgmt.tracking.*;
import com.btxtech.game.wicket.pages.mgmt.tutorial.TutorialTable;
import com.btxtech.game.wicket.pages.mgmt.usermgmt.MultiUserLevelConverter;
import com.btxtech.game.wicket.pages.mgmt.usermgmt.UserStateTable;
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
import java.util.TimeZone;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:37:14 PM
 */
public class MgmtPage extends MgmtWebPage {
    public static final ArrayList<LinkAndName> toolPages;
    @SpringBean
    private MgmtService mgmtService;

    public MgmtPage() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);
        add(new Label("sysstart", simpleDateFormat.format(mgmtService.getStartTime())));
        add(new Label("systemTime", simpleDateFormat.format(new Date())));
        add(new Label("timeZone", TimeZone.getDefault().getDisplayName() + " (" + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT) + ")"));
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
        add(new MemoryVisualisation("heapMemory", mgmtService.getHeapMemoryUsageHistory(), "Heap Memory Usage|(in Mega Bytes)"));
        add(new MemoryVisualisation("noHeapMemory", mgmtService.getNoHeapMemoryUsageHistory(), "No Heap Memory Usage|(in Mega Bytes)"));
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

    public static class LinkAndName implements Serializable {
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
        toolPages = new ArrayList<>();
        toolPages.add(new LinkAndName("Game Properties", GamePropertyEditor.class));
        toolPages.add(new LinkAndName("Online utils", OnlineUtils.class));
        toolPages.add(new LinkAndName("DB View", DbView.class));
        toolPages.add(new LinkAndName("Terrain Tile Editor", TerrainTileEditor.class));
        toolPages.add(new LinkAndName("Backup/Restore", BackupRestore.class));
        toolPages.add(new LinkAndName("Item Type Editpr", ItemTypeTable.class));
        toolPages.add(new LinkAndName("Session Tracking", SessionTable.class));
        toolPages.add(new LinkAndName("User Tracking", UserTracking.class));
        toolPages.add(new LinkAndName("New User Tracking", NewUserTracking.class));
        toolPages.add(new LinkAndName("New User Daily Tracking", NewUserDailyTracking.class));
        toolPages.add(new LinkAndName("Tutorial Tracking", TutorialStatistic.class));
        toolPages.add(new LinkAndName("Bases", PlanetBasesTable.class));
        toolPages.add(new LinkAndName("Levels", LevelTable.class));
        toolPages.add(new LinkAndName("Planets", PlanetTable.class));
        toolPages.add(new LinkAndName("Tutorial", TutorialTable.class));
        toolPages.add(new LinkAndName("User States", UserStateTable.class));
        toolPages.add(new LinkAndName("CMS", Cms.class));
        toolPages.add(new LinkAndName("Inventory", InventoryEditor.class));
        toolPages.add(new LinkAndName("Client Performance Monitor", ClientPerfmonTable.class));
        toolPages.add(new LinkAndName("Sound Library", SoundLibrary.class));
        toolPages.add(new LinkAndName("Image Sprite Map Library", ImageSpriteMapLibrary.class));
        toolPages.add(new LinkAndName("Common Sounds", CommonSounds.class));
        toolPages.add(new LinkAndName("Clip Library", ClipLibrary.class));
        toolPages.add(new LinkAndName("Internationalisation", I18nMgmtPage.class));
        toolPages.add(new LinkAndName("Send Email", SendEmailPage.class));
        toolPages.add(new LinkAndName("Send Server Restart", SendServerRebootMessage.class));
        toolPages.add(new LinkAndName("Add News Entry", AddNewsEntry.class));
        toolPages.add(new LinkAndName("Multi User Level Converter", MultiUserLevelConverter.class));
        toolPages.add(new LinkAndName("Log viewer", LogViewer.class));

    }
}
