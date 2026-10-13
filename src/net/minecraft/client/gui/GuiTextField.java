package net.minecraft.client.gui;

import bypass.Astolfo;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.ARBShaderObjects;

public class GuiTextField extends Gui {
   private final int id;
   private final FontRenderer fontRendererInstance;
   public int xPosition;
   public int yPosition;
   private final int width;
   private final int height;
   private String text = "";
   private int maxStringLength = 32;
   private int A;
   private boolean enableBackgroundDrawing = true;
   private boolean canLoseFocus = true;
   private boolean isFocused;
   private boolean o = true;
   private int lineScrollOffset;
   private int cursorPosition;
   private int selectionEnd;
   private int t = 14737632;
   private int b = 7368816;
   private boolean i = true;
   private GuiPageButtonList.GuiResponder field_175210_x;
   private Predicate<String> validator = Predicates.alwaysTrue();
   public boolean w;
   public int I;
   private static boolean u;
   private static int r;
   private static int n;
   public float d = 0.1F;
   private static final long e = 44900617446799L;

   public GuiTextField(int var1, FontRenderer var2, int var3, int var4, int var5, int var6) {
      this.id = var1;
      this.fontRendererInstance = var2;
      this.xPosition = var3;
      this.yPosition = var4;
      this.width = var5;
      this.height = var6;
   }

   public void func_175207_a(GuiPageButtonList.GuiResponder var1) {
      this.field_175210_x = var1;
   }

   public void updateCursorCounter() {
      this.A++;
   }

   public void setText(String var1) {
      if (this.validator.apply(var1)) {
         if (var1.length() > this.maxStringLength) {
            this.text = var1.substring(0, this.maxStringLength);
         } else {
            this.text = var1;
         }

         this.setCursorPositionEnd();
      }
   }

   public String getText() {
      return this.text;
   }

   public String getSelectedText() {
      int var4 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int var5 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      return this.text.substring(var4, var5);
   }

   public void setValidator(Predicate<String> var1) {
      this.validator = var1;
   }

   public void writeText(String var1) {
      String var5 = "";
      String var6 = ChatAllowedCharacters.filterAllowedCharacters(var1);
      int var7 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int var8 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      int var9 = this.maxStringLength - this.text.length() - (var7 - var8);
      int var10 = 0;
      if (this.text.length() > 0) {
         var5 = var5 + this.text.substring(0, var7);
      }

      if (var9 < var6.length()) {
         var5 = var5 + var6.substring(0, var9);
         var10 = var9;
      } else {
         var5 = var5 + var6;
         var10 = var6.length();
      }

      if (this.text.length() > 0 && var8 < this.text.length()) {
         var5 = var5 + this.text.substring(var8);
      }

      if (this.validator.apply(var5)) {
         this.text = var5;
         this.moveCursorBy(var7 - this.selectionEnd + var10);
         if (this.field_175210_x != null) {
            this.field_175210_x.func_175319_a(this.id, this.text);
         }
      }
   }

