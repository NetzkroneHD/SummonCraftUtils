package de.netzkronehd.hitboxutils.message;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

public class HexColor {

    public static final Pattern DEFATLT_HEX_PATTERN = Pattern.compile("#[A-fA-F0-9]{6}");
    private static final Pattern HEX_PATTERN = Pattern.compile("&#[A-fA-F0-9]{6}");

    public static final String BROWN = translateHexCodes("&#964B00");

    public static TextComponent translateHexCodesInComponents(String message) {

        final TextComponent msg = new TextComponent();

        final String[] split = message.split("&#");
        if (split.length > 0) {
            for (String fragment : split) {
                if (fragment.equalsIgnoreCase("")) continue;
                fragment = "&#" + fragment;

                final TextComponent added = new TextComponent();
                final Matcher matcher = HEX_PATTERN.matcher(fragment);
                if (matcher.find()) {
                    try {
                        final String color = fragment.substring(matcher.start(), matcher.end());
                        added.setColor(ChatColor.of("#" + color.replace("&#", "")));
                        fragment = fragment.replace(color, "");
                    } catch (Exception e) {
                        fragment = fragment.substring(2);
                    }

                } else fragment = fragment.substring(2);
                added.setText(translateAlternateColorCodes('&', fragment));
                msg.addExtra(added);
            }
        } else {
            msg.setText(translateAlternateColorCodes('&', message));
        }

        return msg;
    }

    public static String translateHexCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, String.valueOf(ChatColor.of(color.replace("&", ""))));
            matcher = HEX_PATTERN.matcher(message);
        }
        return translateAlternateColorCodes('&', message);
    }

}
