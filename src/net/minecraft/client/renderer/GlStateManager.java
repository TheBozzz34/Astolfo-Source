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

   public static void alphaFunc(int func, float ref) {
      if (func != alphaState.func || ref != alphaState.ref) {
         alphaState.func = func;
         alphaState.ref = ref;
         GL11.glAlphaFunc(func, ref);
      }
   }

   public static void enableLighting() {
      lightingState.setEnabled();
   }

   public static void disableLighting() {
      lightingState.setDisabled();
   }

   public static void enableLight(int light) {
      lightState[light].setEnabled();
   }

   public static void disableLight(int light) {
      lightState[light].setDisabled();
   }

   public static void enableColorMaterial() {
      colorMaterialState.colorMaterial.setEnabled();
   }

   public static void disableColorMaterial() {
      colorMaterialState.colorMaterial.setDisabled();
   }

   public static void colorMaterial(int face, int mode) {
      if (face != colorMaterialState.face || mode != colorMaterialState.mode) {
         colorMaterialState.face = face;
         colorMaterialState.mode = mode;
         GL11.glColorMaterial(face, mode);
      }
   }

   public static void disableDepth() {
      depthState.depthTest.setDisabled();
   }

   public static void enableDepth() {
      depthState.depthTest.setEnabled();
   }

   public static void depthFunc(int depthFunc) {
      if (depthFunc != depthState.depthFunc) {
         depthState.depthFunc = depthFunc;
         GL11.glDepthFunc(depthFunc);
      }
   }

   public static void depthMask(boolean flagIn) {
      if (flagIn != depthState.maskEnabled) {
         depthState.maskEnabled = flagIn;
         GL11.glDepthMask(flagIn);
      }
   }

   public static void disableBlend() {
      blendState.blend.setDisabled();
   }

   public static void enableBlend() {
      blendState.blend.setEnabled();
   }

   public static void blendFunc(int srcFactor, int dstFactor) {
      if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor) {
         blendState.srcFactor = srcFactor;
         blendState.dstFactor = dstFactor;
         GL11.glBlendFunc(srcFactor, dstFactor);
      }
   }

   public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      if (srcFactor != blendState.srcFactor
         || dstFactor != blendState.dstFactor
         || srcFactorAlpha != blendState.srcFactorAlpha
         || dstFactorAlpha != blendState.dstFactorAlpha) {
         blendState.srcFactor = srcFactor;
         blendState.dstFactor = dstFactor;
         blendState.srcFactorAlpha = srcFactorAlpha;
         blendState.dstFactorAlpha = dstFactorAlpha;
         OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
      }
   }

   public static void enableFog() {
      fogState.fog.setEnabled();
   }

   public static void disableFog() {
      fogState.fog.setDisabled();
   }

   public static void setFog(int param) {
      if (param != fogState.mode) {
         fogState.mode = param;
         GL11.glFogi(2917, param);
      }
   }

   public static void setFogDensity(float param) {
      if (param != fogState.density) {
         fogState.density = param;
         GL11.glFogf(2914, param);
      }
   }

   public static void setFogStart(float param) {
      if (param != fogState.start) {
         fogState.start = param;
         GL11.glFogf(2915, param);
      }
   }

   public static void setFogEnd(float param) {
      if (param != fogState.end) {
         fogState.end = param;
         GL11.glFogf(2916, param);
      }
   }

   public static void enableCull() {
      cullState.cullFace.setEnabled();
   }

   public static void disableCull() {
      cullState.cullFace.setDisabled();
   }

   public static void cullFace(int mode) {
      if (mode != cullState.mode) {
         cullState.mode = mode;
         GL11.glCullFace(mode);
      }
   }

   public static void enablePolygonOffset() {
      polygonOffsetState.polygonOffsetFill.setEnabled();
   }

   public static void disablePolygonOffset() {
      polygonOffsetState.polygonOffsetFill.setDisabled();
   }

   public static void doPolygonOffset(float factor, float units) {
      if (factor != polygonOffsetState.factor || units != polygonOffsetState.units) {
         polygonOffsetState.factor = factor;
         polygonOffsetState.units = units;
         GL11.glPolygonOffset(factor, units);
      }
   }

   public static void enableColorLogic() {
      colorLogicState.colorLogicOp.setEnabled();
   }

   public static void disableColorLogic() {
      colorLogicState.colorLogicOp.setDisabled();
   }

   public static void colorLogicOp(int opcode) {
      if (opcode != colorLogicState.opcode) {
         colorLogicState.opcode = opcode;
         GL11.glLogicOp(opcode);
      }
   }

   public static void enableTexGenCoord(GlStateManager.TexGen texGen) {
      texGenCoord(texGen).textureGen.setEnabled();
   }

   public static void disableTexGenCoord(GlStateManager.TexGen texGen) {
      texGenCoord(texGen).textureGen.setDisabled();
   }

   public static void texGen(GlStateManager.TexGen texGen, int param) {
      GlStateManager.TexGenCoord texGenCoord2 = texGenCoord(texGen);
      if (param != texGenCoord2.param) {
         texGenCoord2.param = param;
         GL11.glTexGeni(texGenCoord2.coord, 9472, param);
      }
   }

   public static void texGen(GlStateManager.TexGen texGen, int pname, FloatBuffer params) {
      GL11.glTexGen(texGenCoord(texGen).coord, pname, params);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen texGen) {
      switch (texGen) {
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

   public static void setActiveTexture(int texture) {
      if (activeTextureUnit != texture - OpenGlHelper.defaultTexUnit) {
         activeTextureUnit = texture - OpenGlHelper.defaultTexUnit;
         OpenGlHelper.setActiveTexture(texture);
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

   public static void deleteTexture(int texture) {
      if (texture != 0) {
         GL11.glDeleteTextures(texture);

         for (GlStateManager.TextureState textureState2 : textureState) {
            if (textureState2.textureName == texture) {
               textureState2.textureName = 0;
            }
         }
      }
   }

   public static void bindTexture(int texture) {
      if (texture != textureState[activeTextureUnit].textureName) {
         textureState[activeTextureUnit].textureName = texture;
         GL11.glBindTexture(3553, texture);
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

   public static void shadeModel(int mode) {
      if (mode != activeShadeModel) {
         activeShadeModel = mode;
         GL11.glShadeModel(mode);
      }
   }

   public static void enableRescaleNormal() {
      rescaleNormalState.setEnabled();
   }

   public static void disableRescaleNormal() {
      rescaleNormalState.setDisabled();
   }

   public static void viewport(int x, int y, int width, int height) {
      GL11.glViewport(x, y, width, height);
   }

   public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
      if (red != colorMaskState.red || green != colorMaskState.green || blue != colorMaskState.blue || alpha != colorMaskState.alpha) {
         colorMaskState.red = red;
         colorMaskState.green = green;
         colorMaskState.blue = blue;
         colorMaskState.alpha = alpha;
         GL11.glColorMask(red, green, blue, alpha);
      }
   }

   public static void clearDepth(double depth) {
      if (depth != clearState.depth) {
         clearState.depth = depth;
         GL11.glClearDepth(depth);
      }
   }

   public static void clearColor(float red, float green, float blue, float alpha) {
      if (red != clearState.color.red || green != clearState.color.green || blue != clearState.color.blue || alpha != clearState.color.alpha) {
         clearState.color.red = red;
         clearState.color.green = green;
         clearState.color.blue = blue;
         clearState.color.alpha = alpha;
         GL11.glClearColor(red, green, blue, alpha);
      }
   }

   public static void clear(int mask) {
      if (clearEnabled) {
         GL11.glClear(mask);
      }
   }

   public static void matrixMode(int mode) {
      GL11.glMatrixMode(mode);
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

   public static void getFloat(int pname, FloatBuffer params) {
      GL11.glGetFloat(pname, params);
   }

   public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
      GL11.glOrtho(left, right, bottom, top, zNear, zFar);
   }

   public static void rotate(float angle, float x, float y, float z) {
      GL11.glRotatef(angle, x, y, z);
   }

   public static void scale(float x, float y, float z) {
      GL11.glScalef(x, y, z);
   }

   public static void scale(double x, double y, double z) {
      GL11.glScaled(x, y, z);
   }

   public static void translate(float x, float y, float z) {
      GL11.glTranslatef(x, y, z);
   }

   public static void translate(double x, double y, double z) {
      GL11.glTranslated(x, y, z);
   }

   public static void multMatrix(FloatBuffer matrix) {
      GL11.glMultMatrix(matrix);
   }

   public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
      if (colorRed != colorState.red || colorGreen != colorState.green || colorBlue != colorState.blue || colorAlpha != colorState.alpha) {
         colorState.red = colorRed;
         colorState.green = colorGreen;
         colorState.blue = colorBlue;
         colorState.alpha = colorAlpha;
         GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
      }
   }

   public static void color(float colorRed, float colorGreen, float colorBlue) {
      color(colorRed, colorGreen, colorBlue, 1.0F);
   }

   public static void resetColor() {
      colorState.red = colorState.green = colorState.blue = colorState.alpha = -1.0F;
   }

   public static void callList(int list) {
      GL11.glCallList(list);
   }

   public static int getActiveTextureUnit() {
      return OpenGlHelper.defaultTexUnit + activeTextureUnit;
   }

   public static int getBoundTexture() {
      return textureState[activeTextureUnit].textureName;
   }

   public static void checkBoundTexture() {
      if (Config.isMinecraftThread()) {
         int i = GL11.glGetInteger(34016);
         int j = GL11.glGetInteger(32873);
         int k = getActiveTextureUnit();
         int l = getBoundTexture();
         if (l > 0 && (i != k || j != l)) {
            Config.dbg("checkTexture: act: " + k + ", glAct: " + i + ", tex: " + l + ", glTex: " + j);
         }
      }
   }

   public static void deleteTextures(IntBuffer intBuffer) {
      ((Buffer)intBuffer).rewind();

      while (intBuffer.position() < intBuffer.limit()) {
         int i = intBuffer.get();
         deleteTexture(i);
      }

      ((Buffer)intBuffer).rewind();
   }

   static {
      for (int i = 0; i < 8; i++) {
         lightState[i] = new GlStateManager.BooleanState(16384 + i);
      }

      for (int j = 0; j < textureState.length; j++) {
         textureState[j] = new GlStateManager.TextureState();
      }
   }

   public static class AlphaState {
      public GlStateManager.BooleanState alphaTest = new GlStateManager.BooleanState(3008);
      public int func = 519;
      public float ref = -1.0F;

      public AlphaState() {
      }
   }

   public static class BlendState {
      public GlStateManager.BooleanState blend = new GlStateManager.BooleanState(3042);
      public int srcFactor = 1;
      public int dstFactor = 0;
      public int srcFactorAlpha = 1;
      public int dstFactorAlpha = 0;

      public BlendState() {
      }
   }

   public static class BooleanState {
      private final int capability;
      private boolean currentState = false;

      public BooleanState(int capabilityIn) {
         this.capability = capabilityIn;
      }

      public void setDisabled() {
         this.setState(false);
      }

      public void setEnabled() {
         this.setState(true);
      }

      public void setState(boolean state) {
         if (state != this.currentState) {
            this.currentState = state;
            if (state) {
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

      public Color(float redIn, float greenIn, float blueIn, float alphaIn) {
         this.red = redIn;
         this.green = greenIn;
         this.blue = blueIn;
         this.alpha = alphaIn;
      }
   }

   public static class ColorLogicState {
      public GlStateManager.BooleanState colorLogicOp = new GlStateManager.BooleanState(3058);
      public int opcode = 5379;

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

      public ColorMaterialState() {
      }
   }

   public static class CullState {
      public GlStateManager.BooleanState cullFace = new GlStateManager.BooleanState(2884);
      public int mode = 1029;

      public CullState() {
      }
   }

   public static class DepthState {
      public GlStateManager.BooleanState depthTest = new GlStateManager.BooleanState(2929);
      public boolean maskEnabled = true;
      public int depthFunc = 513;

      public DepthState() {
      }
   }

   public static class FogState {
      public GlStateManager.BooleanState fog = new GlStateManager.BooleanState(2912);
      public int mode = 2048;
      public float density = 1.0F;
      public float start = 0.0F;
      public float end = 1.0F;

      public FogState() {
      }
   }

   public static class PolygonOffsetState {
      public GlStateManager.BooleanState polygonOffsetFill = new GlStateManager.BooleanState(32823);
      public GlStateManager.BooleanState polygonOffsetLine = new GlStateManager.BooleanState(10754);
      public float factor = 0.0F;
      public float units = 0.0F;

      public PolygonOffsetState() {
      }
   }

   public static class StencilFunc {
      public int field_179081_a = 519;
      public int field_179079_b = 0;
      public int field_179080_c = -1;

      public StencilFunc() {
      }
   }

   public static class StencilState {
      public GlStateManager.StencilFunc field_179078_a = new GlStateManager.StencilFunc();
      public int field_179076_b = -1;
      public int field_179077_c = 7680;
      public int field_179074_d = 7680;
      public int field_179075_e = 7680;

      public StencilState() {
      }
   }

   public enum TexGen {
      S("S", 0),
      T("T", 1),
      R("R", 2),
      Q("Q", 3);

      private static final GlStateManager.TexGen[] VALUES = new GlStateManager.TexGen[]{S, T, R, Q};

      TexGen(String string2, int j) {
      }
   }

   public static class TexGenCoord {
      public GlStateManager.BooleanState textureGen;
      public int coord;
      public int param = -1;

      public TexGenCoord(int i, int j) {
         this.coord = i;
         this.textureGen = new GlStateManager.BooleanState(j);
      }
   }

   public static class TexGenState {
      public GlStateManager.TexGenCoord s = new GlStateManager.TexGenCoord(8192, 3168);
      public GlStateManager.TexGenCoord t = new GlStateManager.TexGenCoord(8193, 3169);
      public GlStateManager.TexGenCoord r = new GlStateManager.TexGenCoord(8194, 3170);
      public GlStateManager.TexGenCoord q = new GlStateManager.TexGenCoord(8195, 3171);

      public TexGenState() {
      }
   }

   public static class TextureState {
      public GlStateManager.BooleanState texture2DState = new GlStateManager.BooleanState(3553);
      public int textureName = 0;

      public TextureState() {
      }
   }
}
