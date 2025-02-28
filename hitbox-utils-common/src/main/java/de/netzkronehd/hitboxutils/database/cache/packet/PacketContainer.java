package de.netzkronehd.hitboxutils.database.cache.packet;

import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PacketContainer {

    private static final Gson gson = new Gson();

    private String packetClass;
    private String packetJson;

    public PacketContainer(Class<?> packetClass, HitBoxPacket hitBoxPacket) {
        this.packetClass = packetClass.getName();
        this.packetJson = gson.toJson(hitBoxPacket);
    }

    public String toMessage() {
        return gson.toJson(this);
    }

    public static PacketContainer fromMessage(String message) {
        return gson.fromJson(message, PacketContainer.class);
    }

    public HitBoxPacket getPacket() {
        return (HitBoxPacket) gson.fromJson(packetJson, getPacketClass());
    }

    public Class<?> getPacketClass() {
        try {
            return Class.forName(this.packetClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
