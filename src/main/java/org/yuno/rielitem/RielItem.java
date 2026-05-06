package org.yuno.rielitem;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.yuno.rielitem.commands.LoyaltyCommand;
import org.yuno.rielitem.listeners.RielListener;
import org.yuno.rielitem.managers.RielManager;

public class RielItem extends JavaPlugin {

    private RielManager rielManager;
    private Economy economy;
    private boolean vaultEnabled = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.rielManager = new RielManager(this);
        setupVault();
        LoyaltyCommand loyaltyCommand = new LoyaltyCommand(this);
        getCommand("loyalty").setExecutor(loyaltyCommand);
        getCommand("loyalty").setTabCompleter(loyaltyCommand);
        getServer().getPluginManager().registerEvents(new RielListener(this), this);
        getLogger().info("RielItem enabled! /loyalty | LoyaltyMC");
    }

    @Override
    public void onDisable() {
        getLogger().info("RielItem disabled.");
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found!");
            return;
        }
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;
        economy = rsp.getProvider();
        vaultEnabled = true;
        getLogger().info("Vault hooked!");
    }

    public RielManager getRielManager() { return rielManager; }
    public Economy getEconomy() { return economy; }
    public boolean hasEconomy() { return vaultEnabled && economy != null; }
}
