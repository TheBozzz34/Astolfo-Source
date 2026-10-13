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

   public RenderEvent(RenderType var1, float var2, ScaledResolution var3, GuiIngame var4) {
      this.type = var1;
      this.partialTicks = var2;
      this.resolution = var3;
      this.guiIngame = var4;
   }

   public RenderEvent(RenderType var1, float var2, double var3, double var5, double var7) {
      this.type = var1;
      this.partialTicks = var2;
      this.viewX = var3;
      this.viewY = var5;
      this.viewZ = var7;
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
