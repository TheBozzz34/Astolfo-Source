package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.src.Config;
import org.lwjgl.opengl.GL11;

public class GlStateManager {
   private static GlStateManager.AlphaState alphaState = new GlStateManager.AlphaState();
   private static GlStateManager.BooleanState lightingState = new GlStateManager.BooleanState(2896);
   private static GlStateManager.BooleanState[] lightState = new GlStateManager.BooleanState[8];
   private static GlStateManager.ColorMaterialState colorMaterialState = new GlStateManager.ColorMaterialState();
   private static GlStateManager.BlendState blendState = new GlStateManager.BlendState();
   private static GlStateManager.DepthState depthState = new GlStateManager.DepthState();
   private static GlStateManager.FogState fogState = new GlStateManager.FogState();
   private static GlStateManager.CullState cullState = new GlStateManager.CullState();
   private static GlStateManager.PolygonOffsetState polygonOffsetState = new GlStateManager.PolygonOffsetState();
   private static GlStateManager.ColorLogicState colorLogicState = new GlStateManager.ColorLogicState();
   private static GlStateManager.TexGenState texGenState = new GlStateManager.TexGenState();
   private static GlStateManager.ClearState clearState = new GlStateManager.ClearState();
   private static GlStateManager.StencilState stencilState = new GlStateManager.StencilState();
   private static GlStateManager.BooleanState normalizeState = new GlStateManager.BooleanState(2977);
   private static int activeTextureUnit = 0;
   private static GlStateManager.TextureState[] textureState = new GlStateManager.TextureState[32];
   private static int activeShadeModel = 7425;
   private static GlStateManager.BooleanState rescaleNormalState = new GlStateManager.BooleanState(32826);
   private static GlStateManager.ColorMask colorMaskState = new GlStateManager.ColorMask();
   private static GlStateManager.Color colorState = new GlStateManager.Color();
   public static boolean clearEnabled = true;
   private static final long a = 57826828008187L;

   public static void pushAttrib() {
      GL11.glPushAttrib(8256);
   }

   public static void popAttrib() {
      GL11.glPopAttrib();
   }

   public static void disableAlpha() {
      alphaState.alphaTest.setDisabled();
   }

   public static void enableAlpha() {
      alphaState.alphaTest.setEnabled();
   }

   public static void alphaFunc(int var0, float var1) {
      if (var0 != alphaState.func || var1 != alphaState.ref) {
         alphaState.func = var0;
         alphaState.ref = var1;
         GL11.glAlphaFunc(var0, var1);
      }
   }

   public static void enableLighting() {
      lightingState.setEnabled();
   }

   public static void disableLighting() {
      lightingState.setDisabled();
   }

   public static void enableLight(int var0) {
      lightState[var0].setEnabled();
   }

   public static void disableLight(int var0) {
      lightState[var0].setDisabled();
   }

   public static void enableColorMaterial() {
      colorMaterialState.colorMaterial.setEnabled();
   }

   public static void disableColorMaterial() {
      colorMaterialState.colorMaterial.setDisabled();
   }

   public static void colorMaterial(int var0, int var1) {
      if (var0 != colorMaterialState.face || var1 != colorMaterialState.mode) {
         colorMaterialState.face = var0;
         colorMaterialState.mode = var1;
         GL11.glColorMaterial(var0, var1);
      }
   }

   public static void disableDepth() {
      depthState.depthTest.setDisabled();
   }

   public static void enableDepth() {
      depthState.depthTest.setEnabled();
   }

   public static void depthFunc(int var0) {
      if (var0 != depthState.depthFunc) {
         depthState.depthFunc = var0;
         GL11.glDepthFunc(var0);
      }
   }

   public static void depthMask(boolean var0) {
      if (var0 != depthState.maskEnabled) {
         depthState.maskEnabled = var0;
         GL11.glDepthMask(var0);
      }
   }

   public static void disableBlend() {
      blendState.blend.setDisabled();
   }

   public static void enableBlend() {
      blendState.blend.setEnabled();
   }

