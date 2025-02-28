package de.netzkronehd.hitboxutils.database.cache.packet.teamchat;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
@EqualsAndHashCode
public class TeamChatMessagePacket extends HitBoxPacket {

    private final TextComponent msg;

    public TeamChatMessagePacket(String source, TextComponent msg) {
        super(source);
        this.msg = msg;
    }

}
