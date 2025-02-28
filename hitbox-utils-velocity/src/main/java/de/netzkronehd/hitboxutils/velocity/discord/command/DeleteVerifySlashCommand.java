package de.netzkronehd.hitboxutils.velocity.discord.command;

import de.netzkronehd.hitboxutils.discord.DiscordBot;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.awt.*;
import java.util.function.Consumer;

public class DeleteVerifySlashCommand extends BotSlashCommand {

    public DeleteVerifySlashCommand(DiscordBot bot, HitBoxUtils hitBoxUtils, String name) {
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

            final HitBoxPlayer hp = hitBoxUtils.getPlayer(verification.getUuid());
            if (hp != null) {
                hp.sendMessage("§e"+interaction.getMember().getEffectiveName()+"§7 removed your discord verification.");
                hp.setDiscordVerification(null);
            }

            hitBoxUtils.getDiscordVerifyManager().deleteVerification(interaction.getMember(), verification);


            final EmbedBuilder builder = new EmbedBuilder();
            builder.setThumbnail(interaction.getGuild().getIconUrl());
            builder.setTitle("Verification");
            builder.setImage("https://mineskin.eu/headhelm/"+verification.getName()+"/100.png");
            builder.setColor(Color.GREEN);
            builder.setDescription("You successfully removed your verification with `"+verification.getName()+"`.");

            interactionHook.sendMessageEmbeds(builder.build()).queue();
            interaction.getMember().modifyNickname(interaction.getUser().getName()).queue();

        });
    }

    @Override
    public void uploadCommand(JDA jda, Consumer<Command> callback) {
        jda.upsertCommand(getName(), "Command for deleting your minecraft account linking with your discord account.")
                .setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                .queue(callback);
    }
}
