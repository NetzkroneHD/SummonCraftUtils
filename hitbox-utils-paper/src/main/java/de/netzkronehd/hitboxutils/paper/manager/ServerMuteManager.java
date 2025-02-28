package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.List;

import static de.netzkronehd.translation.Message.formatColoredValue;

@Getter
public class ServerMuteManager extends Manager {

    private String enableMessage, disableMessage;
    private Component blockMessage;
    private boolean enabled;

    public ServerMuteManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("enabled", false);
            cfg.set("message.enabled", List.of("", "&cThe Chat was disabled by %PLAYER%&c.", ""));
            cfg.set("message.disabled", List.of("", "&aThe Chat was enabled by %PLAYER%&a.", ""));
            cfg.set("message.blocked", "&cThe Chat is currently disabled.");
            save();
        }
    }

    @Override
    public void readFile() {
        this.enabled = cfg.getBoolean("enabled", false);
        this.blockMessage = formatColoredValue(Messages.PREFIX+cfg.getString("message.blocked", "&cThe Chat is currently disabled."));

        final List<String> enableMessages = cfg.getStringList("message.enabled");
        final List<String> disableMessages = cfg.getStringList("message.disabled");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < enableMessages.size(); i++) {
            if((i+1) == enableMessages.size()) {
                sb.append(enableMessages.get(i));
            } else sb.append(enableMessages.get(i)).append("\n");
        }
        this.enableMessage = sb.toString();
        sb = new StringBuilder();
        for (int i = 0; i < disableMessages.size(); i++) {
            if((i+1) == disableMessages.size()) {
                sb.append(disableMessages.get(i));
            } else sb.append(disableMessages.get(i)).append("\n");
        }
        this.disableMessage = sb.toString();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        cfg.set("enabled", enabled);
        save();
    }
}
