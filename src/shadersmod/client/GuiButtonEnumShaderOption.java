package shadersmod.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiButtonEnumShaderOption extends GuiButton {
   private EnumShaderOption enumShaderOption = null;
   private static final long c = 86725453330020L;

   public GuiButtonEnumShaderOption(EnumShaderOption var1, int var2, int var3, int var4, int var5) {
      super(var1.ordinal(), var2, var3, var4, var5, getButtonText(var1));
      this.enumShaderOption = var1;
   }

   public EnumShaderOption getEnumShaderOption() {
      return this.enumShaderOption;
   }

   private static String getButtonText(EnumShaderOption var0) {
      String var3 = I18n.format(var0.getResourceKey()) + ": ";
      switch (var0) {
         case ANTIALIASING:
            return var3 + GuiShaders.toStringAa(Shaders.configAntialiasingLevel);
         case NORMAL_MAP:
            return var3 + GuiShaders.toStringOnOff(Shaders.configNormalMap);
         case SPECULAR_MAP:
            return var3 + GuiShaders.toStringOnOff(Shaders.configSpecularMap);
         case RENDER_RES_MUL:
            return var3 + GuiShaders.toStringQuality(Shaders.configRenderResMul);
         case SHADOW_RES_MUL:
            return var3 + GuiShaders.toStringQuality(Shaders.configShadowResMul);
         case HAND_DEPTH_MUL:
            return var3 + GuiShaders.toStringHandDepth(Shaders.configHandDepthMul);
         case CLOUD_SHADOW:
            return var3 + GuiShaders.toStringOnOff(Shaders.configCloudShadow);
         case OLD_HAND_LIGHT:
            return var3 + Shaders.configOldHandLight.getUserValue();
         case OLD_LIGHTING:
            return var3 + Shaders.configOldLighting.getUserValue();
         case SHADOW_CLIP_FRUSTRUM:
            return var3 + GuiShaders.toStringOnOff(Shaders.configShadowClipFrustrum);
         case TWEAK_BLOCK_DAMAGE:
            return var3 + GuiShaders.toStringOnOff(Shaders.configTweakBlockDamage);
         default:
            return var3 + Shaders.getEnumShaderOption(var0);
      }
   }

   public void updateButtonText() {
      this.displayString = getButtonText(this.enumShaderOption);
   }
}
