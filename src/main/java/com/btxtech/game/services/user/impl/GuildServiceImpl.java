package com.btxtech.game.services.user.impl;

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.cockpit.item.InvitingUnregisteredBaseException;
import com.btxtech.game.jsre.client.common.info.CrystalCostInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildDetailedInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildsResult;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.UserIsAlreadyGuildMemberException;
import com.btxtech.game.jsre.common.gameengine.services.user.NoSuchUserException;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.NoSuchPropertyException;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.common.WrongPropertyTypeException;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.DbGuild;
import com.btxtech.game.services.user.DbGuildInvitation;
import com.btxtech.game.services.user.DbGuildMember;
import com.btxtech.game.services.user.DbGuildMembershipRequest;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 24.04.12
 * Time: 10:13
 */
@Component("guildService")
public class GuildServiceImpl implements GuildService {
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private Log log = LogFactory.getLog(GuildServiceImpl.class);

    @Override
    @Transactional
    public SimpleGuild createGuild(String guildName) throws NoSuchPropertyException, WrongPropertyTypeException {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalArgumentException("User is not registered");
        }
        if (getGuild(user) != null) {
            throw new IllegalStateException("User has already a guild: " + user);
        }
        VerificationRequestCallback.ErrorResult errorResult = isGuildNameValid(guildName);
        if (errorResult != null) {
            throw new IllegalArgumentException("Guild name is invalid: " + errorResult + " " + guildName);
        }
        int crystalCost = propertyService.getIntProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST);
        UserState userState = userService.getUserState();
        if (crystalCost > userState.getCrystals()) {
            throw new IllegalStateException("Not enough crystals to create a guild: " + user);
        }
        DbGuild dbGuild = new DbGuild();
        dbGuild.setTimeStamp();
        dbGuild.setName(guildName);
        dbGuild.addMember(user, GuildMemberInfo.Rank.PRESIDENT);
        sessionFactory.getCurrentSession().save(dbGuild);
        userState.subCrystals(crystalCost);
        removeGuildInvitations(user);
        removeMembershipRequests(user, null);
        historyService.addGuildCreated(user, crystalCost, dbGuild);
        updateBaseService(dbGuild, user);
        return dbGuild.createSimpleGuild();
    }

    @Override
    public FullGuildInfo getFullGuildInfo(int guildId) {
        DbGuild dbGuild = getGuild(guildId);
        if (dbGuild == null) {
            throw new IllegalArgumentException("No such guild: " + guildId);
        }
        return dbGuild.createFullGuildInfo(userService);
    }

    @Override
    @Transactional
    public void inviteUserToGuild(String userName) throws NoSuchUserException, UserIsAlreadyGuildMemberException {
        User invitingUser = userService.getUser();
        if (invitingUser == null || !invitingUser.isRegistrationComplete()) {
            throw new IllegalArgumentException("User is not registered");
        }
        DbGuild hostGuild = getGuild(invitingUser);
        if (hostGuild == null) {
            throw new IllegalArgumentException("User is not member of a guild: " + invitingUser);
        }
        User invitee = userService.getUser(userName);
        if (invitee == null || !invitingUser.isRegistrationComplete()) {
            throw new NoSuchUserException(userName);
        }
        if (getGuild(invitee) != null) {
            throw new UserIsAlreadyGuildMemberException();
        }
        // Ignore double invitation
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuildInvitation.class);
        criteria.add(Restrictions.eq("user", invitee));
        criteria.add(Restrictions.eq("dbGuild", hostGuild));
        criteria.setProjection(Projections.rowCount());
        if (((Number) criteria.uniqueResult()).intValue() == 0) {
            DbGuildInvitation dbGuildInvitation = new DbGuildInvitation();
            dbGuildInvitation.setTimeStamp();
            dbGuildInvitation.setUser(invitee);
            dbGuildInvitation.setDbGuild(hostGuild);
            sessionFactory.getCurrentSession().save(dbGuildInvitation);
            historyService.addGuildInvitation(invitingUser, invitee, hostGuild);
            UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
            userAttentionPacket.setGuildInvitation(UserAttentionPacket.Type.RAISE);
            planetSystemService.sendPacket(userService.getUserState(invitee), userAttentionPacket);
        }
        removeMembershipRequests(invitee, hostGuild);
    }

    @Override
    public void inviteUserToGuild(SimpleBase simpleBase) throws NoSuchUserException, InvitingUnregisteredBaseException, UserIsAlreadyGuildMemberException {
        User invitingUser = userService.getUser();
        if (invitingUser == null || !invitingUser.isRegistrationComplete()) {
            throw new IllegalArgumentException("User is not registered");
        }
        User partnerUser = userService.getUser(simpleBase);
        if (partnerUser == null || !partnerUser.isRegistrationComplete()) {
            if (planetSystemService.getServerPlanetServices(simpleBase).getBaseService().isAbandoned(simpleBase)) {
                throw new IllegalArgumentException("Base is isAbandoned: " + simpleBase);
            } else {
                sendMessage(planetSystemService.getServerPlanetServices(simpleBase).getBaseService().getUserState(simpleBase), "guildOfferedOnlyRegistered", invitingUser.getUsername(), partnerUser == null);
                throw new InvitingUnregisteredBaseException();
            }
        }
        inviteUserToGuild(partnerUser.getUsername());
    }

    @Override
    @Transactional
    public SimpleGuild joinGuild(int guildId) {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        if (getGuild(user) != null) {
            throw new IllegalStateException("User has already a guild: " + user);
        }
        DbGuild dbGuild = getGuild(guildId);
        if (dbGuild == null) {
            throw new IllegalArgumentException("Guild does not exist: " + guildId);
        }
        DbGuildInvitation dbGuildInvitation = getGuildInvitation(user, dbGuild);
        if (dbGuildInvitation == null) {
            throw new IllegalStateException("User does not have an invitation to guild. User: " + user + " Guild: " + dbGuild);
        }
        dbGuild.addMember(user, GuildMemberInfo.Rank.MEMBER);
        sessionFactory.getCurrentSession().save(dbGuild);
        removeGuildInvitations(user);
        removeMembershipRequests(user, null);
        historyService.addGuildJoined(user, dbGuild);
        updateBaseService(dbGuild, user);
        return dbGuild.createSimpleGuild();
    }

    @Override
    @Transactional
    public List<GuildDetailedInfo> dismissGuildInvitation(int guildId) {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        DbGuild dbGuild = getGuild(guildId);
        if (dbGuild == null) {
            throw new IllegalArgumentException("Guild does not exist: " + guildId);
        }
        DbGuildInvitation dbGuildInvitation = getGuildInvitation(user, dbGuild);
        if (dbGuildInvitation == null) {
            throw new IllegalStateException("User does not have an invitation to guild. User: " + user + " Guild: " + dbGuild);
        }
        sessionFactory.getCurrentSession().delete(dbGuildInvitation);
        historyService.addGuildDismissInvitation(user, dbGuild);
        return getGuildInvitations();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GuildDetailedInfo> getGuildInvitations() {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuildInvitation.class);
        criteria.add(Restrictions.eq("user", user));
        Criteria subCriteria = criteria.createCriteria("dbGuild");
        subCriteria.addOrder(Order.asc("name"));
        List<DbGuildInvitation> dbGuildInvitations = criteria.list();
        List<GuildDetailedInfo> invitations = new ArrayList<>();
        if (dbGuildInvitations != null && !dbGuildInvitations.isEmpty()) {
            for (DbGuildInvitation dbGuildInvitation : dbGuildInvitations) {
                invitations.add(dbGuildInvitation.getDbGuild().createGuildDetailedInfo());
            }
        }
        return invitations;
    }

    @Override
    public CrystalCostInfo getCreateGuildCrystalCost() throws NoSuchPropertyException, WrongPropertyTypeException {
        int crystalCost = propertyService.getIntProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST);
        return new CrystalCostInfo(crystalCost, userService.getUserState().getCrystals());
    }

    @Override
    @Transactional
    public void guildMembershipRequest(int guildId, String text) {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        if (getGuild(user) != null) {
            throw new IllegalStateException("User has already a guild: " + user);
        }
        DbGuild dbGuild = getGuild(guildId);
        if (dbGuild == null) {
            throw new IllegalArgumentException("Guild does not exist: " + guildId);
        }

        // Ignore double requests
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuildMembershipRequest.class);
        criteria.add(Restrictions.eq("user", user));
        criteria.add(Restrictions.eq("dbGuild", dbGuild));
        criteria.setProjection(Projections.rowCount());
        if (((Number) criteria.uniqueResult()).intValue() == 0) {
            DbGuildMembershipRequest dbGuildMembershipRequest = new DbGuildMembershipRequest();
            dbGuildMembershipRequest.setTimeStamp();
            dbGuildMembershipRequest.setUser(user);
            dbGuildMembershipRequest.setDbGuild(dbGuild);
            dbGuildMembershipRequest.setText(text);
            sessionFactory.getCurrentSession().save(dbGuildMembershipRequest);
            historyService.addGuildMembershipRequest(user, dbGuild);
            sendRequestUserAttentionPacket(dbGuild);
        }
    }

    private void sendRequestUserAttentionPacket(DbGuild dbGuild) {
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        userAttentionPacket.setGuildMembershipRequest(UserAttentionPacket.Type.RAISE);
        for (DbGuildMember dbGuildMember : dbGuild.getGuildMembers()) {
            planetSystemService.sendPacket(userService.getUserState(dbGuildMember.getUser()), userAttentionPacket);
        }
    }

    @Override
    @Transactional
    public FullGuildInfo dismissGuildMemberRequest(int userId) {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            throw new IllegalStateException("User has no guild: " + user);
        }
        User dismissUser = userService.getUser(userId);
        if (dismissUser == null || !dismissUser.isRegistrationComplete()) {
            throw new IllegalStateException("Dismiss user id does not exist or is not registered: " + userId);
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuildMembershipRequest.class);
        criteria.add(Restrictions.eq("user", dismissUser));
        criteria.add(Restrictions.eq("dbGuild", dbGuild));
        DbGuildMembershipRequest dbGuildMembershipRequest = (DbGuildMembershipRequest) criteria.uniqueResult();
        if (dbGuildMembershipRequest == null) {
            throw new IllegalStateException("No membership request for user: " + dismissUser + " to guild: " + dbGuild);
        }
        sessionFactory.getCurrentSession().delete(dbGuildMembershipRequest);
        historyService.addDismissGuildMemberRequest(user, dismissUser, dbGuild);
        return getFullGuildInfo(dbGuild.getId());
    }

    @Override
    @Transactional
    public FullGuildInfo kickGuildMember(int userId) {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            throw new IllegalStateException("User has no guild: " + user);
        }
        User userToKick = userService.getUser(userId);
        if (userToKick == null || !userToKick.isRegistrationComplete()) {
            throw new IllegalArgumentException("User to kick id does not exist or is not registered: " + userId);
        }
        DbGuild tmpGuild = getGuild(userToKick);
        if (tmpGuild == null) {
            throw new IllegalStateException("User to kick has no guild: " + userToKick);
        }
        if (!dbGuild.equals(tmpGuild)) {
            throw new IllegalStateException("User to kick and user are nor in the same guild. User to kick: " + userToKick + " Guild: " + tmpGuild + " User: " + user + " Guild: " + dbGuild);
        }
        if (!dbGuild.canKickMember(user, userToKick)) {
            throw new IllegalStateException("User " + user + " has not enough permission to kick: " + userToKick + " from Guild: " + dbGuild);
        }

        dbGuild.removeMember(userToKick);
        sessionFactory.getCurrentSession().save(dbGuild);
        updateBaseService(null, userToKick);
        handleNewEnemies(dbGuild, userToKick);
        sendGuildNoPackage(userToKick);
        historyService.addGuildMemberKicked(user, userToKick, dbGuild);
        return getFullGuildInfo(dbGuild.getId());
    }

    @Override
    @Transactional
    public FullGuildInfo changeGuildMemberRank(int userId, GuildMemberInfo.Rank rank) {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            throw new IllegalStateException("User has no guild: " + user);
        }
        if (rank == null || rank == GuildMemberInfo.Rank.PRESIDENT) {
            throw new IllegalArgumentException("Invalid rank: " + rank);
        }
        User userToChange = userService.getUser(userId);
        if (userToChange == null || !userToChange.isRegistrationComplete()) {
            throw new IllegalArgumentException("User to change id does not exist or is not registered: " + userId);
        }
        DbGuild tmpGuild = getGuild(userToChange);
        if (tmpGuild == null) {
            throw new IllegalStateException("User to change has no guild: " + userToChange);
        }
        if (!dbGuild.equals(tmpGuild)) {
            throw new IllegalStateException("User to change and user are nor in the same guild. User to change: " + userToChange + " Guild: " + tmpGuild + " User: " + user + " Guild: " + dbGuild);
        }
        if (dbGuild.getDbGuildMember(user).getRank() != GuildMemberInfo.Rank.PRESIDENT) {
            throw new IllegalStateException("Only guild president can change the member rank: " + user);
        }
        if (dbGuild.getDbGuildMember(userToChange).getRank() == GuildMemberInfo.Rank.PRESIDENT) {
            throw new IllegalStateException("Guild president rank can not be change: " + userToChange);
        }
        dbGuild.getDbGuildMember(userToChange).setRank(rank);
        sessionFactory.getCurrentSession().save(dbGuild);
        historyService.addChangeGuildMemberRank(user, userToChange, rank, dbGuild);
        return getFullGuildInfo(dbGuild.getId());
    }

    @Override
    @Transactional
    public FullGuildInfo saveGuildText(String text) {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            throw new IllegalStateException("User has no guild: " + user);
        }
        if (dbGuild.getDbGuildMember(user).getRank() != GuildMemberInfo.Rank.PRESIDENT) {
            throw new IllegalStateException("Only guild president can change guild text: " + user);
        }
        dbGuild.setText(Jsoup.clean(text, Whitelist.basicWithImages()));
        sessionFactory.getCurrentSession().save(dbGuild);
        historyService.addGuildTextChanged(user, text, dbGuild);
        return getFullGuildInfo(dbGuild.getId());
    }

    @Override
    public VerificationRequestCallback.ErrorResult isGuildNameValid(String guildName) {
        if (guildName.trim().length() < 3) {
            return VerificationRequestCallback.ErrorResult.TO_SHORT;
        }
        if (getGuild(guildName) != null) {
            return VerificationRequestCallback.ErrorResult.ALREADY_USED;
        }
        return null;
    }

    @Override
    public SearchGuildsResult searchGuilds(int start, int length, String guildNameQuery) {
        guildNameQuery = guildNameQuery + "%";
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        // Query for guilds
        CriteriaQuery<DbGuild> dbGuildQuery = criteriaBuilder.createQuery(DbGuild.class);
        Root<DbGuild> from = dbGuildQuery.from(DbGuild.class);
        CriteriaQuery<DbGuild> dbGuildSelect = dbGuildQuery.select(from);
        Predicate predicate = criteriaBuilder.like(from.<String>get("name"), guildNameQuery);
        dbGuildQuery.where(predicate);
        dbGuildQuery.orderBy(criteriaBuilder.asc(from.<String>get("name")));
        TypedQuery<DbGuild> typedDbGuildQuery = entityManagerFactory.createEntityManager().createQuery(dbGuildSelect);
        typedDbGuildQuery.setMaxResults(length);
        typedDbGuildQuery.setFirstResult(start);
        List<DbGuild> dbGuilds = typedDbGuildQuery.getResultList();
        // Query for total row count
        CriteriaQuery<Long> longQuery = criteriaBuilder.createQuery(Long.class);
        from = longQuery.from(DbGuild.class);
        predicate = criteriaBuilder.like(from.<String>get("name"), guildNameQuery);
        longQuery.where(predicate);
        CriteriaQuery<Long> longSelect = longQuery.select(criteriaBuilder.count(from));
        TypedQuery<Long> typedLongQuery = entityManagerFactory.createEntityManager().createQuery(longSelect);
        Long totalRowCount = typedLongQuery.getSingleResult();
        // Handle answer
        SearchGuildsResult searchGuildsResult = new SearchGuildsResult();
        searchGuildsResult.setStartRow(start);
        List<GuildDetailedInfo> guildDetailedInfos = new ArrayList<>();
        if (dbGuilds != null) {
            for (DbGuild dbGuild : dbGuilds) {
                guildDetailedInfos.add(dbGuild.createGuildDetailedInfo());
            }
        }
        searchGuildsResult.setGuildDetailedInfos(guildDetailedInfos);
        searchGuildsResult.setTotalRowCount(totalRowCount.intValue());
        return searchGuildsResult;
    }

    @Override
    public SimpleGuild getSimpleGuild() {
        User user = userService.getUser();
        if (user == null) {
            return null;
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            return null;
        }
        return dbGuild.createSimpleGuild();
    }

    @Override
    @Transactional
    public void leaveGuild() {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            throw new IllegalStateException("User has no guild: " + user);
        }
        if (dbGuild.getDbGuildMember(user).getRank() == GuildMemberInfo.Rank.PRESIDENT) {
            throw new IllegalStateException("President can not leave the guild: " + user);
        }
        dbGuild.removeMember(user);
        historyService.addGuildLeft(user, dbGuild);
        sessionFactory.getCurrentSession().save(dbGuild);
        updateBaseService(null, user);
        handleNewEnemies(dbGuild, user);
    }

    @Override
    @Transactional
    public void closeGuild() {
        User user = userService.getUser();
        if (user == null || !user.isRegistrationComplete()) {
            throw new IllegalStateException("User is not registered");
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            throw new IllegalStateException("User has no guild: " + user);
        }
        if (dbGuild.getDbGuildMember(user).getRank() != GuildMemberInfo.Rank.PRESIDENT) {
            throw new IllegalStateException("Only president can close guild: " + user);
        }
        removeMembershipRequests(null, dbGuild);
        removeGuildInvitations(dbGuild);
        Collection<User> exMembers = new ArrayList<>();
        for (DbGuildMember dbGuildMember : dbGuild.getGuildMembers()) {
            exMembers.add(dbGuildMember.getUser());
            if (!user.equals(dbGuildMember.getUser())) {
                historyService.addKickedGuildClosed(user, dbGuildMember.getUser(), dbGuild);
                sendGuildNoPackage(dbGuildMember.getUser());
            }
            updateBaseService(null, dbGuildMember.getUser());
        }
        historyService.addGuildClosed(user, dbGuild);
        sessionFactory.getCurrentSession().delete(dbGuild);
        handleNewEnemies(exMembers);
    }

    @Override
    public void fillUserAttentionPacket(User user, UserAttentionPacket userAttentionPacket) {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        // Query for total row count in invitations
        CriteriaQuery<Long> longQuery = criteriaBuilder.createQuery(Long.class);
        Root<DbGuildInvitation> fromInvitation = longQuery.from(DbGuildInvitation.class);
        Predicate predicateUser = criteriaBuilder.equal(fromInvitation.<String>get("user"), user);
        longQuery.where(predicateUser);
        CriteriaQuery<Long> longSelect = longQuery.select(criteriaBuilder.count(fromInvitation));
        TypedQuery<Long> typedLongQuery = entityManagerFactory.createEntityManager().createQuery(longSelect);
        Long totalRowCount = typedLongQuery.getSingleResult();
        if (totalRowCount > 0) {
            userAttentionPacket.setGuildInvitation(UserAttentionPacket.Type.RAISE);
        }
        // Query for total row count in membership requests
        DbGuild dbGuild = getGuild(user);
        if (dbGuild != null) {
            longQuery = criteriaBuilder.createQuery(Long.class);
            Root<DbGuildMembershipRequest> fromRequests = longQuery.from(DbGuildMembershipRequest.class);
            Predicate predicateGuild = criteriaBuilder.equal(fromRequests.<String>get("dbGuild"), dbGuild);
            longQuery.where(predicateGuild);
            longSelect = longQuery.select(criteriaBuilder.count(fromRequests));
            typedLongQuery = entityManagerFactory.createEntityManager().createQuery(longSelect);
            totalRowCount = typedLongQuery.getSingleResult();
            if (totalRowCount > 0) {
                userAttentionPacket.setGuildMembershipRequest(UserAttentionPacket.Type.RAISE);
            }
        }
    }

    @Override
    public SimpleGuild getGuildId(UserState userState) {
        if (userState == null) {
            return null;
        }
        if (HibernateUtil.hasOpenSession(sessionFactory)) {
            return getGuildIdInSession(userState);
        } else {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                return getGuildIdInSession(userState);
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    @Override
    public Set<SimpleBase> getGuildBases(UserState userState, int planetId) {
        User user = userService.getUser(userState);
        if (user == null) {
            return Collections.emptySet();
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            return Collections.emptySet();
        }
        Set<SimpleBase> guildBases = new HashSet<>();
        for (DbGuildMember member : dbGuild.getGuildMembers()) {
            SimpleBase simpleBase = getSimpleBase(member.getUser());
            if (simpleBase != null && simpleBase.getPlanetId() == planetId) {
                guildBases.add(simpleBase);
            }
        }
        return guildBases;
    }

    @SuppressWarnings("unchecked")
    private DbGuild getGuild(User user) {
        Criteria rootCriteria = sessionFactory.getCurrentSession().createCriteria(DbGuild.class);
        Criteria memberQuery = rootCriteria.createCriteria("guildMembers");
        memberQuery.add(Restrictions.eq("user", user));
        List<DbGuild> guilds = rootCriteria.list();
        if (guilds == null || guilds.isEmpty()) {
            return null;
        } else {
            if (guilds.size() > 1) {
                log.warn("User is member of more than one guild: " + user);
            }
            return guilds.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    private DbGuild getGuild(String name) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuild.class);
        criteria.add(Restrictions.eq("name", name));
        List<DbGuild> guilds = criteria.list();
        if (guilds == null || guilds.isEmpty()) {
            return null;
        } else {
            if (guilds.size() > 1) {
                log.warn("There are more then on gild with the name: " + name);
            }
            return guilds.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    private DbGuild getGuild(int guildId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuild.class);
        criteria.add(Restrictions.eq("id", guildId));
        List<DbGuild> guilds = criteria.list();
        if (guilds == null || guilds.isEmpty()) {
            return null;
        } else {
            if (guilds.size() > 1) {
                log.warn("There are more then on gild with the id: " + guildId);
            }
            return guilds.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    private DbGuildInvitation getGuildInvitation(User user, DbGuild dbGuild) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuildInvitation.class);
        criteria.add(Restrictions.eq("user", user));
        criteria.add(Restrictions.eq("dbGuild", dbGuild));
        List<DbGuildInvitation> dbGuildInvitations = criteria.list();
        if (dbGuildInvitations == null || dbGuildInvitations.isEmpty()) {
            return null;
        } else {
            if (dbGuildInvitations.size() > 1) {
                log.warn("There are more then on guild invitations for user and guild. User: " + user + " Guild: " + dbGuild);
            }
            return dbGuildInvitations.get(0);
        }
    }

    private SimpleGuild getGuildIdInSession(UserState userState) {
        User user = userService.getUser(userState);
        if (user == null) {
            return null;
        }
        DbGuild dbGuild = getGuild(user);
        if (dbGuild == null) {
            return null;
        }
        return dbGuild.createSimpleGuild();
    }

    private void removeGuildInvitations(User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuildInvitation.class);
        criteria.add(Restrictions.eq("user", user));
        for (Object dbGuildInvitation : criteria.list()) {
            sessionFactory.getCurrentSession().delete(dbGuildInvitation);
        }
    }

    private void removeGuildInvitations(DbGuild dbGuild) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuildInvitation.class);
        criteria.add(Restrictions.eq("dbGuild", dbGuild));
        for (Object dbGuildInvitation : criteria.list()) {
            sessionFactory.getCurrentSession().delete(dbGuildInvitation);
        }
    }

    private void removeMembershipRequests(User user, DbGuild dbGuild) {
        if (user == null && dbGuild == null) {
            throw new IllegalArgumentException("user == null && dbGuild == null");
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbGuildMembershipRequest.class);
        if (user != null) {
            criteria.add(Restrictions.eq("user", user));
        }
        if (dbGuild != null) {
            criteria.add(Restrictions.eq("dbGuild", dbGuild));
        }
        for (Object dbGuildMembershipRequest : criteria.list()) {
            sessionFactory.getCurrentSession().delete(dbGuildMembershipRequest);
        }
    }

    private void updateBaseService(DbGuild dbGuild, User user) {
        SimpleGuild simpleGuild = null;
        if (dbGuild != null) {
            simpleGuild = dbGuild.createSimpleGuild();
        }
        SimpleBase simpleBase = getSimpleBase(user);
        if (simpleBase == null) {
            // User has no base
            UserState userState = userService.getUserState(user);
            ServerPlanetServices serverPlanetServices = planetSystemService.getPlanet4BaselessConnection(userState);
            if (serverPlanetServices != null) {
                serverPlanetServices.getBaseService().sendGuildChanged4FakeBase(userState, simpleGuild);
            }
        } else {
            planetSystemService.getServerPlanetServices(simpleBase).getBaseService().setGuild(simpleBase, simpleGuild);
            planetSystemService.getServerPlanetServices(simpleBase).getBaseService().sendGuildChanged(simpleBase);
        }
    }

    private void handleNewEnemies(DbGuild dbGuild, User newGuildEnemy) {
        Set<SimpleBase> simpleBases = new HashSet<>();
        SimpleBase simpleBase = getSimpleBase(newGuildEnemy);
        if (simpleBase == null) {
            return;
        }
        simpleBases.add(simpleBase);
        for (DbGuildMember dbGuildMember : dbGuild.getGuildMembers()) {
            SimpleBase guildMemberBase = getSimpleBase(dbGuildMember.getUser());
            if (guildMemberBase != null && simpleBase.getPlanetId() == guildMemberBase.getPlanetId()) {
                simpleBases.add(guildMemberBase);
            }
        }
        if (simpleBases.size() > 1) {
            planetSystemService.getServerPlanetServices(simpleBase).getItemService().onGuildChanged(simpleBases);
        }
    }

    private void handleNewEnemies(Collection<User> newEnemies) {
        Map<Integer, Set<SimpleBase>> allBases = new HashMap<>();
        for (User newEnemy : newEnemies) {
            SimpleBase simpleBase = getSimpleBase(newEnemy);
            if (simpleBase != null) {
                Set<SimpleBase> planetBases = allBases.get(simpleBase.getPlanetId());
                if (planetBases == null) {
                    planetBases = new HashSet<>();
                    allBases.put(simpleBase.getPlanetId(), planetBases);
                }
                planetBases.add(simpleBase);
            }
        }

        for (Map.Entry<Integer, Set<SimpleBase>> entry : allBases.entrySet()) {
            if (entry.getValue().size() > 1) {
                planetSystemService.getServerPlanetServices(entry.getKey()).getItemService().onGuildChanged(entry.getValue());
            }
        }
    }

    private SimpleBase getSimpleBase(User user) {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(user);
        if (serverPlanetServices == null) {
            return null;
        }
        return serverPlanetServices.getBaseService().getSimpleBase(user);
    }

    private void sendGuildNoPackage(User user) {
        StorablePacket storablePacket = new StorablePacket();
        storablePacket.setType(StorablePacket.Type.GUILD_LOST);
        planetSystemService.sendPacket(userService.getUserState(user), storablePacket);
    }

    private void sendMessage(UserState userState, String key, String arg, boolean showRegisterDialog) {
        Object[] args = null;
        if (arg != null) {
            args = new Object[]{arg};
        }
        planetSystemService.sendMessage(userState, key, args, showRegisterDialog);
    }
}
