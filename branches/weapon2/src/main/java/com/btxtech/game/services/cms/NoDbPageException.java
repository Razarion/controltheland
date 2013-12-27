package com.btxtech.game.services.cms;

/**
 * User: beat
 * Date: 20.01.13
 * Time: 11:51
 */
public class NoDbPageException extends RuntimeException {
    public NoDbPageException(int pageId) {
        super("No DbPage for id: " + pageId);
    }
}
