package net.minecraft.client.renderer.chunk;

import bypass.module.ModuleManager;
import bypass.module.visuals.Xray;
import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.src.BlockPosM;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.src.ReflectorForge;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import shadersmod.client.SVertexBuilder;

public class RenderChunk {
   private World world;
   private final RenderGlobal renderGlobal;
   public static int renderChunksUpdated;
   private BlockPos position;
   public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
   private final ReentrantLock lockCompileTask = new ReentrantLock();
   private final ReentrantLock lockCompiledChunk = new ReentrantLock();
   private ChunkCompileTaskGenerator compileTask = null;
   private final Set<TileEntity> setTileEntities = Sets.newHashSet();
   private final int index;
   private final FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
   private final VertexBuffer[] vertexBuffers = new VertexBuffer[EnumWorldBlockLayer.values().length];
   public AxisAlignedBB boundingBox;
   private int frameIndex = -1;
   private boolean j = true;
   private EnumMap<EnumFacing, BlockPos> mapEnumFacing;
   private BlockPos[] positionOffsets16 = new BlockPos[EnumFacing.VALUES.length];
   private static EnumWorldBlockLayer[] ENUM_WORLD_BLOCK_LAYERS = EnumWorldBlockLayer.values();
   private EnumWorldBlockLayer[] blockLayersSingle = new EnumWorldBlockLayer[1];
   private boolean isMipmaps = Config.pm();
   private boolean fixBlockLayer = !Reflector.BetterFoliageClient.exists();
   private boolean playerUpdate = false;
   private static final long e = 74437678023409L;

   public RenderChunk(World worldIn, RenderGlobal renderGlobalIn, BlockPos blockPosIn, int indexIn) {
      this.world = worldIn;
      this.renderGlobal = renderGlobalIn;
      this.index = indexIn;
      if (!blockPosIn.equals(this.getPosition())) {
         this.setPosition(blockPosIn);
      }

      if (OpenGlHelper.useVbo()) {
         for (int i = 0; i < EnumWorldBlockLayer.values().length; i++) {
            this.vertexBuffers[i] = new VertexBuffer(DefaultVertexFormats.BLOCK);
         }
      }
   }

   public boolean setFrameIndex(int frameIndexIn) {
      if (this.frameIndex == frameIndexIn) {
         return false;
      }

      this.frameIndex = frameIndexIn;
      return true;
   }

   public VertexBuffer getVertexBufferByLayer(int layer) {
      return this.vertexBuffers[layer];
   }

   public void setPosition(BlockPos pos) {
      this.stopCompileTask();
      this.position = pos;
      this.boundingBox = new AxisAlignedBB(pos, pos.add(16, 16, 16));
      this.initModelviewMatrix();

      for (int i = 0; i < this.positionOffsets16.length; i++) {
         this.positionOffsets16[i] = null;
      }
   }

