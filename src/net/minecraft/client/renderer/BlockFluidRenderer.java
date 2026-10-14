package net.minecraft.client.renderer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.src.CustomColors;
import net.minecraft.src.RenderEnv;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockFluidRenderer {
   private TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
   private TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];

   public BlockFluidRenderer() {
      this.initAtlasSprites();
   }

   public void initAtlasSprites() {
      TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
      this.atlasSpritesLava[0] = textureMap.getAtlasSprite("minecraft:blocks/lava_still");
      this.atlasSpritesLava[1] = textureMap.getAtlasSprite("minecraft:blocks/lava_flow");
      this.atlasSpritesWater[0] = textureMap.getAtlasSprite("minecraft:blocks/water_still");
      this.atlasSpritesWater[1] = textureMap.getAtlasSprite("minecraft:blocks/water_flow");
   }

   public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn) {
      BlockLiquid blockLiquid = (BlockLiquid)blockStateIn.getBlock();
      blockLiquid.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
      TextureAtlasSprite[] textureAtlasSprites = blockLiquid.getMaterial() == Material.lava ? this.atlasSpritesLava : this.atlasSpritesWater;
      RenderEnv renderEnv = RenderEnv.getInstance(blockAccess, blockStateIn, blockPosIn);
      int i = CustomColors.getFluidColor(blockAccess, blockStateIn, blockPosIn, renderEnv);
      float f = (i >> 16 & 0xFF) / 255.0F;
      float g = (i >> 8 & 0xFF) / 255.0F;
      float h = (i & 0xFF) / 255.0F;
      boolean bl = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP);
      boolean bl2 = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN);
      boolean[] booleans = renderEnv.getBorderFlags();
      booleans[0] = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH);
      booleans[1] = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH);
      booleans[2] = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST);
      booleans[3] = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST);
      float f2 = 1.0F;
      if (!bl && !bl2 && !booleans[0] && !booleans[1] && !booleans[2] && !booleans[3]) {
         return false;
      }

      boolean bl3 = false;
      float f3 = 0.5F;
      float f4 = 1.0F;
      float f5 = 0.8F;
      float f6 = 0.6F;
      Material material = blockLiquid.getMaterial();
      float f7 = this.getFluidHeight(blockAccess, blockPosIn, material);
      float f8 = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
      float f9 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
      float f10 = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
      double d = blockPosIn.getX();
      double e = blockPosIn.getY();
      double d2 = blockPosIn.getZ();
      float f11 = 0.001F;
      if (bl) {
         bl3 = true;
         TextureAtlasSprite textureAtlasSprite = textureAtlasSprites[0];
         float f12 = (float)BlockLiquid.getFlowDirection(blockAccess, blockPosIn, material);
         if (f12 > -999.0F) {
            textureAtlasSprite = textureAtlasSprites[1];
         }

         worldRendererIn.setSprite(textureAtlasSprite);
         f7 -= f11;
         f8 -= f11;
         f9 -= f11;
         f10 -= f11;
         float f13;
         float f14;
         float f15;
         float f16;
         float f17;
         float f18;
         float f19;
         float f20;
         if (f12 < -999.0F) {
            f13 = textureAtlasSprite.getInterpolatedU(0.0);
            f17 = textureAtlasSprite.getInterpolatedV(0.0);
            f14 = f13;
            f18 = textureAtlasSprite.getInterpolatedV(16.0);
            f15 = textureAtlasSprite.getInterpolatedU(16.0);
            f19 = f18;
            f16 = f15;
            f20 = f17;
         } else {
            float f21 = MathHelper.sin(f12) * 0.25F;
            float f22 = MathHelper.cos(f12) * 0.25F;
            float f23 = 8.0F;
            f13 = textureAtlasSprite.getInterpolatedU(8.0F + (-f22 - f21) * 16.0F);
            f17 = textureAtlasSprite.getInterpolatedV(8.0F + (-f22 + f21) * 16.0F);
            f14 = textureAtlasSprite.getInterpolatedU(8.0F + (-f22 + f21) * 16.0F);
            f18 = textureAtlasSprite.getInterpolatedV(8.0F + (f22 + f21) * 16.0F);
            f15 = textureAtlasSprite.getInterpolatedU(8.0F + (f22 + f21) * 16.0F);
            f19 = textureAtlasSprite.getInterpolatedV(8.0F + (f22 - f21) * 16.0F);
            f16 = textureAtlasSprite.getInterpolatedU(8.0F + (f22 - f21) * 16.0F);
            f20 = textureAtlasSprite.getInterpolatedV(8.0F + (-f22 - f21) * 16.0F);
         }

         int j = blockLiquid.getMixedBrightnessForBlock(blockAccess, blockPosIn);
         int k = j >> 16 & 65535;
         int l = j & 65535;
         float f24 = f4 * f;
         float f25 = f4 * g;
         float f26 = f4 * h;
         worldRendererIn.pos(d + 0.0, e + f7, d2 + 0.0).color(f24, f25, f26, f2).tex(f13, f17).lightmap(k, l).endVertex();
         worldRendererIn.pos(d + 0.0, e + f8, d2 + 1.0).color(f24, f25, f26, f2).tex(f14, f18).lightmap(k, l).endVertex();
         worldRendererIn.pos(d + 1.0, e + f9, d2 + 1.0).color(f24, f25, f26, f2).tex(f15, f19).lightmap(k, l).endVertex();
         worldRendererIn.pos(d + 1.0, e + f10, d2 + 0.0).color(f24, f25, f26, f2).tex(f16, f20).lightmap(k, l).endVertex();
         if (blockLiquid.shouldRenderSides(blockAccess, blockPosIn.up())) {
            worldRendererIn.pos(d + 0.0, e + f7, d2 + 0.0).color(f24, f25, f26, f2).tex(f13, f17).lightmap(k, l).endVertex();
            worldRendererIn.pos(d + 1.0, e + f10, d2 + 0.0).color(f24, f25, f26, f2).tex(f16, f20).lightmap(k, l).endVertex();
            worldRendererIn.pos(d + 1.0, e + f9, d2 + 1.0).color(f24, f25, f26, f2).tex(f15, f19).lightmap(k, l).endVertex();
            worldRendererIn.pos(d + 0.0, e + f8, d2 + 1.0).color(f24, f25, f26, f2).tex(f14, f18).lightmap(k, l).endVertex();
         }
      }

      if (bl2) {
         float f27 = textureAtlasSprites[0].getMinU();
         float f28 = textureAtlasSprites[0].getMaxU();
         float f29 = textureAtlasSprites[0].getMinV();
         float f30 = textureAtlasSprites[0].getMaxV();
         int m = blockLiquid.getMixedBrightnessForBlock(blockAccess, blockPosIn.down());
         int n = m >> 16 & 65535;
         int i2 = m & 65535;
         worldRendererIn.pos(d, e, d2 + 1.0).color(f3, f3, f3, f2).tex(f27, f30).lightmap(n, i2).endVertex();
         worldRendererIn.pos(d, e, d2).color(f3, f3, f3, f2).tex(f27, f29).lightmap(n, i2).endVertex();
         worldRendererIn.pos(d + 1.0, e, d2).color(f3, f3, f3, f2).tex(f28, f29).lightmap(n, i2).endVertex();
         worldRendererIn.pos(d + 1.0, e, d2 + 1.0).color(f3, f3, f3, f2).tex(f28, f30).lightmap(n, i2).endVertex();
         bl3 = true;
      }

      for (int i3 = 0; i3 < 4; i3++) {
         int i4 = 0;
         int i5 = 0;
         if (i3 == 0) {
            i5--;
         }

         if (i3 == 1) {
            i5++;
         }

         if (i3 == 2) {
            i4--;
         }

         if (i3 == 3) {
            i4++;
         }

         BlockPos blockPos = blockPosIn.add(i4, 0, i5);
         TextureAtlasSprite textureAtlasSprite2 = textureAtlasSprites[1];
         worldRendererIn.setSprite(textureAtlasSprite2);
         if (booleans[i3]) {
            double d3;
            double d4;
            float f31;
            float f32;
            double d5;
            double d6;
            if (i3 == 0) {
               f31 = f7;
               f32 = f10;
               d5 = d;
               d3 = d + 1.0;
               d6 = d2 + f11;
               d4 = d2 + f11;
            } else if (i3 == 1) {
               f31 = f9;
               f32 = f8;
               d5 = d + 1.0;
               d3 = d;
               d6 = d2 + 1.0 - f11;
               d4 = d2 + 1.0 - f11;
            } else if (i3 == 2) {
               f31 = f8;
               f32 = f7;
               d5 = d + f11;
               d3 = d + f11;
               d6 = d2 + 1.0;
               d4 = d2;
            } else {
               f31 = f10;
               f32 = f9;
               d5 = d + 1.0 - f11;
               d3 = d + 1.0 - f11;
               d6 = d2;
               d4 = d2 + 1.0;
            }

            bl3 = true;
            float f33 = textureAtlasSprite2.getInterpolatedU(0.0);
            float f34 = textureAtlasSprite2.getInterpolatedU(8.0);
            float f35 = textureAtlasSprite2.getInterpolatedV((1.0F - f31) * 16.0F * 0.5F);
            float f36 = textureAtlasSprite2.getInterpolatedV((1.0F - f32) * 16.0F * 0.5F);
            float f37 = textureAtlasSprite2.getInterpolatedV(8.0);
            int i6 = blockLiquid.getMixedBrightnessForBlock(blockAccess, blockPos);
            int i7 = i6 >> 16 & 65535;
            int i8 = i6 & 65535;
            float f38 = i3 < 2 ? f5 : f6;
            float f39 = f4 * f38 * f;
            float f40 = f4 * f38 * g;
            float f41 = f4 * f38 * h;
            worldRendererIn.pos(d5, e + f31, d6).color(f39, f40, f41, f2).tex(f33, f35).lightmap(i7, i8).endVertex();
            worldRendererIn.pos(d3, e + f32, d4).color(f39, f40, f41, f2).tex(f34, f36).lightmap(i7, i8).endVertex();
            worldRendererIn.pos(d3, e + 0.0, d4).color(f39, f40, f41, f2).tex(f34, f37).lightmap(i7, i8).endVertex();
            worldRendererIn.pos(d5, e + 0.0, d6).color(f39, f40, f41, f2).tex(f33, f37).lightmap(i7, i8).endVertex();
            worldRendererIn.pos(d5, e + 0.0, d6).color(f39, f40, f41, f2).tex(f33, f37).lightmap(i7, i8).endVertex();
            worldRendererIn.pos(d3, e + 0.0, d4).color(f39, f40, f41, f2).tex(f34, f37).lightmap(i7, i8).endVertex();
            worldRendererIn.pos(d3, e + f32, d4).color(f39, f40, f41, f2).tex(f34, f36).lightmap(i7, i8).endVertex();
            worldRendererIn.pos(d5, e + f31, d6).color(f39, f40, f41, f2).tex(f33, f35).lightmap(i7, i8).endVertex();
         }
      }

      worldRendererIn.setSprite((TextureAtlasSprite)null);
      return bl3;
   }

   private float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
      int i = 0;
      float f = 0.0F;

      for (int j = 0; j < 4; j++) {
         BlockPos blockPos = blockPosIn.add(-(j & 1), 0, -(j >> 1 & 1));
         if (blockAccess.getBlockState(blockPos.up()).getBlock().getMaterial() == blockMaterial) {
            return 1.0F;
         }

         IBlockState iBlockState = blockAccess.getBlockState(blockPos);
         Material material = iBlockState.getBlock().getMaterial();
         if (material != blockMaterial) {
            if (!material.isSolid()) {
               f++;
               i++;
            }
         } else {
            int k = iBlockState.getValue(BlockLiquid.LEVEL);
            if (k >= 8 || k == 0) {
               f += BlockLiquid.getLiquidHeightPercent(k) * 10.0F;
               i += 10;
            }

            f += BlockLiquid.getLiquidHeightPercent(k);
            i++;
         }
      }

      return 1.0F - f / i;
   }
}
