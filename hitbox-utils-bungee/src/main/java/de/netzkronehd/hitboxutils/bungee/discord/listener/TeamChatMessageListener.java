package de.netzkronehd.hitboxutils.bungee.discord.listener;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class TeamChatMessageListener extends ListenerAdapter {

    private final HitBoxUtils hitBoxUtils;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if(!e.isFromGuild()) return;
        if(e.getAuthor().isBot()) return;
        if(e.getAuthor().isSystem()) return;
        if(e.getMember() == null) return;
        if(!e.getGuild().getId().equals(hitBoxUtils.getDiscordTeamChatManager().getTeamChatGuildId())) return;
        if(!(e.getGuildChannel() instanceof TextChannel)) return;
        if(!hitBoxUtils.getDiscordTeamChatManager().isEnabled()) return;
        if(!e.getChannel().getId().equals(hitBoxUtils.getDiscordTeamChatManager().getTeamChatChannelId())) return;

        hitBoxUtils.getDiscordTeamChatManager().sendMessageInMinecraft(e.getMember(), e.getMessage().getContentRaw());

    }
}
