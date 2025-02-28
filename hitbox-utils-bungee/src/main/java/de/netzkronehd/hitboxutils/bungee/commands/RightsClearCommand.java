package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.HexColor;
import de.netzkronehd.hitboxutils.message.Messages;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class RightsClearCommand extends HitBoxCommand {

    public RightsClearCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "rightsclear");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (hasCommandPermission(hp)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
                hp.sendMessage("Clearing all rights...");
                final Title title = hitBoxUtils.getProxy().createTitle();
                title.title(new TextComponent("§e"));
                title.subTitle(HexColor.translateHexCodesInComponents("§c§lEstado de emergencia activado"));
                title.fadeIn(20);
                title.stay(20 * 3);
                title.fadeOut(20);
                hitBoxUtils.getProxy().getPlayers().forEach(title::send);
                hitBoxUtils.runAsync(() -> hitBoxUtils.getLuckPermsApi().getUserManager().getUniqueUsers().thenAccept(uuids -> {
                    for (UUID uuid : uuids) {
                        hitBoxUtils.getLuckPermsApi().getUserManager().loadUser(uuid).thenAccept(user -> {
                            if (user == null) return;
                            user.setPrimaryGroup("default");
                            user.data().clear();
                            hitBoxUtils.getLuckPermsApi().getUserManager().saveUser(user);
                            hitBoxUtils.getLogger().info("Cleared permission data from " + user.getUsername() + " (" + user.getUniqueId() + ").");
                        });
                    }
                }));
            } else {
                hp.sendMessage(text(Messages.PREFIX + "Do you really want to clear all rights? Please confirm by using ")
                        .color(NamedTextColor.GRAY)
                        .append(text("/rightsclear confirm")
                                .color(NamedTextColor.YELLOW)
                                .clickEvent(ClickEvent.suggestCommand("/rightsclear confirm"))
                                .hoverEvent(text("Confirm the rights clear").color(NamedTextColor.GRAY))
                        ).append(text(".").color(NamedTextColor.GRAY))
                );
            }
        }
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        return super.onTab(hp, args);
    }
}
