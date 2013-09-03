package com.btxtech.game.wicket;

import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.cms.InvalidUrlException;
import com.btxtech.game.services.cms.NoDbContentInCacheException;
import com.btxtech.game.services.cms.NoDbPageException;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.NoSuchChildException;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.socialnet.facebook.FacebookUrlException;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;

/**
 * User: beat
 * Date: 31.07.13
 * Time: 10:37
 */
public class RazarionExceptionMapper implements IExceptionMapper {
    private Log log = LogFactory.getLog(RazarionExceptionMapper.class);
    @SpringBean
    private UserService userService;
    @SpringBean
    private MgmtService mgmtService;
    @SpringBean
    private CmsUiService cmsUiService;

    public RazarionExceptionMapper() {
        Injector.get().inject(this);
    }

    @Override
    public IRequestHandler map(Exception e) {
        if (e instanceof PageExpiredException) {
            log.error("------------------PageExpiredException---------------------------------");
            ExceptionHandler.logParameters(log, userService);
            log.error(e.getMessage());
            return new RenderPageRequestHandler(new PageProvider(cmsUiService.getPredefinedNotFound()));
        } else if (CommonJava.getMostInnerThrowable(e) instanceof NoSuchChildException) {
            saveServerDebug(null, e);
            return new RenderPageRequestHandler(new PageProvider(cmsUiService.getPredefinedNotFound()));
        } else if (CommonJava.getMostInnerThrowable(e) instanceof InvalidUrlException) {
            saveServerDebug(null, e);
            return new RenderPageRequestHandler(new PageProvider(cmsUiService.getPredefinedNotFound()));
        } else if (CommonJava.getMostInnerThrowable(e) instanceof NoDbContentInCacheException) {
            saveServerDebug(null, e);
            return new RenderPageRequestHandler(new PageProvider(cmsUiService.getPredefinedNotFound()));
        } else if (CommonJava.getMostInnerThrowable(e) instanceof NoDbPageException) {
            saveServerDebug(null, e);
            return new RenderPageRequestHandler(new PageProvider(cmsUiService.getPredefinedNotFound()));
        } else if (CommonJava.getMostInnerThrowable(e) instanceof NumberFormatException) {
            saveServerDebug(null, e);
            return new RenderPageRequestHandler(new PageProvider(cmsUiService.getPredefinedNotFound()));
        } else if (CommonJava.getMostInnerThrowable(e) instanceof FacebookUrlException) {
            ExceptionHandler.handleException(e);
            CmsPage cmsPage = new CmsPage(new PageParameters());
            ((WebResponse) cmsPage.getResponse()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new RenderPageRequestHandler(new PageProvider(cmsPage));
        } else {
            log.error("------------------CMS Unknown Exception---------------------------------");
            ExceptionHandler.logParameters(log, userService);
            log.error("", e);
            return new RenderPageRequestHandler(new PageProvider(CmsPage.class));
        }
    }

    private void saveServerDebug(final Page cause, Exception e) {
        mgmtService.saveServerDebug(MgmtService.SERVER_DEBUG_CMS, ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest(), cause, e);
    }
}
