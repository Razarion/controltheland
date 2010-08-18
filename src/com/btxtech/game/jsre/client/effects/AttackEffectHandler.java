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

package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.GwtCommon;
import com.google.gwt.user.client.Timer;
import java.util.HashSet;
import java.util.Iterator;

/**
 * User: beat
 * Date: Jun 20, 2009
 * Time: 1:29:15 PM
 */
public class AttackEffectHandler {
    public final static int ATTACK_EFFECT_TIMER_DELAY = 50;
    private static final AttackEffectHandler INSTANCE = new AttackEffectHandler();
    private final HashSet<MuzzleFlash> attacks = new HashSet<MuzzleFlash>();

    /**
     * Singleton
     */
    private AttackEffectHandler() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                try {
                    for (Iterator<MuzzleFlash> it = attacks.iterator(); it.hasNext();) {
                        MuzzleFlash attack = it.next();
                        if (attack.isTimeUp()) {
                            attack.dispose();
                            it.remove();
                        }
                    }
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                }
            }
        };
        timer.scheduleRepeating(ATTACK_EFFECT_TIMER_DELAY);
    }

    public static AttackEffectHandler getInstance() {
        return INSTANCE;
    }


    public void onAttack(ClientSyncItem clientSyncItem) {
        if (!clientSyncItem.isVisible()) {
            return;
        }
        try {
            attacks.add(new MuzzleFlash(clientSyncItem));
        } catch (Exception e) {
            GwtCommon.handleException(e);
        }
    }
}
