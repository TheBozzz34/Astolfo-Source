package net.minecraft.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ChatComponentScore extends ChatComponentStyle {
   private final String name;
   private final String objective;
   private String value = "";

   public ChatComponentScore(String nameIn, String objectiveIn) {
      this.name = nameIn;
      this.objective = objectiveIn;
   }

   public String getName() {
      return this.name;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String valueIn) {
      this.value = valueIn;
   }

   @Override
   public String getUnformattedTextForChat() {
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      if (minecraftServer != null && minecraftServer.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
         Scoreboard scoreboard = minecraftServer.worldServerForDimension(0).getScoreboard();
         ScoreObjective scoreObjective = scoreboard.getObjective(this.objective);
         if (scoreboard.entityHasObjective(this.name, scoreObjective)) {
            Score score = scoreboard.getValueFromObjective(this.name, scoreObjective);
            this.setValue(String.format("%d", score.getScorePoints()));
         } else {
            this.value = "";
         }
      }

      return this.value;
   }

   public ChatComponentScore createCopy() {
      ChatComponentScore chatComponentScore = new ChatComponentScore(this.name, this.objective);
      chatComponentScore.setValue(this.value);
      chatComponentScore.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent iChatComponent : this.getSiblings()) {
         chatComponentScore.appendSibling(iChatComponent.createCopy());
      }

      return chatComponentScore;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof ChatComponentScore)) {
         return false;
      }

      ChatComponentScore chatComponentScore = (ChatComponentScore)object;
      return this.name.equals(chatComponentScore.name) && this.objective.equals(chatComponentScore.objective) && super.equals(object);
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
