package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.common.Index;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

/**
 * User: beat
 * Date: 11.12.2011
 * Time: 12:15:26
 */
public class TestWeaponType {
    @Test
    @DirtiesContext
    public void changeMuzzleFlashCount() {
        Index[][] muzzleFlashPositions = new Index[1][];
        muzzleFlashPositions[0] = new Index[4];
        for (int i = 0; i < muzzleFlashPositions[0].length; i++) {
            muzzleFlashPositions[0][i] = new Index(0, i);
        }

        WeaponType weaponType = new WeaponType(0, null, 0, null, 0, null, null, null, null, Collections.<Integer, Double>emptyMap(), muzzleFlashPositions);

        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(0, 1), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(0, 2), weaponType.getMuzzleFlashPosition(0, 2));
        Assert.assertEquals(new Index(0, 3), weaponType.getMuzzleFlashPosition(0, 3));

        weaponType.setMuzzleFlashPosition(0, 1, new Index(12, 13));
        Assert.assertEquals(new Index(12, 13), weaponType.getMuzzleFlashPosition(0, 1));

        weaponType.changeMuzzleFlashCount(2);
        Assert.assertEquals(2, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(12, 13), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(0, 2), weaponType.getMuzzleFlashPosition(0, 2));
        Assert.assertEquals(new Index(0, 3), weaponType.getMuzzleFlashPosition(0, 3));

        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(1, 0));
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(1, 1));
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(1, 2));
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(1, 3));

        weaponType.setMuzzleFlashPosition(1, 2, new Index(14, 15));
        Assert.assertEquals(new Index(14, 15), weaponType.getMuzzleFlashPosition(1, 2));
        Assert.assertEquals(2, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(12, 13), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(0, 2), weaponType.getMuzzleFlashPosition(0, 2));
        Assert.assertEquals(new Index(0, 3), weaponType.getMuzzleFlashPosition(0, 3));

        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(1, 0));
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(1, 1));
        Assert.assertEquals(new Index(14, 15), weaponType.getMuzzleFlashPosition(1, 2));
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(1, 3));

        weaponType.changeMuzzleFlashCount(1);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(12, 13), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(0, 2), weaponType.getMuzzleFlashPosition(0, 2));
        Assert.assertEquals(new Index(0, 3), weaponType.getMuzzleFlashPosition(0, 3));

        try {
            weaponType.changeMuzzleFlashCount(0);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ignore) {
            // Expected
        }
    }
}
