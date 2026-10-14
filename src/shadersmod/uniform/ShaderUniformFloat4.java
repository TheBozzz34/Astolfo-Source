package shadersmod.uniform;

import org.lwjgl.opengl.ARBShaderObjects;
import shadersmod.client.Shaders;

public class ShaderUniformFloat4 extends ShaderUniformBase {
   private float[] values = new float[4];

   public ShaderUniformFloat4(String name) {
      super(name);
   }

   @Override
   public void onProgramChanged() {
      this.values[0] = 0.0F;
      this.values[1] = 0.0F;
      this.values[2] = 0.0F;
      this.values[3] = 0.0F;
   }

   public void setValue(float f0In, float f1, float f2, float f3) {
      if (this.e() >= 0 && (this.values[0] != f0In || this.values[1] != f1 || this.values[2] != f2 || this.values[3] != f3)) {
         ARBShaderObjects.glUniform4fARB(this.e(), f0In, f1, f2, f3);
         Shaders.checkGLError(this.getName());
         this.values[0] = f0In;
         this.values[1] = f1;
         this.values[2] = f2;
         this.values[3] = f3;
      }
   }

   public float[] getValues() {
      return this.values;
   }
}
