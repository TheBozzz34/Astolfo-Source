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
   private static final long c = 82417675109247L;

   public ProtocolButton(int var1, int var2, int var3) {
      super(var1, var2, var3, 100, 20, "");
      this.x = P;
      this.displayString = "Protocol: " + ViaFabric.INSTANCE.getVersion().getName();
   }

   @Override
   public int getHoverState(boolean var1) {
      return 0;
   }

   @Override
   public void mouseDragged(Minecraft var1, int var2, int var3) {
      if (this.visible) {
         if (this.K) {
            this.x = (float)(var2 - (this.xPosition + 4)) / (this.width - 8);
            this.x = MathHelper.clamp_float(this.x, 0.0F, 1.0F);
            this.q(this.x);
         }

         var1.getTextureManager().bindTexture(buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.xPosition + (int)(this.x * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.xPosition + (int)(this.x * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
      }
   }

   @Override
   public boolean mousePressed(Minecraft var1, int var2, int var3) {
      if (super.mousePressed(var1, var2, var3)) {
         this.x = (float)(var2 - (this.xPosition + 4)) / (this.width - 8);
         this.x = MathHelper.clamp_float(this.x, 0.0F, 1.0F);
         this.q(this.x);
         this.K = true;
         return true;
      } else {
         return false;
      }
   }

   private void q(float var1) {
      ProtocolVersion var4 = GuiMultiplayer.O.get(this.o(var1));
      ViaFabric.INSTANCE.setVersion(var4);
      P = var1;
      this.displayString = "Protocol: " + ViaFabric.INSTANCE.getVersion().getName();
   }

   @Override
   public void mouseReleased(int var1, int var2) {
      this.K = false;
   }

   public int o(double var1) {
      if (var1 == 0.0) {
         ViaFabric.enabled = false;
         return 0;
      }

      int var6 = 0;

      for (double var7 = 0.0; var7 <= var1; var7 += 0.033) {
         var6++;
      }

      ViaFabric.enabled = true;
      return var6;
   }
}
