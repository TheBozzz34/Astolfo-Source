package bypass.via.gui;

import bypass.via.ViaFabric;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class ProtocolButton extends GuiButton {
   private float x;
   public boolean K;
   public static float P;

   public ProtocolButton(int i, int j, int k) {
      super(i, j, k, 100, 20, "");
      this.x = P;
      this.displayString = "Protocol: " + ViaFabric.INSTANCE.getVersion().getName();
   }

   @Override
   public int getHoverState(boolean mouseOver) {
      return 0;
   }

   @Override
   public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         if (this.K) {
            this.x = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
            this.x = MathHelper.clamp_float(this.x, 0.0F, 1.0F);
            this.q(this.x);
         }

         mc.getTextureManager().bindTexture(buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.xPosition + (int)(this.x * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.xPosition + (int)(this.x * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
      }
   }

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.x = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
         this.x = MathHelper.clamp_float(this.x, 0.0F, 1.0F);
         this.q(this.x);
         this.K = true;
         return true;
      } else {
         return false;
      }
   }

   private void q(float f) {
      ProtocolVersion protocolVersion = GuiMultiplayer.O.get(this.o(f));
      ViaFabric.INSTANCE.setVersion(protocolVersion);
      P = f;
      this.displayString = "Protocol: " + ViaFabric.INSTANCE.getVersion().getName();
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY) {
      this.K = false;
   }

   public int o(double d) {
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
