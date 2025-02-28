package de.netzkronehd.hitboxutils.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PlayerInfo {

    private final UUID uuid;
    private long lastJoin, lastQuit;
    private String lastProxy, lastIp;

}
