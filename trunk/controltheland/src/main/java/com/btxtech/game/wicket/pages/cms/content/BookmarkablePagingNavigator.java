package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 21.09.2011
 * Time: 12:03:05
 */
public class BookmarkablePagingNavigator extends PagingNavigator {
    private BeanIdPathElement beanIdPathElement;
    @SpringBean
    private CmsUiService cmsUiService;

    public BookmarkablePagingNavigator(String id, IPageable iPageable, BeanIdPathElement beanIdPathElement) {
        super(id, iPageable);
        this.beanIdPathElement = beanIdPathElement;
    }

    @Override
    protected AbstractLink newPagingNavigationLink(String id, final IPageable pageable, int pageNumber) {
        PageParameters parameters = cmsUiService.createPageParametersFromBeanId(beanIdPathElement);
        final int idx = getPagingNumber(pageNumber, pageable);
        parameters.put(CmsPage.PAGING_NUMBER, idx);
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>(id, CmsPage.class, parameters) {
            public boolean linksTo(final Page page) {
                return idx == pageable.getCurrentPage();
            }
        };
        link.setAutoEnable(true);
        return link;
    }

    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, final IPageable pageable, final int increment) {
        int idx = pageable.getCurrentPage() + increment;
        // make sure the index lies between 0 and the last page
        idx = Math.max(0, Math.min(pageable.getPageCount() - 1, idx));

        PageParameters parameters = cmsUiService.createPageParametersFromBeanId(beanIdPathElement);
        parameters.put(CmsPage.PAGING_NUMBER, getPagingNumber(idx, pageable));
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>(id, CmsPage.class, parameters) {
            public boolean linksTo(final Page page) {
                if (pageable.getCurrentPage() <= 0) {
                    // is first
                    return increment < 0;
                } else {
                    // Is may last
                    return pageable.getCurrentPage() >= (pageable.getPageCount() - 1) && increment > 0;
                }
            }
        };
        link.setAutoEnable(true);
        return link;
    }

    @Override
    protected PagingNavigation newNavigation(final IPageable pageable, final IPagingLabelProvider labelProvider) {
        return new PagingNavigation(NAVIGATION_ID, pageable, labelProvider) {
            @Override
            protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageIndex) {
                return BookmarkablePagingNavigator.this.newPagingNavigationLink(id, pageable, pageIndex);
            }
        };
    }

    private int getPagingNumber(int pageNumber, IPageable pageable) {
        int idx = pageNumber;
        if (idx < 0) {
            idx = pageable.getPageCount() + idx;
        }

        if (idx > (pageable.getPageCount() - 1)) {
            idx = pageable.getPageCount() - 1;
        }

        if (idx < 0) {
            idx = 0;
        }
        return idx;
    }

}
