package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import lombok.Getter;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static de.netzkronehd.hitboxutils.utils.Utils.getRemainingTimeInMinutes;
import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static net.kyori.adventure.title.Title.title;

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
                    hitBox.getProxyServer().shutdown(formatColoredValue("§eEl servidor proxy será reiniciado..."));
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
        final Title title = title(formatColoredValue("§e "),
                formatColoredValue(subtile),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
        );
        hitBox.getProxyServer().sendMessage(title.subtitle());
        hitBox.getProxyServer().getAllPlayers().forEach(player -> player.showTitle(title));
    }
}
