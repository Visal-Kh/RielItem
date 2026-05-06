package org.yuno.rielitem.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yuno.rielitem.RielItem;
import org.yuno.rielitem.managers.RielManager;

import java.util.*;
import java.util.stream.Collectors;

public class LoyaltyCommand implements CommandExecutor, TabCompleter {

    private static final List<String> AMOUNTS = Arrays.asList(
        "100","500","1000","5000","10000","50000","100000"
    );
    private static final List<String> SUB_COMMANDS = Arrays.asList(
        "give","give-all","withdraw","balance","list","reload"
    );

    private final RielItem plugin;

    public LoyaltyCommand(RielItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!hasAdmin(sender)) { noPerms(sender); return true; }
            if (args.length == 0) { sendHelp(sender); return true; }
            switch (args[0].toLowerCase()) {
                case "give"     -> handleGive(sender, args);
                case "give-all" -> handleGiveAll(sender, args);
                case "withdraw" -> handleWithdraw(sender, args);
                case "balance"  -> handleBalance(sender);
                case "list"     -> handleList(sender);
                case "reload"   -> handleReload(sender);
                default         -> sendHelp(sender);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("LoyaltyCommand error: " + e.getMessage());
        }
        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) { send(sender, "Usage: /loyalty give <player> <amount> [qty]", 0xFFAA00); return; }
        Player target = plugin.getServer().getPlayerExact(args[1]);
        if (target == null) { send(sender, "❌ Player not found: " + args[1], 0xFF5555); return; }
        int amount;
        try { amount = Integer.parseInt(args[2]); }
        catch (NumberFormatException e) { send(sender, "❌ Invalid amount!", 0xFF5555); return; }
        if (!plugin.getRielManager().getValidAmounts().contains(amount)) {
            send(sender, "❌ Invalid denomination! Use: 100,500,1000,5000,10000,50000,100000", 0xFF5555); return;
        }
        int qty = 1;
        if (args.length >= 4) { try { qty = Math.max(1, Integer.parseInt(args[3])); } catch (Exception ignored) {} }
        ItemStack item = plugin.getRielManager().createRielItem(amount, qty);
        if (item == null) { send(sender, "❌ Failed to create item!", 0xFF5555); return; }
        target.getInventory().addItem(item);
        send(sender, "✅ ផ្តល់ " + plugin.getRielManager().formatAmount(amount) + " ៛ ទៅ " + target.getName() + "!", 0x55FF55);
        target.getWorld().playSound(target.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
    }

    private void handleGiveAll(CommandSender sender, String[] args) {
        if (args.length < 2) { send(sender, "Usage: /loyalty give-all <amount> [qty]", 0xFFAA00); return; }
        int amount;
        try { amount = Integer.parseInt(args[1]); }
        catch (NumberFormatException e) { send(sender, "❌ Invalid amount!", 0xFF5555); return; }
        if (!plugin.getRielManager().getValidAmounts().contains(amount)) {
            send(sender, "❌ Invalid denomination!", 0xFF5555); return;
        }
        int qty = 1;
        if (args.length >= 3) { try { qty = Math.max(1, Integer.parseInt(args[2])); } catch (Exception ignored) {} }
        final int fQty = qty;
        int count = 0;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            ItemStack item = plugin.getRielManager().createRielItem(amount, fQty);
            if (item != null) { p.getInventory().addItem(item); count++; }
        }
        send(sender, "✅ ផ្តល់ " + plugin.getRielManager().formatAmount(amount) + " ៛ ទៅ " + count + " players!", 0x55FF55);
    }

    private void handleWithdraw(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) { send(sender, "❌ Only players!", 0xFF5555); return; }
        if (args.length < 2) { send(sender, "Usage: /loyalty withdraw <amount>", 0xFFAA00); return; }
        if (!plugin.hasEconomy()) { send(sender, "❌ Vault not installed!", 0xFF5555); return; }
        int amount;
        try { amount = Integer.parseInt(args[1]); }
        catch (NumberFormatException e) { send(sender, "❌ Invalid amount!", 0xFF5555); return; }
        if (!plugin.getRielManager().getValidAmounts().contains(amount)) {
            send(sender, "❌ Invalid denomination!", 0xFF5555); return;
        }
        double balance = plugin.getEconomy().getBalance(player);
        if (balance < amount) {
            send(sender, "❌ Balance " + plugin.getRielManager().formatAmount(balance) + " ៛ មិនគ្រប់គ្រាន់!", 0xFF5555); return;
        }
        net.milkbowl.vault.economy.EconomyResponse resp = plugin.getEconomy().withdrawPlayer(player, amount);
        if (!resp.transactionSuccess()) { send(sender, "❌ Transaction failed!", 0xFF5555); return; }
        ItemStack item = plugin.getRielManager().createRielItem(amount, 1);
        if (item == null) { plugin.getEconomy().depositPlayer(player, amount); send(sender, "❌ Failed!", 0xFF5555); return; }
        player.getInventory().addItem(item);
        send(sender, "✅ ដក " + plugin.getRielManager().formatAmount(amount) + " ៛ ចេញពី Balance!", 0x55FF55);
    }

    private void handleBalance(CommandSender sender) {
        if (!(sender instanceof Player player)) { send(sender, "❌ Only players!", 0xFF5555); return; }
        if (!plugin.hasEconomy()) { send(sender, "❌ Vault not installed!", 0xFF5555); return; }
        double bal = plugin.getEconomy().getBalance(player);
        send(sender, "💰 Balance: " + plugin.getRielManager().formatAmount(bal) + " ៛", 0xFFAA00);
    }

    private void handleList(CommandSender sender) {
        send(sender, "─── RielItem Denominations ─── LoyaltyMC ───", 0xFFD700);
        for (Map.Entry<Integer, RielManager.DenominationData> e : RielManager.DENOMINATIONS.entrySet()) {
            sender.sendMessage(Component.text("  » " + e.getValue().displayName).color(TextColor.color(e.getValue().color)));
        }
    }

    private void handleReload(CommandSender sender) {
        try { plugin.reloadConfig(); send(sender, "✅ Config reloaded!", 0x55FF55); }
        catch (Exception e) { send(sender, "❌ Reload error: " + e.getMessage(), 0xFF5555); }
    }

    private void sendHelp(CommandSender sender) {
        send(sender, "─── /loyalty commands ───", 0xFFD700);
        send(sender, "/loyalty give <player> <amount> [qty]", 0xAAAAAA);
        send(sender, "/loyalty give-all <amount> [qty]", 0xAAAAAA);
        send(sender, "/loyalty withdraw <amount>", 0xAAAAAA);
        send(sender, "/loyalty balance", 0xAAAAAA);
        send(sender, "/loyalty list", 0xAAAAAA);
        send(sender, "/loyalty reload", 0xAAAAAA);
    }

    private boolean hasAdmin(CommandSender s) { return s.isOp() || s.hasPermission("rielitem.admin"); }
    private void noPerms(CommandSender s) { send(s, "❌ No permission!", 0xFF5555); }
    private void send(CommandSender s, String msg, int hex) {
        s.sendMessage(Component.text(msg).color(TextColor.color(hex)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!hasAdmin(sender)) return Collections.emptyList();
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.addAll(SUB_COMMANDS);
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give"))
                plugin.getServer().getOnlinePlayers().forEach(p -> list.add(p.getName()));
            else if (args[0].equalsIgnoreCase("give-all") || args[0].equalsIgnoreCase("withdraw"))
                list.addAll(AMOUNTS);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) list.addAll(AMOUNTS);
        String typed = args[args.length - 1].toLowerCase();
        return list.stream().filter(c -> c.toLowerCase().startsWith(typed)).collect(Collectors.toList());
    }
             }
