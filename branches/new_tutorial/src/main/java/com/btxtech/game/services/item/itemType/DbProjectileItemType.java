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

package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 13.10.2010
 * Time: 11:56:00
 */
@Entity
@DiscriminatorValue("PROJECTILE")
public class DbProjectileItemType extends DbItemType {
    private int explosionRadius;
    private int damage;
    private int speed;
    @Column(name = "theRange")
    private int range;
    private int price;
    private int buildup;

    public int getExplosionRadius() {
        return explosionRadius;
    }

    public void setExplosionRadius(int explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBuildup() {
        return buildup;
    }

    public void setBuildup(int buildup) {
        this.buildup = buildup;
    }

    @Override
    public ItemType createItemType() {
        ProjectileItemType projectileItemType = new ProjectileItemType();
        setupItemType(projectileItemType);
        projectileItemType.setExplosionRadius(explosionRadius);
        projectileItemType.setDamage(damage);
        projectileItemType.setSpeed(speed);
        projectileItemType.setRange(range);
        projectileItemType.setPrice(price);
        projectileItemType.setBuildup(buildup);
        return projectileItemType;
    }

}
