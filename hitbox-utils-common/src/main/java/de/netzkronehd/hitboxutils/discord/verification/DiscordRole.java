package de.netzkronehd.hitboxutils.discord.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DiscordRole {

    private String minecraftRoleId;
    private String discordRoleId;

    private String discordNamePrefix;


}
