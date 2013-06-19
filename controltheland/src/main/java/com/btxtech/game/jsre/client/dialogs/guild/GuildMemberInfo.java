package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.common.info.DetailedUser;

import java.io.Serializable;

public class GuildMemberInfo implements Serializable {
    public enum Rank {
        PRESIDENT(3),
        MANAGEMENT(2),
        MEMBER(1);
        private int level;

        Rank(int level) {
            this.level = level;
        }

        public boolean isHigher(Rank rank) {
            return level > rank.level;
        }

    }

    private Rank rank;
    private DetailedUser detailedUser;

    /**
     * Used by GWT
     */
    GuildMemberInfo() {
    }

    public GuildMemberInfo(DetailedUser detailedUser, Rank rank) {
        this.detailedUser = detailedUser;
        this.rank = rank;
    }

    public Rank getRank() {
        return rank;
    }

    public DetailedUser getDetailedUser() {
        return detailedUser;
    }
}
