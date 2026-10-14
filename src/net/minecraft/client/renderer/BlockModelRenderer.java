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

   public static void updateAoLightValue() {
      aoLightValueOpaque = 1.0F - Config.getAmbientOcclusionLevel() * 0.8F;
   }

   public BlockModelRenderer() {
      if (Reflector.ForgeModContainer_forgeLightPipelineEnabled.exists()) {
         Reflector.setFieldValue(Reflector.ForgeModContainer_forgeLightPipelineEnabled, false);
      }
   }

   public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn) {
      Block block = blockStateIn.getBlock();
      block.setBlockBoundsBasedOnState(blockAccessIn, blockPosIn);
      return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, true);
   }

   public boolean renderModel(
      IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      boolean bl = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();

      try {
         Block block2 = blockStateIn.getBlock();
         if (Config.isTreesSmart() && blockStateIn.getBlock() instanceof BlockLeavesBase) {
            modelIn = SmartLeaves.getLeavesModel(modelIn);
         }

         return bl
            ? this.renderModelAmbientOcclusion(blockAccessIn, modelIn, block2, blockStateIn, blockPosIn, worldRendererIn, checkSides)
            : this.renderModelStandard(blockAccessIn, modelIn, block2, blockStateIn, blockPosIn, worldRendererIn, checkSides);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Block model being tesselated");
         CrashReportCategory.addBlockInfo(crashReportCategory, blockPosIn, blockStateIn);
         crashReportCategory.addCrashSection("Using AO", bl);
         throw new ReportedException(crashReport);
      }
   }

   public boolean M(IBlockAccess iBlockAccess, IBakedModel iBakedModel, Block block, BlockPos blockPos, WorldRenderer worldRenderer, boolean bl) {
      return this.renderModelAmbientOcclusion(iBlockAccess, iBakedModel, block, iBlockAccess.getBlockState(blockPos), blockPos, worldRenderer, bl);
   }

   public boolean renderModelAmbientOcclusion(
      IBlockAccess iBlockAccess, IBakedModel iBakedModel, Block block, IBlockState iBlockState, BlockPos blockPos, WorldRenderer worldRenderer, boolean bl
   ) {
      boolean bl2 = false;
      RenderEnv renderEnv = null;

      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         List list = iBakedModel.getFaceQuads(enumFacing);
         if (!list.isEmpty()) {
            BlockPos blockPos2 = blockPos.offset(enumFacing);
            if (!bl || block.shouldSideBeRendered(iBlockAccess, blockPos2, enumFacing)) {
               if (renderEnv == null) {
                  renderEnv = RenderEnv.getInstance(iBlockAccess, iBlockState, blockPos);
               }

               if (!renderEnv.isBreakingAnimation(list) && Config.py()) {
                  list = BetterGrass.Q(iBlockAccess, block, blockPos, enumFacing, list);
               }

               if (ModuleManager.s.isEnabled()) {
                  RenderBlockEvent renderBlockEvent = new RenderBlockEvent(block, blockPos);
                  Astolfo.INSTANCE.getEventBus().post(renderBlockEvent);
               }

               this.renderModelAmbientOcclusionQuads(iBlockAccess, block, blockPos, worldRenderer, list, renderEnv);
               bl2 = true;
            }
         }
      }

      List list2 = iBakedModel.getGeneralQuads();
      if (list2.size() > 0) {
         if (renderEnv == null) {
            renderEnv = RenderEnv.getInstance(iBlockAccess, iBlockState, blockPos);
         }

         this.renderModelAmbientOcclusionQuads(iBlockAccess, block, blockPos, worldRenderer, list2, renderEnv);
         bl2 = true;
      }

      if (renderEnv != null && Config.d() && !renderEnv.isBreakingAnimation() && BetterSnow.V(iBlockAccess, block, iBlockState, blockPos)) {
         IBakedModel iBakedModel2 = BetterSnow.r();
         IBlockState iBlockState2 = BetterSnow.V();
         this.renderModelAmbientOcclusion(iBlockAccess, iBakedModel2, iBlockState2.getBlock(), iBlockState2, blockPos, worldRenderer, true);
      }

      return bl2;
   }

   public boolean a(IBlockAccess iBlockAccess, IBakedModel iBakedModel, Block block, BlockPos blockPos, WorldRenderer worldRenderer, boolean bl) {
      return this.renderModelStandard(iBlockAccess, iBakedModel, block, iBlockAccess.getBlockState(blockPos), blockPos, worldRenderer, bl);
   }

   public boolean renderModelStandard(
      IBlockAccess iBlockAccess, IBakedModel iBakedModel, Block block, IBlockState iBlockState, BlockPos blockPos, WorldRenderer worldRenderer, boolean bl
   ) {
      boolean bl2 = false;
      RenderEnv renderEnv = null;

      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         List list = iBakedModel.getFaceQuads(enumFacing);
         if (!list.isEmpty()) {
            BlockPos blockPos2 = blockPos.offset(enumFacing);
            if (!bl || block.shouldSideBeRendered(iBlockAccess, blockPos2, enumFacing)) {
               if (renderEnv == null) {
                  renderEnv = RenderEnv.getInstance(iBlockAccess, iBlockState, blockPos);
               }

               if (!renderEnv.isBreakingAnimation(list) && Config.py()) {
                  list = BetterGrass.Q(iBlockAccess, block, blockPos, enumFacing, list);
               }

               int i = block.getMixedBrightnessForBlock(iBlockAccess, blockPos2);
               this.renderModelStandardQuads(iBlockAccess, block, blockPos, enumFacing, i, false, worldRenderer, list, renderEnv);
               bl2 = true;
            }
         }
      }

      List list2 = iBakedModel.getGeneralQuads();
      if (list2.size() > 0) {
         if (renderEnv == null) {
            renderEnv = RenderEnv.getInstance(iBlockAccess, iBlockState, blockPos);
         }

         this.renderModelStandardQuads(iBlockAccess, block, blockPos, (EnumFacing)null, -1, true, worldRenderer, list2, renderEnv);
         bl2 = true;
      }

      if (renderEnv != null
         && Config.d()
         && !renderEnv.isBreakingAnimation()
         && BetterSnow.V(iBlockAccess, block, iBlockState, blockPos)
         && BetterSnow.V(iBlockAccess, block, iBlockState, blockPos)) {
         IBakedModel iBakedModel2 = BetterSnow.r();
         IBlockState iBlockState2 = BetterSnow.V();
         this.renderModelStandard(iBlockAccess, iBakedModel2, iBlockState2.getBlock(), iBlockState2, blockPos, worldRenderer, true);
      }

      return bl2;
   }

   private void renderModelAmbientOcclusionQuads(
      IBlockAccess iBlockAccess, Block block, BlockPos blockPos, WorldRenderer worldRenderer, List list, RenderEnv renderEnv
   ) {
      float[] floats = renderEnv.getQuadBounds();
      BitSet bitSet = renderEnv.getBoundsFlags();
      BlockModelRenderer.AmbientOcclusionFace ambientOcclusionFace = renderEnv.getAoFace();
      IBlockState iBlockState = renderEnv.getBlockState();
      double d = blockPos.getX();
      double e = blockPos.getY();
      double d2 = blockPos.getZ();
      Block.EnumOffsetType enumOffsetType = block.getOffsetType();
      if (enumOffsetType != Block.EnumOffsetType.NONE) {
         long l = MathHelper.getPositionRandom(blockPos);
         d += ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.5;
         d2 += ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.5;
         if (enumOffsetType == Block.EnumOffsetType.XYZ) {
            e += ((float)(l >> 20 & 15L) / 15.0F - 1.0) * 0.2;
         }
      }

      for (Object object : list) {
         BakedQuad bakedQuad = (BakedQuad)object;
         if (!renderEnv.isBreakingAnimation(bakedQuad)) {
            BakedQuad bakedQuad2 = bakedQuad;
            if (Config.isConnectedTextures()) {
               bakedQuad = ConnectedTextures.getConnectedTexture(iBlockAccess, iBlockState, blockPos, bakedQuad, renderEnv);
            }

            if (bakedQuad == bakedQuad2 && Config.isNaturalTextures()) {
               bakedQuad = NaturalTextures.getNaturalTexture(blockPos, bakedQuad);
            }
         }

         this.fillQuadBounds(block, bakedQuad.getVertexData(), bakedQuad.getFace(), floats, bitSet);
         ambientOcclusionFace.updateVertexBrightness(iBlockAccess, block, blockPos, bakedQuad.getFace(), floats, bitSet);
         if (worldRenderer.isMultiTexture()) {
            worldRenderer.addVertexData(bakedQuad.getVertexDataSingle());
            worldRenderer.putSprite(bakedQuad.getSprite());
         } else {
            worldRenderer.addVertexData(bakedQuad.getVertexData());
         }

         worldRenderer.putBrightness4(ambientOcclusionFace.vertexBrightness[0], ambientOcclusionFace.vertexBrightness[1], ambientOcclusionFace.vertexBrightness[2], ambientOcclusionFace.vertexBrightness[3]);
         int i = CustomColors.getColorMultiplier(bakedQuad, block, iBlockAccess, blockPos, renderEnv);
         if (!bakedQuad.hasTintIndex() && i == -1) {
            worldRenderer.putColorMultiplier(ambientOcclusionFace.vertexColorMultiplier[0], ambientOcclusionFace.vertexColorMultiplier[0], ambientOcclusionFace.vertexColorMultiplier[0], 4);
            worldRenderer.putColorMultiplier(ambientOcclusionFace.vertexColorMultiplier[1], ambientOcclusionFace.vertexColorMultiplier[1], ambientOcclusionFace.vertexColorMultiplier[1], 3);
            worldRenderer.putColorMultiplier(ambientOcclusionFace.vertexColorMultiplier[2], ambientOcclusionFace.vertexColorMultiplier[2], ambientOcclusionFace.vertexColorMultiplier[2], 2);
            worldRenderer.putColorMultiplier(ambientOcclusionFace.vertexColorMultiplier[3], ambientOcclusionFace.vertexColorMultiplier[3], ambientOcclusionFace.vertexColorMultiplier[3], 1);
         } else {
            int j;
            if (i != -1) {
               j = i;
            } else {
               j = block.colorMultiplier(iBlockAccess, blockPos, bakedQuad.getTintIndex());
            }

            if (EntityRenderer.anaglyphEnable) {
               j = TextureUtil.anaglyphColor(j);
            }

            float f = (j >> 16 & 0xFF) / 255.0F;
            float g = (j >> 8 & 0xFF) / 255.0F;
            float h = (j & 0xFF) / 255.0F;
            worldRenderer.putColorMultiplier(
               ambientOcclusionFace.vertexColorMultiplier[0] * f, ambientOcclusionFace.vertexColorMultiplier[0] * g, ambientOcclusionFace.vertexColorMultiplier[0] * h, 4
            );
            worldRenderer.putColorMultiplier(
               ambientOcclusionFace.vertexColorMultiplier[1] * f, ambientOcclusionFace.vertexColorMultiplier[1] * g, ambientOcclusionFace.vertexColorMultiplier[1] * h, 3
            );
            worldRenderer.putColorMultiplier(
               ambientOcclusionFace.vertexColorMultiplier[2] * f, ambientOcclusionFace.vertexColorMultiplier[2] * g, ambientOcclusionFace.vertexColorMultiplier[2] * h, 2
            );
            worldRenderer.putColorMultiplier(
               ambientOcclusionFace.vertexColorMultiplier[3] * f, ambientOcclusionFace.vertexColorMultiplier[3] * g, ambientOcclusionFace.vertexColorMultiplier[3] * h, 1
            );
         }

         worldRenderer.putPosition(d, e, d2);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void fillQuadBounds(Block blockIn, int[] vertexData, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags) {
      float f = 32.0F;
      float g = 32.0F;
      float h = 32.0F;
      float f2 = -32.0F;
      float f3 = -32.0F;
      float f4 = -32.0F;
      int i = vertexData.length / 4;

      for (int j = 0; j < 4; j++) {
         float f5 = Float.intBitsToFloat(vertexData[j * i]);
         float f6 = Float.intBitsToFloat(vertexData[j * i + 1]);
         float f7 = Float.intBitsToFloat(vertexData[j * i + 2]);
         f = Math.min(f, f5);
         g = Math.min(g, f6);
         h = Math.min(h, f7);
         f2 = Math.max(f2, f5);
         f3 = Math.max(f3, f6);
         f4 = Math.max(f4, f7);
      }

      if (quadBounds != null) {
         quadBounds[EnumFacing.WEST.getIndex()] = f;
         quadBounds[EnumFacing.EAST.getIndex()] = f2;
         quadBounds[EnumFacing.DOWN.getIndex()] = g;
         quadBounds[EnumFacing.UP.getIndex()] = f3;
         quadBounds[EnumFacing.NORTH.getIndex()] = h;
         quadBounds[EnumFacing.SOUTH.getIndex()] = f4;
         quadBounds[EnumFacing.WEST.getIndex() + EnumFacing.VALUES.length] = 1.0F - f;
         quadBounds[EnumFacing.EAST.getIndex() + EnumFacing.VALUES.length] = 1.0F - f2;
         quadBounds[EnumFacing.DOWN.getIndex() + EnumFacing.VALUES.length] = 1.0F - g;
         quadBounds[EnumFacing.UP.getIndex() + EnumFacing.VALUES.length] = 1.0F - f3;
         quadBounds[EnumFacing.NORTH.getIndex() + EnumFacing.VALUES.length] = 1.0F - h;
         quadBounds[EnumFacing.SOUTH.getIndex() + EnumFacing.VALUES.length] = 1.0F - f4;
      }

      float f8 = 1.0E-4F;
      float f9 = 0.9999F;
      switch (facingIn) {
         case DOWN:
            boundsFlags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || f2 <= 0.9999F || f4 <= 0.9999F);
            boundsFlags.set(0, (g < 1.0E-4F || blockIn.isFullCube()) && g == f3);
            break;
         case UP:
            boundsFlags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || f2 <= 0.9999F || f4 <= 0.9999F);
            boundsFlags.set(0, (f3 > 0.9999F || blockIn.isFullCube()) && g == f3);
            break;
         case NORTH:
            boundsFlags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || f2 <= 0.9999F || f3 <= 0.9999F);
            boundsFlags.set(0, (h < 1.0E-4F || blockIn.isFullCube()) && h == f4);
            break;
         case SOUTH:
            boundsFlags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || f2 <= 0.9999F || f3 <= 0.9999F);
            boundsFlags.set(0, (f4 > 0.9999F || blockIn.isFullCube()) && h == f4);
            break;
         case WEST:
            boundsFlags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
            boundsFlags.set(0, (f < 1.0E-4F || blockIn.isFullCube()) && f == f2);
            break;
         case EAST:
            boundsFlags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
            boundsFlags.set(0, (f2 > 0.9999F || blockIn.isFullCube()) && f == f2);
      }
   }

   private void renderModelStandardQuads(
      IBlockAccess iBlockAccess,
      Block block,
      BlockPos blockPos,
      EnumFacing enumFacing,
      int i,
      boolean bl,
      WorldRenderer worldRenderer,
      List list,
      RenderEnv renderEnv
   ) {
      BitSet bitSet = renderEnv.getBoundsFlags();
      IBlockState iBlockState = renderEnv.getBlockState();
      double d = blockPos.getX();
      double e = blockPos.getY();
      double d2 = blockPos.getZ();
      Block.EnumOffsetType enumOffsetType = block.getOffsetType();
      if (enumOffsetType != Block.EnumOffsetType.NONE) {
         int j = blockPos.getX();
         int k = blockPos.getZ();
         long l = j * 3129871 ^ k * 116129781L;
         l = l * l * 42317861L + l * 11L;
         d += ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.5;
         d2 += ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.5;
         if (enumOffsetType == Block.EnumOffsetType.XYZ) {
            e += ((float)(l >> 20 & 15L) / 15.0F - 1.0) * 0.2;
         }
      }

      for (Object object : list) {
         BakedQuad bakedQuad = (BakedQuad)object;
         if (!renderEnv.isBreakingAnimation(bakedQuad)) {
            BakedQuad bakedQuad2 = bakedQuad;
            if (Config.isConnectedTextures()) {
               bakedQuad = ConnectedTextures.getConnectedTexture(iBlockAccess, iBlockState, blockPos, bakedQuad, renderEnv);
            }

            if (bakedQuad == bakedQuad2 && Config.isNaturalTextures()) {
               bakedQuad = NaturalTextures.getNaturalTexture(blockPos, bakedQuad);
            }
         }

         if (bl) {
            this.fillQuadBounds(block, bakedQuad.getVertexData(), bakedQuad.getFace(), (float[])null, bitSet);
            i = bitSet.get(0)
               ? block.getMixedBrightnessForBlock(iBlockAccess, blockPos.offset(bakedQuad.getFace()))
               : block.getMixedBrightnessForBlock(iBlockAccess, blockPos);
         }

         if (worldRenderer.isMultiTexture()) {
            worldRenderer.addVertexData(bakedQuad.getVertexDataSingle());
            worldRenderer.putSprite(bakedQuad.getSprite());
         } else {
            worldRenderer.addVertexData(bakedQuad.getVertexData());
         }

         worldRenderer.putBrightness4(i, i, i, i);
         int m = CustomColors.getColorMultiplier(bakedQuad, block, iBlockAccess, blockPos, renderEnv);
         if (bakedQuad.hasTintIndex() || m != -1) {
            int n;
            if (m != -1) {
               n = m;
            } else {
               n = block.colorMultiplier(iBlockAccess, blockPos, bakedQuad.getTintIndex());
            }

            if (EntityRenderer.anaglyphEnable) {
               n = TextureUtil.anaglyphColor(n);
            }

            float f = (n >> 16 & 0xFF) / 255.0F;
            float g = (n >> 8 & 0xFF) / 255.0F;
            float h = (n & 0xFF) / 255.0F;
            worldRenderer.putColorMultiplier(f, g, h, 4);
            worldRenderer.putColorMultiplier(f, g, h, 3);
            worldRenderer.putColorMultiplier(f, g, h, 2);
            worldRenderer.putColorMultiplier(f, g, h, 1);
         }

         worldRenderer.putPosition(d, e, d2);
      }
   }

   public void renderModelBrightnessColor(IBakedModel bakedModel, float f, float red, float green, float blue) {
      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         this.renderModelBrightnessColorQuads(f, red, green, blue, bakedModel.getFaceQuads(enumFacing));
      }

      this.renderModelBrightnessColorQuads(f, red, green, blue, bakedModel.getGeneralQuads());
   }

   public void renderModelBrightness(IBakedModel model, IBlockState iBlockState, float brightness, boolean bl) {
      Block block = iBlockState.getBlock();
      block.setBlockBoundsForItemRender();
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      int i = block.getRenderColor(block.getStateForEntityRender(iBlockState));
      if (EntityRenderer.anaglyphEnable) {
         i = TextureUtil.anaglyphColor(i);
      }

      float f = (i >> 16 & 0xFF) / 255.0F;
      float g = (i >> 8 & 0xFF) / 255.0F;
      float h = (i & 0xFF) / 255.0F;
      if (!bl) {
         GlStateManager.color(brightness, brightness, brightness, 1.0F);
      }

      this.renderModelBrightnessColor(model, brightness, f, g, h);
   }

   private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();

      for (Object object : listQuads) {
         BakedQuad bakedQuad = (BakedQuad)object;
         worldRenderer.begin(7, DefaultVertexFormats.ITEM);
         worldRenderer.addVertexData(bakedQuad.getVertexData());
         if (bakedQuad.hasTintIndex()) {
            worldRenderer.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
         } else {
            worldRenderer.putColorRGB_F4(brightness, brightness, brightness);
         }

         Vec3i vec3i = bakedQuad.getFace().getDirectionVec();
         worldRenderer.putNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
         tessellator.draw();
      }
   }

   public static float fixAoLightValue(float f) {
      return f == 0.2F ? aoLightValueOpaque : f;
   }

   public static class AmbientOcclusionFace {
      private final float[] vertexColorMultiplier = new float[4];
      private final int[] vertexBrightness = new int[4];

      public AmbientOcclusionFace(BlockModelRenderer blockModelRenderer) {
      }

      public AmbientOcclusionFace() {
      }

      public void updateVertexBrightness(
         IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags
      ) {
         BlockPos blockPos = boundsFlags.get(0) ? blockPosIn.offset(facingIn) : blockPosIn;
         BlockModelRenderer.EnumNeighborInfo enumNeighborInfo = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(facingIn);
         BlockPos blockPos2 = blockPos.offset(enumNeighborInfo.field_178276_g[0]);
         BlockPos blockPos3 = blockPos.offset(enumNeighborInfo.field_178276_g[1]);
         BlockPos blockPos4 = blockPos.offset(enumNeighborInfo.field_178276_g[2]);
         BlockPos blockPos5 = blockPos.offset(enumNeighborInfo.field_178276_g[3]);
         int i = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos2);
         int j = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos3);
         int k = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos4);
         int l = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos5);
         float f = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos2).getBlock().getAmbientOcclusionLightValue());
         float g = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos3).getBlock().getAmbientOcclusionLightValue());
         float h = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos4).getBlock().getAmbientOcclusionLightValue());
         float f2 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos5).getBlock().getAmbientOcclusionLightValue());
         boolean bl = blockAccessIn.getBlockState(blockPos2.offset(facingIn)).getBlock().isTranslucent();
         boolean bl2 = blockAccessIn.getBlockState(blockPos3.offset(facingIn)).getBlock().isTranslucent();
         boolean bl3 = blockAccessIn.getBlockState(blockPos4.offset(facingIn)).getBlock().isTranslucent();
         boolean bl4 = blockAccessIn.getBlockState(blockPos5.offset(facingIn)).getBlock().isTranslucent();
         float f3;
         int m;
         if (!bl3 && !bl) {
            f3 = f;
            m = i;
         } else {
            BlockPos blockPos6 = blockPos2.offset(enumNeighborInfo.field_178276_g[2]);
            f3 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos6).getBlock().getAmbientOcclusionLightValue());
            m = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos6);
         }

         int n;
         float f4;
         if (!bl4 && !bl) {
            f4 = f;
            n = i;
         } else {
            BlockPos blockPos7 = blockPos2.offset(enumNeighborInfo.field_178276_g[3]);
            f4 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos7).getBlock().getAmbientOcclusionLightValue());
            n = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos7);
         }

         int i2;
         float f5;
         if (!bl3 && !bl2) {
            f5 = g;
            i2 = j;
         } else {
            BlockPos blockPos8 = blockPos3.offset(enumNeighborInfo.field_178276_g[2]);
            f5 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos8).getBlock().getAmbientOcclusionLightValue());
            i2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos8);
         }

         int i3;
         float f6;
         if (!bl4 && !bl2) {
            f6 = g;
            i3 = j;
         } else {
            BlockPos blockPos9 = blockPos3.offset(enumNeighborInfo.field_178276_g[3]);
            f6 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos9).getBlock().getAmbientOcclusionLightValue());
            i3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos9);
         }

         int i4 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);
         if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockPosIn.offset(facingIn)).getBlock().isOpaqueCube()) {
            i4 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(facingIn));
         }

         float f7 = boundsFlags.get(0)
            ? blockAccessIn.getBlockState(blockPos).getBlock().getAmbientOcclusionLightValue()
            : blockAccessIn.getBlockState(blockPosIn).getBlock().getAmbientOcclusionLightValue();
         f7 = BlockModelRenderer.fixAoLightValue(f7);
         BlockModelRenderer.VertexTranslations vertexTranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(facingIn);
         if (boundsFlags.get(1) && enumNeighborInfo.field_178289_i) {
            float f8 = (f2 + f + f4 + f7) * 0.25F;
            float f9 = (h + f + f3 + f7) * 0.25F;
            float f10 = (h + g + f5 + f7) * 0.25F;
            float f11 = (f2 + g + f6 + f7) * 0.25F;
            float f12 = quadBounds[enumNeighborInfo.field_178286_j[0].field_178229_m] * quadBounds[enumNeighborInfo.field_178286_j[1].field_178229_m];
            float f13 = quadBounds[enumNeighborInfo.field_178286_j[2].field_178229_m] * quadBounds[enumNeighborInfo.field_178286_j[3].field_178229_m];
            float f14 = quadBounds[enumNeighborInfo.field_178286_j[4].field_178229_m] * quadBounds[enumNeighborInfo.field_178286_j[5].field_178229_m];
            float f15 = quadBounds[enumNeighborInfo.field_178286_j[6].field_178229_m] * quadBounds[enumNeighborInfo.field_178286_j[7].field_178229_m];
            float f16 = quadBounds[enumNeighborInfo.field_178287_k[0].field_178229_m] * quadBounds[enumNeighborInfo.field_178287_k[1].field_178229_m];
            float f17 = quadBounds[enumNeighborInfo.field_178287_k[2].field_178229_m] * quadBounds[enumNeighborInfo.field_178287_k[3].field_178229_m];
            float f18 = quadBounds[enumNeighborInfo.field_178287_k[4].field_178229_m] * quadBounds[enumNeighborInfo.field_178287_k[5].field_178229_m];
            float f19 = quadBounds[enumNeighborInfo.field_178287_k[6].field_178229_m] * quadBounds[enumNeighborInfo.field_178287_k[7].field_178229_m];
            float f20 = quadBounds[enumNeighborInfo.field_178284_l[0].field_178229_m] * quadBounds[enumNeighborInfo.field_178284_l[1].field_178229_m];
            float f21 = quadBounds[enumNeighborInfo.field_178284_l[2].field_178229_m] * quadBounds[enumNeighborInfo.field_178284_l[3].field_178229_m];
            float f22 = quadBounds[enumNeighborInfo.field_178284_l[4].field_178229_m] * quadBounds[enumNeighborInfo.field_178284_l[5].field_178229_m];
            float f23 = quadBounds[enumNeighborInfo.field_178284_l[6].field_178229_m] * quadBounds[enumNeighborInfo.field_178284_l[7].field_178229_m];
            float f24 = quadBounds[enumNeighborInfo.field_178285_m[0].field_178229_m] * quadBounds[enumNeighborInfo.field_178285_m[1].field_178229_m];
            float f25 = quadBounds[enumNeighborInfo.field_178285_m[2].field_178229_m] * quadBounds[enumNeighborInfo.field_178285_m[3].field_178229_m];
            float f26 = quadBounds[enumNeighborInfo.field_178285_m[4].field_178229_m] * quadBounds[enumNeighborInfo.field_178285_m[5].field_178229_m];
            float f27 = quadBounds[enumNeighborInfo.field_178285_m[6].field_178229_m] * quadBounds[enumNeighborInfo.field_178285_m[7].field_178229_m];
            this.vertexColorMultiplier[vertexTranslations.field_178191_g] = f8 * f12 + f9 * f13 + f10 * f14 + f11 * f15;
            this.vertexColorMultiplier[vertexTranslations.field_178200_h] = f8 * f16 + f9 * f17 + f10 * f18 + f11 * f19;
            this.vertexColorMultiplier[vertexTranslations.field_178201_i] = f8 * f20 + f9 * f21 + f10 * f22 + f11 * f23;
            this.vertexColorMultiplier[vertexTranslations.field_178198_j] = f8 * f24 + f9 * f25 + f10 * f26 + f11 * f27;
            int i5 = this.getAoBrightness(l, i, n, i4);
            int i6 = this.getAoBrightness(k, i, m, i4);
            int i7 = this.getAoBrightness(k, j, i2, i4);
            int i8 = this.getAoBrightness(l, j, i3, i4);
            this.vertexBrightness[vertexTranslations.field_178191_g] = this.getVertexBrightness(i5, i6, i7, i8, f12, f13, f14, f15);
            this.vertexBrightness[vertexTranslations.field_178200_h] = this.getVertexBrightness(i5, i6, i7, i8, f16, f17, f18, f19);
            this.vertexBrightness[vertexTranslations.field_178201_i] = this.getVertexBrightness(i5, i6, i7, i8, f20, f21, f22, f23);
            this.vertexBrightness[vertexTranslations.field_178198_j] = this.getVertexBrightness(i5, i6, i7, i8, f24, f25, f26, f27);
         } else {
            float f28 = (f2 + f + f4 + f7) * 0.25F;
            float f29 = (h + f + f3 + f7) * 0.25F;
            float f30 = (h + g + f5 + f7) * 0.25F;
            float f31 = (f2 + g + f6 + f7) * 0.25F;
            this.vertexBrightness[vertexTranslations.field_178191_g] = this.getAoBrightness(l, i, n, i4);
            this.vertexBrightness[vertexTranslations.field_178200_h] = this.getAoBrightness(k, i, m, i4);
            this.vertexBrightness[vertexTranslations.field_178201_i] = this.getAoBrightness(k, j, i2, i4);
            this.vertexBrightness[vertexTranslations.field_178198_j] = this.getAoBrightness(l, j, i3, i4);
            this.vertexColorMultiplier[vertexTranslations.field_178191_g] = f28;
            this.vertexColorMultiplier[vertexTranslations.field_178200_h] = f29;
            this.vertexColorMultiplier[vertexTranslations.field_178201_i] = f30;
            this.vertexColorMultiplier[vertexTranslations.field_178198_j] = f31;
         }
      }

      private int getAoBrightness(int br1, int br2, int br3, int br4) {
         if (br1 == 0) {
            br1 = br4;
         }

         if (br2 == 0) {
            br2 = br4;
         }

         if (br3 == 0) {
            br3 = br4;
         }

         return br1 + br2 + br3 + br4 >> 2 & 16711935;
      }

      private int getVertexBrightness(int i, int j, int k, int l, float f, float g, float h, float f2) {
         int m = (int)((i >> 16 & 0xFF) * f + (j >> 16 & 0xFF) * g + (k >> 16 & 0xFF) * h + (l >> 16 & 0xFF) * f2) & 0xFF;
         int n = (int)((i & 0xFF) * f + (j & 0xFF) * g + (k & 0xFF) * h + (l & 0xFF) * f2) & 0xFF;
         return m << 16 | n;
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
         String string2,
         int j,
         EnumFacing[] enumFacings,
         float f,
         boolean bl,
         BlockModelRenderer.Orientation[] orientations,
         BlockModelRenderer.Orientation[] orientations2,
         BlockModelRenderer.Orientation[] orientations3,
         BlockModelRenderer.Orientation[] orientations4
      ) {
         this.field_178276_g = enumFacings;
         this.field_178288_h = f;
         this.field_178289_i = bl;
         this.field_178286_j = orientations;
         this.field_178287_k = orientations2;
         this.field_178284_l = orientations3;
         this.field_178285_m = orientations4;
      }

      public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing enumFacing) {
         return VALUES[enumFacing.getIndex()];
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

      Orientation(String string2, int k, EnumFacing enumFacing, boolean bl) {
         this.field_178229_m = enumFacing.getIndex() + (bl ? EnumFacing.values().length : 0);
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

      VertexTranslations(String string2, int j, int k, int l, int m, int n) {
         this.field_178191_g = k;
         this.field_178200_h = l;
         this.field_178201_i = m;
         this.field_178198_j = n;
      }

      public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing enumFacing) {
         return VALUES[enumFacing.getIndex()];
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
