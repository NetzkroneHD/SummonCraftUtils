package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.command.HitBoxCommand;

import java.util.HashMap;

public class CommandManager extends Manager {

    private final HashMap<String, HitBoxCommand> commands;

    public CommandManager(HitBoxUtils elmoBox) {
        super(elmoBox);
        commands = new HashMap<>();
    }

    @Override
    public void onLoad() {

    }

    public void registerCommand(HitBoxCommand sc) {
        try {
            hitBox.getCommand(sc.getName()).setExecutor(sc);
            hitBox.getCommand(sc.getName()).setAliases(sc.getAlias());
            hitBox.getCommand(sc.getName()).setTabCompleter(sc);
            commands.put(sc.getName(), sc);
            log("Registered '" + sc.getSimpleName() + "'.");
        } catch (NullPointerException ex) {
            hitBox.getLogger().warning("Could not register command '" + sc.getSimpleName() + "': " + ex);
        }
    }

    public void registerCommand(HitBoxCommand... sc) {
        for (HitBoxCommand elmoCommand : sc) {
            registerCommand(elmoCommand);
        }
    }

    public HashMap<String, HitBoxCommand> getCommands() {
        return commands;
    }
}
