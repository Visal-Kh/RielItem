package org.yuno.rielitem.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.yuno.rielitem.RielItem;

public class RielListener implements Listener {

    private final RielItem plugin;

    public RielListener(RielItem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            if (event.getHand() != EquipmentSlot.HAND) return;
            var action = event.getAction();
            if (action != org.bukkit.event.block.Action.RIGHT_CLICK_AIR
             && action != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR || !item.hasItemMeta()) return;
            if (!plugin.getRielManager().isRielItem(item)) return;

            event.setCancelled(true);

            int amountPerItem = plugin.getRielManager().getRielAmount(item);
            int qty = item.getAmount();
            double total = (double) amountPerItem * qty;

            if (!plugin.hasEconomy()) {
                player.sendMessage(Component.text("❌ Vault not installed!").color(TextColor.color(0xFF5555)));
                return;
            }

            try {
                plugin.getEconomy().depositPlayer(player, total);
            } catch (Exception e) {
                plugin.getLogger().warning("Economy deposit error: " + e.getMessage());
                player.sendMessage(Component.text("Economy error!").color(TextColor.color(0xFF5555)));
                return;
            }

            item.setAmount(0);

            String formatted = plugin.getRielManager().formatAmount(total);
            String msg = plugin.getConfig().getString("messages.collected", "បានទទួល {amount} ៛ ចូល Balance!")
                    .replace("{amount}", formatted);
            player.sendActionBar(Component.text(msg).color(TextColor.color(0x55FF55)));

            try {
                Sound sound = Sound.valueOf(plugin.getConfig().getString("settings.collect-sound", "ENTITY_EXPERIENCE_ORB_PICKUP"));
                player.getWorld().playSound(player.getLocation(), sound, 1f, 1f);
            } catch (Exception ignored) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            }

            if (plugin.getConfig().getBoolean("settings.collect-particle", true)) {
                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,
                        player.getLocation().clone().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.1);
            }

        } catch (Exception e) {
            plugin.getLogger().severe("RielListener error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
