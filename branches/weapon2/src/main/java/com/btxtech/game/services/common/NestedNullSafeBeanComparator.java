package com.btxtech.game.services.common;

/**
 * User: beat
 * Date: 23.09.2011
 * Time: 13:17:18
 */

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Comparator;

public class NestedNullSafeBeanComparator extends BeanComparator {
    private Log log = LogFactory.getLog(NestedNullSafeBeanComparator.class);
    private boolean nullsAreHigh = true;
    private String property;
    private Comparator comparator;

    public NestedNullSafeBeanComparator(String property, boolean nullAreHigh) {
        this.comparator = new NullComparator(nullAreHigh);
        this.property = property;
        this.nullsAreHigh = nullAreHigh;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int compare(Object o1, Object o2) {

        if (property == null) {
            // compare the actual objects
            // noinspection unchecked
            return comparator.compare(o1, o2);
        }

        Object val1 = null;
        Object val2 = null;

        try {
            try {
                val1 = PropertyUtils.getProperty(o1, property);
            } catch (NestedNullException ignored) {
            }

            try {
                val2 = PropertyUtils.getProperty(o2, property);
            } catch (NestedNullException ignored) {
            }

            if (val1 == val2 || (val1 == null && val2 == null)) {
                return 0;
            }

            if (val1 == null) {
                return this.nullsAreHigh ? 1 : -1;
            }

            if (val2 == null) {
                return this.nullsAreHigh ? -1 : 1;
            }

            // noinspection unchecked
            return comparator.compare(val1, val2);
        } catch (Exception e) {
            log.warn("", e);
            return 0;
        }
    }
}
