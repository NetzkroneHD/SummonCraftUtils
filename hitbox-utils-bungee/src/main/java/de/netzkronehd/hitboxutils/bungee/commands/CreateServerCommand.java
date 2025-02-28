package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CreateServerCommand extends HitBoxCommand {

    public CreateServerCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "createserver");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;

        if (args.length != 3) {
            sendHelp(hp);
            return;
        }
        final String ip = args[0];

        if (!ip.contains(".") || ip.split("\\.").length != 4) {
            hp.sendMessage("Please provide a valid IP-Address. ([0-255].[0-255].[0-255].[0-255])");
            return;
        }

        try {
            final int port = Integer.parseInt(args[1]);
            if (port <= 0) {
                hp.sendMessage("The port can not be 0 or smaller than 0.");
                return;
            }
            final String serverName = args[2].toLowerCase();

            if (hitBoxUtils.getProxy().getServerInfo(serverName) != null) {
                hp.sendMessage("The server§e " + args[2] + "§7 does already exists.");
                return;
            }

            final InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
            final ServerInfo info = hitBoxUtils.getProxy().constructServerInfo(serverName, socketAddress, "", false);
            hitBoxUtils.getProxy().getServers().put(serverName, info);

            hp.sendMessage("Server§e " + serverName + "§7 with the Address§e " + ip + ":" + port + "§7 created successfully.");
            hitBoxUtils.getServerManager().setServer(info.getName(), ip + ":" + port, "", false);
            hp.sendMessage("Server successfully stored in the§e config.yml§7.");

        } catch (NumberFormatException ex) {
            hp.sendMessage("Please enter an integer for the port.");
        } catch (IOException ex) {
            hp.sendMessage("Could not save config§8:§e " + ex);
            hp.sendMessage("§cReminder!§7 Please don't forget to add the server to§b config.yml§7 in the BungeeCord.");

        } catch (Exception ex) {
            hp.sendMessage("Could not create connection to address§8:§e " + ex);
        }
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("createserver§8 <§eIP§8> <§ePort§8>§8 <§eServer-Name§8>");
    }

}
