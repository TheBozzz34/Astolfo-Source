package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.TeleportPath;
import bypass.util.Vector3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public final class InfiniteReach extends Module {
   private final EnumSetting<InfiniteReachMode> mode = new EnumSetting<>("Mode", "Set your teleport mode", InfiniteReachMode.LINEAR);
   public static final NumberSetting<Float> range = new NumberSetting<>("Range", "Distance of blocks", 30.0F, 4.5F, 64.0F);
   private List<Vector3> lastPath;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)packetEvent.getPacket();
            if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK
               && mc.thePlayer.getDistance(c07PacketPlayerDigging.getPosition().getX(), c07PacketPlayerDigging.getPosition().getY(), c07PacketPlayerDigging.getPosition().getZ()) > 4.5) {
               this.teleportToPath(packetEvent, c07PacketPlayerDigging.getPosition());
            }
         }

         if (packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = (C08PacketPlayerBlockPlacement)packetEvent.getPacket();
            if (c08PacketPlayerBlockPlacement.getPlacedBlockDirection() != 255) {
               this.teleportToPath(packetEvent, c08PacketPlayerBlockPlacement.getPosition());
            }
         }

         if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packetEvent.getPacket();
            if (c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
               Entity entity = c02PacketUseEntity.getEntityFromWorld(mc.theWorld);
               if (entity != null) {
                  this.teleportToPath(packetEvent, entity.getPosition());
               }
            }
         }
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD && this.lastPath != null) {
         GL11.glPushMatrix();
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(2929);
         GL11.glEnable(2848);
         GL11.glDepthMask(false);
         GL11.glLineWidth(1.0F);
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         GL11.glBegin(3);

         for (Vector3 vector3 : this.lastPath) {
            GL11.glVertex3d(
               vector3.getX() - mc.getRenderManager().viewerPosX, vector3.getY() - mc.getRenderManager().viewerPosY, vector3.getZ() - mc.getRenderManager().viewerPosZ
            );
         }

         GL11.glEnd();
         GL11.glDepthMask(true);
         GL11.glDisable(2848);
         GL11.glEnable(2929);
         GL11.glEnable(2896);
         GL11.glDisable(3042);
         GL11.glEnable(3553);
         GL11.glPopMatrix();
      }
   };

   public InfiniteReach(Category category) {
      super(category);
      this.addSettings(this.mode, range);
   }

   private void teleportToPath(PacketEvent packetEvent, BlockPos blockPos) {
      List list = null;
      double d = 3.0 * Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
      double e = 3.0 * Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
      switch ((InfiniteReachMode)this.mode.getValue()) {
         case LINEAR:
            list = createLinearPath(
               mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, blockPos.getX() - d, Math.floor(blockPos.getY()) + 3.0, blockPos.getZ() - e, 5.5
            );
            break;
         case PATH_FIND:
            list = TeleportPath.findPath(
               new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
               new Vector3(blockPos.getX(), Math.floor(blockPos.getY()) + 1.0, blockPos.getZ()),
               90
            );
      }

      if (list.size() > 500 && this.mode.getValue() == InfiniteReachMode.PATH_FIND) {
         list = createLinearPath(
            mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, blockPos.getX() - d, Math.floor(blockPos.getY()) + 3.0, blockPos.getZ() - e, 5.5
         );
      }

      list.forEach(object -> mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(((bypass.util.Vector3)object).getX(), ((bypass.util.Vector3)object).getY(), ((bypass.util.Vector3)object).getZ(), true)));
      mc.thePlayer.sendQueue.sendPacketNoEvent(packetEvent.getPacket());
      Collections.reverse(list);
      list.forEach(object2 -> mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(((bypass.util.Vector3)object2).getX(), ((bypass.util.Vector3)object2).getY(), ((bypass.util.Vector3)object2).getZ(), true)));
      this.lastPath = list;
   }

   public static List<Vector3> createSteppedPathFromPlayer(double d, double e, double d2) {
      ArrayList arrayList = new ArrayList();
      double d3 = mc.thePlayer.posX;
      double d4 = mc.thePlayer.posY;
      double d5 = mc.thePlayer.posZ;
      double d6 = Math.abs(d3 - d) + Math.abs(d4 - e) + Math.abs(d5 - d2);

      for (int i = 0; d6 > 0.0; i++) {
         d6 = Math.abs(d3 - d) + Math.abs(d4 - e) + Math.abs(d5 - d2);
         double d7 = d3 - d;
         double d8 = d4 - e;
         double d9 = d5 - d2;
         double d10 = (i & 1) == 0 ? 0.4 : 0.1;
         double d11 = Math.min(Math.abs(d7), d10);
         if (d7 < 0.0) {
            d3 += d11;
         }

         if (d7 > 0.0) {
            d3 -= d11;
         }

         double d12 = Math.min(Math.abs(d8), 0.25);
         if (d8 < 0.0) {
            d4 += d12;
         }

         if (d8 > 0.0) {
            d4 -= d12;
         }

         double d13 = Math.min(Math.abs(d9), d10);
         if (d9 < 0.0) {
            d5 += d13;
         }

         if (d9 > 0.0) {
            d5 -= d13;
         }

         arrayList.add(new Vector3(d3, d4, d5));
      }

      return arrayList;
   }

   public static List<Vector3> createLinearPath(double d, double e, double d2, double d3, double d4, double d5, double d6) {
      ArrayList arrayList = new ArrayList();
      double d7 = Math.ceil(getDistance(d, e, d2, d3, d4, d5) / d6);
      double d8 = d3 - d;
      double d9 = d4 - e;
      double d10 = d5 - d2;

      for (double d11 = 1.0; d11 <= d7; d11++) {
         arrayList.add(new Vector3(d + d8 * d11 / d7, e + d9 * d11 / d7, d2 + d10 * d11 / d7));
      }

      return arrayList;
   }

   private static double getDistance(double d, double e, double d2, double d3, double d4, double d5) {
      double d6 = d - d3;
      double d7 = e - d4;
      double d8 = d2 - d5;
      return MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d8 * d8);
   }
}
