package de.netzkronehd.hitboxutils.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class StaffSettings {

    private final UUID uuid;
    private boolean pickUpItems;
    private boolean doubleSneakSpectator;
    private boolean autoVanish;
    private boolean autoEnable;
    private boolean filterBroadcast;
    private boolean mineBroadcast;


    public StaffSettings reset() {
        pickUpItems = false;
        doubleSneakSpectator = false;
        autoVanish = false;
        autoEnable = false;
        filterBroadcast = true;
        mineBroadcast = true;
        return this;
    }

}
