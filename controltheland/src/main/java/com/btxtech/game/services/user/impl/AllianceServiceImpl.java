package com.btxtech.game.services.user.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.AllianceOfferPacket;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.AllianceService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 24.04.12
 * Time: 10:13
 */
@Component("allianceService")
public class AllianceServiceImpl implements AllianceService {
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void proposeAlliance(SimpleBase partner) {
        User user = userService.getUser();
        if (user == null || !user.isAccountNonLocked()) {
            sendMessage(planetSystemService.getServerPlanetServices().getBaseService().getBase().getSimpleBase(), "alliancesOnlyRegistered", null, user == null);
            return;
        }
        User partnerUser = userService.getUser(partner);
        if (partnerUser == null || !partnerUser.isAccountNonLocked()) {
            sendMessage(partner, "alliancesOfferedOnlyRegistered", user.getUsername(), partnerUser == null);
            sendMessage(user, "alliancesOfferedNotRegistered", planetSystemService.getServerPlanetServices(partner).getBaseService().getBaseName(partner), false);
            return;
        }
        if (partnerUser.getAlliances().contains(user)) {
            throw new IllegalStateException("The user " + user + " has already an alliance with the user " + partnerUser);
        }
        if (!partnerUser.getAllianceOffers().contains(user)) {
            partnerUser.getAllianceOffers().add(user);
            userService.save(partnerUser);
            historyService.addAllianceOffered(user, partnerUser);
            planetSystemService.getServerPlanetServices(partner).getConnectionService().sendPacket(partner, createAllianceOfferPackage(user));
        }
    }

    @Override
    public void acceptAllianceOffer(String partnerUserName) {
        User user = userService.getUser();
        if (user == null) {
            throw new IllegalStateException("User is not registered");
        }
        User partnerUser = userService.getUser(partnerUserName);
        if (partnerUser == null) {
            throw new IllegalStateException("No such user");
        }
        if (!user.getAllianceOffers().contains(partnerUser)) {
            throw new IllegalStateException("Alliance offer does not exists. From: " + partnerUser + " to: " + user);
        }
        user.getAllianceOffers().remove(partnerUser);
        user.getAlliances().add(partnerUser);
        partnerUser.getAlliances().add(user);
        userService.save(user);
        userService.save(partnerUser);
        historyService.addAllianceOfferAccepted(user, partnerUser);
        updateBaseService(user);
        updateBaseService(partnerUser);
        sendAllianceChanged(user);
        sendAllianceChanged(partnerUser);
        sendMessage(partnerUser, "alliancesAccepted", user.getUsername(), false);
    }

    @Override
    public void rejectAllianceOffer(String partnerUserName) {
        User user = userService.getUser();
        if (user == null) {
            throw new IllegalStateException("User is not registered");
        }
        User partnerUser = userService.getUser(partnerUserName);
        if (partnerUser == null) {
            throw new IllegalStateException("No such user");
        }
        if (!user.getAllianceOffers().contains(partnerUser)) {
            throw new IllegalStateException("Alliance offer does not exists. From: " + partnerUser + " to: " + user);
        }
        user.getAllianceOffers().remove(partnerUser);
        userService.save(user);
        historyService.addAllianceOfferRejected(user, partnerUser);
        sendMessage(partnerUser, "alliancesRejected", user.getUsername(), false);
    }

    @Override
    public void breakAlliance(String partnerUserName) {
        User user = userService.getUser();
        if (user == null) {
            throw new IllegalStateException("User is not registered");
        }
        User partnerUser = userService.getUser(partnerUserName);
        if (partnerUser == null) {
            throw new IllegalStateException("No such user");
        }
        user.getAlliances().remove(partnerUser);
        partnerUser.getAlliances().remove(user);
        userService.save(user);
        userService.save(partnerUser);
        historyService.addAllianceBroken(user, partnerUser);
        updateBaseService(user);
        updateBaseService(partnerUser);
        sendAllianceChanged(user);
        sendAllianceChanged(partnerUser);
        handleNewEnemies(user, partnerUser);
        sendMessage(partnerUser, "alliancesBroken", user.getUsername(), false);
    }

