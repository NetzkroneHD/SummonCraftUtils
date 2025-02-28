package de.netzkronehd.hitboxutils.database.cache.model;

import net.md_5.bungee.api.ChatMessageType;

import java.util.Optional;

public enum BroadcastType {

    CHAT,
    SYSTEM,
    ACTION_BAR,
    TITLE,
    SUBTITLE;

    private ChatMessageType chatMessageType;

    BroadcastType() {
        try {
            this.chatMessageType = ChatMessageType.valueOf(this.name());
        } catch (IllegalArgumentException ex) {
            this.chatMessageType = null;
        }
    }

    public ChatMessageType getChatMessageType() {
        return chatMessageType;
    }

    public boolean isChatMessageType() {
        return chatMessageType != null;
    }

    public static Optional<BroadcastType> of(String name) {
        try {
            return Optional.of(valueOf(name));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

}
