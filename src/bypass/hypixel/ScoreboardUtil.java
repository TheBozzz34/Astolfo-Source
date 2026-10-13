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
      ArrayList var3 = new ArrayList();
      if (x.theWorld == null) {
         return var3;
      }

      Scoreboard var4 = x.theWorld.getScoreboard();
      return var4 == null ? var3 : getFormattedSidebarText(var4);
   }

   public static List<String> getFormattedSidebarText(Scoreboard var0) {
      ArrayList var3 = new ArrayList();
      ScoreObjective var4 = var0.getObjectiveInDisplaySlot(1);
      if (var4 == null) {
         return var3;
      }

      java.util.Collection var5 = var0.getSortedScores(var4);
      List var6 = (java.util.List)(var5.stream()
         .filter(var0x -> var0x != null && ((net.minecraft.scoreboard.Score)var0x).getPlayerName() != null && !((net.minecraft.scoreboard.Score)var0x).getPlayerName().startsWith("#"))
         .collect(Collectors.toList()));
      if (var6.size() > 15) {
         var5 = Lists.newArrayList(Iterables.skip(var6, var5.size() - 15));
      } else {
         var5 = var6;
      }

      for (Object var8$raw : var5) {
         Score var8 = (Score)var8$raw;
         ScorePlayerTeam var9 = var0.getPlayersTeam(var8.getPlayerName());
         var3.add(ScorePlayerTeam.formatPlayerName(var9, ""));
      }

      Collections.reverse(var3);
      return var3;
   }

   public static List<String> d() {
      return z(p());
   }

   public static List<String> P(Scoreboard var0) {
      List var1 = getFormattedSidebarText(var0);
      return z(var1);
   }

   public static List<String> z(List<String> var0) {
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 < var0.size(); var4++) {
         var3.add(var4, StringUtils.stripControlCodes((String)var0.get(var4)));
      }

      return var3;
   }

   public static String k(Scoreboard var0) {
      ScoreObjective var3 = var0.getObjectiveInDisplaySlot(1);
      return var3 == null ? "" : var3.getDisplayName();
   }

   public static String c(Scoreboard var0) {
      return StringUtils.stripControlCodes(k(var0));
   }

   public static String m() {
      return x.theWorld == null ? null : c(x.theWorld.getScoreboard());
   }
}
