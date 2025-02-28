package de.netzkronehd.hitboxutils.velocity.discord.listener;

import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DiscordListener extends ListenerAdapter {

    private final HitBoxUtils hitBoxUtils;

    @Override
    public void onReady(@NotNull ReadyEvent e) {
        e.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
        e.getJDA().getPresence().setActivity(Activity.listening(hitBoxUtils.getDiscordManager().getConfig().getActivity()));
        hitBoxUtils.getDiscordManager().getDiscordBot().setReady(true);

        hitBoxUtils.getLogger().info("Discord-Bot loaded successfully.");

        hitBoxUtils.getLogger().info("Loading guild...");
        final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
        if (guild == null) {
            hitBoxUtils.getLogger().info("Could not load guild with the id '{}'.", hitBoxUtils.getDiscordManager().getDiscordBot().getGuildId());
        } else {
            hitBoxUtils.getLogger().info("Successfully loaded Guild '{}'.", guild.getName());
        }


        hitBoxUtils.getLogger().info("Executing on ready runnable...");

        for (Runnable runnable : hitBoxUtils.getDiscordManager().getOnReady()) {
            hitBoxUtils.runAsync(runnable);
        }

    }

    @Override
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent e) {
        hitBoxUtils.getLogger().info("###############################");
        hitBoxUtils.getLogger().info("Received BoostTime: {}", e);
        hitBoxUtils.getLogger().info("IsBoosting: {}", e.getMember().isBoosting());
        hitBoxUtils.getLogger().info("Member: {}", e.getMember().getEffectiveName());
        hitBoxUtils.getLogger().info("###############################");

        e.getGuild().retrieveMemberById(407931744604520499L).queue(member -> member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Received BoostTime: ```" + e + "```\n" +
                "IsBoosting: " + e.getMember().isBoosting() + "\n" +
                "Member: " + e.getMember().getEffectiveName()).queue()), throwable -> {
        });
    }

}
