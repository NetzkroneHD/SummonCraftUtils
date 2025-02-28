package de.netzkronehd.hitboxutils.velocity.discord.command;

import de.netzkronehd.hitboxutils.discord.DiscordBot;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.awt.*;
import java.util.function.Consumer;

public class VerifyInfoSlashCommand extends BotSlashCommand {

    public VerifyInfoSlashCommand(DiscordBot bot, HitBoxUtils hitBoxUtils, String name) {
        super(bot, hitBoxUtils, name);
    }

    @Override
    public void onExecute(CommandInteraction interaction, String[] args) {
        interaction.deferReply(true).queue(interactionHook -> {
            final DiscordVerification verification = hitBoxUtils.getDiscordVerifyManager().getVerification(interaction.getMember().getId());
            if (verification == null) {
                interactionHook.sendMessage("You are not verified.").queue();
                return;
            }

            final EmbedBuilder builder = new EmbedBuilder();
            builder.setThumbnail(interaction.getGuild().getIconUrl());
            builder.setTitle("Verification");
            builder.setImage("https://mineskin.eu/headhelm/"+verification.getName()+"/100.png");
            builder.setColor(Color.GREEN);
            builder.addField("Name", verification.getName(), true);
            builder.addField("Minecraft-Role", verification.getMinecraftRoleId(), true);
            final Role role = interaction.getMember().getGuild().getRoleById(verification.getDiscordRoleId());
            builder.addField("Discord-Role", (role != null ? role.getAsMention():"Not found"), true);
            interactionHook.sendMessageEmbeds(builder.build()).queue();
        });
    }

    @Override
    public void uploadCommand(JDA jda, Consumer<Command> callback) {
        jda.upsertCommand(getName(), "Command for information about your discord verification.")
                .setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                .queue(callback);
    }
}
