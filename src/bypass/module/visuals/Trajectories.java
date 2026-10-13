package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.render.ClientColor;
import bypass.render.RenderUtil;
import bypass.setting.ColorSetting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public final class Trajectories extends Module {
   private final ColorSetting color = new ColorSetting("Color", "Name color", ClientColor.PURPLE.getColor());
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.WORLD) {
         double var5 = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * var1x.getPartialTicks();
         double var7 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * var1x.getPartialTicks();
         double var9 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * var1x.getPartialTicks();
         if (mc.thePlayer.getHeldItem() == null || mc.gameSettings.thirdPersonView != 0) {
            return;
         }

         if (!this.isSplashPotion(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getItem())
            && mc.thePlayer.getHeldItem().getItem() != Items.experience_bottle
            && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod)
            && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)
            && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSnowball)
            && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl)
            && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEgg)) {
            return;
         }

         ItemStack var11 = mc.thePlayer.getHeldItem();
         Item var12 = mc.thePlayer.getHeldItem().getItem();
         double var13 = var5 - MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
         double var15 = var7 + mc.thePlayer.getEyeHeight() - 0.1000000014901161;
         double var17 = var9 - MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
         double var19 = -MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
               * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
            * (var12 instanceof ItemBow ? 1.0 : 0.4);
         double var21 = -MathHelper.sin(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI) * (var12 instanceof ItemBow ? 1.0 : 0.4);
         double var23 = MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
               * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
            * (var12 instanceof ItemBow ? 1.0 : 0.4);
         int var25 = 72000 - mc.thePlayer.p();
         float var26 = var25 / 20.0F;
         var26 = (var26 * var26 + var26 * 2.0F) / 3.0F;
         if (var26 < 0.1) {
            return;
         }

         if (var26 > 1.0F) {
            var26 = 1.0F;
         }

         float var27 = MathHelper.sqrt_double(var19 * var19 + var21 * var21 + var23 * var23);
         var19 /= var27;
         var21 /= var27;
         var23 /= var27;
         float var28 = var12 instanceof ItemBow
            ? var26 * 2.0F
            : (
               this.isSplashPotion(var11, var12)
                  ? 0.325F
                  : (var12 instanceof ItemFishingRod ? 1.25F : (mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.9F : 1.0F))
            );
         var19 *= var28 * (var12 instanceof ItemFishingRod ? 0.75F : (mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F));
         var21 *= var28 * (var12 instanceof ItemFishingRod ? 0.75F : (mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F));
         var23 *= var28 * (var12 instanceof ItemFishingRod ? 0.75F : (mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F));
         setupRender();
         RenderUtil.setColor(this.color.getValue().getRGB());
         Tessellator var29 = Tessellator.getInstance();
         WorldRenderer var30 = var29.getWorldRenderer();
         var30.begin(3, DefaultVertexFormats.POSITION);
         var30.pos(var13 - var5, var15 - var7, var17 - var9).endVertex();
         float var31 = (float)(var12 instanceof ItemBow ? 0.3 : 0.25);
         boolean var32 = false;
         Entity var33 = null;
         MovingObjectPosition var34 = null;

         while (!var32 && var15 > 0.0) {
            Vec3 var35 = new Vec3(var13, var15, var17);
            Vec3 var36 = new Vec3(var13 + var19, var15 + var21, var17 + var23);
            MovingObjectPosition var37 = mc.theWorld.rayTraceBlocks(var35, var36, false, true, false);
            if (var37 != null && var37.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
               var34 = var37;
               var32 = true;
            }

            AxisAlignedBB var38 = new AxisAlignedBB(var13 - var31, var15 - var31, var17 - var31, var13 + var31, var15 + var31, var17 + var31);

            for (Object var41 : this.getEntitiesInBox(var38.addCoord(var19, var21, var23).expand(1.0, 1.0, 1.0))) {
               Entity var42 = (Entity)var41;
               if (var42.canBeCollidedWith() && var42 != mc.thePlayer) {
                  float var43 = 0.3F;
                  AxisAlignedBB var44 = var42.getEntityBoundingBox().expand(var43, var43, var43);
                  MovingObjectPosition var45 = var44.calculateIntercept(var35, var36);
                  if (var45 != null) {
                     var32 = true;
                     var33 = var42;
                     var34 = var45;
                  }
               }
            }

            var13 += var19;
            var15 += var21;
            var17 += var23;
            float var56 = 0.99F;
            var19 *= var56;
            var21 *= var56;
            var23 *= var56;
            var21 -= var12 instanceof ItemBow ? 0.05 : 0.03;
            var30.pos(var13 - var5, var15 - var7, var17 - var9).endVertex();
         }

         var29.draw();
         if (var34 != null && var34.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            GlStateManager.translate(var13 - var5, var15 - var7, var17 - var9);
            int var54 = var34.sideHit.getIndex();
            if (var54 == 2) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            } else if (var54 == 3) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            } else if (var54 == 4) {
               GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            } else if (var54 == 5) {
               GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            Cylinder var55 = new Cylinder();
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            var55.setDrawStyle(100011);
            if (var33 != null) {
               RenderUtil.setColor(this.color.getValue().getRGB());
               GL11.glLineWidth(3.5F);
               var55.draw(0.6F, 0.3F, 0.0F, 4, 1);
               GL11.glLineWidth(1.0F);
            }

            var55.draw(0.6F, 0.3F, 0.0F, 4, 1);
         }

         restoreRender();
      }
   };
   private static final long c = 43546861681520L;

   public Trajectories(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.color);
   }

   public static void setupRender() {
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.disableTexture2D();
      GL11.glEnable(2848);
      GL11.glLineWidth(1.0F);
   }

   public static void restoreRender() {
      GL11.glLineWidth(2.0F);
      GL11.glDisable(2848);
      GlStateManager.enableTexture2D();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   private boolean isSplashPotion(ItemStack var1, Item var2) {
      if (var2 != null && var2 instanceof ItemPotion) {
         ItemPotion var6 = (ItemPotion)var2;
         if (!ItemPotion.isSplash(var1.getItemDamage())) {
            return false;
         }

         if (var6.getEffects(var1) != null) {
            return true;
         }
      }

      return false;
   }

   private List getEntitiesInBox(AxisAlignedBB var1) {
      ArrayList var5 = new ArrayList();
      int var6 = MathHelper.floor_double((var1.minX - 2.0) / 16.0);
      int var7 = MathHelper.floor_double((var1.maxX + 2.0) / 16.0);
      int var8 = MathHelper.floor_double((var1.minZ - 2.0) / 16.0);
      int var9 = MathHelper.floor_double((var1.maxZ + 2.0) / 16.0);

      for (int var10 = var6; var10 <= var7; var10++) {
         for (int var11 = var8; var11 <= var9; var11++) {
            if (mc.theWorld.getChunkProvider().chunkExists(var10, var11)) {
               mc.theWorld.getChunkFromChunkCoords(var10, var11).getEntitiesWithinAABBForEntity(mc.thePlayer, var1, var5, null);
            }
         }
      }

      return var5;
   }
}
