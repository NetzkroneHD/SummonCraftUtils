package de.netzkronehd.hitboxutils.velocity.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;


@AllArgsConstructor
@Getter
public enum Cooldown {

    PLAYTIME_TOP(TimeUnit.SECONDS.toMillis(10));

    private final long time;

    public long buildCooldown() {
        return System.currentTimeMillis() + time;
    }


}
