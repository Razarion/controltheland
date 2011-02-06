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

import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.wicket.WebCommon;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class BackupRestore extends WebPage {
    @SpringBean
    private MgmtService mgmtService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);

    public BackupRestore() {
        setupBackup();
        setupRestore();
    }

    private void setupRestore() {
        Form form = new Form<DbView>("restore");
        add(form);
        DataView<BackupSummary> tileList = new DataView<BackupSummary>("restoreTable", new BackupDataProvider()) {

            @Override
            protected void populateItem(final Item<BackupSummary> baseItem) {
                baseItem.add(new Label("date", simpleDateFormat.format(baseItem.getModelObject().getDate())));
                baseItem.add(new Label("items", Integer.toString(baseItem.getModelObject().getItemCount())));
                baseItem.add(new Label("bases", Integer.toString(baseItem.getModelObject().getBaseCount())));
                baseItem.add(new Button("button") {

                    @Override
                    public void onSubmit() {
                        setResponsePage(new ConfirmRestorePage(baseItem.getModelObject()));
                    }
                });
            }
        };
        form.add(tileList);

    }

    private void setupBackup() {
        Form form = new Form<DbView>("backup") {
            @Override
            protected void onSubmit() {
                mgmtService.backup();
            }
        };
        add(form);
    }

    private class BackupDataProvider implements IDataProvider<BackupSummary> {
        @Override
        public void detach() {
        }

        @Override
        public Iterator<? extends BackupSummary> iterator(int first, int count) {
            List<BackupSummary> backups = mgmtService.getBackupSummary();
            if (first != 0 || count != backups.size()) {
                throw new IllegalArgumentException();
            }
            return backups.iterator();
        }

        @Override
        public int size() {
            return mgmtService.getBackupSummary().size();
        }

        @Override
        public IModel<BackupSummary> model(BackupSummary object) {
            return new Model(object);
        }
    }
}