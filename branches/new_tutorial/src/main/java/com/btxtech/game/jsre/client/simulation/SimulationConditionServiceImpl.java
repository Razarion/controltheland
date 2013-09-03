/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientGlobalServices;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 13:09:19
 */
public class SimulationConditionServiceImpl extends ConditionServiceImpl<SimpleBase, Void> {
    private static final SimulationConditionServiceImpl INSTANCE = new SimulationConditionServiceImpl();
    private AbstractConditionTrigger<SimpleBase, Void> abstractConditionTrigger;
    private int rate = 10000;
    private Timer timer;
    private Timer deferredUpdateTimer;

    public static SimulationConditionServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    protected void saveAbstractConditionTrigger(AbstractConditionTrigger<SimpleBase, Void> abstractConditionTrigger) {
        if (!ClientBase.getInstance().isMyOwnBase(abstractConditionTrigger.getActor())) {
            throw new IllegalArgumentException("Only condition for own base can be saved");
        }
        this.abstractConditionTrigger = abstractConditionTrigger;
        if (deferredUpdateTimer != null) {
            deferredUpdateTimer = new Timer() {
                @Override
                public void run() {
                    if (SimulationConditionServiceImpl.this.abstractConditionTrigger != null
                            && SimulationConditionServiceImpl.this.abstractConditionTrigger.getAbstractComparison() != null) {
                        sendProgressUpdate(SimulationConditionServiceImpl.this.abstractConditionTrigger.getActor(), null);
                    }
                }
            };
            deferredUpdateTimer.scheduleRepeating(2000);
        }
    }

    @Override
    protected Collection<AbstractConditionTrigger<SimpleBase, Void>> getAbstractConditionPrivate(SimpleBase actor, ConditionTrigger conditionTrigger) {
        if (!ClientBase.getInstance().isMyOwnBase(actor)) {
            return null;
        }
        if (abstractConditionTrigger != null && abstractConditionTrigger.getConditionTrigger() == conditionTrigger) {
            return Collections.singletonList(abstractConditionTrigger);
        } else {
            return null;
        }
    }

    @Override
    protected SimpleBase getActor(SimpleBase actorBase) {
        return actorBase;
    }

    @Override
    protected SimpleBase getSimpleBase(SimpleBase actorBase) {
        return actorBase;
    }

    @Override
    protected AbstractConditionTrigger<SimpleBase, Void> getActorConditionsPrivate(SimpleBase actor, Void identifier) {
        if (!ClientBase.getInstance().isMyOwnBase(actor)) {
            throw new IllegalArgumentException("SimulationConditionServiceImpl.getActorConditionsPrivate() Not my base " + actor);
        }
        return abstractConditionTrigger;
    }

    @Override
    protected AbstractConditionTrigger<SimpleBase, Void> removeActorConditionsPrivate(SimpleBase actor, Void identifier) {
        if (!ClientBase.getInstance().isMyOwnBase(actor)) {
            return null;
        }
        AbstractConditionTrigger<SimpleBase, Void> tmp = abstractConditionTrigger;
        abstractConditionTrigger = null;
        return tmp;
    }

    @Override
    protected Collection<AbstractConditionTrigger<SimpleBase, Void>> removeAllActorConditionsPrivate(SimpleBase actor) {
        Collection<AbstractConditionTrigger<SimpleBase, Void>> removed = new ArrayList<AbstractConditionTrigger<SimpleBase, Void>>();
        if (!ClientBase.getInstance().isMyOwnBase(actor)) {
            return removed;
        }
        removed.add(abstractConditionTrigger);
        abstractConditionTrigger = null;
        return removed;
    }

    @Override
    protected void removeAllConditionsPrivate() {
        abstractConditionTrigger = null;
    }

    @Override
    protected GlobalServices getGlobalServices() {
        return ClientGlobalServices.getInstance();
    }

    @Override
    protected PlanetServices getPlanetServices(SimpleBase simpleBase) {
        return ClientPlanetServices.getInstance();
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    protected void startTimer() {
        timer = new TimerPerfmon(PerfmonEnum.SIMULATION_CONDITION_SERVICE) {
            @Override
            public void runPerfmon() {
                onTimer();
            }
        };
        timer.scheduleRepeating(rate);
    }

    @Override
    protected void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void sendProgressUpdate(SimpleBase a, Void i) {
        LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
        levelTaskPacket.setQuestProgressInfo(SimulationConditionServiceImpl.getInstance().getQuestProgressInfo(a, i));
        QuestVisualisationModel.getInstance().setLevelTask(levelTaskPacket);
    }

    public void stopUpdateTimer() {
        if (deferredUpdateTimer != null) {
            deferredUpdateTimer.cancel();
            deferredUpdateTimer = null;
        }
    }
}
