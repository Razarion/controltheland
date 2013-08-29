/*
 * Copyright (c) 2011.
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

package com.btxtech.game.services.user;

import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.services.planet.Base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * User: beat
 * Date: 19.01.2011
 * Time: 10:42:00
 */
public class UserState {
    private Integer userId;
    private Base base;
    private int dbLevelId;
    private int xp;
    private String sessionId;
    private int razarion;
    private Collection<Integer> inventoryItemIds = new ArrayList<>();
    private Collection<Integer> inventoryArtifactIds = new ArrayList<>();
    private Collection<StorablePacket> storablePackets = new ArrayList<>();
    private Locale locale;

    public boolean isRegistered() {
        return userId != null;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public int getDbLevelId() {
        return dbLevelId;
    }

    public void setDbLevelId(int dbLevelId) {
        this.dbLevelId = dbLevelId;
    }

    public Base getBase() {
        return base;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void increaseXp(int deltaXp) {
        xp += deltaXp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isOnline() {
        return sessionId != null;
    }

    public Integer getUser() {
        return userId;
    }

    public void setUser(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserState: user=" + userId;
    }

    public int getRazarion() {
        return razarion;
    }

    public void setRazarion(int razarion) {
        this.razarion = razarion;
    }

    public void addRazarion(int value) {
        razarion += value;
    }

    public void subRazarion(int value) {
        razarion -= value;
    }

    public void addInventoryItem(int inventoryItemId) {
        inventoryItemIds.add(inventoryItemId);
    }

    public boolean hasInventoryItemId(int inventoryItemId) {
        return inventoryItemIds.contains(inventoryItemId);
    }

    public void removeInventoryItemId(int inventoryItemId) {
        inventoryItemIds.remove(inventoryItemId);
    }

    public void removeAllInventoryItemId(int inventoryItemId) {
        while (inventoryItemIds.remove(inventoryItemId)) {
        }
    }

    public void addInventoryArtifact(int inventoryArtifactId) {
        inventoryArtifactIds.add(inventoryArtifactId);
    }

    public boolean removeArtifactIds(Collection<Integer> artifactIds) {
        Collection<Integer> copy = new ArrayList<>(inventoryArtifactIds);
        for (Integer artifactId : artifactIds) {
            if (!copy.remove(artifactId)) {
                return false;
            }
        }
        inventoryArtifactIds = copy;
        return true;
    }

    public Collection<Integer> getInventoryItemIds() {
        return inventoryItemIds;
    }

    public Collection<Integer> getInventoryArtifactIds() {
        return inventoryArtifactIds;
    }

    public void removeAllInventoryArtifactId(int inventoryArtifactId) {
        while (inventoryArtifactIds.remove(inventoryArtifactId)) {
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void saveStorablePackage(StorablePacket packet) {
        storablePackets.add(packet);
    }

    public Collection<StorablePacket> getAndClearStorablePackets() {
        Collection<StorablePacket> result = new ArrayList<>(storablePackets);
        storablePackets.clear();
        return result;
    }

    public Collection<StorablePacket> getStorablePackets() {
        return storablePackets;
    }
}
