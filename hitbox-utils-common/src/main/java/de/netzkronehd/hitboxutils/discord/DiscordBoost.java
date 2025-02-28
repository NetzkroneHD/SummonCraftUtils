package de.netzkronehd.hitboxutils.discord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class DiscordBoost {


    private final long memberId, timestamp;
    private UUID uuid;
    private boolean accepted;

}
