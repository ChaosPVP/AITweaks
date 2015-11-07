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

public class AITweaks extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
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
        if (le instanceof Animals || le instanceof Monster || le instanceof Squid) {
            return AIUtils.forceNoAi(le);
        }
        return false;
    }
}
