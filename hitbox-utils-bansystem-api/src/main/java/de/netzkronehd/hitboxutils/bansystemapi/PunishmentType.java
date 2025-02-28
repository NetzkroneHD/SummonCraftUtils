package de.netzkronehd.hitboxutils.bansystemapi;


public enum PunishmentType {

    BAN("tempban", "ban"),
    MUTE("tempmute", "mute");

    private final String command;
    private final String permanentCommand;

    PunishmentType(String command, String permanentCommand) {
        this.command = command;
        this.permanentCommand = permanentCommand;
    }

    public String getCommand() {
        return command;
    }

    public String getPermanentCommand() {
        return this.permanentCommand;
    }
}