   public static void blendFunc(int var0, int var1) {
      if (var0 != blendState.srcFactor || var1 != blendState.dstFactor) {
         blendState.srcFactor = var0;
         blendState.dstFactor = var1;
         GL11.glBlendFunc(var0, var1);
      }
   }

   public static void tryBlendFuncSeparate(int var0, int var1, int var2, int var3) {
      if (var0 != blendState.srcFactor || var1 != blendState.dstFactor || var2 != blendState.srcFactorAlpha || var3 != blendState.dstFactorAlpha) {
         blendState.srcFactor = var0;
         blendState.dstFactor = var1;
         blendState.srcFactorAlpha = var2;
         blendState.dstFactorAlpha = var3;
         OpenGlHelper.glBlendFunc(var0, var1, var2, var3);
      }
   }

   public static void enableFog() {
      fogState.fog.setEnabled();
   }

   public static void disableFog() {
      fogState.fog.setDisabled();
   }

   public static void setFog(int var0) {
      if (var0 != fogState.mode) {
         fogState.mode = var0;
         GL11.glFogi(2917, var0);
      }
   }

   public static void setFogDensity(float var0) {
      if (var0 != fogState.density) {
         fogState.density = var0;
         GL11.glFogf(2914, var0);
      }
   }

   public static void setFogStart(float var0) {
      if (var0 != fogState.start) {
         fogState.start = var0;
         GL11.glFogf(2915, var0);
      }
   }

   public static void setFogEnd(float var0) {
      if (var0 != fogState.end) {
         fogState.end = var0;
         GL11.glFogf(2916, var0);
      }
   }

   public static void enableCull() {
      cullState.cullFace.setEnabled();
   }

   public static void disableCull() {
      cullState.cullFace.setDisabled();
   }

   public static void cullFace(int var0) {
      if (var0 != cullState.mode) {
         cullState.mode = var0;
         GL11.glCullFace(var0);
      }
   }

   public static void enablePolygonOffset() {
      polygonOffsetState.polygonOffsetFill.setEnabled();
   }

   public static void disablePolygonOffset() {
      polygonOffsetState.polygonOffsetFill.setDisabled();
   }

   public static void doPolygonOffset(float var0, float var1) {
      if (var0 != polygonOffsetState.factor || var1 != polygonOffsetState.units) {
         polygonOffsetState.factor = var0;
         polygonOffsetState.units = var1;
         GL11.glPolygonOffset(var0, var1);
      }
   }

   public static void enableColorLogic() {
      colorLogicState.colorLogicOp.setEnabled();
   }

   public static void disableColorLogic() {
      colorLogicState.colorLogicOp.setDisabled();
   }

   public static void colorLogicOp(int var0) {
      if (var0 != colorLogicState.opcode) {
         colorLogicState.opcode = var0;
         GL11.glLogicOp(var0);
      }
   }

   public static void enableTexGenCoord(GlStateManager.TexGen var0) {
      texGenCoord(var0).textureGen.setEnabled();
   }

   public static void disableTexGenCoord(GlStateManager.TexGen var0) {
      texGenCoord(var0).textureGen.setDisabled();
   }

   public static void texGen(GlStateManager.TexGen var0, int var1) {
      GlStateManager.TexGenCoord var5 = texGenCoord(var0);
      if (var1 != var5.param) {
         var5.param = var1;
         GL11.glTexGeni(var5.coord, 9472, var1);
      }
   }

   public static void texGen(GlStateManager.TexGen var0, int var1, FloatBuffer var2) {
      GL11.glTexGen(texGenCoord(var0).coord, var1, var2);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen var0) {
      switch (var0) {
         case S:
            return texGenState.s;
         case T:
            return texGenState.t;
         case R:
            return texGenState.r;
         case Q:
            return texGenState.q;
         default:
            return texGenState.s;
      }
   }

   public static void setActiveTexture(int var0) {
      if (activeTextureUnit != var0 - OpenGlHelper.defaultTexUnit) {
         activeTextureUnit = var0 - OpenGlHelper.defaultTexUnit;
         OpenGlHelper.setActiveTexture(var0);
      }
   }

