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

public class CheckBansCommand extends HitBoxCommand {

    public CheckBansCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "checkbans");
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
        hp.sendUsage("§echeckbans §8<§ePlayer/IP§8>");
    }

    private void sendBanInforation(HitBoxPlayer hp, String ip) {
        try {
            if (hitBoxUtils.getBanSystemApi().isBanned(ip)) {
                hp.sendMessage("That ip is banned.");
            } else hp.sendMessage("That ip is not banned.");
            sendInfo(hp, ip, hitBoxUtils.getBanSystemApi().listBanPunishments(ip));
        } catch (SQLException e) {
            hp.sendMessage("Could not load banns: " + e);
        }
    }

    private void sendInfo(HitBoxPlayer hp, String name, Map<String, Integer> punishments) {
        if (punishments.isEmpty()) {
            hp.sendMessage("That player was not yet banned.");
            return;
        }
        hp.sendMessageColored("Banns from§e "+name+"§8 (§b"+punishments.size()+"§8)§7:");
        punishments.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
            hp.sendArrow("§e"+entry.getValue()+"x§8 -§b "+entry.getKey());
        });
    }

    private void sendBanInforation(HitBoxPlayer hp, UUID uuid, String name) {
        try {
            if (hitBoxUtils.getBanSystemApi().isPunished(PunishmentType.BAN, uuid)) {
                hp.sendMessage("Player is banned.");
            } else hp.sendMessage("Player is not banned.");
            sendInfo(hp, name, hitBoxUtils.getBanSystemApi().listBanPunishments(uuid));
        } catch (SQLException e) {
            hp.sendMessage("Could not load banns: " + e);
        }
    }

}
