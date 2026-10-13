package net.minecraft.realms;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.world.WorldSettings;

public class Realms {
   private static final long a = 92354507150274L;

   public static boolean isTouchScreen() {
      return Minecraft.getMinecraft().gameSettings.touchscreen;
   }

   public static Proxy getProxy() {
      return Minecraft.getMinecraft().getProxy();
   }

   public static String S() {
      Session var3 = Minecraft.getMinecraft().getSession();
      return var3 == null ? null : var3.getSessionID();
   }

   public static String R() {
      Session var3 = Minecraft.getMinecraft().getSession();
      return var3 == null ? null : var3.getUsername();
   }

   public static long currentTimeMillis() {
      return Minecraft.getSystemTime();
   }

   public static String I() {
      return Minecraft.getMinecraft().getSession().getSessionID();
   }

   public static String T() {
      return Minecraft.getMinecraft().getSession().getPlayerID();
   }

   public static String l() {
      return Minecraft.getMinecraft().getSession().getUsername();
   }

   public static String uuidToName(String var0) {
      return Minecraft.getMinecraft()
         .getSessionService()
         .fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(var0), (String)null), false)
         .getName();
   }

   public static void setScreen(RealmsScreen var0) {
      Minecraft.getMinecraft().displayGuiScreen(var0.getProxy());
   }

   public static String getGameDirectoryPath() {
      return Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
   }

   public static int z() {
      return WorldSettings.GameType.SURVIVAL.getID();
   }

   public static int h() {
      return WorldSettings.GameType.CREATIVE.getID();
   }

   public static int Y() {
      return WorldSettings.GameType.ADVENTURE.getID();
   }

   public static int O() {
      return WorldSettings.GameType.SPECTATOR.getID();
   }

   public static void setConnectedToRealms(boolean var0) {
      Minecraft.getMinecraft().setConnectedToRealms(var0);
   }

   public static ListenableFuture<Object> downloadResourcePack(String var0, String var1) {
      return Minecraft.getMinecraft().getResourcePackRepository().downloadResourcePack(var0, var1);
   }

   public static void clearResourcePack() {
      Minecraft.getMinecraft().getResourcePackRepository().clearResourcePack();
   }
}
