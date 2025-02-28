package de.netzkronehd.hitboxutils.velocity.discord.command;

import de.netzkronehd.hitboxutils.discord.DiscordBot;
import de.netzkronehd.hitboxutils.discord.verification.VerificationRequest;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.awt.*;
import java.util.function.Consumer;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static java.awt.Color.GREEN;
import static net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions.ENABLED;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;


public class VerifySlashCommand extends BotSlashCommand {

    private final Component acceptText;

    public VerifySlashCommand(DiscordBot bot, HitBoxUtils hitBoxUtils, String name) {
        super(bot, hitBoxUtils, name);

        this.acceptText = empty().toBuilder().append(formatColoredValue("§8"+Messages.ARROW_RIGHT+" "))
                .append(
                        formatColoredValue("§a§lACCEPT")
                                .hoverEvent(showText(formatColoredValue("§aAccept the request")))
                                .clickEvent(runCommand("/discordverify accept"))
                                .color(NamedTextColor.GREEN)
                )
                .append(formatColoredValue("§8 | ").color(DARK_GRAY))
                .append(
                        formatColoredValue("§c§lDENY")
                                .hoverEvent(showText(formatColoredValue("§cDeny the request")))
                                .clickEvent(runCommand("/discordverify deny"))
                                .color(RED)
                )
                .build();
    }

    @Override
    public void onExecute(CommandInteraction interaction, String[] args) {
        interaction.deferReply(true).queue(interactionHook -> {
            if (!interactionHook.getInteraction().getGuild().getId().equalsIgnoreCase(hitBoxUtils.getDiscordManager().getDiscordBot().getGuildId())) {
                interactionHook.sendMessage("That command can not be executed here!").queue();
                return;
            }
            final OptionMapping option = interaction.getOption("minecraft-name");
            if (option == null) return;

            final String name = option.getAsString();
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(name);
            final EmbedBuilder builder = new EmbedBuilder();
            builder.setThumbnail(interaction.getGuild().getIconUrl());
            builder.setTitle("Verification request");
            builder.setColor(Color.RED);
            if (hp == null) {
                builder.setDescription("You must be online on the server to request a discord verification.");
                interactionHook.sendMessageEmbeds(builder.build()).queue();
                return;
            }

            builder.setImage("https://mineskin.eu/headhelm/"+hp.getName()+"/100.png");
            if (hp.getVerificationRequest() != null) {
                builder.setDescription("`"+hp.getName()+"` has already received an verification request.");
                interactionHook.sendMessageEmbeds(builder.build()).queue();
                return;
            }
            if (hp.isVerified()) {
                builder.setDescription("`"+hp.getName()+"` is already verified.");
                interactionHook.sendMessageEmbeds(builder.build()).queue();

                return;
            }
            final VerificationRequest request = hitBoxUtils.getDiscordVerifyManager().getRequest(interaction.getMember().getId());
            if (request != null) {
                builder.setDescription("You have already sent a verification request to `"+request.getName()+"`.");
                interactionHook.sendMessageEmbeds(builder.build()).queue();
                return;
            }
            if (hitBoxUtils.getDiscordVerifyManager().isVerified(interaction.getMember().getId())) {
                builder.setDescription("You already verified. To delete your verification use "+hitBoxUtils.getDiscordCommandManager().getSlashCommands().get("deleteverify").getCommand().getAsMention()+".");
                interactionHook.sendMessageEmbeds(builder.build()).queue();
            }

            hp.setVerificationRequest(new VerificationRequest(hp.getUniqueId(), hp.getName(), interaction.getMember().getId(), (GuildMessageChannel)interactionHook.getInteraction().getGuildChannel()));
            builder.setColor(GREEN);
            builder.setDescription("`"+hp.getName()+"` has successfully received your verification request.");

            hp.sendMessage("You received a discord verification request by§e "+interaction.getMember().getEffectiveName()+"§7.");

            hp.sendMessage(acceptText);

            interactionHook.sendMessageEmbeds(builder.build()).queue();
        });
    }

    @Override
    public void uploadCommand(JDA jda, Consumer<Command> callback) {
        jda.upsertCommand(getName(), "Command for linking your minecraft account with your discord account.")
                .setGuildOnly(true)
                .setDefaultPermissions(ENABLED)
                .addOption(OptionType.STRING, "minecraft-name", "Your name of your minecraft account.", true)
                .queue(callback);
    }
}
