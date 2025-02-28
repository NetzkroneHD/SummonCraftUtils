package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class MessageManager extends Manager {

    private MessageManagerConfig config;

    public MessageManager(HitBoxUtils elmoBox) {
        super(elmoBox);
        this.config = new MessageManagerConfig();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            config.loadDefaults();
            save(config);
        }
    }


    @Override
    public void readFile() {
        config = getConfigJson(MessageManagerConfig.class);
        config.messages.forEach((key, message) -> Messages.valueOf(key).setValue(message));
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageManagerConfig extends ManagerConfig {

        private Map<String, String> messages;

        @Override
        public MessageManagerConfig loadDefaults() {
            this.messages = new HashMap<>();
            for (Messages message : Messages.values()) {
                this.messages.put(message.name(), message.getDefaultValue());
            }
            return this;
        }
    }

}
