package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.ColorSetting;
import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockSign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

public final class BlockOverlay extends Module {
   private static final AxisAlignedBB v = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   private final ColorSetting color = new ColorSetting("Color", "Color", Color.WHITE);
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderEvent> onRender = var1x -> {
      long var2 = c ^ 56129572518329L;
      if (var1x.getType() == RenderType.WORLD) {
         MovingObjectPosition var5 = mc.objectMouseOver;
         if (var5 == null || var5.entityHit != null) {
            return;
         }

         Block var6 = mc.theWorld.getBlockState(var5.getBlockPos()).getBlock();
         BlockPos var7 = var5.getBlockPos();
         if (Block.getIdFromBlock(var6) == 0
            || var6 instanceof BlockBanner
            || var6 instanceof BlockSign
            || var6 instanceof BlockCake
            || var6 instanceof BlockCocoa) {
            return;
         }

         GL11.glPushMatrix();
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(2848);
         GL11.glLineWidth(2.0F);
         long var10000 = 7027492741636162116L ^ var2;
         GL11.glDisable(3553);
         GL11.glEnable(2884);
         GL11.glDisable(2929);
         double var8 = mc.getRenderManager().viewerPosX;
         double var10 = mc.getRenderManager().viewerPosY;
         double var12 = mc.getRenderManager().viewerPosZ;
         GL11.glTranslated(-var8, -var10, -var12);
         GL11.glTranslated(var7.getX(), var7.getY(), var7.getZ());
         GL11.glColor4f(
            this.color.getValue().getRed() / 255.0F,
            this.color.getValue().getGreen() / 255.0F,
            this.color.getValue().getBlue() / 255.0F,
            this.color.getValue().getAlpha() / 255.0F
         );
         AxisAlignedBB var14 = var6.getSelectedBoundingBox(mc.theWorld, new BlockPos(0, 0, 0));
         if (var14 != null) {
            z(var14);
         }

         GL11.glColor4f(this.color.getValue().getRed() / 255.0F, this.color.getValue().getGreen() / 255.0F, this.color.getValue().getBlue() / 255.0F, 0.5F);
         if (var14 != null) {
            Xray.drawOutlinedBox(var14);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(2929);
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glDisable(2848);
         GL11.glPopMatrix();
      }
   };
   private static final long c = 29575927925452L;

   public BlockOverlay(Category var1) {
      super(var1);
      this.addSettings(this.color);
      this.setHidden(false);
   }

   public static void z(AxisAlignedBB var0) {
      GL11.glBegin(7);
      GL11.glVertex3d(var0.minX, var0.minY, var0.minZ);
      GL11.glVertex3d(var0.maxX, var0.minY, var0.minZ);
      GL11.glVertex3d(var0.maxX, var0.minY, var0.maxZ);
      GL11.glVertex3d(var0.minX, var0.minY, var0.maxZ);
      GL11.glVertex3d(var0.minX, var0.maxY, var0.minZ);
      GL11.glVertex3d(var0.minX, var0.maxY, var0.maxZ);
      GL11.glVertex3d(var0.maxX, var0.maxY, var0.maxZ);
      GL11.glVertex3d(var0.maxX, var0.maxY, var0.minZ);
      GL11.glVertex3d(var0.minX, var0.minY, var0.minZ);
      GL11.glVertex3d(var0.minX, var0.maxY, var0.minZ);
      GL11.glVertex3d(var0.maxX, var0.maxY, var0.minZ);
      GL11.glVertex3d(var0.maxX, var0.minY, var0.minZ);
      GL11.glVertex3d(var0.maxX, var0.minY, var0.minZ);
      GL11.glVertex3d(var0.maxX, var0.maxY, var0.minZ);
      GL11.glVertex3d(var0.maxX, var0.maxY, var0.maxZ);
      GL11.glVertex3d(var0.maxX, var0.minY, var0.maxZ);
      GL11.glVertex3d(var0.minX, var0.minY, var0.maxZ);
      GL11.glVertex3d(var0.maxX, var0.minY, var0.maxZ);
      GL11.glVertex3d(var0.maxX, var0.maxY, var0.maxZ);
      GL11.glVertex3d(var0.minX, var0.maxY, var0.maxZ);
      GL11.glVertex3d(var0.minX, var0.minY, var0.minZ);
      GL11.glVertex3d(var0.minX, var0.minY, var0.maxZ);
      GL11.glVertex3d(var0.minX, var0.maxY, var0.maxZ);
      GL11.glVertex3d(var0.minX, var0.maxY, var0.minZ);
      GL11.glEnd();
   }
}
