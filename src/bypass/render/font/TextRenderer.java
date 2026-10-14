package bypass.render.font;

public interface TextRenderer {
   float B(GlyphPage glyphPage, String string, float f, float g, int i);

   float drawString(String string, float f, float g, int i);

   GlyphPage X();
}
