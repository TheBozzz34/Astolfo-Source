package net.minecraft.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ChatComponentScore extends ChatComponentStyle {
   private final String name;
   private final String objective;
   private String value = "";
   private static final long a = 26030676759320L;

   public ChatComponentScore(String var1, String var2) {
      this.name = var1;
      this.objective = var2;
   }

   public String getName() {
      return this.name;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   @Override
   public String getUnformattedTextForChat() {
      MinecraftServer var4 = MinecraftServer.getServer();
      if (var4 != null && var4.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
         Scoreboard var5 = var4.worldServerForDimension(0).getScoreboard();
         ScoreObjective var6 = var5.getObjective(this.objective);
         if (var5.entityHasObjective(this.name, var6)) {
            Score var7 = var5.getValueFromObjective(this.name, var6);
            this.setValue(String.format("%d", var7.getScorePoints()));
         } else {
            this.value = "";
         }
      }

      return this.value;
   }

   public ChatComponentScore createCopy() {
      ChatComponentScore var4 = new ChatComponentScore(this.name, this.objective);
      var4.setValue(this.value);
      var4.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent var6 : this.getSiblings()) {
         var4.appendSibling(var6.createCopy());
      }

      return var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ChatComponentScore)) {
         return false;
      }

      ChatComponentScore var5 = (ChatComponentScore)var1;
      return this.name.equals(var5.name) && this.objective.equals(var5.objective) && super.equals(var1);
   }

   @Override
   public String toString() {
      return "ScoreComponent{name='"
         + this.name
         + '\''
         + "objective='"
         + this.objective
         + '\''
         + ", siblings="
         + this.siblings
         + ", style="
         + this.getChatStyle()
         + '}';
   }
}
