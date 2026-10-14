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
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         double d = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * renderEvent.getPartialTicks();
         double e = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * renderEvent.getPartialTicks();
         double d2 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * renderEvent.getPartialTicks();
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

         ItemStack itemStack = mc.thePlayer.getHeldItem();
         Item item = mc.thePlayer.getHeldItem().getItem();
         double d3 = d - MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
         double d4 = e + mc.thePlayer.getEyeHeight() - 0.1000000014901161;
         double d5 = d2 - MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
         double d6 = -MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
               * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
            * (item instanceof ItemBow ? 1.0 : 0.4);
         double d7 = -MathHelper.sin(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI) * (item instanceof ItemBow ? 1.0 : 0.4);
         double d8 = MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
               * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
            * (item instanceof ItemBow ? 1.0 : 0.4);
         int i = 72000 - mc.thePlayer.p();
         float f = i / 20.0F;
         f = (f * f + f * 2.0F) / 3.0F;
         if (f < 0.1) {
            return;
         }

         if (f > 1.0F) {
            f = 1.0F;
         }

         float g = MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d8 * d8);
         d6 /= g;
         d7 /= g;
         d8 /= g;
         float h = item instanceof ItemBow
            ? f * 2.0F
            : (
               this.isSplashPotion(itemStack, item)
                  ? 0.325F
                  : (item instanceof ItemFishingRod ? 1.25F : (mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.9F : 1.0F))
            );
         d6 *= h * (item instanceof ItemFishingRod ? 0.75F : (mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F));
         d7 *= h * (item instanceof ItemFishingRod ? 0.75F : (mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F));
         d8 *= h * (item instanceof ItemFishingRod ? 0.75F : (mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F));
         setupRender();
         RenderUtil.setColor(this.color.getValue().getRGB());
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(3, DefaultVertexFormats.POSITION);
         worldRenderer.pos(d3 - d, d4 - e, d5 - d2).endVertex();
         float f2 = (float)(item instanceof ItemBow ? 0.3 : 0.25);
         boolean bl = false;
         Entity entity = null;
         MovingObjectPosition movingObjectPosition = null;

         while (!bl && d4 > 0.0) {
            Vec3 vec3 = new Vec3(d3, d4, d5);
            Vec3 vec32 = new Vec3(d3 + d6, d4 + d7, d5 + d8);
            MovingObjectPosition movingObjectPosition2 = mc.theWorld.rayTraceBlocks(vec3, vec32, false, true, false);
            if (movingObjectPosition2 != null && movingObjectPosition2.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
               movingObjectPosition = movingObjectPosition2;
               bl = true;
            }

            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(d3 - f2, d4 - f2, d5 - f2, d3 + f2, d4 + f2, d5 + f2);

            for (Object object : this.getEntitiesInBox(axisAlignedBB.addCoord(d6, d7, d8).expand(1.0, 1.0, 1.0))) {
               Entity entity2 = (Entity)object;
               if (entity2.canBeCollidedWith() && entity2 != mc.thePlayer) {
                  float f3 = 0.3F;
                  AxisAlignedBB axisAlignedBB2 = entity2.getEntityBoundingBox().expand(f3, f3, f3);
                  MovingObjectPosition movingObjectPosition3 = axisAlignedBB2.calculateIntercept(vec3, vec32);
                  if (movingObjectPosition3 != null) {
                     bl = true;
                     entity = entity2;
                     movingObjectPosition = movingObjectPosition3;
                  }
               }
            }

            d3 += d6;
            d4 += d7;
            d5 += d8;
            float f4 = 0.99F;
            d6 *= f4;
            d7 *= f4;
            d8 *= f4;
            d7 -= item instanceof ItemBow ? 0.05 : 0.03;
            worldRenderer.pos(d3 - d, d4 - e, d5 - d2).endVertex();
         }

         tessellator.draw();
         if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            GlStateManager.translate(d3 - d, d4 - e, d5 - d2);
            int j = movingObjectPosition.sideHit.getIndex();
            if (j == 2) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            } else if (j == 3) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            } else if (j == 4) {
               GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            } else if (j == 5) {
               GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            Cylinder cylinder = new Cylinder();
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            cylinder.setDrawStyle(100011);
            if (entity != null) {
               RenderUtil.setColor(this.color.getValue().getRGB());
               GL11.glLineWidth(3.5F);
               cylinder.draw(0.6F, 0.3F, 0.0F, 4, 1);
               GL11.glLineWidth(1.0F);
            }

            cylinder.draw(0.6F, 0.3F, 0.0F, 4, 1);
         }

         restoreRender();
      }
   };

   public Trajectories(Category category) {
      super(category);
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

   private boolean isSplashPotion(ItemStack itemStack, Item item) {
      if (item != null && item instanceof ItemPotion) {
         ItemPotion itemPotion = (ItemPotion)item;
         if (!ItemPotion.isSplash(itemStack.getItemDamage())) {
            return false;
         }

         if (itemPotion.getEffects(itemStack) != null) {
            return true;
         }
      }

      return false;
   }

   private List getEntitiesInBox(AxisAlignedBB axisAlignedBB) {
      ArrayList arrayList = new ArrayList();
      int i = MathHelper.floor_double((axisAlignedBB.minX - 2.0) / 16.0);
      int j = MathHelper.floor_double((axisAlignedBB.maxX + 2.0) / 16.0);
      int k = MathHelper.floor_double((axisAlignedBB.minZ - 2.0) / 16.0);
      int l = MathHelper.floor_double((axisAlignedBB.maxZ + 2.0) / 16.0);

      for (int m = i; m <= j; m++) {
         for (int n = k; n <= l; n++) {
            if (mc.theWorld.getChunkProvider().chunkExists(m, n)) {
               mc.theWorld.getChunkFromChunkCoords(m, n).getEntitiesWithinAABBForEntity(mc.thePlayer, axisAlignedBB, arrayList, null);
            }
         }
      }

      return arrayList;
   }
}
