package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.ClientI18nHelper;

/**
 * User: beat
 * Date: 14.01.13
 * Time: 12:50
 */
public enum QuestTypeEnum {
    PVP {
        @Override
        String getString() {
            return ClientI18nHelper.CONSTANTS.questEnumPvp();
        }
    },
    PVE {
        @Override
        String getString() {
            return ClientI18nHelper.CONSTANTS.questEnumPve();
        }
    },
    BOSS_PVE {
        @Override
        String getString() {
            return ClientI18nHelper.CONSTANTS.questEnumBossPve();
        }
    },
    MISSION {
        @Override
        String getString() {
            return ClientI18nHelper.CONSTANTS.questEnumMission();
        }
    },
    GATHER {
        @Override
        String getString() {
            return ClientI18nHelper.CONSTANTS.questEnumGather();
        }
    },
    MONEY {
        @Override
        String getString() {
            return ClientI18nHelper.CONSTANTS.questEnumMoney();
        }
    },
    BUILDUP {
        @Override
        String getString() {
            return ClientI18nHelper.CONSTANTS.questEnumBuildup();
        }
    },
    HOLD_THE_BASE {
        @Override
        String getString() {
            return ClientI18nHelper.CONSTANTS.questEnumHoldTheBase();
        }
    },
    NONE {  
        @Override
        String getString() {
            return null;
        }
    };

    abstract String getString();

    public static boolean isVisible(QuestTypeEnum questTypeEnum) {
        return questTypeEnum != null && questTypeEnum != NONE;
    }
}
