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

   public EntityPickupFX(World worldIn, Entity entity, Entity entity2, float f) {
      super(worldIn, entity.posX, entity.posY, entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
      this.field_174840_a = entity;
      this.field_174843_ax = entity2;
      this.maxAge = 3;
      this.field_174841_aA = f;
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      float f = (this.age + partialTicks) / this.maxAge;
      f *= f;
      double d = this.field_174840_a.posX;
      double e = this.field_174840_a.posY;
      double d2 = this.field_174840_a.posZ;
      double d3 = this.field_174843_ax.lastTickPosX + (this.field_174843_ax.posX - this.field_174843_ax.lastTickPosX) * partialTicks;
      double d4 = this.field_174843_ax.lastTickPosY + (this.field_174843_ax.posY - this.field_174843_ax.lastTickPosY) * partialTicks + this.field_174841_aA;
      double d5 = this.field_174843_ax.lastTickPosZ + (this.field_174843_ax.posZ - this.field_174843_ax.lastTickPosZ) * partialTicks;
      double d6 = d + (d3 - d) * f;
      double d7 = e + (d4 - e) * f;
      double d8 = d2 + (d5 - d2) * f;
      int i = this.getBrightnessForRender(partialTicks);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      d6 -= interpPosX;
      d7 -= interpPosY;
      d8 -= interpPosZ;
      this.field_174842_aB.renderEntityWithPosYaw(this.field_174840_a, (float)d6, (float)d7, (float)d8, this.field_174840_a.rotationYaw, partialTicks);
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
