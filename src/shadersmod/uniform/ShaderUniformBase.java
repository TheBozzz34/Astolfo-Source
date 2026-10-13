package shadersmod.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public abstract class ShaderUniformBase {
   private String name;
   private int program = -1;
   private int location = -1;
   private static final long a = 95174164555149L;

   public ShaderUniformBase(String var1) {
      this.name = var1;
   }

   public void setProgram(int var1) {
      if (this.program != var1) {
         this.program = var1;
         this.location = ARBShaderObjects.glGetUniformLocationARB(var1, this.name);
         this.onProgramChanged();
      }
   }

   public abstract void onProgramChanged();

   public String getName() {
      return this.name;
   }

   public int C() {
      return this.program;
   }

   public int e() {
      return this.location;
   }

   public boolean isDefined() {
      return this.location >= 0;
   }
}
