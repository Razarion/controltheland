package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
    @Column(length = 50000)
    private String html;
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

    public ConditionConfig createConditionConfig(ServerItemTypeService serverItemTypeService) {
        if (dbTutorialConfig != null) {
            return new ConditionConfig(ConditionTrigger.TUTORIAL, null, null, null);
        } else if (dbConditionConfig != null) {
            return dbConditionConfig.createConditionConfig(serverItemTypeService);
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

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public QuestInfo createQuestInfo() {
        String additionalDescription = null;
        Index radarPositionHint = null;
        if(dbConditionConfig != null) {
            additionalDescription = dbConditionConfig.getAdditionalDescription();
            radarPositionHint = dbConditionConfig.getRadarPositionHint();
        }
        return new QuestInfo(name, html, additionalDescription, xp, money, id, isDbTutorialConfig() ? QuestInfo.Type.MISSION : QuestInfo.Type.QUEST, radarPositionHint);
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
