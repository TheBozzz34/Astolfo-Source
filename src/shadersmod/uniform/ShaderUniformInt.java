package shadersmod.uniform;

import org.lwjgl.opengl.ARBShaderObjects;
import shadersmod.client.Shaders;

public class ShaderUniformInt extends ShaderUniformBase {
   private int value = -1;
   private static final long b = 94424456419221L;

   public ShaderUniformInt(String var1) {
      super(var1);
   }

   @Override
   public void onProgramChanged() {
      this.value = -1;
   }

   public void setValue(int var1) {
      if (this.e() >= 0 && this.value != var1) {
         ARBShaderObjects.glUniform1iARB(this.e(), var1);
         Shaders.checkGLError(this.getName());
         this.value = var1;
      }
   }

   public int getValue() {
      return this.value;
   }
}
