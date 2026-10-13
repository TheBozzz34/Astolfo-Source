package net.minecraft.scoreboard;

public class ScoreObjective {
   private final Scoreboard theScoreboard;
   private final String name;
   private final IScoreObjectiveCriteria objectiveCriteria;
   private IScoreObjectiveCriteria.EnumRenderType renderType;
   private String displayName;

   public ScoreObjective(Scoreboard var1, String var2, IScoreObjectiveCriteria var3) {
      this.theScoreboard = var1;
      this.name = var2;
      this.objectiveCriteria = var3;
      this.displayName = var2;
      this.renderType = var3.getRenderType();
   }

   public Scoreboard getScoreboard() {
      return this.theScoreboard;
   }

   public String getName() {
      return this.name;
   }

   public IScoreObjectiveCriteria getCriteria() {
      return this.objectiveCriteria;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setDisplayName(String var1) {
      this.displayName = var1;
      this.theScoreboard.onObjectiveDisplayNameChanged(this);
   }

   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return this.renderType;
   }

   public void setRenderType(IScoreObjectiveCriteria.EnumRenderType var1) {
      this.renderType = var1;
      this.theScoreboard.onObjectiveDisplayNameChanged(this);
   }
}
