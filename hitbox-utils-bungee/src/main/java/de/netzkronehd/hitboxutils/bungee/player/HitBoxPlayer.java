package de.netzkronehd.hitboxutils.bungee.player;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.utils.Cooldown;
import de.netzkronehd.hitboxutils.database.cache.packet.sound.GlobalSoundPacket;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.discord.verification.VerificationRequest;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.player.StaffSettings;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.translation.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;

@RequiredArgsConstructor
@Data
public class HitBoxPlayer {

    private final HitBoxUtils hitBoxUtils;
    private final ProxiedPlayer player;
    private final Map<Cooldown, Long> cooldown = new EnumMap<>(Cooldown.class);
    private final Audience sender;

    private String lastMessage;
    private Long lastMessageTime;
    private boolean teamChat;
    private PlayerPlaytime playerPlaytime;
    private StaffSettings staffSettings;
    private DiscordVerification discordVerification;
    private VerificationRequest verificationRequest;

    public boolean isStaff() {
        try {
            return player.hasPermission("elmobox.staff") || hasPermission("staff");
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isTeamChatAllowed() {
        return isStaff() || hasPermission("teamchat");
    }

    public boolean hasPermission(String perm) {
        return player.hasPermission(Constants.PERMISSION_PREFIX+ perm) || player.hasPermission(Constants.PERMISSION_PREFIX + "*");
    }

    public void sendMessage(Component component) {
        sender.sendMessage(component);
    }

    public void sendRawMessage(String msg, String hoverText, String clickText) {
        sender.sendMessage(text(msg)
                .hoverEvent(text(hoverText))
                .clickEvent(runCommand(clickText))
        );

    }

    public void sendMessage(String msg, String hoverText, String clickText) {
        sendMessage(msg, hoverText, clickText, ClickEvent.Action.RUN_COMMAND);
    }

    public void sendMessage(String msg, String hoverText, String clickText, ClickEvent.Action clickAction) {
        sender.sendMessage(text(Messages.PREFIX+msg)
                .hoverEvent(text(hoverText))
                .clickEvent(clickEvent(clickAction, clickText))
        );
    }

    public void playSound(GlobalSound sound) {
        playSound(sound, 1, 1);
    }

    public void playSound(GlobalSound sound, float volume, float pitch) {
        hitBoxUtils.getRedisManager().getRedisClient().sendPacket(new GlobalSoundPacket(hitBoxUtils.getRedisManager().getServerName(), getUniqueId(), sound, volume, pitch));
    }

    public void sendRawMessage(String msg) {
        sender.sendMessage(text(msg).color(NamedTextColor.GRAY));
    }

    public void sendArrow(String msg) {
        sendRawMessage("§8" + Messages.ARROW_RIGHT + "§7 " + msg);
    }

    public void sendLine() {
        sendRawMessage("§8§m"+" ".repeat(30)+"§r");
    }

    public void sendMessage(String msg) {
        sendRawMessage(Messages.PREFIX + msg);
    }

    public void sendMessage(TextComponent textComponent) {
        player.sendMessage(textComponent);
    }

    public void sendMessage(Object msg) {
        sendMessage(String.valueOf(msg));
    }

    public void sendMessageColored(String msg) {
        sendMessage(Message.formatColoredValue(Messages.PREFIX + msg));
    }

    public void sendMessage(Messages messages) {
        sendMessage(messages.getValue());
    }

    public void sendMessage(Messages message, String msg) {
        sendMessage(message + msg);
    }

    public void sendUsage(String msg) {
        sendMessage(Messages.USAGE + msg);
    }

    public String getPingText() {
        final int ping = player.getPing();
        if (ping <= 20) {
            return "§a" + ping;
        } else if (ping <= 80) {
            return "§2" + ping;
        } else if (ping <= 100) {
            return "§c" + ping;
        } else {
            return "§4" + ping;
        }
    }

    public boolean isVerified() {
        return discordVerification != null;
    }

    public String getPrefixAndName() {
        return getPrefix() + getName();
    }

    public String getServerName() {
        if (player.getServer() == null) {
            return "§cNot fully connected";
        }
        return player.getServer().getInfo().getName();
    }

    public String getProxy() {
        return hitBoxUtils.getRedisManager().getServerName();
    }

    public String getPrefix() {
        final String prefix = getPrimaryGroup().getCachedData().getMetaData().getPrefix();
        if (prefix == null) return "§e";
        return prefix;
    }

    public Set<HitBoxPlayer> getAltAccounts() {
        final String ip = getIp();
        if (ip == null) return Collections.emptySet();

        final Set<HitBoxPlayer> alts = new HashSet<>();
        for (HitBoxPlayer player : hitBoxUtils.getPlayers()) {
            final String toCheck = player.getIp();
            if (toCheck == null) continue;
            if (player.getUniqueId().equals(getUniqueId())) continue;
            if (player.isStaff()) continue;
            if (player.hasPermission("alts.bypass")) continue;
            if (!ip.equals(toCheck)) continue;
            alts.add(player);
        }
        return alts;
    }

    public Group getPrimaryGroup() {
        final User user = hitBoxUtils.getLuckPermsApi().getPlayerAdapter(ProxiedPlayer.class).getUser(player);
        return hitBoxUtils.getLuckPermsApi().getGroupManager().getGroup(user.getPrimaryGroup());
    }

    public Collection<Group> getGroups() {
        final User user = hitBoxUtils.getLuckPermsApi().getPlayerAdapter(ProxiedPlayer.class).getUser(player);
        return user.getInheritedGroups(user.getQueryOptions());
    }

    public String getFormattedPlaytime() {
        return Utils.getRemainingTimeInHours(getCurrentPlaytime());
    }

    public long getCurrentPlaytime() {
        return playerPlaytime.getPlaytime() + (System.currentTimeMillis() - playerPlaytime.getTimeJoined());
    }

    public String getIp() {
        if (player.getSocketAddress() instanceof final InetSocketAddress address) {
            return Utils.formatIpAddress(address.toString());
        }
        return null;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

}
