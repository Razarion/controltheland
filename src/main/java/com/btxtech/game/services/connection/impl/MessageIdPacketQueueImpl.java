package com.btxtech.game.services.connection.impl;

import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.MessageIdPacketQueue;
import com.btxtech.game.services.mgmt.ServerI18nHelper;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbChatMessage;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * User: beat
 * Date: 02.04.2012
 * Time: 20:27:46
 */
@Component("MessageIdPacketQueue")
public class MessageIdPacketQueueImpl implements MessageIdPacketQueue {
    private static final int OFFLINE_CHAT_QUEUE_SIZE = 20;
    private static final int OFFLINE_CHAT_QUEUE_MIN_GLOBAL_MESSAGE_COUNT = (int) (OFFLINE_CHAT_QUEUE_SIZE * 0.25);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerI18nHelper serverI18nHelper;
    @Autowired
    private GuildService guildService;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private int lastMessageId;
    private final LinkedList<MessageIdPacket> queue = new LinkedList<>();

    @PostConstruct
    public void init() {
        try {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
            // Query for guilds
            CriteriaQuery<DbChatMessage> dbChatMessageQuery = criteriaBuilder.createQuery(DbChatMessage.class);
            Root<DbChatMessage> from = dbChatMessageQuery.from(DbChatMessage.class);
            Predicate predicate = criteriaBuilder.isNull(from.<String>get("guildId"));
            dbChatMessageQuery.where(predicate);
            dbChatMessageQuery.orderBy(criteriaBuilder.desc(from.<String>get("timeStamp")));
            CriteriaQuery<DbChatMessage> dbChatMessageSelect = dbChatMessageQuery.select(from);
            TypedQuery<DbChatMessage> typedDbChatMessageQuery = entityManagerFactory.createEntityManager().createQuery(dbChatMessageSelect);
            typedDbChatMessageQuery.setMaxResults(OFFLINE_CHAT_QUEUE_SIZE);
            List<DbChatMessage> dbChatMessages = typedDbChatMessageQuery.getResultList();
            synchronized (queue) {
                queue.clear();
                if (dbChatMessages != null) {
                    Collections.reverse(dbChatMessages);
                    for (DbChatMessage dbChatMessage : dbChatMessages) {
                        initAndPutMessage(dbChatMessage.createMessageIdPacket());
                    }
                }
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public List<MessageIdPacket> peekMessages(Integer lastMessageId, ChatMessageFilter chatMessageFilter) {
        List<MessageIdPacket> tmpPackets;
        synchronized (queue) {
            if (lastMessageId == null) {
                tmpPackets = new ArrayList<>(queue);
            } else {
                tmpPackets = new ArrayList<>();
                for (MessageIdPacket messageIdPacket : queue) {
                    if (messageIdPacket.getMessageId() > lastMessageId) {
                        tmpPackets.add(messageIdPacket);
                    } else {
                        break;
                    }
                }
            }
        }
        List<MessageIdPacket> result = new ArrayList<>();
        UserState userState = userService.getUserState();
        for (MessageIdPacket messageIdPacket : tmpPackets) {
            MessageIdPacket convertedPackage = (MessageIdPacket) convertPacketIfNecessary(messageIdPacket, chatMessageFilter, userState);
            if (convertedPackage != null) {
                result.add(convertedPackage);
            }
        }
        return result;
    }

    @Override
    public void initAndPutMessage(MessageIdPacket messageIdPacket) {
        synchronized (queue) {
            if (queue.size() >= OFFLINE_CHAT_QUEUE_SIZE) {
                removeMessage();
            }
            messageIdPacket.setMessageId(lastMessageId);
            queue.addFirst(messageIdPacket);
            lastMessageId++;
        }
    }

    private void removeMessage() {
        int global = 0;
        for (MessageIdPacket messageIdPacket : queue) {
            if (messageIdPacket instanceof ChatMessage) {
                ChatMessage chatMessage = (ChatMessage) messageIdPacket;
                if (!chatMessage.hasGuild()) {
                    global++;
                }
            }
        }

        if (global < OFFLINE_CHAT_QUEUE_MIN_GLOBAL_MESSAGE_COUNT) {
            for (ListIterator<MessageIdPacket> iterator = queue.listIterator(queue.size()); iterator.hasPrevious(); ) {
                MessageIdPacket messageIdPacket = iterator.previous();
                if (messageIdPacket instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage) messageIdPacket;
                    if (chatMessage.hasGuild()) {
                        iterator.remove();
                    }
                }
            }
        } else {
            queue.removeLast();
        }
    }

    @Override
    public void setFilterAndPutMessage(ChatMessage chatMessage, ChatMessageFilter chatMessageFilter) {
        User user = userService.getUser();
        if (user != null) {
            chatMessage.setName(user.getUsername());
            chatMessage.setUserId(user.getId());
        } else if (userService.getUserState().getBase() != null) {
            chatMessage.setName(planetSystemService.getServerPlanetServices().getBaseService().getBaseName());
        } else {
            chatMessage.setName(serverI18nHelper.getString("guest"));
        }
        if (chatMessageFilter == ChatMessageFilter.GUILD) {
            if (user == null) {
                throw new IllegalStateException("User is not registered: " + user);
            }
            SimpleGuild simpleGuild = guildService.getSimpleGuild();
            if (simpleGuild == null) {
                throw new IllegalStateException("User does not have a guild: " + user);
            }
            chatMessage.setGuildId(simpleGuild.getId());
        }
        initAndPutMessage(chatMessage);
    }

    @Override
    public Packet convertPacketIfNecessary(Packet packet, ChatMessageFilter chatMessageFilter, UserState userState) {
        if (!(packet instanceof ChatMessage)) {
            return packet;
        }
        ChatMessage chatMessage = (ChatMessage) packet;
        User user = userService.getUser(userState);
        Integer userId = null;
        if (user != null) {
            userId = user.getId();
        }
        switch (chatMessageFilter) {
            case GLOBAL: {
                if (chatMessage.hasGuild()) {
                    return null;
                } else {
                    ChatMessage copy = chatMessage.getCopy();
                    if (chatMessage.isSameUser(userId)) {
                        copy.setType(ChatMessage.Type.OWN);
                    } else {
                        if (chatMessage.hasUserId()) {
                            SimpleGuild simpleGuild = guildService.getGuildId(userState);
                            SimpleGuild otherGuild = guildService.getGuildId(userService.getUserState(userService.getUser(chatMessage.getUserId())));
                            if (simpleGuild != null && simpleGuild.equals(otherGuild)) {
                                copy.setType(ChatMessage.Type.GUILD);
                            } else {
                                copy.setType(ChatMessage.Type.ENEMY);
                            }
                        } else {
                            copy.setType(ChatMessage.Type.ENEMY);
                        }
                    }
                    return copy;
                }
            }
            case GUILD: {
                SimpleGuild simpleGuild = guildService.getGuildId(userState);
                if (simpleGuild == null) {
                    throw new IllegalStateException("User does not have a guild: " + userService.getUser(userState));
                }
                if (chatMessage.isSameGuild(simpleGuild.getId())) {
                    ChatMessage copy = chatMessage.getCopy();
                    if (chatMessage.isSameUser(userId)) {
                        copy.setType(ChatMessage.Type.OWN);
                    } else {
                        copy.setType(ChatMessage.Type.GUILD);
                    }
                    return copy;
                } else {
                    return null;
                }
            }
            default:
                throw new IllegalArgumentException("Unknown ChatMessageFilter: " + chatMessageFilter);
        }
    }

}