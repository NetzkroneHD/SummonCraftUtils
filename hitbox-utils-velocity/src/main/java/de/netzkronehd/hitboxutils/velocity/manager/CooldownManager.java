package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.velocity.utils.Cooldown;

public class CooldownManager extends Manager {

    public CooldownManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public boolean isOver(HitBoxPlayer hp, Cooldown cooldown) {
        return Utils.isOver(hp.getCooldown().get(cooldown));
    }

    public void setCooldown(HitBoxPlayer hp, Cooldown cooldown) {
        hp.getCooldown().put(cooldown, cooldown.buildCooldown());
    }

    public String getRemainingTime(HitBoxPlayer hp, Cooldown cooldown) {
        if (isOver(hp, cooldown)) return "";
        return Utils.getRemainingTime(hp.getCooldown().get(cooldown));
    }


}
