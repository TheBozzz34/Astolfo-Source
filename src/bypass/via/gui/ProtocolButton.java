package bypass.via.gui;

import bypass.via.ViaFabric;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class ProtocolButton extends GuiButton {
   private float sliderValue;
   public boolean dragging;
   public static float lastSliderValue;

   public ProtocolButton(int i, int j, int k) {
      super(i, j, k, 100, 20, "");
      this.sliderValue = lastSliderValue;
      this.displayString = "Protocol: " + ViaFabric.INSTANCE.getVersion().getName();
   }

   @Override
   public int getHoverState(boolean mouseOver) {
      return 0;
   }

   @Override
   public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         if (this.dragging) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            this.applySliderValue(this.sliderValue);
         }

         mc.getTextureManager().bindTexture(buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
      }
   }

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
         this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
         this.applySliderValue(this.sliderValue);
         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   private void applySliderValue(float f) {
      ProtocolVersion protocolVersion = GuiMultiplayer.supportedProtocolVersions.get(this.getProtocolIndex(f));
      ViaFabric.INSTANCE.setVersion(protocolVersion);
      lastSliderValue = f;
      this.displayString = "Protocol: " + ViaFabric.INSTANCE.getVersion().getName();
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY) {
      this.dragging = false;
   }

   public int getProtocolIndex(double d) {
      if (d == 0.0) {
         ViaFabric.enabled = false;
         return 0;
      }

      int i = 0;

      for (double e = 0.0; e <= d; e += 0.033) {
         i++;
      }

      ViaFabric.enabled = true;
      return i;
   }
}
