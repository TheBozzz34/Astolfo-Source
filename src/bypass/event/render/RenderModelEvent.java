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

   public RenderModelEvent(
      RendererLivingEntity rendererLivingEntity, EntityLivingBase entityLivingBase, float f, float g, float h, float f2, float f3, float f4, float f5
   ) {
      this.state = EventState.PRE;
      this.renderer = rendererLivingEntity;
      this.entity = entityLivingBase;
      this.limbSwing = f;
      this.limbSwingAmount = g;
      this.ageInTicks = h;
      this.netHeadYaw = f2;
      this.headPitch = f3;
      this.renderYawOffset = f4;
      this.scale = f5;
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
