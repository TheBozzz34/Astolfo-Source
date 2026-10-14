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

   public ServerScoreboard(MinecraftServer mcServer) {
      this.scoreboardMCServer = mcServer;
   }

   @Override
   public void func_96536_a(Score score) {
      super.func_96536_a(score);
      if (this.field_96553_b.contains(score.getObjective())) {
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(score));
      }

      this.markSaveDataDirty();
   }

   @Override
   public void func_96516_a(String string) {
      super.func_96516_a(string);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(string));
      this.markSaveDataDirty();
   }

   @Override
   public void func_178820_a(String string, ScoreObjective scoreObjective) {
      super.func_178820_a(string, scoreObjective);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(string, scoreObjective));
      this.markSaveDataDirty();
   }

   @Override
   public void setObjectiveInDisplaySlot(int i, ScoreObjective scoreObjective) {
      ScoreObjective scoreObjective2 = this.getObjectiveInDisplaySlot(i);
      super.setObjectiveInDisplaySlot(i, scoreObjective);
      if (scoreObjective2 != scoreObjective && scoreObjective2 != null) {
         if (this.func_96552_h(scoreObjective2) > 0) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(i, scoreObjective));
         } else {
            this.sendDisplaySlotRemovalPackets(scoreObjective2);
         }
      }

      if (scoreObjective != null) {
         if (this.field_96553_b.contains(scoreObjective)) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(i, scoreObjective));
         } else {
            this.func_96549_e(scoreObjective);
         }
      }

      this.markSaveDataDirty();
   }

   @Override
   public boolean addPlayerToTeam(String player, String newTeam) {
      if (super.addPlayerToTeam(player, newTeam)) {
         ScorePlayerTeam scorePlayerTeam = this.getTeam(newTeam);
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam, Arrays.asList(player), 3));
         this.markSaveDataDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removePlayerFromTeam(String string, ScorePlayerTeam scorePlayerTeam) {
      super.removePlayerFromTeam(string, scorePlayerTeam);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam, Arrays.asList(string), 4));
      this.markSaveDataDirty();
   }

   @Override
   public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn) {
      super.onScoreObjectiveAdded(scoreObjectiveIn);
      this.markSaveDataDirty();
   }

   @Override
   public void onObjectiveDisplayNameChanged(ScoreObjective scoreObjective) {
      super.onObjectiveDisplayNameChanged(scoreObjective);
      if (this.field_96553_b.contains(scoreObjective)) {
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3BPacketScoreboardObjective(scoreObjective, 2));
      }

      this.markSaveDataDirty();
   }

   @Override
   public void onScoreObjectiveRemoved(ScoreObjective scoreObjective) {
      super.onScoreObjectiveRemoved(scoreObjective);
      if (this.field_96553_b.contains(scoreObjective)) {
         this.sendDisplaySlotRemovalPackets(scoreObjective);
      }

      this.markSaveDataDirty();
   }

   @Override
   public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {
      super.broadcastTeamCreated(playerTeam);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 0));
      this.markSaveDataDirty();
   }

   @Override
   public void sendTeamUpdate(ScorePlayerTeam playerTeam) {
      super.sendTeamUpdate(playerTeam);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 2));
      this.markSaveDataDirty();
   }

   @Override
   public void func_96513_c(ScorePlayerTeam playerTeam) {
      super.func_96513_c(playerTeam);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 1));
      this.markSaveDataDirty();
   }

   public void func_96547_a(ScoreboardSaveData scoreboardSaveData) {
      this.scoreboardSaveData = scoreboardSaveData;
   }

   public void markSaveDataDirty() {
      if (this.scoreboardSaveData != null) {
         this.scoreboardSaveData.markDirty();
      }
   }

   public List<Packet> func_96550_d(ScoreObjective scoreObjective) {
      ArrayList arrayList = Lists.newArrayList();
      arrayList.add(new S3BPacketScoreboardObjective(scoreObjective, 0));

      for (int i = 0; i < 19; i++) {
         if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
            arrayList.add(new S3DPacketDisplayScoreboard(i, scoreObjective));
         }
      }

      for (Score score : this.getSortedScores(scoreObjective)) {
         arrayList.add(new S3CPacketUpdateScore(score));
      }

      return arrayList;
   }

   public void func_96549_e(ScoreObjective scoreObjective) {
      List list = this.func_96550_d(scoreObjective);

      for (EntityPlayerMP entityPlayerMP : this.scoreboardMCServer.getConfigurationManager().getPlayerList()) {
         for (Object object : list) {
            Packet packet = (Packet)object;
            entityPlayerMP.playerNetServerHandler.sendPacket(packet);
         }
      }

      this.field_96553_b.add(scoreObjective);
   }

   public List<Packet> func_96548_f(ScoreObjective scoreObjective) {
      ArrayList arrayList = Lists.newArrayList();
      arrayList.add(new S3BPacketScoreboardObjective(scoreObjective, 1));

      for (int i = 0; i < 19; i++) {
         if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
            arrayList.add(new S3DPacketDisplayScoreboard(i, scoreObjective));
         }
      }

      return arrayList;
   }

   public void sendDisplaySlotRemovalPackets(ScoreObjective scoreObjective) {
      List list = this.func_96548_f(scoreObjective);

      for (EntityPlayerMP entityPlayerMP : this.scoreboardMCServer.getConfigurationManager().getPlayerList()) {
         for (Object object : list) {
            Packet packet = (Packet)object;
            entityPlayerMP.playerNetServerHandler.sendPacket(packet);
         }
      }

      this.field_96553_b.remove(scoreObjective);
   }

   public int func_96552_h(ScoreObjective scoreObjective) {
      int i = 0;

      for (int j = 0; j < 19; j++) {
         if (this.getObjectiveInDisplaySlot(j) == scoreObjective) {
            i++;
         }
      }

      return i;
   }
}
