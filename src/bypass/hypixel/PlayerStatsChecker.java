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
   private HypixelAPI w;
   public final JsonObject[] v = new JsonObject[1];
   public final int[] l = new int[1];
   public static final HashMap<String, PlayerStatsChecker.r95> V = new HashMap<>();
   private final List<String> z = Arrays.asList(
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
   private static final long a = 8583257840339L;

   public PlayerStatsChecker(HypixelAPI hypixelAPI) {
      this.w = hypixelAPI;
      this.l[0] = 0;
   }

   public void initialize(UUID uuid, String string, NetworkPlayerInfo networkPlayerInfo, boolean bl) {
      this.w.getPlayerByUuid(uuid).whenComplete((playerReply, throwable) -> {
         if (PlayerListTracker.checkedPlayers.contains(string)) {
            this.l[0] = -2;
         } else if (throwable != null) {
            PlayerListTracker.checkTimer.reset();
            throwable.printStackTrace();
            this.l[0] = -2;
         } else {
            this.v[0] = playerReply.getPlayer();
            if (this.v[0] == null && !bl) {
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
                           SkinTexturesPayload skinTexturesPayload = ConfigFiles.V.fromJson(string3, SkinTexturesPayload.class);
                           if (this.z.contains(skinTexturesPayload.g())) {
                              atomicBoolean.set(true);
                           } else {
                              Denicker.nicks.put(string, skinTexturesPayload.g());
                              PlayerStatsEntry playerStatsEntry = new PlayerStatsEntry(skinTexturesPayload.g(), networkPlayerInfo, this);
                              playerStatsEntry.E = skinTexturesPayload.g();
                              playerStatsEntry.Q = string;
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
                     V.put(string, new PlayerStatsChecker.r95(networkPlayerInfo, this));
                  }
               }

               this.l[0] = -1;
            } else {
               System.out.println("Added " + string + " to stats checking");
               this.l[0] = 1;
               PlayerListTracker.playerStats.add(new PlayerStatsEntry(string, networkPlayerInfo, this));
            }

            PlayerListTracker.checkedPlayers.add(string);
         }
      });
   }

   public void h(UUID uuid, String string, NetworkPlayerInfo networkPlayerInfo) {
      this.initialize(uuid, string, networkPlayerInfo, false);
   }

   public double r() {
      String string = this.N("networkExp");
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
      String string = this.N("lastLogin");
      String string2 = this.N("lastLogout");
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

   public String v() {
      String string = this.N("knownAliases");
      if (string.equals("Err")) {
         return "Err";
      } else {
         return string.equals("NA") ? "NA" : string;
      }
   }

   public String E_String() {
      String string = this.N("guildKickReason");
      if (string.equals("Err")) {
         return "Err";
      } else {
         return string.equals("NA") ? "NA" : string;
      }
   }

   public String r_String() {
      String string = this.N("mostRecentGameType");
      if (string.equals("Err")) {
         return "Err";
      } else {
         return string.equals("NA") ? "NA" : string;
      }
   }

   public String U_String() {
      String string = this.N("userLanguage");
      if (string.equals("Err")) {
         return "Err";
      } else {
         return string.equals("NA") ? "NA" : string;
      }
   }

   public String G_String() {
      String string = this.N("lastLogin");
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

   public int n() {
      return (int)this.W(this.D("stats", "Bedwars", "Experience"));
   }

   private double W(double d) {
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

   public int B() {
      return this.D("stats", "SkyWars", "kills");
   }

   public int z_int() {
      return this.D("stats", "SkyWars", "deaths");
   }

   public float z() {
      return this.K(this.B(), this.z_int());
   }

   public int C() {
      return this.D("stats", "SkyWars", "wins");
   }

   public int o() {
      return this.D("stats", "SkyWars", "losses");
   }

   public float Y() {
      return this.K(this.C(), this.o());
   }

   public int k_int() {
      return this.D("stats", "Bedwars", "kills_bedwars");
   }

   public int j() {
      return this.D("stats", "Bedwars", "deaths_bedwars");
   }

   public float T() {
      return this.K(this.k_int(), this.j());
   }

   public int W() {
      return this.D("stats", "Bedwars", "final_kills_bedwars");
   }

   public int l() {
      return this.D("stats", "Bedwars", "final_deaths_bedwars");
   }

   public float d() {
      return this.K(this.W(), this.l());
   }

   public int w() {
      return this.D("stats", "Bedwars", "winstreak");
   }

   public int q() {
      return this.D("stats", "UHC", "kills");
   }

   public int F_int() {
      return this.D("stats", "UHC", "deaths");
   }

   public float F() {
      return this.K(this.q(), this.F_int());
   }

   public int Y_int() {
      return this.D("stats", "UHC", "wins");
   }

   public int K_int() {
      int i = this.D("stats", "UHC", "score");
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

   public int f() {
      return this.D("stats", "UHC", "kills_solo");
   }

   public int H() {
      return this.D("stats", "UHC", "deaths_solo");
   }

   public float k() {
      return this.K(this.f(), this.H());
   }

   public int i() {
      return this.I(this.f(), this.q());
   }

   public int R() {
      return this.I(this.H(), this.F_int());
   }

   public float P() {
      return this.K(this.i(), this.R());
   }

   public String X_String() {
      return this.N("stats", "UHC", "equippedKit");
   }

   public int t_int() {
      return this.D("stats", "Duels", "kills");
   }

   public int d_int() {
      return this.D("stats", "Duels", "deaths");
   }

   public float u() {
      return this.K(this.t_int(), this.d_int());
   }

   public int E_int() {
      return this.D("stats", "Duels", "wins");
   }

   public int A_int() {
      return this.D("stats", "Duels", "losses");
   }

   public float E() {
      return this.K(this.E_int(), this.A_int());
   }

   public int U() {
      return this.D("stats", "Duels", "current_winstreak");
   }

   public int G() {
      return this.D("stats", "Duels", "melee_hits");
   }

   public int p() {
      return this.D("stats", "Duels", "melee_swings");
   }

   public float O() {
      return this.K(this.G(), this.p());
   }

   public int S() {
      return this.D("stats", "Duels", "rounds_played");
   }

   public int J() {
      return this.D("stats", "HungerGames", "kills");
   }

   public int u_int() {
      return this.D("stats", "HungerGames", "deaths");
   }

   public float K() {
      return this.K(this.J(), this.u_int());
   }

   public int y() {
      return this.D("stats", "HungerGames", "wins");
   }

   public int Z_int() {
      return this.D("stats", "HungerGames", "wins_teams");
   }

   public int x() {
      return this.D("stats", "Walls3", "kills");
   }

   public int O_int() {
      return this.D("stats", "Walls3", "assists");
   }

   public int r_int() {
      return this.D("stats", "Walls3", "final_assists");
   }

   public int c() {
      return this.D("stats", "Walls3", "deaths");
   }

   public int s() {
      return this.D("stats", "Walls3", "final_kills");
   }

   public int X() {
      return this.D("stats", "Walls3", "final_deaths");
   }

   public int P_int() {
      return this.D("stats", "Walls3", "wins");
   }

   public int M() {
      return this.D("stats", "Walls3", "losses");
   }

   public float A() {
      return this.K(this.x(), this.c());
   }

   public float D() {
      return this.K(this.s(), this.X());
   }

   public float N() {
      return this.K(this.P_int(), this.M());
   }

   public String f_String() {
      return this.N("stats", "Walls3", "chosen_class");
   }

   public String l_String() {
      return this.N("stats", "Pit", "profile", "selected_perk_0");
   }

   public String V() {
      return this.N("stats", "Pit", "profile", "selected_perk_1");
   }

   public String u_String() {
      return this.N("stats", "Pit", "profile", "selected_killstreak_1");
   }

   public int m() {
      return this.D("stats", "Pit", "pit_stats_ptl", "deaths");
   }

   public int b() {
      return this.D("stats", "Pit", "pit_stats_ptl", "kills");
   }

   public float t() {
      return this.K(this.b(), this.m());
   }

   public int D_int() {
      return this.D("stats", "Pit", "pit_stats_ptl", "max_streak");
   }

   public float Z() {
      return this.R("stats", "Pit", "profile", "cash");
   }

   private void S_void() {
      try {
         while (this.l[0] == 0) {
            Thread.sleep(100L);
         }
      } catch (InterruptedException interruptedException) {
         System.out.println("This only happens if you try to wait a negative time.");
      }
   }

   private float K(int i, int j) {
      float f = i;
      float g = j;
      if (f < 0.0F || g < 0.0F) {
         return Math.min(f, g);
      } else {
         return g == 0.0F ? f : f / g;
      }
   }

   private int I(int i, int j) {
      return i >= 0 && j >= 0 ? i + j : Math.min(i, j);
   }

   public int D(String... strings) {
      this.S_void();
      if (this.l[0] != 1) {
         return this.l[0];
      }

      try {
         JsonObject[] jsonObjects = new JsonObject[strings.length];
         jsonObjects[0] = this.v[0];

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
      return this.N("displayname");
   }

   public String s_String() {
      String string = this.N("prefix");
      if (!string.equals("Err") && !string.equals("NA")) {
         return string;
      }

      String string2 = this.N("rank");
      if (!string2.equals("Err") && !string2.equals("NA")) {
         return string2;
      }

      String string3 = this.N("monthlyPackageRank");
      if (!string3.equals("Err") && !string3.equals("NA") && !string3.contains("NONE")) {
         return "MVP++";
      }

      String string4 = this.N("newPackageRank");
      if (!string4.equals("Err") && !string4.equals("NA") && !string4.equals("NONE")) {
         return string4.replaceAll("_PLUS", "+");
      }

      String string5 = this.N("packageRank");
      return !string5.equals("Err") && !string5.equals("NA") && !string5.equals("NONE") ? string5.replaceAll("_PLUS", "+") : "";
   }

   public float R(String... strings) {
      if (this.l[0] != 1) {
         return this.l[0];
      }

      try {
         JsonObject[] jsonObjects = new JsonObject[strings.length];
         jsonObjects[0] = this.v[0];

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

   public String N(String... strings) {
      this.S_void();
      if (this.l[0] == -2) {
         return "Err";
      }

      if (this.l[0] == -1) {
         return "NA";
      }

      try {
         JsonObject[] jsonObjects = new JsonObject[strings.length];
         jsonObjects[0] = this.v[0];

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
      public NetworkPlayerInfo F;
      public PlayerStatsChecker w;

      public r95(NetworkPlayerInfo networkPlayerInfo, PlayerStatsChecker playerStatsChecker2) {
         this.F = networkPlayerInfo;
         this.w = playerStatsChecker2;
      }
   }
}
