package de.netzkronehd.hitboxutils.paper.utils;

import de.netzkronehd.hitboxutils.message.HexColor;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.manager.PunishmentManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Items {
    public record SortedItem(ItemStack item, int slot) {
    }

    public static final ItemStack LOADING = new ItemBuilder(Material.BARRIER).setName("§cLoading...").build();

    //Chat-Logs
    public static class ChatLogs {

    }

    public static class ClickBlock {
        public static final ItemStack SET_LOCATION = ItemBuilder.builder(Material.IRON_AXE).setName("§7Set the location of the ClickBlock").build();
    }



    //Spectate-Mode
    public static class SpectateMode {
        public static final SortedItem VANISHED = new SortedItem(new ItemBuilder(Material.GLASS, 1).setName("§7You are§8: §aVanished").build(), 0);
        public static final SortedItem VANISH = new SortedItem(new ItemBuilder(Material.WHITE_WOOL, 1).setName("§7You are§8: §cUnvanished").build(), 0);
        public static final SortedItem TELEPORT_WAND = new SortedItem(new ItemBuilder(Material.BLAZE_ROD, 1).setName("§6Teleport").build(), 1);
        public static final SortedItem SPECTATOR = new SortedItem(new ItemBuilder(Material.ENDER_EYE, 1).setName("§7Spectator-Mode").build(), 2);
        public static final SortedItem SPEED_NORMAL = new SortedItem(new ItemBuilder(Material.GUNPOWDER, 1).setName("§7Fly-Speed§8:§7 Normal").build(), 4);
        public static final SortedItem SPEED_FAST = new SortedItem(new ItemBuilder(Material.GLOWSTONE_DUST, 1).setName("§7Fly-Speed§8:§e Fast").build(), 4);
        public static final SortedItem SPEED_SUPER_FAST = new SortedItem(new ItemBuilder(Material.REDSTONE, 1).setName("§7Fly-Speed§8:§c Super-Fast").build(), 4);
        public static final SortedItem KNOCKBACK_TEST = new SortedItem(new ItemBuilder(Material.STICK, 1).setName(HexColor.BROWN + "Knockback-Test").setLore("§7Don't worry it does 0 damage.").build(), 6);
        public static final SortedItem FLY_MODE_ACTIVATED = new SortedItem(new ItemBuilder(Material.LIME_DYE).setName("§fFly-Mode§8:§a Activated").build(), 7);
        public static final SortedItem FLY_MODE_DEACTIVATED = new SortedItem(new ItemBuilder(Material.GRAY_DYE).setName("§fFly-Mode§8:§c Deactivated").build(), 7);
        public static final SortedItem PLAYERS = new SortedItem(new ItemBuilder(Material.ENDER_CHEST, 1).setName("§7Menu").build(), 8);
        public static final List<SortedItem> SPECTATE_MODE_ITEMS = List.of(VANISHED, VANISH, TELEPORT_WAND, PLAYERS, SPEED_FAST, SPEED_NORMAL, SPEED_SUPER_FAST, FLY_MODE_ACTIVATED, FLY_MODE_DEACTIVATED, SPECTATOR, KNOCKBACK_TEST);

    }

    public static class PlayerMenu {

        public static final SortedItem TELEPORT = new SortedItem(new ItemBuilder(Material.ENDER_PEARL, 1).setName("§eTeleport").build(), 12);
        public static final SortedItem FREEZE = new SortedItem(new ItemBuilder(Material.ICE, 1).setName("§eFreeze").build(), 13);
        public static final SortedItem SPECTATE = new SortedItem(new ItemBuilder(Material.ENDER_EYE, 1).setName(HexColor.translateHexCodes("&#81BD52Spectate")).build(), 14);

        //mute
        public static final SortedItem TOXIC_BEHAVIOR = new SortedItem(
                new ItemBuilder(Material.POTION, 1)
                        .setName("§7Mute§8:§a " + PunishmentManager.Punishments.TOXIC_BEHAVIOR.getName())
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.TOXIC_BEHAVIOR.getTime() + PunishmentManager.Punishments.TOXIC_BEHAVIOR.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .setPotionType(PotionEffectType.POISON)
                        .addFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
                        .build(), 18);
        public static final SortedItem FLOOD = new SortedItem(
                new ItemBuilder(Material.BOOK, 1)
                        .setName("§7Mute§8:§c " + PunishmentManager.Punishments.FLOOD.getName())
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.FLOOD.getTime() + PunishmentManager.Punishments.FLOOD.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 19);
        public static final SortedItem SPAM = new SortedItem(
                new ItemBuilder(Material.BOOKSHELF, 1)
                        .setName("§7Mute§8:§e " + PunishmentManager.Punishments.SPAM.getName())
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.SPAM.getTime() + PunishmentManager.Punishments.SPAM.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 20);
        public static final SortedItem SLIGHT_INSULT = new SortedItem(
                new ItemBuilder(Material.REDSTONE, 1)
                        .setName("§7Mute§8:§c " + PunishmentManager.Punishments.SLIGHT_INSULT.getName())
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.SLIGHT_INSULT.getTime() + PunishmentManager.Punishments.SLIGHT_INSULT.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 21);
        public static final SortedItem SERIOUS_INSULT = new SortedItem(
                new ItemBuilder(Material.REDSTONE_BLOCK, 1)
                        .setName("§7Mute§8:§4 " + PunishmentManager.Punishments.SERIOUS_INSULT.getName())
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.SERIOUS_INSULT.getTime() + PunishmentManager.Punishments.SERIOUS_INSULT.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 22);
        public static final SortedItem THIRD_PARTY_ADVERTISING = new SortedItem(
                new ItemBuilder(Material.PAPER, 1)
                        .setName("§7Mute§8:§b " + PunishmentManager.Punishments.THIRD_PARTY_ADVERTISING.getName())
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.THIRD_PARTY_ADVERTISING.getTime() + PunishmentManager.Punishments.THIRD_PARTY_ADVERTISING.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 23);
        public static final SortedItem UNKNOWN = new SortedItem(
                new ItemBuilder(Material.FEATHER, 1)
                        .setName("§7Mute§8:§f Unknown")
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.FLY.getTime() + PunishmentManager.Punishments.FLY.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 24);

        //ban
        public static final SortedItem CHEATING = new SortedItem(
                new ItemBuilder(Material.COMPARATOR, 1)
                        .setName("§7Ban§8:§4 Cheating")
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.CHEATING.getTime() + PunishmentManager.Punishments.CHEATING.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 28);
        public static final SortedItem KILLAURA = new SortedItem(
                new ItemBuilder(Material.RED_DYE, 1)
                        .setName("§7Ban§8:§c Killaura")
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.KILLAURA.getTime() + PunishmentManager.Punishments.KILLAURA.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 29);
        public static final SortedItem AUTO_TOTEM = new SortedItem(
                new ItemBuilder(Material.TOTEM_OF_UNDYING, 1)
                        .setName("§7Ban§8:§e Auto-Totem")
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.AUTO_TOTEM.getTime() + PunishmentManager.Punishments.AUTO_TOTEM.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 30);
        public static final SortedItem SPEED = new SortedItem(
                new ItemBuilder(Material.SUGAR, 1)
                        .setName("§7Ban§8:§f Speed")
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.SPEED.getTime() + PunishmentManager.Punishments.SPEED.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 31);
        public static final SortedItem X_RAY = new SortedItem(
                new ItemBuilder(Material.GLASS, 1)
                        .setName("§7Ban§8:§f X-Ray")
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.X_RAY.getTime() + PunishmentManager.Punishments.X_RAY.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 32);
        public static final SortedItem AUTO_CRITICAL = new SortedItem(
                new ItemBuilder(Material.DIAMOND_SWORD, 1)
                        .setName("§7Ban§8:§b Critical")
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.AUTO_CRITICAL.getTime() + PunishmentManager.Punishments.AUTO_CRITICAL.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 33);
        public static final SortedItem FLY = new SortedItem(
                new ItemBuilder(Material.FEATHER, 1)
                        .setName("§7Ban§8:§f Fly")
                        .setLore(
                                "",
                                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + PunishmentManager.Punishments.FLY.getTime() + PunishmentManager.Punishments.FLY.getTimeFormat(),
                                "",
                                "§8(The time will be automatically calculated",
                                "§8when you execute the punishment action)")
                        .build(), 34);
        public static final ItemStack PUNISHED = new ItemBuilder(Material.BARRIER, 1).setName("§cPunished successfully").build();

        public static final List<SortedItem> PLAYER_MENU_ITEMS = List.of(TELEPORT, SPECTATE, CHEATING, KILLAURA, AUTO_TOTEM, SPEED, X_RAY, AUTO_CRITICAL, FLY, FREEZE,
                new SortedItem(ItemBuilder.BACKWARD, 36));
//                TOXIC_BEHAVIOR, FLOOD, SPAM, SLIGHT_INSULT, THIRD_PARTY_ADVERTISING, UNKNOWN);

    }

    public static class SelectSound {
        public static final SortedItem DRAGON_SOUND = new SortedItem(new ItemBuilder(Material.DRAGON_EGG).setName("§7Dragon Sound").build(), 19);

        public static final SortedItem BEACON_SOUND = new SortedItem(new ItemBuilder(Material.BEACON).setName("§bBeacon Sound").build(), 21);

        public static final SortedItem THUNDER_SOUND = new SortedItem(new ItemBuilder(Material.BEACON).setName("§eThunder Sound").build(), 23);

        public static final SortedItem BELL_SOUND = new SortedItem(new ItemBuilder(Material.BELL).setName("§6Bell Sound").build(), 25);

        public static final List<SortedItem> SELECT_SOUND_ITEMS = List.of(DRAGON_SOUND, BEACON_SOUND, THUNDER_SOUND, BELL_SOUND);


    }

    public static class PlayerList {
        public static final SortedItem OPEN_SETTINGS = new SortedItem(new ItemBuilder(Material.COMPARATOR, 1).setName("§cOpen Settings").build(), 49);

    }

    public static class StaffSettings {
        public static final SortedItem ITEM_PICK_UP = new SortedItem(new ItemBuilder(Material.HOPPER)
                .setName("§7Item-Pickup")
                .setLore(
                        "",
                        "§7You can pick up items in",
                        "§7staff-mode if§a enabled§7.",
                        "")
                .build(), 10);

        public static final SortedItem SNEAK_SPECTATOR = new SortedItem(new ItemBuilder(Material.QUARTZ_STAIRS)
                .setName("§7Sneak-Spectator")
                .setLore(
                        "",
                        "§7If§a enabled§7 you can double-sneak",
                        "§7to change into spectator-mode.",
                        "")
                .build(), 11);

        public static final SortedItem AUTO_ENABLE = new SortedItem(new ItemBuilder(Material.IRON_DOOR)
                .setName("§7Auto-Enable")
                .setLore(
                        "",
                        "§7Auto enables the staff-mode",
                        "§7if you join the server.",
                        "")
                .build(), 12);

        public static final SortedItem AUTO_VANISH = new SortedItem(new ItemBuilder(Material.GLASS)
                .setName("§7Auto-Enable Vanish")
                .setLore(
                        "",
                        "§7Auto enables the vanish-mode",
                        "§7if you enable the staff-mode.",
                        "")
                .build(), 13);

        public static final SortedItem FILTER_BROADCAST = new SortedItem(new ItemBuilder(Material.SCAFFOLDING)
                .setName("§7Filter Messages")
                .setLore(
                        "",
                        "§7If§a enabled§7 you will see messages",
                        "§7by the§b chat-filter-system§7.",
                        ""
                )
                .build(), 14);

        public static final SortedItem MINE_BROADCAST = new SortedItem(new ItemBuilder(Material.DIAMOND_ORE)
                .setName("§7Mine Messages")
                .setLore(
                        "",
                        "§7If§a enabled§7 you will see messages",
                        "§7by the§b mine-detection-system§7.",
                        ""
                )
                .build(), 15);

        public static final SortedItem RESET = new SortedItem(new ItemBuilder(Material.REDSTONE_TORCH)
                .setName("§cReset settings")
                .build(), 44);

        public static final SortedItem ENABLED = new SortedItem(new ItemBuilder(Material.LIME_DYE)
                .setName("§aEnabled")
                .build(), 0);

        public static final SortedItem DISABLED = new SortedItem(new ItemBuilder(Material.GRAY_DYE)
                .setName("§7Disabled")
                .build(), 0);

        public static final List<SortedItem> SETTINGS_ITEMS = List.of(ITEM_PICK_UP, SNEAK_SPECTATOR, AUTO_ENABLE, AUTO_VANISH, FILTER_BROADCAST, MINE_BROADCAST);


    }

    public static class SelectedSound {

        public static final SortedItem SET_SOUND = new SortedItem(new ItemBuilder(Material.LIME_DYE).setName("§aSet Sound").build(), 20);

        public static final SortedItem TEST_SOUND = new SortedItem(new ItemBuilder(Material.YELLOW_DYE).setName("§eTest Sound").build(), 24);

        public static final List<SortedItem> SELECTED_SOUND_ITEMS = List.of(SET_SOUND, TEST_SOUND);

    }

}
