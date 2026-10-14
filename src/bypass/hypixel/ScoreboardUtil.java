package bypass.hypixel;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.StringUtils;

public class ScoreboardUtil {
   private static final Minecraft x = Minecraft.getMinecraft();
   private static final long a = 66278723889102L;

   public static List<String> p() {
      ArrayList arrayList = new ArrayList();
      if (x.theWorld == null) {
         return arrayList;
      }

      Scoreboard scoreboard = x.theWorld.getScoreboard();
      return scoreboard == null ? arrayList : getFormattedSidebarText(scoreboard);
   }

   public static List<String> getFormattedSidebarText(Scoreboard scoreboard) {
      ArrayList arrayList = new ArrayList();
      ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
      if (scoreObjective == null) {
         return arrayList;
      }

      java.util.Collection collection = scoreboard.getSortedScores(scoreObjective);
      List list = (java.util.List)(collection.stream()
         .filter(object -> object != null && ((net.minecraft.scoreboard.Score)object).getPlayerName() != null && !((net.minecraft.scoreboard.Score)object).getPlayerName().startsWith("#"))
         .collect(Collectors.toList()));
      if (list.size() > 15) {
         collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
      } else {
         collection = list;
      }

      for (Object object2 : collection) {
         Score score = (Score)object2;
         ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
         arrayList.add(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, ""));
      }

      Collections.reverse(arrayList);
      return arrayList;
   }

   public static List<String> d() {
      return z(p());
   }

   public static List<String> P(Scoreboard scoreboard) {
      List list = getFormattedSidebarText(scoreboard);
      return z(list);
   }

   public static List<String> z(List<String> list) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < list.size(); i++) {
         arrayList.add(i, StringUtils.stripControlCodes((String)list.get(i)));
      }

      return arrayList;
   }

   public static String k(Scoreboard scoreboard) {
      ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
      return scoreObjective == null ? "" : scoreObjective.getDisplayName();
   }

   public static String c(Scoreboard scoreboard) {
      return StringUtils.stripControlCodes(k(scoreboard));
   }

   public static String m() {
      return x.theWorld == null ? null : c(x.theWorld.getScoreboard());
   }
}
