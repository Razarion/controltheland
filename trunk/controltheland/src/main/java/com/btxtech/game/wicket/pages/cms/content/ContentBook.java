package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DbContentBook;
import com.btxtech.game.services.cms.DbContentRow;
import com.btxtech.game.wicket.pages.cms.EditPanel;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:29:43
 */
public class ContentBook extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private Object bean;
    private BeanIdPathElement beanIdPathElement;
    private int contentId;

    public ContentBook(String id, DbContentBook dbContentBook, final BeanIdPathElement beanIdPathElement) {
        super(id);
        this.beanIdPathElement = beanIdPathElement;
        contentId = dbContentBook.getId();
        setDefaultModel(new LoadableDetachableModel<DbContentBook>() {
            @Override
            protected DbContentBook load() {
                bean = cmsUiService.getDataProviderBean(beanIdPathElement);
                return cmsUiService.getDbContent(contentId);
            }

            @Override
            protected void onDetach() {
                bean = null;
            }
        });
        add(new EditPanel("edit", dbContentBook, contentId, beanIdPathElement, false, false));
        WebMarkupContainer table = new WebMarkupContainer("table");
        add(table);

        setupPropertyBook(table, dbContentBook);
        cmsUiService.invokeHiddenMethod(dbContentBook, beanIdPathElement);
    }

    private void setupPropertyBook(WebMarkupContainer table, DbContentBook dbContentBook) {

        List<ICellPopulator<DbContentRow>> columns = new ArrayList<ICellPopulator<DbContentRow>>();

        if (dbContentBook.isShowName()) {
            // Label
            columns.add(new HeaderlessColumn<DbContentRow>() {
                @Override
                public void populateItem(Item<ICellPopulator<DbContentRow>> cellItem, String componentId, IModel<DbContentRow> rowModel) {
                    cellItem.add(new Label(componentId, rowModel.getObject().getName()));
                }
            });
        }

        // Property
        columns.add(new HeaderlessColumn<DbContentRow>() {
            @Override
            public void populateItem(Item<ICellPopulator<DbContentRow>> cellItem, String componentId, IModel<DbContentRow> rowModel) {
                BeanIdPathElement childBeanIdPathElement = cmsUiService.createChildBeanIdPathElement(rowModel.getObject().getDbContent(), beanIdPathElement, null);
                cellItem.add(cmsUiService.getComponent(rowModel.getObject().getDbContent(), bean, componentId, childBeanIdPathElement));
            }
        });

        DetachHashListProvider<DbContentRow> detachHashListProvider = new DetachHashListProvider<DbContentRow>() {
            @Override
            protected List<DbContentRow> createList() {
                DbContentBook dbContentBook = (DbContentBook) getDefaultModelObject();
                return dbContentBook.getRowCrud().readDbChildren();
            }
        };

        DataGridView<DbContentRow> dataGridView = new DataGridView<DbContentRow>("rows", columns, detachHashListProvider);
        table.add(dataGridView);
        if (dbContentBook.getCssClass() != null) {
            table.add(new SimpleAttributeModifier("class", dbContentBook.getCssClass()));
        }
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
