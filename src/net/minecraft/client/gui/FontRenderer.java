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
   private float[] d = new float[256];
   public int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   private byte[] glyphWidth = new byte[65536];
   public int[] H = new int[32];
   private ResourceLocation w;
   private final TextureManager renderEngine;
   private float posX;
   private float posY;
   private boolean F;
   private boolean bidiFlag;
   private float red;
   private float blue;
   private float green;
   private float alpha;
   private int textColor;
   private boolean I;
   private boolean B;
   private boolean Y;
   private boolean W;
   private boolean v;
   public GameSettings gameSettings;
   public ResourceLocation locationFontTextureBase;
   public boolean Z = true;
   public float offsetBold = 1.0F;
   private int g = new Color(0, 0, 0, 120).getRGB();
   private static final long b = 120209987115995L;

   public FontRenderer(GameSettings var1, ResourceLocation var2, TextureManager var3, boolean var4) {
      this.gameSettings = var1;
      this.locationFontTextureBase = var2;
      this.w = var2;
      this.renderEngine = var3;
      this.F = var4;
      this.w = FontUtils.getHdFontLocation(this.locationFontTextureBase);
      this.bindTexture(this.w);

      for (int var8 = 0; var8 < 32; var8++) {
         int var9 = (var8 >> 3 & 1) * 85;
         int var10 = (var8 >> 2 & 1) * 170 + var9;
         int var11 = (var8 >> 1 & 1) * 170 + var9;
         int var12 = (var8 >> 0 & 1) * 170 + var9;
         if (var8 == 6) {
            var10 += 85;
         }

         if (var1.anaglyph) {
            int var13 = (var10 * 30 + var11 * 59 + var12 * 11) / 100;
            int var14 = (var10 * 30 + var11 * 70) / 100;
            int var15 = (var10 * 30 + var12 * 70) / 100;
            var10 = var13;
            var11 = var14;
            var12 = var15;
         }

         if (var8 >= 16) {
            var10 /= 4;
            var11 /= 4;
            var12 /= 4;
         }

         this.H[var8] = (var10 & 0xFF) << 16 | (var11 & 0xFF) << 8 | var12 & 0xFF;
      }

      this.readGlyphSizes();
   }

   @Override
   public void onResourceManagerReload(IResourceManager var1) {
      this.w = FontUtils.getHdFontLocation(this.locationFontTextureBase);

      for (int var5 = 0; var5 < unicodePageLocations.length; var5++) {
         unicodePageLocations[var5] = null;
      }

      this.readFontTexture();
      this.readGlyphSizes();
   }

   private void readFontTexture() {
      BufferedImage var4;
      try {
         var4 = TextureUtil.readBufferedImage(this.getResourceInputStream(this.w));
      } catch (IOException var24) {
         throw new RuntimeException(var24);
      }

      Properties var5 = FontUtils.readFontProperties(this.w);
      int var6 = var4.getWidth();
      int var7 = var4.getHeight();
      int var8 = var6 / 16;
      int var9 = var7 / 16;
      float var10 = var6 / 128.0F;
      float var11 = Config.limit(var10, 1.0F, 2.0F);
      this.offsetBold = 1.0F / var11;
      float var12 = FontUtils.readFloat(var5, "offsetBold", -1.0F);
      if (var12 >= 0.0F) {
         this.offsetBold = var12;
      }

      int[] var13 = new int[var6 * var7];
      var4.getRGB(0, 0, var6, var7, var13, 0, var6);

      for (int var14 = 0; var14 < 256; var14++) {
         int var15 = var14 % 16;
         int var16 = var14 / 16;
         int var17 = 0;

         for (var17 = var8 - 1; var17 >= 0; var17--) {
            int var18 = var15 * var8 + var17;
            boolean var19 = true;

            for (int var20 = 0; var20 < var9 && var19; var20++) {
               int var21 = (var16 * var9 + var20) * var6;
               int var22 = var13[var18 + var21];
               int var23 = var22 >> 24 & 0xFF;
               if (var23 > 16) {
                  var19 = false;
               }
            }

            if (!var19) {
               break;
            }
         }

         if (var14 == 65) {
            var14 = var14;
         }

         if (var14 == 32) {
            if (var8 <= 8) {
               var17 = (int)(2.0F * var10);
            } else {
               var17 = (int)(1.5F * var10);
            }
         }

         this.d[var14] = (var17 + 1) / var10 + 1.0F;
      }

      FontUtils.readCustomCharWidths(var5, this.d);
   }

   private void readGlyphSizes() {
      InputStream var3 = null;

      try {
         var3 = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
         var3.read(this.glyphWidth);
      } catch (IOException var8) {
         throw new RuntimeException(var8);
      } finally {
         IOUtils.closeQuietly(var3);
      }
   }

   private float renderChar(char var1, boolean var2) {
      if (var1 == ' ') {
         return !this.F ? this.d[var1] : 4.0F;
      }

      int var6 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
         .indexOf(var1);
      return var6 != -1 && !this.F ? this.renderDefaultChar(var6, var2) : this.renderUnicodeChar(var1, var2);
   }

   private float renderDefaultChar(int var1, boolean var2) {
      int var6 = var1 % 16 * 8;
      int var7 = var1 / 16 * 8;
      int var8 = var2 ? 1 : 0;
      this.bindTexture(this.w);
      float var9 = this.d[var1];
      float var10 = 7.99F;
      GL11.glBegin(5);
      GL11.glTexCoord2f(var6 / 128.0F, var7 / 128.0F);
      GL11.glVertex3f(this.posX + var8, this.posY, 0.0F);
      GL11.glTexCoord2f(var6 / 128.0F, (var7 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX - var8, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f((var6 + var10 - 1.0F) / 128.0F, var7 / 128.0F);
      GL11.glVertex3f(this.posX + var10 - 1.0F + var8, this.posY, 0.0F);
      GL11.glTexCoord2f((var6 + var10 - 1.0F) / 128.0F, (var7 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX + var10 - 1.0F - var8, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return var9;
   }

   private ResourceLocation getUnicodePageLocation(int var1) {
      if (unicodePageLocations[var1] == null) {
         unicodePageLocations[var1] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", var1));
         unicodePageLocations[var1] = FontUtils.getHdFontLocation(unicodePageLocations[var1]);
      }

      return unicodePageLocations[var1];
   }

   private void loadGlyphTexture(int var1) {
      this.bindTexture(this.getUnicodePageLocation(var1));
   }

   private float renderUnicodeChar(char var1, boolean var2) {
      if (this.glyphWidth[var1] == 0) {
         return 0.0F;
      }

      int var6 = var1 / 256;
      this.loadGlyphTexture(var6);
      int var7 = this.glyphWidth[var1] >>> 4;
      int var8 = this.glyphWidth[var1] & 15;
      var7 &= 15;
      float var9 = var7;
      float var10 = var8 + 1;
      float var11 = var1 % 16 * 16 + var9;
      float var12 = (var1 & 255) / 16 * 16;
      float var13 = var10 - var9 - 0.02F;
      float var14 = var2 ? 1.0F : 0.0F;
      GL11.glBegin(5);
      GL11.glTexCoord2f(var11 / 256.0F, var12 / 256.0F);
      GL11.glVertex3f(this.posX + var14, this.posY, 0.0F);
      GL11.glTexCoord2f(var11 / 256.0F, (var12 + 15.98F) / 256.0F);
      GL11.glVertex3f(this.posX - var14, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f((var11 + var13) / 256.0F, var12 / 256.0F);
      GL11.glVertex3f(this.posX + var13 / 2.0F + var14, this.posY, 0.0F);
      GL11.glTexCoord2f((var11 + var13) / 256.0F, (var12 + 15.98F) / 256.0F);
      GL11.glVertex3f(this.posX + var13 / 2.0F - var14, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return (var10 - var9) / 2.0F + 1.0F;
   }

   public int drawStringWithShadow(String var1, float var2, float var3, int var4) {
      return this.drawString(var1, var2, var3, var4, true);
   }

   public int D(String var1, float var2, float var3, int var4) {
      return !this.Z ? 0 : this.drawString(var1, var2, var3, var4, false);
   }

   public int drawString(String var1, float var2, float var3, int var4, boolean var5) {
      long var6 = b ^ 38032282281789L;
      long var8 = var6 ^ 132715678597461L;
      EntityPlayerSP var11 = Minecraft.getMinecraft().thePlayer;
      if (Minecraft.getMinecraft().theWorld != null && var11 != null && Astolfo.INSTANCE.getModuleManager() != null && ModuleManager.G.isEnabled()) {
         if (StreamerMode.scrambleNames.getValue()) {
            for (String var13 : StreamerMode.d) {
               if (var1.contains(var13)) {
                  var1 = this.F(var1, var13);
               }
            }

            for (NetworkPlayerInfo var14 : GuiPlayerTabOverlay.field_175252_a.sortedCopy(var11.sendQueue.getPlayerInfoMap())) {
               if (var14.getGameProfile() != null && var11.getPlayerInfo() != var14) {
                  String var15 = StreamerMode.t(var14);
                  if (var1.contains(var15)) {
                     var1 = this.F(var1, var15);
                  }
               }
            }
         }

         if (var11.getPlayerInfo() != null && var11.getPlayerInfo().getGameProfile() != null && var1.contains(var11.getPlayerInfo().getGameProfile().getName())
            )
          {
            var1 = var1.replaceAll(var11.getPlayerInfo().getGameProfile().getName(), StreamerMode.spoofName.getValue());
         }
      }

      this.enableAlpha();
      this.resetStyles();
      int var18;
      if (var5) {
         var18 = this.renderString(var1, var2 + 1.0F, var3 + 1.0F, var4, true);
         var18 = Math.max(var18, this.renderString(var1, var2, var3, var4, false));
      } else {
         var18 = this.renderString(var1, var2, var3, var4, false);
      }

      return var18;
   }

   private String F(String var1, String var2) {
      return var1.replace(var2, "§k" + var2 + "§r");
   }

   private String bidiReorder(String var1) {
      try {
         Bidi var4 = new Bidi(new ArabicShaping(8).shape(var1), 127);
         var4.setReorderingMode(0);
         return var4.writeReordered(2);
      } catch (ArabicShapingException var5) {
         return var1;
      }
   }

   private void resetStyles() {
      this.I = false;
      this.B = false;
      this.Y = false;
      this.W = false;
      this.v = false;
   }

   private void renderStringAtPos(String var1, boolean var2) {
      for (int var6 = 0; var6 < var1.length(); var6++) {
         char var7 = var1.charAt(var6);
         if (var7 == 167 && var6 + 1 < var1.length()) {
            int var15 = "0123456789abcdefklmnor".indexOf(var1.toLowerCase().charAt(var6 + 1));
            if (var15 < 16) {
               this.I = false;
               this.B = false;
               this.v = false;
               this.W = false;
               this.Y = false;
               if (var15 < 0 || var15 > 15) {
                  var15 = 15;
               }

               if (var2) {
                  var15 += 16;
               }

               int var17 = this.H[var15];
               if (Config.isCustomColors()) {
                  var17 = CustomColors.getTextColor(var15, var17);
               }

               this.textColor = var17;
               this.setColor((var17 >> 16) / 255.0F, (var17 >> 8 & 0xFF) / 255.0F, (var17 & 0xFF) / 255.0F, this.alpha);
            } else if (var15 == 16) {
               this.I = true;
            } else if (var15 == 17) {
               this.B = true;
            } else if (var15 == 18) {
               this.v = true;
            } else if (var15 == 19) {
               this.W = true;
            } else if (var15 == 20) {
               this.Y = true;
            } else if (var15 == 21) {
               this.I = false;
               this.B = false;
               this.v = false;
               this.W = false;
               this.Y = false;
               this.setColor(this.red, this.blue, this.green, this.alpha);
            }

            var6++;
         } else {
            int var8 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
               .indexOf(var7);
            if (this.I && var8 != -1) {
               int var9 = this.getCharWidth(var7);

               char var10;
               do {
                  var8 = this.fontRandom
                     .nextInt(
                        "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                           .length()
                     );
                  var10 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                     .charAt(var8);
               } while (var9 != this.getCharWidth(var10));

               var7 = var10;
            }

            float var16 = var8 != -1 && !this.F ? this.offsetBold : 0.5F;
            boolean var18 = (var7 == 0 || var8 == -1 || this.F) && var2;
            if (var18) {
               this.posX -= var16;
               this.posY -= var16;
            }

            float var11 = this.renderChar(var7, this.Y);
            if (var18) {
               this.posX += var16;
               this.posY += var16;
            }

            if (this.B) {
               this.posX += var16;
               if (var18) {
                  this.posX -= var16;
                  this.posY -= var16;
               }

               this.renderChar(var7, this.Y);
               this.posX -= var16;
               if (var18) {
                  this.posX += var16;
                  this.posY += var16;
               }

               var11 += var16;
            }

            if (this.v) {
               Tessellator var12 = Tessellator.getInstance();
               WorldRenderer var13 = var12.getWorldRenderer();
               GlStateManager.disableTexture2D();
               var13.begin(7, DefaultVertexFormats.POSITION);
               var13.pos(this.posX, this.posY + this.FONT_HEIGHT / 2, 0.0).endVertex();
               var13.pos(this.posX + var11, this.posY + this.FONT_HEIGHT / 2, 0.0).endVertex();
               var13.pos(this.posX + var11, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).endVertex();
               var13.pos(this.posX, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).endVertex();
               var12.draw();
               GlStateManager.enableTexture2D();
            }

            if (this.W) {
               Tessellator var19 = Tessellator.getInstance();
               WorldRenderer var20 = var19.getWorldRenderer();
               GlStateManager.disableTexture2D();
               var20.begin(7, DefaultVertexFormats.POSITION);
               int var14 = this.W ? -1 : 0;
               var20.pos(this.posX + var14, this.posY + this.FONT_HEIGHT, 0.0).endVertex();
               var20.pos(this.posX + var11, this.posY + this.FONT_HEIGHT, 0.0).endVertex();
               var20.pos(this.posX + var11, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).endVertex();
               var20.pos(this.posX + var14, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).endVertex();
               var19.draw();
               GlStateManager.enableTexture2D();
            }

            this.posX += var11;
         }
      }
   }

   private int renderStringAligned(String var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (this.bidiFlag) {
         int var10 = this.getStringWidth(this.bidiReorder(var1));
         var2 = var2 + var4 - var10;
      }

      return this.renderString(var1, var2, var3, var5, var6);
   }

   private int renderString(String var1, float var2, float var3, int var4, boolean var5) {
      if (var1 == null) {
         return 0;
      }

      if (this.bidiFlag) {
         var1 = this.bidiReorder(var1);
      }

      if ((var4 & -67108864) == 0) {
         var4 |= -16777216;
      }

      if (var5) {
         var4 = (var4 & 16579836) >> 2 | var4 & 0xFF000000;
      }

      this.red = (var4 >> 16 & 0xFF) / 255.0F;
      this.blue = (var4 >> 8 & 0xFF) / 255.0F;
      this.green = (var4 & 0xFF) / 255.0F;
      this.alpha = (var4 >> 24 & 0xFF) / 255.0F;
      this.setColor(this.red, this.blue, this.green, this.alpha);
      this.posX = var2;
      this.posY = var3;
      this.renderStringAtPos(var1, var5);
      return (int)this.posX;
   }

   public void O(String var1, int var2, int var3, int var4) {
      this.D(StringUtils.stripControlCodes(var1), var2 + 1, var3, -16777216);
      this.D(StringUtils.stripControlCodes(var1), var2 - 1, var3, -16777216);
      this.D(StringUtils.stripControlCodes(var1), var2, var3 + 1, -16777216);
      this.D(StringUtils.stripControlCodes(var1), var2, var3 - 1, -16777216);
      this.D(var1, var2, var3, var4);
   }

   public void N(String var1, float var2, float var3, int var4) {
      this.D(StringUtils.stripControlCodes(var1), var2 + 0.5F, var3, var4);
      this.D(StringUtils.stripControlCodes(var1), var2 - 0.5F, var3, var4);
      this.D(StringUtils.stripControlCodes(var1), var2, var3 + 0.5F, var4);
      this.D(StringUtils.stripControlCodes(var1), var2, var3 - 0.5F, var4);
      this.D(var1, var2, var3, var4);
   }

   public int getStringWidth(String var1) {
      if (var1 == null) {
         return 0;
      }

      float var4 = 0.0F;
      boolean var5 = false;

      for (int var6 = 0; var6 < var1.length(); var6++) {
         char var7 = var1.charAt(var6);
         float var8 = this.getCharWidthFloat(var7);
         if (var8 < 0.0F && var6 < var1.length() - 1) {
            var7 = var1.charAt(++var6);
            if (var7 == 'l' || var7 == 'L') {
               var5 = true;
            } else if (var7 == 'r' || var7 == 'R') {
               var5 = false;
            }

            var8 = 0.0F;
         }

         var4 += var8;
         if (var5 && var8 > 0.0F) {
            var4 += this.F ? 1.0F : this.offsetBold;
         }
      }

      return (int)var4;
   }

   public int getCharWidth(char var1) {
      return Math.round(this.getCharWidthFloat(var1));
   }

   private float getCharWidthFloat(char var1) {
      if (var1 == 167) {
         return -1.0F;
      } else if (var1 == ' ') {
         return this.d[32];
      } else {
         int var4 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(var1);
         if (var1 > 0 && var4 != -1 && !this.F) {
            return this.d[var4];
         } else if (this.glyphWidth[var1] != 0) {
            int var5 = this.glyphWidth[var1] >>> 4;
            int var6 = this.glyphWidth[var1] & 15;
            var5 &= 15;
            var6++;
            return (var6 - var5) / 2 + 1;
         } else {
            return 0.0F;
         }
      }
   }

   public String trimStringToWidth(String var1, int var2) {
      return this.trimStringToWidth(var1, var2, false);
   }

   public String trimStringToWidth(String var1, int var2, boolean var3) {
      StringBuilder var6 = new StringBuilder();
      float var7 = 0.0F;
      int var8 = var3 ? var1.length() - 1 : 0;
      int var9 = var3 ? -1 : 1;
      boolean var10 = false;
      boolean var11 = false;

      for (int var12 = var8; var12 >= 0 && var12 < var1.length() && var7 < var2; var12 += var9) {
         char var13 = var1.charAt(var12);
         float var14 = this.getCharWidthFloat(var13);
         if (var10) {
            var10 = false;
            if (var13 == 'l' || var13 == 'L') {
               var11 = true;
            } else if (var13 == 'r' || var13 == 'R') {
               var11 = false;
            }
         } else if (var14 < 0.0F) {
            var10 = true;
         } else {
            var7 += var14;
            if (var11) {
               var7++;
            }
         }

         if (var7 > var2) {
            break;
         }

         if (var3) {
            var6.insert(0, var13);
         } else {
            var6.append(var13);
         }
      }

      return var6.toString();
   }

   private String trimStringNewline(String var1) {
      while (var1 != null && var1.endsWith("\n")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      return var1;
   }

   public void drawSplitString(String var1, int var2, int var3, int var4, int var5) {
      this.resetStyles();
      this.textColor = var5;
      var1 = this.trimStringNewline(var1);
      this.renderSplitString(var1, var2, var3, var4, false);
   }

   private void renderSplitString(String var1, int var2, int var3, int var4, boolean var5) {
      for (Object var10 : this.listFormattedStringToWidth(var1, var4)) {
         this.renderStringAligned((String)var10, var2, var3, var4, this.textColor, var5);
         var3 += this.FONT_HEIGHT;
      }
   }

   public int splitStringWidth(String var1, int var2) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(var1, var2).size();
   }

   public void setUnicodeFlag(boolean var1) {
      this.F = var1;
   }

   public boolean n() {
      return this.F;
   }

   public void setBidiFlag(boolean var1) {
      this.bidiFlag = var1;
   }

   public List<String> listFormattedStringToWidth(String var1, int var2) {
      return Arrays.asList(this.wrapFormattedStringToWidth(var1, var2).split("\n"));
   }

   public String wrapFormattedStringToWidth(String var1, int var2) {
      int var6 = this.sizeStringToWidth(var1, var2);
      if (var1.length() <= var6) {
         return var1;
      }

      String var7 = var1.substring(0, var6);
      char var8 = var1.charAt(var6);
      boolean var9 = var8 == ' ' || var8 == '\n';
      String var10 = getFormatFromString(var7) + var1.substring(var6 + (var9 ? 1 : 0));
      return var7 + "\n" + this.wrapFormattedStringToWidth(var10, var2);
   }

   private int sizeStringToWidth(String var1, int var2) {
      int var6 = var1.length();
      float var7 = 0.0F;
      int var8 = 0;
      int var9 = -1;
      boolean var10 = false;

      while (var8 < var6) {
         char var11 = var1.charAt(var8);
         switch (var11) {
            case '\n':
               var8--;
               break;
            case ' ':
               var9 = var8;
            default:
               var7 += this.getCharWidthFloat(var11);
               if (var10) {
                  var7++;
               }
               break;
            case '§':
               if (var8 < var6 - 1) {
                  char var12 = var1.charAt(++var8);
                  if (var12 == 'l' || var12 == 'L') {
                     var10 = true;
                  } else if (var12 == 'r' || var12 == 'R' || isFormatColor(var12)) {
                     var10 = false;
                  }
               }
         }

         if (var11 == '\n') {
            var9 = ++var8;
            break;
         }

         if (var7 > var2) {
            break;
         }

         var8++;
      }

      return var8 != var6 && var9 != -1 && var9 < var8 ? var9 : var8;
   }

   private static boolean isFormatColor(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'a' && var0 <= 'f' || var0 >= 'A' && var0 <= 'F';
   }

   private static boolean isFormatSpecial(char var0) {
      return var0 >= 'k' && var0 <= 'o' || var0 >= 'K' && var0 <= 'O' || var0 == 'r' || var0 == 'R';
   }

   public static String getFormatFromString(String var0) {
      String var4 = "";
      int var5 = -1;
      int var6 = var0.length();

      while ((var5 = var0.indexOf(167, var5 + 1)) != -1) {
         if (var5 < var6 - 1) {
            char var7 = var0.charAt(var5 + 1);
            if (isFormatColor(var7)) {
               var4 = "§" + var7;
            } else if (isFormatSpecial(var7)) {
               var4 = var4 + "§" + var7;
            }
         }
      }

      return var4;
   }

   public boolean U() {
      return this.bidiFlag;
   }

   public int getColorCode(char var1) {
      int var5 = "0123456789abcdef".indexOf(var1);
      if (var5 >= 0 && var5 < this.H.length) {
         int var6 = this.H[var5];
         if (Config.isCustomColors()) {
            var6 = CustomColors.getTextColor(var5, var6);
         }

         return var6;
      } else {
         return 16777215;
      }
   }

   public void setColor(float var1, float var2, float var3, float var4) {
      GlStateManager.color(var1, var2, var3, var4);
   }

   public void enableAlpha() {
      GlStateManager.enableAlpha();
   }

   public void bindTexture(ResourceLocation var1) {
      this.renderEngine.bindTexture(var1);
   }

   public InputStream getResourceInputStream(ResourceLocation var1) throws IOException {
      return Minecraft.getMinecraft().getResourceManager().getResource(var1).getInputStream();
   }
}
