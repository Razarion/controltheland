package com.btxtech.game.services.cms;

/**
 * User: beat
 * Date: 13.06.2011
 * Time: 15:13:44
 */
public interface ContentDataProviderInfo {
    String getSpringBeanName();

    String getContentProviderGetter();

    ContentDataProviderInfo getParentContentDataProvider();
}
