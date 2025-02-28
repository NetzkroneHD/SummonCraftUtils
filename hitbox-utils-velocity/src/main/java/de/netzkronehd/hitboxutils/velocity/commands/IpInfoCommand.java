package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.api.GeolocationApi;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

import java.io.IOException;

import static de.netzkronehd.hitboxutils.utils.Utils.formatIpAddress;

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

            hitBoxUtils.runAsync(() -> {
                try {
                    final GeolocationApi.GeolocationInfo info = hitBoxUtils.getGeolocationApi().getGeolocation(formatIpAddress(ht.getPlayer().getRemoteAddress().toString()));
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
        hp.sendRawMessage("Ip§8:§e " + info.getIp());
        hp.sendRawMessage("Country§8:§e " + info.getCountry());
        hp.sendRawMessage("Region§8:§e " + info.getRegionName());
        hp.sendRawMessage("City§8:§e " + info.getCity());
        hp.sendRawMessage("Provider§8:§e " + info.getIsp());
        hp.sendRawMessage("VPN§8:§e " + (info.isProxy() ? "§cYes" : "§aNo"));
        hp.sendLine();
    }

}
