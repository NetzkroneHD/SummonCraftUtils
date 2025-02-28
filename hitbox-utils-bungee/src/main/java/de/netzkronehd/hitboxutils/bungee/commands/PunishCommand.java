package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bansystemapi.PunishmentType;
import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.bungee.punishment.TimePunishment;
import net.luckperms.api.model.user.User;
import org.javatuples.KeyValue;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static de.netzkronehd.hitboxutils.utils.Constants.PERMISSION_PREFIX;

public class PunishCommand extends HitBoxCommand {

    private final String punishCommand;

    public PunishCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "punishment", "punish");
        this.punishCommand = "[type] [player] [punisher] [duration] [reason]";
    }


    // punish <type> <player> <reason>

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length == 1) {
            try {
                final PunishmentType type = PunishmentType.valueOf(args[0].toUpperCase());
                hp.sendMessage("§e"+type.name()+"§7-Reasons§8:");
                hitBoxUtils.getPunishmentManager().getPunishments(type).values().stream()
                        .sorted(Comparator.comparing(TimePunishment::getReason))
                        .forEach(timePunishment -> {
                            if (timePunishment.isPermanent()) {
                                hp.sendArrow("§bPerma§8 - §e"+timePunishment.getReason()+"§8 - §7Max§8:§d Perma");
                            } else {
                                hp.sendArrow("§b"+timePunishment.getTime()+timePunishment.getTimeUnit()+"§8 - §e"+timePunishment.getReason()+"§8 - §7Max§8:§d "+timePunishment.getLimit()+timePunishment.getTimeUnit());
                            }
                        });

            } catch (IllegalArgumentException ex) {
                sendHelp(hp);
            }
            return;
        }
        if (args.length < 3) {
            sendHelp(hp);
            return;
        }

        try {
            final PunishmentType type = PunishmentType.valueOf(args[0].toUpperCase());
            final TimePunishment timePunishment = hitBoxUtils.getPunishmentManager().getPunishment(type, getArgsAsText(args, 2).toLowerCase());
            if (timePunishment == null) {
                hp.sendMessage("Could not found that reason.");
                return;
            }
            final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[1]);
            if (ht != null) {
                if (ht.isStaff()) {
                    hp.sendMessageColored(ht.getPrefixAndName() + "§7 is a staff member.");
                    return;
                }
                if (hitBoxUtils.getBanSystemApi().isPunished(type, ht.getUniqueId())) {
                    hp.sendMessageColored(ht.getPrefixAndName() + "§7 is already punished.");
                    return;
                }
                hitBoxUtils.runAsync(() -> createPunishment(type, timePunishment, ht.getUniqueId(), ht.getName(), hp));
            } else {
                hitBoxUtils.runAsync(() -> {
                    final KeyValue<UUID, String> uuidAndName = hitBoxUtils.getPlayerManager().getUuidAndName(args[1]);
                    if (uuidAndName == null) {
                        hp.sendMessage("Could not find that player in the database.");
                        return;
                    }
                    if (hitBoxUtils.getBanSystemApi().isPunished(type, uuidAndName.getKey())) {
                        hp.sendMessageColored("§e"+uuidAndName.getValue() + "§7 is already punished.");
                        return;
                    }

                    try {
                        final User user = hitBoxUtils.getLuckPermsApi().getUserManager().loadUser(uuidAndName.getKey()).get();
                        if (user.getCachedData().getPermissionData().checkPermission(PERMISSION_PREFIX + "staff").asBoolean()) {
                            hp.sendMessage("§e"+user.getUsername()+"§7 is a staff member.");
                            return;
                        }
                        createPunishment(type, timePunishment, user.getUniqueId(), user.getUsername(), hp);
                    } catch (InterruptedException | ExecutionException e) {
                        hp.sendMessage("Could not load LuckPerms user: "+e);
                    }

                });
            }

        } catch (IllegalArgumentException ex) {
            hp.sendMessage("Please use a proper punishment type.");
        }
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("punish§8 <§eType§8> <§eUser§8> <§eReason§8>");
    }

    private void createPunishment(PunishmentType type, TimePunishment timePunishment, UUID uuid, String name, HitBoxPlayer player) {

        final String calculatedTime = hitBoxUtils.getPunishmentManager().calculateTime(uuid, timePunishment, type);
        final String commandLine = this.punishCommand
                .replace("[type]", (timePunishment.isPermanent() ? type.getPermanentCommand():type.getCommand()))
                .replace("[punisher]", "--sender=" + player.getName()+" --sender-uuid="+player.getUniqueId())
                .replace("[player]", name)
                .replace("[duration]", (timePunishment.isPermanent() ? "":calculatedTime))
                .replace("[reason]", timePunishment.getReason());

        hitBoxUtils.getProxy().getPluginManager().dispatchCommand(
                hitBoxUtils.getProxy().getConsole(),
                commandLine
        );

    }


    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                args[0] = args[0].toLowerCase();
                return Arrays.stream(PunishmentType.values())
                        .map(Enum::name)
                        .filter(name -> name.toLowerCase().startsWith(args[0])).toList();
            } else if (args.length == 2) {
                return getPlayerTabComplete(args[1]);
            } else if (args.length >= 3) {
                final String text = getArgsAsText(args, 2).toLowerCase();
                try {
                    final PunishmentType type = PunishmentType.valueOf(args[0].toUpperCase());
                    return hitBoxUtils.getPunishmentManager().getPunishments(type).values().stream()
                            .map(TimePunishment::getReason)
                            .filter(id -> id.toLowerCase().startsWith(text)).toList();

                } catch (IllegalArgumentException ignored) {}
            }
        }
        return Collections.emptyList();
    }
}
