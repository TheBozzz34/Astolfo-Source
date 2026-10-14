package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class RealmsSliderButton extends RealmsButton {
   public float value = 1.0F;
   public boolean sliding;
   private final float minValue;
   private final float maxValue;
   private int steps;

   public RealmsSliderButton(int i, int j, int k, int l, int m, int n) {
      this(i, j, k, l, n, 0, 1.0F, m);
   }

   public RealmsSliderButton(int i, int j, int k, int l, int m, int n, float f, float g) {
      super(i, j, k, l, 20, "");
      this.minValue = f;
      this.maxValue = g;
      this.value = this.toPct(n);
      this.getProxy().displayString = this.getMessage();
   }

   public String getMessage() {
      return "";
   }

   public float toPct(float f) {
      return MathHelper.clamp_float((this.clamp(f) - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
   }

   public float toValue(float f) {
      return this.clamp(this.minValue + (this.maxValue - this.minValue) * MathHelper.clamp_float(f, 0.0F, 1.0F));
   }

   public float clamp(float f) {
      f = this.clampSteps(f);
      return MathHelper.clamp_float(f, this.minValue, this.maxValue);
   }

   public float clampSteps(float f) {
      if (this.steps > 0) {
         f = this.steps * Math.round(f / this.steps);
      }

      return f;
   }

   @Override
   public int getYImage(boolean bl) {
      return 0;
   }

   @Override
   public void renderBg(int i, int j) {
      if (this.getProxy().visible) {
         if (this.sliding) {
            this.value = (float)(i - (this.getProxy().xPosition + 4)) / (this.getProxy().getButtonWidth() - 8);
            this.value = MathHelper.clamp_float(this.value, 0.0F, 1.0F);
            float f = this.toValue(this.value);
            this.clicked(f);
            this.value = this.toPct(f);
            this.getProxy().displayString = this.getMessage();
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(WIDGETS_LOCATION);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(this.getProxy().xPosition + (int)(this.value * (this.getProxy().getButtonWidth() - 8)), this.getProxy().yPosition, 0, 66, 4, 20);
         this.blit(this.getProxy().xPosition + (int)(this.value * (this.getProxy().getButtonWidth() - 8)) + 4, this.getProxy().yPosition, 196, 66, 4, 20);
      }
   }

   @Override
   public void clicked(int i, int j) {
      this.value = (float)(i - (this.getProxy().xPosition + 4)) / (this.getProxy().getButtonWidth() - 8);
      this.value = MathHelper.clamp_float(this.value, 0.0F, 1.0F);
      this.clicked(this.toValue(this.value));
      this.getProxy().displayString = this.getMessage();
      this.sliding = true;
   }

   public void clicked(float f) {
   }

   @Override
   public void released(int i, int j) {
      this.sliding = false;
   }
}
