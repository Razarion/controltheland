package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.user.SecurityRoles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.WebPage;

/**
 * User: beat
 * Date: 09.03.2011
 * Time: 14:13:57
 */
@AuthorizeInstantiation(SecurityRoles.ROLE_ADMINISTRATOR)
public class MgmtWebPage extends WebPage {
    public MgmtWebPage() {
    }
    
    public MgmtWebPage(PageParameters parameters) {
        super(parameters);
    }
}
