package de.netzkronehd.hitboxutils.bungee.discord.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.discord.command.BotSlashCommand;
import de.netzkronehd.hitboxutils.bungee.discord.command.DeleteVerifySlashCommand;
import de.netzkronehd.hitboxutils.bungee.discord.command.VerifySlashCommand;
import de.netzkronehd.hitboxutils.bungee.manager.Manager;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.HashMap;

@Getter
public class DiscordCommandManager extends Manager {

    private final HashMap<String, BotSlashCommand> slashCommands;

    public DiscordCommandManager(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils);
        slashCommands = new HashMap<>();

    }

    @Override
    public void onLoad() {

    }

    public void registerCommands() {
        registerSlashCommand(new VerifySlashCommand(hitBox.getDiscordManager().getDiscordBot(), hitBox, "verify"));
        registerSlashCommand(new DeleteVerifySlashCommand(hitBox.getDiscordManager().getDiscordBot(), hitBox, "deleteverify"));

    }

    public void registerSlashCommand(BotSlashCommand slashCommand) {
        hitBox.getDiscordManager().getDiscordBot().getJda().retrieveCommands().queue(commands -> {
            Command cmd = null;
            for (Command command : commands) {
                if (command.getName().equalsIgnoreCase(slashCommand.getName())) {
                    cmd = command;
                    break;
                }
            }

            if (cmd != null) {
                log("Loading command '"+slashCommand.getName()+"'...");
                registerCommand(cmd, slashCommand);
            } else {
                log("Uploading command '"+slashCommand.getName()+"'...");
                slashCommand.uploadCommand(hitBox.getDiscordManager().getDiscordBot().getJda(), command -> registerCommand(command, slashCommand));
            }
        });
    }

    public void registerCommand(Command command, BotSlashCommand slashCommand) {
        slashCommands.put(command.getId(), slashCommand);
        slashCommand.setCommand(command);
        log("Command '" + command.getName() + "' was registered.");
    }


}
