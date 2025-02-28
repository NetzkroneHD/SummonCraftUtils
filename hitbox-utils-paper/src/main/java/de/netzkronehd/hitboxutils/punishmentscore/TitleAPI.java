package de.netzkronehd.hitboxutils.punishmentscore;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleAPI {
    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendTitle(PunishmentsCore plugin, Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        if (plugin.isNew()) {
            if (title.isEmpty()) {
                title = " ";
            }
            if (subtitle.isEmpty()) {
                subtitle = " ";
            }
            player.sendTitle(getColoredMessageNew(title), getColoredMessageNew(subtitle), fadeIn.intValue(), stay.intValue(), fadeOut.intValue());
            return;
        }
        if (title != null) {
            try {
                title = ChatColor.translateAlternateColorCodes('&', title).replaceAll("%player%", player.getDisplayName());
                Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
                sendPacket(player, titlePacket);
                Object e2 = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatTitle2 = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor subtitleConstructor2 = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                Object titlePacket2 = subtitleConstructor2.newInstance(e2, chatTitle2);
                sendPacket(player, titlePacket2);
            } catch (Exception var11) {
                var11.printStackTrace();
                return;
            }
        }
        if (subtitle != null) {
            String subtitle2 = ChatColor.translateAlternateColorCodes('&', subtitle).replaceAll("%player%", player.getDisplayName());
            Object e3 = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
            Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
            Constructor subtitleConstructor3 = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
            Object subtitlePacket = subtitleConstructor3.newInstance(e3, chatSubtitle, fadeIn, stay, fadeOut);
            sendPacket(player, subtitlePacket);
            Object e4 = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            Object chatSubtitle2 = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle2 + "\"}");
            Constructor subtitleConstructor4 = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
            Object subtitlePacket2 = subtitleConstructor4.newInstance(e4, chatSubtitle2, fadeIn, stay, fadeOut);
            sendPacket(player, subtitlePacket2);
        }
    }

    private static String getColoredMessageNew(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (true) {
            Matcher match = matcher;
            if (match.find()) {
                String color = message.substring(match.start(), match.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            } else {
                return ChatColor.translateAlternateColorCodes('&', message);
            }
        }
    }
}
