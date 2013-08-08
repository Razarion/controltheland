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
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class BackupRestore extends MgmtWebPage {
    @SpringBean
    private BackupService backupService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);
    private Collection<Date> toBeDeleted = new ArrayList<>();

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
                baseItem.add(new Label("userStates", Integer.toString(baseItem.getModelObject().getUserStateCount())));
                baseItem.add(new Button("button") {

                    @Override
                    public void onSubmit() {
                        setResponsePage(new ConfirmRestorePage(baseItem.getModelObject()));
                    }
                });
                baseItem.add(new CheckBox("delete", new IModel<Boolean>() {

                    @Override
                    public Boolean getObject() {
                        return false;
                    }

                    @Override
                    public void setObject(Boolean delete) {
                        if (delete) {
                            toBeDeleted.add(baseItem.getModelObject().getDate());
                        }
                    }

                    @Override
                    public void detach() {
                        toBeDeleted.clear();
                    }
                }));

            }
        };
        form.add(tileList);
        form.add(new Button("delete") {
            @Override
            public void onSubmit() {
                for (Date date : toBeDeleted) {
                    try {
                        backupService.deleteBackupEntry(date);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void setupBackup() {
        Form form = new Form<DbView>("backup") {
            @Override
            protected void onSubmit() {
                backupService.backup();
            }
        };
        add(form);
    }

    private class BackupDataProvider implements IDataProvider<BackupSummary> {
        @Override
        public void detach() {
        }

        @Override
        public Iterator<? extends BackupSummary> iterator(long first, long count) {
            List<BackupSummary> backups = backupService.getBackupSummary();
            if (first != 0 || count != backups.size()) {
                throw new IllegalArgumentException();
            }
            return backups.iterator();
        }

        @Override
        public long size() {
            return backupService.getBackupSummary().size();
        }

        @Override
        public IModel<BackupSummary> model(BackupSummary object) {
            return new Model<>(object);
        }
    }
}