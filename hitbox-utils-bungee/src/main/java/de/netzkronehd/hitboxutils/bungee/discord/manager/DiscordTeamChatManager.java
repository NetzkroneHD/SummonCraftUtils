package de.netzkronehd.hitboxutils.bungee.discord.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.manager.Manager;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.message.Placeholder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.luckperms.api.model.group.Group;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.Optional;
import java.util.logging.Level;

import static de.netzkronehd.hitboxutils.bungee.manager.TeamChatManager.TEAM_CHAT_PREFIX;
import static de.netzkronehd.hitboxutils.message.MessageBuilder.builder;

@Getter
public class DiscordTeamChatManager extends Manager {

    private String teamChatGuildId;
    private String teamChatChannelId;
    private String discordMessageFormat;

    public DiscordTeamChatManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("TeamChatGuildId", "-1");
            cfg.set("TeamChatChannelId", "-1");
            cfg.set("MessageFormat", "> **[Minecraft-TeamChat] (%SERVER%)** %GROUP% %PLAYER%: `%MESSAGE%`");
            save();
        }
    }

    @Override
    public void readFile() {
        this.teamChatGuildId = cfg.getString("TeamChatGuildId", "-1");
        this.teamChatChannelId = cfg.getString("TeamChatChannelId", "-1");
        this.discordMessageFormat = cfg.getString("MessageFormat", "> **[Minecraft-TeamChat] (%SERVER%)** %GROUP% %PLAYER%: `%MESSAGE%`");
    }

    public void sendMessageInDiscord(HitBoxPlayer hp, String message) {
        if(!isEnabled()) return;
        getGuild().ifPresentOrElse(guild -> {
            final TextChannel textChannel = guild.getTextChannelById(this.teamChatChannelId);
            if (textChannel == null) return;

            final Group primaryGroup = hp.getPrimaryGroup();
            final String displayName = primaryGroup.getDisplayName();

            textChannel.sendMessage(new Placeholder(this.discordMessageFormat)
                    .replace("server", hp.getServerName())
                    .replace("group", (displayName == null ? primaryGroup.getName().toUpperCase(): displayName))
                    .replace("player", hp.getName())
                    .replace("message", message)
                    .build()).queue();
        }, () -> log(Level.WARNING, "Could not send message in Discord because the guild is not available."));

    }

    public void sendMessageInMinecraft(Member member, String message) {
        final ChatColor color = ChatColor.of((member.getColor() != null ? member.getColor() : Color.BLUE));
        hitBox.getTeamChatManager().sendToTeamMembers(builder(TEAM_CHAT_PREFIX+" (§e")
                .append("&#0000ffDiscord", true)
                .append("§8) ")
                .append(builder(member.getEffectiveName()).color(color))
                .append(" §8"+ Messages.ARROW_RIGHT+"§f "+message).build());
    }

    public Optional<Guild> getGuild() {
        try {
            return Optional.ofNullable(hitBox.getDiscordManager().getDiscordBot().getJda().getGuildById(this.teamChatGuildId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isEnabled() {
        return !this.teamChatChannelId.equals("-1");
    }

}
