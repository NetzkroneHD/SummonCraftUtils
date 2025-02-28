package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.sound.GlobalSound;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SoundCommand extends HitBoxCommand {

    public SoundCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "globalsound", "gsound", "glsound");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length != 2) {
            sendHelp(hp);
            return;
        }
        final HitBoxPlayer tp = hitBoxUtils.getPlayer(args[0]);
        if(tp == null) {
            hp.sendMessage(Messages.PLAYER_OFFLINE);
            return;
        }
        GlobalSound.of(args[1].toUpperCase()).ifPresentOrElse(
                sound -> {
                    tp.playSound(sound);
                    hp.sendMessageColored("Played sound§e "+sound.name()+"§7 for player "+tp.getPrefixAndName()+"§7.");
                },
                () -> hp.sendMessage("That sound could not be found.")
        );


    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                return getPlayerTabComplete(args[0]);
            } else if (args.length == 2) {
                args[1] = args[1].toLowerCase();
                return Arrays.stream(GlobalSound.values())
                        .map(Enum::name)
                        .filter(name -> name.toLowerCase().startsWith(args[1]))
                        .toList();
            }
        }
        return Collections.emptyList();
    }
}
