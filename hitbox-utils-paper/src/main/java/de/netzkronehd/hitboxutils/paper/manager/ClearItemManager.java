package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodes;
import static me.clip.placeholderapi.PlaceholderAPI.setPlaceholders;

@Getter
public class ClearItemManager extends Manager {

    private boolean enabled;
    @Setter
    private int initialCountdown, countdown;
    private final Set<Integer> broadcastCountdown;
    private String broadcastMessage, doneMessage;
    private Sound broadcastSound;

    private BukkitTask taskTimer;

    public ClearItemManager(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils);
        broadcastCountdown = new HashSet<>();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
        if(!enabled) return;
        startCountdown();
    }

    @Override
    public void onReload() {
        try {
            taskTimer.cancel();
        } catch (Exception e) {
            log(Level.WARNING, "Failed to cancel task timer: "+e);
        }
        super.onReload();
    }

    public void startCountdown() {
        if(initialCountdown == 0) {
            log("Countdown not started because countdown is 0.");
            return;
        }
        log("Starting countdown...");
        taskTimer = hitBox.getServer().getScheduler().runTaskTimer(hitBox, () -> {
            if (broadcastCountdown.contains(countdown)) {
                hitBox.getPlayers().forEach(ep -> ep.sendMessage(
                        translateHexCodes(setPlaceholders(ep.getPlayer(), broadcastMessage.replace("%TIME%", Utils.getRemainingTimeInMinutes(countdown))))));
            }
            if (countdown <= 0) {
                countdown = initialCountdown;
                long removedItems = 0;
                for (World world : Bukkit.getWorlds()) {
                    final List<Entity> items = world.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.DROPPED_ITEM)).toList();
                    for (Entity item : items) {
                        item.remove();
                        removedItems++;
                    }
                }

                for (HitBoxPlayer player : hitBox.getPlayers()) {
                    player.sendMessage(translateHexCodes(setPlaceholders(player.getPlayer(), doneMessage.replace("%ITEMS%", String.valueOf(removedItems)))));
                }
                log("Removed "+removedItems+" items.");
                return;
            }
            countdown--;


        }, 0, 20);
        log("Started countdown.");
    }


    @Override
    public void createFile() {
        if(!file.exists()) {
            cfg.set("enabled", false);
            cfg.set("countdown", 900);
            cfg.set("broadcast.message", "Todos los elementos que se encuentren en el suelo se eliminarán en&e %TIME%&7.");
            cfg.set("broadcast.done_message", "Se han eliminado&e %ITEMS%&7 elementos.");
            cfg.set("broadcast.countdown", List.of(5*60, 3*60, 2*60, 60, 30, 15, 10, 5, 4, 3, 2, 1, 0));
            cfg.set("broadcast.sound", Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM.name());
            save();
        }
    }

    @Override
    public void readFile() {
        broadcastCountdown.clear();

        enabled = cfg.getBoolean("enabled", false);
        countdown = cfg.getInt("countdown");
        initialCountdown = countdown;

        broadcastMessage = cfg.getString("broadcast.message");
        doneMessage = cfg.getString("broadcast.done_message");

        broadcastCountdown.addAll(cfg.getIntegerList("broadcast.countdown"));

        final String soundName = cfg.getString("broadcast.sound");
        if(soundName == null) {
            broadcastSound = null;
            return;
        }
        try {
            broadcastSound = Sound.valueOf(soundName.toUpperCase());
        } catch (Exception e) {
            log(Level.SEVERE, "Could not find sound: "+soundName);
        }
    }
}
