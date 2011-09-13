package com.btxtech.game.jsre.common.gameengine.formation;

/**
 * User: beat
 * Date: 07.09.2011
 * Time: 20:51:55
 */
public interface AttackFormation {
    boolean hasNext();

    void lastAccepted();

    AttackFormationItem calculateNextEntry();
}
