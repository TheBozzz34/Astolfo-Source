package net.minecraft.client.renderer;

import bypass.Astolfo;
import bypass.event.render.RenderBlockEvent;
import bypass.module.ModuleManager;
import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.BetterGrass;
import net.minecraft.src.BetterSnow;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedTextures;
import net.minecraft.src.CustomColors;
import net.minecraft.src.NaturalTextures;
import net.minecraft.src.Reflector;
import net.minecraft.src.RenderEnv;
import net.minecraft.src.SmartLeaves;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;

public class BlockModelRenderer {
   private static float aoLightValueOpaque = 0.2F;
   private static final long a = 33855445556129L;

   public static void updateAoLightValue() {
      aoLightValueOpaque = 1.0F - Config.getAmbientOcclusionLevel() * 0.8F;
   }

   public BlockModelRenderer() {
      if (Reflector.ForgeModContainer_forgeLightPipelineEnabled.exists()) {
         Reflector.setFieldValue(Reflector.ForgeModContainer_forgeLightPipelineEnabled, false);
      }
   }

   public boolean renderModel(IBlockAccess var1, IBakedModel var2, IBlockState var3, BlockPos var4, WorldRenderer var5) {
      Block var6 = var3.getBlock();
      var6.setBlockBoundsBasedOnState(var1, var4);
      return this.renderModel(var1, var2, var3, var4, var5, true);
   }

   public boolean renderModel(IBlockAccess var1, IBakedModel var2, IBlockState var3, BlockPos var4, WorldRenderer var5, boolean var6) {
      boolean var10 = Minecraft.isAmbientOcclusionEnabled() && var3.getBlock().getLightValue() == 0 && var2.isAmbientOcclusion();

      try {
         Block var11 = var3.getBlock();
         if (Config.isTreesSmart() && var3.getBlock() instanceof BlockLeavesBase) {
            var2 = SmartLeaves.getLeavesModel(var2);
         }

         return var10
            ? this.renderModelAmbientOcclusion(var1, var2, var11, var3, var4, var5, var6)
            : this.renderModelStandard(var1, var2, var11, var3, var4, var5, var6);
      } catch (Throwable var14) {
         CrashReport var12 = CrashReport.makeCrashReport(var14, "Tesselating block model");
         CrashReportCategory var13 = var12.makeCategory("Block model being tesselated");
         CrashReportCategory.addBlockInfo(var13, var4, var3);
         var13.addCrashSection("Using AO", var10);
         throw new ReportedException(var12);
      }
   }

   public boolean M(IBlockAccess var1, IBakedModel var2, Block var3, BlockPos var4, WorldRenderer var5, boolean var6) {
      return this.renderModelAmbientOcclusion(var1, var2, var3, var1.getBlockState(var4), var4, var5, var6);
   }

   public boolean renderModelAmbientOcclusion(
      IBlockAccess var1, IBakedModel var2, Block var3, IBlockState var4, BlockPos var5, WorldRenderer var6, boolean var7
   ) {
      boolean var11 = false;
      RenderEnv var12 = null;

      for (EnumFacing var16 : EnumFacing.VALUES) {
         List var17 = var2.getFaceQuads(var16);
         if (!var17.isEmpty()) {
            BlockPos var18 = var5.offset(var16);
            if (!var7 || var3.shouldSideBeRendered(var1, var18, var16)) {
               if (var12 == null) {
                  var12 = RenderEnv.getInstance(var1, var4, var5);
               }

               if (!var12.isBreakingAnimation(var17) && Config.py()) {
                  var17 = BetterGrass.Q(var1, var3, var5, var16, var17);
               }

               if (ModuleManager.s.isEnabled()) {
                  RenderBlockEvent var19 = new RenderBlockEvent(var3, var5);
                  Astolfo.INSTANCE.getEventBus().post(var19);
               }

               this.renderModelAmbientOcclusionQuads(var1, var3, var5, var6, var17, var12);
               var11 = true;
            }
         }
      }

      List var20 = var2.getGeneralQuads();
      if (var20.size() > 0) {
         if (var12 == null) {
            var12 = RenderEnv.getInstance(var1, var4, var5);
         }

         this.renderModelAmbientOcclusionQuads(var1, var3, var5, var6, var20, var12);
         var11 = true;
      }

      if (var12 != null && Config.d() && !var12.isBreakingAnimation() && BetterSnow.V(var1, var3, var4, var5)) {
         IBakedModel var21 = BetterSnow.r();
         IBlockState var22 = BetterSnow.V();
         this.renderModelAmbientOcclusion(var1, var21, var22.getBlock(), var22, var5, var6, true);
      }

      return var11;
   }

