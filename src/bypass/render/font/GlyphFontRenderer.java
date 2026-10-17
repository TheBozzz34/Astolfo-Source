package bypass.render.font;

import bypass.render.GlHelper;
import bypass.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;

public class GlyphFontRenderer implements TextRenderer {
   public int charSpacing = 0;
   public final GlyphPage glyphPage = new GlyphPage();

   @Override
   public float drawStringWithPage(GlyphPage glyphPage, String string, float f, float g, int i) {
      if (!glyphPage.isGenerated()) {
         return 0.0F;
      }

      GlStateManager.enableBlend();
      glyphPage.bindTexture();
      GlHelper.setColorHex(i);
      int j = string.length();

      for (int k = 0; k < j; k++) {
         char c = string.charAt(k);
         if (glyphPage.hasGlyph(c)) {
            GlyphPage.Glyph glyph = drawGlyph(glyphPage, f, g, c);
            f += glyph.width + this.charSpacing;
         }
      }

      return f;
   }

   public static GlyphPage.Glyph drawGlyph(GlyphPage glyphPage, float f, float g, char c) {
      GlyphPage.Glyph glyph = glyphPage.getGlyph(c);
      RenderUtil.drawTexturedRect(
         f,
         g,
         glyph.width,
         glyph.height,
         (float)glyph.x / glyphPage.getTextureWidth(),
         (float)glyph.y / glyphPage.getTextureHeight(),
         (float)(glyph.x + glyph.width) / glyphPage.getTextureWidth(),
         (float)(glyph.y + glyph.height) / glyphPage.getTextureHeight()
      );
      return glyph;
   }

   @Override
   public float drawString(String string, float f, float g, int i) {
      return this.drawStringWithPage(this.glyphPage, string, f, g, i);
   }

   public int getCharSpacing() {
      return this.charSpacing;
   }

   public void setCharSpacing(int i) {
      this.charSpacing = i;
   }

   @Override
   public GlyphPage getGlyphPage() {
      return this.glyphPage;
   }
}
