package me.reizora.dev.staffmodehotfix;

import org.bukkit.inventory.ItemStack;
import java.io.*;
import java.util.Base64;

public class SerializationUtil {
    public static String itemStackArrayToBase64(ItemStack[] items) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(items);
        }
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (ItemStack[]) objectInputStream.readObject();
        }
    }
}
