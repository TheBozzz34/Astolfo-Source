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

   public PlayerManager(WorldServer serverWorld) {
      this.theWorldServer = serverWorld;
      this.setPlayerViewRadius(serverWorld.getMinecraftServer().getConfigurationManager().getViewDistance());
   }

   public WorldServer getWorldServer() {
      return this.theWorldServer;
   }

   public void updatePlayerInstances() {
      long l = this.theWorldServer.getTotalWorldTime();
      if (l - this.previousTotalWorldTime > 8000L) {
         this.previousTotalWorldTime = l;

         for (int i = 0; i < this.playerInstanceList.size(); i++) {
            PlayerManager.PlayerInstance playerInstance = this.playerInstanceList.get(i);
            playerInstance.onUpdate();
            playerInstance.processChunk();
         }
      } else {
         for (int j = 0; j < this.playerInstancesToUpdate.size(); j++) {
            PlayerManager.PlayerInstance playerInstance2 = this.playerInstancesToUpdate.get(j);
            playerInstance2.onUpdate();
         }
      }

      this.playerInstancesToUpdate.clear();
      if (this.players.isEmpty()) {
         WorldProvider worldProvider = this.theWorldServer.provider;
         if (!worldProvider.canRespawnHere()) {
            this.theWorldServer.theChunkProviderServer.unloadAllChunks();
         }
      }
   }

   public boolean hasPlayerInstance(int chunkX, int chunkZ) {
      long l = chunkX + 2147483647L | chunkZ + 2147483647L << 32;
      return this.playerInstances.getValueByKey(l) != null;
   }

   private PlayerManager.PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean createIfAbsent) {
      long l = chunkX + 2147483647L | chunkZ + 2147483647L << 32;
      PlayerManager.PlayerInstance playerInstance = this.playerInstances.getValueByKey(l);
      if (playerInstance == null && createIfAbsent) {
         playerInstance = new PlayerManager.PlayerInstance(chunkX, chunkZ);
         this.playerInstances.add(l, playerInstance);
         this.playerInstanceList.add(playerInstance);
      }

      return playerInstance;
   }

   public void markBlockForUpdate(BlockPos pos) {
      int i = pos.getX() >> 4;
      int j = pos.getZ() >> 4;
      PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i, j, false);
      if (playerInstance != null) {
         playerInstance.flagChunkForUpdate(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
      }
   }

   public void addPlayer(EntityPlayerMP player) {
      int i = (int)player.posX >> 4;
      int j = (int)player.posZ >> 4;
      player.managedPosX = player.posX;
      player.managedPosZ = player.posZ;

      for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; k++) {
         for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; l++) {
            this.getPlayerInstance(k, l, true).addPlayer(player);
         }
      }

      this.players.add(player);
      this.filterChunkLoadQueue(player);
   }

   public void filterChunkLoadQueue(EntityPlayerMP player) {
      ArrayList arrayList = Lists.newArrayList(player.loadedChunks);
      int i = 0;
      int j = this.playerViewRadius;
      int k = (int)player.posX >> 4;
      int l = (int)player.posZ >> 4;
      int m = 0;
      int n = 0;
      ChunkCoordIntPair chunkCoordIntPair = this.getPlayerInstance(k, l, true).chunkCoords;
      player.loadedChunks.clear();
      if (arrayList.contains(chunkCoordIntPair)) {
         player.loadedChunks.add(chunkCoordIntPair);
      }

      for (int i2 = 1; i2 <= j * 2; i2++) {
         for (int i3 = 0; i3 < 2; i3++) {
            int[] ints = this.xzDirectionsConst[i++ % 4];

            for (int i4 = 0; i4 < i2; i4++) {
               m += ints[0];
               n += ints[1];
               chunkCoordIntPair = this.getPlayerInstance(k + m, l + n, true).chunkCoords;
               if (arrayList.contains(chunkCoordIntPair)) {
                  player.loadedChunks.add(chunkCoordIntPair);
               }
            }
         }
      }

      i %= 4;

      for (int i5 = 0; i5 < j * 2; i5++) {
         m += this.xzDirectionsConst[i][0];
         n += this.xzDirectionsConst[i][1];
         chunkCoordIntPair = this.getPlayerInstance(k + m, l + n, true).chunkCoords;
         if (arrayList.contains(chunkCoordIntPair)) {
            player.loadedChunks.add(chunkCoordIntPair);
         }
      }
   }

   public void removePlayer(EntityPlayerMP player) {
      int i = (int)player.managedPosX >> 4;
      int j = (int)player.managedPosZ >> 4;

      for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; k++) {
         for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; l++) {
            PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(k, l, false);
            if (playerInstance != null) {
               playerInstance.removePlayer(player);
            }
         }
      }

      this.players.remove(player);
   }

   private boolean overlaps(int x1, int z1, int x2, int z2, int radius) {
      int i = x1 - x2;
      int j = z1 - z2;
      return i >= -radius && i <= radius ? j >= -radius && j <= radius : false;
   }

   public void updateMountedMovingPlayer(EntityPlayerMP player) {
      int i = (int)player.posX >> 4;
      int j = (int)player.posZ >> 4;
      double d = player.managedPosX - player.posX;
      double e = player.managedPosZ - player.posZ;
      double d2 = d * d + e * e;
      if (d2 >= 64.0) {
         int k = (int)player.managedPosX >> 4;
         int l = (int)player.managedPosZ >> 4;
         int m = this.playerViewRadius;
         int n = i - k;
         int i2 = j - l;
         if (n != 0 || i2 != 0) {
            for (int i3 = i - m; i3 <= i + m; i3++) {
               for (int i4 = j - m; i4 <= j + m; i4++) {
                  if (!this.overlaps(i3, i4, k, l, m)) {
                     this.getPlayerInstance(i3, i4, true).addPlayer(player);
                  }

                  if (!this.overlaps(i3 - n, i4 - i2, i, j, m)) {
                     PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i3 - n, i4 - i2, false);
                     if (playerInstance != null) {
                        playerInstance.removePlayer(player);
                     }
                  }
               }
            }

            this.filterChunkLoadQueue(player);
            player.managedPosX = player.posX;
            player.managedPosZ = player.posZ;
         }
      }
   }

   public boolean isPlayerWatchingChunk(EntityPlayerMP player, int chunkX, int chunkZ) {
      PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(chunkX, chunkZ, false);
      return playerInstance != null && playerInstance.playersWatchingChunk.contains(player) && !player.loadedChunks.contains(playerInstance.chunkCoords);
   }

   public void setPlayerViewRadius(int radius) {
      radius = MathHelper.clamp_int(radius, 3, 32);
      if (radius != this.playerViewRadius) {
         int i = radius - this.playerViewRadius;

         for (EntityPlayerMP entityPlayerMP : Lists.newArrayList(this.players)) {
            int j = (int)entityPlayerMP.posX >> 4;
            int k = (int)entityPlayerMP.posZ >> 4;
            if (i > 0) {
               for (int l = j - radius; l <= j + radius; l++) {
                  for (int m = k - radius; m <= k + radius; m++) {
                     PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(l, m, true);
                     if (!playerInstance.playersWatchingChunk.contains(entityPlayerMP)) {
                        playerInstance.addPlayer(entityPlayerMP);
                     }
                  }
               }
            } else {
               for (int n = j - this.playerViewRadius; n <= j + this.playerViewRadius; n++) {
                  for (int i2 = k - this.playerViewRadius; i2 <= k + this.playerViewRadius; i2++) {
                     if (!this.overlaps(n, i2, j, k, radius)) {
                        this.getPlayerInstance(n, i2, true).removePlayer(entityPlayerMP);
                     }
                  }
               }
            }
         }

         this.playerViewRadius = radius;
      }
   }

   public static int getFurthestViewableBlock(int distance) {
      return distance * 16 - 16;
   }

   public class PlayerInstance {
      private final List<EntityPlayerMP> playersWatchingChunk = Lists.newArrayList();
      private final ChunkCoordIntPair chunkCoords;
      private short[] locationOfBlockChange = new short[64];
      private int numBlocksToUpdate;
      private int flagsYAreasToUpdate;
      private long previousWorldTime;

      public PlayerInstance(int chunkX, int chunkZ) {
         this.chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
         PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(chunkX, chunkZ);
      }

      public void addPlayer(EntityPlayerMP player) {
         if (this.playersWatchingChunk.contains(player)) {
            PlayerManager.pmLogger.debug("Failed to add player. {} already is in chunk {}, {}", player, this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
         } else {
            if (this.playersWatchingChunk.isEmpty()) {
               this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
            }

            this.playersWatchingChunk.add(player);
            player.loadedChunks.add(this.chunkCoords);
         }
      }

      public void removePlayer(EntityPlayerMP player) {
         if (this.playersWatchingChunk.contains(player)) {
            Chunk chunk = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
            if (chunk.isPopulated()) {
               player.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunk, true, 0));
            }

            this.playersWatchingChunk.remove(player);
            player.loadedChunks.remove(this.chunkCoords);
            if (this.playersWatchingChunk.isEmpty()) {
               long l = this.chunkCoords.chunkXPos + 2147483647L | this.chunkCoords.chunkZPos + 2147483647L << 32;
               this.increaseInhabitedTime(chunk);
               PlayerManager.this.playerInstances.remove(l);
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

      private void increaseInhabitedTime(Chunk theChunk) {
         theChunk.setInhabitedTime(theChunk.getInhabitedTime() + PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
         this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
      }

      public void flagChunkForUpdate(int x, int y, int z) {
         if (this.numBlocksToUpdate == 0) {
            PlayerManager.this.playerInstancesToUpdate.add(this);
         }

         this.flagsYAreasToUpdate |= 1 << (y >> 4);
         if (this.numBlocksToUpdate < 64) {
            short s = (short)(x << 12 | z << 8 | y);

            for (int i = 0; i < this.numBlocksToUpdate; i++) {
               if (this.locationOfBlockChange[i] == s) {
                  return;
               }
            }

            this.locationOfBlockChange[this.numBlocksToUpdate++] = s;
         }
      }

      public void sendToAllPlayersWatchingChunk(Packet thePacket) {
         for (int i = 0; i < this.playersWatchingChunk.size(); i++) {
            EntityPlayerMP entityPlayerMP = this.playersWatchingChunk.get(i);
            if (!entityPlayerMP.loadedChunks.contains(this.chunkCoords)) {
               entityPlayerMP.playerNetServerHandler.sendPacket(thePacket);
            }
         }
      }

      public void onUpdate() {
         if (this.numBlocksToUpdate != 0) {
            if (this.numBlocksToUpdate == 1) {
               int i = (this.locationOfBlockChange[0] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
               int j = this.locationOfBlockChange[0] & 255;
               int k = (this.locationOfBlockChange[0] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
               BlockPos blockPos = new BlockPos(i, j, k);
               this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(PlayerManager.this.theWorldServer, blockPos));
               if (PlayerManager.this.theWorldServer.getBlockState(blockPos).getBlock().hasTileEntity()) {
                  this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockPos));
               }
            } else if (this.numBlocksToUpdate != 64) {
               this.sendToAllPlayersWatchingChunk(
                  new S22PacketMultiBlockChange(
                     this.numBlocksToUpdate,
                     this.locationOfBlockChange,
                     PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos)
                  )
               );

               for (int l = 0; l < this.numBlocksToUpdate; l++) {
                  int m = (this.locationOfBlockChange[l] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
                  int n = this.locationOfBlockChange[l] & 255;
                  int i2 = (this.locationOfBlockChange[l] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
                  BlockPos blockPos2 = new BlockPos(m, n, i2);
                  if (PlayerManager.this.theWorldServer.getBlockState(blockPos2).getBlock().hasTileEntity()) {
                     this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockPos2));
                  }
               }
            } else {
               int i3 = this.chunkCoords.chunkXPos * 16;
               int i4 = this.chunkCoords.chunkZPos * 16;
               this.sendToAllPlayersWatchingChunk(
                  new S21PacketChunkData(
                     PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos),
                     false,
                     this.flagsYAreasToUpdate
                  )
               );

               for (int i5 = 0; i5 < 16; i5++) {
                  if ((this.flagsYAreasToUpdate & 1 << i5) != 0) {
                     int i6 = i5 << 4;
                     List list = PlayerManager.this.theWorldServer.getTileEntitiesIn(i3, i6, i4, i3 + 16, i6 + 16, i4 + 16);

                     for (int i7 = 0; i7 < list.size(); i7++) {
                        this.sendTileToAllPlayersWatchingChunk((TileEntity)list.get(i7));
                     }
                  }
               }
            }

            this.numBlocksToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
         }
      }

      private void sendTileToAllPlayersWatchingChunk(TileEntity theTileEntity) {
         if (theTileEntity != null) {
            Packet packet = theTileEntity.getDescriptionPacket();
            if (packet != null) {
               this.sendToAllPlayersWatchingChunk(packet);
            }
         }
      }
   }
}
