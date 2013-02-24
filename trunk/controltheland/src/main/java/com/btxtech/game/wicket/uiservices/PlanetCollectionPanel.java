package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.wicket.pages.mgmt.items.ItemsUtil;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collection;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 14.03.2011
 * Time: 20:33:57
 */
public class PlanetCollectionPanel extends Panel {
    @SpringBean
    private PlanetSystemService planetSystemService;

    public PlanetCollectionPanel(String id) {
        this(id, null);
    }

    public PlanetCollectionPanel(String id, IModel<Collection<DbPlanet>> model) {
        super(id, model);
        add(new TextField<>("planetIdString", new IModel<String>() {

            @Override
            public String getObject() {
                Collection<DbPlanet> dbPlanets = (Collection<DbPlanet>) getDefaultModelObject();
                if (dbPlanets == null) {
                    return "";
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (DbPlanet dbPlanet : dbPlanets) {
                    stringBuilder.append(dbPlanet.getId());
                    stringBuilder.append(ItemsUtil.DELIMITER);
                }
                return stringBuilder.toString();
            }

            @Override
            public void setObject(String questIdString) {
                Collection<DbPlanet> dbPlanets = (Collection<DbPlanet>) getDefaultModelObject();
                dbPlanets.clear();
                if (questIdString != null) {
                    StringTokenizer st = new StringTokenizer(questIdString, ItemsUtil.DELIMITER);
                    while (st.hasMoreTokens()) {
                        int planetId = Integer.parseInt(st.nextToken());
                        dbPlanets.add(planetSystemService.getDbPlanetCrud().readDbChild(planetId));
                    }
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
    }
}
