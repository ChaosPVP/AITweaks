package es.nkmem.da.optimizate;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AIUtils {
    private static Field fromMobSpawner;
    private static Method getHandle;
    private static String ver;

    public static boolean forceNoAi(LivingEntity entity) {
        if (ver == null) {
            String name = Bukkit.getServer().getClass().getName();
            String[] parts = name.split("\\.");
            ver = parts[3];
        }
        if (fromMobSpawner == null) {
            try {
                Class<?> nmsEntity = Class.forName("net.minecraft.server." + ver + ".Entity");
                fromMobSpawner = nmsEntity.getDeclaredField("fromMobSpawner");
                fromMobSpawner.setAccessible(true);
            } catch (ClassNotFoundException | NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
        if (getHandle == null) {
            try {
                Class<?> craftLivingEntity = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftLivingEntity");
                getHandle = craftLivingEntity.getDeclaredMethod("getHandle");
                getHandle.setAccessible(true);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        try {
            Object el = getHandle.invoke(entity);
            boolean curr = fromMobSpawner.getBoolean(el);
            if (!curr) {
                fromMobSpawner.setBoolean(el, true);
                return true;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
}
