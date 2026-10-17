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
   private static int outlineShaderProgram;
   private static int colorUniformLocation;
   private static boolean shaderInitialized;
   public float outlineAlpha = 0.1F;
   public boolean useCustomAlpha;
   public int customAlpha;

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
      if (this.visible) {
         if (!shaderInitialized) {
            int i = ARBShaderObjects.glCreateShaderObjectARB(35632);
            ARBShaderObjects.glShaderSourceARB(i, "#version 110\nuniform vec4 color;void main() { gl_FragColor = color;}");
            ARBShaderObjects.glCompileShaderARB(i);
            System.err.println(ARBShaderObjects.glGetInfoLogARB(i, 200));
            ARBShaderObjects.glAttachObjectARB(outlineShaderProgram = ARBShaderObjects.glCreateProgramObjectARB(), i);
            ARBShaderObjects.glLinkProgramARB(outlineShaderProgram);
            ARBShaderObjects.glValidateProgramARB(outlineShaderProgram);
            shaderInitialized = true;
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
                     ? (this.useCustomAlpha ? new Color(18, 18, 18, this.customAlpha).getRGB() : -1726737388)
                     : (this.useCustomAlpha ? new Color(18, 18, 18, this.customAlpha).getRGB() : -2012081646)
               )
               : 1426063360
         );
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(3008);
         float f = mc.theWorld == null ? 0.1F : 0.01F;
         if (this.hovered && this.outlineAlpha < 1.0F) {
            this.outlineAlpha += f;
         } else if (this.outlineAlpha > 0.5F && !this.hovered) {
            this.outlineAlpha -= f;
         }

         if (this.outlineAlpha < 0.5F) {
            this.outlineAlpha = 0.5F;
         } else if (this.outlineAlpha > 1.0F) {
            this.outlineAlpha = 1.0F;
         }

         GL11.glDisable(2848);
         GL11.glLineWidth(1.0F);
         GL11.glHint(3154, 4352);
         float g = Astolfo.INSTANCE.getColor().getRed() / 255.0F;
         float h = Astolfo.INSTANCE.getColor().getGreen() / 255.0F;
         float f2 = Astolfo.INSTANCE.getColor().getBlue() / 255.0F;
         ARBShaderObjects.glUseProgramObjectARB(outlineShaderProgram);
         if ((!this.hovered || !this.enabled) && (this.hovered || !this.enabled || !(this.outlineAlpha > 0.5F))) {
            if (this.enabled) {
               ARBShaderObjects.glUniform4fARB(colorUniformLocation, 0.2F, 0.2F, 0.2F, this.outlineAlpha);
            } else {
               ARBShaderObjects.glUniform4fARB(colorUniformLocation, 0.1F, 0.1F, 0.1F, this.outlineAlpha);
            }
         } else {
            ARBShaderObjects.glUniform4fARB(colorUniformLocation, g, h, f2, this.outlineAlpha);
         }

         colorUniformLocation = ARBShaderObjects.glGetUniformLocationARB(outlineShaderProgram, "color");
         GL11.glBegin(2);
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
         int j = this.useCustomAlpha ? new Color(224, 224, 224, this.customAlpha).getRGB() : -2039584;
         if (!this.enabled) {
            j = -6250336;
         } else if (this.hovered) {
            j = Astolfo.INSTANCE.getColorSetting().getValue().getRGB();
         }

         String string = this.enabled ? this.displayString : EnumChatFormatting.ITALIC + this.displayString;
         this.drawCenteredString(mc.fontRendererObj, string, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
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
