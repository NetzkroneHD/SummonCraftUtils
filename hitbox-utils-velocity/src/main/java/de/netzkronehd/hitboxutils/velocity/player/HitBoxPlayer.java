package de.netzkronehd.hitboxutils.velocity.player;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.discord.verification.VerificationRequest;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.player.StaffSettings;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.utils.Cooldown;
import lombok.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

import java.util.*;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HitBoxPlayer {

    private final HitBoxUtils hitBoxUtils;
    private final Player player;
    private final Map<Cooldown, Long> cooldown = new HashMap<>();

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

    public boolean hasPermission(String perm) {
        return player.hasPermission(Constants.PERMISSION_PREFIX+ perm) || player.hasPermission(Constants.PERMISSION_PREFIX + "*");
    }

    public void sendMessage(Component tc) {
        player.sendMessage(tc);
    }

    public void sendRawMessage(String msg, String hoverText, String clickText) {
        player.sendMessage(
                formatColoredValue(msg)
                        .clickEvent(runCommand(clickText))
                        .hoverEvent(showText(formatColoredValue(hoverText)))
        );
    }

    public void sendMessage(String msg, String hoverText, String clickText) {
        sendMessage(msg, hoverText, clickText, ClickEvent.Action.RUN_COMMAND);
    }

    public void sendMessage(String msg, String hoverText, String clickText, ClickEvent.Action clickAction) {
        player.sendMessage(
                formatColoredValue(Messages.PREFIX+msg)
                        .clickEvent(clickEvent(clickAction, clickText))
                        .hoverEvent(showText(formatColoredValue(hoverText)))
        );
    }

    public void playSound(GlobalSound sound) {
        playSound(sound, 1, 1);
    }

    public void playSound(GlobalSound sound, float volume, float pitch) {
        player.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, sound.getKey()), Sound.Source.MASTER, volume, pitch));
    }

    public void sendRawMessage(String msg) {
        sendMessage(formatColoredValue("§7" + msg));
    }

    public void sendArrow(String msg) {
        sendRawMessage("§8" + Messages.ARROW_RIGHT + "§7 " + msg);
    }

    public void sendLine() {
        sendRawMessage("§8§m                            §r");
    }

    public void sendMessage(String msg) {
        sendRawMessage(Messages.PREFIX + msg);
    }

    public void sendMessageColored(String msg) {
        sendMessage(formatColoredValue(Messages.PREFIX + msg));
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
        final long ping = player.getPing();
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
        final ServerConnection connection = player.getCurrentServer().orElse(null);
        return (connection == null ? "§cNot fully connected":connection.getServerInfo().getName());
    }

    public boolean isChatMutationAllowed() {
        return player.getProtocolVersion().getProtocol() <= ProtocolVersion.MINECRAFT_1_19.getProtocol();
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
        final User user = hitBoxUtils.getLuckPermsApi().getPlayerAdapter(Player.class).getUser(player);
        return hitBoxUtils.getLuckPermsApi().getGroupManager().getGroup(user.getPrimaryGroup());
    }

    public Collection<Group> getGroups() {
        final User user = hitBoxUtils.getLuckPermsApi().getPlayerAdapter(Player.class).getUser(player);
        return user.getInheritedGroups(user.getQueryOptions());
    }

    public String getFormattedPlaytime() {
        return Utils.getRemainingTimeInHours(getCurrentPlaytime());
    }

    public long getCurrentPlaytime() {
        return playerPlaytime.getPlaytime() + (System.currentTimeMillis() - playerPlaytime.getTimeJoined());
    }

    public String getIp() {
        return Utils.formatIpAddress(player.getRemoteAddress().toString());
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getUsername();
    }

    public boolean isTeamChatAllowed() {
        return isStaff() || hasPermission("teamchat");
    }

    public String getProxy() {
        return hitBoxUtils.getRedisManager().getConfig().getServerName();
    }
}
