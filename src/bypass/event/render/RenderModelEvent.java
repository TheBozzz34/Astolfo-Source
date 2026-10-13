package bypass.event.render;

import bypass.event.CancellableEvent;
import bypass.event.EventState;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public class RenderModelEvent extends CancellableEvent {
   private EventState state;
   private EntityLivingBase entity;
   private RendererLivingEntity renderer;
   private float limbSwing;
   private float limbSwingAmount;
   private float ageInTicks;
   private float netHeadYaw;
   private float headPitch;
   private float scale;
   private float renderYawOffset;
   private static final long a = 50280872532823L;

   public RenderModelEvent(RendererLivingEntity var1, EntityLivingBase var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      this.state = EventState.PRE;
      this.renderer = var1;
      this.entity = var2;
      this.limbSwing = var3;
      this.limbSwingAmount = var4;
      this.ageInTicks = var5;
      this.netHeadYaw = var6;
      this.headPitch = var7;
      this.renderYawOffset = var8;
      this.scale = var9;
   }

   public RenderModelEvent() {
      this.state = EventState.POST;
   }

   public EventState getState() {
      return this.state;
   }

   public RendererLivingEntity getRenderer() {
      return this.renderer;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   public float getLimbSwing() {
      return this.limbSwing;
   }

   public float getLimbSwingAmount() {
      return this.limbSwingAmount;
   }

   public float getAgeInTicks() {
      return this.ageInTicks;
   }

   public float getHeadPitch() {
      return this.headPitch;
   }

   public float getNetHeadYaw() {
      return this.netHeadYaw;
   }

   public float getRenderYawOffset() {
      return this.renderYawOffset;
   }

   public float getScale() {
      return this.scale;
   }
}
