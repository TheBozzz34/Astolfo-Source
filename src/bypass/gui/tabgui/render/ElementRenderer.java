package bypass.gui.tabgui.render;

import bypass.gui.tabgui.Sized;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

public interface ElementRenderer<T extends Sized> {
   default void P(Sized sized, ScaledResolution scaledResolution, Minecraft minecraft) {
      this.a((T)sized, scaledResolution, minecraft);
   }

   void a(T sized, ScaledResolution scaledResolution, Minecraft minecraft);

   default void v(Sized sized, ScaledResolution scaledResolution, Minecraft minecraft) {
      this.x((T)sized, scaledResolution, minecraft);
   }

   default void x(T sized, ScaledResolution scaledResolution, Minecraft minecraft) {
   }

   default float[] p(Sized sized) {
      return this.H((T)sized);
   }

   default float[] H(T sized) {
      return new float[]{0.0F, 0.0F};
   }

   default float l() {
      return Display.getWidth() / 2.0F;
   }

   default float J() {
      return Display.getHeight() / 2.0F;
   }
}
