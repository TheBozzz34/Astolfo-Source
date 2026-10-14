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

   public GuiButton(int buttonId, int x, int y, String buttonText) {
      this(buttonId, x, y, 200, 20, buttonText);
   }

   public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
      this.width = 200;
      this.height = 20;
      this.enabled = true;
      this.visible = true;
      this.id = buttonId;
      this.xPosition = x;
      this.yPosition = y;
      this.width = widthIn;
      this.height = heightIn;
      this.displayString = buttonText;
   }

   public int getHoverState(boolean mouseOver) {
      byte b = 1;
      if (!this.enabled) {
         b = 0;
      } else if (mouseOver) {
         b = 2;
      }

      return b;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      long l = b ^ 78581708617997L;
      long m = l ^ 113303742019696L;
      if (this.visible) {
         if (!q) {
            int i = ARBShaderObjects.glCreateShaderObjectARB(35632);
            ARBShaderObjects.glShaderSourceARB(i, "#version 110\nuniform vec4 color;void main() { gl_FragColor = color;}");
            ARBShaderObjects.glCompileShaderARB(i);
            System.err.println(ARBShaderObjects.glGetInfoLogARB(i, 200));
            ARBShaderObjects.glAttachObjectARB(B = ARBShaderObjects.glCreateProgramObjectARB(), i);
            ARBShaderObjects.glLinkProgramARB(B);
            ARBShaderObjects.glValidateProgramARB(B);
            q = true;
         }

         this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
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
         float g = mc.theWorld == null ? 0.1F : 0.01F;
         if (this.hovered && this.f < 1.0F) {
            this.f += g;
         } else if (this.f > 0.5F && !this.hovered) {
            this.f -= g;
         }

         if (this.f < 0.5F) {
            this.f = 0.5F;
         } else if (this.f > 1.0F) {
            this.f = 1.0F;
         }

         GL11.glDisable(2848);
         GL11.glLineWidth(1.0F);
         GL11.glHint(3154, 4352);
         float h = Astolfo.INSTANCE.getColor().getRed() / 255.0F;
         float f2 = Astolfo.INSTANCE.getColor().getGreen() / 255.0F;
         float f3 = Astolfo.INSTANCE.getColor().getBlue() / 255.0F;
         ARBShaderObjects.glUseProgramObjectARB(B);
         if ((!this.hovered || !this.enabled) && (this.hovered || !this.enabled || !(this.f > 0.5F))) {
            if (this.enabled) {
               ARBShaderObjects.glUniform4fARB(p, 0.2F, 0.2F, 0.2F, this.f);
            } else {
               ARBShaderObjects.glUniform4fARB(p, 0.1F, 0.1F, 0.1F, this.f);
            }
         } else {
            ARBShaderObjects.glUniform4fARB(p, h, f2, f3, this.f);
         }

         p = ARBShaderObjects.glGetUniformLocationARB(B, "color");
         GL11.glBegin(2);
         long l2 = 1993990222346444441L ^ l;
         GL11.glVertex2d(this.xPosition, this.yPosition);
         GL11.glVertex2d(this.xPosition + this.width, this.yPosition);
         GL11.glVertex2d(this.xPosition + this.width, this.yPosition + this.height);
         GL11.glVertex2d(this.xPosition, this.yPosition + this.height);
         GL11.glEnd();
         ARBShaderObjects.glUseProgramObjectARB(0);
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glPopMatrix();
         this.mouseDragged(mc, mouseX, mouseY);
         int j = this.n ? new Color(224, 224, 224, this.t).getRGB() : -2039584;
         if (!this.enabled) {
            j = -6250336;
         } else if (this.hovered) {
            j = Astolfo.INSTANCE.getColorSetting().getValue().getRGB();
         }

         String string = this.enabled ? this.displayString : EnumChatFormatting.ITALIC + this.displayString;
         this.d(mc.fontRendererObj, string, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
      }
   }

   public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
   }

   public void mouseReleased(int mouseX, int mouseY) {
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      return this.enabled
         && this.visible
         && mouseX >= this.xPosition
         && mouseY >= this.yPosition
         && mouseX < this.xPosition + this.width
         && mouseY < this.yPosition + this.height;
   }

   public boolean isMouseOver() {
      return this.hovered;
   }

   public void drawButtonForegroundLayer(int mouseX, int mouseY) {
   }

   public void playPressSound(SoundHandler soundHandlerIn) {
      soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
   }

   public int getButtonWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }
}
