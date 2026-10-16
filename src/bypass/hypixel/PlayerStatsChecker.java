package bypass.hypixel;

import bypass.config.ConfigFiles;
import bypass.module.exploits.Denicker;
import bypass.module.hidden.HypixelGameTracker;
import bypass.module.hidden.PlayerListTracker;
import bypass.module.other.AutoHypixel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.properties.Property;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hypixel.api.HypixelAPI;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PlayerStatsChecker {
   private HypixelAPI hypixelApi;
   public final JsonObject[] playerJson = new JsonObject[1];
   public final int[] fetchState = new int[1];
   public static final HashMap<String, PlayerStatsChecker.r95> unresolvedNicks = new HashMap<>();
   private final List<String> nickSkinOwners = Arrays.asList(
      "Pickguard",
      "Tactful",
      "DoubleDeltas",
      "Megakloon",
      "Parroteme",
      "DannoBanannoXD",
      "loyal",
      "2807",
      "MysticGamerMan",
      "roro___",
      "Ho_Bot",
      "Syleex",
      "Violetskyzz",
      "codename_B",
      "MHF_Mineskin",
      "08BED5"
   );

   public PlayerStatsChecker(HypixelAPI hypixelAPI) {
      this.hypixelApi = hypixelAPI;
      this.fetchState[0] = 0;
   }

   public void initialize(UUID uuid, String string, NetworkPlayerInfo networkPlayerInfo, boolean bl) {
      this.hypixelApi.getPlayerByUuid(uuid).whenComplete((playerReply, throwable) -> {
         if (PlayerListTracker.checkedPlayers.contains(string)) {
            this.fetchState[0] = -2;
         } else if (throwable != null) {
            PlayerListTracker.checkTimer.reset();
            throwable.printStackTrace();
            this.fetchState[0] = -2;
         } else {
            this.playerJson[0] = playerReply.getPlayer();
            if (this.playerJson[0] == null && !bl) {
               System.err.println("That player was not found on hypixel. " + string);
               if (!AutoHypixel.autoChecker.getSetting("Denick").asBoolean()) {
                  Denicker.nicks.put(string, "???");
               } else {
                  AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                  if (HypixelGameTracker.game != 4) {
                     Collection collection = networkPlayerInfo.getGameProfile().getProperties().get("textures");
                     String string2 = "";

                     for (Object object : collection) {
                        Property property = (Property)object;
                        if (property.getName().equals("textures")) {
                           string2 = property.getValue();
                        }
                     }

                     if (!string2.isEmpty()) {
                        String string3 = new String(Base64.getDecoder().decode(string2.getBytes(StandardCharsets.UTF_8)));

                        try {
                           SkinTexturesPayload skinTexturesPayload = ConfigFiles.GSON.fromJson(string3, SkinTexturesPayload.class);
                           if (this.nickSkinOwners.contains(skinTexturesPayload.getProfileName())) {
                              atomicBoolean.set(true);
                           } else {
                              Denicker.nicks.put(string, skinTexturesPayload.getProfileName());
                              PlayerStatsEntry playerStatsEntry = new PlayerStatsEntry(skinTexturesPayload.getProfileName(), networkPlayerInfo, this);
                              playerStatsEntry.realName = skinTexturesPayload.getProfileName();
                              playerStatsEntry.name = string;
                              PlayerListTracker.playerStats.add(playerStatsEntry);
                           }
                        } catch (Exception exception) {
                           atomicBoolean.set(true);
                           exception.printStackTrace();
                        }
                     } else {
                        atomicBoolean.set(true);
                     }
                  }

                  if (atomicBoolean.get() || HypixelGameTracker.game == 4) {
                     Denicker.nicks.put(string, "???");
                     unresolvedNicks.put(string, new PlayerStatsChecker.r95(networkPlayerInfo, this));
                  }
               }

               this.fetchState[0] = -1;
            } else {
               System.out.println("Added " + string + " to stats checking");
               this.fetchState[0] = 1;
               PlayerListTracker.playerStats.add(new PlayerStatsEntry(string, networkPlayerInfo, this));
            }

            PlayerListTracker.checkedPlayers.add(string);
         }
      });
   }

   public void initializeWithDenick(UUID uuid, String string, NetworkPlayerInfo networkPlayerInfo) {
      this.initialize(uuid, string, networkPlayerInfo, false);
   }

   public double getNetworkLevel() {
      String string = this.getStatString("networkExp");
      if (string.equals("Err")) {
         return -2.0;
      }

      if (string.equals("NA")) {
         return -1.0;
      }

      double d = Double.parseDouble(string);
      return Math.sqrt(2.0 * d + 30625.0) / 50.0 - 2.5;
   }

   public boolean M_boolean() {
      String string = this.getStatString("lastLogin");
      String string2 = this.getStatString("lastLogout");
      if (string.equals("Err") || string2.equals("Err")) {
         return false;
      } else if (!string.equals("NA") && !string2.equals("NA")) {
         long l = Long.parseLong(string);
         long m = Long.parseLong(string2);
         return l >= 0L && m >= 0L ? l > m : false;
      } else {
         return false;
      }
   }

   public String getKnownAliases() {
      String string = this.getStatString("knownAliases");
      if (string.equals("Err")) {
         return "Err";
      } else {
         return string.equals("NA") ? "NA" : string;
      }
   }

   public String E_String() {
      String string = this.getStatString("guildKickReason");
      if (string.equals("Err")) {
         return "Err";
      } else {
         return string.equals("NA") ? "NA" : string;
      }
   }

   public String r_String() {
      String string = this.getStatString("mostRecentGameType");
      if (string.equals("Err")) {
         return "Err";
      } else {
         return string.equals("NA") ? "NA" : string;
      }
   }

   public String U_String() {
      String string = this.getStatString("userLanguage");
      if (string.equals("Err")) {
         return "Err";
      } else {
         return string.equals("NA") ? "NA" : string;
      }
   }

   public String G_String() {
      String string = this.getStatString("lastLogin");
      if (string.equals("Err")) {
         return "Err";
      }

      if (string.equals("NA")) {
         return "NA";
      }

      long l = Long.parseLong(string);
      long n = System.currentTimeMillis();
      long l2 = n - l;
      long l3 = 86400000L;
      long l4 = 3600000L;
      long l5 = 60000L;
      long l6 = 1000L;

      int i;
      for (i = 0; l2 - l3 >= 0L; i++) {
         l2 -= l3;
      }

      int j;
      for (j = 0; l2 - l4 >= 0L; j++) {
         l2 -= l4;
      }

      int k;
      for (k = 0; l2 - l5 >= 0L; k++) {
         l2 -= l5;
      }

      int i2;
      for (i2 = 0; l2 - l6 >= 0L; i2++) {
         l2 -= l6;
      }

      return i + "d, " + j + "h, " + k + "m, " + i2 + "s ago";
   }

   public int getBedwarsLevel() {
      return (int)this.getBedwarsLevelFromExp(this.getStatInt("stats", "Bedwars", "Experience"));
   }

   private double getBedwarsLevelFromExp(double d) {
      int i = 100 * (int)(d / 487000.0);
      d %= 487000.0;
      if (d < 500.0) {
         return i + d / 500.0;
      }

      i++;
      if (d < 1500.0) {
         return i + (d - 500.0) / 1000.0;
      }

      i++;
      if (d < 3500.0) {
         return i + (d - 1500.0) / 2000.0;
      }

      i++;
      if (d < 7000.0) {
         return i + (d - 3500.0) / 3500.0;
      }

      i++;
      d -= 7000.0;
      return i + d / 5000.0;
   }

   public int getSkywarsKills() {
      return this.getStatInt("stats", "SkyWars", "kills");
   }

   public int z_int() {
      return this.getStatInt("stats", "SkyWars", "deaths");
   }

   public float getSkywarsKdr() {
      return this.getRatio(this.getSkywarsKills(), this.z_int());
   }

   public int getSkywarsWins() {
      return this.getStatInt("stats", "SkyWars", "wins");
   }

   public int getSkywarsLosses() {
      return this.getStatInt("stats", "SkyWars", "losses");
   }

   public float getSkywarsWlr() {
      return this.getRatio(this.getSkywarsWins(), this.getSkywarsLosses());
   }

   public int k_int() {
      return this.getStatInt("stats", "Bedwars", "kills_bedwars");
   }

   public int getBedwarsDeaths() {
      return this.getStatInt("stats", "Bedwars", "deaths_bedwars");
   }

   public float getBedwarsKdr() {
      return this.getRatio(this.k_int(), this.getBedwarsDeaths());
   }

   public int getBedwarsFinalKills() {
      return this.getStatInt("stats", "Bedwars", "final_kills_bedwars");
   }

   public int getBedwarsFinalDeaths() {
      return this.getStatInt("stats", "Bedwars", "final_deaths_bedwars");
   }

   public float getBedwarsFkdr() {
      return this.getRatio(this.getBedwarsFinalKills(), this.getBedwarsFinalDeaths());
   }

   public int getBedwarsWinstreak() {
      return this.getStatInt("stats", "Bedwars", "winstreak");
   }

   public int getUhcKills() {
      return this.getStatInt("stats", "UHC", "kills");
   }

   public int F_int() {
      return this.getStatInt("stats", "UHC", "deaths");
   }

   public float getUhcKdr() {
      return this.getRatio(this.getUhcKills(), this.F_int());
   }

   public int Y_int() {
      return this.getStatInt("stats", "UHC", "wins");
   }

   public int K_int() {
      int i = this.getStatInt("stats", "UHC", "score");
      if (i < 10) {
         return 1;
      } else if (i < 60) {
         return 2;
      } else if (i < 210) {
         return 3;
      } else if (i < 460) {
         return 4;
      } else if (i < 960) {
         return 5;
      } else if (i < 1710) {
         return 6;
      } else if (i < 2710) {
         return 7;
      } else if (i < 5210) {
         return 8;
      } else if (i < 10210) {
         return 9;
      } else if (i < 13210) {
         return 10;
      } else if (i < 16210) {
         return 11;
      } else if (i < 19210) {
         return 12;
      } else if (i < 22210) {
         return 13;
      } else {
         return i < 25210 ? 14 : 15;
      }
   }

   public int getUhcSoloKills() {
      return this.getStatInt("stats", "UHC", "kills_solo");
   }

   public int getUhcSoloDeaths() {
      return this.getStatInt("stats", "UHC", "deaths_solo");
   }

   public float getUhcSoloKdr() {
      return this.getRatio(this.getUhcSoloKills(), this.getUhcSoloDeaths());
   }

   public int getUhcTotalKills() {
      return this.sumStats(this.getUhcSoloKills(), this.getUhcKills());
   }

   public int getUhcTotalDeaths() {
      return this.sumStats(this.getUhcSoloDeaths(), this.F_int());
   }

   public float getUhcTotalKdr() {
      return this.getRatio(this.getUhcTotalKills(), this.getUhcTotalDeaths());
   }

   public String X_String() {
      return this.getStatString("stats", "UHC", "equippedKit");
   }

   public int t_int() {
      return this.getStatInt("stats", "Duels", "kills");
   }

   public int d_int() {
      return this.getStatInt("stats", "Duels", "deaths");
   }

   public float getDuelsKdr() {
      return this.getRatio(this.t_int(), this.d_int());
   }

   public int E_int() {
      return this.getStatInt("stats", "Duels", "wins");
   }

   public int A_int() {
      return this.getStatInt("stats", "Duels", "losses");
   }

   public float getDuelsWlr() {
      return this.getRatio(this.E_int(), this.A_int());
   }

   public int getDuelsWinstreak() {
      return this.getStatInt("stats", "Duels", "current_winstreak");
   }

   public int getDuelsMeleeHits() {
      return this.getStatInt("stats", "Duels", "melee_hits");
   }

   public int getDuelsMeleeSwings() {
      return this.getStatInt("stats", "Duels", "melee_swings");
   }

   public float getDuelsMeleeAccuracy() {
      return this.getRatio(this.getDuelsMeleeHits(), this.getDuelsMeleeSwings());
   }

   public int getDuelsRoundsPlayed() {
      return this.getStatInt("stats", "Duels", "rounds_played");
   }

   public int getBlitzKills() {
      return this.getStatInt("stats", "HungerGames", "kills");
   }

   public int u_int() {
      return this.getStatInt("stats", "HungerGames", "deaths");
   }

   public float getBlitzKdr() {
      return this.getRatio(this.getBlitzKills(), this.u_int());
   }

   public int getBlitzWins() {
      return this.getStatInt("stats", "HungerGames", "wins");
   }

   public int Z_int() {
      return this.getStatInt("stats", "HungerGames", "wins_teams");
   }

   public int getMegaWallsKills() {
      return this.getStatInt("stats", "Walls3", "kills");
   }

   public int O_int() {
      return this.getStatInt("stats", "Walls3", "assists");
   }

   public int r_int() {
      return this.getStatInt("stats", "Walls3", "final_assists");
   }

   public int getMegaWallsDeaths() {
      return this.getStatInt("stats", "Walls3", "deaths");
   }

   public int getMegaWallsFinalKills() {
      return this.getStatInt("stats", "Walls3", "final_kills");
   }

   public int getMegaWallsFinalDeaths() {
      return this.getStatInt("stats", "Walls3", "final_deaths");
   }

   public int P_int() {
      return this.getStatInt("stats", "Walls3", "wins");
   }

   public int getMegaWallsLosses() {
      return this.getStatInt("stats", "Walls3", "losses");
   }

   public float getMegaWallsKdr() {
      return this.getRatio(this.getMegaWallsKills(), this.getMegaWallsDeaths());
   }

   public float getMegaWallsFkdr() {
      return this.getRatio(this.getMegaWallsFinalKills(), this.getMegaWallsFinalDeaths());
   }

   public float getMegaWallsWlr() {
      return this.getRatio(this.P_int(), this.getMegaWallsLosses());
   }

   public String f_String() {
      return this.getStatString("stats", "Walls3", "chosen_class");
   }

   public String l_String() {
      return this.getStatString("stats", "Pit", "profile", "selected_perk_0");
   }

   public String getPitSecondPerk() {
      return this.getStatString("stats", "Pit", "profile", "selected_perk_1");
   }

   public String u_String() {
      return this.getStatString("stats", "Pit", "profile", "selected_killstreak_1");
   }

   public int getPitDeaths() {
      return this.getStatInt("stats", "Pit", "pit_stats_ptl", "deaths");
   }

   public int getPitKills() {
      return this.getStatInt("stats", "Pit", "pit_stats_ptl", "kills");
   }

   public float getPitKdr() {
      return this.getRatio(this.getPitKills(), this.getPitDeaths());
   }

   public int D_int() {
      return this.getStatInt("stats", "Pit", "pit_stats_ptl", "max_streak");
   }

   public float getPitGold() {
      return this.getStatFloat("stats", "Pit", "profile", "cash");
   }

   private void S_void() {
      try {
         while (this.fetchState[0] == 0) {
            Thread.sleep(100L);
         }
      } catch (InterruptedException interruptedException) {
         System.out.println("This only happens if you try to wait a negative time.");
      }
   }

   private float getRatio(int i, int j) {
      float f = i;
      float g = j;
      if (f < 0.0F || g < 0.0F) {
         return Math.min(f, g);
      } else {
         return g == 0.0F ? f : f / g;
      }
   }

   private int sumStats(int i, int j) {
      return i >= 0 && j >= 0 ? i + j : Math.min(i, j);
   }

   public int getStatInt(String... strings) {
      this.S_void();
      if (this.fetchState[0] != 1) {
         return this.fetchState[0];
      }

      try {
         JsonObject[] jsonObjects = new JsonObject[strings.length];
         jsonObjects[0] = this.playerJson[0];

         for (int i = 0; i < jsonObjects.length - 1; i++) {
            if (jsonObjects[i] == null) {
               return -1;
            }

            jsonObjects[i + 1] = jsonObjects[i].getAsJsonObject(strings[i]);
         }

         if (jsonObjects[jsonObjects.length - 1] == null) {
            return 0;
         }

         JsonElement jsonElement = jsonObjects[jsonObjects.length - 1].get(strings[strings.length - 1]);
         return jsonElement == null ? 0 : (int)Float.parseFloat(jsonElement.toString());
      } catch (NullPointerException nullPointerException) {
         return -2;
      }
   }

   public String x_String() {
      String string = this.s_String();
      String string2 = this.Z_String();
      return string.equals("") ? string2 : string + " " + string2;
   }

   public String Z_String() {
      return this.getStatString("displayname");
   }

   public String s_String() {
      String string = this.getStatString("prefix");
      if (!string.equals("Err") && !string.equals("NA")) {
         return string;
      }

      String string2 = this.getStatString("rank");
      if (!string2.equals("Err") && !string2.equals("NA")) {
         return string2;
      }

      String string3 = this.getStatString("monthlyPackageRank");
      if (!string3.equals("Err") && !string3.equals("NA") && !string3.contains("NONE")) {
         return "MVP++";
      }

      String string4 = this.getStatString("newPackageRank");
      if (!string4.equals("Err") && !string4.equals("NA") && !string4.equals("NONE")) {
         return string4.replaceAll("_PLUS", "+");
      }

      String string5 = this.getStatString("packageRank");
      return !string5.equals("Err") && !string5.equals("NA") && !string5.equals("NONE") ? string5.replaceAll("_PLUS", "+") : "";
   }

   public float getStatFloat(String... strings) {
      if (this.fetchState[0] != 1) {
         return this.fetchState[0];
      }

      try {
         JsonObject[] jsonObjects = new JsonObject[strings.length];
         jsonObjects[0] = this.playerJson[0];

         for (int i = 0; i < jsonObjects.length - 1; i++) {
            if (jsonObjects[i] == null) {
               return -1.0F;
            }

            jsonObjects[i + 1] = jsonObjects[i].getAsJsonObject(strings[i]);
         }

         if (jsonObjects[jsonObjects.length - 1] == null) {
            return 0.0F;
         }

         JsonElement jsonElement = jsonObjects[jsonObjects.length - 1].get(strings[strings.length - 1]);
         return jsonElement == null ? 0.0F : Float.parseFloat(jsonElement.toString());
      } catch (NullPointerException nullPointerException) {
         return -2.0F;
      }
   }

   public String getStatString(String... strings) {
      this.S_void();
      if (this.fetchState[0] == -2) {
         return "Err";
      }

      if (this.fetchState[0] == -1) {
         return "NA";
      }

      try {
         JsonObject[] jsonObjects = new JsonObject[strings.length];
         jsonObjects[0] = this.playerJson[0];

         for (int i = 0; i < jsonObjects.length - 1; i++) {
            if (jsonObjects[i] == null) {
               return "NA";
            }

            jsonObjects[i + 1] = jsonObjects[i].getAsJsonObject(strings[i]);
         }

         if (jsonObjects[jsonObjects.length - 1] == null) {
            return "NA";
         }

         JsonElement jsonElement = jsonObjects[jsonObjects.length - 1].get(strings[strings.length - 1]);
         return jsonElement == null ? "NA" : jsonElement.toString();
      } catch (NullPointerException nullPointerException) {
         return "Err";
      }
   }

   public class r95 {
      public NetworkPlayerInfo playerInfo;
      public PlayerStatsChecker checker;

      public r95(NetworkPlayerInfo networkPlayerInfo, PlayerStatsChecker playerStatsChecker2) {
         this.playerInfo = networkPlayerInfo;
         this.checker = playerStatsChecker2;
      }
   }
}
