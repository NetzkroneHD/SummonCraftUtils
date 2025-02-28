package de.netzkronehd.hitboxutils.paper.player;

import de.netzkronehd.hitboxutils.message.HexColor;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlock;
import de.netzkronehd.hitboxutils.paper.inventory.*;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.player.StaffSettings;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static de.netzkronehd.translation.Message.formatColoredValue;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.title.Title.title;

@Getter
@Setter
@ToString
public class HitBoxPlayer implements Comparable<HitBoxPlayer> {

    private final HitBoxUtils hitBoxUtils;
    private final Player player;
    private final HashMap<String, PlayerClickInventory> inventories;

    private SelectSoundInventory selectSoundInventory;
    private StaffSettingsInventory staffSettingsInventory;

    private Long clickTime, sneakTime, interactTime;
    private PlayerPlaytime playerPlaytime;
    private StaffSettings staffSettings;
    private SpectateMode spectateMode;
    private Integer playerListPage;

    private ClickBlock editClickBlock;

    private int afkTime;

    private HashMap<Integer, ChatLogInventory> chatLogInventory;
    private boolean frozen;
    private String lastMessage;
    private Long lastMessageTime;


    public HitBoxPlayer(HitBoxUtils hitBoxUtils, Player player) {
        this.hitBoxUtils = hitBoxUtils;
        this.player = player;
        inventories = new HashMap<>();
    }

