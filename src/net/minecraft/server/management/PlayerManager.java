package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {
   private static final Logger pmLogger = LogManager.getLogger();
   private final WorldServer theWorldServer;
   private final List<EntityPlayerMP> players = Lists.newArrayList();
   private final LongHashMap<PlayerManager.PlayerInstance> playerInstances = new LongHashMap<>();
   private final List<PlayerManager.PlayerInstance> playerInstancesToUpdate = Lists.newArrayList();
   private final List<PlayerManager.PlayerInstance> playerInstanceList = Lists.newArrayList();
   private int playerViewRadius;
   private long previousTotalWorldTime;
   private final int[][] xzDirectionsConst = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
   private static final long b = 86329544726729L;

   public PlayerManager(WorldServer var1) {
      this.theWorldServer = var1;
      this.setPlayerViewRadius(var1.getMinecraftServer().getConfigurationManager().getViewDistance());
   }

   public WorldServer getWorldServer() {
      return this.theWorldServer;
   }

   public void updatePlayerInstances() {
      long var4 = this.theWorldServer.getTotalWorldTime();
      if (var4 - this.previousTotalWorldTime > 8000L) {
         this.previousTotalWorldTime = var4;

         for (int var6 = 0; var6 < this.playerInstanceList.size(); var6++) {
            PlayerManager.PlayerInstance var7 = this.playerInstanceList.get(var6);
            var7.onUpdate();
            var7.processChunk();
         }
      } else {
         for (int var8 = 0; var8 < this.playerInstancesToUpdate.size(); var8++) {
            PlayerManager.PlayerInstance var10 = this.playerInstancesToUpdate.get(var8);
            var10.onUpdate();
         }
      }

      this.playerInstancesToUpdate.clear();
      if (this.players.isEmpty()) {
         WorldProvider var9 = this.theWorldServer.provider;
         if (!var9.canRespawnHere()) {
            this.theWorldServer.theChunkProviderServer.unloadAllChunks();
         }
      }
   }

   public boolean hasPlayerInstance(int var1, int var2) {
      long var5 = var1 + 2147483647L | var2 + 2147483647L << 32;
      return this.playerInstances.getValueByKey(var5) != null;
   }

   private PlayerManager.PlayerInstance getPlayerInstance(int var1, int var2, boolean var3) {
      long var7 = var1 + 2147483647L | var2 + 2147483647L << 32;
      PlayerManager.PlayerInstance var9 = this.playerInstances.getValueByKey(var7);
      if (var9 == null && var3) {
         var9 = new PlayerManager.PlayerInstance(var1, var2);
         this.playerInstances.add(var7, var9);
         this.playerInstanceList.add(var9);
      }

      return var9;
   }

   public void markBlockForUpdate(BlockPos var1) {
      int var5 = var1.getX() >> 4;
      int var6 = var1.getZ() >> 4;
      PlayerManager.PlayerInstance var7 = this.getPlayerInstance(var5, var6, false);
      if (var7 != null) {
         var7.flagChunkForUpdate(var1.getX() & 15, var1.getY(), var1.getZ() & 15);
      }
   }

   public void addPlayer(EntityPlayerMP var1) {
      int var5 = (int)var1.posX >> 4;
      int var6 = (int)var1.posZ >> 4;
      var1.managedPosX = var1.posX;
      var1.managedPosZ = var1.posZ;

      for (int var7 = var5 - this.playerViewRadius; var7 <= var5 + this.playerViewRadius; var7++) {
         for (int var8 = var6 - this.playerViewRadius; var8 <= var6 + this.playerViewRadius; var8++) {
            this.getPlayerInstance(var7, var8, true).addPlayer(var1);
         }
      }

      this.players.add(var1);
      this.filterChunkLoadQueue(var1);
   }

   public void filterChunkLoadQueue(EntityPlayerMP var1) {
      ArrayList var5 = Lists.newArrayList(var1.loadedChunks);
      int var6 = 0;
      int var7 = this.playerViewRadius;
      int var8 = (int)var1.posX >> 4;
      int var9 = (int)var1.posZ >> 4;
      int var10 = 0;
      int var11 = 0;
      ChunkCoordIntPair var12 = this.getPlayerInstance(var8, var9, true).chunkCoords;
      var1.loadedChunks.clear();
      if (var5.contains(var12)) {
         var1.loadedChunks.add(var12);
      }

      for (int var13 = 1; var13 <= var7 * 2; var13++) {
         for (int var14 = 0; var14 < 2; var14++) {
            int[] var15 = this.xzDirectionsConst[var6++ % 4];

            for (int var16 = 0; var16 < var13; var16++) {
               var10 += var15[0];
               var11 += var15[1];
               var12 = this.getPlayerInstance(var8 + var10, var9 + var11, true).chunkCoords;
               if (var5.contains(var12)) {
                  var1.loadedChunks.add(var12);
               }
            }
         }
      }

      var6 %= 4;

      for (int var20 = 0; var20 < var7 * 2; var20++) {
         var10 += this.xzDirectionsConst[var6][0];
         var11 += this.xzDirectionsConst[var6][1];
         var12 = this.getPlayerInstance(var8 + var10, var9 + var11, true).chunkCoords;
         if (var5.contains(var12)) {
            var1.loadedChunks.add(var12);
         }
      }
   }

   public void removePlayer(EntityPlayerMP var1) {
      int var5 = (int)var1.managedPosX >> 4;
      int var6 = (int)var1.managedPosZ >> 4;

      for (int var7 = var5 - this.playerViewRadius; var7 <= var5 + this.playerViewRadius; var7++) {
         for (int var8 = var6 - this.playerViewRadius; var8 <= var6 + this.playerViewRadius; var8++) {
            PlayerManager.PlayerInstance var9 = this.getPlayerInstance(var7, var8, false);
            if (var9 != null) {
               var9.removePlayer(var1);
            }
         }
      }

      this.players.remove(var1);
   }

   private boolean overlaps(int var1, int var2, int var3, int var4, int var5) {
      int var9 = var1 - var3;
      int var10 = var2 - var4;
      return var9 >= -var5 && var9 <= var5 ? var10 >= -var5 && var10 <= var5 : false;
   }

   public void updateMountedMovingPlayer(EntityPlayerMP var1) {
      int var5 = (int)var1.posX >> 4;
      int var6 = (int)var1.posZ >> 4;
      double var7 = var1.managedPosX - var1.posX;
      double var9 = var1.managedPosZ - var1.posZ;
      double var11 = var7 * var7 + var9 * var9;
      if (var11 >= 64.0) {
         int var13 = (int)var1.managedPosX >> 4;
         int var14 = (int)var1.managedPosZ >> 4;
         int var15 = this.playerViewRadius;
         int var16 = var5 - var13;
         int var17 = var6 - var14;
         if (var16 != 0 || var17 != 0) {
            for (int var18 = var5 - var15; var18 <= var5 + var15; var18++) {
               for (int var19 = var6 - var15; var19 <= var6 + var15; var19++) {
                  if (!this.overlaps(var18, var19, var13, var14, var15)) {
                     this.getPlayerInstance(var18, var19, true).addPlayer(var1);
                  }

                  if (!this.overlaps(var18 - var16, var19 - var17, var5, var6, var15)) {
                     PlayerManager.PlayerInstance var20 = this.getPlayerInstance(var18 - var16, var19 - var17, false);
                     if (var20 != null) {
                        var20.removePlayer(var1);
                     }
                  }
               }
            }

            this.filterChunkLoadQueue(var1);
            var1.managedPosX = var1.posX;
            var1.managedPosZ = var1.posZ;
         }
      }
   }

   public boolean isPlayerWatchingChunk(EntityPlayerMP var1, int var2, int var3) {
      PlayerManager.PlayerInstance var7 = this.getPlayerInstance(var2, var3, false);
      return var7 != null && var7.playersWatchingChunk.contains(var1) && !var1.loadedChunks.contains(var7.chunkCoords);
   }

   public void setPlayerViewRadius(int var1) {
      var1 = MathHelper.clamp_int(var1, 3, 32);
      if (var1 != this.playerViewRadius) {
         int var5 = var1 - this.playerViewRadius;

         for (EntityPlayerMP var7 : Lists.newArrayList(this.players)) {
            int var8 = (int)var7.posX >> 4;
            int var9 = (int)var7.posZ >> 4;
            if (var5 > 0) {
               for (int var14 = var8 - var1; var14 <= var8 + var1; var14++) {
                  for (int var15 = var9 - var1; var15 <= var9 + var1; var15++) {
                     PlayerManager.PlayerInstance var12 = this.getPlayerInstance(var14, var15, true);
                     if (!var12.playersWatchingChunk.contains(var7)) {
                        var12.addPlayer(var7);
                     }
                  }
               }
            } else {
               for (int var10 = var8 - this.playerViewRadius; var10 <= var8 + this.playerViewRadius; var10++) {
                  for (int var11 = var9 - this.playerViewRadius; var11 <= var9 + this.playerViewRadius; var11++) {
                     if (!this.overlaps(var10, var11, var8, var9, var1)) {
                        this.getPlayerInstance(var10, var11, true).removePlayer(var7);
                     }
                  }
               }
            }
         }

         this.playerViewRadius = var1;
      }
   }

   public static int getFurthestViewableBlock(int var0) {
      return var0 * 16 - 16;
   }

   public class PlayerInstance {
      private final List<EntityPlayerMP> playersWatchingChunk = Lists.newArrayList();
      private final ChunkCoordIntPair chunkCoords;
      private short[] locationOfBlockChange = new short[64];
      private int numBlocksToUpdate;
      private int flagsYAreasToUpdate;
      private long previousWorldTime;
      private static final long a = 113068349012539L;

      public PlayerInstance(int var2, int var3) {
         this.chunkCoords = new ChunkCoordIntPair(var2, var3);
         PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(var2, var3);
      }

      public void addPlayer(EntityPlayerMP var1) {
         if (this.playersWatchingChunk.contains(var1)) {
            PlayerManager.pmLogger.debug("Failed to add player. {} already is in chunk {}, {}", var1, this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
         } else {
            if (this.playersWatchingChunk.isEmpty()) {
               this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
            }

            this.playersWatchingChunk.add(var1);
            var1.loadedChunks.add(this.chunkCoords);
         }
      }

      public void removePlayer(EntityPlayerMP var1) {
         if (this.playersWatchingChunk.contains(var1)) {
            Chunk var5 = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
            if (var5.isPopulated()) {
               var1.playerNetServerHandler.sendPacket(new S21PacketChunkData(var5, true, 0));
            }

            this.playersWatchingChunk.remove(var1);
            var1.loadedChunks.remove(this.chunkCoords);
            if (this.playersWatchingChunk.isEmpty()) {
               long var6 = this.chunkCoords.chunkXPos + 2147483647L | this.chunkCoords.chunkZPos + 2147483647L << 32;
               this.increaseInhabitedTime(var5);
               PlayerManager.this.playerInstances.remove(var6);
               PlayerManager.this.playerInstanceList.remove(this);
               if (this.numBlocksToUpdate > 0) {
                  PlayerManager.this.playerInstancesToUpdate.remove(this);
               }

               PlayerManager.this.getWorldServer().theChunkProviderServer.dropChunk(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
            }
         }
      }

      public void processChunk() {
         this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos));
      }

      private void increaseInhabitedTime(Chunk var1) {
         var1.setInhabitedTime(var1.getInhabitedTime() + PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
         this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
      }

      public void flagChunkForUpdate(int var1, int var2, int var3) {
         if (this.numBlocksToUpdate == 0) {
            PlayerManager.this.playerInstancesToUpdate.add(this);
         }

         this.flagsYAreasToUpdate |= 1 << (var2 >> 4);
         if (this.numBlocksToUpdate < 64) {
            short var7 = (short)(var1 << 12 | var3 << 8 | var2);

            for (int var8 = 0; var8 < this.numBlocksToUpdate; var8++) {
               if (this.locationOfBlockChange[var8] == var7) {
                  return;
               }
            }

            this.locationOfBlockChange[this.numBlocksToUpdate++] = var7;
         }
      }

      public void sendToAllPlayersWatchingChunk(Packet var1) {
         for (int var5 = 0; var5 < this.playersWatchingChunk.size(); var5++) {
            EntityPlayerMP var6 = this.playersWatchingChunk.get(var5);
            if (!var6.loadedChunks.contains(this.chunkCoords)) {
               var6.playerNetServerHandler.sendPacket(var1);
            }
         }
      }

      public void onUpdate() {
         if (this.numBlocksToUpdate != 0) {
            if (this.numBlocksToUpdate == 1) {
               int var11 = (this.locationOfBlockChange[0] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
               int var13 = this.locationOfBlockChange[0] & 255;
               int var15 = (this.locationOfBlockChange[0] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
               BlockPos var17 = new BlockPos(var11, var13, var15);
               this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(PlayerManager.this.theWorldServer, var17));
               if (PlayerManager.this.theWorldServer.getBlockState(var17).getBlock().hasTileEntity()) {
                  this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(var17));
               }
            } else if (this.numBlocksToUpdate != 64) {
               this.sendToAllPlayersWatchingChunk(
                  new S22PacketMultiBlockChange(
                     this.numBlocksToUpdate,
                     this.locationOfBlockChange,
                     PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos)
                  )
               );

               for (int var10 = 0; var10 < this.numBlocksToUpdate; var10++) {
                  int var12 = (this.locationOfBlockChange[var10] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
                  int var14 = this.locationOfBlockChange[var10] & 255;
                  int var16 = (this.locationOfBlockChange[var10] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
                  BlockPos var18 = new BlockPos(var12, var14, var16);
                  if (PlayerManager.this.theWorldServer.getBlockState(var18).getBlock().hasTileEntity()) {
                     this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(var18));
                  }
               }
            } else {
               int var4 = this.chunkCoords.chunkXPos * 16;
               int var5 = this.chunkCoords.chunkZPos * 16;
               this.sendToAllPlayersWatchingChunk(
                  new S21PacketChunkData(
                     PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos),
                     false,
                     this.flagsYAreasToUpdate
                  )
               );

               for (int var6 = 0; var6 < 16; var6++) {
                  if ((this.flagsYAreasToUpdate & 1 << var6) != 0) {
                     int var7 = var6 << 4;
                     List var8 = PlayerManager.this.theWorldServer.getTileEntitiesIn(var4, var7, var5, var4 + 16, var7 + 16, var5 + 16);

                     for (int var9 = 0; var9 < var8.size(); var9++) {
                        this.sendTileToAllPlayersWatchingChunk((TileEntity)var8.get(var9));
                     }
                  }
               }
            }

            this.numBlocksToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
         }
      }

      private void sendTileToAllPlayersWatchingChunk(TileEntity var1) {
         if (var1 != null) {
            Packet var5 = var1.getDescriptionPacket();
            if (var5 != null) {
               this.sendToAllPlayersWatchingChunk(var5);
            }
         }
      }
   }
}
