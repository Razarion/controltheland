package com.btxtech.game.jsre.common.utg.condition;

/**
 * User: beat
 * Date: 31.01.2012
 * Time: 14:19:20
 */
public interface TimeAware {
    void onTimer();

    boolean isTimerNeeded();

    <A, I> AbstractConditionTrigger<A, I> getAbstractConditionTrigger();
}
