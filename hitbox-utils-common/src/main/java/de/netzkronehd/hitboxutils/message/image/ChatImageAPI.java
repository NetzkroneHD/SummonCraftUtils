package de.netzkronehd.hitboxutils.message.image;

import de.netzkronehd.hitboxutils.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ChatImageAPI {

    private ChatImageAPI() {
    }


    /**
     * Creates an image that can be sent in Minecraft chat.
     *
     * @param url    the url of the image
     * @param text   optional text to append to the right of the image
     * @param smooth whether to use smooth rendering (true) or simple rendering (false)
     * @param trim   whether to trim transparent edges or not
     * @param width  the maximum width an image can be
     * @param height the maximum height an image can be
     * @return a Minecraft chat image
     */
    public static TextComponent createChatImage(String url, String text, boolean smooth, boolean trim, int width, int height) throws IOException {

        final BufferedImage image = Utils.loadImage(url);
        if (image == null) return null;

        final Dimension dim = new Dimension(width, height);
        TextComponent component = ImageMaker.createChatImage(image, dim, smooth, trim);
        if (text != null && !text.isEmpty()) component = ImageMaker.addText(component, text);

        return component;

    }

    /**
     * Adds text to the right side of a Minecraft chat image
     *
     * @param chatImage the Minecraft chat image to add text to
     * @param text      the text to add
     * @return the updated Minecraft chat image with text
     */
    public static TextComponent addText(TextComponent chatImage, String text) {
        return ImageMaker.addText(chatImage, text);
    }

}
