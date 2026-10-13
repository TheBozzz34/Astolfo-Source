package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;

public class ScorePlayerTeam extends Team {
   private final Scoreboard theScoreboard;
   private final String registeredName;
   private final Set<String> membershipSet = Sets.newHashSet();
   private String teamNameSPT;
   private String namePrefixSPT = "";
   private String colorSuffix = "";
   private boolean allowFriendlyFire = true;
   private boolean canSeeFriendlyInvisibles = true;
   private Team.EnumVisible nameTagVisibility = Team.EnumVisible.ALWAYS;
   private Team.EnumVisible deathMessageVisibility = Team.EnumVisible.ALWAYS;
   private EnumChatFormatting chatFormat = EnumChatFormatting.RESET;
   private static final long a = 45737796354070L;

   public ScorePlayerTeam(Scoreboard var1, String var2) {
      this.theScoreboard = var1;
      this.registeredName = var2;
      this.teamNameSPT = var2;
   }

   @Override
   public String getRegisteredName() {
      return this.registeredName;
   }

   public String getTeamName() {
      return this.teamNameSPT;
   }

   public void setTeamName(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Name cannot be null");
      }

      this.teamNameSPT = var1;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public Collection<String> getMembershipCollection() {
      return this.membershipSet;
   }

   public String getColorPrefix() {
      return this.namePrefixSPT;
   }

   public void setNamePrefix(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Prefix cannot be null");
      }

      this.namePrefixSPT = var1;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public String getColorSuffix() {
      return this.colorSuffix;
   }

   public void setNameSuffix(String var1) {
      this.colorSuffix = var1;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public String formatString(String var1) {
      return this.getColorPrefix() + var1 + this.getColorSuffix();
   }

   public static String formatPlayerName(Team var0, String var1) {
      return var0 == null ? var1 : var0.formatString(var1);
   }

   @Override
   public boolean getAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean var1) {
      this.allowFriendlyFire = var1;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public boolean getSeeFriendlyInvisiblesEnabled() {
      return this.canSeeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisiblesEnabled(boolean var1) {
      this.canSeeFriendlyInvisibles = var1;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public Team.EnumVisible getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   @Override
   public Team.EnumVisible getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(Team.EnumVisible var1) {
      this.nameTagVisibility = var1;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public void setDeathMessageVisibility(Team.EnumVisible var1) {
      this.deathMessageVisibility = var1;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public int func_98299_i() {
      byte var4 = 0;
      if (this.getAllowFriendlyFire()) {
         var4 |= 1;
      }

      if (this.getSeeFriendlyInvisiblesEnabled()) {
         var4 |= 2;
      }

      return var4;
   }

   public void func_98298_a(int var1) {
      this.setAllowFriendlyFire((var1 & 1) > 0);
      this.setSeeFriendlyInvisiblesEnabled((var1 & 2) > 0);
   }

   public void setChatFormat(EnumChatFormatting var1) {
      this.chatFormat = var1;
   }

   public EnumChatFormatting getChatFormat() {
      return this.chatFormat;
   }
}
