package com.btxtech.game.services.utg;

import java.util.Date;
import java.util.TimeZone;

/**
 * User: beat
 * Date: 23.08.13
 * Time: 18:54
 */
public class NewUserDailyDto {
    private Date date;
    private int sessions;
    private int registered;
    private int level1;
    private int level2;
    private int level3;
    private int level4;
    private int level5;
    private int level6;

    public NewUserDailyDto(Date date) {
        this.date = date;
    }

    public int getSessions() {
        return sessions;
    }

    public void increaseSessions() {
        sessions++;
    }

    public void increaseRegistered() {
        registered++;
    }

    public void increaseLevelNumber(int number) {
        switch (number) {
            case 6:
                level6++;
            case 5:
                level5++;
            case 4:
                level4++;
            case 3:
                level3++;
            case 2:
                level2++;
            case 1:
                level1++;
        }
    }

    public Date getDate() {
        return date;
    }

    public int getRegistered() {
        return registered;
    }

    public int getLevel1() {
        return level1;
    }

    public int getLevel2() {
        return level2;
    }

    public int getLevel3() {
        return level3;
    }

    public int getLevel4() {
        return level4;
    }

    public int getLevel5() {
        return level5;
    }

    public int getLevel6() {
        return level6;
    }

    public String getLevel1Percent() {
        return Integer.toString((int) ((double) level1 / (double) registered * 100.0)) + "%";
    }

    @Override
    public String toString() {
        return "NewUserDailyDto{" +
                "date=" + date +
                ", sessions=" + sessions +
                ", registered=" + registered +
                '}';
    }
}
