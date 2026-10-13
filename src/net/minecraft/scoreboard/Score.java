package net.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class Score {
   public static final Comparator<Score> scoreComparator = new Comparator<Score>() {
      private static final long a = 33098526354769L;

      public int compare(Score var1, Score var2) {
         return var1.getScorePoints() > var2.getScorePoints()
            ? 1
            : (var1.getScorePoints() < var2.getScorePoints() ? -1 : var2.getPlayerName().compareToIgnoreCase(var1.getPlayerName()));
      }
   };
   private final Scoreboard theScoreboard;
   private final ScoreObjective theScoreObjective;
   private final String scorePlayerName;
   private int scorePoints;
   private boolean locked;
   private boolean forceUpdate;
   private static final long b = 121153079425935L;

   public Score(Scoreboard var1, ScoreObjective var2, String var3) {
      this.theScoreboard = var1;
      this.theScoreObjective = var2;
      this.scorePlayerName = var3;
      this.forceUpdate = true;
   }

   public void increseScore(int var1) {
      if (this.theScoreObjective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      }

      this.setScorePoints(this.getScorePoints() + var1);
   }

   public void decreaseScore(int var1) {
      if (this.theScoreObjective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      }

      this.setScorePoints(this.getScorePoints() - var1);
   }

   public void func_96648_a() {
      if (this.theScoreObjective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      }

      this.increseScore(1);
   }

   public int getScorePoints() {
      return this.scorePoints;
   }

   public void setScorePoints(int var1) {
      int var5 = this.scorePoints;
      this.scorePoints = var1;
      if (var5 != var1 || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreScoreboard().func_96536_a(this);
      }
   }

   public ScoreObjective getObjective() {
      return this.theScoreObjective;
   }

   public String getPlayerName() {
      return this.scorePlayerName;
   }

   public Scoreboard getScoreScoreboard() {
      return this.theScoreboard;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }

   public void func_96651_a(List<EntityPlayer> var1) {
      this.setScorePoints(this.theScoreObjective.getCriteria().setScore(var1));
   }
}
