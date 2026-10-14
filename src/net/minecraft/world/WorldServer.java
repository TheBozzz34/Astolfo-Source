package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ReportedException;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener {
   private static final Logger logger = LogManager.getLogger();
   private final MinecraftServer mcServer;
   private final EntityTracker theEntityTracker;
   private final PlayerManager thePlayerManager;
   private final Set<NextTickListEntry> pendingTickListEntriesHashSet = Sets.newHashSet();
   private final TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet = new TreeSet<>();
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   public ChunkProviderServer theChunkProviderServer;
   public boolean disableLevelSaving;
   private boolean allPlayersSleeping;
   private int updateEntityTick;
   private final Teleporter worldTeleporter;
   private final SpawnerAnimals mobSpawner = new SpawnerAnimals();
   public final VillageSiege villageSiege = new VillageSiege(this);
   private WorldServer.ServerBlockEventList[] blockEventQueue = new WorldServer.ServerBlockEventList[]{
      new WorldServer.ServerBlockEventList(), new WorldServer.ServerBlockEventList()
   };
   private int blockEventCacheIndex;
   private static final List<WeightedRandomChestContent> bonusChestContent = Lists.newArrayList(
      new WeightedRandomChestContent(Items.stick, 0, 1, 3, 10),
      new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.planks), 0, 1, 3, 10),
      new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 1, 3, 10),
      new WeightedRandomChestContent(Items.stone_axe, 0, 1, 1, 3),
      new WeightedRandomChestContent(Items.wooden_axe, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.stone_pickaxe, 0, 1, 1, 3),
      new WeightedRandomChestContent(Items.wooden_pickaxe, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.apple, 0, 2, 3, 5),
      new WeightedRandomChestContent(Items.bread, 0, 2, 3, 3),
      new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log2), 0, 1, 3, 10)
   );
   private List<NextTickListEntry> pendingTickListEntriesThisTick = Lists.newArrayList();

   public WorldServer(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn) {
      super(saveHandlerIn, info, WorldProvider.getProviderForDimension(dimensionId), profilerIn, false);
      this.mcServer = server;
      this.theEntityTracker = new EntityTracker(this);
      this.thePlayerManager = new PlayerManager(this);
      this.provider.registerWorld(this);
      this.chunkProvider = this.createChunkProvider();
      this.worldTeleporter = new Teleporter(this);
      this.a();
      this.H();
      this.getWorldBorder().setSize(server.getMaxWorldSize());
   }

   @Override
   public World init() {
      this.mapStorage = new MapStorage(this.saveHandler);
      String string = VillageCollection.fileNameForProvider(this.provider);
      VillageCollection villageCollection = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, string);
      if (villageCollection == null) {
         this.villageCollectionObj = new VillageCollection(this);
         this.mapStorage.setData(string, this.villageCollectionObj);
      } else {
         this.villageCollectionObj = villageCollection;
         this.villageCollectionObj.setWorldsForAll(this);
      }

      this.worldScoreboard = new ServerScoreboard(this.mcServer);
      ScoreboardSaveData scoreboardSaveData = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");
      if (scoreboardSaveData == null) {
         scoreboardSaveData = new ScoreboardSaveData();
         this.mapStorage.setData("scoreboard", scoreboardSaveData);
      }

      scoreboardSaveData.setScoreboard(this.worldScoreboard);
      ((ServerScoreboard)this.worldScoreboard).func_96547_a(scoreboardSaveData);
      this.getWorldBorder().setCenter(this.worldInfo.getBorderCenterX(), this.worldInfo.getBorderCenterZ());
      this.getWorldBorder().setDamageAmount(this.worldInfo.getBorderDamagePerBlock());
      this.getWorldBorder().setDamageBuffer(this.worldInfo.getBorderSafeZone());
      this.getWorldBorder().setWarningDistance(this.worldInfo.getBorderWarningDistance());
      this.getWorldBorder().setWarningTime(this.worldInfo.getBorderWarningTime());
      if (this.worldInfo.getBorderLerpTime() > 0L) {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize(), this.worldInfo.getBorderLerpTarget(), this.worldInfo.getBorderLerpTime());
      } else {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize());
      }

      return this;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getWorldInfo().isHardcoreModeEnabled() && this.getDifficulty() != EnumDifficulty.HARD) {
         this.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
      }

      this.provider.getWorldChunkManager().cleanupCache();
      if (this.areAllPlayersAsleep()) {
         if (this.getGameRules().getBoolean("doDaylightCycle")) {
            long l = this.worldInfo.getWorldTime() + 24000L;
            this.worldInfo.setWorldTime(l - l % 24000L);
         }

         this.wakeAllPlayers();
      }

      this.theProfiler.startSection("mobSpawner");
      if (this.getGameRules().getBoolean("doMobSpawning") && this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
         this.mobSpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
      }

      this.theProfiler.endStartSection("chunkSource");
      this.chunkProvider.unloadQueuedChunks();
      int i = this.calculateSkylightSubtracted(1.0F);
      if (i != this.getSkylightSubtracted()) {
         this.setSkylightSubtracted(i);
      }

      this.worldInfo.setWorldTotalTime(this.worldInfo.getWorldTotalTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
      }

      this.theProfiler.endStartSection("tickPending");
      this.tickUpdates(false);
      this.theProfiler.endStartSection("tickBlocks");
      this.updateBlocks();
      this.theProfiler.endStartSection("chunkMap");
      this.thePlayerManager.updatePlayerInstances();
      this.theProfiler.endStartSection("village");
      this.villageCollectionObj.tick();
      this.villageSiege.tick();
      this.theProfiler.endStartSection("portalForcer");
      this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
      this.theProfiler.endSection();
      this.sendQueuedBlockEvents();
   }

   public BiomeGenBase.SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos) {
      List list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
      return list != null && !list.isEmpty() ? (net.minecraft.world.biome.BiomeGenBase.SpawnListEntry)(WeightedRandom.getRandomItem(this.rand, list)) : null;
   }

   public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, BiomeGenBase.SpawnListEntry spawnListEntry, BlockPos pos) {
      List list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
      return list != null && !list.isEmpty() ? list.contains(spawnListEntry) : false;
   }

   @Override
   public void updateAllPlayersSleepingFlag() {
      this.allPlayersSleeping = false;
      if (!this.playerEntities.isEmpty()) {
         int i = 0;
         int j = 0;

         for (EntityPlayer entityPlayer : this.playerEntities) {
            if (entityPlayer.isSpectator()) {
               i++;
            } else if (entityPlayer.isPlayerSleeping()) {
               j++;
            }
         }

         this.allPlayersSleeping = j > 0 && j >= this.playerEntities.size() - i;
      }
   }

   public void wakeAllPlayers() {
      this.allPlayersSleeping = false;

      for (EntityPlayer entityPlayer : this.playerEntities) {
         if (entityPlayer.isPlayerSleeping()) {
            entityPlayer.wakeUpPlayer(false, false, true);
         }
      }

      this.resetRainAndThunder();
   }

   private void resetRainAndThunder() {
      this.worldInfo.setRainTime(0);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThunderTime(0);
      this.worldInfo.setThundering(false);
   }

   public boolean areAllPlayersAsleep() {
      if (this.allPlayersSleeping && !this.isRemote) {
         for (EntityPlayer entityPlayer : this.playerEntities) {
            if (entityPlayer.isSpectator() || !entityPlayer.isPlayerFullyAsleep()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setInitialSpawnLocation() {
      if (this.worldInfo.getSpawnY() <= 0) {
         this.worldInfo.setSpawnY(this.getSeaLevel() + 1);
      }

      int i = this.worldInfo.getSpawnX();
      int j = this.worldInfo.getSpawnZ();
      int k = 0;

      while (this.getGroundAboveSeaLevel(new BlockPos(i, 0, j)).getMaterial() == Material.air) {
         i += this.rand.nextInt(8) - this.rand.nextInt(8);
         j += this.rand.nextInt(8) - this.rand.nextInt(8);
         if (++k == 10000) {
            break;
         }
      }

      this.worldInfo.setSpawnX(i);
      this.worldInfo.setSpawnZ(j);
   }

   @Override
   public void updateBlocks() {
      super.updateBlocks();
      if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         for (ChunkCoordIntPair chunkCoordIntPair : this.activeChunkSet) {
            this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos).func_150804_b(false);
         }
      } else {
         int i = 0;
         int j = 0;

         for (ChunkCoordIntPair chunkCoordIntPair2 : this.activeChunkSet) {
            int k = chunkCoordIntPair2.chunkXPos * 16;
            int l = chunkCoordIntPair2.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunk = this.getChunkFromChunkCoords(chunkCoordIntPair2.chunkXPos, chunkCoordIntPair2.chunkZPos);
            this.playMoodSoundAndCheckLight(k, l, chunk);
            this.theProfiler.endStartSection("tickChunk");
            chunk.func_150804_b(false);
            this.theProfiler.endStartSection("thunder");
            if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int m = this.updateLCG >> 2;
               BlockPos blockPos = this.adjustPosToNearbyEntity(new BlockPos(k + (m & 15), 0, l + (m >> 8 & 15)));
               if (this.isRainingAt(blockPos)) {
                  this.addWeatherEffect(new EntityLightningBolt(this, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
               }
            }

            this.theProfiler.endStartSection("iceandsnow");
            if (this.rand.nextInt(16) == 0) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int n = this.updateLCG >> 2;
               BlockPos blockPos2 = this.getPrecipitationHeight(new BlockPos(k + (n & 15), 0, l + (n >> 8 & 15)));
               BlockPos blockPos3 = blockPos2.down();
               if (this.canBlockFreezeNoWater(blockPos3)) {
                  this.setBlockState(blockPos3, Blocks.ice.getDefaultState());
               }

               if (this.isRaining() && this.canSnowAt(blockPos2, true)) {
                  this.setBlockState(blockPos2, Blocks.snow_layer.getDefaultState());
               }

               if (this.isRaining() && this.getBiomeGenForCoords(blockPos3).canRain()) {
                  this.getBlockState(blockPos3).getBlock().fillWithRain(this, blockPos3);
               }
            }

            this.theProfiler.endStartSection("tickBlocks");
            int i2 = this.getGameRules().getInt("randomTickSpeed");
            if (i2 > 0) {
               for (ExtendedBlockStorage extendedBlockStorage : chunk.getBlockStorageArray()) {
                  if (extendedBlockStorage != null && extendedBlockStorage.getNeedsRandomTick()) {
                     for (int i3 = 0; i3 < i2; i3++) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int i4 = this.updateLCG >> 2;
                        int i5 = i4 & 15;
                        int i6 = i4 >> 8 & 15;
                        int i7 = i4 >> 16 & 15;
                        j++;
                        IBlockState iBlockState = extendedBlockStorage.get(i5, i7, i6);
                        Block block = iBlockState.getBlock();
                        if (block.getTickRandomly()) {
                           i++;
                           block.randomTick(this, new BlockPos(i5 + k, i7 + extendedBlockStorage.getYLocation(), i6 + l), iBlockState, this.rand);
                        }
                     }
                  }
               }
            }

            this.theProfiler.endSection();
         }
      }
   }

   public BlockPos adjustPosToNearbyEntity(BlockPos pos) {
      BlockPos blockPos = this.getPrecipitationHeight(pos);
      AxisAlignedBB axisAlignedBB = new AxisAlignedBB(blockPos, new BlockPos(blockPos.getX(), this.getHeight(), blockPos.getZ())).expand(3.0, 3.0, 3.0);
      List list = this.getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB, new Predicate<EntityLivingBase>() {
         public boolean apply(EntityLivingBase entityLivingBase) {
            return entityLivingBase != null && entityLivingBase.isEntityAlive() && WorldServer.this.canSeeSky(entityLivingBase.getPosition());
         }
      });
      return !list.isEmpty() ? ((EntityLivingBase)list.get(this.rand.nextInt(list.size()))).getPosition() : blockPos;
   }

   @Override
   public boolean isBlockTickPending(BlockPos pos, Block blockType) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockType);
      return this.pendingTickListEntriesThisTick.contains(nextTickListEntry);
   }

   @Override
   public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
      this.updateBlockTick(pos, blockIn, delay, 0);
   }

   @Override
   public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockIn);
      byte b = 0;
      if (this.scheduledUpdatesAreImmediate && blockIn.getMaterial() != Material.air) {
         if (blockIn.requiresUpdates()) {
            b = 8;
            if (this.isAreaLoaded(nextTickListEntry.position.add(-b, -b, -b), nextTickListEntry.position.add(b, b, b))) {
               IBlockState iBlockState = this.getBlockState(nextTickListEntry.position);
               if (iBlockState.getBlock().getMaterial() != Material.air && iBlockState.getBlock() == nextTickListEntry.getBlock()) {
                  iBlockState.getBlock().updateTick(this, nextTickListEntry.position, iBlockState, this.rand);
               }
            }

            return;
         }

         delay = 1;
      }

      if (this.isAreaLoaded(pos.add(-b, -b, -b), pos.add(b, b, b))) {
         if (blockIn.getMaterial() != Material.air) {
            nextTickListEntry.setScheduledTime(delay + this.worldInfo.getWorldTotalTime());
            nextTickListEntry.setPriority(priority);
         }

         if (!this.pendingTickListEntriesHashSet.contains(nextTickListEntry)) {
            this.pendingTickListEntriesHashSet.add(nextTickListEntry);
            this.pendingTickListEntriesTreeSet.add(nextTickListEntry);
         }
      }
   }

   @Override
   public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockIn);
      nextTickListEntry.setPriority(priority);
      if (blockIn.getMaterial() != Material.air) {
         nextTickListEntry.setScheduledTime(delay + this.worldInfo.getWorldTotalTime());
      }

      if (!this.pendingTickListEntriesHashSet.contains(nextTickListEntry)) {
         this.pendingTickListEntriesHashSet.add(nextTickListEntry);
         this.pendingTickListEntriesTreeSet.add(nextTickListEntry);
      }
   }

   @Override
   public void updateEntities() {
      if (this.playerEntities.isEmpty()) {
         if (this.updateEntityTick++ >= 1200) {
            return;
         }
      } else {
         this.resetUpdateEntityTick();
      }

      super.updateEntities();
   }

   public void resetUpdateEntityTick() {
      this.updateEntityTick = 0;
   }

   @Override
   public boolean tickUpdates(boolean bl) {
      if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         return false;
      }

      int i = this.pendingTickListEntriesTreeSet.size();
      if (i != this.pendingTickListEntriesHashSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      }

      if (i > 1000) {
         i = 1000;
      }

      this.theProfiler.startSection("cleaning");

      for (int j = 0; j < i; j++) {
         NextTickListEntry nextTickListEntry = this.pendingTickListEntriesTreeSet.first();
         if (!bl && nextTickListEntry.scheduledTime > this.worldInfo.getWorldTotalTime()) {
            break;
         }

         this.pendingTickListEntriesTreeSet.remove(nextTickListEntry);
         this.pendingTickListEntriesHashSet.remove(nextTickListEntry);
         this.pendingTickListEntriesThisTick.add(nextTickListEntry);
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("ticking");
      Iterator iterator2 = this.pendingTickListEntriesThisTick.iterator();

      while (iterator2.hasNext()) {
         NextTickListEntry nextTickListEntry2 = (NextTickListEntry)iterator2.next();
         iterator2.remove();
         byte b = 0;
         if (this.isAreaLoaded(nextTickListEntry2.position.add(-b, -b, -b), nextTickListEntry2.position.add(b, b, b))) {
            IBlockState iBlockState = this.getBlockState(nextTickListEntry2.position);
            if (iBlockState.getBlock().getMaterial() != Material.air && Block.isEqualTo(iBlockState.getBlock(), nextTickListEntry2.getBlock())) {
               try {
                  iBlockState.getBlock().updateTick(this, nextTickListEntry2.position, iBlockState, this.rand);
               } catch (Throwable throwable) {
                  CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception while ticking a block");
                  CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being ticked");
                  CrashReportCategory.addBlockInfo(crashReportCategory, nextTickListEntry2.position, iBlockState);
                  throw new ReportedException(crashReport);
               }
            }
         } else {
            this.scheduleUpdate(nextTickListEntry2.position, nextTickListEntry2.getBlock(), 0);
         }
      }

      this.theProfiler.endSection();
      this.pendingTickListEntriesThisTick.clear();
      return !this.pendingTickListEntriesTreeSet.isEmpty();
   }

   @Override
   public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean bl) {
      ChunkCoordIntPair chunkCoordIntPair = chunkIn.getChunkCoordIntPair();
      int i = (chunkCoordIntPair.chunkXPos << 4) - 2;
      int j = i + 16 + 2;
      int k = (chunkCoordIntPair.chunkZPos << 4) - 2;
      int l = k + 16 + 2;
      return this.func_175712_a(new StructureBoundingBox(i, 0, k, j, 256, l), bl);
   }

   @Override
   public List<NextTickListEntry> func_175712_a(StructureBoundingBox structureBB, boolean bl) {
      ArrayList arrayList = null;

      for (int i = 0; i < 2; i++) {
         Iterator iterator2;
         if (i == 0) {
            iterator2 = this.pendingTickListEntriesTreeSet.iterator();
         } else {
            iterator2 = this.pendingTickListEntriesThisTick.iterator();
         }

         while (iterator2.hasNext()) {
            NextTickListEntry nextTickListEntry = (NextTickListEntry)iterator2.next();
            BlockPos blockPos = nextTickListEntry.position;
            if (blockPos.getX() >= structureBB.minX && blockPos.getX() < structureBB.maxX && blockPos.getZ() >= structureBB.minZ && blockPos.getZ() < structureBB.maxZ) {
               if (bl) {
                  this.pendingTickListEntriesHashSet.remove(nextTickListEntry);
                  iterator2.remove();
               }

               if (arrayList == null) {
                  arrayList = Lists.newArrayList();
               }

               arrayList.add(nextTickListEntry);
            }
         }
      }

      return arrayList;
   }

   @Override
   public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
      if (!this.canSpawnAnimals() && (entityIn instanceof EntityAnimal || entityIn instanceof EntityWaterMob)) {
         entityIn.setDead();
      }

      if (!this.canSpawnNPCs() && entityIn instanceof INpc) {
         entityIn.setDead();
      }

      super.updateEntityWithOptionalForce(entityIn, forceUpdate);
   }

   private boolean canSpawnNPCs() {
      return this.mcServer.getCanSpawnNPCs();
   }

   private boolean canSpawnAnimals() {
      return this.mcServer.getCanSpawnAnimals();
   }

   @Override
   public IChunkProvider createChunkProvider() {
      IChunkLoader iChunkLoader = this.saveHandler.getChunkLoader(this.provider);
      this.theChunkProviderServer = new ChunkProviderServer(this, iChunkLoader, this.provider.createChunkGenerator());
      return this.theChunkProviderServer;
   }

   public List<TileEntity> getTileEntitiesIn(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < this.loadedTileEntityList.size(); i++) {
         TileEntity tileEntity = this.loadedTileEntityList.get(i);
         BlockPos blockPos = tileEntity.getPos();
         if (blockPos.getX() >= minX && blockPos.getY() >= minY && blockPos.getZ() >= minZ && blockPos.getX() < maxX && blockPos.getY() < maxY && blockPos.getZ() < maxZ) {
            arrayList.add(tileEntity);
         }
      }

      return arrayList;
   }

   @Override
   public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
      return !this.mcServer.isBlockProtected(this, pos, player) && this.getWorldBorder().contains(pos);
   }

   @Override
   public void initialize(WorldSettings settings) {
      if (!this.worldInfo.isInitialized()) {
         try {
            this.createSpawnPosition(settings);
            if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
               this.setDebugWorldSettings();
            }

            super.initialize(settings);
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

            try {
               this.addWorldInfoToCrashReport(crashReport);
            } catch (Throwable throwable2) {
            }

            throw new ReportedException(crashReport);
         }

         this.worldInfo.setServerInitialized(true);
      }
   }

   private void setDebugWorldSettings() {
      this.worldInfo.setMapFeaturesEnabled(false);
      this.worldInfo.setAllowCommands(true);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThundering(false);
      this.worldInfo.setCleanWeatherTime(1000000000);
      this.worldInfo.setWorldTime(6000L);
      this.worldInfo.setGameType(WorldSettings.GameType.SPECTATOR);
      this.worldInfo.setHardcore(false);
      this.worldInfo.setDifficulty(EnumDifficulty.PEACEFUL);
      this.worldInfo.setDifficultyLocked(true);
      this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
   }

   private void createSpawnPosition(WorldSettings settings) {
      if (!this.provider.canRespawnHere()) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up(this.provider.getAverageGroundLevel()));
      } else if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up());
      } else {
         this.findingSpawnPoint = true;
         WorldChunkManager worldChunkManager = this.provider.getWorldChunkManager();
         List list = worldChunkManager.getBiomesToSpawnIn();
         Random random = new Random(this.getSeed());
         BlockPos blockPos = worldChunkManager.findBiomePosition(0, 0, 256, list, random);
         int i = 0;
         int j = this.provider.getAverageGroundLevel();
         int k = 0;
         if (blockPos != null) {
            i = blockPos.getX();
            k = blockPos.getZ();
         } else {
            logger.warn("Unable to find spawn biome");
         }

         int l = 0;

         while (!this.provider.canCoordinateBeSpawn(i, k)) {
            i += random.nextInt(64) - random.nextInt(64);
            k += random.nextInt(64) - random.nextInt(64);
            if (++l == 1000) {
               break;
            }
         }

         this.worldInfo.setSpawn(new BlockPos(i, j, k));
         this.findingSpawnPoint = false;
         if (settings.isBonusChestEnabled()) {
            this.createBonusChest();
         }
      }
   }

   public void createBonusChest() {
      WorldGeneratorBonusChest worldGeneratorBonusChest = new WorldGeneratorBonusChest(bonusChestContent, 10);

      for (int i = 0; i < 10; i++) {
         int j = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
         int k = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
         BlockPos blockPos = this.getTopSolidOrLiquidBlock(new BlockPos(j, 0, k)).up();
         if (worldGeneratorBonusChest.generate(this, this.rand, blockPos)) {
            break;
         }
      }
   }

   public BlockPos getSpawnCoordinate() {
      return this.provider.getSpawnCoordinate();
   }

   public void saveAllChunks(boolean bl, IProgressUpdate progressCallback) throws MinecraftException {
      if (this.chunkProvider.canSave()) {
         if (progressCallback != null) {
            progressCallback.displaySavingString("Saving level");
         }

         this.saveLevel();
         if (progressCallback != null) {
            progressCallback.displayLoadingString("Saving chunks");
         }

         this.chunkProvider.saveChunks(bl, progressCallback);

         for (Chunk chunk : Lists.newArrayList(this.theChunkProviderServer.func_152380_a())) {
            if (chunk != null && !this.thePlayerManager.hasPlayerInstance(chunk.xPosition, chunk.zPosition)) {
               this.theChunkProviderServer.dropChunk(chunk.xPosition, chunk.zPosition);
            }
         }
      }
   }

   public void saveChunkData() {
      if (this.chunkProvider.canSave()) {
         this.chunkProvider.saveExtraData();
      }
   }

   public void saveLevel() throws MinecraftException {
      this.checkSessionLock();
      this.worldInfo.setBorderSize(this.getWorldBorder().getDiameter());
      this.worldInfo.getBorderCenterX(this.getWorldBorder().getCenterX());
      this.worldInfo.getBorderCenterZ(this.getWorldBorder().getCenterZ());
      this.worldInfo.setBorderSafeZone(this.getWorldBorder().getDamageBuffer());
      this.worldInfo.setBorderDamagePerBlock(this.getWorldBorder().getDamageAmount());
      this.worldInfo.setBorderWarningDistance(this.getWorldBorder().getWarningDistance());
      this.worldInfo.setBorderWarningTime(this.getWorldBorder().getWarningTime());
      this.worldInfo.setBorderLerpTarget(this.getWorldBorder().getTargetSize());
      this.worldInfo.setBorderLerpTime(this.getWorldBorder().getTimeUntilTarget());
      this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
      this.mapStorage.saveAllData();
   }

   @Override
   public void onEntityAdded(Entity entityIn) {
      super.onEntityAdded(entityIn);
      this.entitiesById.addKey(entityIn.getEntityId(), entityIn);
      this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
      Entity[] entitys = entityIn.getParts();
      if (entitys != null) {
         for (int i = 0; i < entitys.length; i++) {
            this.entitiesById.addKey(entitys[i].getEntityId(), entitys[i]);
         }
      }
   }

   @Override
   public void onEntityRemoved(Entity entityIn) {
      super.onEntityRemoved(entityIn);
      this.entitiesById.removeObject(entityIn.getEntityId());
      this.entitiesByUuid.remove(entityIn.getUniqueID());
      Entity[] entitys = entityIn.getParts();
      if (entitys != null) {
         for (int i = 0; i < entitys.length; i++) {
            this.entitiesById.removeObject(entitys[i].getEntityId());
         }
      }
   }

   @Override
   public boolean addWeatherEffect(Entity entityIn) {
      if (super.addWeatherEffect(entityIn)) {
         this.mcServer
            .getConfigurationManager()
            .sendToAllNear(entityIn.posX, entityIn.posY, entityIn.posZ, 512.0, this.provider.getDimensionId(), new S2CPacketSpawnGlobalEntity(entityIn));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setEntityState(Entity entityIn, byte state) {
      this.getEntityTracker().func_151248_b(entityIn, new S19PacketEntityStatus(entityIn, state));
   }

   @Override
   public Explosion newExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
      Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
      explosion.doExplosionA();
      explosion.doExplosionB(false);
      if (!isSmoking) {
         explosion.clearAffectedBlockPositions();
      }

      for (EntityPlayer entityPlayer : this.playerEntities) {
         if (entityPlayer.getDistanceSq(x, y, z) < 4096.0) {
            ((EntityPlayerMP)entityPlayer)
               .playerNetServerHandler
               .sendPacket(new S27PacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(entityPlayer)));
         }
      }

      return explosion;
   }

   @Override
   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
      BlockEventData blockEventData = new BlockEventData(pos, blockIn, eventID, eventParam);

      for (BlockEventData blockEventData2 : this.blockEventQueue[this.blockEventCacheIndex]) {
         if (blockEventData2.equals(blockEventData)) {
            return;
         }
      }

      this.blockEventQueue[this.blockEventCacheIndex].add(blockEventData);
   }

   private void sendQueuedBlockEvents() {
      while (!this.blockEventQueue[this.blockEventCacheIndex].isEmpty()) {
         int i = this.blockEventCacheIndex;
         this.blockEventCacheIndex ^= 1;

         for (BlockEventData blockEventData : this.blockEventQueue[i]) {
            if (this.fireBlockEvent(blockEventData)) {
               this.mcServer
                  .getConfigurationManager()
                  .sendToAllNear(
                     blockEventData.getPosition().getX(),
                     blockEventData.getPosition().getY(),
                     blockEventData.getPosition().getZ(),
                     64.0,
                     this.provider.getDimensionId(),
                     new S24PacketBlockAction(blockEventData.getPosition(), blockEventData.getBlock(), blockEventData.getEventID(), blockEventData.getEventParameter())
                  );
            }
         }

         this.blockEventQueue[i].clear();
      }
   }

   private boolean fireBlockEvent(BlockEventData event) {
      IBlockState iBlockState = this.getBlockState(event.getPosition());
      return iBlockState.getBlock() == event.getBlock()
         ? iBlockState.getBlock().onBlockEventReceived(this, event.getPosition(), iBlockState, event.getEventID(), event.getEventParameter())
         : false;
   }

   public void flush() {
      this.saveHandler.flush();
   }

   @Override
   public void updateWeather() {
      boolean bl = this.isRaining();
      super.updateWeather();
      if (this.prevRainingStrength != this.rainingStrength) {
         this.mcServer
            .getConfigurationManager()
            .sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.getDimensionId());
      }

      if (this.prevThunderingStrength != this.thunderingStrength) {
         this.mcServer
            .getConfigurationManager()
            .sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.getDimensionId());
      }

      if (bl != this.isRaining()) {
         if (bl) {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
         } else {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(1, 0.0F));
         }

         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, this.rainingStrength));
         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, this.thunderingStrength));
      }
   }

   @Override
   public int getRenderDistanceChunks() {
      return this.mcServer.getConfigurationManager().getViewDistance();
   }

   public MinecraftServer getMinecraftServer() {
      return this.mcServer;
   }

   public EntityTracker getEntityTracker() {
      return this.theEntityTracker;
   }

   public PlayerManager getPlayerManager() {
      return this.thePlayerManager;
   }

   public Teleporter getDefaultTeleporter() {
      return this.worldTeleporter;
   }

   public void spawnParticle(
      EnumParticleTypes particleType,
      double xCoord,
      double yCoord,
      double zCoord,
      int numberOfParticles,
      double xOffset,
      double yOffset,
      double zOffset,
      double particleSpeed,
      int... particleArguments
   ) {
      this.spawnParticle(particleType, false, xCoord, yCoord, zCoord, numberOfParticles, xOffset, yOffset, zOffset, particleSpeed, particleArguments);
   }

   public void spawnParticle(
      EnumParticleTypes particleType,
      boolean longDistance,
      double xCoord,
      double yCoord,
      double zCoord,
      int numberOfParticles,
      double xOffset,
      double yOffset,
      double zOffset,
      double particleSpeed,
      int... particleArguments
   ) {
      S2APacketParticles s2APacketParticles = new S2APacketParticles(
         particleType,
         longDistance,
         (float)xCoord,
         (float)yCoord,
         (float)zCoord,
         (float)xOffset,
         (float)yOffset,
         (float)zOffset,
         (float)particleSpeed,
         numberOfParticles,
         particleArguments
      );

      for (int i = 0; i < this.playerEntities.size(); i++) {
         EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntities.get(i);
         BlockPos blockPos = entityPlayerMP.getPosition();
         double d = blockPos.distanceSq(xCoord, yCoord, zCoord);
         if (d <= 256.0 || longDistance && d <= 65536.0) {
            entityPlayerMP.playerNetServerHandler.sendPacket(s2APacketParticles);
         }
      }
   }

   public Entity getEntityFromUuid(UUID uuid) {
      return this.entitiesByUuid.get(uuid);
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      return this.mcServer.addScheduledTask(runnableToSchedule);
   }

   @Override
   public boolean isCallingFromMinecraftThread() {
      return this.mcServer.isCallingFromMinecraftThread();
   }

   public static class ServerBlockEventList extends ArrayList<BlockEventData> {
      public ServerBlockEventList() {
      }
   }
}
