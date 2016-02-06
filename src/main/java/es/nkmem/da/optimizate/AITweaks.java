package es.nkmem.da.optimizate;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AITweaks extends JavaPlugin implements Listener {
    private List<EntityType> allowedTypes = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        allowedTypes.addAll(getConfig().getStringList("allowed-types").stream()
                .map(allowed -> EntityType.valueOf(allowed.toUpperCase()))
                .collect(Collectors.toList()));
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().runTaskLater(this, () -> {
            int i = 0;
            for (World w : Bukkit.getWorlds()) {
                for (Entity e : w.getEntities()) {
                    if (e instanceof LivingEntity && updateAi((LivingEntity) e)) {
                        i++;
                    }
                }
            }
            getLogger().info("Disabled " + i + " entity AIs");
        }, 20);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity e : event.getChunk().getEntities()) {
            if (e instanceof LivingEntity) {
                updateAi((LivingEntity) e);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        updateAi(event.getEntity());
    }

    private boolean updateAi(LivingEntity le) {
        if (allowedTypes.contains(le.getType())) {
            return false;
        }
        if (le instanceof Animals || le instanceof Monster || le instanceof WaterMob) {
            return AIUtils.forceNoAi(le);
        }
        return false;
    }
}
