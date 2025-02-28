package de.netzkronehd.hitboxutils.discord.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.util.UUID;

@Data
@AllArgsConstructor
public class VerificationRequest {

    private final UUID uuid;
    private final String name;
    private final String discordId;
    private final GuildMessageChannel messageChannel;


}
