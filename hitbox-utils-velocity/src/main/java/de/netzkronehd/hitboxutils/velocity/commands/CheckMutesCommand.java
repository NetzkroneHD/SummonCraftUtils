package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.bansystemapi.PunishmentType;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import org.javatuples.KeyValue;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CheckMutesCommand extends HitBoxCommand {

    public CheckMutesCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "checkmutes");
    }

    // /checkbans <player/ip>
    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length != 1) {
            sendHelp(hp);
            return;
        }


        final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);

        if (ht != null) {
            hitBoxUtils.runAsync(() -> sendBanInforation(hp, ht.getUniqueId(), ht.getPrefixAndName()));
        } else if (args[0].contains(".") || args[0].split("\\.").length == 4) {
            hitBoxUtils.runAsync(() -> sendBanInforation(hp, args[0]));
        } else {
            hitBoxUtils.runAsync(() -> {
                final KeyValue<UUID, String> uuidAndName = hitBoxUtils.getPlayerManager().getUuidAndName(args[0].toLowerCase());
                if (uuidAndName == null) {
                    hp.sendMessage("Could not find that player in the database.");
                    return;
                }
                sendBanInforation(hp, uuidAndName.getKey(), uuidAndName.getValue());
            });
        }
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasCommandPermission(hp)) {
            if (args.length == 1) {
                return getPlayerTabComplete(args[0]);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("§echeckmutes §8<§ePlayer/IP§8>");
    }

    private void sendBanInforation(HitBoxPlayer hp, String ip) {
        try {
            if (hitBoxUtils.getBanSystemApi().isMuted(ip)) {
                hp.sendMessage("That ip is muted.");
            } else hp.sendMessage("That ip is not muted.");
            sendInfo(hp, ip, hitBoxUtils.getBanSystemApi().listMutePunishments(ip));
        } catch (SQLException e) {
            hp.sendMessage("Could not load mutes: " + e);
        }
    }

    private void sendInfo(HitBoxPlayer hp, String name, Map<String, Integer> punishments) {
        if (punishments.isEmpty()) {
            hp.sendMessage("That player was not yet muted.");
            return;
        }
        hp.sendMessageColored("Mutes from§e "+name+"§8 (§b"+punishments.size()+"§8)§7:");
        punishments.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
            hp.sendArrow("§e"+entry.getValue()+"x§8 -§b "+entry.getKey());
        });
    }

    private void sendBanInforation(HitBoxPlayer hp, UUID uuid, String name) {
        try {
            if (hitBoxUtils.getBanSystemApi().isPunished(PunishmentType.MUTE, uuid)) {
                hp.sendMessage("Player is muted.");
            } else hp.sendMessage("Player is not muted.");
            sendInfo(hp, name, hitBoxUtils.getBanSystemApi().listMutePunishments(uuid));
        } catch (SQLException e) {
            hp.sendMessage("Could not load mutes: " + e);
        }
    }

}
