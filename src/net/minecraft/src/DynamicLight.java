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
   private static final long a = 113357607647473L;

   public DynamicLight(Entity var1) {
      this.entity = var1;
      this.offsetY = var1.getEyeHeight();
   }

   public void update(RenderGlobal var1) {
      if (Config.isDynamicLightsFast()) {
         long var5 = System.currentTimeMillis();
         if (var5 < this.timeCheckMs + 500L) {
            return;
         }

         this.timeCheckMs = var5;
      }

      double var34 = this.entity.posX - 0.5;
      double var7 = this.entity.posY - 0.5 + this.offsetY;
      double var9 = this.entity.posZ - 0.5;
      int var11 = DynamicLights.getLightLevel(this.entity);
      double var12 = var34 - this.lastPosX;
      double var14 = var7 - this.lastPosY;
      double var16 = var9 - this.lastPosZ;
      double var18 = 0.1;
      if (Math.abs(var12) > var18 || Math.abs(var14) > var18 || Math.abs(var16) > var18 || this.lastLightLevel != var11) {
         this.lastPosX = var34;
         this.lastPosY = var7;
         this.lastPosZ = var9;
         this.lastLightLevel = var11;
         this.underwater = false;
         WorldClient var20 = var1.getWorld();
         if (var20 != null) {
            this.blockPosMutable.set(MathHelper.floor_double(var34), MathHelper.floor_double(var7), MathHelper.floor_double(var9));
            IBlockState var21 = var20.getBlockState(this.blockPosMutable);
            Block var22 = var21.getBlock();
            this.underwater = var22 == Blocks.water;
         }

         HashSet var35 = new HashSet();
         if (var11 > 0) {
            EnumFacing var36 = (MathHelper.floor_double(var34) & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
            EnumFacing var23 = (MathHelper.floor_double(var7) & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
            EnumFacing var24 = (MathHelper.floor_double(var9) & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            BlockPos var25 = new BlockPos(var34, var7, var9);
            RenderChunk var26 = var1.getRenderChunk(var25);
            RenderChunk var27 = var1.getRenderChunk(var26, var36);
            RenderChunk var28 = var1.getRenderChunk(var26, var24);
            RenderChunk var29 = var1.getRenderChunk(var27, var24);
            RenderChunk var30 = var1.getRenderChunk(var26, var23);
            RenderChunk var31 = var1.getRenderChunk(var30, var36);
            RenderChunk var32 = var1.getRenderChunk(var30, var24);
            RenderChunk var33 = var1.getRenderChunk(var31, var24);
            this.updateChunkLight(var26, this.setLitChunkPos, var35);
            this.updateChunkLight(var27, this.setLitChunkPos, var35);
            this.updateChunkLight(var28, this.setLitChunkPos, var35);
            this.updateChunkLight(var29, this.setLitChunkPos, var35);
            this.updateChunkLight(var30, this.setLitChunkPos, var35);
            this.updateChunkLight(var31, this.setLitChunkPos, var35);
            this.updateChunkLight(var32, this.setLitChunkPos, var35);
            this.updateChunkLight(var33, this.setLitChunkPos, var35);
         }

         this.updateLitChunks(var1);
         this.setLitChunkPos = var35;
      }
   }

   private void updateChunkLight(RenderChunk var1, Set<BlockPos> var2, Set<BlockPos> var3) {
      if (var1 != null) {
         CompiledChunk var7 = var1.getCompiledChunk();
         if (var7 != null && !var7.isEmpty()) {
            var1.setNeedsUpdate(true);
         }

         BlockPos var8 = var1.getPosition();
         if (var2 != null) {
            var2.remove(var8);
         }

         if (var3 != null) {
            var3.add(var8);
         }
      }
   }

   public void updateLitChunks(RenderGlobal var1) {
      for (BlockPos var6 : this.setLitChunkPos) {
         RenderChunk var7 = var1.getRenderChunk(var6);
         this.updateChunkLight(var7, (Set<BlockPos>)null, (Set<BlockPos>)null);
      }
   }

   public Entity getEntity() {
      return this.entity;
   }

   public void I(Entity var1) {
      this.entity = var1;
   }

   public double B() {
      return this.lastPosX;
   }

   public double r() {
      return this.lastPosY;
   }

   public double f() {
      return this.lastPosZ;
   }

   public int getLastLightLevel() {
      return this.lastLightLevel;
   }

   public boolean isUnderwater() {
      return this.underwater;
   }

   public double L() {
      return this.offsetY;
   }

   @Override
   public String toString() {
      return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
   }
}
