package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlock;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlockExecuteCommand;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlockLocation;
import de.netzkronehd.hitboxutils.paper.listener.ClickBlockListener;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;
import java.util.logging.Level;

@Getter
public class ClickBlockManager extends Manager {

    private final Map<ClickBlockLocation, ClickBlock> clickBlocks;
    private final ClickBlockListener clickBlockListener;
    private boolean activated;

    public ClickBlockManager(HitBoxUtils hitBox) {
        super(hitBox);
        this.clickBlocks = new HashMap<>();
        this.clickBlockListener = new ClickBlockListener(hitBox);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("activated", false);
            save();
        }
    }

    @Override
    public void readFile() {
        this.clickBlocks.clear();
        if (this.activated) {
            PlayerInteractEvent.getHandlerList().unregister(clickBlockListener);
        }
        this.activated = cfg.getBoolean("activated", false);
        if (this.activated) {
            hitBox.getServer().getPluginManager().registerEvents(clickBlockListener, hitBox);
        }
        for (String key : cfg.getKeys(false)) {
            if(key.equalsIgnoreCase("activated")) continue;
            final int id = Integer.parseInt(key);
            final int x = Integer.parseInt(cfg.getString(id+".location.x"));
            final int y = Integer.parseInt(cfg.getString(id+".location.y"));
            final int z = Integer.parseInt(cfg.getString(id+".location.z"));
            final World world = hitBox.getServer().getWorld(cfg.getString(id+".location.world"));
            if (world == null) {
                log(Level.WARNING, "Cloud not load ClickBlock '"+key+"' because world '"+cfg.getString(id+".location.world")+"' does not exists.");
                continue;
            }

            final ClickBlock clickBlock = ClickBlock.builder()
                    .id(id)
                    .location(new Location(world, x, y, z))
                    .build();

            final List<ClickBlockExecuteCommand> commandList = (List<ClickBlockExecuteCommand>) cfg.getList(clickBlock.getId() + ".commands", new ArrayList<ClickBlockExecuteCommand>());

            clickBlock.getCommands().addAll(commandList);

            cfg.getStringList(clickBlock.getId()+".players").forEach(s -> clickBlock.getPlayers().add(UUID.fromString(s)));


            this.clickBlocks.put(ClickBlockLocation.adapt(clickBlock.getLocation()), clickBlock);
            log("Loaded ClockBlock '"+key+"'with the command '"+clickBlock.getCommands()+"'.");
        }

    }

    public int getNewId() {
        int id = 0;
        while (cfg.getString(id + ".location.x") != null) {
            id++;
        }
        return id;
    }

    public void deleteBlock(ClickBlock clickBlock) {
        deleteBlock(clickBlock.getId());
    }

    public void deleteBlock(int id) {
        cfg.set(String.valueOf(id), null);
        this.clickBlocks.entrySet().removeIf(entry -> entry.getValue().getId() == id);
        save();
    }

    public ClickBlock getBlock(int id) {
        return this.clickBlocks.values().stream()
                .filter(clickBlock -> clickBlock.getId() == id)
                .findFirst().orElse(null);
    }

    public void saveClickBlock(ClickBlock clickBlock) {
        cfg.set(clickBlock.getId()+".location.x", clickBlock.getLocation().getBlockX());
        cfg.set(clickBlock.getId()+".location.y", clickBlock.getLocation().getBlockY());
        cfg.set(clickBlock.getId()+".location.z", clickBlock.getLocation().getBlockZ());
        cfg.set(clickBlock.getId()+".location.world", clickBlock.getLocation().getWorld().getName());
        cfg.set(clickBlock.getId()+".commands", clickBlock.getCommands());

        final List<String> uuidStringList = new ArrayList<>();
        for (UUID uuid : clickBlock.getPlayers()) {
            uuidStringList.add(uuid.toString());
        }

        cfg.set(clickBlock.getId()+".players", uuidStringList);
        this.clickBlocks.put(clickBlock.getClickBlockLocation(), clickBlock);
        save();

    }

}
