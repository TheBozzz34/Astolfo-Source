package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiStreamIndicator {
   private static final ResourceLocation locationStreamIndicator = new ResourceLocation("textures/gui/stream_indicator.png");
   private final Minecraft mc;
   private float streamAlpha = 1.0F;
   private int streamAlphaDelta = 1;
   private static final long b = 119585383255818L;

   public GuiStreamIndicator(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public void render(int i, int j) {
   }

   public void updateStreamAlpha() {
      this.streamAlpha = 1.0F;
      this.streamAlphaDelta = 1;
   }
}
