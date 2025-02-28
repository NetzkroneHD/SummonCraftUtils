package de.netzkronehd.hitboxutils.bungee.discord.command;

import java.awt.Color;
import java.util.function.Consumer;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.discord.DiscordBot;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

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

            final EmbedBuilder builder = new EmbedBuilder()
                    .setThumbnail(interaction.getGuild().getIconUrl())
                    .setTitle("Verification")
                    .setImage("https://mineskin.eu/headhelm/"+verification.getName()+"/100.png")
                    .setColor(Color.GREEN)
                    .addField("Name", verification.getName(), true)
                    .addField("Minecraft-Role", verification.getMinecraftRoleId(), true);

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
