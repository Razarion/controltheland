package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.common.SimpleBase;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * User: beat
 * Date: 14.01.13
 * Time: 22:53
 */
public interface ServerI18nHelper {
    public String getString(String key);

    public String getString(String key, Object[] args);

    String getStringNoRequest(SimpleBase simpleBase, String key, Object[] args);
}
