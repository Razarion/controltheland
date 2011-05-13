package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.mapview.common.GeometricalUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 22.04.2011
 * Time: 21:06:58
 */
public class TestGeometricalUtil {
    @Test
    public void testOne() {
        Collection<Index> indexes = new ArrayList<Index>();
        addTileIndex(indexes, 0, true, true, true, true);
        addTileIndex(indexes, 1, true, true, true, true);
        addTileIndex(indexes, 2, true, true, true, true);
        addTileIndex(indexes, 3, true, true, true, true);
        addTileIndex(indexes, 4, true, true, true, true);
        addTileIndex(indexes, 5, true, true, true, true);

        List<Rectangle> rectangles = GeometricalUtil.separateIntoRectangles(indexes);
        Assert.assertEquals(1, rectangles.size());
        assertRectangle(rectangles, new Rectangle(0, 0, 4, 6));
        Assert.assertEquals(0, rectangles.size());
    }

    @Test
    public void testTwo() {
        Collection<Index> indexes = new ArrayList<Index>();
        addTileIndex(indexes, 0, true, false, true, true);
        addTileIndex(indexes, 1, true, false, true, true);
        addTileIndex(indexes, 2, true, false, true, true);
        addTileIndex(indexes, 3, true, false, true, true);
        addTileIndex(indexes, 4, true, false, true, true);
        addTileIndex(indexes, 5, true, false, true, true);

        List<Rectangle> rectangles = GeometricalUtil.separateIntoRectangles(indexes);
        Assert.assertEquals(2, rectangles.size());
        assertRectangle(rectangles, new Rectangle(0, 0, 1, 6));
        assertRectangle(rectangles, new Rectangle(2, 0, 2, 6));
        Assert.assertEquals(0, rectangles.size());

    }

    @Test
    public void testFour() {
        Collection<Index> indexes = new ArrayList<Index>();
        addTileIndex(indexes, 0, true, false, true, true);
        addTileIndex(indexes, 1, true, false, true, true);
        addTileIndex(indexes, 2, false, false, false, false);
        addTileIndex(indexes, 3, true, false, true, true);
        addTileIndex(indexes, 4, true, false, true, true);
        addTileIndex(indexes, 5, true, false, true, true);

        List<Rectangle> rectangles = GeometricalUtil.separateIntoRectangles(indexes);
        Assert.assertEquals(4, rectangles.size());
        assertRectangle(rectangles, new Rectangle(0, 0, 1, 2));
        assertRectangle(rectangles, new Rectangle(2, 0, 2, 2));
        assertRectangle(rectangles, new Rectangle(0, 3, 1, 3));
        assertRectangle(rectangles, new Rectangle(2, 3, 2, 3));
        Assert.assertEquals(0, rectangles.size());

    }

    @Test
    public void testAdjoin() {
        Collection<Index> indexes = new ArrayList<Index>();
        addTileIndex(indexes, 0, true, true, true, true);
        addTileIndex(indexes, 1, true, false, true, true);
        addTileIndex(indexes, 2, true, true, true, true);
        addTileIndex(indexes, 3, true, true, true, true);
        addTileIndex(indexes, 4, true, true, false, true);
        addTileIndex(indexes, 5, true, true, true, true);

        List<Rectangle> rectangles = GeometricalUtil.separateIntoRectangles(indexes);
        Assert.assertEquals(7, rectangles.size());
        Index index1 = new Index(1,1);
        Index index2 = new Index(2,4);
        for (Rectangle rectangle1 : rectangles) {
            for (Rectangle rectangle2 : rectangles) {
                if (rectangle2.equals(rectangle1)) {
                    continue;
                }
                Assert.assertFalse("Rectangles are not allowed to overlap: [" + rectangle1 + "] [" + rectangle2 + "]", rectangle1.adjoinsEclusive(rectangle2));
            }
            Assert.assertFalse("Index not allowed to be in rectangle: [" + rectangle1 + "] Index[" + index1 + "]" , rectangle1.containsExclusive(index1));
            Assert.assertFalse("Index not allowed to be in rectangle: [" + rectangle1 + "] Index[" + index2 + "]" , rectangle1.containsExclusive(index2));
        }
    }

    private void addTileIndex(Collection<Index> indexes, int y, boolean... tiles) {
        for (int i = 0, tilesLength = tiles.length; i < tilesLength; i++) {
            if (tiles[i]) {
                indexes.add(new Index(i, y));
            }
        }
    }

    private void assertRectangle(Collection<Rectangle> actual, Rectangle expected) {
        if (!actual.remove(expected)) {
            System.out.println("Rectangles actual: " + actual);
            Assert.fail("Rectangle expected: " + expected);
        }
    }
}