    private void handleNewEnemies(User user1, User user2) {
        SimpleBase simpleBase1 = getSimpleBase(user1);
        if (simpleBase1 == null) {
            return;
        }
        SimpleBase simpleBase2 = getSimpleBase(user2);
        if (simpleBase2 == null) {
            return;
        }
        if (simpleBase1.getPlanetId() != simpleBase2.getPlanetId()) {
            return;
        }
        planetSystemService.getServerPlanetServices(simpleBase1).getItemService().onAllianceBroken(simpleBase1, simpleBase2);
    }

    @Override
    public void restoreAlliances() {
        for (User user : userService.getAllUsers()) {
            updateBaseService(user);
        }
    }

    @Override
    public Collection<AllianceOfferPacket> getPendingAllianceOffers() {
        Collection<AllianceOfferPacket> allianceOffers = new ArrayList<>();
        User user = userService.getUser();
        if (user == null) {
            return allianceOffers;
        }
        for (User allianceOffer : user.getAllianceOffers()) {
            allianceOffers.add(createAllianceOfferPackage(allianceOffer));
        }
        return allianceOffers;
    }

    @Override
    public void onBaseCreatedOrDeleted(String userName) {
        if (HibernateUtil.hasOpenSession(sessionFactory)) {
            onBaseCreatedOrDeletedInSession(userService.getUser(userName));
        } else {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                onBaseCreatedOrDeletedInSession(userService.getUser(userName));
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    @Override
    public void onMakeBaseAbandoned(SimpleBase simpleBase) {
        planetSystemService.getServerPlanetServices(simpleBase).getBaseService().setAlliances(simpleBase, new ArrayList<SimpleBase>());
        planetSystemService.getServerPlanetServices(simpleBase).getBaseService().sendAlliancesChanged(simpleBase);
    }

    @Override
    public Collection<String> getAllAlliances() {
        User user = userService.getUser();
        if (user == null) {
            throw new IllegalStateException("User is not registered");
        }
        Collection<String> alliances = new ArrayList<>();
        for (User allianceUser : user.getAlliances()) {
            alliances.add(allianceUser.getUsername());
        }
        return alliances;
    }

    private void onBaseCreatedOrDeletedInSession(User user) {
        updateBaseService(user);
        sendAllianceChanged(user);
        for (User alliance : user.getAlliances()) {
            updateBaseService(alliance);
            sendAllianceChanged(alliance);
        }
    }

    private void updateBaseService(User user) {
        SimpleBase simpleBase = getSimpleBase(user);
        if (simpleBase == null) {
            // User has no base
            return;
        }
        Collection<SimpleBase> allianceBases = new ArrayList<>();
        for (User allianceUser : user.getAlliances()) {
            SimpleBase allianceBase = getSimpleBase(allianceUser);
            if (allianceBase == null) {
                continue;
            }
            if (allianceBase.getPlanetId() != simpleBase.getPlanetId()) {
                continue;
            }
            if (planetSystemService.getServerPlanetServices(allianceBase).getBaseService().isAlive(allianceBase)) {
                allianceBases.add(allianceBase);
            }
        }
        planetSystemService.getServerPlanetServices(simpleBase).getBaseService().setAlliances(simpleBase, allianceBases);
    }

    private void sendAllianceChanged(User user) {
        SimpleBase simpleBase = getSimpleBase(user);
        if (simpleBase == null) {
            // User has no base
            return;
        }
        planetSystemService.getServerPlanetServices(simpleBase).getBaseService().sendAlliancesChanged(simpleBase);
    }

    private void sendMessage(User user, String key, String arg, boolean showRegisterDialog) {
        SimpleBase simpleBase = getSimpleBase(user);
        sendMessage(simpleBase, key, arg, showRegisterDialog);
    }

    private void sendMessage(SimpleBase simpleBase, String key, String arg, boolean showRegisterDialog) {
        if (simpleBase != null) {
            Object[] args = null;
            if (arg != null) {
                args = new Object[]{arg};
            }
            planetSystemService.getServerPlanetServices(simpleBase).getConnectionService().sendMessage(simpleBase, key, args, showRegisterDialog);
        }
    }

    private AllianceOfferPacket createAllianceOfferPackage(User user) {
        AllianceOfferPacket allianceOfferPacket = new AllianceOfferPacket();
        allianceOfferPacket.setActorUserName(user.getUsername());
        return allianceOfferPacket;
    }

    private SimpleBase getSimpleBase(User user) {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(user);
        if (serverPlanetServices == null) {
            return null;
        }
        return serverPlanetServices.getBaseService().getSimpleBase(user);
    }
}
