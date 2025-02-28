package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.bungee.utils.Cooldown;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.utils.Utils;
import net.luckperms.api.model.user.User;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodesInComponents;

public class PlaytimeTopCommand extends HitBoxCommand {

    public PlaytimeTopCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "playtimetop", "onlinetimetop");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hitBoxUtils.getCooldownManager().isOver(hp, Cooldown.PLAYTIME_TOP)) {
            hp.sendMessage("Please wait§e " + hitBoxUtils.getCooldownManager().getRemainingTime(hp, Cooldown.PLAYTIME_TOP) + "§7 until executing the command again.");
            return;
        }

        hitBoxUtils.getCooldownManager().setCooldown(hp, Cooldown.PLAYTIME_TOP);

        hitBoxUtils.runAsync(() -> {
            hp.sendLine();
            final List<PlayerPlaytime> topPlaytime = hitBoxUtils.getPlaytimeManager().getTopPlaytime();
            final List<Color> colors = Utils.getGradientColors(topPlaytime.size(), "#00FF00", "#FF0000");

            for (int i = 0; i < topPlaytime.size(); i++) {
                final UUID uuid = topPlaytime.get(i).getUuid();
                final HitBoxPlayer tp = hitBoxUtils.getPlayer(uuid);
                final String hex = "&" + String.format("#%02x%02x%02x", colors.get(i).getRed(), colors.get(i).getGreen(), colors.get(i).getBlue());
                if (tp != null) {
                    hp.sendMessage(translateHexCodesInComponents(hex + (i + 1) + "§7.§e " + tp.getPrefixAndName() + "§7 -§b " + tp.getFormattedPlaytime()));
                } else {
                    if (hitBoxUtils.getLuckPermsApi().getUserManager().isLoaded(uuid)) {
                        final User user = hitBoxUtils.getLuckPermsApi().getUserManager().getUser(uuid);
                        hp.sendMessage(translateHexCodesInComponents(hex + (i + 1) + "§7.§e " + hitBoxUtils.getLuckPermsApi().getGroupManager().getGroup(user.getPrimaryGroup()).getCachedData().getMetaData().getPrefix() + topPlaytime.get(i).getName() + "§7 -§b " + hitBoxUtils.getPlaytimeManager().getFormattedPlaytime(topPlaytime.get(i))));
                    } else {
                        try {
                            final User user = hitBoxUtils.getLuckPermsApi().getUserManager().loadUser(uuid).get();
                            hp.sendMessage(translateHexCodesInComponents(hex + (i + 1) + "§7.§e " + hitBoxUtils.getLuckPermsApi().getGroupManager().getGroup(user.getPrimaryGroup()).getCachedData().getMetaData().getPrefix() + topPlaytime.get(i).getName()+ "§7 -§b " + hitBoxUtils.getPlaytimeManager().getFormattedPlaytime(topPlaytime.get(i))));

                        } catch (InterruptedException | ExecutionException e) {
                            hp.sendMessage(translateHexCodesInComponents(hex + (i + 1) + "§7.§e " + topPlaytime.get(i).getName() + "§7 -§b " + hitBoxUtils.getPlaytimeManager().getFormattedPlaytime(topPlaytime.get(i))));
                        }
                    }


                }
            }
            hp.sendLine();
        });
    }
}
