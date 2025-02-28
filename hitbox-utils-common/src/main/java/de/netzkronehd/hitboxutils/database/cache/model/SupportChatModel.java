package de.netzkronehd.hitboxutils.database.cache.model;

import java.util.List;
import java.util.Objects;

public record SupportChatModel(TeamUserModel owner, List<TeamUserModel> users) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupportChatModel that)) return false;

        return Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(owner);
    }
}
