package com.btxtech.game.services.cms.layout;

/**
 * User: beat
 * Date: 13.06.2011
 * Time: 15:13:44
 */
public interface DataProviderInfo {
    String getSpringBeanName();

    void setSpringBeanName(String springBeanName);

    String getContentProviderGetter();

    void setContentProviderGetter(String contentProviderGetter);

    String getExpression();

    void setExpression(String expression);

    DataProviderInfo getParentContentDataProvider();
}
