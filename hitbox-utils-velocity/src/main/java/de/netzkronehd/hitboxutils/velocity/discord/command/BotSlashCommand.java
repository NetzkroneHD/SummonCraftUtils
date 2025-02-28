package de.netzkronehd.hitboxutils.velocity.discord.command;

import de.netzkronehd.hitboxutils.discord.DiscordBot;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Data
public abstract class BotSlashCommand {

    protected final DiscordBot bot;
    protected final HitBoxUtils hitBoxUtils;
    private final String name;
    private Command command;

    public abstract void onExecute(CommandInteraction interaction, String[] args);

    public abstract void uploadCommand(JDA jda, Consumer<Command> callback);

    public void sendHelp(Message msg) {
    }

    public void sendHelp(MessageChannel channel) {
    }

}
