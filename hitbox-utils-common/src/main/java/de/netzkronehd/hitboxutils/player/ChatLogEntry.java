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
public class ChatLogEntry {

    private UUID uuid;
    private long timestamp;
    private String name, server, message;

}
