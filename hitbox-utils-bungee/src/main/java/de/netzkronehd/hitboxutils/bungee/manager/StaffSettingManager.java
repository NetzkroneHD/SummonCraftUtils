package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.player.StaffSettings;

import java.sql.SQLException;
import java.util.UUID;

public class StaffSettingManager extends Manager {

    public StaffSettingManager(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils);
    }

    @Override
    public void onLoad() {

    }

    public StaffSettings loadSettings(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getSettings(uuid);
        } catch (SQLException e) {
            final StaffSettings staffSettings = new StaffSettings(uuid);
            staffSettings.reset();
            return staffSettings;
        }
    }

    public StaffSettings saveSettings(StaffSettings settings) {
        try {
            return hitBox.getDatabaseManager().getDatabase().saveSettings(settings.getUuid(), settings);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
