package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestTypeEnum;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.db.DbI18nString;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Locale;

/**
 * User: beat
 * Date: 17.01.2012
 * Time: 14:30:16
 */
@Entity(name = "GUIDANCE_LEVEL_TASK")
public class DbLevelTask implements CrudChild<DbLevel> {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbI18nString i18nTitle = new DbI18nString();
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbI18nString i18nDescription = new DbI18nString();
    @Enumerated(EnumType.STRING)
    private QuestTypeEnum questTypeEnum;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbLevel", insertable = false, updatable = false, nullable = false)
    private DbLevel dbLevel;
    // ----- Rewards -----
    private int money;
    private int xp;
    // ----- Condition -----
    @ManyToOne(fetch = FetchType.LAZY)
    private DbTutorialConfig dbTutorialConfig;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private DbConditionConfig dbConditionConfig;
    private Integer unlockCrystals;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(DbLevel parent) {
        dbLevel = parent;
    }

    @Override
    public DbLevel getParent() {
        return dbLevel;
    }

    public DbTutorialConfig getDbTutorialConfig() {
        return dbTutorialConfig;
    }

    public void setDbTutorialConfig(DbTutorialConfig dbTutorialConfig) {
        this.dbTutorialConfig = dbTutorialConfig;
    }

    public ConditionConfig createConditionConfig(ServerItemTypeService serverItemTypeService, Locale locale) {
        if (dbTutorialConfig != null) {
            return new ConditionConfig(ConditionTrigger.TUTORIAL, null, null, null, false);
        } else if (dbConditionConfig != null) {
            return dbConditionConfig.createConditionConfig(serverItemTypeService, locale);
        } else {
            return null;
        }
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public DbConditionConfig getDbConditionConfig() {
        return dbConditionConfig;
    }

    public void setDbConditionConfig(DbConditionConfig dbConditionConfig) {
        this.dbConditionConfig = dbConditionConfig;
    }

    public DbI18nString getI18nTitle() {
        return i18nTitle;
    }

    public DbI18nString getI18nDescription() {
        return i18nDescription;
    }

    public QuestTypeEnum getQuestTypeEnum() {
        return questTypeEnum;
    }

    public void setQuestTypeEnum(QuestTypeEnum questTypeEnum) {
        this.questTypeEnum = questTypeEnum;
    }

    public Integer getUnlockCrystals() {
        return unlockCrystals;
    }

    public void setUnlockCrystals(Integer unlockCrystals) {
        this.unlockCrystals = unlockCrystals;
    }

    public boolean isUnlockNeeded() {
        return unlockCrystals != null;
    }

    public QuestInfo createQuestInfo(Locale locale) {
        String additionalDescription = null;
        Index radarPositionHint = null;
        boolean hideQuestProgress = false;
        if(dbConditionConfig != null) {
            additionalDescription = dbConditionConfig.getI18nAdditionalDescription().getString(locale);
            radarPositionHint = dbConditionConfig.getRadarPositionHint();
            hideQuestProgress = dbConditionConfig.isHideQuestProgress();
        }
        return new QuestInfo(i18nTitle.getString(locale), i18nDescription.getString(locale), additionalDescription, questTypeEnum, xp, money, id, isDbTutorialConfig() ? QuestInfo.Type.MISSION : QuestInfo.Type.QUEST, radarPositionHint, hideQuestProgress, unlockCrystals);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbLevelTask)) return false;

        DbLevelTask that = (DbLevelTask) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    public boolean isDbTutorialConfig() {
        return dbTutorialConfig != null;
    }

    @Override
    public String toString() {
        return "DbLevelTask{id=" + id + ", name='" + name + '}';
    }
}
