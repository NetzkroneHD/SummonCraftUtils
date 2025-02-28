package de.netzkronehd.hitboxutils.database.cache.model;

import java.util.Objects;
import java.util.UUID;

public record UserModel(UUID uuid,
                        String name,
                        String displayName,
                        String server,
                        GroupModel group) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserModel that)) return false;

        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
