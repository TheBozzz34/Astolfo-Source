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

   public RenderChunk(World var1, RenderGlobal var2, BlockPos var3, int var4) {
      this.world = var1;
      this.renderGlobal = var2;
      this.index = var4;
      if (!var3.equals(this.getPosition())) {
         this.setPosition(var3);
      }

      if (OpenGlHelper.useVbo()) {
         for (int var9 = 0; var9 < EnumWorldBlockLayer.values().length; var9++) {
            this.vertexBuffers[var9] = new VertexBuffer(DefaultVertexFormats.BLOCK);
         }
      }
   }

   public boolean setFrameIndex(int var1) {
      if (this.frameIndex == var1) {
         return false;
      }

      this.frameIndex = var1;
      return true;
   }

   public VertexBuffer getVertexBufferByLayer(int var1) {
      return this.vertexBuffers[var1];
   }

   public void setPosition(BlockPos var1) {
      this.stopCompileTask();
      this.position = var1;
      this.boundingBox = new AxisAlignedBB(var1, var1.add(16, 16, 16));
      this.initModelviewMatrix();

      for (int var5 = 0; var5 < this.positionOffsets16.length; var5++) {
         this.positionOffsets16[var5] = null;
      }
   }

   public void resortTransparency(float var1, float var2, float var3, ChunkCompileTaskGenerator var4) {
      CompiledChunk var8 = var4.getCompiledChunk();
      if (var8.getState() != null && !var8.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT)) {
         WorldRenderer var9 = var4.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT);
         this.preRenderBlocks(var9, this.position);
         var9.setVertexState(var8.getState());
         this.postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, var1, var2, var3, var9, var8);
      }
   }

   public void rebuildChunk(float var1, float var2, float var3, ChunkCompileTaskGenerator var4) {
      CompiledChunk var8 = new CompiledChunk();
      BlockPos var10 = this.position;
      BlockPos var11 = var10.add(15, 15, 15);
      var4.getLock().lock();

      RegionRenderCache var12;
      try {
         if (var4.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
            return;
         }

         if (this.world == null) {
            return;
         }

         var12 = this.createRegionRenderCache(this.world, var10.add(-1, -1, -1), var11.add(1, 1, 1), 1);
         if (Reflector.MinecraftForgeClient_onRebuildChunk.exists()) {
            Reflector.call(Reflector.MinecraftForgeClient_onRebuildChunk, this.world, this.position, var12);
         }

         var4.setCompiledChunk(var8);
      } finally {
         var4.getLock().unlock();
      }

      VisGraph var13 = new VisGraph();
      HashSet var14 = Sets.newHashSet();
      if (!var12.extendedLevelsInChunkCache()) {
         renderChunksUpdated++;
         boolean[] var15 = new boolean[ENUM_WORLD_BLOCK_LAYERS.length];
         BlockRendererDispatcher var16 = Minecraft.getMinecraft().getBlockRendererDispatcher();
         Iterator var17 = BlockPosM.m(var10, var11).iterator();
         boolean var18 = Reflector.ForgeBlock_hasTileEntity.exists();
         boolean var19 = Reflector.ForgeBlock_canRenderInLayer.exists();
         boolean var20 = Reflector.ForgeHooksClient_setRenderLayer.exists();

         while (var17.hasNext()) {
            BlockPosM var21 = (BlockPosM)var17.next();
            IBlockState var22 = var12.getBlockState(var21);
            Block var23 = var22.getBlock();
            if (var23.isOpaqueCube()) {
               var13.func_178606_a(var21);
            }

            if (ReflectorForge.blockHasTileEntity(var22)) {
               TileEntity var24 = var12.getTileEntity(new BlockPos(var21));
               TileEntitySpecialRenderer var25 = TileEntityRendererDispatcher.instance.getSpecialRenderer(var24);
               if (var24 != null && var25 != null) {
                  var8.addTileEntity(var24);
                  if (var25.forceTileEntityRender()) {
                     var14.add(var24);
                  }
               }
            }

            EnumWorldBlockLayer[] var41;
            if (var19) {
               var41 = ENUM_WORLD_BLOCK_LAYERS;
            } else {
               var41 = this.blockLayersSingle;
               var41[0] = ModuleManager.s.isEnabled() && !Xray.isEnabledOre(var23) ? EnumWorldBlockLayer.TRANSLUCENT : var23.getBlockLayer();
            }

            for (int var43 = 0; var43 < var41.length; var43++) {
               EnumWorldBlockLayer var26 = var41[var43];
               if (var19) {
                  boolean var27 = Reflector.callBoolean(var23, Reflector.ForgeBlock_canRenderInLayer, var26);
                  if (!var27) {
                     continue;
                  }
               }

               if (var20) {
                  Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, var26);
               }

               if (this.fixBlockLayer) {
                  var26 = this.fixBlockLayer(var23, var26);
               }

               int var44 = var26.ordinal();
               if (var23.getRenderType() != -1) {
                  WorldRenderer var28 = var4.getRegionRenderCacheBuilder().getWorldRendererByLayerId(var44);
                  var28.setBlockLayer(var26);
                  if (!var8.isLayerStarted(var26)) {
                     var8.G(var26);
                     this.preRenderBlocks(var28, var10);
                  }

                  var15[var44] |= var16.renderBlock(var22, var21, var12, var28);
               }
            }
         }

         for (EnumWorldBlockLayer var42 : ENUM_WORLD_BLOCK_LAYERS) {
            if (var15[var42.ordinal()]) {
               var8.q(var42);
            }

            if (var8.isLayerStarted(var42)) {
               if (Config.isShaders()) {
                  SVertexBuilder.calcNormalChunkLayer(var4.getRegionRenderCacheBuilder().getWorldRendererByLayer(var42));
               }

               this.postRenderBlocks(var42, var1, var2, var3, var4.getRegionRenderCacheBuilder().getWorldRendererByLayer(var42), var8);
            }
         }
      }

      var8.setVisibility(var13.computeVisibility());
      this.lockCompileTask.lock();

      try {
         HashSet var36 = Sets.newHashSet(var14);
         HashSet var37 = Sets.newHashSet(this.setTileEntities);
         var36.removeAll(this.setTileEntities);
         var37.removeAll(var14);
         this.setTileEntities.clear();
         this.setTileEntities.addAll(var14);
         this.renderGlobal.updateTileEntities(var37, var36);
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

      ChunkCompileTaskGenerator var1;
      try {
         this.finishCompileTask();
         this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
         var1 = this.compileTask;
      } finally {
         this.lockCompileTask.unlock();
      }

      return var1;
   }

   public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
      this.lockCompileTask.lock();

      ChunkCompileTaskGenerator var4;
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
         ChunkCompileTaskGenerator var5 = this.compileTask;
         var4 = var5;
      } finally {
         this.lockCompileTask.unlock();
      }

      return var4;
   }

   private void preRenderBlocks(WorldRenderer var1, BlockPos var2) {
      var1.begin(7, DefaultVertexFormats.BLOCK);
      var1.setTranslation(-var2.getX(), -var2.getY(), -var2.getZ());
   }

   private void postRenderBlocks(EnumWorldBlockLayer var1, float var2, float var3, float var4, WorldRenderer var5, CompiledChunk var6) {
      if (var1 == EnumWorldBlockLayer.TRANSLUCENT && !var6.isLayerEmpty(var1)) {
         var5.sortVertexData(var2, var3, var4);
         var6.setState(var5.getVertexState());
      }

      var5.finishDrawing();
   }

   private void initModelviewMatrix() {
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      float var3 = 1.000001F;
      GlStateManager.translate(-8.0F, -8.0F, -8.0F);
      GlStateManager.scale(var3, var3, var3);
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

   public void setCompiledChunk(CompiledChunk var1) {
      this.lockCompiledChunk.lock();

      try {
         this.compiledChunk = var1;
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

      for (int var4 = 0; var4 < EnumWorldBlockLayer.values().length; var4++) {
         if (this.vertexBuffers[var4] != null) {
            this.vertexBuffers[var4].deleteGlBuffers();
         }
      }
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setNeedsUpdate(boolean var1) {
      this.j = var1;
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

   public BlockPos getBlockPosOffset16(EnumFacing var1) {
      return this.getPositionOffset16(var1);
   }

   public BlockPos getPositionOffset16(EnumFacing var1) {
      int var5 = var1.getIndex();
      BlockPos var6 = this.positionOffsets16[var5];
      if (var6 == null) {
         var6 = this.getPosition().offset(var1, 16);
         this.positionOffsets16[var5] = var6;
      }

      return var6;
   }

   private boolean isWorldPlayerUpdate() {
      if (this.world instanceof WorldClient) {
         WorldClient var4 = (WorldClient)this.world;
         return var4.o();
      } else {
         return false;
      }
   }

   public boolean P() {
      return this.playerUpdate;
   }

   public RegionRenderCache createRegionRenderCache(World var1, BlockPos var2, BlockPos var3, int var4) {
      return new RegionRenderCache(var1, var2, var3, var4);
   }

   private EnumWorldBlockLayer fixBlockLayer(Block var1, EnumWorldBlockLayer var2) {
      if (this.isMipmaps) {
         if (var2 == EnumWorldBlockLayer.CUTOUT) {
            if (var1 instanceof BlockRedstoneWire) {
               return var2;
            }

            if (var1 instanceof BlockCactus) {
               return var2;
            }

            return EnumWorldBlockLayer.CUTOUT_MIPPED;
         }
      } else if (var2 == EnumWorldBlockLayer.CUTOUT_MIPPED) {
         return EnumWorldBlockLayer.CUTOUT;
      }

      return var2;
   }
}
