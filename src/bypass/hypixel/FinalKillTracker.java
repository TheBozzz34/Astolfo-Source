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

   public static void setGame(String string) {
      if (!Objects.equals(string, game)) {
         teamFinals.clear();
         playerFinals.clear();
         game = string;
      }
   }

   public static void K() {
   }

   public static HashMap<String, Integer> N(int j) {
      return new HashMap<>();
   }

   private static boolean d(String string) {
      return !ScoreboardTracker.N.O(string);
   }

   public static int getTeamFinals(int j) {
      Integer integer = teamFinals.get(j);
      return integer != null ? integer : 0;
   }

   public static boolean onChatMessage(String string, String string2) {
      if (ScoreboardTracker.r() && !ScoreboardTracker.N.K() && !ScoreboardTracker.N.G()) {
         if (string.contains("by ") && string.endsWith("§r")) {
            String[] strings = string.split("by ");
            System.out.println("Found kill");
            if (strings.length > 1) {
               String[] strings2 = string.split(" ");
               if (strings2.length >= 1) {
                  String string3 = strings2[0].replace("§r", "").split("'")[0];
                  String string4 = string3.substring(0, 2);
                  if (d(string4)) {
                     String string5 = strings[1].replace("§r", "");
                     String string6 = string5.substring(0, 2);
                     int i = teamIndex(string6);
                     if (i != -1) {
                        String string7 = string2.split("by ")[1];
                        String string8 = StringUtils.stripControlCodes(string3);
                        Integer integer = playerFinals.get(string8);
                        int j = playerFinals.containsKey(string7) ? playerFinals.get(string7) : 0;
                        int k = teamFinals.containsKey(i) ? teamFinals.get(i) : 0;
                        if (integer == null) {
                           playerFinals.put(string7, j + 1);
                           teamFinals.put(i, k + 1);
                           return true;
                        }

                        int l = teamIndex(string4);
                        if (l != -1) {
                           playerFinals.put(string7, j + integer + 1);
                           int m = teamFinals.containsKey(l) ? teamFinals.get(l) : 0;
                           teamFinals.put(l, Math.max(m - integer, 0));
                           teamFinals.put(i, k + integer + 1);
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

   private static int teamIndex(String string) {
      switch (string) {
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