   public boolean a(IBlockAccess var1, IBakedModel var2, Block var3, BlockPos var4, WorldRenderer var5, boolean var6) {
      return this.renderModelStandard(var1, var2, var3, var1.getBlockState(var4), var4, var5, var6);
   }

   public boolean renderModelStandard(IBlockAccess var1, IBakedModel var2, Block var3, IBlockState var4, BlockPos var5, WorldRenderer var6, boolean var7) {
      boolean var11 = false;
      RenderEnv var12 = null;

      for (EnumFacing var16 : EnumFacing.VALUES) {
         List var17 = var2.getFaceQuads(var16);
         if (!var17.isEmpty()) {
            BlockPos var18 = var5.offset(var16);
            if (!var7 || var3.shouldSideBeRendered(var1, var18, var16)) {
               if (var12 == null) {
                  var12 = RenderEnv.getInstance(var1, var4, var5);
               }

               if (!var12.isBreakingAnimation(var17) && Config.py()) {
                  var17 = BetterGrass.Q(var1, var3, var5, var16, var17);
               }

               int var19 = var3.getMixedBrightnessForBlock(var1, var18);
               this.renderModelStandardQuads(var1, var3, var5, var16, var19, false, var6, var17, var12);
               var11 = true;
            }
         }
      }

      List var20 = var2.getGeneralQuads();
      if (var20.size() > 0) {
         if (var12 == null) {
            var12 = RenderEnv.getInstance(var1, var4, var5);
         }

         this.renderModelStandardQuads(var1, var3, var5, (EnumFacing)null, -1, true, var6, var20, var12);
         var11 = true;
      }

      if (var12 != null && Config.d() && !var12.isBreakingAnimation() && BetterSnow.V(var1, var3, var4, var5) && BetterSnow.V(var1, var3, var4, var5)) {
         IBakedModel var21 = BetterSnow.r();
         IBlockState var22 = BetterSnow.V();
         this.renderModelStandard(var1, var21, var22.getBlock(), var22, var5, var6, true);
      }

      return var11;
   }

