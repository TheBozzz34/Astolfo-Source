package net.minecraft.src;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class DynamicLight {
   private Entity entity = null;
   private double offsetY = 0.0;
   private double lastPosX = -2.1474836E9F;
   private double lastPosY = -2.1474836E9F;
   private double lastPosZ = -2.1474836E9F;
   private int lastLightLevel = 0;
   private boolean underwater = false;
   private long timeCheckMs = 0L;
   private Set<BlockPos> setLitChunkPos = new HashSet<>();
   private BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();

   public DynamicLight(Entity entity) {
      this.entity = entity;
      this.offsetY = entity.getEyeHeight();
   }

   public void update(RenderGlobal renderGlobal) {
      if (Config.isDynamicLightsFast()) {
         long l = System.currentTimeMillis();
         if (l < this.timeCheckMs + 500L) {
            return;
         }

         this.timeCheckMs = l;
      }

      double d = this.entity.posX - 0.5;
      double e = this.entity.posY - 0.5 + this.offsetY;
      double d2 = this.entity.posZ - 0.5;
      int i = DynamicLights.getLightLevel(this.entity);
      double d3 = d - this.lastPosX;
      double d4 = e - this.lastPosY;
      double d5 = d2 - this.lastPosZ;
      double d6 = 0.1;
      if (Math.abs(d3) > d6 || Math.abs(d4) > d6 || Math.abs(d5) > d6 || this.lastLightLevel != i) {
         this.lastPosX = d;
         this.lastPosY = e;
         this.lastPosZ = d2;
         this.lastLightLevel = i;
         this.underwater = false;
         WorldClient worldClient = renderGlobal.getWorld();
         if (worldClient != null) {
            this.blockPosMutable.set(MathHelper.floor_double(d), MathHelper.floor_double(e), MathHelper.floor_double(d2));
            IBlockState iBlockState = worldClient.getBlockState(this.blockPosMutable);
            Block block = iBlockState.getBlock();
            this.underwater = block == Blocks.water;
         }

         HashSet hashSet = new HashSet();
         if (i > 0) {
            EnumFacing enumFacing = (MathHelper.floor_double(d) & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
            EnumFacing enumFacing2 = (MathHelper.floor_double(e) & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
            EnumFacing enumFacing3 = (MathHelper.floor_double(d2) & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            BlockPos blockPos = new BlockPos(d, e, d2);
            RenderChunk renderChunk = renderGlobal.getRenderChunk(blockPos);
            RenderChunk renderChunk2 = renderGlobal.getRenderChunk(renderChunk, enumFacing);
            RenderChunk renderChunk3 = renderGlobal.getRenderChunk(renderChunk, enumFacing3);
            RenderChunk renderChunk4 = renderGlobal.getRenderChunk(renderChunk2, enumFacing3);
            RenderChunk renderChunk5 = renderGlobal.getRenderChunk(renderChunk, enumFacing2);
            RenderChunk renderChunk6 = renderGlobal.getRenderChunk(renderChunk5, enumFacing);
            RenderChunk renderChunk7 = renderGlobal.getRenderChunk(renderChunk5, enumFacing3);
            RenderChunk renderChunk8 = renderGlobal.getRenderChunk(renderChunk6, enumFacing3);
            this.updateChunkLight(renderChunk, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk2, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk3, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk4, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk5, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk6, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk7, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk8, this.setLitChunkPos, hashSet);
         }

         this.updateLitChunks(renderGlobal);
         this.setLitChunkPos = hashSet;
      }
   }

   private void updateChunkLight(RenderChunk renderChunk, Set<BlockPos> setPrevPos, Set<BlockPos> setNewPos) {
      if (renderChunk != null) {
         CompiledChunk compiledChunk = renderChunk.getCompiledChunk();
         if (compiledChunk != null && !compiledChunk.isEmpty()) {
            renderChunk.setNeedsUpdate(true);
         }

         BlockPos blockPos = renderChunk.getPosition();
         if (setPrevPos != null) {
            setPrevPos.remove(blockPos);
         }

         if (setNewPos != null) {
            setNewPos.add(blockPos);
         }
      }
   }

   public void updateLitChunks(RenderGlobal renderGlobal) {
      for (BlockPos blockPos : this.setLitChunkPos) {
         RenderChunk renderChunk = renderGlobal.getRenderChunk(blockPos);
         this.updateChunkLight(renderChunk, (Set<BlockPos>)null, (Set<BlockPos>)null);
      }
   }

   public Entity getEntity() {
      return this.entity;
   }

   public void setEntity(Entity entity) {
      this.entity = entity;
   }

   public double getLastPosX() {
      return this.lastPosX;
   }

   public double getLastPosY() {
      return this.lastPosY;
   }

   public double getLastPosZ() {
      return this.lastPosZ;
   }

   public int getLastLightLevel() {
      return this.lastLightLevel;
   }

   public boolean isUnderwater() {
      return this.underwater;
   }

   public double getOffsetY() {
      return this.offsetY;
   }

   @Override
   public String toString() {
      return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
   }
}
