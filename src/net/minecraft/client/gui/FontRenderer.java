package net.minecraft.client.gui;

import bypass.Astolfo;
import bypass.module.ModuleManager;
import bypass.module.visuals.StreamerMode;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.src.FontUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
   private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
   private float[] charWidth = new float[256];
   public int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   private byte[] glyphWidth = new byte[65536];
   public int[] colorCode = new int[32];
   private ResourceLocation locationFontTexture;
   private final TextureManager renderEngine;
   private float posX;
   private float posY;
   private boolean unicodeFlag;
   private boolean bidiFlag;
   private float red;
   private float blue;
   private float green;
   private float alpha;
   private int textColor;
   private boolean randomStyle;
   private boolean boldStyle;
   private boolean italicStyle;
   private boolean underlineStyle;
   private boolean strikethroughStyle;
   public GameSettings gameSettings;
   public ResourceLocation locationFontTextureBase;
   public boolean enabled = true;
   public float offsetBold = 1.0F;
   private int g = new Color(0, 0, 0, 120).getRGB();

   public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
      this.gameSettings = gameSettingsIn;
      this.locationFontTextureBase = location;
      this.locationFontTexture = location;
      this.renderEngine = textureManagerIn;
      this.unicodeFlag = unicode;
      this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
      this.bindTexture(this.locationFontTexture);

      for (int i = 0; i < 32; i++) {
         int j = (i >> 3 & 1) * 85;
         int k = (i >> 2 & 1) * 170 + j;
         int l = (i >> 1 & 1) * 170 + j;
         int m = (i >> 0 & 1) * 170 + j;
         if (i == 6) {
            k += 85;
         }

         if (gameSettingsIn.anaglyph) {
            int n = (k * 30 + l * 59 + m * 11) / 100;
            int i2 = (k * 30 + l * 70) / 100;
            int i3 = (k * 30 + m * 70) / 100;
            k = n;
            l = i2;
            m = i3;
         }

         if (i >= 16) {
            k /= 4;
            l /= 4;
            m /= 4;
         }

         this.colorCode[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | m & 0xFF;
      }

      this.readGlyphSizes();
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

      for (int i = 0; i < unicodePageLocations.length; i++) {
         unicodePageLocations[i] = null;
      }

      this.readFontTexture();
      this.readGlyphSizes();
   }

   private void readFontTexture() {
      BufferedImage bufferedImage;
      try {
         bufferedImage = TextureUtil.readBufferedImage(this.getResourceInputStream(this.locationFontTexture));
      } catch (IOException ioException) {
         throw new RuntimeException(ioException);
      }

      Properties properties = FontUtils.readFontProperties(this.locationFontTexture);
      int i = bufferedImage.getWidth();
      int j = bufferedImage.getHeight();
      int k = i / 16;
      int l = j / 16;
      float f = i / 128.0F;
      float g = Config.limit(f, 1.0F, 2.0F);
      this.offsetBold = 1.0F / g;
      float h = FontUtils.readFloat(properties, "offsetBold", -1.0F);
      if (h >= 0.0F) {
         this.offsetBold = h;
      }

      int[] ints = new int[i * j];
      bufferedImage.getRGB(0, 0, i, j, ints, 0, i);

      for (int m = 0; m < 256; m++) {
         int n = m % 16;
         int i2 = m / 16;
         int i3 = 0;

         for (i3 = k - 1; i3 >= 0; i3--) {
            int i4 = n * k + i3;
            boolean bl = true;

            for (int i5 = 0; i5 < l && bl; i5++) {
               int i6 = (i2 * l + i5) * i;
               int i7 = ints[i4 + i6];
               int i8 = i7 >> 24 & 0xFF;
               if (i8 > 16) {
                  bl = false;
               }
            }

            if (!bl) {
               break;
            }
         }

         if (m == 65) {
            m = m;
         }

         if (m == 32) {
            if (k <= 8) {
               i3 = (int)(2.0F * f);
            } else {
               i3 = (int)(1.5F * f);
            }
         }

         this.charWidth[m] = (i3 + 1) / f + 1.0F;
      }

      FontUtils.readCustomCharWidths(properties, this.charWidth);
   }

   private void readGlyphSizes() {
      InputStream inputStream = null;

      try {
         inputStream = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
         inputStream.read(this.glyphWidth);
      } catch (IOException ioException) {
         throw new RuntimeException(ioException);
      } finally {
         IOUtils.closeQuietly(inputStream);
      }
   }

   private float renderChar(char ch, boolean italic) {
      if (ch == ' ') {
         return !this.unicodeFlag ? this.charWidth[ch] : 4.0F;
      }

      int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
         .indexOf(ch);
      return i != -1 && !this.unicodeFlag ? this.renderDefaultChar(i, italic) : this.renderUnicodeChar(ch, italic);
   }

   private float renderDefaultChar(int ch, boolean italic) {
      int i = ch % 16 * 8;
      int j = ch / 16 * 8;
      int k = italic ? 1 : 0;
      this.bindTexture(this.locationFontTexture);
      float f = this.charWidth[ch];
      float g = 7.99F;
      GL11.glBegin(5);
      GL11.glTexCoord2f(i / 128.0F, j / 128.0F);
      GL11.glVertex3f(this.posX + k, this.posY, 0.0F);
      GL11.glTexCoord2f(i / 128.0F, (j + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX - k, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f((i + g - 1.0F) / 128.0F, j / 128.0F);
      GL11.glVertex3f(this.posX + g - 1.0F + k, this.posY, 0.0F);
      GL11.glTexCoord2f((i + g - 1.0F) / 128.0F, (j + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX + g - 1.0F - k, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return f;
   }

   private ResourceLocation getUnicodePageLocation(int page) {
      if (unicodePageLocations[page] == null) {
         unicodePageLocations[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page));
         unicodePageLocations[page] = FontUtils.getHdFontLocation(unicodePageLocations[page]);
      }

      return unicodePageLocations[page];
   }

   private void loadGlyphTexture(int page) {
      this.bindTexture(this.getUnicodePageLocation(page));
   }

   private float renderUnicodeChar(char ch, boolean italic) {
      if (this.glyphWidth[ch] == 0) {
         return 0.0F;
      }

      int i = ch / 256;
      this.loadGlyphTexture(i);
      int j = this.glyphWidth[ch] >>> 4;
      int k = this.glyphWidth[ch] & 15;
      j &= 15;
      float f = j;
      float g = k + 1;
      float h = ch % 16 * 16 + f;
      float f2 = (ch & 255) / 16 * 16;
      float f3 = g - f - 0.02F;
      float f4 = italic ? 1.0F : 0.0F;
      GL11.glBegin(5);
      GL11.glTexCoord2f(h / 256.0F, f2 / 256.0F);
      GL11.glVertex3f(this.posX + f4, this.posY, 0.0F);
      GL11.glTexCoord2f(h / 256.0F, (f2 + 15.98F) / 256.0F);
      GL11.glVertex3f(this.posX - f4, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f((h + f3) / 256.0F, f2 / 256.0F);
      GL11.glVertex3f(this.posX + f3 / 2.0F + f4, this.posY, 0.0F);
      GL11.glTexCoord2f((h + f3) / 256.0F, (f2 + 15.98F) / 256.0F);
      GL11.glVertex3f(this.posX + f3 / 2.0F - f4, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return (g - f) / 2.0F + 1.0F;
   }

   public int drawStringWithShadow(String text, float x, float y, int color) {
      return this.drawString(text, x, y, color, true);
   }

   public int D(String string, float f, float g, int i) {
      return !this.enabled ? 0 : this.drawString(string, f, g, i, false);
   }

   public int drawString(String text, float x, float y, int color, boolean dropShadow) {
      EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
      if (Minecraft.getMinecraft().theWorld != null && entityPlayerSP != null && Astolfo.INSTANCE.getModuleManager() != null && ModuleManager.streamerMode.isEnabled()) {
         if (StreamerMode.scrambleNames.getValue()) {
            for (String string : StreamerMode.seenPlayerNames) {
               if (text.contains(string)) {
                  text = this.F(text, string);
               }
            }

            for (NetworkPlayerInfo networkPlayerInfo : GuiPlayerTabOverlay.field_175252_a.sortedCopy(entityPlayerSP.sendQueue.getPlayerInfoMap())) {
               if (networkPlayerInfo.getGameProfile() != null && entityPlayerSP.getPlayerInfo() != networkPlayerInfo) {
                  String string2 = StreamerMode.getPlayerListName(networkPlayerInfo);
                  if (text.contains(string2)) {
                     text = this.F(text, string2);
                  }
               }
            }
         }

         if (entityPlayerSP.getPlayerInfo() != null && entityPlayerSP.getPlayerInfo().getGameProfile() != null && text.contains(entityPlayerSP.getPlayerInfo().getGameProfile().getName())
            )
          {
            text = text.replaceAll(entityPlayerSP.getPlayerInfo().getGameProfile().getName(), StreamerMode.spoofName.getValue());
         }
      }

      this.enableAlpha();
      this.resetStyles();
      int i;
      if (dropShadow) {
         i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
         i = Math.max(i, this.renderString(text, x, y, color, false));
      } else {
         i = this.renderString(text, x, y, color, false);
      }

      return i;
   }

   private String F(String string, String string2) {
      return string.replace(string2, "§k" + string2 + "§r");
   }

   private String bidiReorder(String text) {
      try {
         Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException arabicShapingException) {
         return text;
      }
   }

   private void resetStyles() {
      this.randomStyle = false;
      this.boldStyle = false;
      this.italicStyle = false;
      this.underlineStyle = false;
      this.strikethroughStyle = false;
   }

   private void renderStringAtPos(String text, boolean shadow) {
      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == 167 && i + 1 < text.length()) {
            int j = "0123456789abcdefklmnor".indexOf(text.toLowerCase().charAt(i + 1));
            if (j < 16) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               if (j < 0 || j > 15) {
                  j = 15;
               }

               if (shadow) {
                  j += 16;
               }

               int k = this.colorCode[j];
               if (Config.isCustomColors()) {
                  k = CustomColors.getTextColor(j, k);
               }

               this.textColor = k;
               this.setColor((k >> 16) / 255.0F, (k >> 8 & 0xFF) / 255.0F, (k & 0xFF) / 255.0F, this.alpha);
            } else if (j == 16) {
               this.randomStyle = true;
            } else if (j == 17) {
               this.boldStyle = true;
            } else if (j == 18) {
               this.strikethroughStyle = true;
            } else if (j == 19) {
               this.underlineStyle = true;
            } else if (j == 20) {
               this.italicStyle = true;
            } else if (j == 21) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               this.setColor(this.red, this.blue, this.green, this.alpha);
            }

            i++;
         } else {
            int l = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
               .indexOf(c);
            if (this.randomStyle && l != -1) {
               int m = this.getCharWidth(c);

               char c2;
               do {
                  l = this.fontRandom
                     .nextInt(
                        "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                           .length()
                     );
                  c2 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                     .charAt(l);
               } while (m != this.getCharWidth(c2));

               c = c2;
            }

            float f = l != -1 && !this.unicodeFlag ? this.offsetBold : 0.5F;
            boolean bl = (c == 0 || l == -1 || this.unicodeFlag) && shadow;
            if (bl) {
               this.posX -= f;
               this.posY -= f;
            }

            float g = this.renderChar(c, this.italicStyle);
            if (bl) {
               this.posX += f;
               this.posY += f;
            }

            if (this.boldStyle) {
               this.posX += f;
               if (bl) {
                  this.posX -= f;
                  this.posY -= f;
               }

               this.renderChar(c, this.italicStyle);
               this.posX -= f;
               if (bl) {
                  this.posX += f;
                  this.posY += f;
               }

               g += f;
            }

            if (this.strikethroughStyle) {
               Tessellator tessellator = Tessellator.getInstance();
               WorldRenderer worldRenderer = tessellator.getWorldRenderer();
               GlStateManager.disableTexture2D();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION);
               worldRenderer.pos(this.posX, this.posY + this.FONT_HEIGHT / 2, 0.0).endVertex();
               worldRenderer.pos(this.posX + g, this.posY + this.FONT_HEIGHT / 2, 0.0).endVertex();
               worldRenderer.pos(this.posX + g, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).endVertex();
               worldRenderer.pos(this.posX, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).endVertex();
               tessellator.draw();
               GlStateManager.enableTexture2D();
            }

            if (this.underlineStyle) {
               Tessellator tessellator2 = Tessellator.getInstance();
               WorldRenderer worldRenderer2 = tessellator2.getWorldRenderer();
               GlStateManager.disableTexture2D();
               worldRenderer2.begin(7, DefaultVertexFormats.POSITION);
               int n = this.underlineStyle ? -1 : 0;
               worldRenderer2.pos(this.posX + n, this.posY + this.FONT_HEIGHT, 0.0).endVertex();
               worldRenderer2.pos(this.posX + g, this.posY + this.FONT_HEIGHT, 0.0).endVertex();
               worldRenderer2.pos(this.posX + g, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).endVertex();
               worldRenderer2.pos(this.posX + n, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).endVertex();
               tessellator2.draw();
               GlStateManager.enableTexture2D();
            }

            this.posX += g;
         }
      }
   }

   private int renderStringAligned(String text, int x, int y, int width, int color, boolean dropShadow) {
      if (this.bidiFlag) {
         int i = this.getStringWidth(this.bidiReorder(text));
         x = x + width - i;
      }

      return this.renderString(text, x, y, color, dropShadow);
   }

   private int renderString(String text, float x, float y, int color, boolean dropShadow) {
      if (text == null) {
         return 0;
      }

      if (this.bidiFlag) {
         text = this.bidiReorder(text);
      }

      if ((color & -67108864) == 0) {
         color |= -16777216;
      }

      if (dropShadow) {
         color = (color & 16579836) >> 2 | color & 0xFF000000;
      }

      this.red = (color >> 16 & 0xFF) / 255.0F;
      this.blue = (color >> 8 & 0xFF) / 255.0F;
      this.green = (color & 0xFF) / 255.0F;
      this.alpha = (color >> 24 & 0xFF) / 255.0F;
      this.setColor(this.red, this.blue, this.green, this.alpha);
      this.posX = x;
      this.posY = y;
      this.renderStringAtPos(text, dropShadow);
      return (int)this.posX;
   }

   public void O(String string, int i, int j, int k) {
      this.D(StringUtils.stripControlCodes(string), i + 1, j, -16777216);
      this.D(StringUtils.stripControlCodes(string), i - 1, j, -16777216);
      this.D(StringUtils.stripControlCodes(string), i, j + 1, -16777216);
      this.D(StringUtils.stripControlCodes(string), i, j - 1, -16777216);
      this.D(string, i, j, k);
   }

   public void N(String string, float f, float g, int i) {
      this.D(StringUtils.stripControlCodes(string), f + 0.5F, g, i);
      this.D(StringUtils.stripControlCodes(string), f - 0.5F, g, i);
      this.D(StringUtils.stripControlCodes(string), f, g + 0.5F, i);
      this.D(StringUtils.stripControlCodes(string), f, g - 0.5F, i);
      this.D(string, f, g, i);
   }

   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      }

      float f = 0.0F;
      boolean bl = false;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         float g = this.getCharWidthFloat(c);
         if (g < 0.0F && i < text.length() - 1) {
            c = text.charAt(++i);
            if (c == 'l' || c == 'L') {
               bl = true;
            } else if (c == 'r' || c == 'R') {
               bl = false;
            }

            g = 0.0F;
         }

         f += g;
         if (bl && g > 0.0F) {
            f += this.unicodeFlag ? 1.0F : this.offsetBold;
         }
      }

      return (int)f;
   }

   public int getCharWidth(char character) {
      return Math.round(this.getCharWidthFloat(character));
   }

   private float getCharWidthFloat(char c) {
      if (c == 167) {
         return -1.0F;
      } else if (c == ' ') {
         return this.charWidth[32];
      } else {
         int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(c);
         if (c > 0 && i != -1 && !this.unicodeFlag) {
            return this.charWidth[i];
         } else if (this.glyphWidth[c] != 0) {
            int j = this.glyphWidth[c] >>> 4;
            int k = this.glyphWidth[c] & 15;
            j &= 15;
            k++;
            return (k - j) / 2 + 1;
         } else {
            return 0.0F;
         }
      }
   }

   public String trimStringToWidth(String text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   public String trimStringToWidth(String text, int width, boolean reverseIn) {
      StringBuilder stringBuilder = new StringBuilder();
      float f = 0.0F;
      int i = reverseIn ? text.length() - 1 : 0;
      int j = reverseIn ? -1 : 1;
      boolean bl = false;
      boolean bl2 = false;

      for (int k = i; k >= 0 && k < text.length() && f < width; k += j) {
         char c = text.charAt(k);
         float g = this.getCharWidthFloat(c);
         if (bl) {
            bl = false;
            if (c == 'l' || c == 'L') {
               bl2 = true;
            } else if (c == 'r' || c == 'R') {
               bl2 = false;
            }
         } else if (g < 0.0F) {
            bl = true;
         } else {
            f += g;
            if (bl2) {
               f++;
            }
         }

         if (f > width) {
            break;
         }

         if (reverseIn) {
            stringBuilder.insert(0, c);
         } else {
            stringBuilder.append(c);
         }
      }

      return stringBuilder.toString();
   }

   private String trimStringNewline(String text) {
      while (text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      this.resetStyles();
      this.textColor = textColor;
      str = this.trimStringNewline(str);
      this.renderSplitString(str, x, y, wrapWidth, false);
   }

   private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
      for (Object object : this.listFormattedStringToWidth(str, wrapWidth)) {
         this.renderStringAligned((String)object, x, y, wrapWidth, this.textColor, addShadow);
         y += this.FONT_HEIGHT;
      }
   }

   public int splitStringWidth(String str, int maxLength) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
   }

   public void setUnicodeFlag(boolean unicodeFlagIn) {
      this.unicodeFlag = unicodeFlagIn;
   }

   public boolean getUnicodeFlag() {
      return this.unicodeFlag;
   }

   public void setBidiFlag(boolean bidiFlagIn) {
      this.bidiFlag = bidiFlagIn;
   }

   public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
      return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
   }

   public String wrapFormattedStringToWidth(String str, int wrapWidth) {
      int i = this.sizeStringToWidth(str, wrapWidth);
      if (str.length() <= i) {
         return str;
      }

      String string = str.substring(0, i);
      char c = str.charAt(i);
      boolean bl = c == ' ' || c == '\n';
      String string2 = getFormatFromString(string) + str.substring(i + (bl ? 1 : 0));
      return string + "\n" + this.wrapFormattedStringToWidth(string2, wrapWidth);
   }

   private int sizeStringToWidth(String str, int wrapWidth) {
      int i = str.length();
      float f = 0.0F;
      int j = 0;
      int k = -1;
      boolean bl = false;

      while (j < i) {
         char c = str.charAt(j);
         switch (c) {
            case '\n':
               j--;
               break;
            case ' ':
               k = j;
            default:
               f += this.getCharWidthFloat(c);
               if (bl) {
                  f++;
               }
               break;
            case '§':
               if (j < i - 1) {
                  char c2 = str.charAt(++j);
                  if (c2 == 'l' || c2 == 'L') {
                     bl = true;
                  } else if (c2 == 'r' || c2 == 'R' || isFormatColor(c2)) {
                     bl = false;
                  }
               }
         }

         if (c == '\n') {
            k = ++j;
            break;
         }

         if (f > wrapWidth) {
            break;
         }

         j++;
      }

      return j != i && k != -1 && k < j ? k : j;
   }

   private static boolean isFormatColor(char colorChar) {
      return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
   }

   private static boolean isFormatSpecial(char formatChar) {
      return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
   }

   public static String getFormatFromString(String text) {
      String string = "";
      int i = -1;
      int j = text.length();

      while ((i = text.indexOf(167, i + 1)) != -1) {
         if (i < j - 1) {
            char c = text.charAt(i + 1);
            if (isFormatColor(c)) {
               string = "§" + c;
            } else if (isFormatSpecial(c)) {
               string = string + "§" + c;
            }
         }
      }

      return string;
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }

   public int getColorCode(char character) {
      int i = "0123456789abcdef".indexOf(character);
      if (i >= 0 && i < this.colorCode.length) {
         int j = this.colorCode[i];
         if (Config.isCustomColors()) {
            j = CustomColors.getTextColor(i, j);
         }

         return j;
      } else {
         return 16777215;
      }
   }

   public void setColor(float f, float g, float h, float f2) {
      GlStateManager.color(f, g, h, f2);
   }

   public void enableAlpha() {
      GlStateManager.enableAlpha();
   }

   public void bindTexture(ResourceLocation resourceLocation) {
      this.renderEngine.bindTexture(resourceLocation);
   }

   public InputStream getResourceInputStream(ResourceLocation resourceLocation) throws IOException {
      return Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
   }
}