   public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator) {
      CompiledChunk compiledChunk = generator.getCompiledChunk();
      if (compiledChunk.getState() != null && !compiledChunk.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT)) {
         WorldRenderer worldRenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT);
         this.preRenderBlocks(worldRenderer, this.position);
         worldRenderer.setVertexState(compiledChunk.getState());
         this.postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, x, y, z, worldRenderer, compiledChunk);
      }
   }

   public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
      CompiledChunk compiledChunk = new CompiledChunk();
      BlockPos blockPos = this.position;
      BlockPos blockPos2 = blockPos.add(15, 15, 15);
      generator.getLock().lock();

      RegionRenderCache regionRenderCache;
      try {
         if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
            return;
         }

         if (this.world == null) {
            return;
         }

         regionRenderCache = this.createRegionRenderCache(this.world, blockPos.add(-1, -1, -1), blockPos2.add(1, 1, 1), 1);
         if (Reflector.MinecraftForgeClient_onRebuildChunk.exists()) {
            Reflector.call(Reflector.MinecraftForgeClient_onRebuildChunk, this.world, this.position, regionRenderCache);
         }

         generator.setCompiledChunk(compiledChunk);
      } finally {
         generator.getLock().unlock();
      }

      VisGraph visGraph = new VisGraph();
      HashSet hashSet = Sets.newHashSet();
      if (!regionRenderCache.extendedLevelsInChunkCache()) {
         renderChunksUpdated++;
         boolean[] booleans = new boolean[ENUM_WORLD_BLOCK_LAYERS.length];
         BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
         Iterator iterator2 = BlockPosM.m(blockPos, blockPos2).iterator();
         boolean bl = Reflector.ForgeBlock_hasTileEntity.exists();
         boolean bl2 = Reflector.ForgeBlock_canRenderInLayer.exists();
         boolean bl3 = Reflector.ForgeHooksClient_setRenderLayer.exists();

         while (iterator2.hasNext()) {
            BlockPosM blockPosM = (BlockPosM)iterator2.next();
            IBlockState iBlockState = regionRenderCache.getBlockState(blockPosM);
            Block block = iBlockState.getBlock();
            if (block.isOpaqueCube()) {
               visGraph.func_178606_a(blockPosM);
            }

            if (ReflectorForge.blockHasTileEntity(iBlockState)) {
               TileEntity tileEntity = regionRenderCache.getTileEntity(new BlockPos(blockPosM));
               TileEntitySpecialRenderer tileEntitySpecialRenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileEntity);
               if (tileEntity != null && tileEntitySpecialRenderer != null) {
                  compiledChunk.addTileEntity(tileEntity);
                  if (tileEntitySpecialRenderer.forceTileEntityRender()) {
                     hashSet.add(tileEntity);
                  }
               }
            }

            EnumWorldBlockLayer[] enumWorldBlockLayers;
            if (bl2) {
               enumWorldBlockLayers = ENUM_WORLD_BLOCK_LAYERS;
            } else {
               enumWorldBlockLayers = this.blockLayersSingle;
               enumWorldBlockLayers[0] = ModuleManager.s.isEnabled() && !Xray.isEnabledOre(block) ? EnumWorldBlockLayer.TRANSLUCENT : block.getBlockLayer();
            }

            for (int i = 0; i < enumWorldBlockLayers.length; i++) {
               EnumWorldBlockLayer enumWorldBlockLayer = enumWorldBlockLayers[i];
               if (bl2) {
                  boolean bl4 = Reflector.callBoolean(block, Reflector.ForgeBlock_canRenderInLayer, enumWorldBlockLayer);
                  if (!bl4) {
                     continue;
                  }
               }

               if (bl3) {
                  Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, enumWorldBlockLayer);
               }

               if (this.fixBlockLayer) {
                  enumWorldBlockLayer = this.fixBlockLayer(block, enumWorldBlockLayer);
               }

               int j = enumWorldBlockLayer.ordinal();
               if (block.getRenderType() != -1) {
                  WorldRenderer worldRenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);
                  worldRenderer.setBlockLayer(enumWorldBlockLayer);
                  if (!compiledChunk.isLayerStarted(enumWorldBlockLayer)) {
                     compiledChunk.G(enumWorldBlockLayer);
                     this.preRenderBlocks(worldRenderer, blockPos);
                  }

                  booleans[j] |= blockRendererDispatcher.renderBlock(iBlockState, blockPosM, regionRenderCache, worldRenderer);
               }
            }
         }

         for (EnumWorldBlockLayer enumWorldBlockLayer2 : ENUM_WORLD_BLOCK_LAYERS) {
            if (booleans[enumWorldBlockLayer2.ordinal()]) {
               compiledChunk.q(enumWorldBlockLayer2);
            }

            if (compiledChunk.isLayerStarted(enumWorldBlockLayer2)) {
               if (Config.isShaders()) {
                  SVertexBuilder.calcNormalChunkLayer(generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumWorldBlockLayer2));
               }

               this.postRenderBlocks(enumWorldBlockLayer2, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumWorldBlockLayer2), compiledChunk);
            }
         }
      }

      compiledChunk.setVisibility(visGraph.computeVisibility());
      this.lockCompileTask.lock();

      try {
         HashSet hashSet2 = Sets.newHashSet(hashSet);
         HashSet hashSet3 = Sets.newHashSet(this.setTileEntities);
         hashSet2.removeAll(this.setTileEntities);
         hashSet3.removeAll(hashSet);
         this.setTileEntities.clear();
         this.setTileEntities.addAll(hashSet);
         this.renderGlobal.updateTileEntities(hashSet3, hashSet2);
      } finally {
         this.lockCompileTask.unlock();
      }
   }

   public void finishCompileTask() {
      this.lockCompileTask.lock();

      try {
         if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }
      } finally {
         this.lockCompileTask.unlock();
      }
   }

   public ReentrantLock getLockCompileTask() {
      return this.lockCompileTask;
   }

   public ChunkCompileTaskGenerator makeCompileTaskChunk() {
      this.lockCompileTask.lock();

      ChunkCompileTaskGenerator chunkCompileTaskGenerator;
      try {
         this.finishCompileTask();
         this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
         chunkCompileTaskGenerator = this.compileTask;
      } finally {
         this.lockCompileTask.unlock();
      }

      return chunkCompileTaskGenerator;
   }

   public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
      this.lockCompileTask.lock();

      ChunkCompileTaskGenerator chunkCompileTaskGenerator;
      try {
         if (this.compileTask != null && this.compileTask.getStatus() == ChunkCompileTaskGenerator.Status.PENDING) {
            return null;
         }

         if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }

         this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY);
         this.compileTask.setCompiledChunk(this.compiledChunk);
         ChunkCompileTaskGenerator chunkCompileTaskGenerator2 = this.compileTask;
         chunkCompileTaskGenerator = chunkCompileTaskGenerator2;
      } finally {
         this.lockCompileTask.unlock();
      }

      return chunkCompileTaskGenerator;
   }

   private void preRenderBlocks(WorldRenderer worldRendererIn, BlockPos pos) {
      worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
      worldRendererIn.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
   }

   private void postRenderBlocks(EnumWorldBlockLayer layer, float x, float y, float z, WorldRenderer worldRendererIn, CompiledChunk compiledChunkIn) {
      if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer)) {
         worldRendererIn.sortVertexData(x, y, z);
         compiledChunkIn.setState(worldRendererIn.getVertexState());
      }

      worldRendererIn.finishDrawing();
   }

   private void initModelviewMatrix() {
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      float f = 1.000001F;
      GlStateManager.translate(-8.0F, -8.0F, -8.0F);
      GlStateManager.scale(f, f, f);
      GlStateManager.translate(8.0F, 8.0F, 8.0F);
      GlStateManager.getFloat(2982, this.modelviewMatrix);
      GlStateManager.popMatrix();
   }

   public void multModelviewMatrix() {
      GlStateManager.multMatrix(this.modelviewMatrix);
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {
      this.lockCompiledChunk.lock();

      try {
         this.compiledChunk = compiledChunkIn;
      } finally {
         this.lockCompiledChunk.unlock();
      }
   }

   public void stopCompileTask() {
      this.finishCompileTask();
      this.compiledChunk = CompiledChunk.DUMMY;
   }

   public void deleteGlResources() {
      this.stopCompileTask();
      this.world = null;

      for (int i = 0; i < EnumWorldBlockLayer.values().length; i++) {
         if (this.vertexBuffers[i] != null) {
            this.vertexBuffers[i].deleteGlBuffers();
         }
      }
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setNeedsUpdate(boolean needsUpdateIn) {
      this.j = needsUpdateIn;
      if (this.j) {
         if (this.isWorldPlayerUpdate()) {
            this.playerUpdate = true;
         }
      } else {
         this.playerUpdate = false;
      }
   }

   public boolean Q() {
      return this.j;
   }

   public BlockPos getBlockPosOffset16(EnumFacing enumFacing) {
      return this.getPositionOffset16(enumFacing);
   }

   public BlockPos getPositionOffset16(EnumFacing enumFacing) {
      int i = enumFacing.getIndex();
      BlockPos blockPos = this.positionOffsets16[i];
      if (blockPos == null) {
         blockPos = this.getPosition().offset(enumFacing, 16);
         this.positionOffsets16[i] = blockPos;
      }

      return blockPos;
   }

   private boolean isWorldPlayerUpdate() {
      if (this.world instanceof WorldClient) {
         WorldClient worldClient = (WorldClient)this.world;
         return worldClient.o();
      } else {
         return false;
      }
   }

   public boolean P() {
      return this.playerUpdate;
   }

   public RegionRenderCache createRegionRenderCache(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
      return new RegionRenderCache(world, blockPos, blockPos2, i);
   }

   private EnumWorldBlockLayer fixBlockLayer(Block block, EnumWorldBlockLayer enumWorldBlockLayer) {
      if (this.isMipmaps) {
         if (enumWorldBlockLayer == EnumWorldBlockLayer.CUTOUT) {
            if (block instanceof BlockRedstoneWire) {
               return enumWorldBlockLayer;
            }

            if (block instanceof BlockCactus) {
               return enumWorldBlockLayer;
            }

            return EnumWorldBlockLayer.CUTOUT_MIPPED;
         }
      } else if (enumWorldBlockLayer == EnumWorldBlockLayer.CUTOUT_MIPPED) {
         return EnumWorldBlockLayer.CUTOUT;
      }

      return enumWorldBlockLayer;
   }
}
