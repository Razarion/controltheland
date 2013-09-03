package com.btxtech.game.wicket.pages.mgmt.planet;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 07.09.12
 * Time: 14:19
 */
public class PlanetTable extends MgmtWebPage {
    @SpringBean
    private PlanetSystemService planetSystemService;

    public PlanetTable() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("form");
        add(form);

        new CrudRootTableHelper<DbPlanet>("planets", "save", "create", true, form, false) {

            @Override
            protected CrudRootServiceHelper<DbPlanet> getCrudRootServiceHelperImpl() {
                return planetSystemService.getDbPlanetCrud();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbPlanet> item) {
                displayId(item);
                super.extendedPopulateItem(item);
            }

            @Override
            protected void onEditSubmit(DbPlanet dbPlanet) {
                setResponsePage(new PlanetEditor(dbPlanet));
            }
        };
    }
}
