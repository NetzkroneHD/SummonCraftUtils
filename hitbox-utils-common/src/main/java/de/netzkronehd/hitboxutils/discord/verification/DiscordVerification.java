package de.netzkronehd.hitboxutils.discord.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DiscordVerification {

    private UUID uuid;
    private String name;
    private String discordId;

    private String discordRoleId;
    private String minecraftRoleId;

}
