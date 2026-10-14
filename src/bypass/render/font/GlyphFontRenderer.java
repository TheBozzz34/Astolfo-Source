package bypass.render.font;

import bypass.render.GlHelper;
import bypass.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;

public class GlyphFontRenderer implements TextRenderer {
   public int N = 0;
   public final GlyphPage v = new GlyphPage();
   private static final long a = 21706810875415L;

   @Override
   public float B(GlyphPage glyphPage, String string, float f, float g, int i) {
      if (!glyphPage.s()) {
         return 0.0F;
      }

      GlStateManager.enableBlend();
      glyphPage.p();
      GlHelper.R(i);
      int j = string.length();

      for (int k = 0; k < j; k++) {
         char c = string.charAt(k);
         if (glyphPage.x(c)) {
            GlyphPage.aun aun2 = O(glyphPage, f, g, c);
            f += aun2.F + this.N;
         }
      }

      return f;
   }

   public static GlyphPage.aun O(GlyphPage glyphPage, float f, float g, char c) {
      GlyphPage.aun aun2 = glyphPage.c(c);
      RenderUtil.drawTexturedRect(
         f,
         g,
         aun2.F,
         aun2.j,
         (float)aun2.p / glyphPage.r(),
         (float)aun2.s / glyphPage.c(),
         (float)(aun2.p + aun2.F) / glyphPage.r(),
         (float)(aun2.s + aun2.j) / glyphPage.c()
      );
      return aun2;
   }

   @Override
   public float drawString(String string, float f, float g, int i) {
      return this.B(this.v, string, f, g, i);
   }

   public int J() {
      return this.N;
   }

   public void b(int i) {
      this.N = i;
   }

   @Override
   public GlyphPage X() {
      return this.v;
   }
}
