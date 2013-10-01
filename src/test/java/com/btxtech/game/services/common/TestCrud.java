package com.btxtech.game.services.common;

import org.apache.commons.beanutils.BeanComparator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 22.09.2011
 * Time: 12:23:54
 */
public class TestCrud {
    @Test
    public void sortTestSortClass() throws Exception {
        List<TestSortClass> entries = new ArrayList<TestSortClass>();

        entries.add(new TestSortClass(1, 0.1, "aaa", null));
        entries.add(new TestSortClass(2, 0.2, null, new NestedTestSortClass(null)));
        entries.add(new TestSortClass(3, null, "bbb", new NestedTestSortClass(1)));
        ReadonlyListContentProvider<TestSortClass> provider = new ReadonlyListContentProvider<TestSortClass>(entries);

        ContentSortList sort = new ContentSortList();
        sort.addAsc("d");
        Utils.printJarLocation(BeanComparator.class);
        List<TestSortClass> sorted = (List<TestSortClass>) provider.readDbChildren(sort);
        Assert.assertEquals(3, (int) sorted.get(0).getId());
        Assert.assertEquals(1, (int) sorted.get(1).getId());
        Assert.assertEquals(2, (int) sorted.get(2).getId());

        sort = new ContentSortList();
        sort.addDesc("d");
        sorted = (List<TestSortClass>) provider.readDbChildren(sort);
        Assert.assertEquals(2, (int) sorted.get(0).getId());
        Assert.assertEquals(1, (int) sorted.get(1).getId());
        Assert.assertEquals(3, (int) sorted.get(2).getId());

        sort = new ContentSortList();
        sort.addAsc("s");
        sorted = (List<TestSortClass>) provider.readDbChildren(sort);
        Assert.assertEquals(2, (int) sorted.get(0).getId());
        Assert.assertEquals(1, (int) sorted.get(1).getId());
        Assert.assertEquals(3, (int) sorted.get(2).getId());

        sort = new ContentSortList();
        sort.addDesc("s");
        sorted = (List<TestSortClass>) provider.readDbChildren(sort);
        Assert.assertEquals(3, (int) sorted.get(0).getId());
        Assert.assertEquals(1, (int) sorted.get(1).getId());
        Assert.assertEquals(2, (int) sorted.get(2).getId());

        sort = new ContentSortList();
        sort.addAsc("n.i");
        sorted = (List<TestSortClass>) provider.readDbChildren(sort);
        Assert.assertEquals(1, (int) sorted.get(0).getId());
        Assert.assertEquals(2, (int) sorted.get(1).getId());
        Assert.assertEquals(3, (int) sorted.get(2).getId());

        sort = new ContentSortList();
        sort.addDesc("n.i");
        sorted = (List<TestSortClass>) provider.readDbChildren(sort);
        Assert.assertEquals(3, (int) sorted.get(0).getId());
        Assert.assertEquals(1, (int) sorted.get(1).getId());
        Assert.assertEquals(2, (int) sorted.get(2).getId());
    }

    public class TestSortClass extends SimpleCrudChild {
        private int id;
        private Double d;
        private String s;
        private NestedTestSortClass n;

        private TestSortClass(int id, Double d, String s, NestedTestSortClass n) {
            this.id = id;
            this.d = d;
            this.s = s;
            this.n = n;
        }

        public Double getD() {
            return d;
        }

        public String getS() {
            return s;
        }

        public NestedTestSortClass getN() {
            return n;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }

    public class NestedTestSortClass {
        private Integer i;

        private NestedTestSortClass(Integer i) {
            this.i = i;
        }

        public Integer getI() {
            return i;
        }
    }

}
