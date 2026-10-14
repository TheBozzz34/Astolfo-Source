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

   public GuiTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
      this.id = componentId;
      this.fontRendererInstance = fontrendererObj;
      this.xPosition = x;
      this.yPosition = y;
      this.width = par5Width;
      this.height = par6Height;
   }

   public void func_175207_a(GuiPageButtonList.GuiResponder guiResponder) {
      this.field_175210_x = guiResponder;
   }

   public void updateCursorCounter() {
      this.A++;
   }

   public void setText(String string) {
      if (this.validator.apply(string)) {
         if (string.length() > this.maxStringLength) {
            this.text = string.substring(0, this.maxStringLength);
         } else {
            this.text = string;
         }

         this.setCursorPositionEnd();
      }
   }

   public String getText() {
      return this.text;
   }

   public String getSelectedText() {
      int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      return this.text.substring(i, j);
   }

   public void setValidator(Predicate<String> theValidator) {
      this.validator = theValidator;
   }

   public void writeText(String string) {
      String string2 = "";
      String string3 = ChatAllowedCharacters.filterAllowedCharacters(string);
      int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      int k = this.maxStringLength - this.text.length() - (i - j);
      int l = 0;
      if (this.text.length() > 0) {
         string2 = string2 + this.text.substring(0, i);
      }

      if (k < string3.length()) {
         string2 = string2 + string3.substring(0, k);
         l = k;
      } else {
         string2 = string2 + string3;
         l = string3.length();
      }

      if (this.text.length() > 0 && j < this.text.length()) {
         string2 = string2 + this.text.substring(j);
      }

      if (this.validator.apply(string2)) {
         this.text = string2;
         this.moveCursorBy(i - this.selectionEnd + l);
         if (this.field_175210_x != null) {
            this.field_175210_x.func_175319_a(this.id, this.text);
         }
      }
   }

   public void deleteWords(int i) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(i) - this.cursorPosition);
         }
      }
   }

   public void deleteFromCursor(int i) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean bl = i < 0;
            int j = bl ? this.cursorPosition + i : this.cursorPosition;
            int k = bl ? this.cursorPosition : this.cursorPosition + i;
            String string = "";
            if (j >= 0) {
               string = this.text.substring(0, j);
            }

            if (k < this.text.length()) {
               string = string + this.text.substring(k);
            }

            if (this.validator.apply(string)) {
               this.text = string;
               if (bl) {
                  this.moveCursorBy(i);
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

   public int getNthWordFromCursor(int i) {
      return this.getNthWordFromPos(i, this.getCursorPosition());
   }

   public int getNthWordFromPos(int i, int j) {
      return this.func_146197_a(i, j, true);
   }

   public int func_146197_a(int i, int j, boolean bl) {
      int k = j;
      boolean bl2 = i < 0;
      int l = Math.abs(i);

      for (int m = 0; m < l; m++) {
         if (!bl2) {
            int n = this.text.length();
            k = this.text.indexOf(32, k);
            if (k == -1) {
               k = n;
            } else {
               while (bl && k < n && this.text.charAt(k) == ' ') {
                  k++;
               }
            }
         } else {
            while (bl && k > 0 && this.text.charAt(k - 1) == ' ') {
               k--;
            }

            while (k > 0 && this.text.charAt(k - 1) != ' ') {
               k--;
            }
         }
      }

      return k;
   }

   public void moveCursorBy(int i) {
      this.setCursorPosition(this.selectionEnd + i);
   }

   public void setCursorPosition(int i) {
      this.cursorPosition = i;
      int j = this.text.length();
      this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, j);
      this.setSelectionPos(this.cursorPosition);
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public boolean textboxKeyTyped(char c, int i) {
      if (!this.isFocused) {
         return false;
      }

      if (GuiScreen.isKeyComboCtrlA(i)) {
         this.setCursorPositionEnd();
         this.setSelectionPos(0);
         return true;
      }

      if (GuiScreen.isKeyComboCtrlC(i)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         return true;
      }

      if (GuiScreen.isKeyComboCtrlV(i)) {
         if (this.o) {
            this.writeText(GuiScreen.getClipboardString());
         }

         return true;
      } else if (GuiScreen.isKeyComboCtrlX(i)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         if (this.o) {
            this.writeText("");
         }

         return true;
      } else {
         switch (i) {
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
               if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                  if (this.o) {
                     this.writeText(Character.toString(c));
                  }

                  return true;
               } else {
                  return false;
               }
         }
      }
   }

   public void mouseClicked(int i, int j, int k) {
      boolean bl = i >= this.xPosition && i < this.xPosition + this.width && j >= this.yPosition && j < this.yPosition + this.height;
      if (this.canLoseFocus) {
         this.setFocused(bl);
      }

      if (this.isFocused && bl && k == 0) {
         int l = i - this.xPosition;
         if (this.enableBackgroundDrawing) {
            l -= 4;
         }

         String string = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(string, l).length() + this.lineScrollOffset);
      }
   }

   public void drawTextBox() {
      long l = e ^ 96417654235985L;
      long m = l ^ 83189695294706L;
      if (this.getVisible()) {
         if (!u) {
            int i = ARBShaderObjects.glCreateShaderObjectARB(35632);
            ARBShaderObjects.glShaderSourceARB(i, "#version 110\nuniform vec4 color;void main() { gl_FragColor = color;}");
            ARBShaderObjects.glCompileShaderARB(i);
            System.err.println(ARBShaderObjects.glGetInfoLogARB(i, 200));
            ARBShaderObjects.glAttachObjectARB(r = ARBShaderObjects.glCreateProgramObjectARB(), i);
            ARBShaderObjects.glLinkProgramARB(r);
            ARBShaderObjects.glValidateProgramARB(r);
            u = true;
         }

         if (this.getEnableBackgroundDrawing()) {
            float f = Minecraft.getMinecraft().theWorld == null ? 0.1F : 0.01F;
            if (this.isFocused() && this.d < 1.0F) {
               this.d += f;
            } else if (this.d > 0.5F && !this.isFocused) {
               this.d -= f;
            }

            if (this.d < 0.5F) {
               this.d = 0.5F;
            } else if (this.d > 1.0F) {
               this.d = 1.0F;
            }

            ARBShaderObjects.glUseProgramObjectARB(r);
            float g = Astolfo.INSTANCE.getColor().getRed() / 255.0F;
            float h = Astolfo.INSTANCE.getColor().getGreen() / 255.0F;
            float f2 = Astolfo.INSTANCE.getColor().getBlue() / 255.0F;
            if (this.isFocused() && this.o) {
               ARBShaderObjects.glUniform4fARB(n, g, h, f2, this.d);
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

         int j = this.w ? new Color(224, 224, 224, this.I).getRGB() : (this.o ? this.t : this.b);
         int k = this.cursorPosition - this.lineScrollOffset;
         int i2 = this.selectionEnd - this.lineScrollOffset;
         String string = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         boolean bl = k >= 0 && k <= string.length();
         boolean bl2 = this.isFocused && this.A / 6 % 2 == 0 && bl;
         int i3 = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
         int i4 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
         int i5 = i3;
         if (i2 > string.length()) {
            i2 = string.length();
         }

         if (string.length() > 0) {
            String string2 = bl ? string.substring(0, k) : string;
            i5 = this.fontRendererInstance.drawStringWithShadow(string2, i3, i4, j);
         }

         boolean bl3 = this.cursorPosition < this.text.length() || this.text.length() >= this.M();
         int i6 = i5;
         if (!bl) {
            i6 = k > 0 ? i3 + this.width : i3;
         } else if (bl3) {
            i6 = i5 - 1;
            i5--;
         }

         if (string.length() > 0 && bl && k < string.length()) {
            i5 = this.fontRendererInstance.drawStringWithShadow(string.substring(k), i5, i4, j);
         }

         if (bl2) {
            if (bl3) {
               Gui.drawRect(i6, i4 - 1, i6 + 1, i4 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
            } else {
               this.fontRendererInstance.drawStringWithShadow("_", i6, i4, j);
            }
         }

         if (i2 != k) {
            int i7 = i3 + this.fontRendererInstance.getStringWidth(string.substring(0, i2));
            this.drawCursorVertical(i6, i4 - 1, i7 - 1, i4 + 1 + this.fontRendererInstance.FONT_HEIGHT);
         }
      }
   }

   private void drawCursorVertical(int i, int j, int k, int l) {
      long m = e ^ 54923105916482L;
      if (i < k) {
         int n = i;
         i = k;
         k = n;
      }

      if (j < l) {
         int i2 = j;
         j = l;
         l = i2;
      }

      if (k > this.xPosition + this.width) {
         k = this.xPosition + this.width;
      }

      if (i > this.xPosition + this.width) {
         i = this.xPosition + this.width;
      }

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      long l2 = 4122863923570950327L ^ m;
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(5387);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(i, l, 0.0).endVertex();
      worldRenderer.pos(k, l, 0.0).endVertex();
      worldRenderer.pos(k, j, 0.0).endVertex();
      worldRenderer.pos(i, j, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
   }

   public void setMaxStringLength(int i) {
      this.maxStringLength = i;
      if (this.text.length() > i) {
         this.text = this.text.substring(0, i);
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

   public void setEnableBackgroundDrawing(boolean bl) {
      this.enableBackgroundDrawing = bl;
   }

   public void setTextColor(int i) {
      this.t = i;
   }

   public void setDisabledTextColour(int i) {
      this.b = i;
   }

   public void setFocused(boolean bl) {
      if (bl && !this.isFocused) {
         this.A = 0;
      }

      this.isFocused = bl;
   }

   public boolean isFocused() {
      return this.isFocused;
   }

   public void setEnabled(boolean bl) {
      this.o = bl;
   }

   public int m() {
      return this.selectionEnd;
   }

   public int getWidth() {
      return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
   }

   public void setSelectionPos(int i) {
      int j = this.text.length();
      if (i > j) {
         i = j;
      }

      if (i < 0) {
         i = 0;
      }

      this.selectionEnd = i;
      if (this.fontRendererInstance != null) {
         if (this.lineScrollOffset > j) {
            this.lineScrollOffset = j;
         }

         int k = this.getWidth();
         String string = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), k);
         int l = string.length() + this.lineScrollOffset;
         if (i == this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - this.fontRendererInstance.trimStringToWidth(this.text, k, true).length();
         }

         if (i > l) {
            this.lineScrollOffset += i - l;
         } else if (i <= this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - (this.lineScrollOffset - i);
         }

         this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, j);
      }
   }

   public void setCanLoseFocus(boolean bl) {
      this.canLoseFocus = bl;
   }

   public boolean getVisible() {
      return this.i;
   }

   public void setVisible(boolean bl) {
      this.i = bl;
   }
}
