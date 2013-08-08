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

import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:15:59 PM
 */
public class ConfirmRestorePage extends MgmtWebPage {
    @SpringBean
    private BackupService backupService;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);
    private static Log log = LogFactory.getLog(ConfirmRestorePage.class);

    public ConfirmRestorePage(final BackupSummary backupSummary) {
        add(new Label("date", simpleDateFormat.format(backupSummary.getDate())));

        Form form = new Form("confirm");
        add(form);
        form.add(new Button("cancel") {

            @Override
            public void onSubmit() {
                setResponsePage(BackupRestore.class);
            }
        });
        form.add(new Button("restore") {

            @Override
            public void onSubmit() {
                try {
                    backupService.restore(backupSummary.getDate());
                    setResponsePage(BackupRestore.class);
                } catch (NoSuchItemTypeException e) {
                    log.error("", e);
                    throw new RuntimeException(e);
                }
            }
        });

    }
}