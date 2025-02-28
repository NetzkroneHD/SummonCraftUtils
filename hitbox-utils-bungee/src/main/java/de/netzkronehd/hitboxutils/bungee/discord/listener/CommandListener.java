package de.netzkronehd.hitboxutils.bungee.discord.listener;

import de.netzkronehd.hitboxutils.bungee.discord.command.BotSlashCommand;
import de.netzkronehd.hitboxutils.bungee.discord.manager.DiscordCommandManager;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@AllArgsConstructor
public class CommandListener extends ListenerAdapter {

    private final DiscordCommandManager discordCommandManager;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        discordCommandManager.registerCommands();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (e.getInteraction().getUser().getId().equals(e.getJDA().getSelfUser().getId())) return;
        final BotSlashCommand command = discordCommandManager.getSlashCommands().get(e.getCommandId());
        if (command != null) {
            final String[] fullArgs = e.getInteraction().getFullCommandName().split(" ");
            final String[] args;

            if (fullArgs.length >= 2) {
                args = new String[fullArgs.length - 1];
                System.arraycopy(fullArgs, 1, args, 0, fullArgs.length - 1);
            } else args = new String[0];

            command.onExecute(e.getInteraction(), args);
            discordCommandManager.log("'"+e.getInteraction().getUser().getName()+"' performed command '"+e.getName()+"' (id: "+e.getCommandId()+") with the options '"+e.getInteraction().getOptions()+"' and the args '"+Arrays.toString(args)+"'.");
        }
    }

}
