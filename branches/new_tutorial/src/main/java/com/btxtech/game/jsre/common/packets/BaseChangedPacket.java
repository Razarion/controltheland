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

package com.btxtech.game.jsre.common.packets;

import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;

/**
 * User: beat
 * Date: Sep 10, 2009
 * Time: 11:35:20 AM
 */
public class BaseChangedPacket extends Packet {
    public enum Type {
        CHANGED,
        CREATED,
        REMOVED
    }

    private Type type;
    private BaseAttributes baseAttributes;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BaseAttributes getBaseAttributes() {
        return baseAttributes;
    }

    public void setBaseAttributes(BaseAttributes baseAttributes) {
        this.baseAttributes = baseAttributes;
    }

    @Override
    public String toString() {
        return "BaseChangedPacket{type=" + type + ", baseAttributes=" + baseAttributes + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseChangedPacket that = (BaseChangedPacket) o;

        return baseAttributes.equals(that.baseAttributes) && type == that.type;

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + baseAttributes.hashCode();
        return result;
    }
}