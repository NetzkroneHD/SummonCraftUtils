package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.database.cache.packet.broadcast.BroadcastPacket;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;

import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodesInComponents;

public class BroadcastManager extends Manager {

    public static final String GLOBAL_SERVER = "global";

    public BroadcastManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public void receivedBroadcast(BroadcastPacket broadcastPacket) {
        switch (broadcastPacket.getType()) {
            case TITLE:
                final Title title = hitBox.getProxy().createTitle();
                title.title(translateHexCodesInComponents(broadcastPacket.getMessage()));
                title.subTitle(new TextComponent(""));
                title.fadeIn(20);
                title.stay(20 * 4);
                title.fadeOut(20);

                sendTitle(broadcastPacket.getServer(), title, broadcastPacket.getSound());
                break;
            case SUBTITLE:
                final Title subtitle = hitBox.getProxy().createTitle();
                subtitle.title(new TextComponent("§e"));
                subtitle.fadeOut(20);
                subtitle.subTitle(translateHexCodesInComponents(broadcastPacket.getMessage()));
                subtitle.stay(20 * 4);
                subtitle.fadeIn(20);

                sendTitle(broadcastPacket.getServer(), subtitle, broadcastPacket.getSound());
                break;
            default:
                sendMessage(broadcastPacket.getServer(), translateHexCodesInComponents(broadcastPacket.getMessage()), broadcastPacket.getSound(), broadcastPacket.getType().getChatMessageType());
                break;

        }
    }

    public void sendBroadcast(BroadcastPacket broadcastPacket) {
        hitBox.getRedisManager().getRedisClient().sendPacket(broadcastPacket);
    }

    private void sendMessage(String server, TextComponent message, GlobalSound sound, ChatMessageType type) {
            if (server.equalsIgnoreCase(GLOBAL_SERVER)) {
                hitBox.getPlayers().forEach(hitBoxPlayer -> {
                    hitBoxPlayer.getPlayer().sendMessage(type, message);
                    hitBoxPlayer.playSound(sound);
                });
            } else {
                hitBox.getPlayers().stream().filter(hitBoxPlayer -> hitBoxPlayer.getServerName().equalsIgnoreCase(server)).forEach(hitBoxPlayer -> {
                    hitBoxPlayer.getPlayer().sendMessage(type, message);
                    hitBoxPlayer.playSound(sound);
                });
            }
    }

    private void sendTitle(String server, Title title, GlobalSound sound) {
        if (server.equalsIgnoreCase(GLOBAL_SERVER)) {
            hitBox.getPlayers().forEach(hitBoxPlayer -> {
                hitBoxPlayer.getPlayer().sendTitle(title);
                hitBoxPlayer.playSound(sound);
            });
        } else {
            hitBox.getPlayers().stream().filter(hitBoxPlayer -> hitBoxPlayer.getServerName().equalsIgnoreCase(server)).forEach(hitBoxPlayer -> {
                hitBoxPlayer.getPlayer().sendTitle(title);
                hitBoxPlayer.playSound(sound);
            });
        }
    }

}
