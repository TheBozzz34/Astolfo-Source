package net.minecraft.client.gui;

import bypass.Astolfo;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class GuiButton extends Gui {
   public static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
   public int width;
   public int height;
   public int xPosition;
   public int yPosition;
   public String displayString;
   public int id;
   public boolean enabled;
   public boolean visible;
   public boolean hovered;
   private static int B;
   private static int p;
   private static boolean q;
   public float f = 0.1F;
   public boolean n;
   public int t;
   private static final long b = 22795320404817L;

   public GuiButton(int var1, int var2, int var3, String var4) {
      this(var1, var2, var3, 200, 20, var4);
   }

   public GuiButton(int var1, int var2, int var3, int var4, int var5, String var6) {
      this.width = 200;
      this.height = 20;
      this.enabled = true;
      this.visible = true;
      this.id = var1;
      this.xPosition = var2;
      this.yPosition = var3;
      this.width = var4;
      this.height = var5;
      this.displayString = var6;
   }

   public int getHoverState(boolean var1) {
      byte var5 = 1;
      if (!this.enabled) {
         var5 = 0;
      } else if (var1) {
         var5 = 2;
      }

      return var5;
   }

   public void drawButton(Minecraft var1, int var2, int var3) {
      long var4 = b ^ 78581708617997L;
      long var6 = var4 ^ 113303742019696L;
      if (this.visible) {
         if (!q) {
            int var9 = ARBShaderObjects.glCreateShaderObjectARB(35632);
            ARBShaderObjects.glShaderSourceARB(var9, "#version 110\nuniform vec4 color;void main() { gl_FragColor = color;}");
            ARBShaderObjects.glCompileShaderARB(var9);
            System.err.println(ARBShaderObjects.glGetInfoLogARB(var9, 200));
            ARBShaderObjects.glAttachObjectARB(B = ARBShaderObjects.glCreateProgramObjectARB(), var9);
            ARBShaderObjects.glLinkProgramARB(B);
            ARBShaderObjects.glValidateProgramARB(B);
            q = true;
         }

         this.hovered = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
         GL11.glPushMatrix();
         GL11.glDisable(3553);
         drawRect(
            this.xPosition,
            this.yPosition,
            this.xPosition + this.width,
            this.yPosition + this.height,
            this.enabled
               ? (
                  this.hovered
                     ? (this.n ? new Color(18, 18, 18, this.t).getRGB() : -1726737388)
                     : (this.n ? new Color(18, 18, 18, this.t).getRGB() : -2012081646)
               )
               : 1426063360
         );
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(3008);
         float var15 = var1.theWorld == null ? 0.1F : 0.01F;
         if (this.hovered && this.f < 1.0F) {
            this.f += var15;
         } else if (this.f > 0.5F && !this.hovered) {
            this.f -= var15;
         }

         if (this.f < 0.5F) {
            this.f = 0.5F;
         } else if (this.f > 1.0F) {
            this.f = 1.0F;
         }

         GL11.glDisable(2848);
         GL11.glLineWidth(1.0F);
         GL11.glHint(3154, 4352);
         float var10 = Astolfo.INSTANCE.getColor().getRed() / 255.0F;
         float var11 = Astolfo.INSTANCE.getColor().getGreen() / 255.0F;
         float var12 = Astolfo.INSTANCE.getColor().getBlue() / 255.0F;
         ARBShaderObjects.glUseProgramObjectARB(B);
         if ((!this.hovered || !this.enabled) && (this.hovered || !this.enabled || !(this.f > 0.5F))) {
            if (this.enabled) {
               ARBShaderObjects.glUniform4fARB(p, 0.2F, 0.2F, 0.2F, this.f);
            } else {
               ARBShaderObjects.glUniform4fARB(p, 0.1F, 0.1F, 0.1F, this.f);
            }
         } else {
            ARBShaderObjects.glUniform4fARB(p, var10, var11, var12, this.f);
         }

         p = ARBShaderObjects.glGetUniformLocationARB(B, "color");
         GL11.glBegin(2);
         long var10000 = 1993990222346444441L ^ var4;
         GL11.glVertex2d(this.xPosition, this.yPosition);
         GL11.glVertex2d(this.xPosition + this.width, this.yPosition);
         GL11.glVertex2d(this.xPosition + this.width, this.yPosition + this.height);
         GL11.glVertex2d(this.xPosition, this.yPosition + this.height);
         GL11.glEnd();
         ARBShaderObjects.glUseProgramObjectARB(0);
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glPopMatrix();
         this.mouseDragged(var1, var2, var3);
         int var13 = this.n ? new Color(224, 224, 224, this.t).getRGB() : -2039584;
         if (!this.enabled) {
            var13 = -6250336;
         } else if (this.hovered) {
            var13 = Astolfo.INSTANCE.getColorSetting().getValue().getRGB();
         }

         String var14 = this.enabled ? this.displayString : EnumChatFormatting.ITALIC + this.displayString;
         this.d(var1.fontRendererObj, var14, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var13);
      }
   }

   public void mouseDragged(Minecraft var1, int var2, int var3) {
   }

   public void mouseReleased(int var1, int var2) {
   }

   public boolean mousePressed(Minecraft var1, int var2, int var3) {
      return this.enabled
         && this.visible
         && var2 >= this.xPosition
         && var3 >= this.yPosition
         && var2 < this.xPosition + this.width
         && var3 < this.yPosition + this.height;
   }

   public boolean isMouseOver() {
      return this.hovered;
   }

   public void drawButtonForegroundLayer(int var1, int var2) {
   }

   public void playPressSound(SoundHandler var1) {
      var1.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
   }

   public int getButtonWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }
}
