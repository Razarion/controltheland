package com.btxtech.game.jsre.common.utg;

/**
 * User: beat
 * Date: 29.03.2011
 * Time: 22:21:18
 */
public interface ConditionServiceListener<A, I> {
    void conditionPassed(A actor, I identifier);
}
