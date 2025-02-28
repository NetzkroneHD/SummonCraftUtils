package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.message.HexColor;
import de.netzkronehd.hitboxutils.message.Messages;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AdminVerifyCommand extends HitBoxCommand {

    public AdminVerifyCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "discordadminverify", "adminverify", "dcadminverify", "verifyadmin", "forceverify");
        subcommands.addAll(List.of("info", "verify", "delete"));
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;


        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[1]);
                if (ht != null) {
                    if (!ht.isVerified()) {
                        hp.sendMessageColored(ht.getPrefixAndName() + "§7 is not verified.");
                        return;
                    }
                    hp.sendMessage("Loading info...");
                    final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
                    if (guild == null) {
                        hp.sendMessage("Could not find the guild.");
                        return;
                    }
                    hitBoxUtils.runAsync(() -> sendInfo(guild, ht.getDiscordVerification(), hp, ht));
                } else {
                    hp.sendMessage("Loading info...");
                    final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
                    if (guild == null) {
                        hp.sendMessage("Could not find the guild.");
                        return;
                    }
                    hitBoxUtils.runAsync(() -> {
                        final UUID uuid = hitBoxUtils.getPlayerManager().getUuid(args[1]);
                        if (uuid == null) {
                            hp.sendMessage("Could not find that player in the database.");
                            return;
                        }
                        final DiscordVerification verification = hitBoxUtils.getDiscordVerifyManager().getVerification(uuid);
                        if (verification == null) {
                            hp.sendMessage("That player is not verified.");
                            return;
                        }
                        sendInfo(guild, verification, hp, null);


                    });
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[1]);
                if (ht != null) {
                    final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
                    if (guild == null) {
                        hp.sendMessage("Could not find the guild. Please contact an administrator.");
                        return;
                    }
                    hp.sendMessageColored("Deleting verification of "+ht.getPrefixAndName()+"§7...");

                    hitBoxUtils.runAsync(() -> deleteVerification(guild, ht.getDiscordVerification(), hp, ht));
                } else {

                    hitBoxUtils.runAsync(() -> {

                        final UUID uuid = hitBoxUtils.getPlayerManager().getUuid(args[1]);
                        if (uuid == null) {
                            hp.sendMessage("Could not find that player in the database.");
                            return;
                        }

                        final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
                        if (guild == null) {
                            hp.sendMessage("Could not find the guild. Please contact an administrator.");
                            return;
                        }


                        final DiscordVerification verification = hitBoxUtils.getDiscordVerifyManager().getVerification(uuid);
                        if (verification == null) {
                            hp.sendMessage("That player is not verified.");
                            return;
                        }
                        deleteVerification(guild, verification, hp, null);

                    });

                }

            } else sendHelp(hp);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("verify")) {
                final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[1]);
                if (ht == null) {
                    hp.sendMessage(Messages.PLAYER_OFFLINE);
                    return;
                }

                if (ht.isVerified()) {
                    hp.sendMessage("That player is already verified.");
                    return;
                }
                final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
                if (guild == null) {
                    hp.sendMessage("Could not find the guild.");
                    return;
                }
                ht.setVerificationRequest(null);

                hp.sendMessage("Loading verification...");
                hitBoxUtils.runAsync(() -> {
                    guild.retrieveMemberById(args[2]).queue(member -> {
                        if (hitBoxUtils.getDiscordVerifyManager().isVerified(member.getId())) {
                            hp.sendMessage("§e"+member.getEffectiveName()+"§7 is already verified.");
                            return;
                        }

                        ht.setDiscordVerification(new DiscordVerification(ht.getUniqueId(), ht.getName(), member.getId(), null, ht.getPrimaryGroup().getName()));
                        hitBoxUtils.getDiscordVerifyManager().updateVerification(member, ht.getDiscordVerification(), null, hitBoxUtils.getDiscordVerifyManager().getRole(ht)).queue(unused -> {
                            hitBoxUtils.getDiscordVerifyManager().createVerification(ht.getDiscordVerification());
                            guild.modifyNickname(member, hitBoxUtils.getDiscordVerifyManager().getNickname(hp.getDiscordVerification(), hitBoxUtils.getDiscordVerifyManager().getRole(ht))).queue(unused1 -> {
                                hp.sendMessageColored("You successfully linked the discord account§e "+member.getEffectiveName()+"§7 with "+ht.getPrefixAndName()+".");
                            });
                        });

                    }, throwable -> hp.sendMessage("Could not load member: " + throwable));
                });


            } else sendHelp(hp);
        } else sendHelp(hp);
    }

    private void deleteVerification(Guild guild, DiscordVerification verification, HitBoxPlayer hp, HitBoxPlayer ht) {
        guild.retrieveMemberById(verification.getDiscordId()).queue(member -> {
            if(ht != null) {
                ht.setDiscordVerification(null);
                hp.sendMessageColored("Successfully deleted the verification of§e "+ht.getPrefixAndName()+".");
            } else hp.sendMessage("Successfully deleted the verification of§e "+verification.getName()+".");
            hitBoxUtils.getDiscordVerifyManager().deleteVerification(member, verification);

            member.modifyNickname(member.getUser().getName()).queue();

        }, throwable -> {
            if(ht != null) {
                ht.setDiscordVerification(null);
                hp.sendMessageColored("Successfully deleted the verification of§e "+ht.getPrefixAndName()+".");
            } else hp.sendMessage("Successfully deleted the verification of§e "+verification.getName()+".");
            hitBoxUtils.getDiscordVerifyManager().deleteVerification(null, verification);

        });
    }

    private void sendInfo(Guild guild, DiscordVerification verification, HitBoxPlayer hp, HitBoxPlayer ht) {
        guild.retrieveMemberById(verification.getDiscordId()).queue(member -> {

            final Role discordRole = member.getRoles().stream()
                    .filter(role -> hitBoxUtils.getDiscordVerifyManager().getRole(member).getDiscordRoleId().equalsIgnoreCase(role.getId()))
                    .findFirst()
                    .orElse(null);

            final ChatColor color;
            final String roleName;

            if (discordRole != null) {
                roleName = discordRole.getName();
                color = ChatColor.of(discordRole.getColor() != null ? discordRole.getColor() : Color.YELLOW);
            } else {
                roleName = "Not found";
                color = ChatColor.RED;
            }

            final TextComponent roleComponent = new TextComponent(roleName);
            final TextComponent textComponent = new TextComponent("§7Discord-Role§8 " + Messages.ARROW_RIGHT + "§e ");

            textComponent.addExtra(roleComponent);
            roleComponent.setColor(color);

            hp.sendLine();
            hp.sendRawMessage("§7Minecraft-Name§8 "+Messages.ARROW_RIGHT+"§e "+verification.getName());
            hp.sendMessage(HexColor.translateHexCodesInComponents("§7Minecraft-Role§8 "+Messages.ARROW_RIGHT+"§e "+(ht != null ? ht.getPrefix():verification.getMinecraftRoleId())));
            hp.sendRawMessage("§7Discord-Name§8 "+Messages.ARROW_RIGHT+"§e "+member.getEffectiveName());
            hp.sendMessage(textComponent);
            hp.sendLine();
        }, throwable -> hp.sendMessage("Could§c not§7 load information from your discord account§8:§e "+throwable));
    }


    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("adminverify§8 <§eArgument§8>");
        hp.sendArrow("§everify§8 <§ePlayer§8> <§eDiscordId§8>§7 - Verifies a user.");
        hp.sendArrow("§einfo §8<§ePlayer§8>§7 - Shows information about a verification.");
        hp.sendArrow("§edelete §8<§ePlayer§8>§7 - Deletes a verification.");

    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                return super.onTab(hp, args);
            } else if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("verify") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("delete")) {
                    return getPlayerTabComplete(args[1]);
                }
            }
        }
        return Collections.emptyList();
    }
}