    public void playSound(Sound sound) {
        playSound(sound, 1, 1);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void loadData() {

        if (isStaff()) {
            staffSettings = hitBoxUtils.getStaffSettingManager().loadSettings(player.getUniqueId());
            if (staffSettings == null) {
                staffSettings = new StaffSettings(getUniqueId());
                staffSettings.reset();
            }
            staffSettingsInventory = new StaffSettingsInventory(this);
            if (staffSettings.isAutoEnable()) hitBoxUtils.runSync(this::enableSpectatorMode);
        }

        final PlayerPlaytime playtime = hitBoxUtils.getPlaytimeManager().getPlaytime(getUniqueId());
        setPlayerPlaytime(PlayerPlaytime.builder()
                .uuid(getUniqueId())
                .name(getName())
                .playtime(playtime.getPlaytime())
                .timeJoined((playtime.getTimeJoined() <= 0 ? System.currentTimeMillis() : playtime.getTimeJoined()))
                .build());

    }

    public void enableSpectatorMode() {
        spectateMode = new SpectateMode(player.getGameMode(), player.getAllowFlight(), player.isFlying(), player.getInventory().getContents());
        player.getInventory().clear();

        for (Items.SortedItem item : Items.SpectateMode.SPECTATE_MODE_ITEMS) {
            if (item.equals(Items.SpectateMode.SPEED_FAST)) continue;
            if (item.equals(Items.SpectateMode.SPEED_SUPER_FAST)) continue;
            if (item.equals(Items.SpectateMode.FLY_MODE_DEACTIVATED)) continue;
            player.getInventory().setItem(item.slot(), item.item());
        }
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFoodLevel(20);
        player.setSaturation(1);
        player.setMetadata(Constants.META_DATA_KEY_SPECTATING, new FixedMetadataValue(hitBoxUtils, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, true, false, false));


        if (isVanished()) return;
        if (staffSettings.isAutoVanish()) vanish();

    }

    public void disableSpectatorMode() {
        player.getInventory().clear();
        player.setGameMode(spectateMode.getGameMode());
        player.setAllowFlight(spectateMode.isAllowedToFly());
        player.setFlying(spectateMode.isFlying());
        player.getInventory().setContents(spectateMode.getInventory());
        player.setFallDistance(-9999);
        player.setFlySpeed(Utils.getMoveSpeed(1, true));
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removeMetadata(Constants.META_DATA_KEY_SPECTATING, hitBoxUtils);
        spectateMode = null;

    }

    public void freeze(HitBoxPlayer source) {
        if (source != null) {
            player.showTitle(title(formatColoredValue("§b§lFrozen"), formatColoredValue("§7by "+source.getPrefixAndName())));
            setFrozen(true);
            playSound(Sound.ENTITY_PLAYER_HURT_FREEZE);
            source.playSound(Sound.ENTITY_PLAYER_HURT_FREEZE);
            source.sendMessage("You have frozen " + getDisplayName() + "§7.");
        } else {
            player.showTitle(title(formatColoredValue("§b§lFrozen"), formatColoredValue("§7")));
            setFrozen(true);
            playSound(Sound.ENTITY_PLAYER_HURT_FREEZE);
        }
    }

    public void unfreeze(HitBoxPlayer source) {
        player.showTitle(title(formatColoredValue("§a§lUnfrozen"), formatColoredValue("§7by "+source.getPrefixAndName())));
        setFrozen(false);
        playSound(Sound.BLOCK_GLASS_BREAK);
        source.playSound(Sound.BLOCK_GLASS_BREAK);
        source.sendMessage("You have unfrozen " + getDisplayName() + "§7.");
    }

    public void sendMessage(String msg, String hoverText, String clickText) {
        player.sendMessage(text().append(formatColoredValue(Messages.PREFIX+msg))
                .clickEvent(runCommand(clickText))
                .hoverEvent(showText(formatColoredValue(hoverText)))
                .build()
        );
    }

    public Optional<String> getIp() {
        final InetSocketAddress address = player.getAddress();
        if(address == null) return Optional.empty();
        final InetAddress inetAddress = address.getAddress();
        if(inetAddress == null) return Optional.empty();
        return Optional.ofNullable(inetAddress.getHostAddress());
    }

    public void openInventory(HitBoxInventory inventory) {
        if (inventory == null) return;
        openInventory(inventory.getInventory());
    }

    public void openInventory(Inventory inventory) {
        player.openInventory(inventory);
    }


    public void sendMessage(Component component) {
        player.sendMessage(component);
    }

    public void sendArrow(String msg) {
        sendRawMessage("§8" + Messages.ARROW_RIGHT + "§7 " + msg);
    }

    public void sendMessage(String msg) {
        sendRawMessage(Messages.PREFIX + msg);
    }

    public void sendUsage(String msg) {
        sendMessage(Messages.USAGE + msg);
    }

    public void sendNoPerms() {
        sendMessage(Messages.NO_PERMS.toString());
    }

    public void sendOffline() {
        sendMessage(Messages.PLAYER_OFFLINE.toString());
    }

    public void sendNotExists() {
        sendMessage(Messages.PLAYER_NOT_EXISTS.toString());
    }

    public void sendRawMessage(String msg) {
        player.sendMessage("§7" + msg);
    }

    public void sendLine() {
        sendRawMessage("§8§m                            §r");
    }

    public void sendMessage(Messages msg) {
        sendMessage(msg.toString());
    }

    public void sendMessage(Messages msg, String text) {
        sendMessage(msg + text);
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public boolean hasRawPermission(String perms) {
        return player.hasPermission(perms);
    }

    public boolean isStaff() {
        return player.hasPermission("elmobox.staff") || hasPermission("staff");
    }

    public boolean isTeamChatAllowed() {
        return isStaff() || hasPermission("teamchat");
    }

    public boolean hasPermission(String perm) {
        return player.hasPermission(Constants.PERMISSION_PREFIX + perm) || player.hasPermission(Constants.PERMISSION_PREFIX + "*");
    }

    public boolean hasPermission(String perms, String suffix) {
        return (hasPermission(perms+"."+suffix));
    }

    public void sendActionBar(String msg) {
        sendActionBarNonPrefix(Messages.PREFIX + msg);
    }

    public void sendActionBarNonPrefix(String msg) {
        player.sendActionBar(formatColoredValue(msg));
    }

    public void vanish() {
        hitBoxUtils.getVanishManager().vanishPlayer(this);
    }

    public void unvanish() {
        hitBoxUtils.getVanishManager().showPlayer(this);
    }

    public boolean isVanished() {
        return hitBoxUtils.getVanishManager().isVanished(this);
    }

    public String getDisplayName() {
        return "§e" + player.getName();
    }

    public String getPrefixAndName() {
        return HexColor.translateHexCodes(getPrefix()+getName());
    }

    public String getPrefixAndNameRaw() {
        return getPrefix()+getName();
    }

    public boolean isVip() {
        return isInGroup("vip");
    }

    public boolean isInGroup(String group) {
        return player.hasPermission("group." + group);
    }

    public String getPrefix() {
        return getPrimaryGroup().getCachedData().getMetaData().getPrefix();
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

    public void teleport(Location loc) {
        player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    public ItemStack getItemInMainHand() {
        return player.getInventory().getItemInMainHand();
    }

    public boolean isSpectating() {
        return spectateMode != null;
    }

    @Override
    public int compareTo(@NotNull HitBoxPlayer o) {
        return getName().compareTo(o.getName());
    }

}
