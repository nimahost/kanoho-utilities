package net.nimajnebec.kanoho.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.nimajnebec.kanoho.Kanoho;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class UseHelper implements Listener {
    public static final String TAG_USED = "used";
    private final Kanoho plugin;

    public UseHelper(Kanoho plugin) {
        this.plugin = plugin;
    }

    public boolean isUsable(CompoundTag nbt) {
        return nbt.contains(TAG_USED);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerUse(PlayerInteractEvent event) {
        // TODO: Add checks to ensure only one item is used at a time
        org.bukkit.entity.Player player = event.getPlayer();

        if (event.getItem() == null) return;
        ItemStack item = ((CraftItemStack) event.getItem()).handle;
        CompoundTag nbt = item.getOrCreateTag();

        if (isUsable(nbt)) {
            nbt.putBoolean(TAG_USED, true);
            item.setTag(nbt);

            // Clear used tag after next tick
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                nbt.putBoolean(TAG_USED, false);
                item.setTag(nbt);
            }, 2);
        }
    }

}
