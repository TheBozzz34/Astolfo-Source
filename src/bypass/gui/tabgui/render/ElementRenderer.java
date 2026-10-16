package bypass.gui.tabgui.render;

import bypass.gui.tabgui.Sized;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

public interface ElementRenderer<T extends Sized> {
   default void renderUnchecked(Sized sized, ScaledResolution scaledResolution, Minecraft minecraft) {
      this.render((T)sized, scaledResolution, minecraft);
   }

   void render(T sized, ScaledResolution scaledResolution, Minecraft minecraft);

   default void updateUnchecked(Sized sized, ScaledResolution scaledResolution, Minecraft minecraft) {
      this.update((T)sized, scaledResolution, minecraft);
   }

   default void update(T sized, ScaledResolution scaledResolution, Minecraft minecraft) {
   }

   default float[] computeSizeUnchecked(Sized sized) {
      return this.computeSize((T)sized);
   }

   default float[] computeSize(T sized) {
      return new float[]{0.0F, 0.0F};
   }

   default float getScreenCenterX() {
      return Display.getWidth() / 2.0F;
   }

   default float getScreenCenterY() {
      return Display.getHeight() / 2.0F;
   }
}
