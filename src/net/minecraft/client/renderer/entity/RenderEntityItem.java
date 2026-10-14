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

   public RenderEntityItem(RenderManager renderManagerIn, RenderItem renderItem) {
      super(renderManagerIn);
      this.itemRenderer = renderItem;
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   private int func_177077_a(EntityItem itemIn, double d, double d2, double d3, float f, IBakedModel iBakedModel) {
      ItemStack itemStack = itemIn.getEntityItem();
      Item item = itemStack.getItem();
      if (item == null) {
         return 0;
      }

      boolean bl = iBakedModel.isGui3d();
      int i = this.func_177078_a(itemStack);
      float g = MathHelper.sin((itemIn.getAge() + f) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
      float h = iBakedModel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
      GlStateManager.translate((float)d, (float)d2 + g + 0.25F * h, (float)d3);
      if (bl || this.renderManager.options != null) {
         float f2 = ((itemIn.getAge() + f) / 20.0F + itemIn.hoverStart) * (180.0F / (float)Math.PI);
         GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
      }

      if (!bl) {
         float f3 = -0.0F * (i - 1) * 0.5F;
         float f4 = -0.0F * (i - 1) * 0.5F;
         float f5 = -0.046875F * (i - 1) * 0.5F;
         GlStateManager.translate(f3, f4, f5);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      return i;
   }

   private int func_177078_a(ItemStack stack) {
      byte b = 1;
      if (stack.stackSize > 48) {
         b = 5;
      } else if (stack.stackSize > 32) {
         b = 4;
      } else if (stack.stackSize > 16) {
         b = 3;
      } else if (stack.stackSize > 1) {
         b = 2;
      }

      return b;
   }

   public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (ModuleManager.J.isEnabled()) {
         this.b(entity, x, y, z, entityYaw, partialTicks);
      } else {
         ItemStack itemStack = entity.getEntityItem();
         this.field_177079_e.setSeed(187L);
         boolean bl = false;
         if (this.bindEntityTexture(entity)) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            bl = true;
         }

         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         IBakedModel iBakedModel = this.itemRenderer.getItemModelMesher().getItemModel(itemStack);
         int i = this.func_177077_a(entity, x, y, z, partialTicks, iBakedModel);

         for (int j = 0; j < i; j++) {
            if (iBakedModel.isGui3d()) {
               GlStateManager.pushMatrix();
               if (j > 0) {
                  float f = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  float g = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  float h = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  GlStateManager.translate(f, g, h);
               }

               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               iBakedModel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
               this.itemRenderer.renderItem(itemStack, iBakedModel);
               GlStateManager.popMatrix();
            } else {
               GlStateManager.pushMatrix();
               iBakedModel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
               this.itemRenderer.renderItem(itemStack, iBakedModel);
               GlStateManager.popMatrix();
               float f2 = iBakedModel.getItemCameraTransforms().ground.scale.x;
               float f3 = iBakedModel.getItemCameraTransforms().ground.scale.y;
               float f4 = iBakedModel.getItemCameraTransforms().ground.scale.z;
               GlStateManager.translate(0.0F * f2, 0.0F * f3, 0.046875F * f4);
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
         this.bindEntityTexture(entity);
         if (bl) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
         }

         super.doRender(entity, x, y, z, entityYaw, partialTicks);
      }
   }

   private void b(EntityItem entityItem, double d, double d2, double d3, float f, float g) {
      e = (System.nanoTime() - w) / 2500000.0;
      if (!Minecraft.getMinecraft().inGameHasFocus) {
         e = 0.0;
      }

      ResourceLocation resourceLocation = this.getEntityTexture(entityItem);
      ItemStack itemStack = entityItem.getEntityItem();
      int i;
      if (itemStack != null && itemStack.getItem() != null) {
         i = Item.getIdFromItem(itemStack.getItem()) + itemStack.getMetadata();
      } else {
         i = 187;
      }

      this.field_177079_e.setSeed(i);
      boolean bl = true;
      this.bindTexture(resourceLocation);
      this.getRenderManager().renderEngine.getTexture(resourceLocation).setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      IBakedModel iBakedModel = this.itemRenderer.getItemModelMesher().getItemModel(itemStack);
      boolean bl2 = iBakedModel.isGui3d();
      boolean bl3 = iBakedModel.isGui3d();
      int j = this.func_177078_a(itemStack);
      float h = iBakedModel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
      GlStateManager.translate((float)d, (float)d2, (float)d3);
      if (iBakedModel.isGui3d()) {
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
      }

      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(entityItem.rotationYaw, 0.0F, 0.0F, 1.0F);
      if (bl3) {
         GlStateManager.translate(0.0, 0.0, -0.08);
      } else {
         GlStateManager.translate(0.0, 0.0, -0.04);
      }

      if (bl3 || this.getRenderManager().options != null) {
         if (bl3) {
            if (!entityItem.onGround) {
               double d4 = e * 2.0;
               float f2 = m(entityItem);
               if (f2 != -1.0F) {
                  d4 /= f2 / 1000.0F * 10.0F;
               }

               entityItem.rotationPitch = (float)(entityItem.rotationPitch + d4);
            }
         } else if (!Double.isNaN(entityItem.posX) && !Double.isNaN(entityItem.posY) && !Double.isNaN(entityItem.posZ) && entityItem.worldObj != null) {
            if (entityItem.onGround) {
               entityItem.rotationPitch = 0.0F;
            } else {
               double d5 = e * 2.0;
               float f3 = m(entityItem);
               if (f3 != -1.0F) {
                  d5 /= f3 / 1000.0F * 10.0F;
               }

               entityItem.rotationPitch = (float)(entityItem.rotationPitch + d5);
            }
         }

         GlStateManager.rotate(entityItem.rotationPitch, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      for (int k = 0; k < j; k++) {
         GlStateManager.pushMatrix();
         if (bl2) {
            if (k > 0) {
               float f4 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translate(0.0F, 0.0F, f4);
            }

            iBakedModel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(itemStack, iBakedModel);
            GlStateManager.popMatrix();
         } else {
            iBakedModel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(itemStack, iBakedModel);
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.05375F);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.bindTexture(resourceLocation);
      if (bl) {
         this.getRenderManager().renderEngine.getTexture(resourceLocation).restoreLastBlurMipmap();
      }
   }

   public static float m(EntityItem entityItem) {
      return G(entityItem, false);
   }

   public static float G(EntityItem entityItem, boolean bl) {
      double d = entityItem.posY + entityItem.getEyeHeight();
      int i = MathHelper.floor_double(entityItem.posX);
      int j = MathHelper.floor_float(MathHelper.floor_double(d));
      if (bl) {
         j--;
      }

      int k = MathHelper.floor_double(entityItem.posZ);
      BlockPos blockPos = new BlockPos(i, j, k);
      Block block = entityItem.worldObj.getBlockState(blockPos).getBlock();
      float f = -1.0F;
      if (block instanceof BlockLiquid) {
         BlockLiquid blockLiquid = (BlockLiquid)block;
         f = blockLiquid.getEffectiveFlowDecay(entityItem.worldObj, blockPos);
      }

      if (bl) {
         return f;
      }

      double e = 1.0;
      if (block instanceof BlockLiquid) {
         e = ((BlockLiquid)block).getLevel(entityItem.worldObj, blockPos);
      }

      if (e < 0.0) {
         e *= -1.0;
         if (d > j + (1.0 - e)) {
            return f;
         }
      } else if (d < j + e) {
         return f;
      }

      return -1.0F;
   }

   public ResourceLocation getEntityTexture(EntityItem entity) {
      return TextureMap.locationBlocksTexture;
   }
}
