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
   private Listener<RenderEvent> onRender = var1x -> {
      long var2 = d ^ 137511027157851L;
      if (var1x.getType() == RenderType.WORLD) {
         int var5 = (int)Math.floor(mc.thePlayer.posX / 16.0) * 16;
         int var6 = (int)Math.floor(mc.thePlayer.posZ / 16.0) * 16;
         GL11.glPushMatrix();
         long var10000 = 3813013019286911635L ^ var2;
         GL11.glTranslated(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(2929);
         GL11.glEnable(2848);
         GL11.glLineWidth(this.width.getValue());
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         int var7 = this.height.getValue();
         if (var7 <= 0) {
            var7 = (int)Math.floor(mc.getRenderManager().viewerPosY);
         }

         for (int var8 = -2; var8 <= 2; var8++) {
            for (int var9 = -2; var9 <= 2; var9++) {
               GL11.glBegin(3);
               GL11.glVertex3d(var5 + var8 * 16, var7, var6 + var9 * 16);
               GL11.glVertex3d(var5 + 16 + var8 * 16, var7, var6 + var9 * 16);
               GL11.glVertex3d(var5 + 16 + var8 * 16, var7, var6 + 16 + var9 * 16);
               GL11.glVertex3d(var5 + var8 * 16, var7, var6 + 16 + var9 * 16);
               GL11.glVertex3d(var5 + var8 * 16, var7, var6 + var9 * 16);
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

   public ChunkBorders(Category var1) {
      super(var1);
      this.addSettings(this.height, this.width);
      this.setHidden(false);
   }
}
