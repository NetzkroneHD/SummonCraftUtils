package de.netzkronehd.hitboxutils.message;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodes;
import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodesInComponents;

public class MessageBuilder {

    private final TextComponent textComponent;

    public MessageBuilder() {
        this.textComponent = new TextComponent();
    }

    public MessageBuilder(boolean prefix) {
        if(prefix) this.textComponent = new TextComponent(Messages.PREFIX.toString());
        else this.textComponent = new TextComponent();
    }

    public MessageBuilder(TextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public MessageBuilder(String message) {
        this.textComponent = new TextComponent("§7"+message);
    }

    public MessageBuilder(String message, boolean translateColor) {
        if (translateColor) this.textComponent = new TextComponent(translateHexCodesInComponents(message));
        else this.textComponent = new TextComponent(message);
    }

    public MessageBuilder color(ChatColor chatColor) {
        textComponent.setColor(chatColor);
        return this;
    }

    public MessageBuilder bold(boolean bold) {
        textComponent.setBold(bold);
        return this;
    }

    public MessageBuilder italic(boolean italic) {
        textComponent.setItalic(italic);
        return this;
    }

    public MessageBuilder strikethrough(boolean strikethrough) {
        textComponent.setStrikethrough(strikethrough);
        return this;
    }

    public MessageBuilder underlined(boolean underlined) {
        textComponent.setUnderlined(underlined);
        return this;
    }

    public MessageBuilder obfuscated(boolean obfuscated) {
        textComponent.setObfuscated(obfuscated);
        return this;
    }

    public MessageBuilder insertion(String insertion) {
        textComponent.setInsertion(insertion);
        return this;
    }

    public MessageBuilder font(String font) {
        textComponent.setFont(font);
        return this;
    }

    public MessageBuilder runCommand(String command) {
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    public MessageBuilder suggestCommand(String suggest) {
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        return this;
    }

    public MessageBuilder clickEvent(ClickEvent.Action action, String value) {
        textComponent.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public MessageBuilder showText(String text) {
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text)));
        return this;
    }

    public MessageBuilder append(MessageBuilder messageBuilder) {
        textComponent.addExtra(messageBuilder.build());
        return this;
    }

    public MessageBuilder append(TextComponent textComponent) {
        this.textComponent.addExtra(textComponent);
        return this;
    }

    public MessageBuilder append(String message, boolean translateColor) {
        if(translateColor) textComponent.addExtra(translateHexCodesInComponents(message));
        else textComponent.addExtra(message);
        return this;
    }

    public MessageBuilder append(String message) {
        return append(message, false);
    }

    public TextComponent build() {
        return textComponent;
    }

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public static MessageBuilder builder(boolean prefix) {
        return new MessageBuilder(prefix);
    }

    public static MessageBuilder builder(String message) {
        return new MessageBuilder(message);
    }

    public static MessageBuilder builder(TextComponent textComponent) {
        return new MessageBuilder(textComponent);
    }

    public static MessageBuilder prefix() {
        return builder(true);
    }

    public static MessageBuilder runCommand(String message, String command) {
        return runCommand(message, command, false);
    }

    public static MessageBuilder showText(String message, String text) {
        return showText(message, text, false);
    }

    public static MessageBuilder runCommandAndShowText(String message, String command, String text) {
        return runCommandAndShowText(message, command, text, false);
    }

    public static MessageBuilder runCommand(String message, String command, boolean translateColor) {
        return runCommand(message, command, translateColor, false);
    }

    public static MessageBuilder showText(String message, String text, boolean translateColor) {
        return showText(message, text, translateColor, false);
    }

    public static MessageBuilder runCommandAndShowText(String message, String command, String text, boolean translateColor) {
        return runCommandAndShowText(message, command, text, translateColor, false);
    }

    public static MessageBuilder runCommand(String message, String command, boolean translateColor, boolean prefix) {
        return new MessageBuilder(prefix).append(message, translateColor).runCommand(command);
    }

    public static MessageBuilder showText(String message, String text, boolean translateColor, boolean prefix) {
        return new MessageBuilder(prefix).append(message, translateColor).showText((translateColor ? translateHexCodes(text):text));
    }

    public static MessageBuilder runCommandAndShowText(String message, String command, String text, boolean translateColor, boolean prefix) {
        return showText(message, text, translateColor, prefix).runCommand(command);
    }

}
