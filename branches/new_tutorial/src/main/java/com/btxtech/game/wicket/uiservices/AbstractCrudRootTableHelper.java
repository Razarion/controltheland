package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.CrudChild;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 14.03.2011
 * Time: 12:42:45
 */
abstract public class AbstractCrudRootTableHelper<T extends CrudChild> implements Serializable {
    private CrudTableListProvider<T> provider;

    public AbstractCrudRootTableHelper(String tableId, String saveId, String createId, final boolean showEdit, WebMarkupContainer markupContainer, final boolean showOrderButtons) {
        provider = new CrudTableListProvider<T>() {
            @Override
            protected List<T> createList() {
                Collection<T> collection = readDbChildren();
                if (collection instanceof List) {
                    return (List<T>) collection;
                } else {
                    return new ArrayList<T>(collection);
                }
            }
        };


        markupContainer.add(new DataView<T>(tableId, provider) {
            @Override
            protected void populateItem(final Item<T> item) {
                extendedPopulateItem(item);
                if (showEdit) {
                    item.add(new Button("edit") {

                        @Override
                        public void onSubmit() {
                            onEditSubmit(item.getModelObject());
                        }
                    });
                }

                if (showOrderButtons) {
                    item.add(new Button("up") {
                        @Override
                        public void onSubmit() {
                            moveChildUp(item);
                            refresh();
                        }

                        @Override
                        public boolean isVisible() {
                            return canMoveUp(item);
                        }
                    });
                    item.add(new Button("down") {
                        @Override
                        public void onSubmit() {
                            moveChildDown(item);
                            refresh();
                        }

                        @Override
                        public boolean isVisible() {
                            return canMoveDown(item);
                        }
                    });
                }

                item.add(new Button("delete") {
                    @Override
                    public void onSubmit() {
                        deleteChild(item.getModelObject());
                        refresh();
                    }
                });

            }
        });

        if (saveId != null) {
            setupSave(markupContainer, saveId);
        }
        setupCreate(markupContainer, createId);
    }

    protected abstract Collection<T> readDbChildren();

    protected abstract void deleteChild(T modelObject);

    protected abstract void updateDbChildren(List<T> children);

    protected abstract T createDbChild();

    protected abstract <C extends T> C createDbChild(Class<C> createClass);

    protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
        markupContainer.add(new Button(createId) {

            @Override
            public void onSubmit() {
                createDbChild();
                refresh();
            }
        });
    }

    protected void setupSave(WebMarkupContainer markupContainer, String saveId) {
        markupContainer.add(new Button(saveId) {

            @Override
            public void onSubmit() {
                updateDbChildren(getList());
            }
        });
    }

    /**
     * Overide in subclasses
     *
     * @param item From PopulateItem
     */
    protected void extendedPopulateItem(Item<T> item) {
        item.add(new TextField<String>("name"));
    }

    protected void displayId(Item<T> item) {
        item.add(new Label("id"));
    }


    /**
     * Override in subclasses
     *
     * @param t the item to edit
     */
    protected void onEditSubmit(T t) {

    }

    public void swapRow(int i, int j) {
        Collections.swap(provider.getList(), i, j);
        updateDbChildren(provider.getList());
    }

    public void moveChildUp(Item<T> item) {
        if (item.getIndex() < 1) {
            return;
        }
        swapRow(item.getIndex(), item.getIndex() - 1);
    }

    public void moveChildDown(Item<T> item) {
        if (item.getIndex() + 1 >= rowCount()) {
            return;
        }
        swapRow(item.getIndex(), item.getIndex() + 1);
    }

    public boolean canMoveUp(Item<T> item) {
        return item.getIndex() > 0;
    }

    public boolean canMoveDown(Item<T> item) {
        return item.getIndex() + 1 < rowCount();
    }

    public int rowCount() {
        return provider.getList().size();
    }

    public List<T> getList() {
        return provider.getList();
    }

    public void refresh() {
        provider.refresh();
    }


}
