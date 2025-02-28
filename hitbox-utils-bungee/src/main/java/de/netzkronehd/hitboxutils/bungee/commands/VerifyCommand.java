package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.message.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.List;

import static de.netzkronehd.translation.Message.formatColoredValue;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public class VerifyCommand extends HitBoxCommand {

    public VerifyCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "discordverify", "dcverify", "verify");
        subcommands.addAll(List.of("accept", "deny", "info", "update", "delete"));
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (args.length != 1) {
            sendHelp(hp);
            return;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (hp.isVerified()) {
                hp.sendMessage("You are already verified.");
                return;
            }
            if (hp.getVerificationRequest() == null) {
                hp.sendMessage("You did not receive a verification request.");
                return;
            }
            hp.sendMessage("Accepting verification request...");
            final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
            if (guild == null) {
                hp.sendMessage("Could not find the guild. Please contact an administrator.");
                return;
            }
            hitBoxUtils.runAsync(() -> {
                guild.retrieveMemberById(hp.getVerificationRequest().getDiscordId()).queue(member -> {
                    hp.setVerificationRequest(null);
                    hp.setDiscordVerification(new DiscordVerification(hp.getUniqueId(), hp.getName(), member.getId(), null, hp.getPrimaryGroup().getName()));
                    hitBoxUtils.getDiscordVerifyManager().updateVerification(member, hp.getDiscordVerification(), null, hitBoxUtils.getDiscordVerifyManager().getRole(hp)).queue(unused -> {
                        hitBoxUtils.getDiscordVerifyManager().createVerification(hp.getDiscordVerification());
                        guild.modifyNickname(member, hitBoxUtils.getDiscordVerifyManager().getNickname(hp.getDiscordVerification(), hitBoxUtils.getDiscordVerifyManager().getRole(hp))).queue(unused1 -> {
                            hp.sendMessage("You successfully linked your discord account§e "+member.getEffectiveName()+"§7.");
                        });
                    });
                }, throwable -> {
                    hp.sendMessage("Could not find member: "+throwable);
                });
            });

        } else if (args[0].equalsIgnoreCase("deny")) {
            if (hp.isVerified()) {
                hp.sendMessage("You are already verified.");
                return;
            }
            if (hp.getVerificationRequest() == null) {
                hp.sendMessage("You did not receive a verification request.");
                return;
            }
            hp.sendMessage("Denying request...");
            hitBoxUtils.runAsync(() -> {
                hp.getVerificationRequest().getMessageChannel().getGuild().retrieveMemberById(hp.getVerificationRequest().getDiscordId()).queue(member -> {
                    hp.setVerificationRequest(null);
                    hp.sendMessage("Successfully§c denied§7 the request from§e " + member.getEffectiveName() + "§7.");
                    member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your verification request has been denied by `"+hp.getName()+"`.").queue());

                }, throwable -> {
                    hp.setVerificationRequest(null);
                    hp.sendMessage("Successfully§c denied§7 the request.");
                });
            });

        } else if (args[0].equalsIgnoreCase("info")) {
            if (!hp.isVerified()) {
                hp.sendMessage("You not verified. Use§e /verify§7 on our discord to verify your account.");
                return;
            }

            hp.sendMessage("Loading info...");
            final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
            if (guild == null) {
                hp.sendMessage("Could not find the guild. Please contact an administrator.");
                return;
            }
            hitBoxUtils.runAsync(() -> {
                guild.retrieveMemberById(hp.getDiscordVerification().getDiscordId()).queue(member -> {

                    final Role discordRole = member.getRoles().stream()
                            .filter(role -> hitBoxUtils.getDiscordVerifyManager().getRole(member).getDiscordRoleId().equalsIgnoreCase(role.getId()))
                            .findFirst()
                            .orElse(null);

                    final String roleName;
                    final Color color;

                    if (discordRole != null) {
                        color = discordRole.getColor()  != null ? discordRole.getColor():Color.YELLOW;
                        roleName = discordRole.getName();
                    } else {
                        color = Color.RED;
                        roleName = "Not found";
                    }

                    hp.sendLine();
                    hp.sendRawMessage("§7Discord-Name§8 "+Messages.ARROW_RIGHT+"§e "+member.getEffectiveName());
                    hp.sendMessage(text("§7Discord-Role§8 "+Messages.ARROW_RIGHT+"§e ")
                            .append(text(roleName).color(color(color.getRGB())))
                    );
                    hp.sendMessage(formatColoredValue("§7Minecraft-Role§8 "+Messages.ARROW_RIGHT+"§e "+hp.getPrefix()));
                    hp.sendLine();
                }, throwable -> hp.sendMessage("Could§c not§7 load information from your discord account§8:§e "+throwable));
            });

        } else if (args[0].equalsIgnoreCase("update")) {
            if (!hp.isVerified()) {
                hp.sendMessage("You not verified. Use§e /verify§7 on our discord to verify your account.");
                return;
            }
            final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
            if (guild == null) {
                hp.sendMessage("Could not find the guild. Please contact an administrator.");
                return;
            }
            hp.sendMessage("Updating roles...");
            hitBoxUtils.runAsync(() -> {
                guild.retrieveMemberById(hp.getDiscordVerification().getDiscordId()).queue(member -> {
                    hitBoxUtils.getDiscordVerifyManager().updateVerification(member, hp.getDiscordVerification(), hitBoxUtils.getDiscordVerifyManager().getDiscordRoles().get(hp.getDiscordVerification().getMinecraftRoleId().toLowerCase()), hitBoxUtils.getDiscordVerifyManager().getRole(hp)).queue(unused -> {
                        hitBoxUtils.getDiscordVerifyManager().setNickname(member, hp.getDiscordVerification(), hitBoxUtils.getDiscordVerifyManager().getRole(hp)).queue(unused1 -> {
                            hp.sendMessage("You're discord roles are now up to date.");
                        });
                    });
                }, throwable -> hp.sendMessage("Could§c not§7 load information from your discord account§8:§e "+throwable));
            });
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (!hp.isVerified()) {
                hp.sendMessage("You not verified. Use§e /verify§7 on our discord to verify your account.");
                return;
            }
            final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
            if (guild == null) {
                hp.sendMessage("Could not find the guild. Please contact an administrator.");
                return;
            }
            hp.sendMessage("Deleting verification...");

            hitBoxUtils.runAsync(() -> {
                guild.retrieveMemberById(hp.getDiscordVerification().getDiscordId()).queue(member -> {
                    hitBoxUtils.getDiscordVerifyManager().deleteVerification(member, hp.getDiscordVerification());
                    hp.setDiscordVerification(null);

                    member.getUser().openPrivateChannel().queue(privateChannel -> {
                        final EmbedBuilder builder = new EmbedBuilder();
                        builder.setThumbnail(guild.getIconUrl());
                        builder.setTitle("Verification request");
                        builder.setColor(Color.RED);
                        builder.setImage("https://mineskin.eu/headhelm/"+hp.getName()+"/100.png");
                        builder.setDescription("`"+hp.getName()+"` has deleted the verification with your discord account.");
                        privateChannel.sendMessageEmbeds(builder.build()).queue();
                        hp.sendMessage("Successfully deleted the verification with§e "+member.getEffectiveName()+"§7.");

                    }, throwable -> {
                        hp.setDiscordVerification(null);
                        hp.sendMessage("Successfully deleted the verification with§e "+member.getEffectiveName()+"§7.");
                    });
                }, throwable -> {
                    hitBoxUtils.getDiscordVerifyManager().deleteVerification(null, hp.getDiscordVerification());
                    hp.setDiscordVerification(null);
                    hp.sendMessage("Successfully deleted the verification.");
                });
            });

        } else sendHelp(hp);

    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("verify§8 <§eArgument§8>");
        hp.sendArrow("§eaccept§7 - Accepts a verification request.");
        hp.sendArrow("§edeny§7 - Denys a verification request.");
        hp.sendArrow("§einfo§7 - Shows information about the current verification.");
        hp.sendArrow("§eupdate§7 - Manually checks for rank updates.");
        hp.sendArrow("§edelete§7 - Deletes your verification.");
    }
}
