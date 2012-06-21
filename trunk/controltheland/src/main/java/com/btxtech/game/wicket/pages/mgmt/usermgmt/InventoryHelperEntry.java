package com.btxtech.game.wicket.pages.mgmt.usermgmt;

/**
 * User: beat
 * Date: 20.06.12
 * Time: 16:48
 */
public class InventoryHelperEntry {
    private String name;
    private int id;
    private int count;

    public InventoryHelperEntry(String name, int id, int count) {
        this.name = name;
        this.id = id;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventoryHelperEntry that = (InventoryHelperEntry) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
