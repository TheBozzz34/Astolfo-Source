package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.NumberSetting;
import org.lwjgl.opengl.GL11;

public final class ChunkBorders extends Module {
   private final NumberSetting<Integer> height = new NumberSetting<>("Height", "Size", 0, 0, 100);
   private final NumberSetting<Float> width = new NumberSetting<>("Width", "line width", 1.0F, 0.1F, 3.0F);
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      long l = d ^ 137511027157851L;
      if (renderEvent.getType() == RenderType.WORLD) {
         int i = (int)Math.floor(mc.thePlayer.posX / 16.0) * 16;
         int j = (int)Math.floor(mc.thePlayer.posZ / 16.0) * 16;
         GL11.glPushMatrix();
         long m = 3813013019286911635L ^ l;
         GL11.glTranslated(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(2929);
         GL11.glEnable(2848);
         GL11.glLineWidth(this.width.getValue());
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         int k = this.height.getValue();
         if (k <= 0) {
            k = (int)Math.floor(mc.getRenderManager().viewerPosY);
         }

         for (int n = -2; n <= 2; n++) {
            for (int i2 = -2; i2 <= 2; i2++) {
               GL11.glBegin(3);
               GL11.glVertex3d(i + n * 16, k, j + i2 * 16);
               GL11.glVertex3d(i + 16 + n * 16, k, j + i2 * 16);
               GL11.glVertex3d(i + 16 + n * 16, k, j + 16 + i2 * 16);
               GL11.glVertex3d(i + n * 16, k, j + 16 + i2 * 16);
               GL11.glVertex3d(i + n * 16, k, j + i2 * 16);
               GL11.glEnd();
            }
         }

         GL11.glDisable(2848);
         GL11.glEnable(2929);
         GL11.glEnable(2896);
         GL11.glDisable(3042);
         GL11.glEnable(3553);
         GL11.glPopMatrix();
      }
   };
   private static final long d = 116990112267910L;

   public ChunkBorders(Category category) {
      super(category);
      this.addSettings(this.height, this.width);
      this.setHidden(false);
   }
}
