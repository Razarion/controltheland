package com.btxtech.game.services.user;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 25.07.13
 * Time: 14:12
 */
@Embeddable
public class DbInvitationInfo {
    public enum Source {
        MAIL,
        URL,
        FACEBOOK
    }
    private Source source;
    @OneToOne(fetch = FetchType.LAZY)
    private User host;

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
