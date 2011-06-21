package com.btxtech.game.services.common;

import org.hibernate.proxy.HibernateProxy;

/**
 * User: beat
 * Date: 16.06.2011
 * Time: 14:42:02
 */
public class HibernateUtil {
    public static <T> T deproxy(Object maybeProxy, Class<T> baseClass) throws ClassCastException {
        if (maybeProxy == null) {
            return null;
        }
        if (maybeProxy instanceof HibernateProxy) {
            return baseClass.cast(((HibernateProxy) maybeProxy).getHibernateLazyInitializer().getImplementation());
        } else {
            return baseClass.cast(maybeProxy);
        }
    }
}
