package com.btxtech.game.rest;

import com.btxtech.game.jsre.client.common.Constants;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 10.02.14
 * Time: 21:50
 */
@ApplicationPath(Constants.REST_ROOT)
public class RaxRsApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(FacebookPaymentCallback.class);
        return classes;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        // properties.put(ServerProperties.TRACING, "ALL");
        // properties.put(ServerProperties.TRACING_THRESHOLD, "VERBOSE");
        return properties;
    }
}
