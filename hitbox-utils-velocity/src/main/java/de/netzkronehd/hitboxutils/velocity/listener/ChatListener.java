package de.netzkronehd.hitboxutils.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatListener {

    private final HitBoxUtils hitBoxUtils;

    @Subscribe(order = PostOrder.LAST)
    public void onChat(PlayerChatEvent e) {

        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if (hp == null) return;
        if (hp.isTeamChat()) {
            e.setResult(PlayerChatEvent.ChatResult.denied());
            hitBoxUtils.getTeamChatManager().sendTeamChatMessage(hp, e.getMessage());
            return;
        }


        if(!e.getResult().isAllowed()) return;

        final long time = System.currentTimeMillis();
        hp.setLastMessageTime(time + hitBoxUtils.getChatManager().getConfig().getMessageTimeout());
        hp.setLastMessage(e.getMessage());
        final String serverName = hp.getServerName();

        hitBoxUtils.runAsync(() -> hitBoxUtils.getChatManager().createChatLog(hp.getUniqueId(), hp.getName(), time, serverName, e.getMessage()));

    }

    /**

    if (!hp.isStaff()) {
            final ChatManager.FilterResult filterResult = hitBoxUtils.getChatManager().filterMessage(e.getMessage(), hp.getLastMessage());
            if (!Utils.isOver(hp.getLastMessageTime())) {
                e.setCancelled(true);
                hp.sendMessage("Por favor, espera un momento.");
                return;
            }

            if (filterResult != null) {
                if (filterResult.toSimilar()) {
                    e.setCancelled(true);
                    hp.sendMessage("Por favor, no escribas el mismo mensaje.");
                    return;
                }

                if (filterResult.exceedsUpperCaseLimit()) {
                    e.setCancelled(true);
                    hp.sendMessage("Por favor, desactiva el bloqueo de mayúsculas (BLOQ MAYUS).");
                    return;
                }

                if (filterResult.isBanned()) {
                    final TextComponent filterMessage = HexColor.translateHexCodesInComponents(
                            Placeholder.placeholder(hitBoxUtils.getChatManager().getFilterMessage())
                                    .replace("PLAYER", hp.getPrefixAndName())
                                    .replace("WORD", filterResult.bannedWord())
                                    .replace("MESSAGE", filterResult.text())
                                    .replace("SERVER", hp.getServerName())
                                    .build()
                    );
                    filterMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§cMute")));
                    filterMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mute " + hp.getName()));

                    hitBoxUtils.getPlayers().stream().filter(
                            player -> (player.isStaff() || player.hasPermission("chatfilter.broadcast")) &&
                                    player.getStaffSettings() != null &&
                                    player.getStaffSettings().isFilterBroadcast()
                    ).forEach(
                            player -> player.sendMessage(filterMessage)
                    );
                    if (hitBoxUtils.getChatManager().isBlockMessages()) {
                        e.setCancelled(true);
                        hp.sendMessage(hitBoxUtils.getChatManager().getBlockMessage().replace("%WORD%", filterResult.bannedWord()));
                        return;
                    }
                }
            }
        }
     */

}
