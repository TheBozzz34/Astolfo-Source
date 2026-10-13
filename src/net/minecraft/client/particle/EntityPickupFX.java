package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPickupFX extends EntityFX {
   private Entity field_174840_a;
   private Entity field_174843_ax;
   private int age;
   private int maxAge;
   private float field_174841_aA;
   private RenderManager field_174842_aB = Minecraft.getMinecraft().getRenderManager();
   private static final long ab = 2642768705333L;

   public EntityPickupFX(World var1, Entity var2, Entity var3, float var4) {
      super(var1, var2.posX, var2.posY, var2.posZ, var2.motionX, var2.motionY, var2.motionZ);
      this.field_174840_a = var2;
      this.field_174843_ax = var3;
      this.maxAge = 3;
      this.field_174841_aA = var4;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var11 = (this.age + var3) / this.maxAge;
      var11 *= var11;
      double var12 = this.field_174840_a.posX;
      double var14 = this.field_174840_a.posY;
      double var16 = this.field_174840_a.posZ;
      double var18 = this.field_174843_ax.lastTickPosX + (this.field_174843_ax.posX - this.field_174843_ax.lastTickPosX) * var3;
      double var20 = this.field_174843_ax.lastTickPosY + (this.field_174843_ax.posY - this.field_174843_ax.lastTickPosY) * var3 + this.field_174841_aA;
      double var22 = this.field_174843_ax.lastTickPosZ + (this.field_174843_ax.posZ - this.field_174843_ax.lastTickPosZ) * var3;
      double var24 = var12 + (var18 - var12) * var11;
      double var26 = var14 + (var20 - var14) * var11;
      double var28 = var16 + (var22 - var16) * var11;
      int var30 = this.getBrightnessForRender(var3);
      int var31 = var30 % 65536;
      int var32 = var30 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var31 / 1.0F, var32 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      var24 -= interpPosX;
      var26 -= interpPosY;
      var28 -= interpPosZ;
      this.field_174842_aB.renderEntityWithPosYaw(this.field_174840_a, (float)var24, (float)var26, (float)var28, this.field_174840_a.rotationYaw, var3);
   }

   @Override
   public void onUpdate() {
      this.age++;
      if (this.age == this.maxAge) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }
}
