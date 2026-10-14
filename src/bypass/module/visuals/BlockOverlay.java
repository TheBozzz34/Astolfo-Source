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
   private final Listener<RenderEvent> onRender = renderEvent -> {
      long l = c ^ 56129572518329L;
      if (renderEvent.getType() == RenderType.WORLD) {
         MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
         if (movingObjectPosition == null || movingObjectPosition.entityHit != null) {
            return;
         }

         Block block = mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock();
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         if (Block.getIdFromBlock(block) == 0
            || block instanceof BlockBanner
            || block instanceof BlockSign
            || block instanceof BlockCake
            || block instanceof BlockCocoa) {
            return;
         }

         GL11.glPushMatrix();
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(2848);
         GL11.glLineWidth(2.0F);
         long m = 7027492741636162116L ^ l;
         GL11.glDisable(3553);
         GL11.glEnable(2884);
         GL11.glDisable(2929);
         double d = mc.getRenderManager().viewerPosX;
         double e = mc.getRenderManager().viewerPosY;
         double d2 = mc.getRenderManager().viewerPosZ;
         GL11.glTranslated(-d, -e, -d2);
         GL11.glTranslated(blockPos.getX(), blockPos.getY(), blockPos.getZ());
         GL11.glColor4f(
            this.color.getValue().getRed() / 255.0F,
            this.color.getValue().getGreen() / 255.0F,
            this.color.getValue().getBlue() / 255.0F,
            this.color.getValue().getAlpha() / 255.0F
         );
         AxisAlignedBB axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld, new BlockPos(0, 0, 0));
         if (axisAlignedBB != null) {
            z(axisAlignedBB);
         }

         GL11.glColor4f(this.color.getValue().getRed() / 255.0F, this.color.getValue().getGreen() / 255.0F, this.color.getValue().getBlue() / 255.0F, 0.5F);
         if (axisAlignedBB != null) {
            Xray.drawOutlinedBox(axisAlignedBB);
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

   public BlockOverlay(Category category) {
      super(category);
      this.addSettings(this.color);
      this.setHidden(false);
   }

   public static void z(AxisAlignedBB axisAlignedBB) {
      GL11.glBegin(7);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
      GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
      GL11.glEnd();
   }
}
