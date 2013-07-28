package com.btxtech.game.services.user.impl;

import com.btxtech.game.jsre.client.dialogs.incentive.FriendInvitationBonus;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.mgmt.ServerI18nHelper;
import com.btxtech.game.services.mgmt.impl.MgmtServiceImpl;
import com.btxtech.game.services.user.DbFacebookInvitation;
import com.btxtech.game.services.user.DbFriendInvitationBonus;
import com.btxtech.game.services.user.DbInvitationInfo;
import com.btxtech.game.services.user.InvitationService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 24.07.13
 * Time: 13:32
 */
@Component
public class InvitationServiceImpl implements InvitationService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ServerI18nHelper serverI18nHelper;
    @Autowired
    private VelocityEngine velocityEngine;
    @Autowired
    private UserService userService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void sendMailInvite(String emailAddress) {
        User user = userService.getUser();
        if (user == null || !user.isVerified()) {
            throw new IllegalStateException("User is not logged in or verified");
        }
        String url = CmsUtil.generateInviteUrl(user.createSimpleUser(), CmsUtil.MAIL_VALUE);
        sendInvitationEmail(emailAddress, user.getUsername(), url);
        historyService.addFriendInvitationMailSent(user, emailAddress);
    }

    @Override
    @Transactional
    public void onFacebookInvite(String fbRequestId, Collection<String> fbUserIds) {
        User user = userService.getUser();
        if (user == null || !user.isVerified()) {
            throw new IllegalStateException("User is not logged in or verified");
        }
        DbFacebookInvitation dbFacebookInvitation = new DbFacebookInvitation();
        dbFacebookInvitation.setTimeStamp();
        dbFacebookInvitation.setHost(user);
        dbFacebookInvitation.setFbRequestId(fbRequestId);
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> iterator = fbUserIds.iterator(); iterator.hasNext(); ) {
            String fbUserId = iterator.next();
            builder.append(fbUserId);
            if (iterator.hasNext()) {
                builder.append(";");
            }
        }
        String idString = builder.toString();
        if (idString.length() > 1000) {
            idString = "...Truncated...";
        }
        dbFacebookInvitation.setFbInvitedUserIds(idString);
        sessionFactory.getCurrentSession().save(dbFacebookInvitation);
        historyService.addFriendInvitationFacebookSent(user, fbRequestId);

    }

    private void sendInvitationEmail(final String emailAddress, final String host, final String url) {
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(emailAddress);
                    message.setFrom(MgmtServiceImpl.REPLY_EMAIL);
                    message.setSubject(serverI18nHelper.getString("invitationSubject", new Object[]{host}));
                    Map<Object, Object> model = new HashMap<>();
                    model.put("title", serverI18nHelper.getString("invitationTitle"));
                    model.put("line1", serverI18nHelper.getString("invitationLine1", new Object[]{host}));
                    model.put("line2", serverI18nHelper.getString("invitationLine2", new Object[]{host}));
                    model.put("url", url);
                    model.put("linkText", serverI18nHelper.getString("invitationLinkText", new Object[]{host}));
                    model.put("line3", serverI18nHelper.getString("invitationLine3", new Object[]{host}));
                    model.put("razarionTeam", serverI18nHelper.getString("invitationRazarionTeam"));
                    String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "com/btxtech/game/services/user/invitation.vm", "UTF-8", model);
                    message.setText(text, true);
                }
            };
            mailSender.send(preparator);
        } catch (Exception ex) {
            ExceptionHandler.handleException(ex);
        }
    }

    @Override
    public User getHost4FacebookRequest(String fbRequestIds) {
        if (fbRequestIds == null || fbRequestIds.trim().isEmpty()) {
            throw new IllegalArgumentException("fbRequestIds must nt be null and must contain som characters: " + fbRequestIds);
        }
        List<String> fbRequestIdList = Arrays.asList(fbRequestIds.split("\\s*,\\s*"));
        if (fbRequestIdList.isEmpty()) {
            throw new IllegalArgumentException("No fbRequestIds in: " + fbRequestIds);
        }

        // Problem mixing JPA and hibernate session
        /*CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Query for total row count in invitations
        CriteriaQuery<DbFacebookInvitation> facebookInvitationQuery = criteriaBuilder.createQuery(DbFacebookInvitation.class);
        Root<DbFacebookInvitation> fromInvitation = facebookInvitationQuery.from(DbFacebookInvitation.class);
        facebookInvitationQuery.where(fromInvitation.get("fbRequestId").in(fbRequestIdList));
        facebookInvitationQuery.orderBy(criteriaBuilder.desc(fromInvitation.<String>get("timeStamp")));
        CriteriaQuery<DbFacebookInvitation> userSelect = facebookInvitationQuery.select(fromInvitation);
        TypedQuery<DbFacebookInvitation> typedUserQuery = entityManager.createQuery(userSelect);
        List<DbFacebookInvitation> dbFacebookInvitations = typedUserQuery.setMaxResults(1).getResultList();
        if(dbFacebookInvitations == null || dbFacebookInvitations.isEmpty()) {
            throw new IllegalArgumentException("DbFacebookInvitation for: " + fbRequestIds);
        }
        return dbFacebookInvitations.get(0).getHost(); */
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbFacebookInvitation.class);
        criteria.add(Restrictions.in("fbRequestId", fbRequestIdList));
        criteria.addOrder(Order.desc("timeStamp"));
        criteria.setMaxResults(1);
        List list = criteria.list();
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("DbFacebookInvitation for: " + fbRequestIds);
        }
        return ((DbFacebookInvitation) list.get(0)).getHost();
    }

    @Override
    @Transactional
    public void onLevelUp(UserState inviteeUserState, DbLevel dbLevel) {
        try {
            User invitee = userService.getUser(inviteeUserState);
            if (invitee == null) {
                return;
            }
            DbInvitationInfo dbInvitationInfo = invitee.getDbInvitationInfo();
            if (dbInvitationInfo == null) {
                return;
            }
            User host = dbInvitationInfo.getHost();
            UserState hostUserState = userService.getUserState(host);
            hostUserState.addRazarion(dbLevel.getFriendInvitationBonus());
            historyService.addFriendInvitationBonus(host, invitee, dbLevel.getFriendInvitationBonus(), hostUserState.getRazarion());
            updateDbFriendInvitationBonus(host, invitee, dbLevel);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    @Override
    @Transactional
    public void onUserRegisteredAndVerified(User invitee) {
        if (invitee.getDbInvitationInfo() == null) {
            return;
        }
        try {
            sessionFactory.getCurrentSession().save(new DbFriendInvitationBonus(invitee.getDbInvitationInfo().getHost(), invitee));
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void updateDbFriendInvitationBonus(User host, User invitee, DbLevel dbLevel) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            // Query for total row count in invitations
            CriteriaQuery<DbFriendInvitationBonus> friendInvitationBonusQuery = criteriaBuilder.createQuery(DbFriendInvitationBonus.class);
            Root<DbFriendInvitationBonus> friendInvitationBonusFrom = friendInvitationBonusQuery.from(DbFriendInvitationBonus.class);
            Predicate predicateHost = criteriaBuilder.equal(friendInvitationBonusFrom.<String>get("host"), host);
            Predicate predicateInvitee = criteriaBuilder.equal(friendInvitationBonusFrom.<String>get("invitee"), invitee);
            friendInvitationBonusQuery.where(criteriaBuilder.and(predicateHost, predicateInvitee));
            friendInvitationBonusQuery.orderBy(criteriaBuilder.desc(friendInvitationBonusFrom.get("timeStamp")));
            TypedQuery<DbFriendInvitationBonus> typedFriendInvitationBonusQuery = entityManager.createQuery(friendInvitationBonusQuery);
            typedFriendInvitationBonusQuery.setMaxResults(1);
            DbFriendInvitationBonus dbFriendInvitationBonus = typedFriendInvitationBonusQuery.getSingleResult();
            if (dbFriendInvitationBonus == null) {
                throw new IllegalStateException("No DbFriendInvitationBonus available for. host: " + host + " invitee: " + invitee);
            }
            dbFriendInvitationBonus.addBonus(dbLevel.getFriendInvitationBonus());
            sessionFactory.getCurrentSession().merge(dbFriendInvitationBonus);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendInvitationBonus> getFriendInvitationBonus() {
        User user = userService.getUser();
        if (user == null || !user.isVerified()) {
            throw new IllegalStateException("User is not logged in or verified");
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Query for total row count in invitations
        CriteriaQuery<DbFriendInvitationBonus> friendInvitationBonusQuery = criteriaBuilder.createQuery(DbFriendInvitationBonus.class);
        Root<DbFriendInvitationBonus> friendInvitationBonusFrom = friendInvitationBonusQuery.from(DbFriendInvitationBonus.class);
        Predicate predicateHost = criteriaBuilder.equal(friendInvitationBonusFrom.<String>get("host"), user);
        friendInvitationBonusQuery.where(predicateHost);
        friendInvitationBonusQuery.orderBy(criteriaBuilder.desc(friendInvitationBonusFrom.get("bonus")));
        TypedQuery<DbFriendInvitationBonus> typedFriendInvitationBonusQuery = entityManager.createQuery(friendInvitationBonusQuery);
        List<DbFriendInvitationBonus> dbFriendInvitationBonuses = typedFriendInvitationBonusQuery.getResultList();
        List<FriendInvitationBonus> friendInvitationBonuses = new ArrayList<>();
        for (DbFriendInvitationBonus dbFriendInvitationBonus : dbFriendInvitationBonuses) {
            friendInvitationBonuses.add(dbFriendInvitationBonus.createFriendInvitationBonus(userGuidanceService));
        }
        return friendInvitationBonuses;
    }
}
