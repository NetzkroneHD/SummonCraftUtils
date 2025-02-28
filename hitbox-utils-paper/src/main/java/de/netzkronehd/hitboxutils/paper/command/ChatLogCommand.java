package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.player.ChatLogEntry;
import de.netzkronehd.hitboxutils.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatLogCommand extends HitBoxCommand {

    private final SimpleDateFormat dateFormat;

    public ChatLogCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "chatlogs", "chatlog");
        dateFormat = new SimpleDateFormat("dd.MM.yy-HH:mm:ss");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("player")) {
                final long from = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
                final long to = System.currentTimeMillis();
                loadFromPlayer(hp, args[1], from, to);
            } else if (args[0].equalsIgnoreCase("server")) {
                final long from = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
                final long to = System.currentTimeMillis();
                loadFromServer(hp, args[1], from, to);
            } else sendHelp(hp);
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("player")) {
                if (args[2].equalsIgnoreCase("find") && args.length >= 4) {
                    final String message = Utils.getArgsAsText(args, 3);
                    findFromPlayer(hp, args[1], message);
                } else {
                    try {
                        final Date from = dateFormat.parse(args[2]);
                        final Date to = (args.length == 4 ? dateFormat.parse(args[3]) : new Date());
                        loadFromPlayer(hp, args[1], from.getTime(), to.getTime());
                    } catch (ParseException e) {
                        hp.sendMessage("Please use a date format like§e " + this.dateFormat.toPattern() + "§7.");
                    }
                }
            } else if (args[0].equalsIgnoreCase("server")) {
                if (args[2].equalsIgnoreCase("find") && args.length >= 4) {
                    final String message = Utils.getArgsAsText(args, 3);
                    findFromServer(hp, args[1], message);
                } else {
                    try {
                        final Date from = dateFormat.parse(args[2]);
                        final Date to = (args.length == 4 ? dateFormat.parse(args[3]) : new Date());
                        loadFromServer(hp, args[1], from.getTime(), to.getTime());
                    } catch (ParseException e) {
                        hp.sendMessage("Please use a date format like§e " + this.dateFormat.toPattern() + "§7.");
                    }
                }
            } else sendHelp(hp);
        } else sendHelp(hp);
    }

    private void loadFromServer(HitBoxPlayer hp, String server, long from, long to) {
        hp.sendMessage("Loading chat logs of the server§e " + server + "§7.");
        hitBoxUtils.runAsync(() -> {
            final List<ChatLogEntry> chatLogs = hitBoxUtils.getChatManager().getChatLogsBetweenTime(server, from, to);
            if (chatLogs.isEmpty()) {
                hp.sendMessage("Could not find any chat logs from§e " + Utils.DATE_FORMAT.format(new Date(from)) + "§7 -§e " + Utils.DATE_FORMAT.format(new Date(to)) + "§7 on the server§b "+server+".");
                return;
            }
            hitBoxUtils.runSync(() -> hitBoxUtils.getChatManager().openChatLogs(hp, chatLogs, server));
        });
    }

    private void findFromServer(HitBoxPlayer hp, String server, String message) {
        hp.sendMessage("Loading chat logs of the server§e " + server + "§7.");
        hitBoxUtils.runAsync(() -> {
            final List<ChatLogEntry> chatLogs = hitBoxUtils.getChatManager().getChatLogsFindMessages(server, message);
            if (chatLogs.isEmpty()) {
                hp.sendMessage("Could not find any chat logs with the pattern§e " +message+ "§7.");
                return;
            }
            hitBoxUtils.runSync(() -> hitBoxUtils.getChatManager().openChatLogs(hp, chatLogs, server));
        });
    }

    private void findFromPlayer(HitBoxPlayer hp, String name, String message) {
        final HitBoxPlayer ht = hitBoxUtils.getPlayer(name);
        if (ht != null) {
            hp.sendMessage("Loading chat logs of§e " + ht.getPrefixAndName() + "§7.");
            hitBoxUtils.runAsync(() -> {
                final List<ChatLogEntry> chatLogs = hitBoxUtils.getChatManager().getChatLogsFindMessages(ht.getUniqueId(), message);
                if (chatLogs.isEmpty()) {
                    hp.sendMessage("Could not find any chat logs with the pattern§e " +message+ "§7.");
                    return;
                }
                hitBoxUtils.runSync(() -> hitBoxUtils.getChatManager().openChatLogs(hp, chatLogs, ht.getName()));
            });
        } else {
            hp.sendMessage("Loading chat logs of§e " + name + "§7.");
            hitBoxUtils.runAsync(() -> {
                final UUID uuid = hitBoxUtils.getPlayerManager().getUuid(name);
                if (uuid == null) {
                    hp.sendMessage("Cloud not find§e " + name + "§7 in the database.");
                    return;
                }
                final List<ChatLogEntry> chatLogs = hitBoxUtils.getChatManager().getChatLogsFindMessages(uuid, message);
                if (chatLogs.isEmpty()) {
                    hp.sendMessage("Could not find any chat logs with the pattern§e " +message+ "§7.");
                    return;
                }
                hitBoxUtils.runSync(() -> hitBoxUtils.getChatManager().openChatLogs(hp, chatLogs, name));
            });
        }
    }

    private void loadFromPlayer(HitBoxPlayer hp, String name, long from, long to) {
        final HitBoxPlayer ht = hitBoxUtils.getPlayer(name);
        if (ht != null) {
            hp.sendMessage("Loading chat logs of§e " + ht.getPrefixAndName() + "§7.");
            hitBoxUtils.runAsync(() -> {
                final List<ChatLogEntry> chatLogs = hitBoxUtils.getChatManager().getChatLogsBetweenTime(ht.getUniqueId(), from, to);
                if (chatLogs.isEmpty()) {
                    hp.sendMessage("Could not find any chat logs from§e " + Utils.DATE_FORMAT.format(new Date(from)) + "§7 -§e " + Utils.DATE_FORMAT.format(new Date(to)) + "§7.");
                    return;
                }
                hitBoxUtils.runSync(() -> hitBoxUtils.getChatManager().openChatLogs(hp, chatLogs, ht.getName()));
            });
        } else {
            hp.sendMessage("Loading chat logs of§e " + name + "§7.");
            hitBoxUtils.runAsync(() -> {
                final UUID uuid = hitBoxUtils.getPlayerManager().getUuid(name);
                if (uuid == null) {
                    hp.sendMessage("Cloud not find§e " + name + "§7 in the database.");
                    return;
                }
                final List<ChatLogEntry> chatLogs = hitBoxUtils.getChatManager().getChatLogsBetweenTime(uuid, from, to);
                if (chatLogs.isEmpty()) {
                    hp.sendMessage("Could not find any chat logs from§e " + Utils.DATE_FORMAT.format(new Date(from)) + "§7 -§e " + Utils.DATE_FORMAT.format(new Date(to)) + "§7.");
                    return;
                }
                hitBoxUtils.runSync(() -> hitBoxUtils.getChatManager().openChatLogs(hp, chatLogs, name));
            });
        }
    }

    //chatlog player <player>
    //chatlog player <player> <time>
    //chatlog player <player> <from> <to>
    //chatlog player <player> find <message>
    //chatlog server <server>
    //chatlog server <server> <time>
    //chatlog server <server> <from> <to>
    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                args[0] = args[0].toLowerCase();
                final List<String> tabs = new ArrayList<>();
                if("player".startsWith(args[0])) tabs.add("player");
                if("server".startsWith(args[0])) tabs.add("server");
                return tabs;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
                return getPlayerTabComplete(args[1]);
            } else if (args.length == 3 && (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("server"))) {
                final List<String> tabs = new ArrayList<>();
                final String date = dateFormat.format(new Date(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(2)));

                if("find".startsWith(args[2].toLowerCase())) tabs.add("find");
                if(date.startsWith(args[2])) tabs.add(date);

                return tabs;

            } else if (args.length == 4 && (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("server"))) {
                if (args[2].equalsIgnoreCase("find")) {
                    return List.of("%");
                } else {
                    final String date = dateFormat.format(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
                    if (date.startsWith(args[3])) return List.of(date);
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("chatlog");
        hp.sendArrow("§eplayer§8 <§eplayer§8>§7 - Gets the Chatlogs from a player");
        hp.sendArrow("§eplayer§8 <§eplayer§8> <§etime§8>§7 - Chatlogs of a player at a date");
        hp.sendArrow("§eplayer§8 <§eplayer§8> <§efrom§8> <§eto§8>§7 - Chatlogs between a date");
        hp.sendArrow("§eplayer§8 <§eplayer§8>§e find§8 <§emessage§8>§7 - Finds a message with placeholders %");

        hp.sendArrow("§eserver§8 <§eserver§8>§7 - Chatlogs of a server.");
        hp.sendArrow("§eserver§8 <§eserver§8>§8 <§etime§8>§7 - Chatlogs of a server at a date");
        hp.sendArrow("§eserver§8 <§eserver§8>§8 <§efrom§8> <§eto§8>§7 - Chatlogs between a date");
        hp.sendArrow("§eserver§8 <§eserver§8>§e find§8 <§emessage§8>§7 - Finds a message with placeholders %");

    }

}
