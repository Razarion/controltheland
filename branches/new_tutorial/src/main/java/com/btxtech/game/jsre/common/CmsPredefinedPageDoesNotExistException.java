package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 09.08.2011
 * Time: 17:56:03
 */
public class CmsPredefinedPageDoesNotExistException extends Exception {
    public CmsPredefinedPageDoesNotExistException(CmsUtil.CmsPredefinedPage page) {
        super("Predifined page could not be found. The page has may not been configured: " + page);
    }
}
