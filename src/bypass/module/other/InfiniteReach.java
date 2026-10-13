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
   private List<Vector3> m;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (var1x.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging var5 = (C07PacketPlayerDigging)var1x.getPacket();
            if (var5.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK
               && mc.thePlayer.getDistance(var5.getPosition().getX(), var5.getPosition().getY(), var5.getPosition().getZ()) > 4.5) {
               this.teleportToPath(var1x, var5.getPosition());
            }
         }

         if (var1x.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement var7 = (C08PacketPlayerBlockPlacement)var1x.getPacket();
            if (var7.getPlacedBlockDirection() != 255) {
               this.teleportToPath(var1x, var7.getPosition());
            }
         }

         if (var1x.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity var8 = (C02PacketUseEntity)var1x.getPacket();
            if (var8.getAction() == C02PacketUseEntity.Action.ATTACK) {
               Entity var6 = var8.getEntityFromWorld(mc.theWorld);
               if (var6 != null) {
                  this.teleportToPath(var1x, var6.getPosition());
               }
            }
         }
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = var1x -> {
      long var2 = c ^ 31923425309270L;
      if (var1x.getType() == RenderType.WORLD && this.m != null) {
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

         for (Vector3 var6 : this.m) {
            GL11.glVertex3d(
               var6.getX() - mc.getRenderManager().viewerPosX, var6.getY() - mc.getRenderManager().viewerPosY, var6.getZ() - mc.getRenderManager().viewerPosZ
            );
         }

         GL11.glEnd();
         GL11.glDepthMask(true);
         long var10000 = 8336396032766770604L ^ var2;
         GL11.glDisable(2848);
         GL11.glEnable(2929);
         GL11.glEnable(2896);
         GL11.glDisable(3042);
         GL11.glEnable(3553);
         GL11.glPopMatrix();
      }
   };
   private static final long c = 108326489742469L;

   public InfiniteReach(Category var1) {
      super(var1);
      this.addSettings(this.mode, range);
   }

   private void teleportToPath(PacketEvent var1, BlockPos var2) {
      List var6 = null;
      double var7 = 3.0 * Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
      double var9 = 3.0 * Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
      switch ((InfiniteReachMode)this.mode.getValue()) {
         case LINEAR:
            var6 = Z(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, var2.getX() - var7, Math.floor(var2.getY()) + 3.0, var2.getZ() - var9, 5.5);
            break;
         case PATH_FIND:
            var6 = TeleportPath.U(
               new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3(var2.getX(), Math.floor(var2.getY()) + 1.0, var2.getZ()), 90
            );
      }

      if (var6.size() > 500 && this.mode.getValue() == InfiniteReachMode.PATH_FIND) {
         var6 = Z(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, var2.getX() - var7, Math.floor(var2.getY()) + 3.0, var2.getZ() - var9, 5.5);
      }

      var6.forEach(var0 -> mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(((bypass.util.Vector3)var0).getX(), ((bypass.util.Vector3)var0).getY(), ((bypass.util.Vector3)var0).getZ(), true)));
      mc.thePlayer.sendQueue.u(var1.getPacket());
      Collections.reverse(var6);
      var6.forEach(var0 -> mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(((bypass.util.Vector3)var0).getX(), ((bypass.util.Vector3)var0).getY(), ((bypass.util.Vector3)var0).getZ(), true)));
      this.m = var6;
   }

   public static List<Vector3> Y(double var0, double var2, double var4) {
      ArrayList var9 = new ArrayList();
      double var10 = mc.thePlayer.posX;
      double var12 = mc.thePlayer.posY;
      double var14 = mc.thePlayer.posZ;
      double var16 = Math.abs(var10 - var0) + Math.abs(var12 - var2) + Math.abs(var14 - var4);

      for (int var18 = 0; var16 > 0.0; var18++) {
         var16 = Math.abs(var10 - var0) + Math.abs(var12 - var2) + Math.abs(var14 - var4);
         double var19 = var10 - var0;
         double var21 = var12 - var2;
         double var23 = var14 - var4;
         double var25 = (var18 & 1) == 0 ? 0.4 : 0.1;
         double var27 = Math.min(Math.abs(var19), var25);
         if (var19 < 0.0) {
            var10 += var27;
         }

         if (var19 > 0.0) {
            var10 -= var27;
         }

         double var29 = Math.min(Math.abs(var21), 0.25);
         if (var21 < 0.0) {
            var12 += var29;
         }

         if (var21 > 0.0) {
            var12 -= var29;
         }

         double var31 = Math.min(Math.abs(var23), var25);
         if (var23 < 0.0) {
            var14 += var31;
         }

         if (var23 > 0.0) {
            var14 -= var31;
         }

         var9.add(new Vector3(var10, var12, var14));
      }

      return var9;
   }

   public static List<Vector3> Z(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      ArrayList var17 = new ArrayList();
      double var18 = Math.ceil(F(var0, var2, var4, var6, var8, var10) / var12);
      double var20 = var6 - var0;
      double var22 = var8 - var2;
      double var24 = var10 - var4;

      for (double var26 = 1.0; var26 <= var18; var26++) {
         var17.add(new Vector3(var0 + var20 * var26 / var18, var2 + var22 * var26 / var18, var4 + var24 * var26 / var18));
      }

      return var17;
   }

   private static double F(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = var0 - var6;
      double var14 = var2 - var8;
      double var16 = var4 - var10;
      return MathHelper.sqrt_double(var12 * var12 + var14 * var14 + var16 * var16);
   }
}
