package de.netzkronehd.hitboxutils.database.cache.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class CustomMessageHandler {

    private final String identifier;

    public abstract void processMessage(String message);

}
