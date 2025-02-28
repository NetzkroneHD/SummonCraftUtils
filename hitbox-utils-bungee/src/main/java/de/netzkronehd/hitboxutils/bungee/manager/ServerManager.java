package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.configuration.file.YamlConfiguration;
import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;

import java.io.File;
import java.io.IOException;

public class ServerManager extends Manager {

    private final File bungeeConfig;

    public ServerManager(HitBoxUtils hitBox) {
        super(hitBox);
        bungeeConfig = new File("config.yml");
    }

    @Override
    public void onLoad() {

    }


    public void setServer(String name, String address, String motd, boolean restricted) throws IOException {
        final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(bungeeConfig);
        cfg.set("servers." + name + ".address", address);
        cfg.set("servers." + name + ".motd", motd);
        cfg.set("servers." + name + ".restricted", restricted);
        cfg.save(bungeeConfig);
    }

}
