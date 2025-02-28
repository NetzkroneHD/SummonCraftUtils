package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import lombok.Getter;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodesInComponents;
import static de.netzkronehd.hitboxutils.utils.Utils.getRemainingTimeInMinutes;

@Getter
public class RestartManager extends Manager {

    private Timer timer;
    private int countdown;

    public RestartManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public void startCountdown(int seconds) {
        this.countdown = seconds;

        sendTitle("§c§lPor favor, abandone el área de PvP");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (countdown > 15 && countdown % 10 == 0) {
                    sendTitle("§e&lReinicio de proxy en " + getRemainingTimeInMinutes(countdown));
                } else if (countdown <= 15 && countdown > 0) {
                    sendTitle("§e&lReinicio de proxy en " + getRemainingTimeInMinutes(countdown));
                } else if (countdown <= 0) {
                    sendTitle("§e§lReiniciando...");
                    hitBox.getProxy().stop("§eEl servidor proxy será reiniciado...");
                    return;
                }
                countdown--;
            }
        }, 0, TimeUnit.SECONDS.toMillis(1));
    }

    public void cancel() {
        timer.cancel();
        timer = null;
        sendTitle("§a§lProxy restart was cancelled");
    }

    public boolean isRunning() {
        return timer != null;
    }

    private void sendTitle(String subtile) {
        final TextComponent text = translateHexCodesInComponents(subtile);
        final Title title = hitBox.getProxy().createTitle();
        title.title(new TextComponent("§e"));
        title.subTitle(text);
        title.fadeIn(20);
        title.stay(20 * 3);
        title.fadeOut(20);
        hitBox.getProxy().getPlayers().forEach(title::send);
        hitBox.getProxy().broadcast(text);

    }
}
