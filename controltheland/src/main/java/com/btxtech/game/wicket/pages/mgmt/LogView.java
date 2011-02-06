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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class LogView extends WebPage {
    @SpringBean
    private MgmtService mgmtService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
    
    public LogView() {
        ListView<File> logs = new ListView<File>("logFiles", mgmtService.getLogFiles()) {
            @Override
            protected void populateItem(final ListItem<File> listItem) {
                Resource resource = new Resource() {
                    @Override
                    public IResourceStream getResourceStream() {
                        return new FileResourceStream(listItem.getModelObject());
                    }
                };
                resource.setCacheable(false);
                ResourceLink link = new ResourceLink("logLink", resource);
                link.add(new Label("name", listItem.getModelObject().getName()));
                        
                listItem.add(link);
                listItem.add(new Label("date", simpleDateFormat.format(new Date(listItem.getModelObject().lastModified()))));
                listItem.add(new Label("size", Long.toString(listItem.getModelObject().length() / 1000) + " KB"));
            }
        };
        add(logs);
    }
}
