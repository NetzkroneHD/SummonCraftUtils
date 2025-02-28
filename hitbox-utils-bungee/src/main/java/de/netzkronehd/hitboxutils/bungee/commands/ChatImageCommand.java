package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.image.ChatImageAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatImageCommand extends HitBoxCommand {

    public ChatImageCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "chatimage");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length < 1) {
            sendHelp(hp);
            return;
        }
        final boolean smooth;
        final boolean trim;
        int width = 8;
        int height = 8;

        if (args.length > 4) {
            smooth = Boolean.parseBoolean(args[1]);
            trim = Boolean.parseBoolean(args[2]);

            try {
                width = Integer.parseInt(args[3]);
                height = Integer.parseInt(args[4]);
            } catch (NumberFormatException ex) {
                hp.sendMessage("§7Invalid width/height§8:§e " + ex);
                return;
            }

        } else {
            trim = false;
            smooth = false;
        }
        final int finalWidth = width;
        final int finalHeight = height;

        hp.sendMessage("Loading image...");


        hitBoxUtils.runAsync(() -> {
            try {
                sendImage(args, smooth, trim, finalWidth, finalHeight);
            } catch (Exception e) {
                hp.sendMessage("§7Could not load image§8:§e " + e);
            }
        });
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("hitboxutils.chatimage")) return;
        if (args.length < 1) {
            return;
        }
        final boolean smooth;
        final boolean trim;
        int width = 8;
        int height = 8;

        if (args.length > 4) {
            smooth = Boolean.parseBoolean(args[1]);
            trim = Boolean.parseBoolean(args[2]);

            try {
                width = Integer.parseInt(args[3]);
                height = Integer.parseInt(args[4]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(new TextComponent("§7Invalid width/height§8:§e " + ex));
                return;
            }

        } else {
            trim = false;
            smooth = false;
        }
        final int finalWidth = width;
        final int finalHeight = height;

        sender.sendMessage(new TextComponent("Loading image..."));
        hitBoxUtils.runAsync(() -> {
            try {
                sendImage(args, smooth, trim, finalWidth, finalHeight);
            } catch (IOException e) {
                sender.sendMessage(new TextComponent("§7Could not load image§8:§e " + e));
            }
        });
    }

    private void sendImage(String[] args, boolean smooth, boolean trim, int finalWidth, int finalHeight) throws IOException {
        final TextComponent chatImage = ChatImageAPI.createChatImage(args[0], "", smooth, trim, finalWidth, finalHeight);


        if (args.length > 5) {
            final String text = getArgsAsText(args, 5);
            hitBoxUtils.getProxy().broadcast(ChatImageAPI.addText(chatImage, text));

        } else hitBoxUtils.getProxy().broadcast(chatImage);
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasCommandPermission(hp)) {
            if (args.length == 2) {
                final List<String> tabs = new ArrayList<>();
                args[1] = args[1].toLowerCase();
                if ("true".startsWith(args[1])) tabs.add("true");
                if ("false".startsWith(args[1])) tabs.add("false");
                return tabs;
            } else if (args.length == 3) {
                final List<String> tabs = new ArrayList<>();
                args[2] = args[2].toLowerCase();
                if ("true".startsWith(args[2])) tabs.add("true");
                if ("false".startsWith(args[2])) tabs.add("false");
                return tabs;
            } else if (args.length == 4) {
                final List<String> tabs = new ArrayList<>();
                args[3] = args[3].toLowerCase();
                if ("8".startsWith(args[3])) tabs.add("8");
                if ("4".startsWith(args[3])) tabs.add("4");
                return tabs;
            } else if (args.length == 5) {
                final List<String> tabs = new ArrayList<>();
                args[4] = args[4].toLowerCase();
                if ("8".startsWith(args[4])) tabs.add("8");
                if ("4".startsWith(args[4])) tabs.add("4");
                return tabs;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("chatimage§8 <§eURL§8>");
        hp.sendUsage("chatimage§8 <§eURL§8> <§eSmooth§8> <§eTrim§8> §8<§eWidth§8> <§eHeight§8>§8 <§eMessage§8>");
    }
}
