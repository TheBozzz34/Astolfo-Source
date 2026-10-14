package shadersmod.client;

public class ShaderOptionScreen extends ShaderOption {
   private static final long b = 55844184056710L;

   public ShaderOptionScreen(String name) {
      super(name, (String)null, (String)null, new String[]{null}, (String)null, (String)null);
   }

   @Override
   public String getNameText() {
      return Shaders.translate("screen." + this.getName(), this.getName());
   }

   @Override
   public String getDescriptionText() {
      return Shaders.translate("screen." + this.getName() + ".comment", (String)null);
   }
}
