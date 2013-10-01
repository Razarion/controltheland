package com.btxtech.game.services.mgmt;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * User: beat
 * Date: 15.01.13
 * Time: 16:52
 */
public interface RequestHelper {
   Locale getLocale();

    HttpServletRequest getRequest();
}
