package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.discord.verification.DiscordRole;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.discord.verification.VerificationRequest;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

@Getter
public class DiscordVerifyManager extends Manager {

    private final Map<String, DiscordRole> discordRoles;

    public DiscordVerifyManager(HitBoxUtils hitBox) {
        super(hitBox);
        setPriority(7);
        this.discordRoles = new HashMap<>();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            saveRole(new DiscordRole("default", "940985010188607498", "[USER] "));
            saveRole(new DiscordRole("vip", "1135535686821363764", "[VIP] "));
            saveRole(new DiscordRole("vip+", "1135535798511472660", "[VIP+] "));
            saveRole(new DiscordRole("platino", "1135535793990013009", "[PLATINO] "));
            saveRole(new DiscordRole("platino+", "1135535788155744256", "[PLATINO+] "));
            saveRole(new DiscordRole("inmortal", "1135535785890828318", "[INMORTAL] "));
            saveRole(new DiscordRole("inmortal+", "1135535778726957057", "[INMORTAL+] "));
            saveRole(new DiscordRole("summon", "1135535776084529162", "[SUMMON] "));
            saveRole(new DiscordRole("summon+", "1135535770770341939", "[SUMMON+] "));
            saveRole(new DiscordRole("myt", "952135188761178122", "[MINI-MYT] "));
            saveRole(new DiscordRole("youtuber", "941407386583859241", "[YOUTUBER] "));
            saveRole(new DiscordRole("tiktok", "944309514394533918", "[TIKTOK] "));
            saveRole(new DiscordRole("streamer", "944282101698674708", "[STREAMER] "));
            saveRole(new DiscordRole("famoso", "952175351533019166", "[FAMOSO] "));
            saveRole(new DiscordRole("builder", "941040301194219622", "[BUILDER] "));
            saveRole(new DiscordRole("soporte", "962788056354656386", "[SOPORTE] "));
            saveRole(new DiscordRole("helper", "940978663283888198", "[HELPER] "));
            saveRole(new DiscordRole("t-mod", "1138607145143779460", "[T-MOD] "));
            saveRole(new DiscordRole("mod", "940978109585428483", "[MOD] "));
            saveRole(new DiscordRole("sr-mod", "941053185806717008", "[SR-MOD] "));
            saveRole(new DiscordRole("t-admin", "967065191571390466", "[T-ADMIN] "));
            saveRole(new DiscordRole("admin", "955198705055719496", "[ADMIN] "));
            saveRole(new DiscordRole("sr-admin", "1136756652599681095", "[SR-ADMIN] "));
            saveRole(new DiscordRole("connfigurador", "1178032919550361680", "[CONFIG] "));
            saveRole(new DiscordRole("dev", "941031814665994330", "[DEV] "));
            saveRole(new DiscordRole("h-dev", "965014951636926485", "[H-DEV] "));
            saveRole(new DiscordRole("coordinator", "1137421283026546800", "[COORDINATOR] "));
            saveRole(new DiscordRole("manager", "941061888412901398", "[MANAGER] "));
            saveRole(new DiscordRole("co-owner", "997584770286497822", "[CO-OWNER] "));
            saveRole(new DiscordRole("owner-media", "1141135653489348739", "[OWNER-MEDIA] "));
            saveRole(new DiscordRole("owner", "951931733186584596", "[OWNER] "));
            saveRole(new DiscordRole("fundadores", "1016676402474528800", "[FOUNDER] "));
            save();
        }
    }

    @Override
    public void readFile() {
        this.discordRoles.clear();
        for (String minecraftRoleId : cfg.getKeys(false)) {
            this.discordRoles.put(minecraftRoleId.toLowerCase(), new DiscordRole(minecraftRoleId, cfg.getString(minecraftRoleId+".discordRoleId"), cfg.getString(minecraftRoleId+".discordNamePrefix", "")));
        }
        if (hitBox.getDiscordManager().getDiscordBot().isReady()) {
            checkRoles();
        } else {
            hitBox.getDiscordManager().executeWhenReady(this::checkRoles);
        }
    }

    public void checkRoles() {
        final Guild guild = hitBox.getDiscordManager().getDiscordBot().getGuild();
        if (guild == null) {
            log(Level.SEVERE, "Could not check roles because the guild '"+hitBox.getDiscordManager().getDiscordBot().getGuildId()+"' does not exists.");
            return;
        }

        final Iterator<Map.Entry<String, DiscordRole>> iterator = this.discordRoles.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, DiscordRole> next = iterator.next();
            final Role role = guild.getRoleById(next.getValue().getDiscordRoleId());
            if(role != null) continue;
            log(Level.WARNING, "Removing '"+next.getValue()+"' because the discord roles do not exist.");
            iterator.remove();
        }
    }



    public boolean hasSentRequest(String discordId) {
        return hitBox.getPlayers().stream()
                .anyMatch(hitBoxPlayer -> !hitBoxPlayer.isVerified() && hitBoxPlayer.getVerificationRequest() != null && hitBoxPlayer.getVerificationRequest().getDiscordId().equalsIgnoreCase(discordId));
    }

    public VerificationRequest getRequest(String discordId) {
        return hitBox.getPlayers().stream()
                .filter(hitBoxPlayer -> !hitBoxPlayer.isVerified() && hitBoxPlayer.getVerificationRequest() != null && hitBoxPlayer.getVerificationRequest().getDiscordId().equalsIgnoreCase(discordId)).map(HitBoxPlayer::getVerificationRequest)
                .findFirst().orElse(null);
    }

    public String getNickname(DiscordVerification verification, DiscordRole role) {
        return role.getDiscordNamePrefix()+verification.getName();
    }

    public DiscordRole getRole(HitBoxPlayer hp) {
        return this.discordRoles.get(hp.getPrimaryGroup().getName().toLowerCase());
    }

    public DiscordRole getRole(Member member) {
        for (Role role : member.getRoles()) {
            for (DiscordRole discordRole : discordRoles.values()) {
                if (!discordRole.getDiscordRoleId().equalsIgnoreCase(role.getId())) continue;
                return discordRole;
            }
        }
        return null;
    }

    public void updateVerification(Guild guild, HitBoxPlayer hp, Consumer<Member> callback) {
        guild.retrieveMemberById(hp.getDiscordVerification().getDiscordId()).queue(member -> {
            final DiscordRole role = hitBox.getDiscordVerifyManager().getRole(hp);
            if(role == null) {
                log(Level.SEVERE, "Could not update verification because role is null. Player has PrimaryGroup '"+hp.getPrimaryGroup().getName()+"'. Please check if the role exists in the config.");
                return;
            }
            hitBox.getDiscordVerifyManager().updateVerification(
                    member,
                    hp.getDiscordVerification(),
                    hitBox.getDiscordVerifyManager().getDiscordRoles().get(hp.getDiscordVerification().getMinecraftRoleId().toLowerCase()),
                    role
            ).queue(unused -> {
                hitBox.getDiscordVerifyManager().setNickname(member, hp.getDiscordVerification(), hitBox.getDiscordVerifyManager().getRole(hp)).queue(unused1 -> {
                    if(callback != null) callback.accept(member);
                });
            });
        }, throwable -> hp.sendMessage("Could§c not§7 load information from your discord account§8:§e " + throwable));
    }

    public AuditableRestAction<Void> updateVerification(Member member, DiscordVerification verification, DiscordRole from, DiscordRole to) {
        if (to == null) {
            log(Level.SEVERE, "Could not update verification because new role is null.");
            return null;
        }
        final Guild guild = hitBox.getDiscordManager().getDiscordBot().getGuild();
        if (guild == null) {
            log(Level.SEVERE, "Could not update verification because guild could not be found.");
            return null;
        }
        final Role fromRole = (from == null ? null:guild.getRoleById(from.getDiscordRoleId()));
        final Role toRole = guild.getRoleById(to.getDiscordRoleId());

        if (toRole == null) {
            log(Level.SEVERE, "Could not update verification because new role ('"+to+"') could not be found.");
            return null;
        }
        verification.setDiscordRoleId(to.getDiscordRoleId());
        verification.setMinecraftRoleId(to.getMinecraftRoleId());

        if (fromRole != null) guild.removeRoleFromMember(member, fromRole).queue();

        return guild.addRoleToMember(member, toRole);
    }

    public AuditableRestAction<Void> setNickname(Member member, DiscordVerification verification, DiscordRole role) {
        return member.getGuild().modifyNickname(member, getNickname(verification, role));
    }


    public DiscordRole getRoleByDiscordRoleId(String discordRoleId) {
        return this.discordRoles.values().stream()
                .filter(discordRole -> discordRole.getDiscordRoleId().equalsIgnoreCase(discordRoleId))
                .findFirst().orElse(null);
    }

    public void createVerification(DiscordVerification discordVerification) {
        try {
            hitBox.getDatabaseManager().getDatabase().createDiscordVerification(discordVerification);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteVerification(Member member, DiscordVerification discordVerification) {
        deleteVerification(discordVerification);
        if(member == null) return;
        member.getGuild().modifyNickname(member, member.getUser().getName()).queue();
        final DiscordRole discordRole = getRole(member);
        if (discordRole == null) return;
        final Role role = member.getGuild().getRoleById(discordRole.getDiscordRoleId());
        if(role == null) return;
        member.getGuild().removeRoleFromMember(member, role).queue();
    }

    public void deleteVerification(DiscordVerification verification) {
        try {
            hitBox.getDatabaseManager().getDatabase().deleteVerification(verification);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateVerification(DiscordVerification verification) {
        try {
            hitBox.getDatabaseManager().getDatabase().updateDiscordVerification(verification);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DiscordVerification getVerification(UUID uuid) {
        final HitBoxPlayer player = hitBox.getPlayer(uuid);
        if (player != null) return player.getDiscordVerification();

        try {
            return hitBox.getDatabaseManager().getDatabase().getDiscordVerification(uuid);
        } catch (SQLException e) {
            return null;
        }
    }

    public DiscordVerification loadVerification(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getDiscordVerification(uuid);
        } catch (SQLException e) {
            return null;
        }
    }

    public DiscordVerification getVerification(String discordId) {
        try {
            return hitBox.getPlayers().stream()
                    .filter(hitBoxPlayer -> hitBoxPlayer.isVerified() && hitBoxPlayer.getDiscordVerification().getDiscordId().equalsIgnoreCase(discordId))
                    .map(HitBoxPlayer::getDiscordVerification)
                    .findFirst().orElse(hitBox.getDatabaseManager().getDatabase().getDiscordVerification(discordId));
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean isVerified(UUID uuid) {
        final HitBoxPlayer player = hitBox.getPlayer(uuid);
        if (player != null) return player.isVerified();

        try {
            return hitBox.getDatabaseManager().getDatabase().existsDiscordVerification(uuid);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isVerified(String discordId) {
        try {
            return hitBox.getPlayers().stream()
                    .filter(hitBoxPlayer -> hitBoxPlayer.isVerified() && hitBoxPlayer.getDiscordVerification().getDiscordId().equalsIgnoreCase(discordId))
                    .map(HitBoxPlayer::isVerified)
                    .findFirst().orElse(hitBox.getDatabaseManager().getDatabase().existsDiscordVerification(discordId));
        } catch (SQLException e) {
            return false;
        }
    }

    private void saveRole(DiscordRole discordRole) {
        cfg.set(discordRole.getMinecraftRoleId()+".discordNamePrefix", discordRole.getDiscordNamePrefix());
        cfg.set(discordRole.getMinecraftRoleId()+".discordRoleId", discordRole.getDiscordRoleId());
        this.discordRoles.put(discordRole.getMinecraftRoleId(), discordRole);
    }

}
