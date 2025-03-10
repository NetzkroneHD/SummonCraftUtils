package de.netzkronehd.hitboxutils.punishmentscore.api.database.models;

public class Sanction {

    private final int id;
    private final String banned_by_name;
    private final String reason;
    private final Long date;
    private final Long until;

    public Sanction(int id, String banned_by_name, String reason, Long date, Long until) {
        this.id = id;
        this.banned_by_name = banned_by_name;
        this.reason = reason;
        this.date = date;
        this.until = until;
    }

    public String getStaff() {
        return this.banned_by_name;
    }

    public String getReason() {
        return this.reason;
    }

    public Long getDate() {
        return this.date;
    }

    public Long getUntil() {
        return this.until;
    }

    public int getId() {
        return this.id;
    }
}
