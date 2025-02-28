package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.message.Messages;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

public class MessageManager extends Manager {

    public MessageManager(HitBoxUtils elmoBox) {
        super(elmoBox);
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
            message.setValue(translateAlternateColorCodes('&', cfg.getString(message.getConfigKey(), message.getDefaultValue())));
        }
    }
}
