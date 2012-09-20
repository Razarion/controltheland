/*
 * Copyright (c) 2011.
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

package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat Date: 12.01.2011 Time: 12:05:40
 */
public abstract class AbstractSyncItemComparison implements AbstractComparison {
    public static final String SHARP = "#";
    private static int MIN_SEND_DELAY = 3000;
    private AbstractConditionTrigger abstractConditionTrigger;
    private List<ProgressTemplateElement> metaTemplate;
    private long lastProgressSendTime;
    private GlobalServices globalServices;

    protected abstract void privateOnSyncItem(SyncItem syncItem);

    protected abstract String getValue(char parameter, Integer number);

    protected AbstractSyncItemComparison(String htmlProgressTamplate) {
        prepareMetaProgressTamplate(htmlProgressTamplate);
    }

    public final void onSyncItem(SyncItem syncItem) {
        privateOnSyncItem(syncItem);
    }

    protected GlobalServices getGlobalServices() {
        return globalServices;
    }

    public void setGlobalServices(GlobalServices globalServices) {
        this.globalServices = globalServices;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A, I> AbstractConditionTrigger<A, I> getAbstractConditionTrigger() {
        return abstractConditionTrigger;
    }

    @Override
    public void setAbstractConditionTrigger(AbstractConditionTrigger abstractConditionTrigger) {
        this.abstractConditionTrigger = abstractConditionTrigger;
    }

    protected void onProgressChanged() {
        if (metaTemplate == null || metaTemplate.isEmpty()) {
            return;
        }
        if (lastProgressSendTime + MIN_SEND_DELAY > System.currentTimeMillis()) {
            return;
        }
        if (globalServices != null) {
            globalServices.getConditionService().sendProgressUpdate(abstractConditionTrigger.getActor(), abstractConditionTrigger.getIdentifier());
            lastProgressSendTime = System.currentTimeMillis();
        }
    }

    @Override
    public String createProgressHtml() {
        if (metaTemplate == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (ProgressTemplateElement templateElement : metaTemplate) {
            if (templateElement.isParameter()) {
                builder.append(getValue(templateElement.getParameter(), templateElement.getNumber()));
            } else {
                builder.append(templateElement.getString());
            }
        }
        return builder.toString();
    }

    private void prepareMetaProgressTamplate(String htmlProgressTamplate) {
        if (htmlProgressTamplate == null || htmlProgressTamplate.isEmpty()) {
            return;
        }
        metaTemplate = new ArrayList<ProgressTemplateElement>();

        int fromIndex = 0;
        while (fromIndex < htmlProgressTamplate.length()) {
            int startIndex = htmlProgressTamplate.indexOf(SHARP, fromIndex);
            if (startIndex < 0) {
                metaTemplate.add(new ProgressTemplateElement(htmlProgressTamplate, fromIndex, htmlProgressTamplate.length(), false));
                break;
            } else {
                if (fromIndex < startIndex) {
                    metaTemplate.add(new ProgressTemplateElement(htmlProgressTamplate, fromIndex, startIndex, false));
                }
                int endIndex = htmlProgressTamplate.length();
                for (int i = startIndex + 1; i < htmlProgressTamplate.length(); i++) {
                    if (!Character.isLetterOrDigit(htmlProgressTamplate.charAt(i))) {
                        endIndex = i;
                        break;
                    }
                }
                metaTemplate.add(new ProgressTemplateElement(htmlProgressTamplate, startIndex, endIndex, true));
                fromIndex = endIndex;
            }
        }

    }

    class ProgressTemplateElement {
        private String string;
        private boolean isParameter;
        private char parameter;
        private Integer number;

        public ProgressTemplateElement(String wholeString, int beginIndex, int endIndex, boolean isParameter) {
            this.string = wholeString.substring(beginIndex, endIndex);
            this.isParameter = isParameter;
            if (isParameter) {
                parameter = Character.toUpperCase(string.charAt(1));
                if (string.length() > 2) {
                    number = Integer.parseInt(string.substring(2));
                }
            }
        }

        public boolean isParameter() {
            return isParameter;
        }

        public String getString() {
            return string;
        }

        public char getParameter() {
            return parameter;
        }

        public Integer getNumber() {
            return number;
        }
    }
}
