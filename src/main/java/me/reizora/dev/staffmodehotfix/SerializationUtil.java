package me.reizora.dev.staffmodehotfix;

import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Base64;
import java.util.Map;

public class SerializationUtil {

    public static String itemStackArrayToBase64(ItemStack[] items) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(serializeItemStackArray(items));
        }
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return deserializeItemStackArray((String[]) objectInputStream.readObject());
        }
    }

    private static String[] serializeItemStackArray(ItemStack[] items) {
        String[] serializedItems = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            serializedItems[i] = items[i] == null ? "null" : itemStackToBase64(items[i]);
        }
        return serializedItems;
    }

    private static ItemStack[] deserializeItemStackArray(String[] serializedItems) {
        ItemStack[] items = new ItemStack[serializedItems.length];
        for (int i = 0; i < serializedItems.length; i++) {
            items[i] = "null".equals(serializedItems[i]) ? null : itemStackFromBase64(serializedItems[i]);
        }
        return items;
    }

    public static String itemStackToBase64(ItemStack item) {
        if (item == null) return "null";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream dataOutput = new ObjectOutputStream(outputStream)) {
            Map<String, Object> serialized = item.serialize();
            dataOutput.writeObject(serialized);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static ItemStack itemStackFromBase64(String data) {
        if (data.equals("null")) return null;
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try (ObjectInputStream dataInput = new ObjectInputStream(inputStream)) {
            Map<String, Object> serialized = (Map<String, Object>) dataInput.readObject();
            return ItemStack.deserialize(serialized);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}