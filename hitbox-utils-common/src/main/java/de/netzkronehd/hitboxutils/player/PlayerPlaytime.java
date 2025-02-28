package de.netzkronehd.hitboxutils.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerPlaytime {

    private UUID uuid;
    private String name;
    private long playtime, timeJoined;

}