   private void renderModelAmbientOcclusionQuads(IBlockAccess var1, Block var2, BlockPos var3, WorldRenderer var4, List var5, RenderEnv var6) {
      float[] var10 = var6.getQuadBounds();
      BitSet var11 = var6.getBoundsFlags();
      BlockModelRenderer.AmbientOcclusionFace var12 = var6.getAoFace();
      IBlockState var13 = var6.getBlockState();
      double var14 = var3.getX();
      double var16 = var3.getY();
      double var18 = var3.getZ();
      Block.EnumOffsetType var20 = var2.getOffsetType();
      if (var20 != Block.EnumOffsetType.NONE) {
         long var21 = MathHelper.getPositionRandom(var3);
         var14 += ((float)(var21 >> 16 & 15L) / 15.0F - 0.5) * 0.5;
         var18 += ((float)(var21 >> 24 & 15L) / 15.0F - 0.5) * 0.5;
         if (var20 == Block.EnumOffsetType.XYZ) {
            var16 += ((float)(var21 >> 20 & 15L) / 15.0F - 1.0) * 0.2;
         }
      }

      for (Object var22 : var5) {
         BakedQuad var23 = (BakedQuad)var22;
         if (!var6.isBreakingAnimation(var23)) {
            BakedQuad var24 = var23;
            if (Config.isConnectedTextures()) {
               var23 = ConnectedTextures.getConnectedTexture(var1, var13, var3, var23, var6);
            }

            if (var23 == var24 && Config.isNaturalTextures()) {
               var23 = NaturalTextures.getNaturalTexture(var3, var23);
            }
         }

         this.fillQuadBounds(var2, var23.getVertexData(), var23.getFace(), var10, var11);
         var12.updateVertexBrightness(var1, var2, var3, var23.getFace(), var10, var11);
         if (var4.isMultiTexture()) {
            var4.addVertexData(var23.getVertexDataSingle());
            var4.putSprite(var23.getSprite());
         } else {
            var4.addVertexData(var23.getVertexData());
         }

         var4.putBrightness4(var12.vertexBrightness[0], var12.vertexBrightness[1], var12.vertexBrightness[2], var12.vertexBrightness[3]);
         int var30 = CustomColors.getColorMultiplier(var23, var2, var1, var3, var6);
         if (!var23.hasTintIndex() && var30 == -1) {
            var4.putColorMultiplier(var12.vertexColorMultiplier[0], var12.vertexColorMultiplier[0], var12.vertexColorMultiplier[0], 4);
            var4.putColorMultiplier(var12.vertexColorMultiplier[1], var12.vertexColorMultiplier[1], var12.vertexColorMultiplier[1], 3);
            var4.putColorMultiplier(var12.vertexColorMultiplier[2], var12.vertexColorMultiplier[2], var12.vertexColorMultiplier[2], 2);
            var4.putColorMultiplier(var12.vertexColorMultiplier[3], var12.vertexColorMultiplier[3], var12.vertexColorMultiplier[3], 1);
         } else {
            int var25;
            if (var30 != -1) {
               var25 = var30;
            } else {
               var25 = var2.colorMultiplier(var1, var3, var23.getTintIndex());
            }

            if (EntityRenderer.anaglyphEnable) {
               var25 = TextureUtil.anaglyphColor(var25);
            }

            float var26 = (var25 >> 16 & 0xFF) / 255.0F;
            float var27 = (var25 >> 8 & 0xFF) / 255.0F;
            float var28 = (var25 & 0xFF) / 255.0F;
            var4.putColorMultiplier(var12.vertexColorMultiplier[0] * var26, var12.vertexColorMultiplier[0] * var27, var12.vertexColorMultiplier[0] * var28, 4);
            var4.putColorMultiplier(var12.vertexColorMultiplier[1] * var26, var12.vertexColorMultiplier[1] * var27, var12.vertexColorMultiplier[1] * var28, 3);
            var4.putColorMultiplier(var12.vertexColorMultiplier[2] * var26, var12.vertexColorMultiplier[2] * var27, var12.vertexColorMultiplier[2] * var28, 2);
            var4.putColorMultiplier(var12.vertexColorMultiplier[3] * var26, var12.vertexColorMultiplier[3] * var27, var12.vertexColorMultiplier[3] * var28, 1);
         }

         var4.putPosition(var14, var16, var18);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void fillQuadBounds(Block var1, int[] var2, EnumFacing var3, float[] var4, BitSet var5) {
      float var9 = 32.0F;
      float var10 = 32.0F;
      float var11 = 32.0F;
      float var12 = -32.0F;
      float var13 = -32.0F;
      float var14 = -32.0F;
      int var15 = var2.length / 4;

      for (int var16 = 0; var16 < 4; var16++) {
         float var17 = Float.intBitsToFloat(var2[var16 * var15]);
         float var18 = Float.intBitsToFloat(var2[var16 * var15 + 1]);
         float var19 = Float.intBitsToFloat(var2[var16 * var15 + 2]);
         var9 = Math.min(var9, var17);
         var10 = Math.min(var10, var18);
         var11 = Math.min(var11, var19);
         var12 = Math.max(var12, var17);
         var13 = Math.max(var13, var18);
         var14 = Math.max(var14, var19);
      }

      if (var4 != null) {
         var4[EnumFacing.WEST.getIndex()] = var9;
         var4[EnumFacing.EAST.getIndex()] = var12;
         var4[EnumFacing.DOWN.getIndex()] = var10;
         var4[EnumFacing.UP.getIndex()] = var13;
         var4[EnumFacing.NORTH.getIndex()] = var11;
         var4[EnumFacing.SOUTH.getIndex()] = var14;
         var4[EnumFacing.WEST.getIndex() + EnumFacing.VALUES.length] = 1.0F - var9;
         var4[EnumFacing.EAST.getIndex() + EnumFacing.VALUES.length] = 1.0F - var12;
         var4[EnumFacing.DOWN.getIndex() + EnumFacing.VALUES.length] = 1.0F - var10;
         var4[EnumFacing.UP.getIndex() + EnumFacing.VALUES.length] = 1.0F - var13;
         var4[EnumFacing.NORTH.getIndex() + EnumFacing.VALUES.length] = 1.0F - var11;
         var4[EnumFacing.SOUTH.getIndex() + EnumFacing.VALUES.length] = 1.0F - var14;
      }

      float var20 = 1.0E-4F;
      float var21 = 0.9999F;
      switch (var3) {
         case DOWN:
            var5.set(1, var9 >= 1.0E-4F || var11 >= 1.0E-4F || var12 <= 0.9999F || var14 <= 0.9999F);
            var5.set(0, (var10 < 1.0E-4F || var1.isFullCube()) && var10 == var13);
            break;
         case UP:
            var5.set(1, var9 >= 1.0E-4F || var11 >= 1.0E-4F || var12 <= 0.9999F || var14 <= 0.9999F);
            var5.set(0, (var13 > 0.9999F || var1.isFullCube()) && var10 == var13);
            break;
         case NORTH:
            var5.set(1, var9 >= 1.0E-4F || var10 >= 1.0E-4F || var12 <= 0.9999F || var13 <= 0.9999F);
            var5.set(0, (var11 < 1.0E-4F || var1.isFullCube()) && var11 == var14);
            break;
         case SOUTH:
            var5.set(1, var9 >= 1.0E-4F || var10 >= 1.0E-4F || var12 <= 0.9999F || var13 <= 0.9999F);
            var5.set(0, (var14 > 0.9999F || var1.isFullCube()) && var11 == var14);
            break;
         case WEST:
            var5.set(1, var10 >= 1.0E-4F || var11 >= 1.0E-4F || var13 <= 0.9999F || var14 <= 0.9999F);
            var5.set(0, (var9 < 1.0E-4F || var1.isFullCube()) && var9 == var12);
            break;
         case EAST:
            var5.set(1, var10 >= 1.0E-4F || var11 >= 1.0E-4F || var13 <= 0.9999F || var14 <= 0.9999F);
            var5.set(0, (var12 > 0.9999F || var1.isFullCube()) && var9 == var12);
      }
   }

   private void renderModelStandardQuads(
      IBlockAccess var1, Block var2, BlockPos var3, EnumFacing var4, int var5, boolean var6, WorldRenderer var7, List var8, RenderEnv var9
   ) {
      BitSet var13 = var9.getBoundsFlags();
      IBlockState var14 = var9.getBlockState();
      double var15 = var3.getX();
      double var17 = var3.getY();
      double var19 = var3.getZ();
      Block.EnumOffsetType var21 = var2.getOffsetType();
      if (var21 != Block.EnumOffsetType.NONE) {
         int var22 = var3.getX();
         int var23 = var3.getZ();
         long var24 = var22 * 3129871 ^ var23 * 116129781L;
         var24 = var24 * var24 * 42317861L + var24 * 11L;
         var15 += ((float)(var24 >> 16 & 15L) / 15.0F - 0.5) * 0.5;
         var19 += ((float)(var24 >> 24 & 15L) / 15.0F - 0.5) * 0.5;
         if (var21 == Block.EnumOffsetType.XYZ) {
            var17 += ((float)(var24 >> 20 & 15L) / 15.0F - 1.0) * 0.2;
         }
      }

      for (Object var31 : var8) {
         BakedQuad var33 = (BakedQuad)var31;
         if (!var9.isBreakingAnimation(var33)) {
            BakedQuad var25 = var33;
            if (Config.isConnectedTextures()) {
               var33 = ConnectedTextures.getConnectedTexture(var1, var14, var3, var33, var9);
            }

            if (var33 == var25 && Config.isNaturalTextures()) {
               var33 = NaturalTextures.getNaturalTexture(var3, var33);
            }
         }

         if (var6) {
            this.fillQuadBounds(var2, var33.getVertexData(), var33.getFace(), (float[])null, var13);
            var5 = var13.get(0) ? var2.getMixedBrightnessForBlock(var1, var3.offset(var33.getFace())) : var2.getMixedBrightnessForBlock(var1, var3);
         }

         if (var7.isMultiTexture()) {
            var7.addVertexData(var33.getVertexDataSingle());
            var7.putSprite(var33.getSprite());
         } else {
            var7.addVertexData(var33.getVertexData());
         }

         var7.putBrightness4(var5, var5, var5, var5);
         int var34 = CustomColors.getColorMultiplier(var33, var2, var1, var3, var9);
         if (var33.hasTintIndex() || var34 != -1) {
            int var26;
            if (var34 != -1) {
               var26 = var34;
            } else {
               var26 = var2.colorMultiplier(var1, var3, var33.getTintIndex());
            }

            if (EntityRenderer.anaglyphEnable) {
               var26 = TextureUtil.anaglyphColor(var26);
            }

            float var27 = (var26 >> 16 & 0xFF) / 255.0F;
            float var28 = (var26 >> 8 & 0xFF) / 255.0F;
            float var29 = (var26 & 0xFF) / 255.0F;
            var7.putColorMultiplier(var27, var28, var29, 4);
            var7.putColorMultiplier(var27, var28, var29, 3);
            var7.putColorMultiplier(var27, var28, var29, 2);
            var7.putColorMultiplier(var27, var28, var29, 1);
         }

         var7.putPosition(var15, var17, var19);
      }
   }

   public void renderModelBrightnessColor(IBakedModel var1, float var2, float var3, float var4, float var5) {
      for (EnumFacing var12 : EnumFacing.VALUES) {
         this.renderModelBrightnessColorQuads(var2, var3, var4, var5, var1.getFaceQuads(var12));
      }

      this.renderModelBrightnessColorQuads(var2, var3, var4, var5, var1.getGeneralQuads());
   }

   public void renderModelBrightness(IBakedModel var1, IBlockState var2, float var3, boolean var4) {
      Block var8 = var2.getBlock();
      var8.setBlockBoundsForItemRender();
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      int var9 = var8.getRenderColor(var8.getStateForEntityRender(var2));
      if (EntityRenderer.anaglyphEnable) {
         var9 = TextureUtil.anaglyphColor(var9);
      }

      float var10 = (var9 >> 16 & 0xFF) / 255.0F;
      float var11 = (var9 >> 8 & 0xFF) / 255.0F;
      float var12 = (var9 & 0xFF) / 255.0F;
      if (!var4) {
         GlStateManager.color(var3, var3, var3, 1.0F);
      }

      this.renderModelBrightnessColor(var1, var3, var10, var11, var12);
   }

   private void renderModelBrightnessColorQuads(float var1, float var2, float var3, float var4, List<BakedQuad> var5) {
      Tessellator var9 = Tessellator.getInstance();
      WorldRenderer var10 = var9.getWorldRenderer();

      for (Object var12 : var5) {
         BakedQuad var13 = (BakedQuad)var12;
         var10.begin(7, DefaultVertexFormats.ITEM);
         var10.addVertexData(var13.getVertexData());
         if (var13.hasTintIndex()) {
            var10.putColorRGB_F4(var2 * var1, var3 * var1, var4 * var1);
         } else {
            var10.putColorRGB_F4(var1, var1, var1);
         }

         Vec3i var14 = var13.getFace().getDirectionVec();
         var10.putNormal(var14.getX(), var14.getY(), var14.getZ());
         var9.draw();
      }
   }

   public static float fixAoLightValue(float var0) {
      return var0 == 0.2F ? aoLightValueOpaque : var0;
   }

   public static class AmbientOcclusionFace {
      private final float[] vertexColorMultiplier = new float[4];
      private final int[] vertexBrightness = new int[4];
      private static final long a = 69736498544271L;

      public AmbientOcclusionFace(BlockModelRenderer var1) {
      }

      public AmbientOcclusionFace() {
      }

      public void updateVertexBrightness(IBlockAccess var1, Block var2, BlockPos var3, EnumFacing var4, float[] var5, BitSet var6) {
         BlockPos var10 = var6.get(0) ? var3.offset(var4) : var3;
         BlockModelRenderer.EnumNeighborInfo var11 = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(var4);
         BlockPos var12 = var10.offset(var11.field_178276_g[0]);
         BlockPos var13 = var10.offset(var11.field_178276_g[1]);
         BlockPos var14 = var10.offset(var11.field_178276_g[2]);
         BlockPos var15 = var10.offset(var11.field_178276_g[3]);
         int var16 = var2.getMixedBrightnessForBlock(var1, var12);
         int var17 = var2.getMixedBrightnessForBlock(var1, var13);
         int var18 = var2.getMixedBrightnessForBlock(var1, var14);
         int var19 = var2.getMixedBrightnessForBlock(var1, var15);
         float var20 = BlockModelRenderer.fixAoLightValue(var1.getBlockState(var12).getBlock().getAmbientOcclusionLightValue());
         float var21 = BlockModelRenderer.fixAoLightValue(var1.getBlockState(var13).getBlock().getAmbientOcclusionLightValue());
         float var22 = BlockModelRenderer.fixAoLightValue(var1.getBlockState(var14).getBlock().getAmbientOcclusionLightValue());
         float var23 = BlockModelRenderer.fixAoLightValue(var1.getBlockState(var15).getBlock().getAmbientOcclusionLightValue());
         boolean var24 = var1.getBlockState(var12.offset(var4)).getBlock().isTranslucent();
         boolean var25 = var1.getBlockState(var13.offset(var4)).getBlock().isTranslucent();
         boolean var26 = var1.getBlockState(var14.offset(var4)).getBlock().isTranslucent();
         boolean var27 = var1.getBlockState(var15.offset(var4)).getBlock().isTranslucent();
         float var28;
         int var29;
         if (!var26 && !var24) {
            var28 = var20;
            var29 = var16;
         } else {
            BlockPos var30 = var12.offset(var11.field_178276_g[2]);
            var28 = BlockModelRenderer.fixAoLightValue(var1.getBlockState(var30).getBlock().getAmbientOcclusionLightValue());
            var29 = var2.getMixedBrightnessForBlock(var1, var30);
         }

         int var31;
         float var63;
         if (!var27 && !var24) {
            var63 = var20;
            var31 = var16;
         } else {
            BlockPos var32 = var12.offset(var11.field_178276_g[3]);
            var63 = BlockModelRenderer.fixAoLightValue(var1.getBlockState(var32).getBlock().getAmbientOcclusionLightValue());
            var31 = var2.getMixedBrightnessForBlock(var1, var32);
         }

         int var33;
         float var64;
         if (!var26 && !var25) {
            var64 = var21;
            var33 = var17;
         } else {
            BlockPos var34 = var13.offset(var11.field_178276_g[2]);
            var64 = BlockModelRenderer.fixAoLightValue(var1.getBlockState(var34).getBlock().getAmbientOcclusionLightValue());
            var33 = var2.getMixedBrightnessForBlock(var1, var34);
         }

         int var35;
         float var65;
         if (!var27 && !var25) {
            var65 = var21;
            var35 = var17;
         } else {
            BlockPos var36 = var13.offset(var11.field_178276_g[3]);
            var65 = BlockModelRenderer.fixAoLightValue(var1.getBlockState(var36).getBlock().getAmbientOcclusionLightValue());
            var35 = var2.getMixedBrightnessForBlock(var1, var36);
         }

         int var66 = var2.getMixedBrightnessForBlock(var1, var3);
         if (var6.get(0) || !var1.getBlockState(var3.offset(var4)).getBlock().isOpaqueCube()) {
            var66 = var2.getMixedBrightnessForBlock(var1, var3.offset(var4));
         }

         float var37 = var6.get(0)
            ? var1.getBlockState(var10).getBlock().getAmbientOcclusionLightValue()
            : var1.getBlockState(var3).getBlock().getAmbientOcclusionLightValue();
         var37 = BlockModelRenderer.fixAoLightValue(var37);
         BlockModelRenderer.VertexTranslations var38 = BlockModelRenderer.VertexTranslations.getVertexTranslations(var4);
         if (var6.get(1) && var11.field_178289_i) {
            float var68 = (var23 + var20 + var63 + var37) * 0.25F;
            float var69 = (var22 + var20 + var28 + var37) * 0.25F;
            float var70 = (var22 + var21 + var64 + var37) * 0.25F;
            float var71 = (var23 + var21 + var65 + var37) * 0.25F;
            float var43 = var5[var11.field_178286_j[0].field_178229_m] * var5[var11.field_178286_j[1].field_178229_m];
            float var44 = var5[var11.field_178286_j[2].field_178229_m] * var5[var11.field_178286_j[3].field_178229_m];
            float var45 = var5[var11.field_178286_j[4].field_178229_m] * var5[var11.field_178286_j[5].field_178229_m];
            float var46 = var5[var11.field_178286_j[6].field_178229_m] * var5[var11.field_178286_j[7].field_178229_m];
            float var47 = var5[var11.field_178287_k[0].field_178229_m] * var5[var11.field_178287_k[1].field_178229_m];
            float var48 = var5[var11.field_178287_k[2].field_178229_m] * var5[var11.field_178287_k[3].field_178229_m];
            float var49 = var5[var11.field_178287_k[4].field_178229_m] * var5[var11.field_178287_k[5].field_178229_m];
            float var50 = var5[var11.field_178287_k[6].field_178229_m] * var5[var11.field_178287_k[7].field_178229_m];
            float var51 = var5[var11.field_178284_l[0].field_178229_m] * var5[var11.field_178284_l[1].field_178229_m];
            float var52 = var5[var11.field_178284_l[2].field_178229_m] * var5[var11.field_178284_l[3].field_178229_m];
            float var53 = var5[var11.field_178284_l[4].field_178229_m] * var5[var11.field_178284_l[5].field_178229_m];
            float var54 = var5[var11.field_178284_l[6].field_178229_m] * var5[var11.field_178284_l[7].field_178229_m];
            float var55 = var5[var11.field_178285_m[0].field_178229_m] * var5[var11.field_178285_m[1].field_178229_m];
            float var56 = var5[var11.field_178285_m[2].field_178229_m] * var5[var11.field_178285_m[3].field_178229_m];
            float var57 = var5[var11.field_178285_m[4].field_178229_m] * var5[var11.field_178285_m[5].field_178229_m];
            float var58 = var5[var11.field_178285_m[6].field_178229_m] * var5[var11.field_178285_m[7].field_178229_m];
            this.vertexColorMultiplier[var38.field_178191_g] = var68 * var43 + var69 * var44 + var70 * var45 + var71 * var46;
            this.vertexColorMultiplier[var38.field_178200_h] = var68 * var47 + var69 * var48 + var70 * var49 + var71 * var50;
            this.vertexColorMultiplier[var38.field_178201_i] = var68 * var51 + var69 * var52 + var70 * var53 + var71 * var54;
            this.vertexColorMultiplier[var38.field_178198_j] = var68 * var55 + var69 * var56 + var70 * var57 + var71 * var58;
            int var59 = this.getAoBrightness(var19, var16, var31, var66);
            int var60 = this.getAoBrightness(var18, var16, var29, var66);
            int var61 = this.getAoBrightness(var18, var17, var33, var66);
            int var62 = this.getAoBrightness(var19, var17, var35, var66);
            this.vertexBrightness[var38.field_178191_g] = this.getVertexBrightness(var59, var60, var61, var62, var43, var44, var45, var46);
            this.vertexBrightness[var38.field_178200_h] = this.getVertexBrightness(var59, var60, var61, var62, var47, var48, var49, var50);
            this.vertexBrightness[var38.field_178201_i] = this.getVertexBrightness(var59, var60, var61, var62, var51, var52, var53, var54);
            this.vertexBrightness[var38.field_178198_j] = this.getVertexBrightness(var59, var60, var61, var62, var55, var56, var57, var58);
         } else {
            float var39 = (var23 + var20 + var63 + var37) * 0.25F;
            float var40 = (var22 + var20 + var28 + var37) * 0.25F;
            float var41 = (var22 + var21 + var64 + var37) * 0.25F;
            float var42 = (var23 + var21 + var65 + var37) * 0.25F;
            this.vertexBrightness[var38.field_178191_g] = this.getAoBrightness(var19, var16, var31, var66);
            this.vertexBrightness[var38.field_178200_h] = this.getAoBrightness(var18, var16, var29, var66);
            this.vertexBrightness[var38.field_178201_i] = this.getAoBrightness(var18, var17, var33, var66);
            this.vertexBrightness[var38.field_178198_j] = this.getAoBrightness(var19, var17, var35, var66);
            this.vertexColorMultiplier[var38.field_178191_g] = var39;
            this.vertexColorMultiplier[var38.field_178200_h] = var40;
            this.vertexColorMultiplier[var38.field_178201_i] = var41;
            this.vertexColorMultiplier[var38.field_178198_j] = var42;
         }
      }

      private int getAoBrightness(int var1, int var2, int var3, int var4) {
         if (var1 == 0) {
            var1 = var4;
         }

         if (var2 == 0) {
            var2 = var4;
         }

         if (var3 == 0) {
            var3 = var4;
         }

         return var1 + var2 + var3 + var4 >> 2 & 16711935;
      }

      private int getVertexBrightness(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         int var11 = (int)((var1 >> 16 & 0xFF) * var5 + (var2 >> 16 & 0xFF) * var6 + (var3 >> 16 & 0xFF) * var7 + (var4 >> 16 & 0xFF) * var8) & 0xFF;
         int var12 = (int)((var1 & 0xFF) * var5 + (var2 & 0xFF) * var6 + (var3 & 0xFF) * var7 + (var4 & 0xFF) * var8) & 0xFF;
         return var11 << 16 | var12;
      }
   }

   public enum EnumNeighborInfo {
      DOWN(
         "DOWN",
         0,
         new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.5F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.SOUTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.SOUTH
         }
      ),
      UP(
         "UP",
         1,
         new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH},
         1.0F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.SOUTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.SOUTH
         }
      ),
      NORTH(
         "NORTH",
         2,
         new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST},
         0.8F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST
         }
      ),
      SOUTH(
         "SOUTH",
         3,
         new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP},
         0.8F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.EAST
         }
      ),
      WEST(
         "WEST",
         4,
         new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.6F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.SOUTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.SOUTH
         }
      ),
      EAST(
         "EAST",
         5,
         new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.6F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.SOUTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.SOUTH
         }
      );

      public final EnumFacing[] field_178276_g;
      public final float field_178288_h;
      public final boolean field_178289_i;
      public final BlockModelRenderer.Orientation[] field_178286_j;
      public final BlockModelRenderer.Orientation[] field_178287_k;
      public final BlockModelRenderer.Orientation[] field_178284_l;
      public final BlockModelRenderer.Orientation[] field_178285_m;
      private static final BlockModelRenderer.EnumNeighborInfo[] VALUES = new BlockModelRenderer.EnumNeighborInfo[6];
      private static final BlockModelRenderer.EnumNeighborInfo[] t = new BlockModelRenderer.EnumNeighborInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};

      EnumNeighborInfo(
         String var3,
         int var4,
         EnumFacing[] var5,
         float var6,
         boolean var7,
         BlockModelRenderer.Orientation[] var8,
         BlockModelRenderer.Orientation[] var9,
         BlockModelRenderer.Orientation[] var10,
         BlockModelRenderer.Orientation[] var11
      ) {
         this.field_178276_g = var5;
         this.field_178288_h = var6;
         this.field_178289_i = var7;
         this.field_178286_j = var8;
         this.field_178287_k = var9;
         this.field_178284_l = var10;
         this.field_178285_m = var11;
      }

      public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing var0) {
         return VALUES[var0.getIndex()];
      }

      static {
         VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
         VALUES[EnumFacing.UP.getIndex()] = UP;
         VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
         VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
         VALUES[EnumFacing.WEST.getIndex()] = WEST;
         VALUES[EnumFacing.EAST.getIndex()] = EAST;
      }
   }

   public enum Orientation {
      DOWN("DOWN", 0, EnumFacing.DOWN, false),
      UP("UP", 1, EnumFacing.UP, false),
      NORTH("NORTH", 2, EnumFacing.NORTH, false),
      SOUTH("SOUTH", 3, EnumFacing.SOUTH, false),
      WEST("WEST", 4, EnumFacing.WEST, false),
      EAST("EAST", 5, EnumFacing.EAST, false),
      FLIP_DOWN("FLIP_DOWN", 6, EnumFacing.DOWN, true),
      FLIP_UP("FLIP_UP", 7, EnumFacing.UP, true),
      FLIP_NORTH("FLIP_NORTH", 8, EnumFacing.NORTH, true),
      FLIP_SOUTH("FLIP_SOUTH", 9, EnumFacing.SOUTH, true),
      FLIP_WEST("FLIP_WEST", 10, EnumFacing.WEST, true),
      FLIP_EAST("FLIP_EAST", 11, EnumFacing.EAST, true);

      public final int field_178229_m;
      private static final BlockModelRenderer.Orientation[] j = new BlockModelRenderer.Orientation[]{
         DOWN, UP, NORTH, SOUTH, WEST, EAST, FLIP_DOWN, FLIP_UP, FLIP_NORTH, FLIP_SOUTH, FLIP_WEST, FLIP_EAST
      };
      private static final long a = 116863443103525L;

      Orientation(String var3, int var4, EnumFacing var5, boolean var6) {
         this.field_178229_m = var5.getIndex() + (var6 ? EnumFacing.values().length : 0);
      }
   }

   public enum VertexTranslations {
      DOWN("DOWN", 0, 0, 1, 2, 3),
      UP("UP", 1, 2, 3, 0, 1),
      NORTH("NORTH", 2, 3, 0, 1, 2),
      SOUTH("SOUTH", 3, 0, 1, 2, 3),
      WEST("WEST", 4, 3, 0, 1, 2),
      EAST("EAST", 5, 1, 2, 3, 0);

      private final int field_178191_g;
      private final int field_178200_h;
      private final int field_178201_i;
      private final int field_178198_j;
      private static final BlockModelRenderer.VertexTranslations[] VALUES = new BlockModelRenderer.VertexTranslations[6];
      private static final BlockModelRenderer.VertexTranslations[] M = new BlockModelRenderer.VertexTranslations[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};

      VertexTranslations(String var3, int var4, int var5, int var6, int var7, int var8) {
         this.field_178191_g = var5;
         this.field_178200_h = var6;
         this.field_178201_i = var7;
         this.field_178198_j = var8;
      }

      public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing var0) {
         return VALUES[var0.getIndex()];
      }

      static {
         VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
         VALUES[EnumFacing.UP.getIndex()] = UP;
         VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
         VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
         VALUES[EnumFacing.WEST.getIndex()] = WEST;
         VALUES[EnumFacing.EAST.getIndex()] = EAST;
      }
   }
}
