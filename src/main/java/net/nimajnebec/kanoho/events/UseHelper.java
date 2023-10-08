package net.nimajnebec.kanoho.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.nimajnebec.kanoho.Kanoho;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UseHelper implements Listener {
    public static final String TAG_USED = "used";
    private final Map<UUID, ItemStack> possibleInteractions = new HashMap<>();
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

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;

        UUID uuid = event.getPlayer().getUniqueId();

        // Cancel use of offhand item if mainhand is usable
        PlayerInventory inventory = event.getPlayer().getInventory();
        if (event.getHand() == EquipmentSlot.OFF_HAND && isUsable(inventory.getItem(EquipmentSlot.HAND))) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = ((CraftItemStack) event.getItem()).handle;
        CompoundTag nbt = item.getOrCreateTag();

        if (isUsable(nbt)) {
            possibleInteractions.put(uuid, item);

            // Clean up after next tick
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                nbt.putBoolean(TAG_USED, false);
                item.setTag(nbt);

                possibleInteractions.remove(uuid);
            }, 2);
        }
    }

    @EventHandler
    public void onStatIncrease(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic() == Statistic.USE_ITEM && event.getMaterial() == Material.CARROT_ON_A_STICK) {
            @Nullable ItemStack item = possibleInteractions.get(event.getPlayer().getUniqueId());
            if (item == null) return;

            // Set used nbt tag
            CompoundTag nbt = item.getOrCreateTag();
            nbt.putBoolean(TAG_USED, true);
            item.setTag(nbt);
        }
    }

}