   public static void enableTexture2D() {
      textureState[activeTextureUnit].texture2DState.setEnabled();
   }

   public static void disableTexture2D() {
      textureState[activeTextureUnit].texture2DState.setDisabled();
   }

   public static int generateTexture() {
      return GL11.glGenTextures();
   }

   public static void deleteTexture(int var0) {
      if (var0 != 0) {
         GL11.glDeleteTextures(var0);

         for (GlStateManager.TextureState var6 : textureState) {
            if (var6.textureName == var0) {
               var6.textureName = 0;
            }
         }
      }
   }

   public static void bindTexture(int var0) {
      if (var0 != textureState[activeTextureUnit].textureName) {
         textureState[activeTextureUnit].textureName = var0;
         GL11.glBindTexture(3553, var0);
      }
   }

   public static void bindCurrentTexture() {
      GL11.glBindTexture(3553, textureState[activeTextureUnit].textureName);
   }

   public static void enableNormalize() {
      normalizeState.setEnabled();
   }

   public static void disableNormalize() {
      normalizeState.setDisabled();
   }

   public static void shadeModel(int var0) {
      if (var0 != activeShadeModel) {
         activeShadeModel = var0;
         GL11.glShadeModel(var0);
      }
   }

   public static void enableRescaleNormal() {
      rescaleNormalState.setEnabled();
   }

   public static void disableRescaleNormal() {
      rescaleNormalState.setDisabled();
   }

   public static void viewport(int var0, int var1, int var2, int var3) {
      GL11.glViewport(var0, var1, var2, var3);
   }

