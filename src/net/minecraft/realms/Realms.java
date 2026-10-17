package net.minecraft.realms;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.world.WorldSettings;

public class Realms {
   public static boolean isTouchScreen() {
      return Minecraft.getMinecraft().gameSettings.touchscreen;
   }

   public static Proxy getProxy() {
      return Minecraft.getMinecraft().getProxy();
   }

   public static String sessionId() {
      Session session = Minecraft.getMinecraft().getSession();
      return session == null ? null : session.getSessionID();
   }

   public static String userName() {
      Session session = Minecraft.getMinecraft().getSession();
      return session == null ? null : session.getUsername();
   }

   public static long currentTimeMillis() {
      return Minecraft.getSystemTime();
   }

   public static String getSessionId() {
      return Minecraft.getMinecraft().getSession().getSessionID();
   }

   public static String getUUID() {
      return Minecraft.getMinecraft().getSession().getPlayerID();
   }

   public static String getName() {
      return Minecraft.getMinecraft().getSession().getUsername();
   }

   public static String uuidToName(String string) {
      return Minecraft.getMinecraft()
         .getSessionService()
         .fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), (String)null), false)
         .getName();
   }

   public static void setScreen(RealmsScreen realmsScreen) {
      Minecraft.getMinecraft().displayGuiScreen(realmsScreen.getProxy());
   }

   public static String getGameDirectoryPath() {
      return Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
   }

   public static int z() {
      return WorldSettings.GameType.SURVIVAL.getID();
   }

   public static int creativeId() {
      return WorldSettings.GameType.CREATIVE.getID();
   }

   public static int adventureId() {
      return WorldSettings.GameType.ADVENTURE.getID();
   }

   public static int spectatorId() {
      return WorldSettings.GameType.SPECTATOR.getID();
   }

   public static void setConnectedToRealms(boolean bl) {
      Minecraft.getMinecraft().setConnectedToRealms(bl);
   }

   public static ListenableFuture<Object> downloadResourcePack(String string, String string2) {
      return Minecraft.getMinecraft().getResourcePackRepository().downloadResourcePack(string, string2);
   }

   public static void clearResourcePack() {
      Minecraft.getMinecraft().getResourcePackRepository().clearResourcePack();
   }
}
