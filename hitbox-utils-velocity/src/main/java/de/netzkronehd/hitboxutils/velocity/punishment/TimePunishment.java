package de.netzkronehd.hitboxutils.velocity.punishment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TimePunishment {

    private final String id;
    private String reason;
    private long time;
    private long limit;
    private String timeUnit;


    public boolean isPermanent() {
        return time == -1;
    }

}
