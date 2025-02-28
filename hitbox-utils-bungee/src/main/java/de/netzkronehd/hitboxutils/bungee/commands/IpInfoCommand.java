package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.api.GeolocationApi;
import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;

import java.io.IOException;
import java.net.InetSocketAddress;

import static de.netzkronehd.hitboxutils.message.MessageBuilder.builder;

public class IpInfoCommand extends HitBoxCommand {


    public IpInfoCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "ipinfo", "hbipinfo");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length != 1) {
            sendHelp(hp);
            return;
        }
        if (args[0].contains(".")) {
            hp.sendMessage("Loading information...");
            hitBoxUtils.runAsync(() -> {
                try {
                    final GeolocationApi.GeolocationInfo info = hitBoxUtils.getGeolocationApi().getGeolocation(args[0]);
                    sendInfo(hp, info);
                } catch (IOException | InterruptedException e) {
                    hp.sendMessage("Could not load information§8:§e " + e);
                }
            });

        } else {
            final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
            if (ht == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }
            if (!(ht.getPlayer().getSocketAddress() instanceof InetSocketAddress address)) {
                hp.sendMessage("Can not load IPv4 information from§e " + ht.getPlayer().getSocketAddress().getClass().getSimpleName() + "§7-Connection.");
                return;
            }

            hitBoxUtils.runAsync(() -> {
                try {
                    final GeolocationApi.GeolocationInfo info = hitBoxUtils.getGeolocationApi().getGeolocation(Utils.formatIpAddress(address.toString()));
                    sendInfo(hp, info);
                } catch (IOException | InterruptedException e) {
                    hp.sendMessage("Could not load information§8:§e " + e);
                }
            });
        }
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("ipinfo§8 <§ePlayer/IP§8>");
    }

    private void sendInfo(HitBoxPlayer hp, GeolocationApi.GeolocationInfo info) {
        hp.sendLine();
        hp.sendMessage(builder("Ip§8:§e " + info.getIp()).clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, info.getIp()).showText("§7Click to copy").build());
        hp.sendRawMessage("Country§8:§e " + info.getCountry());
        hp.sendRawMessage("Region§8:§e " + info.getRegionName());
        hp.sendRawMessage("City§8:§e " + info.getCity());
        hp.sendRawMessage("Provider§8:§e " + info.getIsp());
        hp.sendRawMessage("VPN§8:§e " + (info.isProxy() ? "§cYes" : "§aNo"));
        hp.sendLine();
    }

}
