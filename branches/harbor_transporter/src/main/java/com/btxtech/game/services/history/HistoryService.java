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

package com.btxtech.game.services.history;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryElementInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryFilter;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.user.DbGuild;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;

import java.util.List;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:27:16 PM
 */
public interface HistoryService {
    void addBaseStartEntry(SimpleBase simpleBase);

    void addBaseDefeatedEntry(SimpleBase actor, SimpleBase target);

    void addBaseSurrenderedEntry(SimpleBase simpleBase);

    void addItemCreatedEntry(SyncBaseItem syncBaseItem);

    void addItemDestroyedEntry(SimpleBase actor, SyncBaseItem target);

    void addLevelPromotionEntry(UserState user, DbLevel level);

    void addLevelTaskCompletedEntry(UserState userState, DbLevelTask levelTask);

    void addLevelTaskActivated(UserState userState, DbLevelTask dbLevelTask);

    void addLevelTaskDeactivated(UserState userState, DbLevelTask dbLevelTask);

    void addBoxExpired(SyncBoxItem boxItem);

    void addBoxDropped(SyncBoxItem boxItem, Index position, SyncBaseItem dropper);

    void addBoxPicked(SyncBoxItem boxItem, SyncBaseItem picker);

    void addCrystalsFromBox(UserState userState, int crystals);

    void addCrystalsBought(UserState userState, int crystalsBought);

    void addInventoryItemFromBox(UserState userState, String inventoryItemName);

    void addInventoryArtifactFromBox(UserState userState, String inventoryArtifactName);

    void addInventoryItemUsed(UserState userState, String inventoryItemName);

    void addInventoryItemBought(UserState userState, String inventoryItemName, int crystals);

    void addInventoryArtifactBought(UserState userState, String inventoryArtifactName, int crystals);

    void addBotEnrageUp(String botName, BotEnragementStateConfig botEnragementState, SimpleBase actor);

    void addBotEnrageNormal(String botName, BotEnragementStateConfig botEnragementState);

    void addItemUnlocked(UserState userState, BaseItemType baseItemType);

    void addQuestUnlocked(UserState userState, DbLevelTask dbLevelTask);

    void addPlanetUnlocked(UserState userState, PlanetLiteInfo planetLiteInfo);

    void addGuildCreated(User user, int crystalCost, DbGuild dbGuild);

    void addGuildInvitation(User invitingUser, User invitee, DbGuild hostGuild);

    void addGuildJoined(User user, DbGuild dbGuild);

    void addGuildDismissInvitation(User user, DbGuild dbGuild);

    void addGuildMembershipRequest(User user, DbGuild dbGuild);

    void addDismissGuildMemberRequest(User user, User dismissUser, DbGuild dbGuild);

    void addChangeGuildMemberRank(User user, User userToChange, GuildMemberInfo.Rank rank, DbGuild dbGuild);

    void addGuildTextChanged(User user, String text, DbGuild dbGuild);

    void addGuildMemberKicked(User user, User userToKick, DbGuild dbGuild);

    void addGuildLeft(User user, DbGuild dbGuild);

    void addGuildClosed(User user, DbGuild dbGuild);

    void addKickedGuildClosed(User actorUser, User targetUser, DbGuild dbGuild);

    void addFriendInvitationMailSent(User user, String emailAddress);

    void addFriendInvitationFacebookSent(User user, String fbRequestId);

    void addFriendInvitationBonus(User host, User invitee, int bonus, int crystals);

    List<DisplayHistoryElement> getNewestHistoryElements(User user, int start, int count);

    List<DisplayHistoryElement> getHistoryElements(GameHistoryFrame gameHistoryFrame, GameHistoryFilter gameHistoryFilter);

    int getLevelPromotionCount(final String sessionId);

    ReadonlyListContentProvider<DisplayHistoryElement> getNewestHistoryElements();

    HistoryElementInfo getHistoryElements(HistoryFilter historyFilter);
}
