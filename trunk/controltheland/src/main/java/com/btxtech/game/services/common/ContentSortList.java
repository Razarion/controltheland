package com.btxtech.game.services.common;

import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 21.09.2011
 * Time: 20:56:45
 */
public class ContentSortList {
    private List<ContentSort> contentSorts = new ArrayList<ContentSort>();

    public void addAsc(String propertyName) {
        contentSorts.add(new ContentSort(propertyName, true));
    }

    public void addDesc(String propertyName) {
        contentSorts.add(new ContentSort(propertyName, false));
    }

    public void add(boolean ascending, String propertyName) {
        if (ascending) {
            addAsc(propertyName);
        } else {
            addDesc(propertyName);
        }
    }

    public List<Order> generateHibernateOrders() {
        List<Order> orderList = new ArrayList<Order>();
        for (ContentSort contentSort : contentSorts) {
            if (contentSort.isAsc()) {
                orderList.add(Order.asc(contentSort.getPropertyName()));
            } else {
                orderList.add(Order.desc(contentSort.getPropertyName()));
            }
        }
        return orderList;
    }

    public List<ContentSort> getContentSorts() {
        return contentSorts;
    }
}
