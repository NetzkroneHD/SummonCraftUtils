package de.netzkronehd.hitboxutils.velocity.discord.command;

import de.netzkronehd.hitboxutils.discord.DiscordBoost;
import de.netzkronehd.hitboxutils.discord.DiscordBot;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.*;

import java.util.List;
import java.util.function.Consumer;

public class BoostCommand extends BotSlashCommand {

    public BoostCommand(DiscordBot bot, HitBoxUtils hitBoxUtils, String name) {
        super(bot, hitBoxUtils, name);
    }

    @Override
    public void onExecute(CommandInteraction interaction, String[] args) {
        interaction.deferReply(true).queue(interactionHook -> {
            final OptionMapping option = interaction.getOption("ingame-name");
            if (option == null) return;
            final String name = option.getAsString();
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(name);
            if (hp == null) {
                interactionHook.sendMessage("The player `" + name + "` could not be found. Please join the HitBox-Network to receive your boost-gift.").queue();
                return;
            }
            if (!hitBoxUtils.getDiscordManager().hasUnclaimedDiscordBoosts(interaction.getUser().getIdLong())) {
                interactionHook.sendMessage("You do not have any Boost-Gifts left.").queue();
                return;
            }


            final List<DiscordBoost> boosts = hitBoxUtils.getDiscordManager().getUnclaimedDiscordBoost(interaction.getUser().getIdLong());
            if (boosts.isEmpty()) {
                interactionHook.sendMessage("You do not have any Boost-Gifts left.").queue();
                return;
            }

            final DiscordBoost boost = boosts.get(0);
            boost.setUuid(hp.getUniqueId());
            hitBoxUtils.getDiscordManager().saveBoost(boost);


            hp.playSound(GlobalSound.UI_TOAST_CHALLENGE_COMPLETE);
            hp.sendMessage("You got a§d Boost-Gift§7 by§e " + interaction.getUser().getEffectiveName() + ". Type§e /claimboost§7 to receive your gift", "§7Claim your§d Boost-Gift§7.", "/claimboost");


            interactionHook.sendMessage("The player `" + hp.getName() + "` received the gift.").queue();

        });
    }

    @Override
    public void uploadCommand(JDA jda, Consumer<Command> callback) {
        jda.upsertCommand(getName(), "Command for receiving your boost gift")
                .setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                .addOption(OptionType.STRING, "ingame-name", "Your name of your ingame account.", true)
                .queue(callback);
    }
}
