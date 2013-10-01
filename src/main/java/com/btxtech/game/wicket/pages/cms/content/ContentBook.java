package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContentBook;
import com.btxtech.game.services.cms.layout.DbContentRow;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.pages.cms.EditPanel;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
    private ContentContext contentContext;
    private int contentId;

    public ContentBook(String id, DbContentBook dbContentBook, final BeanIdPathElement beanIdPathElement, ContentContext contentContext) {
        super(id);
        this.beanIdPathElement = beanIdPathElement;
        this.contentContext = contentContext;
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
        setupNavigation(dbContentBook, beanIdPathElement);
        WebMarkupContainer table = new WebMarkupContainer("table");
        add(table);

        setupPropertyBook(table, dbContentBook);
        cmsUiService.invokeHiddenMethod(dbContentBook, beanIdPathElement);
    }

    private void setupNavigation(DbContentBook dbContentBook, BeanIdPathElement beanIdPathElement) {
        WebMarkupContainer navigation = new WebMarkupContainer("navigation");
        if (dbContentBook.getNavigationCssClass() != null) {
            navigation.add(new AttributeModifier("class", dbContentBook.getNavigationCssClass()));
        }
        add(navigation);

        if (beanIdPathElement.getParent() != null && dbContentBook.isNavigationVisible()) {
            PageParameters pageParameters = cmsUiService.getPreviousPageParameters(beanIdPathElement, contentId, contentContext);
            BookmarkablePageLink<CmsPage> previous = new BookmarkablePageLink<>("previousLink", CmsPage.class, pageParameters);
            previous.add(new Label("previousLabel", new Model<>(dbContentBook.getPreviousNavigationName())));
            previous.setEnabled(pageParameters != null);
            navigation.add(previous);

            pageParameters = cmsUiService.getUpPageParameters(beanIdPathElement);
            BookmarkablePageLink<CmsPage> up = new BookmarkablePageLink<>("upLink", CmsPage.class, pageParameters);
            up.setEnabled(pageParameters != null);
            up.add(new Label("upLabel", new Model<>(dbContentBook.getUpNavigationName())));
            navigation.add(up);

            pageParameters = cmsUiService.getNextPageParameters(beanIdPathElement, contentId, contentContext);
            BookmarkablePageLink<CmsPage> next = new BookmarkablePageLink<>("nextLink", CmsPage.class, pageParameters);
            next.add(new Label("nextLabel", new Model<>(dbContentBook.getNextNavigationName())));
            next.setEnabled(pageParameters != null);
            navigation.add(next);
        } else {
            navigation.setVisible(false);
            BookmarkablePageLink<CmsPage> previous = new BookmarkablePageLink<>("previousLink", CmsPage.class);
            previous.add(new Label("previousLabel", ""));
            navigation.add(previous);

            BookmarkablePageLink<CmsPage> up = new BookmarkablePageLink<>("upLink", CmsPage.class);
            up.add(new Label("upLabel", ""));
            navigation.add(up);

            BookmarkablePageLink<CmsPage> nextLabel = new BookmarkablePageLink<>("nextLink", CmsPage.class);
            nextLabel.add(new Label("nextLabel", ""));
            navigation.add(nextLabel);
        }
    }

    private void setupPropertyBook(WebMarkupContainer table, DbContentBook dbContentBook) {

        List<ICellPopulator<DbContentRow>> columns = new ArrayList<>();

        if (dbContentBook.isShowName()) {
            // Label
            columns.add(new HeaderlessColumn<DbContentRow, ICellPopulator>() {
                @Override
                public void populateItem(Item<ICellPopulator<DbContentRow>> cellItem, String componentId, IModel<DbContentRow> rowModel) {
                    cellItem.add(new Label(componentId, rowModel.getObject().getDbI18nName().getString(getLocale())));
                }
            });
        }

        // Property
        columns.add(new HeaderlessColumn<DbContentRow, ICellPopulator>() {
            @Override
            public void populateItem(Item<ICellPopulator<DbContentRow>> cellItem, String componentId, IModel<DbContentRow> rowModel) {
                BeanIdPathElement childBeanIdPathElement = cmsUiService.createChildBeanIdPathElement(rowModel.getObject().getDbContent(), beanIdPathElement, null);
                cellItem.add(cmsUiService.getComponent(rowModel.getObject().getDbContent(), bean, componentId, childBeanIdPathElement, contentContext));
            }
        });

        DetachHashListProvider<DbContentRow> detachHashListProvider = new DetachHashListProvider<DbContentRow>() {
            @Override
            protected List<DbContentRow> createList() {
                DbContentBook dbContentBook = (DbContentBook) getDefaultModelObject();
                return dbContentBook.getRowCrud().readDbChildren();
            }
        };

        DataGridView<DbContentRow> dataGridView = new DataGridView<>("rows", columns, detachHashListProvider);
        table.add(dataGridView);
        if (dbContentBook.getCssClass() != null) {
            table.add(new AttributeModifier("class", dbContentBook.getCssClass()));
        }
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
