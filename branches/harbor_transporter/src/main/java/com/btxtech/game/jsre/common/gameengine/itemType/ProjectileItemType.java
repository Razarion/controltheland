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

package com.btxtech.game.jsre.common.gameengine.itemType;

/**
 * User: beat
 * Date: 29.09.2010
 * Time: 22:07:00
 */
public class ProjectileItemType extends ItemType {
    private int explosionRadius;
    private int damage;
    private int speed;
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

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public void changeTo(ItemType itemType) {
        super.changeTo(itemType);
        ProjectileItemType projectileItemType = (ProjectileItemType) itemType;
        explosionRadius = projectileItemType.explosionRadius;
        damage = projectileItemType.damage;
        speed = projectileItemType.speed;
        range = projectileItemType.range;
        price = projectileItemType.price;
        buildup = projectileItemType.buildup;
    }
}
