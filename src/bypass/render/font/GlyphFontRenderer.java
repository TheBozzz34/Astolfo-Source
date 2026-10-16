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
            GlyphPage.aun aun2 = drawGlyph(glyphPage, f, g, c);
            f += aun2.width + this.charSpacing;
         }
      }

      return f;
   }

   public static GlyphPage.aun drawGlyph(GlyphPage glyphPage, float f, float g, char c) {
      GlyphPage.aun aun2 = glyphPage.getGlyph(c);
      RenderUtil.drawTexturedRect(
         f,
         g,
         aun2.width,
         aun2.height,
         (float)aun2.x / glyphPage.getTextureWidth(),
         (float)aun2.y / glyphPage.getTextureHeight(),
         (float)(aun2.x + aun2.width) / glyphPage.getTextureWidth(),
         (float)(aun2.y + aun2.height) / glyphPage.getTextureHeight()
      );
      return aun2;
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
