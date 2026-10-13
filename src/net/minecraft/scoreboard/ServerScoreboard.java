package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer scoreboardMCServer;
   private final Set<ScoreObjective> field_96553_b = Sets.newHashSet();
   private ScoreboardSaveData scoreboardSaveData;
   private static final long e = 112742440145645L;

   public ServerScoreboard(MinecraftServer var1) {
      this.scoreboardMCServer = var1;
   }

   @Override
   public void func_96536_a(Score var1) {
      super.func_96536_a(var1);
      if (this.field_96553_b.contains(var1.getObjective())) {
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(var1));
      }

      this.markSaveDataDirty();
   }

   @Override
   public void func_96516_a(String var1) {
      super.func_96516_a(var1);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(var1));
      this.markSaveDataDirty();
   }

   @Override
   public void func_178820_a(String var1, ScoreObjective var2) {
      super.func_178820_a(var1, var2);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(var1, var2));
      this.markSaveDataDirty();
   }

   @Override
   public void setObjectiveInDisplaySlot(int var1, ScoreObjective var2) {
      ScoreObjective var6 = this.getObjectiveInDisplaySlot(var1);
      super.setObjectiveInDisplaySlot(var1, var2);
      if (var6 != var2 && var6 != null) {
         if (this.func_96552_h(var6) > 0) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(var1, var2));
         } else {
            this.sendDisplaySlotRemovalPackets(var6);
         }
      }

      if (var2 != null) {
         if (this.field_96553_b.contains(var2)) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(var1, var2));
         } else {
            this.func_96549_e(var2);
         }
      }

      this.markSaveDataDirty();
   }

   @Override
   public boolean addPlayerToTeam(String var1, String var2) {
      if (super.addPlayerToTeam(var1, var2)) {
         ScorePlayerTeam var6 = this.getTeam(var2);
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(var6, Arrays.asList(var1), 3));
         this.markSaveDataDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removePlayerFromTeam(String var1, ScorePlayerTeam var2) {
      super.removePlayerFromTeam(var1, var2);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(var2, Arrays.asList(var1), 4));
      this.markSaveDataDirty();
   }

   @Override
   public void onScoreObjectiveAdded(ScoreObjective var1) {
      super.onScoreObjectiveAdded(var1);
      this.markSaveDataDirty();
   }

   @Override
   public void onObjectiveDisplayNameChanged(ScoreObjective var1) {
      super.onObjectiveDisplayNameChanged(var1);
      if (this.field_96553_b.contains(var1)) {
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3BPacketScoreboardObjective(var1, 2));
      }

      this.markSaveDataDirty();
   }

   @Override
   public void onScoreObjectiveRemoved(ScoreObjective var1) {
      super.onScoreObjectiveRemoved(var1);
      if (this.field_96553_b.contains(var1)) {
         this.sendDisplaySlotRemovalPackets(var1);
      }

      this.markSaveDataDirty();
   }

   @Override
   public void broadcastTeamCreated(ScorePlayerTeam var1) {
      super.broadcastTeamCreated(var1);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(var1, 0));
      this.markSaveDataDirty();
   }

   @Override
   public void sendTeamUpdate(ScorePlayerTeam var1) {
      super.sendTeamUpdate(var1);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(var1, 2));
      this.markSaveDataDirty();
   }

   @Override
   public void func_96513_c(ScorePlayerTeam var1) {
      super.func_96513_c(var1);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(var1, 1));
      this.markSaveDataDirty();
   }

   public void func_96547_a(ScoreboardSaveData var1) {
      this.scoreboardSaveData = var1;
   }

   public void markSaveDataDirty() {
      if (this.scoreboardSaveData != null) {
         this.scoreboardSaveData.markDirty();
      }
   }

   public List<Packet> func_96550_d(ScoreObjective var1) {
      ArrayList var5 = Lists.newArrayList();
      var5.add(new S3BPacketScoreboardObjective(var1, 0));

      for (int var6 = 0; var6 < 19; var6++) {
         if (this.getObjectiveInDisplaySlot(var6) == var1) {
            var5.add(new S3DPacketDisplayScoreboard(var6, var1));
         }
      }

      for (Score var7 : this.getSortedScores(var1)) {
         var5.add(new S3CPacketUpdateScore(var7));
      }

      return var5;
   }

   public void func_96549_e(ScoreObjective var1) {
      List var5 = this.func_96550_d(var1);

      for (EntityPlayerMP var7 : this.scoreboardMCServer.getConfigurationManager().getPlayerList()) {
         for (Object var9$raw : var5) {
            Packet var9 = (Packet)var9$raw;
            var7.playerNetServerHandler.sendPacket(var9);
         }
      }

      this.field_96553_b.add(var1);
   }

   public List<Packet> func_96548_f(ScoreObjective var1) {
      ArrayList var5 = Lists.newArrayList();
      var5.add(new S3BPacketScoreboardObjective(var1, 1));

      for (int var6 = 0; var6 < 19; var6++) {
         if (this.getObjectiveInDisplaySlot(var6) == var1) {
            var5.add(new S3DPacketDisplayScoreboard(var6, var1));
         }
      }

      return var5;
   }

   public void sendDisplaySlotRemovalPackets(ScoreObjective var1) {
      List var5 = this.func_96548_f(var1);

      for (EntityPlayerMP var7 : this.scoreboardMCServer.getConfigurationManager().getPlayerList()) {
         for (Object var9$raw : var5) {
            Packet var9 = (Packet)var9$raw;
            var7.playerNetServerHandler.sendPacket(var9);
         }
      }

      this.field_96553_b.remove(var1);
   }

   public int func_96552_h(ScoreObjective var1) {
      int var5 = 0;

      for (int var6 = 0; var6 < 19; var6++) {
         if (this.getObjectiveInDisplaySlot(var6) == var1) {
            var5++;
         }
      }

      return var5;
   }
}
