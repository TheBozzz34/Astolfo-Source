package net.minecraft.client.renderer.entity;

import bypass.module.ModuleManager;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEntityItem extends Render<EntityItem> {
   private final RenderItem itemRenderer;
   private final Random field_177079_e = new Random();
   public static long w;
   public static double e;
   private static final long a = 110307147055816L;

   public RenderEntityItem(RenderManager var1, RenderItem var2) {
      super(var1);
      this.itemRenderer = var2;
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   private int func_177077_a(EntityItem var1, double var2, double var4, double var6, float var8, IBakedModel var9) {
      ItemStack var13 = var1.getEntityItem();
      Item var14 = var13.getItem();
      if (var14 == null) {
         return 0;
      }

      boolean var15 = var9.isGui3d();
      int var16 = this.func_177078_a(var13);
      float var18 = MathHelper.sin((var1.getAge() + var8) / 10.0F + var1.hoverStart) * 0.1F + 0.1F;
      float var19 = var9.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
      GlStateManager.translate((float)var2, (float)var4 + var18 + 0.25F * var19, (float)var6);
      if (var15 || this.renderManager.options != null) {
         float var20 = ((var1.getAge() + var8) / 20.0F + var1.hoverStart) * (180.0F / (float)Math.PI);
         GlStateManager.rotate(var20, 0.0F, 1.0F, 0.0F);
      }

      if (!var15) {
         float var23 = -0.0F * (var16 - 1) * 0.5F;
         float var21 = -0.0F * (var16 - 1) * 0.5F;
         float var22 = -0.046875F * (var16 - 1) * 0.5F;
         GlStateManager.translate(var23, var21, var22);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      return var16;
   }

   private int func_177078_a(ItemStack var1) {
      byte var5 = 1;
      if (var1.stackSize > 48) {
         var5 = 5;
      } else if (var1.stackSize > 32) {
         var5 = 4;
      } else if (var1.stackSize > 16) {
         var5 = 3;
      } else if (var1.stackSize > 1) {
         var5 = 2;
      }

      return var5;
   }

   public void doRender(EntityItem var1, double var2, double var4, double var6, float var8, float var9) {
      long var10 = a ^ 135795353723672L;
      if (ModuleManager.J.isEnabled()) {
         this.b(var1, var2, var4, var6, var8, var9);
      } else {
         ItemStack var13 = var1.getEntityItem();
         this.field_177079_e.setSeed(187L);
         boolean var14 = false;
         if (this.bindEntityTexture(var1)) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(var1)).setBlurMipmap(false, false);
            var14 = true;
         }

         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         long var10000 = 2573219428719095706L ^ var10;
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         IBakedModel var15 = this.itemRenderer.getItemModelMesher().getItemModel(var13);
         int var16 = this.func_177077_a(var1, var2, var4, var6, var9, var15);

         for (int var17 = 0; var17 < var16; var17++) {
            if (var15.isGui3d()) {
               GlStateManager.pushMatrix();
               if (var17 > 0) {
                  float var18 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  float var19 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  float var20 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  GlStateManager.translate(var18, var19, var20);
               }

               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               var15.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
               this.itemRenderer.renderItem(var13, var15);
               GlStateManager.popMatrix();
            } else {
               GlStateManager.pushMatrix();
               var15.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
               this.itemRenderer.renderItem(var13, var15);
               GlStateManager.popMatrix();
               float var21 = var15.getItemCameraTransforms().ground.scale.x;
               float var22 = var15.getItemCameraTransforms().ground.scale.y;
               float var23 = var15.getItemCameraTransforms().ground.scale.z;
               GlStateManager.translate(0.0F * var21, 0.0F * var22, 0.046875F * var23);
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
         this.bindEntityTexture(var1);
         if (var14) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(var1)).restoreLastBlurMipmap();
         }

         super.doRender(var1, var2, var4, var6, var8, var9);
      }
   }

   private void b(EntityItem var1, double var2, double var4, double var6, float var8, float var9) {
      long var10 = a ^ 11501802016012L;
      e = (System.nanoTime() - w) / 2500000.0;
      if (!Minecraft.getMinecraft().inGameHasFocus) {
         e = 0.0;
      }

      ResourceLocation var13 = this.getEntityTexture(var1);
      ItemStack var14 = var1.getEntityItem();
      int var15;
      if (var14 != null && var14.getItem() != null) {
         var15 = Item.getIdFromItem(var14.getItem()) + var14.getMetadata();
      } else {
         var15 = 187;
      }

      this.field_177079_e.setSeed(var15);
      boolean var16 = true;
      long var10000 = 7921707800919149373L ^ var10;
      this.bindTexture(var13);
      this.getRenderManager().renderEngine.getTexture(var13).setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      var10000 = 473045858043443992L ^ var10;
      RenderHelper.enableStandardItemLighting();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      IBakedModel var17 = this.itemRenderer.getItemModelMesher().getItemModel(var14);
      boolean var18 = var17.isGui3d();
      boolean var19 = var17.isGui3d();
      int var20 = this.func_177078_a(var14);
      float var21 = var17.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
      GlStateManager.translate((float)var2, (float)var4, (float)var6);
      if (var17.isGui3d()) {
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
      }

      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(var1.rotationYaw, 0.0F, 0.0F, 1.0F);
      if (var19) {
         GlStateManager.translate(0.0, 0.0, -0.08);
      } else {
         GlStateManager.translate(0.0, 0.0, -0.04);
      }

      if (var19 || this.getRenderManager().options != null) {
         if (var19) {
            if (!var1.onGround) {
               double var22 = e * 2.0;
               float var24 = m(var1);
               if (var24 != -1.0F) {
                  var22 /= var24 / 1000.0F * 10.0F;
               }

               var1.rotationPitch = (float)(var1.rotationPitch + var22);
            }
         } else if (!Double.isNaN(var1.posX) && !Double.isNaN(var1.posY) && !Double.isNaN(var1.posZ) && var1.worldObj != null) {
            if (var1.onGround) {
               var1.rotationPitch = 0.0F;
            } else {
               double var25 = e * 2.0;
               float var27 = m(var1);
               if (var27 != -1.0F) {
                  var25 /= var27 / 1000.0F * 10.0F;
               }

               var1.rotationPitch = (float)(var1.rotationPitch + var25);
            }
         }

         GlStateManager.rotate(var1.rotationPitch, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      for (int var26 = 0; var26 < var20; var26++) {
         GlStateManager.pushMatrix();
         if (var18) {
            if (var26 > 0) {
               float var23 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translate(0.0F, 0.0F, var23);
            }

            var17.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(var14, var17);
            GlStateManager.popMatrix();
         } else {
            var17.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(var14, var17);
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.05375F);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.bindTexture(var13);
      if (var16) {
         this.getRenderManager().renderEngine.getTexture(var13).restoreLastBlurMipmap();
      }
   }

   public static float m(EntityItem var0) {
      return G(var0, false);
   }

   public static float G(EntityItem var0, boolean var1) {
      double var5 = var0.posY + var0.getEyeHeight();
      int var7 = MathHelper.floor_double(var0.posX);
      int var8 = MathHelper.floor_float(MathHelper.floor_double(var5));
      if (var1) {
         var8--;
      }

      int var9 = MathHelper.floor_double(var0.posZ);
      BlockPos var10 = new BlockPos(var7, var8, var9);
      Block var11 = var0.worldObj.getBlockState(var10).getBlock();
      float var12 = -1.0F;
      if (var11 instanceof BlockLiquid) {
         BlockLiquid var13 = (BlockLiquid)var11;
         var12 = var13.getEffectiveFlowDecay(var0.worldObj, var10);
      }

      if (var1) {
         return var12;
      }

      double var15 = 1.0;
      if (var11 instanceof BlockLiquid) {
         var15 = ((BlockLiquid)var11).getLevel(var0.worldObj, var10);
      }

      if (var15 < 0.0) {
         var15 *= -1.0;
         if (var5 > var8 + (1.0 - var15)) {
            return var12;
         }
      } else if (var5 < var8 + var15) {
         return var12;
      }

      return -1.0F;
   }

   public ResourceLocation getEntityTexture(EntityItem var1) {
      return TextureMap.locationBlocksTexture;
   }
}
