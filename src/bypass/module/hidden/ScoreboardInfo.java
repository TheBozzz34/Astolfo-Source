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

   public ScoreboardInfo(Scoreboard scoreboard) {
      if (scoreboard != null) {
         String string = ScoreboardUtil.c(scoreboard);
         if (R.matcher(string).matches()) {
            this.w = true;
            List list = ScoreboardUtil.getFormattedSidebarText(scoreboard);
            List list2 = ScoreboardUtil.z(list);
            if (list2.size() != 0) {
               Matcher matcher2 = N.matcher((CharSequence)list2.get(0));
               if (!matcher2.matches()) {
                  for (Object object : list2) {
                     String string2 = (String)object;
                     if (x.matcher(string2).find()) {
                        this.a = null;
                        this.o = true;
                        this.G = false;
                        return;
                     }
                  }
               } else {
                  this.a = matcher2.group(1);

                  for (Object object2 : list2) {
                     String string3 = (String)object2;
                     if (y.matcher(string3).find()) {
                        this.G = true;
                     } else if (x.matcher(string3).find()) {
                        this.a = null;
                        this.o = true;
                        this.G = false;
                        return;
                     }
                  }

                  if (list2.size() >= 7) {
                     if (((String)list2.get(1)).contains("Walls Fall:") || ((String)list2.get(1)).contains("Gates Open:")) {
                        this.P = true;
                     }

                     int i = 0;
                     int j = 1000;

                     for (int k = 3; k < Math.min(list2.size(), 7); k++) {
                        String string4 = (String)list2.get(k);
                        Matcher matcher3 = F.matcher(string4);
                        if (matcher3.matches()) {
                           String string5 = (String)list.get(k);
                           String string6 = E(string5, "\\[").replace("§", "");
                           this.I.add(string6);
                           j = Integer.parseInt(matcher3.group(1));
                        } else {
                           Matcher matcher4 = g.matcher(string4);
                           if (matcher4.matches()) {
                              String string7 = (String)list.get(k);
                              String string8 = E(string7, "\\[").replace("§", "");
                              this.I.add(string8);
                              j = 2 * Integer.parseInt(matcher4.group(1));
                           }
                        }

                        if (string4.contains("eliminated!")) {
                           i++;
                        }
                     }

                     if (i == 3 || ((String)list2.get(1)).contains("None!:")) {
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

   public boolean O(String string) {
      return this.I.contains(string);
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

   public static String E(String string, String string2) {
      String[] strings = string.split(string2, 2);
      return strings.length != 2 ? "" : R(strings[0]);
   }

   public static String R(String string) {
      Matcher matcher2 = Q.matcher(string);
      String string2 = "";

      while (matcher2.find()) {
         string2 = matcher2.group();
      }

      return string2;
   }
}
