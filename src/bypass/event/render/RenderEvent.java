package bypass.event.render;

import bypass.event.Event;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

public class RenderEvent implements Event {
   private RenderType type;
   private float partialTicks;
   private ScaledResolution resolution;
   private double viewX;
   private double viewY;
   private double viewZ;
   private GuiIngame guiIngame;

   public RenderEvent(RenderType renderType, float f, ScaledResolution scaledResolution, GuiIngame guiIngame) {
      this.type = renderType;
      this.partialTicks = f;
      this.resolution = scaledResolution;
      this.guiIngame = guiIngame;
   }

   public RenderEvent(RenderType renderType, float f, double d, double e, double d2) {
      this.type = renderType;
      this.partialTicks = f;
      this.viewX = d;
      this.viewY = e;
      this.viewZ = d2;
   }

   public double getViewX() {
      return this.viewX;
   }

   public double getViewY() {
      return this.viewY;
   }

   public double getViewZ() {
      return this.viewZ;
   }

   public ScaledResolution getResolution() {
      return this.resolution;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public RenderType getType() {
      return this.type;
   }

   public GuiIngame getGuiIngame() {
      return this.guiIngame;
   }
}
