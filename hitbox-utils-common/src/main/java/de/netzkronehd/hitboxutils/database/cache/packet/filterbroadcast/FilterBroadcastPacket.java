package de.netzkronehd.hitboxutils.database.cache.packet.filterbroadcast;

import de.netzkronehd.hitboxutils.database.cache.model.FilterResultModel;
import de.netzkronehd.hitboxutils.database.cache.model.UserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FilterBroadcastPacket extends HitBoxPacket {

    private final UserModel user;
    private final FilterResultModel filterResultModel;

    public FilterBroadcastPacket(String source, UserModel user, FilterResultModel filterResultModel) {
        super(source);
        this.user = user;
        this.filterResultModel = filterResultModel;
    }
}
