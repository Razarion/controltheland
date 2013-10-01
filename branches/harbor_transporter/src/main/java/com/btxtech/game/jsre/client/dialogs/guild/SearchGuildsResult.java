package com.btxtech.game.jsre.client.dialogs.guild;

import java.io.Serializable;
import java.util.List;

/**
 * User: beat
 * Date: 03.06.13
 * Time: 15:01
 */
public class SearchGuildsResult implements Serializable{
    List<GuildDetailedInfo> guildDetailedInfos;
    private int totalRowCount;
    private int startRow;

    public List<GuildDetailedInfo> getGuildDetailedInfos() {
        return guildDetailedInfos;
    }

    public int getTotalRowCount() {
        return totalRowCount;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setGuildDetailedInfos(List<GuildDetailedInfo> guildDetailedInfos) {
        this.guildDetailedInfos = guildDetailedInfos;
    }

    public void setTotalRowCount(int totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }
}
