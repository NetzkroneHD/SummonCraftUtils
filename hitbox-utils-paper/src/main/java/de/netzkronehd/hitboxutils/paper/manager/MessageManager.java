package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import org.bukkit.ChatColor;

public class MessageManager extends Manager {

    public MessageManager(HitBoxUtils elmoBox) {
        super(elmoBox);
        setPriority(2);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            for (Messages message : Messages.values()) {
                cfg.set(message.getConfigKey(), message.getDefaultValue());
            }
            save();
        }
    }


    @Override
    public void readFile() {
        for (Messages message : Messages.values()) {
            message.setValue(ChatColor.translateAlternateColorCodes('&', cfg.getString(message.getConfigKey(), message.getDefaultValue())));
        }
    }
}
