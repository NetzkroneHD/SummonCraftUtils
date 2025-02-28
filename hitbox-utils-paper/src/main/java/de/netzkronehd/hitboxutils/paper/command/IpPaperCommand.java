package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.api.GeolocationApi;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class IpPaperCommand extends HitBoxCommand {

    private final GeolocationApi geolocationApi;

    public IpPaperCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "ipinfopaper", "ipinfop", "ipinfo");
        this.geolocationApi = new GeolocationApi();
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length != 1) {
            hp.sendUsage("ipinfop§8 <§eplayer§8>");
            return;
        }
        if (args[0].contains(".")) {
            hp.sendMessage("Loading GeoIP data for §e" + args[0] + "§7...");
            hitBoxUtils.runAsync(() -> {
                try {
                    final GeolocationApi.GeolocationInfo info = geolocationApi.getGeolocation(args[0]);
                    sendInfo(hp, info);
                } catch (IOException | InterruptedException e) {
                    hp.sendMessage("§cFailed to load GeoIP data for §e" +args[0]+ "§7.");
                    hitBoxUtils.getLogger().severe(e.toString());
                }
            });
        } else {
            final HitBoxPlayer target = hitBoxUtils.getPlayer(args[0]);
            if (target == null) {
                hp.sendOffline();
                return;
            }
            target.getIp().ifPresentOrElse(ip -> {
                hp.sendMessage("Loading GeoIP data for §e" + target.getPrefixAndName() + "§7...");
                hitBoxUtils.runAsync(() -> {
                    try {
                        final GeolocationApi.GeolocationInfo info = geolocationApi.getGeolocation(ip);
                        sendInfo(hp, info);
                    } catch (IOException | InterruptedException e) {
                        hp.sendMessage("§cFailed to load GeoIP data for §e" + target.getPrefixAndName() + "§7.");
                        hitBoxUtils.getLogger().severe(e.toString());
                    }
                });
            }, () -> {
                hp.sendMessage("§cNo IP found for §e" + target.getPrefixAndName() + "§7.");
            });
        }
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if(!hasPermission(hp)) return Collections.emptyList();
        if(args.length != 1) return Collections.emptyList();
        args[0] = args[0].toLowerCase();
        return hitBoxUtils.getPlayers().stream()
                .map(HitBoxPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0]))
                .toList();
    }

    private void sendInfo(HitBoxPlayer hp, GeolocationApi.GeolocationInfo info) {
        hp.sendLine();
        hp.sendMessage(Component.text("Ip§8:§e " + info.getIp()).color(NamedTextColor.GRAY)
                .clickEvent(ClickEvent.copyToClipboard(info.getIp()))
                .hoverEvent(Component.text("§7Click to copy"))
        );
        hp.sendRawMessage("Country§8:§e " + info.getCountry());
        hp.sendRawMessage("Region§8:§e " + info.getRegionName());
        hp.sendRawMessage("City§8:§e " + info.getCity());
        hp.sendRawMessage("Provider§8:§e " + info.getIsp());
        hp.sendRawMessage("VPN§8:§e " + (info.isProxy() ? "§cYes" : "§aNo"));
        hp.sendLine();
    }
}
