package net.nimajnebec.kanoho.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.nimajnebec.kanoho.Kanoho;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

public class UseHelper implements Listener {
    public static final String TAG_USED = "used";
    private final Kanoho plugin;

    public UseHelper(Kanoho plugin) {
        this.plugin = plugin;
    }

    public boolean isUsable(org.bukkit.inventory.ItemStack item) {
        if (item.getType() == Material.AIR) return false;
        return isUsable(((CraftItemStack) item).handle.getOrCreateTag());
    }

    public boolean isUsable(CompoundTag nbt) {
        return nbt.contains(TAG_USED);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;

        // TODO: Do not use if clicking on interactive block

        // Cancel use of offhand item if mainhand is usable
        PlayerInventory inventory = event.getPlayer().getInventory();
        if (event.getHand() == EquipmentSlot.OFF_HAND && isUsable(inventory.getItem(EquipmentSlot.HAND))) {
            event.setCancelled(true);
            return;
        }

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
