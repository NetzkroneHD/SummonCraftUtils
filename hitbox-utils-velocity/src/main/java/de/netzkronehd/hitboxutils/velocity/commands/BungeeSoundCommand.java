package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BungeeSoundCommand extends HitBoxCommand {

    public BungeeSoundCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "bungeesound", "bsound");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (hasCommandPermission(hp)) {
            if (args.length >= 2) {
                final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
                if (ht == null) {
                    hp.sendMessage(Messages.PLAYER_OFFLINE);
                    return;
                }
                try {
                    final GlobalSound sound = GlobalSound.valueOf(args[1].toUpperCase());
                    float volume;
                    float pitch;
                    try {
                        volume = Float.parseFloat(args[2]);
                        pitch = Float.parseFloat(args[3]);
                    } catch (Exception ex) {
                        volume = 1;
                        pitch = 1;
                    }

                    ht.playSound(sound, volume, pitch);
                    hp.sendMessageColored("Sound§e "+sound.name()+"§7 was played for "+ht.getPrefixAndName()+"§7.");
                } catch (IllegalArgumentException e) {
                    hp.sendMessage("Please use a proper sound.");
                }
            } else sendHelp(hp);
        }
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("bsound§8 <§ePlayer§8> <§eSound§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                return getPlayerTabComplete(args[0]);
            } else if (args.length == 2) {
                final List<String> tabs = new ArrayList<>();
                args[1] = args[1].toLowerCase();
                for (GlobalSound value : GlobalSound.values()) {
                    if (value.name().toLowerCase().startsWith(args[1])) {
                        tabs.add(value.name());
                    }
                }
                return tabs;
            }
        }
        return Collections.emptyList();
    }
}
