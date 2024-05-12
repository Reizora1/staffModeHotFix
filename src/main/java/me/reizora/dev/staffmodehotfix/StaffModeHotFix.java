package me.reizora.dev.staffmodehotfix;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;

public final class StaffModeHotFix extends JavaPlugin implements  Listener, CommandExecutor {
    private final HashMap<Player, ItemStack[]> savedInventoryData = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("Hotfix enabled!");
        System.out.println("THIS PLUGIN WORKS IN CONJUNCTION WITH THE STAFFMODE PLUGIN, DO NOT DELETE!");
    }
    /*@Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("staffmode.staff")) {
                clearThenRestoreItems(player);
            }
        }
    }*/

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("staffmode")) {
                ItemStack[] inventoryData = player.getInventory().getContents();
                savedInventoryData.put(player, inventoryData);
            }
        }
        return true;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("staffmode.staff") && player.getGameMode() == GameMode.CREATIVE) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("staffmode.staff")) {
            restoreItems(player);
        }
    }

    public void restoreItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.setContents(savedInventoryData.get(player));
        player.updateInventory();
    }
}
