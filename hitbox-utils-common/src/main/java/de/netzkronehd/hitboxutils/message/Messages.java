package de.netzkronehd.hitboxutils.message;

import lombok.Getter;

@Getter
public enum Messages {

    PREFIX("prefix", "&8[&6SummonCraft&8]&7 "),
    NO_PERMS("no-perms", "&cYou don't have the permissions to do this."),
    USAGE("usage", "&cUsage&8|&e /"),
    PLAYER_OFFLINE("player-offline", "&7That player is offline."),
    PLAYER_NOT_EXISTS("player-not-exists", "&7That player does not exists."),
    ARROW_RIGHT("arrow-right", "»"),
    ARROW_LEFT("arrow-left", "«"),
    ONE_HOUR("time.one-hour", "una hora"),
    HOURS("time.hours", "horas"),
    ONE_MINUTE("time.one-minute", "un minuto"),
    MINUTES("time.minutes", "minutos"),
    ONE_SECOND("time.one-second", "un segundo"),
    SECONDS("time.seconds", "segundos");

    private String value;
    private final String configKey, defaultValue;

    Messages(String configKey, String value) {
        this.configKey = configKey;
        this.value = value;
        this.defaultValue = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }


}
