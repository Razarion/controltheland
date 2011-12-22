package com.btxtech.game.services.cms.layout;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("CONTENT_DETAIL_LINK")
public class DbContentDetailLink extends DbContent {
}
