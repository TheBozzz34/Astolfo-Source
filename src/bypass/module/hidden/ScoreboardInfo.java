package bypass.module.hidden;

import bypass.hypixel.ScoreboardUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.scoreboard.Scoreboard;

public class ScoreboardInfo {
   private static final Pattern N = Pattern.compile("\\s*\\d+/\\d+/\\d+\\s+([\\d\\w]+)\\s*", 2);
   private static final Pattern R = Pattern.compile("\\s*MEGA\\sWALLS\\s*", 2);
   private static final Pattern y = Pattern.compile("[0-9]+\\sFinals?\\s[0-9]+\\sF\\.\\sAssists?");
   private static final Pattern x = Pattern.compile("Players:\\s*[0-9]+/[0-9]+");
   private static final Pattern F = Pattern.compile("\\s*\\[.\\] Wither HP: ?(\\d+).*", 2);
   private static final Pattern g = Pattern.compile("\\s*\\[.\\] Wither [❤♥]: ?(\\d+).*", 2);
   private ArrayList<String> I = new ArrayList<>();
   private String a = null;
   private boolean G = false;
   private boolean w = false;
   private boolean o = false;
   private boolean P = false;
   private boolean z = false;
   private static final Pattern Q = Pattern.compile("(?i)§[0-9A-F]");
   private static final long b = 117121948055747L;

   public ScoreboardInfo(Scoreboard var1) {
      if (var1 != null) {
         String var4 = ScoreboardUtil.c(var1);
         if (R.matcher(var4).matches()) {
            this.w = true;
            List var5 = ScoreboardUtil.getFormattedSidebarText(var1);
            List var6 = ScoreboardUtil.z(var5);
            if (var6.size() != 0) {
               Matcher var7 = N.matcher((CharSequence)var6.get(0));
               if (!var7.matches()) {
                  for (Object var21$raw : var6) {
                     String var21 = (String)var21$raw;
                     if (x.matcher(var21).find()) {
                        this.a = null;
                        this.o = true;
                        this.G = false;
                        return;
                     }
                  }
               } else {
                  this.a = var7.group(1);

                  for (Object var9$raw : var6) {
                     String var9 = (String)var9$raw;
                     if (y.matcher(var9).find()) {
                        this.G = true;
                     } else if (x.matcher(var9).find()) {
                        this.a = null;
                        this.o = true;
                        this.G = false;
                        return;
                     }
                  }

                  if (var6.size() >= 7) {
                     if (((String)var6.get(1)).contains("Walls Fall:") || ((String)var6.get(1)).contains("Gates Open:")) {
                        this.P = true;
                     }

                     int var16 = 0;
                     int var18 = 1000;

                     for (int var10 = 3; var10 < Math.min(var6.size(), 7); var10++) {
                        String var11 = (String)var6.get(var10);
                        Matcher var12 = F.matcher(var11);
                        if (var12.matches()) {
                           String var14 = (String)var5.get(var10);
                           String var13 = E(var14, "\\[").replace("§", "");
                           this.I.add(var13);
                           var18 = Integer.parseInt(var12.group(1));
                        } else {
                           Matcher var23 = g.matcher(var11);
                           if (var23.matches()) {
                              String var15 = (String)var5.get(var10);
                              String var22 = E(var15, "\\[").replace("§", "");
                              this.I.add(var22);
                              var18 = 2 * Integer.parseInt(var23.group(1));
                           }
                        }

                        if (var11.contains("eliminated!")) {
                           var16++;
                        }
                     }

                     if (var16 == 3 || ((String)var6.get(1)).contains("None!:")) {
                        this.z = true;
                     }

                     if (this.n()) {
                     }
                  }
               }
            }
         }
      }
   }

   public boolean O(String var1) {
      return this.I.contains(var1);
   }

   public String s() {
      return this.a;
   }

   public List<String> h() {
      return this.I;
   }

   public boolean G() {
      return this.I.size() == 4;
   }

   public boolean w() {
      return this.z;
   }

   public boolean K() {
      return this.P;
   }

   public boolean n() {
      return this.I.size() == 1;
   }

   public boolean A() {
      return this.w;
   }

   public boolean M() {
      return this.G;
   }

   public boolean v() {
      return this.o;
   }

   public static String E(String var0, String var1) {
      String[] var4 = var0.split(var1, 2);
      return var4.length != 2 ? "" : R(var4[0]);
   }

   public static String R(String var0) {
      Matcher var3 = Q.matcher(var0);
      String var4 = "";

      while (var3.find()) {
         var4 = var3.group();
      }

      return var4;
   }
}
