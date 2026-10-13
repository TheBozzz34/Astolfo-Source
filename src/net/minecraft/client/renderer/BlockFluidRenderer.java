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
   private static final long a = 30543818363342L;

   public BlockFluidRenderer() {
      this.initAtlasSprites();
   }

   public void initAtlasSprites() {
      TextureMap var3 = Minecraft.getMinecraft().getTextureMapBlocks();
      this.atlasSpritesLava[0] = var3.getAtlasSprite("minecraft:blocks/lava_still");
      this.atlasSpritesLava[1] = var3.getAtlasSprite("minecraft:blocks/lava_flow");
      this.atlasSpritesWater[0] = var3.getAtlasSprite("minecraft:blocks/water_still");
      this.atlasSpritesWater[1] = var3.getAtlasSprite("minecraft:blocks/water_flow");
   }

   public boolean renderFluid(IBlockAccess var1, IBlockState var2, BlockPos var3, WorldRenderer var4) {
      BlockLiquid var8 = (BlockLiquid)var2.getBlock();
      var8.setBlockBoundsBasedOnState(var1, var3);
      TextureAtlasSprite[] var9 = var8.getMaterial() == Material.lava ? this.atlasSpritesLava : this.atlasSpritesWater;
      RenderEnv var10 = RenderEnv.getInstance(var1, var2, var3);
      int var11 = CustomColors.getFluidColor(var1, var2, var3, var10);
      float var12 = (var11 >> 16 & 0xFF) / 255.0F;
      float var13 = (var11 >> 8 & 0xFF) / 255.0F;
      float var14 = (var11 & 0xFF) / 255.0F;
      boolean var15 = var8.shouldSideBeRendered(var1, var3.up(), EnumFacing.UP);
      boolean var16 = var8.shouldSideBeRendered(var1, var3.down(), EnumFacing.DOWN);
      boolean[] var17 = var10.getBorderFlags();
      var17[0] = var8.shouldSideBeRendered(var1, var3.north(), EnumFacing.NORTH);
      var17[1] = var8.shouldSideBeRendered(var1, var3.south(), EnumFacing.SOUTH);
      var17[2] = var8.shouldSideBeRendered(var1, var3.west(), EnumFacing.WEST);
      var17[3] = var8.shouldSideBeRendered(var1, var3.east(), EnumFacing.EAST);
      float var18 = 1.0F;
      if (!var15 && !var16 && !var17[0] && !var17[1] && !var17[2] && !var17[3]) {
         return false;
      }

      boolean var19 = false;
      float var20 = 0.5F;
      float var21 = 1.0F;
      float var22 = 0.8F;
      float var23 = 0.6F;
      Material var24 = var8.getMaterial();
      float var25 = this.getFluidHeight(var1, var3, var24);
      float var26 = this.getFluidHeight(var1, var3.south(), var24);
      float var27 = this.getFluidHeight(var1, var3.east().south(), var24);
      float var28 = this.getFluidHeight(var1, var3.east(), var24);
      double var29 = var3.getX();
      double var31 = var3.getY();
      double var33 = var3.getZ();
      float var35 = 0.001F;
      if (var15) {
         var19 = true;
         TextureAtlasSprite var36 = var9[0];
         float var37 = (float)BlockLiquid.getFlowDirection(var1, var3, var24);
         if (var37 > -999.0F) {
            var36 = var9[1];
         }

         var4.setSprite(var36);
         var25 -= var35;
         var26 -= var35;
         var27 -= var35;
         var28 -= var35;
         float var38;
         float var39;
         float var40;
         float var41;
         float var42;
         float var43;
         float var44;
         float var45;
         if (var37 < -999.0F) {
            var38 = var36.getInterpolatedU(0.0);
            var42 = var36.getInterpolatedV(0.0);
            var39 = var38;
            var43 = var36.getInterpolatedV(16.0);
            var40 = var36.getInterpolatedU(16.0);
            var44 = var43;
            var41 = var40;
            var45 = var42;
         } else {
            float var46 = MathHelper.sin(var37) * 0.25F;
            float var47 = MathHelper.cos(var37) * 0.25F;
            float var48 = 8.0F;
            var38 = var36.getInterpolatedU(8.0F + (-var47 - var46) * 16.0F);
            var42 = var36.getInterpolatedV(8.0F + (-var47 + var46) * 16.0F);
            var39 = var36.getInterpolatedU(8.0F + (-var47 + var46) * 16.0F);
            var43 = var36.getInterpolatedV(8.0F + (var47 + var46) * 16.0F);
            var40 = var36.getInterpolatedU(8.0F + (var47 + var46) * 16.0F);
            var44 = var36.getInterpolatedV(8.0F + (var47 - var46) * 16.0F);
            var41 = var36.getInterpolatedU(8.0F + (var47 - var46) * 16.0F);
            var45 = var36.getInterpolatedV(8.0F + (-var47 - var46) * 16.0F);
         }

         int var78 = var8.getMixedBrightnessForBlock(var1, var3);
         int var80 = var78 >> 16 & 65535;
         int var82 = var78 & 65535;
         float var49 = var21 * var12;
         float var50 = var21 * var13;
         float var51 = var21 * var14;
         var4.pos(var29 + 0.0, var31 + var25, var33 + 0.0).color(var49, var50, var51, var18).tex(var38, var42).lightmap(var80, var82).endVertex();
         var4.pos(var29 + 0.0, var31 + var26, var33 + 1.0).color(var49, var50, var51, var18).tex(var39, var43).lightmap(var80, var82).endVertex();
         var4.pos(var29 + 1.0, var31 + var27, var33 + 1.0).color(var49, var50, var51, var18).tex(var40, var44).lightmap(var80, var82).endVertex();
         var4.pos(var29 + 1.0, var31 + var28, var33 + 0.0).color(var49, var50, var51, var18).tex(var41, var45).lightmap(var80, var82).endVertex();
         if (var8.shouldRenderSides(var1, var3.up())) {
            var4.pos(var29 + 0.0, var31 + var25, var33 + 0.0).color(var49, var50, var51, var18).tex(var38, var42).lightmap(var80, var82).endVertex();
            var4.pos(var29 + 1.0, var31 + var28, var33 + 0.0).color(var49, var50, var51, var18).tex(var41, var45).lightmap(var80, var82).endVertex();
            var4.pos(var29 + 1.0, var31 + var27, var33 + 1.0).color(var49, var50, var51, var18).tex(var40, var44).lightmap(var80, var82).endVertex();
            var4.pos(var29 + 0.0, var31 + var26, var33 + 1.0).color(var49, var50, var51, var18).tex(var39, var43).lightmap(var80, var82).endVertex();
         }
      }

      if (var16) {
         float var63 = var9[0].getMinU();
         float var65 = var9[0].getMaxU();
         float var67 = var9[0].getMinV();
         float var69 = var9[0].getMaxV();
         int var71 = var8.getMixedBrightnessForBlock(var1, var3.down());
         int var73 = var71 >> 16 & 65535;
         int var75 = var71 & 65535;
         var4.pos(var29, var31, var33 + 1.0).color(var20, var20, var20, var18).tex(var63, var69).lightmap(var73, var75).endVertex();
         var4.pos(var29, var31, var33).color(var20, var20, var20, var18).tex(var63, var67).lightmap(var73, var75).endVertex();
         var4.pos(var29 + 1.0, var31, var33).color(var20, var20, var20, var18).tex(var65, var67).lightmap(var73, var75).endVertex();
         var4.pos(var29 + 1.0, var31, var33 + 1.0).color(var20, var20, var20, var18).tex(var65, var69).lightmap(var73, var75).endVertex();
         var19 = true;
      }

      for (int var64 = 0; var64 < 4; var64++) {
         int var66 = 0;
         int var68 = 0;
         if (var64 == 0) {
            var68--;
         }

         if (var64 == 1) {
            var68++;
         }

         if (var64 == 2) {
            var66--;
         }

         if (var64 == 3) {
            var66++;
         }

         BlockPos var70 = var3.add(var66, 0, var68);
         TextureAtlasSprite var72 = var9[1];
         var4.setSprite(var72);
         if (var17[var64]) {
            double var74;
            double var76;
            float var77;
            float var79;
            double var81;
            double var83;
            if (var64 == 0) {
               var77 = var25;
               var79 = var28;
               var81 = var29;
               var74 = var29 + 1.0;
               var83 = var33 + var35;
               var76 = var33 + var35;
            } else if (var64 == 1) {
               var77 = var27;
               var79 = var26;
               var81 = var29 + 1.0;
               var74 = var29;
               var83 = var33 + 1.0 - var35;
               var76 = var33 + 1.0 - var35;
            } else if (var64 == 2) {
               var77 = var26;
               var79 = var25;
               var81 = var29 + var35;
               var74 = var29 + var35;
               var83 = var33 + 1.0;
               var76 = var33;
            } else {
               var77 = var28;
               var79 = var27;
               var81 = var29 + 1.0 - var35;
               var74 = var29 + 1.0 - var35;
               var83 = var33;
               var76 = var33 + 1.0;
            }

            var19 = true;
            float var84 = var72.getInterpolatedU(0.0);
            float var52 = var72.getInterpolatedU(8.0);
            float var53 = var72.getInterpolatedV((1.0F - var77) * 16.0F * 0.5F);
            float var54 = var72.getInterpolatedV((1.0F - var79) * 16.0F * 0.5F);
            float var55 = var72.getInterpolatedV(8.0);
            int var56 = var8.getMixedBrightnessForBlock(var1, var70);
            int var57 = var56 >> 16 & 65535;
            int var58 = var56 & 65535;
            float var59 = var64 < 2 ? var22 : var23;
            float var60 = var21 * var59 * var12;
            float var61 = var21 * var59 * var13;
            float var62 = var21 * var59 * var14;
            var4.pos(var81, var31 + var77, var83).color(var60, var61, var62, var18).tex(var84, var53).lightmap(var57, var58).endVertex();
            var4.pos(var74, var31 + var79, var76).color(var60, var61, var62, var18).tex(var52, var54).lightmap(var57, var58).endVertex();
            var4.pos(var74, var31 + 0.0, var76).color(var60, var61, var62, var18).tex(var52, var55).lightmap(var57, var58).endVertex();
            var4.pos(var81, var31 + 0.0, var83).color(var60, var61, var62, var18).tex(var84, var55).lightmap(var57, var58).endVertex();
            var4.pos(var81, var31 + 0.0, var83).color(var60, var61, var62, var18).tex(var84, var55).lightmap(var57, var58).endVertex();
            var4.pos(var74, var31 + 0.0, var76).color(var60, var61, var62, var18).tex(var52, var55).lightmap(var57, var58).endVertex();
            var4.pos(var74, var31 + var79, var76).color(var60, var61, var62, var18).tex(var52, var54).lightmap(var57, var58).endVertex();
            var4.pos(var81, var31 + var77, var83).color(var60, var61, var62, var18).tex(var84, var53).lightmap(var57, var58).endVertex();
         }
      }

      var4.setSprite((TextureAtlasSprite)null);
      return var19;
   }

   private float getFluidHeight(IBlockAccess var1, BlockPos var2, Material var3) {
      int var7 = 0;
      float var8 = 0.0F;

      for (int var9 = 0; var9 < 4; var9++) {
         BlockPos var10 = var2.add(-(var9 & 1), 0, -(var9 >> 1 & 1));
         if (var1.getBlockState(var10.up()).getBlock().getMaterial() == var3) {
            return 1.0F;
         }

         IBlockState var11 = var1.getBlockState(var10);
         Material var12 = var11.getBlock().getMaterial();
         if (var12 != var3) {
            if (!var12.isSolid()) {
               var8++;
               var7++;
            }
         } else {
            int var13 = var11.getValue(BlockLiquid.LEVEL);
            if (var13 >= 8 || var13 == 0) {
               var8 += BlockLiquid.getLiquidHeightPercent(var13) * 10.0F;
               var7 += 10;
            }

            var8 += BlockLiquid.getLiquidHeightPercent(var13);
            var7++;
         }
      }

      return 1.0F - var8 / var7;
   }
}
