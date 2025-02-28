package de.netzkronehd.hitboxutils.bungee.discord.command;

import java.awt.Color;
import java.util.function.Consumer;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.discord.DiscordBot;
import de.netzkronehd.hitboxutils.discord.verification.VerificationRequest;
import de.netzkronehd.hitboxutils.message.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import static net.kyori.adventure.text.Component.text;
import net.kyori.adventure.text.TextComponent;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class VerifySlashCommand extends BotSlashCommand {

    private final TextComponent acceptText;

    public VerifySlashCommand(DiscordBot bot, HitBoxUtils hitBoxUtils, String name) {
        super(bot, hitBoxUtils, name);

        this.acceptText = text(Messages.ARROW_RIGHT+" ")
                .color(NamedTextColor.DARK_GRAY)
                .append(text("§a§lACCEPT")
                        .hoverEvent(text("§aAccept the request"))
                        .clickEvent(runCommand("/discordverify accept"))
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD))
                .append(text("§8 | "))
                .append(text("§c§lDENY")
                        .hoverEvent(text("§cDeny the request"))
                        .clickEvent(runCommand("/discordverify deny"))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD));

    }

    @Override
    public void onExecute(CommandInteraction interaction, String[] args) {
        final OptionMapping option = interaction.getOption("minecraft-name");
        if (option == null) return;
        

        final String name = option.getAsString();
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(name);
        if(hp == null) return;

        interaction.deferReply(true).queue(interactionHook -> {
            if (!interactionHook.getInteraction().getGuild().getId().equalsIgnoreCase(hitBoxUtils.getDiscordManager().getDiscordBot().getGuildId())) {
                interactionHook.sendMessage("That command can not be executed here!").queue();
                return;
            }
            final EmbedBuilder builder = new EmbedBuilder();
            builder.setThumbnail(interaction.getGuild().getIconUrl());
            builder.setTitle("Verification request");
            builder.setColor(Color.RED);

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
                return;
            }

            hp.setVerificationRequest(new VerificationRequest(hp.getUniqueId(), hp.getName(), interaction.getMember().getId(), (GuildMessageChannel)interactionHook.getInteraction().getGuildChannel()));
            builder.setColor(Color.GREEN);
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
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                .addOption(OptionType.STRING, "minecraft-name", "Your name of your minecraft account.", true)
                .queue(callback);
    }
}
