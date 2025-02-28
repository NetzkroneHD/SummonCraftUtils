package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.discord.verification.DiscordRole;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.discord.verification.VerificationRequest;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import lombok.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

@Getter
public class DiscordVerifyManager extends Manager {

    private final Map<String, DiscordRole> discordRoles;
    private DiscordVerifyConfig config;

    public DiscordVerifyManager(HitBoxUtils hitBox) {
        super(hitBox);
        setPriority(7);
        this.discordRoles = new HashMap<>();
        this.config = new DiscordVerifyConfig();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            config.loadDefaults();
            save(config);
        }
    }

    @Override
    public void readFile() {
        config = getConfigJson(DiscordVerifyConfig.class);
        this.discordRoles.clear();
        for (DiscordRole discordRole : config.discordRoles) {
            this.discordRoles.put(discordRole.getMinecraftRoleId().toLowerCase(), discordRole);
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
            hitBox.getDiscordVerifyManager().updateVerification(member, hp.getDiscordVerification(), hitBox.getDiscordVerifyManager().getDiscordRoles().get(hp.getDiscordVerification().getMinecraftRoleId().toLowerCase()), hitBox.getDiscordVerifyManager().getRole(hp)).queue(unused -> {
                hitBox.getDiscordVerifyManager().setNickname(member, hp.getDiscordVerification(), hitBox.getDiscordVerifyManager().getRole(hp)).queue(unused1 -> {
                    if(callback != null) callback.accept(member);
                });
            });
        }, throwable -> hp.sendMessage("Could§c not§7 load information from your discord account§8:§e " + throwable));
    }

    public AuditableRestAction<Void> updateVerification(Member member, DiscordVerification verification, DiscordRole from, DiscordRole to) {
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

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiscordVerifyConfig extends ManagerConfig {
        private List<DiscordRole> discordRoles;

        @Override
        public DiscordVerifyConfig loadDefaults() {
            discordRoles = new ArrayList<>();
            discordRoles.add(new DiscordRole("default", "940985010188607498", "[USER] "));
            discordRoles.add(new DiscordRole("vip", "1135535686821363764", "[VIP] "));
            discordRoles.add(new DiscordRole("vip+", "1135535798511472660", "[VIP+] "));
            discordRoles.add(new DiscordRole("platino", "1135535793990013009", "[PLATINO] "));
            discordRoles.add(new DiscordRole("platino+", "1135535788155744256", "[PLATINO+] "));
            discordRoles.add(new DiscordRole("inmortal", "1135535785890828318", "[INMORTAL] "));
            discordRoles.add(new DiscordRole("inmortal+", "1135535778726957057", "[INMORTAL+] "));
            discordRoles.add(new DiscordRole("summon", "1135535776084529162", "[SUMMON] "));
            discordRoles.add(new DiscordRole("summon+", "1135535770770341939", "[SUMMON+] "));
            discordRoles.add(new DiscordRole("myt", "952135188761178122", "[MINI-MYT] "));
            discordRoles.add(new DiscordRole("youtuber", "941407386583859241", "[YOUTUBER] "));
            discordRoles.add(new DiscordRole("tiktok", "944309514394533918", "[TIKTOK] "));
            discordRoles.add(new DiscordRole("streamer", "944282101698674708", "[STREAMER] "));
            discordRoles.add(new DiscordRole("famoso", "952175351533019166", "[FAMOSO] "));
            discordRoles.add(new DiscordRole("builder", "941040301194219622", "[BUILDER] "));
            discordRoles.add(new DiscordRole("soporte", "962788056354656386", "[SOPORTE] "));
            discordRoles.add(new DiscordRole("helper", "940978663283888198", "[HELPER] "));
            discordRoles.add(new DiscordRole("t-mod", "1138607145143779460", "[T-MOD] "));
            discordRoles.add(new DiscordRole("mod", "940978109585428483", "[MOD] "));
            discordRoles.add(new DiscordRole("sr-mod", "941053185806717008", "[SR-MOD] "));
            discordRoles.add(new DiscordRole("t-admin", "967065191571390466", "[T-ADMIN] "));
            discordRoles.add(new DiscordRole("admin", "955198705055719496", "[ADMIN] "));
            discordRoles.add(new DiscordRole("sr-admin", "1136756652599681095", "[SR-ADMIN] "));
            discordRoles.add(new DiscordRole("connfigurador", "1178032919550361680", "[CONFIG] "));
            discordRoles.add(new DiscordRole("dev", "941031814665994330", "[DEV] "));
            discordRoles.add(new DiscordRole("h-dev", "965014951636926485", "[H-DEV] "));
            discordRoles.add(new DiscordRole("coordinator", "1137421283026546800", "[COORDINATOR] "));
            discordRoles.add(new DiscordRole("manager", "941061888412901398", "[MANAGER] "));
            discordRoles.add(new DiscordRole("co-owner", "997584770286497822", "[CO-OWNER] "));
            discordRoles.add(new DiscordRole("owner-media", "1141135653489348739", "[OWNER-MEDIA] "));
            discordRoles.add(new DiscordRole("owner", "951931733186584596", "[OWNER] "));
            discordRoles.add(new DiscordRole("fundadores", "1016676402474528800", "[FOUNDER] "));
            return this;
        }
    }

}
