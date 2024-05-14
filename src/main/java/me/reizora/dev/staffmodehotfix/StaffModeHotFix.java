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

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public final class StaffModeHotFix extends JavaPlugin implements Listener, CommandExecutor {
    private HashMap<UUID, String> savedInventoryData = new HashMap<>();

    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("staffmode").setExecutor(this);
        loadHashMapFromFile();

        System.out.println("HOTFIX ENABLED!");
        System.out.println("THIS PLUGIN WORKS IN CONJUNCTION WITH THE STAFFMODE PLUGIN, DO NOT DELETE!");
    }

    @Override
    public void onDisable() {
        saveHashMapToFile();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("staffmode")) {
                try {
                    ItemStack[] inventoryData = player.getInventory().getContents();
                    String serializedData = SerializationUtil.itemStackArrayToBase64(inventoryData);
                    savedInventoryData.put(player.getUniqueId(), serializedData);
                    System.out.println("Inventory data saved!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("staffmode.staff") && player.getGameMode() == GameMode.CREATIVE) {
            player.setGameMode(GameMode.SURVIVAL);
            saveHashMapToFile();
            restoreItems(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        //saveHashMapToFile();
    }

    public void restoreItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        String serializedData = savedInventoryData.get(player.getUniqueId());
        if (serializedData != null) {
            try {
                ItemStack[] savedInventory = SerializationUtil.itemStackArrayFromBase64(serializedData);
                inventory.setContents(savedInventory);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        player.updateInventory();
    }

    private void saveHashMapToFile() {
        try {
            File file = new File(getDataFolder(), "inventoryData.ser");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(savedInventoryData);
                System.out.println("HashMap saved to file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save HashMap to file.");
        }
    }

    private void loadHashMapFromFile() {
        try {
            File file = new File(getDataFolder(), "inventoryData.ser");
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    savedInventoryData = (HashMap<UUID, String>) ois.readObject();
                    System.out.println("HashMap loaded from file.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Failed to load HashMap from file.");
        }
    }
}