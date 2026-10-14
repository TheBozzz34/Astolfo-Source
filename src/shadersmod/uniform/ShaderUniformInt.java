package shadersmod.uniform;

import org.lwjgl.opengl.ARBShaderObjects;
import shadersmod.client.Shaders;

public class ShaderUniformInt extends ShaderUniformBase {
   private int value = -1;
   private static final long b = 94424456419221L;

   public ShaderUniformInt(String name) {
      super(name);
   }

   @Override
   public void onProgramChanged() {
      this.value = -1;
   }

   public void setValue(int value) {
      if (this.e() >= 0 && this.value != value) {
         ARBShaderObjects.glUniform1iARB(this.e(), value);
         Shaders.checkGLError(this.getName());
         this.value = value;
      }
   }

   public int getValue() {
      return this.value;
   }
}
