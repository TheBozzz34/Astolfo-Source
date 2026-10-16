package bypass.module.hidden;

import bypass.hypixel.ScoreboardUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.scoreboard.Scoreboard;

public class ScoreboardInfo {
   private static final Pattern GAME_ID_PATTERN = Pattern.compile("\\s*\\d+/\\d+/\\d+\\s+([\\d\\w]+)\\s*", 2);
   private static final Pattern MEGA_WALLS_TITLE_PATTERN = Pattern.compile("\\s*MEGA\\sWALLS\\s*", 2);
   private static final Pattern FINALS_PATTERN = Pattern.compile("[0-9]+\\sFinals?\\s[0-9]+\\sF\\.\\sAssists?");
   private static final Pattern PLAYERS_COUNT_PATTERN = Pattern.compile("Players:\\s*[0-9]+/[0-9]+");
   private static final Pattern WITHER_HP_PATTERN = Pattern.compile("\\s*\\[.\\] Wither HP: ?(\\d+).*", 2);
   private static final Pattern WITHER_HEARTS_PATTERN = Pattern.compile("\\s*\\[.\\] Wither [❤♥]: ?(\\d+).*", 2);
   private ArrayList<String> aliveWitherTeams = new ArrayList<>();
   private String gameId = null;
   private boolean finalsShown = false;
   private boolean megaWalls = false;
   private boolean waitingForPlayers = false;
   private boolean wallsUp = false;
   private boolean deathmatch = false;
   private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-F]");

   public ScoreboardInfo(Scoreboard scoreboard) {
      if (scoreboard != null) {
         String string = ScoreboardUtil.getStrippedSidebarTitle(scoreboard);
         if (MEGA_WALLS_TITLE_PATTERN.matcher(string).matches()) {
            this.megaWalls = true;
            List list = ScoreboardUtil.getFormattedSidebarText(scoreboard);
            List list2 = ScoreboardUtil.stripControlCodes(list);
            if (list2.size() != 0) {
               Matcher matcher2 = GAME_ID_PATTERN.matcher((CharSequence)list2.get(0));
               if (!matcher2.matches()) {
                  for (Object object : list2) {
                     String string2 = (String)object;
                     if (PLAYERS_COUNT_PATTERN.matcher(string2).find()) {
                        this.gameId = null;
                        this.waitingForPlayers = true;
                        this.finalsShown = false;
                        return;
                     }
                  }
               } else {
                  this.gameId = matcher2.group(1);

                  for (Object object2 : list2) {
                     String string3 = (String)object2;
                     if (FINALS_PATTERN.matcher(string3).find()) {
                        this.finalsShown = true;
                     } else if (PLAYERS_COUNT_PATTERN.matcher(string3).find()) {
                        this.gameId = null;
                        this.waitingForPlayers = true;
                        this.finalsShown = false;
                        return;
                     }
                  }

                  if (list2.size() >= 7) {
                     if (((String)list2.get(1)).contains("Walls Fall:") || ((String)list2.get(1)).contains("Gates Open:")) {
                        this.wallsUp = true;
                     }

                     int i = 0;
                     int j = 1000;

                     for (int k = 3; k < Math.min(list2.size(), 7); k++) {
                        String string4 = (String)list2.get(k);
                        Matcher matcher3 = WITHER_HP_PATTERN.matcher(string4);
                        if (matcher3.matches()) {
                           String string5 = (String)list.get(k);
                           String string6 = getColorCodeBefore(string5, "\\[").replace("§", "");
                           this.aliveWitherTeams.add(string6);
                           j = Integer.parseInt(matcher3.group(1));
                        } else {
                           Matcher matcher4 = WITHER_HEARTS_PATTERN.matcher(string4);
                           if (matcher4.matches()) {
                              String string7 = (String)list.get(k);
                              String string8 = getColorCodeBefore(string7, "\\[").replace("§", "");
                              this.aliveWitherTeams.add(string8);
                              j = 2 * Integer.parseInt(matcher4.group(1));
                           }
                        }

                        if (string4.contains("eliminated!")) {
                           i++;
                        }
                     }

                     if (i == 3 || ((String)list2.get(1)).contains("None!:")) {
                        this.deathmatch = true;
                     }

                     if (this.isLastWitherAlive()) {
                     }
                  }
               }
            }
         }
      }
   }

   public boolean isWitherAlive(String string) {
      return this.aliveWitherTeams.contains(string);
   }

   public String getGameId() {
      return this.gameId;
   }

   public List<String> getAliveWitherTeams() {
      return this.aliveWitherTeams;
   }

   public boolean areAllWithersAlive() {
      return this.aliveWitherTeams.size() == 4;
   }

   public boolean isDeathmatch() {
      return this.deathmatch;
   }

   public boolean areWallsUp() {
      return this.wallsUp;
   }

   public boolean isLastWitherAlive() {
      return this.aliveWitherTeams.size() == 1;
   }

   public boolean isMegaWalls() {
      return this.megaWalls;
   }

   public boolean isFinalsShown() {
      return this.finalsShown;
   }

   public boolean isWaitingForPlayers() {
      return this.waitingForPlayers;
   }

   public static String getColorCodeBefore(String string, String string2) {
      String[] strings = string.split(string2, 2);
      return strings.length != 2 ? "" : getLastColorCode(strings[0]);
   }

   public static String getLastColorCode(String string) {
      Matcher matcher2 = COLOR_CODE_PATTERN.matcher(string);
      String string2 = "";

      while (matcher2.find()) {
         string2 = matcher2.group();
      }

      return string2;
   }
}
