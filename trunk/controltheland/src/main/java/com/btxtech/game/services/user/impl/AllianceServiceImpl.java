package com.btxtech.game.services.user.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.AllianceOfferPacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.ServerConnectionService;
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
        if (user == null) {
            sendMessage(planetSystemService.getServerPlanetServices().getBaseService().getBase().getSimpleBase(), "Only registered user can form alliances.", true);
            return;
        }
        User partnerUser = userService.getUser(partner);
        if (partnerUser == null) {
            sendMessage(partner, user.getUsername() + " offers you an alliance. Only registered user can form alliances.", true);
            sendMessage(user, "The player '" + planetSystemService.getServerPlanetServices(partner).getBaseService().getBaseName(partner) + "' is not registered. Only registered user can form alliances. Use the chat to persuade him to register!", false);
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
        sendMessage(partnerUser, "The user " + user.getUsername() + " has accepted your alliance", false);
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
        sendMessage(partnerUser, "The user " + user.getUsername() + " has rejected your alliance", false);
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
        sendMessage(partnerUser, "The user " + user.getUsername() + " has broken the alliance", false);
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
            if(allianceBase == null) {
                continue;
            }
            if(allianceBase.getPlanetId() != simpleBase.getPlanetId()) {
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

    private void sendMessage(User user, String message, boolean showRegisterDialog) {
        SimpleBase simpleBase = getSimpleBase(user);
        sendMessage(simpleBase, message, showRegisterDialog);
    }

    private void sendMessage(SimpleBase simpleBase, String message, boolean showRegisterDialog) {
        Message accepted = new Message();
        accepted.setMessage(message);
        accepted.setShowRegisterDialog(showRegisterDialog);
        if (simpleBase != null) {
            planetSystemService.getServerPlanetServices(simpleBase).getConnectionService().sendPacket(simpleBase, accepted);
        }
    }

    private AllianceOfferPacket createAllianceOfferPackage(User user) {
        AllianceOfferPacket allianceOfferPacket = new AllianceOfferPacket();
        allianceOfferPacket.setActorUserName(user.getUsername());
        return allianceOfferPacket;
    }

    private SimpleBase getSimpleBase(User user) {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(user);
        if(serverPlanetServices == null) {
            return null;
        }
        return serverPlanetServices.getBaseService().getSimpleBase(user);
    }
}
