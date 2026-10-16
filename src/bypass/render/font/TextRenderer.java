package bypass.render.font;

public interface TextRenderer {
   float drawStringWithPage(GlyphPage glyphPage, String string, float f, float g, int i);

   float drawString(String string, float f, float g, int i);

   GlyphPage getGlyphPage();
}
