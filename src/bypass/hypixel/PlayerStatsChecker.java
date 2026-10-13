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

   public PlayerStatsChecker(HypixelAPI var1) {
      this.w = var1;
      this.l[0] = 0;
   }

   public void initialize(UUID var1, String var2, NetworkPlayerInfo var3, boolean var4) {
      this.w.getPlayerByUuid(var1).whenComplete((var4x, var5) -> {
         if (PlayerListTracker.checkedPlayers.contains(var2)) {
            this.l[0] = -2;
         } else if (var5 != null) {
            PlayerListTracker.checkTimer.reset();
            var5.printStackTrace();
            this.l[0] = -2;
         } else {
            this.v[0] = var4x.getPlayer();
            if (this.v[0] == null && !var4) {
               System.err.println("That player was not found on hypixel. " + var2);
               if (!AutoHypixel.autoChecker.getSetting("Denick").asBoolean()) {
                  Denicker.nicks.put(var2, "???");
               } else {
                  AtomicBoolean var9 = new AtomicBoolean(false);
                  if (HypixelGameTracker.game != 4) {
                     Collection var10 = var3.getGameProfile().getProperties().get("textures");
                     String var11 = "";

                     for (Object var13$raw : var10) {
                        Property var13 = (Property)var13$raw;
                        if (var13.getName().equals("textures")) {
                           var11 = var13.getValue();
                        }
                     }

                     if (!var11.isEmpty()) {
                        String var16 = new String(Base64.getDecoder().decode(var11.getBytes(StandardCharsets.UTF_8)));

                        try {
                           SkinTexturesPayload var17 = ConfigFiles.V.fromJson(var16, SkinTexturesPayload.class);
                           if (this.z.contains(var17.g())) {
                              var9.set(true);
                           } else {
                              Denicker.nicks.put(var2, var17.g());
                              PlayerStatsEntry var14 = new PlayerStatsEntry(var17.g(), var3, this);
                              var14.E = var17.g();
                              var14.Q = var2;
                              PlayerListTracker.playerStats.add(var14);
                           }
                        } catch (Exception var15) {
                           var9.set(true);
                           var15.printStackTrace();
                        }
                     } else {
                        var9.set(true);
                     }
                  }

                  if (var9.get() || HypixelGameTracker.game == 4) {
                     Denicker.nicks.put(var2, "???");
                     V.put(var2, new PlayerStatsChecker.r95(var3, this));
                  }
               }

               this.l[0] = -1;
            } else {
               System.out.println("Added " + var2 + " to stats checking");
               this.l[0] = 1;
               PlayerListTracker.playerStats.add(new PlayerStatsEntry(var2, var3, this));
            }

            PlayerListTracker.checkedPlayers.add(var2);
         }
      });
   }

   public void h(UUID var1, String var2, NetworkPlayerInfo var3) {
      this.initialize(var1, var2, var3, false);
   }

   public double r() {
      String var4 = this.N("networkExp");
      if (var4.equals("Err")) {
         return -2.0;
      }

      if (var4.equals("NA")) {
         return -1.0;
      }

      double var5 = Double.parseDouble(var4);
      return Math.sqrt(2.0 * var5 + 30625.0) / 50.0 - 2.5;
   }

   public boolean M_boolean() {
      String var4 = this.N("lastLogin");
      String var5 = this.N("lastLogout");
      if (var4.equals("Err") || var5.equals("Err")) {
         return false;
      } else if (!var4.equals("NA") && !var5.equals("NA")) {
         long var6 = Long.parseLong(var4);
         long var8 = Long.parseLong(var5);
         return var6 >= 0L && var8 >= 0L ? var6 > var8 : false;
      } else {
         return false;
      }
   }

   public String v() {
      String var4 = this.N("knownAliases");
      if (var4.equals("Err")) {
         return "Err";
      } else {
         return var4.equals("NA") ? "NA" : var4;
      }
   }

   public String E_String() {
      String var4 = this.N("guildKickReason");
      if (var4.equals("Err")) {
         return "Err";
      } else {
         return var4.equals("NA") ? "NA" : var4;
      }
   }

   public String r_String() {
      String var4 = this.N("mostRecentGameType");
      if (var4.equals("Err")) {
         return "Err";
      } else {
         return var4.equals("NA") ? "NA" : var4;
      }
   }

   public String U_String() {
      String var4 = this.N("userLanguage");
      if (var4.equals("Err")) {
         return "Err";
      } else {
         return var4.equals("NA") ? "NA" : var4;
      }
   }

   public String G_String() {
      String var4 = this.N("lastLogin");
      if (var4.equals("Err")) {
         return "Err";
      }

      if (var4.equals("NA")) {
         return "NA";
      }

      long var5 = Long.parseLong(var4);
      long var7 = System.currentTimeMillis();
      long var9 = var7 - var5;
      long var11 = 86400000L;
      long var13 = 3600000L;
      long var15 = 60000L;
      long var17 = 1000L;

      int var19;
      for (var19 = 0; var9 - var11 >= 0L; var19++) {
         var9 -= var11;
      }

      int var20;
      for (var20 = 0; var9 - var13 >= 0L; var20++) {
         var9 -= var13;
      }

      int var21;
      for (var21 = 0; var9 - var15 >= 0L; var21++) {
         var9 -= var15;
      }

      int var22;
      for (var22 = 0; var9 - var17 >= 0L; var22++) {
         var9 -= var17;
      }

      return var19 + "d, " + var20 + "h, " + var21 + "m, " + var22 + "s ago";
   }

   public int n() {
      return (int)this.W(this.D("stats", "Bedwars", "Experience"));
   }

   private double W(double var1) {
      int var6 = 100 * (int)(var1 / 487000.0);
      var1 %= 487000.0;
      if (var1 < 500.0) {
         return var6 + var1 / 500.0;
      }

      var6++;
      if (var1 < 1500.0) {
         return var6 + (var1 - 500.0) / 1000.0;
      }

      var6++;
      if (var1 < 3500.0) {
         return var6 + (var1 - 1500.0) / 2000.0;
      }

      var6++;
      if (var1 < 7000.0) {
         return var6 + (var1 - 3500.0) / 3500.0;
      }

      var6++;
      var1 -= 7000.0;
      return var6 + var1 / 5000.0;
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
      int var4 = this.D("stats", "UHC", "score");
      if (var4 < 10) {
         return 1;
      } else if (var4 < 60) {
         return 2;
      } else if (var4 < 210) {
         return 3;
      } else if (var4 < 460) {
         return 4;
      } else if (var4 < 960) {
         return 5;
      } else if (var4 < 1710) {
         return 6;
      } else if (var4 < 2710) {
         return 7;
      } else if (var4 < 5210) {
         return 8;
      } else if (var4 < 10210) {
         return 9;
      } else if (var4 < 13210) {
         return 10;
      } else if (var4 < 16210) {
         return 11;
      } else if (var4 < 19210) {
         return 12;
      } else if (var4 < 22210) {
         return 13;
      } else {
         return var4 < 25210 ? 14 : 15;
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
      } catch (InterruptedException var5) {
         System.out.println("This only happens if you try to wait a negative time.");
      }
   }

   private float K(int var1, int var2) {
      float var6 = var1;
      float var7 = var2;
      if (var6 < 0.0F || var7 < 0.0F) {
         return Math.min(var6, var7);
      } else {
         return var7 == 0.0F ? var6 : var6 / var7;
      }
   }

   private int I(int var1, int var2) {
      return var1 >= 0 && var2 >= 0 ? var1 + var2 : Math.min(var1, var2);
   }

   public int D(String... var1) {
      this.S_void();
      if (this.l[0] != 1) {
         return this.l[0];
      }

      try {
         JsonObject[] var5 = new JsonObject[var1.length];
         var5[0] = this.v[0];

         for (int var6 = 0; var6 < var5.length - 1; var6++) {
            if (var5[var6] == null) {
               return -1;
            }

            var5[var6 + 1] = var5[var6].getAsJsonObject(var1[var6]);
         }

         if (var5[var5.length - 1] == null) {
            return 0;
         }

         JsonElement var8 = var5[var5.length - 1].get(var1[var1.length - 1]);
         return var8 == null ? 0 : (int)Float.parseFloat(var8.toString());
      } catch (NullPointerException var7) {
         return -2;
      }
   }

   public String x_String() {
      String var4 = this.s_String();
      String var5 = this.Z_String();
      return var4.equals("") ? var5 : var4 + " " + var5;
   }

   public String Z_String() {
      return this.N("displayname");
   }

   public String s_String() {
      String var4 = this.N("prefix");
      if (!var4.equals("Err") && !var4.equals("NA")) {
         return var4;
      }

      String var5 = this.N("rank");
      if (!var5.equals("Err") && !var5.equals("NA")) {
         return var5;
      }

      String var6 = this.N("monthlyPackageRank");
      if (!var6.equals("Err") && !var6.equals("NA") && !var6.contains("NONE")) {
         return "MVP++";
      }

      String var7 = this.N("newPackageRank");
      if (!var7.equals("Err") && !var7.equals("NA") && !var7.equals("NONE")) {
         return var7.replaceAll("_PLUS", "+");
      }

      String var8 = this.N("packageRank");
      return !var8.equals("Err") && !var8.equals("NA") && !var8.equals("NONE") ? var8.replaceAll("_PLUS", "+") : "";
   }

   public float R(String... var1) {
      if (this.l[0] != 1) {
         return this.l[0];
      }

      try {
         JsonObject[] var5 = new JsonObject[var1.length];
         var5[0] = this.v[0];

         for (int var6 = 0; var6 < var5.length - 1; var6++) {
            if (var5[var6] == null) {
               return -1.0F;
            }

            var5[var6 + 1] = var5[var6].getAsJsonObject(var1[var6]);
         }

         if (var5[var5.length - 1] == null) {
            return 0.0F;
         }

         JsonElement var8 = var5[var5.length - 1].get(var1[var1.length - 1]);
         return var8 == null ? 0.0F : Float.parseFloat(var8.toString());
      } catch (NullPointerException var7) {
         return -2.0F;
      }
   }

   public String N(String... var1) {
      this.S_void();
      if (this.l[0] == -2) {
         return "Err";
      }

      if (this.l[0] == -1) {
         return "NA";
      }

      try {
         JsonObject[] var5 = new JsonObject[var1.length];
         var5[0] = this.v[0];

         for (int var6 = 0; var6 < var5.length - 1; var6++) {
            if (var5[var6] == null) {
               return "NA";
            }

            var5[var6 + 1] = var5[var6].getAsJsonObject(var1[var6]);
         }

         if (var5[var5.length - 1] == null) {
            return "NA";
         }

         JsonElement var8 = var5[var5.length - 1].get(var1[var1.length - 1]);
         return var8 == null ? "NA" : var8.toString();
      } catch (NullPointerException var7) {
         return "Err";
      }
   }

   public class r95 {
      public NetworkPlayerInfo F;
      public PlayerStatsChecker w;

      public r95(NetworkPlayerInfo var2, PlayerStatsChecker var3) {
         this.F = var2;
         this.w = var3;
      }
   }
}
