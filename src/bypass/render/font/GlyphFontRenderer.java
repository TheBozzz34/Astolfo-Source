package bypass.render.font;

import bypass.render.GlHelper;
import bypass.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;

public class GlyphFontRenderer implements TextRenderer {
   public int N = 0;
   public final GlyphPage v = new GlyphPage();
   private static final long a = 21706810875415L;

   @Override
   public float B(GlyphPage var1, String var2, float var3, float var4, int var5) {
      if (!var1.s()) {
         return 0.0F;
      }

      GlStateManager.enableBlend();
      var1.p();
      GlHelper.R(var5);
      int var9 = var2.length();

      for (int var10 = 0; var10 < var9; var10++) {
         char var11 = var2.charAt(var10);
         if (var1.x(var11)) {
            GlyphPage.aun var12 = O(var1, var3, var4, var11);
            var3 += var12.F + this.N;
         }
      }

      return var3;
   }

   public static GlyphPage.aun O(GlyphPage var0, float var1, float var2, char var3) {
      GlyphPage.aun var8 = var0.c(var3);
      RenderUtil.drawTexturedRect(
         var1,
         var2,
         var8.F,
         var8.j,
         (float)var8.p / var0.r(),
         (float)var8.s / var0.c(),
         (float)(var8.p + var8.F) / var0.r(),
         (float)(var8.s + var8.j) / var0.c()
      );
      return var8;
   }

   @Override
   public float drawString(String var1, float var2, float var3, int var4) {
      return this.B(this.v, var1, var2, var3, var4);
   }

   public int J() {
      return this.N;
   }

   public void b(int var1) {
      this.N = var1;
   }

   @Override
   public GlyphPage X() {
      return this.v;
   }
}
