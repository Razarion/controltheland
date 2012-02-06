package com.btxtech.game.services.utg.condition;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.utg.condition.GenericComparisonValueContainer;
import com.btxtech.game.jsre.common.utg.condition.GenericComparisonValueException;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 05.02.2012
 * Time: 11:45:14
 */
@Entity(name = "BACKUP_COMPARISON_VALUE")
public class DbGenericComparisonValue {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbGenericComparisonValue> children;
    private GenericComparisonValueContainer.Key enumKey;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbItemType itemTypeKey;
    private Long longValue;
    private Double doubleValue;
    private Integer identifier;

    /**
     * Used be hibernate
     */
    DbGenericComparisonValue() {
    }

    public DbGenericComparisonValue(Integer identifier, GenericComparisonValueContainer genericComparisonValueContainer, ItemService itemService) {
        this.identifier = identifier;
        for (Map.Entry<Object, Object> entry : genericComparisonValueContainer.getEntries()) {
            setKey(entry.getKey(), itemService);
            if (entry.getValue() instanceof GenericComparisonValueContainer) {
                GenericComparisonValueContainer child = (GenericComparisonValueContainer) entry.getValue();
                if (children == null) {
                    children = new ArrayList<DbGenericComparisonValue>();
                }
                children.add(new DbGenericComparisonValue(null, child, itemService));
            } else {
                setValue(entry.getValue());
            }
        }
    }

    public Integer getId() {
        return id;
    }

    private void setKey(Object key, ItemService itemService) {
        if (key instanceof GenericComparisonValueContainer.Key) {
            enumKey = (GenericComparisonValueContainer.Key) key;
        } else if (key instanceof ItemType) {
            itemTypeKey = itemService.getDbItemType(((ItemType) key).getId());
        } else {
            throw new GenericComparisonValueException("Key is not allowed: " + key);
        }
    }

    private void setValue(Object value) {
        if (value instanceof Long) {
            longValue = (Long) value;
        } else if (value instanceof Integer) {
            longValue = ((Integer) value).longValue();
        } else if (value instanceof Double) {
            doubleValue = (Double) value;
        } else {
            throw new GenericComparisonValueException("Value is not allowed: " + value);
        }
    }

    private Object getValue() {
        if (longValue != null) {
            return longValue;
        } else if (doubleValue != null) {
            return doubleValue;
        } else {
            throw new GenericComparisonValueException("Value is not set: " + this);
        }
    }

    public GenericComparisonValueContainer createGenericComparisonValueContainer(ItemService itemService) throws NoSuchItemTypeException {
        GenericComparisonValueContainer container = new GenericComparisonValueContainer();
        if (children == null || children.isEmpty()) {
            getKeyAndValue(itemService, container, getValue());
        } else {
            for (DbGenericComparisonValue child : children) {
                addChildren(itemService, container, child);
            }
        }
        return container;
    }

    private void addChildren(ItemService itemService, GenericComparisonValueContainer container, DbGenericComparisonValue dbGenericComparisonValue) throws NoSuchItemTypeException {
        Object value;
        if (dbGenericComparisonValue.getChildren() != null && !dbGenericComparisonValue.getChildren().isEmpty()) {
            GenericComparisonValueContainer childContainer = new GenericComparisonValueContainer();
            for (DbGenericComparisonValue child : dbGenericComparisonValue.getChildren()) {
                addChildren(itemService, childContainer, child);
            }
            value = childContainer;
        } else {
            value = getValue();
        }
        getKeyAndValue(itemService, container, value);
    }

    private void getKeyAndValue(ItemService itemService, GenericComparisonValueContainer container, Object value) throws NoSuchItemTypeException {
        if (itemTypeKey != null) {
            container.addChild(itemService.getItemType(itemTypeKey.getId()), value);
        } else if (enumKey != null) {
            container.addChild(enumKey, value);
        } else {
            throw new GenericComparisonValueException("Key does not exist: " + this);
        }
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public Collection<DbGenericComparisonValue> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbGenericComparisonValue)) return false;

        DbGenericComparisonValue other = (DbGenericComparisonValue) o;

        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