   public void deleteWords(int var1) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(var1) - this.cursorPosition);
         }
      }
   }

   public void deleteFromCursor(int var1) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean var5 = var1 < 0;
            int var6 = var5 ? this.cursorPosition + var1 : this.cursorPosition;
            int var7 = var5 ? this.cursorPosition : this.cursorPosition + var1;
            String var8 = "";
            if (var6 >= 0) {
               var8 = this.text.substring(0, var6);
            }

            if (var7 < this.text.length()) {
               var8 = var8 + this.text.substring(var7);
            }

            if (this.validator.apply(var8)) {
               this.text = var8;
               if (var5) {
                  this.moveCursorBy(var1);
               }

               if (this.field_175210_x != null) {
                  this.field_175210_x.func_175319_a(this.id, this.text);
               }
            }
         }
      }
   }

   public int getId() {
      return this.id;
   }

   public int getNthWordFromCursor(int var1) {
      return this.getNthWordFromPos(var1, this.getCursorPosition());
   }

   public int getNthWordFromPos(int var1, int var2) {
      return this.func_146197_a(var1, var2, true);
   }

   public int func_146197_a(int var1, int var2, boolean var3) {
      int var7 = var2;
      boolean var8 = var1 < 0;
      int var9 = Math.abs(var1);

      for (int var10 = 0; var10 < var9; var10++) {
         if (!var8) {
            int var11 = this.text.length();
            var7 = this.text.indexOf(32, var7);
            if (var7 == -1) {
               var7 = var11;
            } else {
               while (var3 && var7 < var11 && this.text.charAt(var7) == ' ') {
                  var7++;
               }
            }
         } else {
            while (var3 && var7 > 0 && this.text.charAt(var7 - 1) == ' ') {
               var7--;
            }

            while (var7 > 0 && this.text.charAt(var7 - 1) != ' ') {
               var7--;
            }
         }
      }

      return var7;
   }

   public void moveCursorBy(int var1) {
      this.setCursorPosition(this.selectionEnd + var1);
   }

   public void setCursorPosition(int var1) {
      this.cursorPosition = var1;
      int var2 = this.text.length();
      this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, var2);
      this.setSelectionPos(this.cursorPosition);
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public boolean textboxKeyTyped(char var1, int var2) {
      if (!this.isFocused) {
         return false;
      }

      if (GuiScreen.isKeyComboCtrlA(var2)) {
         this.setCursorPositionEnd();
         this.setSelectionPos(0);
         return true;
      }

      if (GuiScreen.isKeyComboCtrlC(var2)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         return true;
      }

      if (GuiScreen.isKeyComboCtrlV(var2)) {
         if (this.o) {
            this.writeText(GuiScreen.getClipboardString());
         }

         return true;
      } else if (GuiScreen.isKeyComboCtrlX(var2)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         if (this.o) {
            this.writeText("");
         }

         return true;
      } else {
         switch (var2) {
            case 14:
               if (GuiScreen.isCtrlKeyDown()) {
                  if (this.o) {
                     this.deleteWords(-1);
                  }
               } else if (this.o) {
                  this.deleteFromCursor(-1);
               }

               return true;
            case 199:
               if (GuiScreen.isShiftKeyDown()) {
                  this.setSelectionPos(0);
               } else {
                  this.setCursorPositionZero();
               }

               return true;
            case 203:
               if (GuiScreen.isShiftKeyDown()) {
                  if (GuiScreen.isCtrlKeyDown()) {
                     this.setSelectionPos(this.getNthWordFromPos(-1, this.m()));
                  } else {
                     this.setSelectionPos(this.m() - 1);
                  }
               } else if (GuiScreen.isCtrlKeyDown()) {
                  this.setCursorPosition(this.getNthWordFromCursor(-1));
               } else {
                  this.moveCursorBy(-1);
               }

               return true;
            case 205:
               if (GuiScreen.isShiftKeyDown()) {
                  if (GuiScreen.isCtrlKeyDown()) {
                     this.setSelectionPos(this.getNthWordFromPos(1, this.m()));
                  } else {
                     this.setSelectionPos(this.m() + 1);
                  }
               } else if (GuiScreen.isCtrlKeyDown()) {
                  this.setCursorPosition(this.getNthWordFromCursor(1));
               } else {
                  this.moveCursorBy(1);
               }

               return true;
            case 207:
               if (GuiScreen.isShiftKeyDown()) {
                  this.setSelectionPos(this.text.length());
               } else {
                  this.setCursorPositionEnd();
               }

               return true;
            case 211:
               if (GuiScreen.isCtrlKeyDown()) {
                  if (this.o) {
                     this.deleteWords(1);
                  }
               } else if (this.o) {
                  this.deleteFromCursor(1);
               }

               return true;
            default:
               if (ChatAllowedCharacters.isAllowedCharacter(var1)) {
                  if (this.o) {
                     this.writeText(Character.toString(var1));
                  }

                  return true;
               } else {
                  return false;
               }
         }
      }
   }

   public void mouseClicked(int var1, int var2, int var3) {
      boolean var7 = var1 >= this.xPosition && var1 < this.xPosition + this.width && var2 >= this.yPosition && var2 < this.yPosition + this.height;
      if (this.canLoseFocus) {
         this.setFocused(var7);
      }

      if (this.isFocused && var7 && var3 == 0) {
         int var8 = var1 - this.xPosition;
         if (this.enableBackgroundDrawing) {
            var8 -= 4;
         }

         String var9 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(var9, var8).length() + this.lineScrollOffset);
      }
   }

   public void drawTextBox() {
      long var1 = e ^ 96417654235985L;
      long var3 = var1 ^ 83189695294706L;
      if (this.getVisible()) {
         if (!u) {
            int var6 = ARBShaderObjects.glCreateShaderObjectARB(35632);
            ARBShaderObjects.glShaderSourceARB(var6, "#version 110\nuniform vec4 color;void main() { gl_FragColor = color;}");
            ARBShaderObjects.glCompileShaderARB(var6);
            System.err.println(ARBShaderObjects.glGetInfoLogARB(var6, 200));
            ARBShaderObjects.glAttachObjectARB(r = ARBShaderObjects.glCreateProgramObjectARB(), var6);
            ARBShaderObjects.glLinkProgramARB(r);
            ARBShaderObjects.glValidateProgramARB(r);
            u = true;
         }

         if (this.getEnableBackgroundDrawing()) {
            float var18 = Minecraft.getMinecraft().theWorld == null ? 0.1F : 0.01F;
            if (this.isFocused() && this.d < 1.0F) {
               this.d += var18;
            } else if (this.d > 0.5F && !this.isFocused) {
               this.d -= var18;
            }

            if (this.d < 0.5F) {
               this.d = 0.5F;
            } else if (this.d > 1.0F) {
               this.d = 1.0F;
            }

            ARBShaderObjects.glUseProgramObjectARB(r);
            float var7 = Astolfo.INSTANCE.getColor().getRed() / 255.0F;
            float var8 = Astolfo.INSTANCE.getColor().getGreen() / 255.0F;
            float var9 = Astolfo.INSTANCE.getColor().getBlue() / 255.0F;
            if (this.isFocused() && this.o) {
               ARBShaderObjects.glUniform4fARB(n, var7, var8, var9, this.d);
            } else if (this.o) {
               ARBShaderObjects.glUniform4fARB(n, 0.5F, 0.5F, 0.5F, this.d);
            } else {
               ARBShaderObjects.glUniform4fARB(n, 0.1F, 0.1F, 0.1F, this.d);
            }

            n = ARBShaderObjects.glGetUniformLocationARB(r, "color");
            drawRect(
               this.xPosition,
               this.yPosition + this.height - 1,
               this.xPosition + this.width,
               this.yPosition + this.height,
               this.isFocused() ? -65281 : -13421773
            );
            ARBShaderObjects.glUseProgramObjectARB(0);
         }

         int var19 = this.w ? new Color(224, 224, 224, this.I).getRGB() : (this.o ? this.t : this.b);
         int var20 = this.cursorPosition - this.lineScrollOffset;
         int var21 = this.selectionEnd - this.lineScrollOffset;
         String var22 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         boolean var10 = var20 >= 0 && var20 <= var22.length();
         boolean var11 = this.isFocused && this.A / 6 % 2 == 0 && var10;
         int var12 = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
         int var13 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
         int var14 = var12;
         if (var21 > var22.length()) {
            var21 = var22.length();
         }

         if (var22.length() > 0) {
            String var15 = var10 ? var22.substring(0, var20) : var22;
            var14 = this.fontRendererInstance.drawStringWithShadow(var15, var12, var13, var19);
         }

         boolean var24 = this.cursorPosition < this.text.length() || this.text.length() >= this.M();
         int var16 = var14;
         if (!var10) {
            var16 = var20 > 0 ? var12 + this.width : var12;
         } else if (var24) {
            var16 = var14 - 1;
            var14--;
         }

         if (var22.length() > 0 && var10 && var20 < var22.length()) {
            var14 = this.fontRendererInstance.drawStringWithShadow(var22.substring(var20), var14, var13, var19);
         }

         if (var11) {
            if (var24) {
               Gui.drawRect(var16, var13 - 1, var16 + 1, var13 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
            } else {
               this.fontRendererInstance.drawStringWithShadow("_", var16, var13, var19);
            }
         }

         if (var21 != var20) {
            int var17 = var12 + this.fontRendererInstance.getStringWidth(var22.substring(0, var21));
            this.drawCursorVertical(var16, var13 - 1, var17 - 1, var13 + 1 + this.fontRendererInstance.FONT_HEIGHT);
         }
      }
   }

   private void drawCursorVertical(int var1, int var2, int var3, int var4) {
      long var5 = e ^ 54923105916482L;
      if (var1 < var3) {
         int var8 = var1;
         var1 = var3;
         var3 = var8;
      }

      if (var2 < var4) {
         int var10 = var2;
         var2 = var4;
         var4 = var10;
      }

      if (var3 > this.xPosition + this.width) {
         var3 = this.xPosition + this.width;
      }

      if (var1 > this.xPosition + this.width) {
         var1 = this.xPosition + this.width;
      }

      Tessellator var11 = Tessellator.getInstance();
      WorldRenderer var9 = var11.getWorldRenderer();
      long var10000 = 4122863923570950327L ^ var5;
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(5387);
      var9.begin(7, DefaultVertexFormats.POSITION);
      var9.pos(var1, var4, 0.0).endVertex();
      var9.pos(var3, var4, 0.0).endVertex();
      var9.pos(var3, var2, 0.0).endVertex();
      var9.pos(var1, var2, 0.0).endVertex();
      var11.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
   }

   public void setMaxStringLength(int var1) {
      this.maxStringLength = var1;
      if (this.text.length() > var1) {
         this.text = this.text.substring(0, var1);
      }
   }

   public int M() {
      return this.maxStringLength;
   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   public boolean getEnableBackgroundDrawing() {
      return this.enableBackgroundDrawing;
   }

   public void setEnableBackgroundDrawing(boolean var1) {
      this.enableBackgroundDrawing = var1;
   }

   public void setTextColor(int var1) {
      this.t = var1;
   }

   public void setDisabledTextColour(int var1) {
      this.b = var1;
   }

   public void setFocused(boolean var1) {
      if (var1 && !this.isFocused) {
         this.A = 0;
      }

      this.isFocused = var1;
   }

   public boolean isFocused() {
      return this.isFocused;
   }

   public void setEnabled(boolean var1) {
      this.o = var1;
   }

   public int m() {
      return this.selectionEnd;
   }

   public int getWidth() {
      return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
   }

   public void setSelectionPos(int var1) {
      int var5 = this.text.length();
      if (var1 > var5) {
         var1 = var5;
      }

      if (var1 < 0) {
         var1 = 0;
      }

      this.selectionEnd = var1;
      if (this.fontRendererInstance != null) {
         if (this.lineScrollOffset > var5) {
            this.lineScrollOffset = var5;
         }

         int var6 = this.getWidth();
         String var7 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), var6);
         int var8 = var7.length() + this.lineScrollOffset;
         if (var1 == this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - this.fontRendererInstance.trimStringToWidth(this.text, var6, true).length();
         }

         if (var1 > var8) {
            this.lineScrollOffset += var1 - var8;
         } else if (var1 <= this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - (this.lineScrollOffset - var1);
         }

         this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, var5);
      }
   }

   public void setCanLoseFocus(boolean var1) {
      this.canLoseFocus = var1;
   }

   public boolean getVisible() {
      return this.i;
   }

   public void setVisible(boolean var1) {
      this.i = var1;
   }
}
