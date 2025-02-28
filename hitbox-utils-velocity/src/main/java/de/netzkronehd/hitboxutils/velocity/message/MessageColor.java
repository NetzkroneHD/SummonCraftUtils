package de.netzkronehd.hitboxutils.velocity.message;

import de.netzkronehd.hitboxutils.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.awt.*;

public class MessageColor {

    public static final char COLOR_CHAR = '§';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        final char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static TextColor toTextColor(Color color) {
        return TextColor.color(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Component prefix(String value) {
        return formatColoredValue(Messages.PREFIX+value);
    }

    public static Component formatColoredValue(String value) {
        final boolean containsLegacyFormattingCharacter = value.indexOf(LegacyComponentSerializer.AMPERSAND_CHAR) != -1
                || value.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1;

        if (containsLegacyFormattingCharacter) {
            return LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(value)
                    .toBuilder()
                    .build();
        } else {
            return MiniMessage.miniMessage().deserialize(value);
        }
    }

    public static String serializeLegacySection(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

}
