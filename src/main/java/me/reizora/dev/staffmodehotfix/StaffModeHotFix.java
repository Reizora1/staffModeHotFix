package me.reizora.dev.staffmodehotfix;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public final class StaffModeHotFix extends JavaPlugin implements Listener{
    private HashMap<UUID, String> savedInventoryData = new HashMap<>(); //Hashmap to store player inventory data in case of a server crash. Hashmap data will be saved into a .ser file as local backup.

    @Override
    public void onEnable() {
        try {
            File dataFolder = new File(getDataFolder(), "README.yml"); //Create plugin directory and a README.yml file.
            if (!dataFolder.exists()) {
                dataFolder.getParentFile().mkdirs();
                dataFolder.createNewFile();
            }
            FileWriter writeReadMe = new FileWriter(dataFolder);
            writeReadMe.write("DO NOT DELETE THIS DIRECTORY AND THE STAFFMODEHOTFIX JAR!\n\n");
            writeReadMe.write("THIS PLUGIN IS CRUCIAL FOR THE STAFFMODE PLUGIN.\n");
            writeReadMe.write("DELETING THIS PLUGIN WILL RESULT TO ITEM LOSSES WHEN SERVER CRASHES WHILE IN STAFFMODE!\n\n\n\n\n\n\n");
            writeReadMe.write("Discord: reizora_");
            writeReadMe.close();
            System.out.println("README.txt created!");
        } catch (IOException e) {
            System.out.println("File creation error!");
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(this, this);
        loadHashMapFromFile(); //Load HashMap data from the .ser file into the plugin.
        System.out.println("STAFFMODE HOTFIX ENABLED!");
    }

    @Override
    public void onDisable() {
        //Not used.
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){ //listens for when a player with the "staffmode.staff" perms does a /staff or /staffmode command.
        Player player = event.getPlayer();
        String command = event.getMessage();
        if (!player.hasPermission("staffmode.staff")) {
            player.sendMessage(ChatColor.RED+ "You are not a staff!");
            System.out.println("Player " +player.getName()+ " is not a staff!");
        }
        else {
            if((command.equalsIgnoreCase("/staffmode") || command.equalsIgnoreCase("/staff")) && (player.getGameMode() != GameMode.CREATIVE)){
                try {
                    ItemStack[] inventoryData = player.getInventory().getContents();
                    String serializedData = SerializationUtil.itemStackArrayToBase64(inventoryData);
                    savedInventoryData.put(player.getUniqueId(), serializedData);
                    saveHashMapToFile();

                    System.out.println("Player " +player.getName()+ " has entered staffmode!");
                    System.out.println("Player " +player.getName()+"'s items before entering staffmode has been saved!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Player " +player.getName()+ "is leaving staffmode.");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("staffmode.staff") && player.getGameMode() == GameMode.CREATIVE) {
            player.setGameMode(GameMode.SURVIVAL);
            restoreItems(player);
        }
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
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(savedInventoryData);
                System.out.println("HashMap data saved to file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save HashMap data to file.");
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