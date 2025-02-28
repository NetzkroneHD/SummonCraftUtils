package de.netzkronehd.hitboxutils.velocity.discord.listener;

import de.netzkronehd.hitboxutils.discord.verification.DiscordRole;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class VerifyListener extends ListenerAdapter {

    private final HitBoxUtils hitBoxUtils;

    public VerifyListener(HitBoxUtils hitBoxUtils) {
        this.hitBoxUtils = hitBoxUtils;
        this.hitBoxUtils.getLuckPermsApi().getEventBus().subscribe(hitBoxUtils, UserDataRecalculateEvent.class, this::onRoleUpdated);
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent e) {
        final DiscordVerification verification = hitBoxUtils.getDiscordVerifyManager().getVerification(e.getMember().getId());
        if(verification == null) return;

        final DiscordRole discordRole = hitBoxUtils.getDiscordVerifyManager().getDiscordRoles().get(verification.getMinecraftRoleId().toLowerCase());
        if(discordRole == null) return;

        if(hitBoxUtils.getDiscordVerifyManager().getNickname(verification, discordRole).equalsIgnoreCase(e.getNewNickname())) return;

        final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
        if(guild == null) return;
        guild.modifyNickname(e.getMember(), hitBoxUtils.getDiscordVerifyManager().getNickname(verification, discordRole)).queue();

    }

    public void onRoleUpdated(UserDataRecalculateEvent e) {
        final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
        if(guild == null) return;

        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getUser().getUniqueId());
        if (hp != null) {
            if(!hp.isVerified()) return;
            if(!hasRoleUpdate(hp.getDiscordVerification(), e.getData().getMetaData().getPrimaryGroup())) return;

            final DiscordRole toRole = hitBoxUtils.getDiscordVerifyManager().getDiscordRoles().get(e.getData().getMetaData().getPrimaryGroup().toLowerCase());
            final DiscordRole fromRole = hitBoxUtils.getDiscordVerifyManager().getDiscordRoles().get(hp.getDiscordVerification().getMinecraftRoleId().toLowerCase());

            hitBoxUtils.getLogger().info("toRole: {}", toRole);
            hitBoxUtils.getLogger().info("fromRole: {}", fromRole);

            if(toRole == null || fromRole == null) return;

            hp.sendMessage("Updating your verification...");
            hitBoxUtils.getDiscordVerifyManager().updateVerification(hp.getDiscordVerification());

            guild.retrieveMemberById(hp.getDiscordVerification().getDiscordId()).queue(member -> {
                final AuditableRestAction<Void> action = hitBoxUtils.getDiscordVerifyManager().updateVerification(member, hp.getDiscordVerification(), fromRole, toRole);
                if (action == null) {
                    hp.sendMessage("Could not update your verification. Please please contact an administrator.");
                    return;
                }
                action.queue(unused -> {
                    member.modifyNickname(toRole.getDiscordNamePrefix()).queue(unused1 -> {
                        hp.sendMessage("You're verification has been updated§a successfully§7.");
                    });
                });

            });

        } else {
            final DiscordVerification verification = hitBoxUtils.getDiscordVerifyManager().getVerification(e.getUser().getUniqueId());
            if(verification == null) return;
            if(!hasRoleUpdate(verification, e.getData().getMetaData().getPrimaryGroup())) return;

            final DiscordRole toRole = hitBoxUtils.getDiscordVerifyManager().getDiscordRoles().get(e.getData().getMetaData().getPrimaryGroup().toLowerCase());
            final DiscordRole fromRole = hitBoxUtils.getDiscordVerifyManager().getDiscordRoles().get(verification.getMinecraftRoleId().toLowerCase());

            if(toRole == null || fromRole == null) return;

            hitBoxUtils.getDiscordVerifyManager().log(Level.INFO, "Updating verification '{}'...", verification);
            verification.setMinecraftRoleId(e.getData().getMetaData().getPrimaryGroup());
            verification.setDiscordRoleId(toRole.getDiscordRoleId());
            hitBoxUtils.getDiscordVerifyManager().updateVerification(verification);

            guild.retrieveMemberById(verification.getDiscordId()).queue(member -> {
                final AuditableRestAction<Void> action = hitBoxUtils.getDiscordVerifyManager().updateVerification(member, verification, fromRole, toRole);
                if (action == null) {
                    hitBoxUtils.getDiscordVerifyManager().log(Level.SEVERE, "Could not update verification '{}'.", verification);
                    return;
                }
                action.queue(unused -> hitBoxUtils.getDiscordVerifyManager().log(Level.INFO, "Updated verification '{}'.", verification));

            });

        }
    }


    private boolean hasRoleUpdate(DiscordVerification verification, String minecraftRoleId) {
        return !verification.getMinecraftRoleId().equalsIgnoreCase(minecraftRoleId);
    }

}
