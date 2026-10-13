package bypass.gui.tabgui.render;

import bypass.gui.tabgui.Sized;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

public interface ElementRenderer<T extends Sized> {
   default void P(Sized var1, ScaledResolution var2, Minecraft var3) {
      this.a((T)var1, var2, var3);
   }

   void a(T var1, ScaledResolution var2, Minecraft var3);

   default void v(Sized var1, ScaledResolution var2, Minecraft var3) {
      this.x((T)var1, var2, var3);
   }

   default void x(T var1, ScaledResolution var2, Minecraft var3) {
   }

   default float[] p(Sized var1) {
      return this.H((T)var1);
   }

   default float[] H(T var1) {
      return new float[]{0.0F, 0.0F};
   }

   default float l() {
      return Display.getWidth() / 2.0F;
   }

   default float J() {
      return Display.getHeight() / 2.0F;
   }
}
