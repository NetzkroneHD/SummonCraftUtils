package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static de.netzkronehd.translation.Message.formatColoredValue;

@Getter
public class RestartManager extends Manager {

    private Timer timer;
    private int countdown;

    private String restartCommand, cancelCommand;
    private boolean custom, running;

    public RestartManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("custom", false);
            cfg.set("restart-command", "uar now %SECONDS%");
            cfg.set("cancel-command", "uar stop");
            save();
        }
    }

    @Override
    public void readFile() {
        this.custom = cfg.getBoolean("custom", false);
        this.restartCommand = cfg.getString("restart-command", "uar now %SECONDS%");
        this.cancelCommand = cfg.getString("cancel-command", "uar stop");
    }

    public void startCountdown(int seconds, boolean broadcast) {
        this.running = true;
        if (custom) {
            hitBox.getServer().dispatchCommand(hitBox.getServer().getConsoleSender(), this.restartCommand.replace("%SECONDS%", String.valueOf(seconds)));
        } else {
            this.countdown = seconds;

            if(broadcast) sendTitle("§c§lPor favor, abandone el área de PvP");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (countdown > 15 && countdown % 10 == 0) {
                        if (broadcast) sendTitle("§e&lReinicio el servidor en " + Utils.getRemainingTimeInMinutes(countdown));
                    } else if (countdown <= 15 && countdown > 0) {
                        if(broadcast) sendTitle("§e&lReinicio el servidor en " + Utils.getRemainingTimeInMinutes(countdown));
                    } else if (countdown <= 0) {
                        if(broadcast) sendTitle("§e§lReiniciando...");
                        hitBox.getServer().shutdown();
                        return;
                    }
                    countdown--;
                }
            }, 0, TimeUnit.SECONDS.toMillis(1));
        }
    }

    public void cancel() {
        if (custom) {
            hitBox.getServer().dispatchCommand(hitBox.getServer().getConsoleSender(), this.cancelCommand);
        } else {
            timer.cancel();
            timer = null;
            sendTitle("§a§lServer restart was cancelled");
        }
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    private void sendTitle(String subtile) {
        final Title title = Title.title(formatColoredValue("§e "),
                formatColoredValue(subtile),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
        );
        hitBox.getServer().broadcast(title.subtitle());
        hitBox.getServer().getOnlinePlayers().forEach(player -> player.showTitle(title));
    }

}
