package bypass.hypixel;

import bypass.module.hidden.ScoreboardTracker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.util.StringUtils;

public class FinalKillTracker {
   public static int G = 4;
   public static int H = 0;
   public static int h = 1;
   public static int i = 2;
   public static int F = 3;
   public static ArrayList<String> c;
   private static String game;
   public static HashMap<Integer, Integer> teamFinals = new HashMap<>();
   public static HashMap<String, Integer> playerFinals = new HashMap<>();

   public static void setGame(String var0) {
      if (!Objects.equals(var0, game)) {
         teamFinals.clear();
         playerFinals.clear();
         game = var0;
      }
   }

   public static void K() {
   }

   public static HashMap<String, Integer> N(int var0) {
      return new HashMap<>();
   }

   private static boolean d(String var0) {
      return !ScoreboardTracker.N.O(var0);
   }

   public static int getTeamFinals(int var0) {
      Integer var1 = teamFinals.get(var0);
      return var1 != null ? var1 : 0;
   }

   public static boolean onChatMessage(String var0, String var1) {
      if (ScoreboardTracker.r() && !ScoreboardTracker.N.K() && !ScoreboardTracker.N.G()) {
         if (var0.contains("by ") && var0.endsWith("§r")) {
            String[] var2 = var0.split("by ");
            System.out.println("Found kill");
            if (var2.length > 1) {
               String[] var3 = var0.split(" ");
               if (var3.length >= 1) {
                  String var4 = var3[0].replace("§r", "").split("'")[0];
                  String var5 = var4.substring(0, 2);
                  if (d(var5)) {
                     String var6 = var2[1].replace("§r", "");
                     String var7 = var6.substring(0, 2);
                     int var8 = teamIndex(var7);
                     if (var8 != -1) {
                        String var9 = var1.split("by ")[1];
                        String var10 = StringUtils.stripControlCodes(var4);
                        Integer var11 = playerFinals.get(var10);
                        int var12 = playerFinals.containsKey(var9) ? playerFinals.get(var9) : 0;
                        int var13 = teamFinals.containsKey(var8) ? teamFinals.get(var8) : 0;
                        if (var11 == null) {
                           playerFinals.put(var9, var12 + 1);
                           teamFinals.put(var8, var13 + 1);
                           return true;
                        }

                        int var14 = teamIndex(var5);
                        if (var14 != -1) {
                           playerFinals.put(var9, var12 + var11 + 1);
                           int var15 = teamFinals.containsKey(var14) ? teamFinals.get(var14) : 0;
                           teamFinals.put(var14, Math.max(var15 - var11, 0));
                           teamFinals.put(var8, var13 + var11 + 1);
                           return true;
                        }
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static int teamIndex(String var0) {
      switch (var0) {
         case "§c":
            return 0;
         case "§a":
            return 1;
         case "§e":
            return 2;
         case "§9":
            return 3;
         default:
            return -1;
      }
   }
}
