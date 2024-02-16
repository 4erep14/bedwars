package com.test.bedwars.scoreboards;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtils {
    public static void sendTitle(Player player, String message, int fadeIn, int stayTime, int fadeOut) {
        try {
            Object titlePacket = createTitlePacket(message, fadeIn, stayTime, fadeOut);
            sendPacket(player, titlePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object createTitlePacket(String message, int fadeIn, int stayTime, int fadeOut) throws Exception {
        // Get NMS classes
        Class<?> chatComponentClass = getNMSClass("IChatBaseComponent");
        Class<?> chatSerializerClass = getNMSClass("IChatBaseComponent$ChatSerializer");
        Class<?> packetPlayOutTitleClass = getNMSClass("PacketPlayOutTitle");
        Class<?> enumTitleActionClass = getNMSClass("PacketPlayOutTitle$EnumTitleAction");

        // Create the title components
        Object serializedMessage = chatSerializerClass.getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
        Object titleAction = enumTitleActionClass.getField("TITLE").get(null);
        Object titlePacket = packetPlayOutTitleClass.getConstructor(enumTitleActionClass, chatComponentClass, int.class, int.class, int.class)
                .newInstance(titleAction, serializedMessage, fadeIn, stayTime, fadeOut);

        return titlePacket;
    }

    private static void sendPacket(Player player, Object packet) throws Exception {
        Object handle = ((CraftPlayer) player).getHandle();
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    }

    private static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server.v1_8_R3." + name);
    }

}
