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

package com.btxtech.game.wicket.pages.user;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.BorderPanel;
import com.btxtech.game.wicket.uiservices.ColorField;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 12, 2009
 * Time: 12:52:57 PM
 */
public class UserBaseInfo extends BorderPanel {
    public static final int SECOND = 1000;
    public static final int MINUTE = SECOND * 60;
    public static final int HOUR = MINUTE * 60;
    public static final int DAY = HOUR * 24;

    @SpringBean
    private BaseService baseService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private ItemService itemService;
    private User user;


    public UserBaseInfo(String id, final User user) {
        super(id);
        this.user = user;

        ListView<BaseInfo> listView = new ListView<BaseInfo>("baseInfo", new IModel<List<BaseInfo>>() {
            @Override
            public List<BaseInfo> getObject() {
                Base base = baseService.getBase(userService.getUserState(user));
                ArrayList<BaseInfo> baseInfos = new ArrayList<BaseInfo>();
                if (base == null) {
                    return baseInfos;
                }
                baseInfos.add(new BaseInfo("Base since:", WebCommon.formatDuration(base.getUptime())));
                baseInfos.add(new BaseInfo("Base name:", baseService.getBaseName(base.getSimpleBase())));
                baseInfos.add(new BaseInfo("Base color:", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", baseService.getBaseHtmlColor(base.getSimpleBase())));
                baseInfos.add(new BaseInfo("Money:", Integer.toString((int) base.getAccountBalance())));
                for (ItemType itemType : itemService.getItemTypes()) {
                    int count = base.getItemCount(itemType);
                    if (count == 0) {
                        continue;
                    }
                    baseInfos.add(new BaseInfo(itemType.getName() + ":", Integer.toString(count)));
                }
                return baseInfos;
            }

            @Override
            public void setObject(List<BaseInfo> baseInfos) {
                // Ignored
            }

            @Override
            public void detach() {
                // Ignored
            }
        }) {
            @Override
            protected void populateItem(ListItem<BaseInfo> listItem) {
                listItem.add(new Label("key", listItem.getModelObject().getKey()));

                if (listItem.getModelObject().getAttribute() != null) {
                    listItem.add(ColorField.create("value", listItem.getModelObject().getAttribute()));
                } else {
                    listItem.add(new Label("value", listItem.getModelObject().getValue()));
                }
            }
        };
        add(listView);
    }

    @Override
    public boolean isVisible() {
        return userService.getUserState(user) != null;
    }

    class BaseInfo {
        private String key;
        private String value;
        private String attribute;

        BaseInfo(String key, String value) {
            this.key = key;
            this.value = value;
        }

        BaseInfo(String key, String value, String attribute) {
            this.key = key;
            this.value = value;
            this.attribute = attribute;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String getAttribute() {
            return attribute;
        }
    }

}
