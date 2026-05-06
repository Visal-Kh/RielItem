package org.yuno.rielitem.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.yuno.rielitem.RielItem;

import java.util.*;

public class RielManager {

    private final RielItem plugin;
    public static final String NBT_KEY_AMOUNT = "riel_amount";
    public static final String NBT_KEY_TYPE   = "riel_item";
    public static final Map<Integer, DenominationData> DENOMINATIONS = new LinkedHashMap<>();

    static {
        DENOMINATIONS.put(100,    new DenominationData("100 ៛",    0xF5CBA7, 1));
        DENOMINATIONS.put(500,    new DenominationData("500 ៛",    0x82E0AA, 2));
        DENOMINATIONS.put(1000,   new DenominationData("1,000 ៛",  0x85C1E9, 3));
        DENOMINATIONS.put(5000,   new DenominationData("5,000 ៛",  0xF8C471, 4));
        DENOMINATIONS.put(10000,  new DenominationData("10,000 ៛", 0xF1948A, 5));
        DENOMINATIONS.put(50000,  new DenominationData("50,000 ៛", 0xBB8FCE, 6));
        DENOMINATIONS.put(100000, new DenominationData("100,000 ៛",0xF7DC6F, 7));
    }

    public RielManager(RielItem plugin) {
        this.plugin = plugin;
    }

    public ItemStack createRielItem(int amount, int qty) {
        DenominationData data = DENOMINATIONS.get(amount);
        if (data == null) return null;
        ItemStack item = new ItemStack(Material.PAPER, Math.max(1, qty));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.displayName(Component.text(data.displayName).color(TextColor.color(data.color)));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(" Right-click to collect balance").color(TextColor.color(0xAAAAAA)));
        lore.add(Component.text(" LoyaltyMC").color(TextColor.color(0xFFD700)));
        meta.lore(lore);
        meta.setCustomModelData(data.customModelData);
        NamespacedKey keyAmount = new NamespacedKey(plugin, NBT_KEY_AMOUNT);
        NamespacedKey keyType   = new NamespacedKey(plugin, NBT_KEY_TYPE);
        meta.getPersistentDataContainer().set(keyAmount, PersistentDataType.INTEGER, amount);
        meta.getPersistentDataContainer().set(keyType,   PersistentDataType.STRING,  "riel");
        item.setItemMeta(meta);
        return item;
    }

    public int getRielAmount(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return -1;
        ItemMeta meta = item.getItemMeta();
        NamespacedKey keyType = new NamespacedKey(plugin, NBT_KEY_TYPE);
        if (!meta.getPersistentDataContainer().has(keyType, PersistentDataType.STRING)) return -1;
        String type = meta.getPersistentDataContainer().get(keyType, PersistentDataType.STRING);
        if (!"riel".equals(type)) return -1;
        NamespacedKey keyAmount = new NamespacedKey(plugin, NBT_KEY_AMOUNT);
        Integer val = meta.getPersistentDataContainer().get(keyAmount, PersistentDataType.INTEGER);
        return val != null ? val : -1;
    }

    public boolean isRielItem(ItemStack item) { return getRielAmount(item) > 0; }
    public Set<Integer> getValidAmounts() { return DENOMINATIONS.keySet(); }

    public String formatAmount(double amount) {
        if (amount >= 1_000_000) return String.format("%.1fM", amount / 1_000_000);
        else if (amount >= 1_000) return String.format("%,.0f", amount);
        else return String.format("%.0f", amount);
    }

    public static class DenominationData {
        public final String displayName;
        public final int color;
        public final int customModelData;
        public DenominationData(String displayName, int color, int customModelData) {
            this.displayName = displayName;
            this.color = color;
            this.customModelData = customModelData;
        }
    }
}
