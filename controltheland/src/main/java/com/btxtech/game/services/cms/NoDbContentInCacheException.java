package com.btxtech.game.services.cms;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 29.11.12
 * Time: 01:28
 */
public class NoDbContentInCacheException extends RuntimeException {
    public NoDbContentInCacheException(int contentId) {
        super("No content for id: " + contentId);
    }
}