   public static void colorMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      if (var0 != colorMaskState.red || var1 != colorMaskState.green || var2 != colorMaskState.blue || var3 != colorMaskState.alpha) {
         colorMaskState.red = var0;
         colorMaskState.green = var1;
         colorMaskState.blue = var2;
         colorMaskState.alpha = var3;
         GL11.glColorMask(var0, var1, var2, var3);
      }
   }

   public static void clearDepth(double var0) {
      if (var0 != clearState.depth) {
         clearState.depth = var0;
         GL11.glClearDepth(var0);
      }
   }

   public static void clearColor(float var0, float var1, float var2, float var3) {
      if (var0 != clearState.color.red || var1 != clearState.color.green || var2 != clearState.color.blue || var3 != clearState.color.alpha) {
         clearState.color.red = var0;
         clearState.color.green = var1;
         clearState.color.blue = var2;
         clearState.color.alpha = var3;
         GL11.glClearColor(var0, var1, var2, var3);
      }
   }

   public static void clear(int var0) {
      if (clearEnabled) {
         GL11.glClear(var0);
      }
   }

   public static void matrixMode(int var0) {
      GL11.glMatrixMode(var0);
   }

   public static void loadIdentity() {
      GL11.glLoadIdentity();
   }

   public static void pushMatrix() {
      GL11.glPushMatrix();
   }

   public static void popMatrix() {
      GL11.glPopMatrix();
   }

   public static void getFloat(int var0, FloatBuffer var1) {
      GL11.glGetFloat(var0, var1);
   }

   public static void ortho(double var0, double var2, double var4, double var6, double var8, double var10) {
      GL11.glOrtho(var0, var2, var4, var6, var8, var10);
   }

   public static void rotate(float var0, float var1, float var2, float var3) {
      GL11.glRotatef(var0, var1, var2, var3);
   }

   public static void scale(float var0, float var1, float var2) {
      GL11.glScalef(var0, var1, var2);
   }

   public static void scale(double var0, double var2, double var4) {
      GL11.glScaled(var0, var2, var4);
   }

   public static void translate(float var0, float var1, float var2) {
      GL11.glTranslatef(var0, var1, var2);
   }

   public static void translate(double var0, double var2, double var4) {
      GL11.glTranslated(var0, var2, var4);
   }

   public static void multMatrix(FloatBuffer var0) {
      GL11.glMultMatrix(var0);
   }

   public static void color(float var0, float var1, float var2, float var3) {
      if (var0 != colorState.red || var1 != colorState.green || var2 != colorState.blue || var3 != colorState.alpha) {
         colorState.red = var0;
         colorState.green = var1;
         colorState.blue = var2;
         colorState.alpha = var3;
         GL11.glColor4f(var0, var1, var2, var3);
      }
   }

   public static void color(float var0, float var1, float var2) {
      color(var0, var1, var2, 1.0F);
   }

   public static void resetColor() {
      colorState.red = colorState.green = colorState.blue = colorState.alpha = -1.0F;
   }

   public static void callList(int var0) {
      GL11.glCallList(var0);
   }

   public static int getActiveTextureUnit() {
      return OpenGlHelper.defaultTexUnit + activeTextureUnit;
   }

   public static int getBoundTexture() {
      return textureState[activeTextureUnit].textureName;
   }

   public static void checkBoundTexture() {
      if (Config.isMinecraftThread()) {
         int var3 = GL11.glGetInteger(34016);
         int var4 = GL11.glGetInteger(32873);
         int var5 = getActiveTextureUnit();
         int var6 = getBoundTexture();
         if (var6 > 0 && (var3 != var5 || var4 != var6)) {
            Config.dbg("checkTexture: act: " + var5 + ", glAct: " + var3 + ", tex: " + var6 + ", glTex: " + var4);
         }
      }
   }

   public static void deleteTextures(IntBuffer var0) {
      ((Buffer)var0).rewind();

      while (var0.position() < var0.limit()) {
         int var4 = var0.get();
         deleteTexture(var4);
      }

      ((Buffer)var0).rewind();
   }

   static {
      long var20 = a ^ 51511123938719L;
      long var10000 = 4358810586919158650L ^ var20;

      for (int var22 = 0; var22 < 8; var22++) {
         lightState[var22] = new GlStateManager.BooleanState(16384 + var22);
      }

      for (int var23 = 0; var23 < textureState.length; var23++) {
         textureState[var23] = new GlStateManager.TextureState();
      }
   }

   public static class AlphaState {
      public GlStateManager.BooleanState alphaTest = new GlStateManager.BooleanState(3008);
      public int func = 519;
      public float ref = -1.0F;
      private static final long a = 72614404554465L;

      public AlphaState() {
      }
   }

   public static class BlendState {
      public GlStateManager.BooleanState blend;
      public int srcFactor;
      public int dstFactor;
      public int srcFactorAlpha;
      public int dstFactorAlpha;
      private static final long a = 29378522583012L;
      private static final long d = -9199243576305775646L;

      public BlendState() {
         this.blend = new GlStateManager.BooleanState((int)d);
         this.srcFactor = 1;
         this.dstFactor = 0;
         this.srcFactorAlpha = 1;
         this.dstFactorAlpha = 0;
      }
   }

   public static class BooleanState {
      private final int capability;
      private boolean currentState = false;
      private static final long b = 26755856524633L;

      public BooleanState(int var1) {
         this.capability = var1;
      }

      public void setDisabled() {
         this.setState(false);
      }

      public void setEnabled() {
         this.setState(true);
      }

      public void setState(boolean var1) {
         if (var1 != this.currentState) {
            this.currentState = var1;
            if (var1) {
               GL11.glEnable(this.capability);
            } else {
               GL11.glDisable(this.capability);
            }
         }
      }
   }

   public static class ClearState {
      public double depth = 1.0;
      public GlStateManager.Color color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
      public int field_179204_c = 0;

      public ClearState() {
      }
   }

   public static class Color {
      public float red = 1.0F;
      public float green = 1.0F;
      public float blue = 1.0F;
      public float alpha = 1.0F;

      public Color() {
      }

      public Color(float var1, float var2, float var3, float var4) {
         this.red = var1;
         this.green = var2;
         this.blue = var3;
         this.alpha = var4;
      }
   }

   public static class ColorLogicState {
      public GlStateManager.BooleanState colorLogicOp = new GlStateManager.BooleanState(3058);
      public int opcode = 5379;
      private static final long a = 57861087177743L;

      public ColorLogicState() {
      }
   }

   public static class ColorMask {
      public boolean red = true;
      public boolean green = true;
      public boolean blue = true;
      public boolean alpha = true;

      public ColorMask() {
      }
   }

   public static class ColorMaterialState {
      public GlStateManager.BooleanState colorMaterial = new GlStateManager.BooleanState(2903);
      public int face = 1032;
      public int mode = 5634;
      private static final long a = 37962025780035L;

      public ColorMaterialState() {
      }
   }

   public static class CullState {
      public GlStateManager.BooleanState cullFace = new GlStateManager.BooleanState(2884);
      public int mode = 1029;
      private static final long a = 106651138170319L;

      public CullState() {
      }
   }

   public static class DepthState {
      public GlStateManager.BooleanState depthTest = new GlStateManager.BooleanState(2929);
      public boolean maskEnabled = true;
      public int depthFunc = 513;
      private static final long a = 3417362879245L;

      public DepthState() {
      }
   }

   public static class FogState {
      public GlStateManager.BooleanState fog = new GlStateManager.BooleanState(2912);
      public int mode = 2048;
      public float density = 1.0F;
      public float start = 0.0F;
      public float end = 1.0F;
      private static final long c = 30977138082871L;

      public FogState() {
      }
   }

   public static class PolygonOffsetState {
      public GlStateManager.BooleanState polygonOffsetFill = new GlStateManager.BooleanState(32823);
      public GlStateManager.BooleanState polygonOffsetLine = new GlStateManager.BooleanState(10754);
      public float factor = 0.0F;
      public float units = 0.0F;
      private static final long a = 140350316340536L;

      public PolygonOffsetState() {
      }
   }

   public static class StencilFunc {
      public int field_179081_a;
      public int field_179079_b;
      public int field_179080_c;
      private static final long a = 20947697674893L;
      private static final long b = -5639290736798072313L;

      public StencilFunc() {
         this.field_179081_a = (int)b;
         this.field_179079_b = 0;
         this.field_179080_c = -1;
      }
   }

   public static class StencilState {
      public GlStateManager.StencilFunc field_179078_a = new GlStateManager.StencilFunc();
      public int field_179076_b = -1;
      public int field_179077_c = 7680;
      public int field_179074_d = 7680;
      public int field_179075_e = 7680;
      private static final long a = 85678942276669L;

      public StencilState() {
      }
   }

   public enum TexGen {
      S("S", 0),
      T("T", 1),
      R("R", 2),
      Q("Q", 3);

      private static final GlStateManager.TexGen[] H = new GlStateManager.TexGen[]{S, T, R, Q};

      TexGen(String var3, int var4) {
      }
   }

   public static class TexGenCoord {
      public GlStateManager.BooleanState textureGen;
      public int coord;
      public int param = -1;

      public TexGenCoord(int var1, int var2) {
         this.coord = var1;
         this.textureGen = new GlStateManager.BooleanState(var2);
      }
   }

   public static class TexGenState {
      public GlStateManager.TexGenCoord s = new GlStateManager.TexGenCoord(8192, 3168);
      public GlStateManager.TexGenCoord t = new GlStateManager.TexGenCoord(8193, 3169);
      public GlStateManager.TexGenCoord r = new GlStateManager.TexGenCoord(8194, 3170);
      public GlStateManager.TexGenCoord q = new GlStateManager.TexGenCoord(8195, 3171);
      private static final long a = 43663329439842L;

      public TexGenState() {
      }
   }

   public static class TextureState {
      public GlStateManager.BooleanState texture2DState;
      public int textureName;
      private static final long a = 37922884075609L;
      private static final long b = -4776990307398971935L;

      public TextureState() {
         this.texture2DState = new GlStateManager.BooleanState((int)b);
         this.textureName = 0;
      }
   }
}
