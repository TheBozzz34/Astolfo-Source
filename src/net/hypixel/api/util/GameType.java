package net.hypixel.api.util;

public enum GameType {
   QUAKECRAFT("Quakecraft", "Quake", 2),
   WALLS("Walls", "Walls", 3),
   PAINTBALL("Paintball", "Paintball", 4),
   SURVIVAL_GAMES("Blitz Survival Games", "HungerGames", 5),
   TNTGAMES("The TNT Games", "TNTGames", 6),
   VAMPIREZ("VampireZ", "VampireZ", 7),
   WALLS3("Mega Walls", "Walls3", 13),
   ARCADE("Arcade", "Arcade", 14),
   ARENA("Arena Brawl", "Arena", 17),
   MCGO("Cops and Crims", "MCGO", 21),
   UHC("UHC Champions", "UHC", 20),
   BATTLEGROUND("Warlords", "Battleground", 23),
   SUPER_SMASH("Smash Heroes", "SuperSmash", 24),
   GINGERBREAD("Turbo Kart Racers", "GingerBread", 25),
   HOUSING("Housing", "Housing", 26),
   SKYWARS("SkyWars", "SkyWars", 51),
   TRUE_COMBAT("Crazy Walls", "TrueCombat", 52),
   SPEED_UHC("Speed UHC", "SpeedUHC", 54),
   SKYCLASH("SkyClash", "SkyClash", 55),
   LEGACY("Classic Games", "Legacy", 56),
   PROTOTYPE("Prototype", "Prototype", 57),
   BEDWARS("Bed Wars", "Bedwars", 58),
   MURDER_MYSTERY("Murder Mystery", "MurderMystery", 59),
   BUILD_BATTLE("Build Battle", "BuildBattle", 60),
   DUELS("Duels", "Duels", 61),
   SKYBLOCK("SkyBlock", "SkyBlock", 63),
   PIT("Pit", "Pit", 64);

   private static final GameType[] VALUES = values();
   private final String name;
   private final String dbName;
   private final int id;
   private static final long a = 31052646913358L;

   GameType(String var3, String var4, int var5) {
      this.name = var3;
      this.dbName = var4;
      this.id = var5;
   }

   public static GameType fromId(int var0) {
      for (GameType var7 : VALUES) {
         if (var7.id == var0) {
            return var7;
         }
      }

      return null;
   }

   public static GameType fromDatabase(String var0) {
      for (GameType var7 : VALUES) {
         if (var7.dbName.equals(var0)) {
            return var7;
         }
      }

      return null;
   }

   public static GameType[] g_GameTypeArray() {
      return VALUES;
   }

   public String g() {
      return this.name;
   }

   public int getId() {
      return this.id;
   }

   public String t() {
      return this.dbName;
   }
}
