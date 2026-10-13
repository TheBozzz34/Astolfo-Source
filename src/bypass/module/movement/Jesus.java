package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.JumpEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.world.LiquidCollisionEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import bypass.util.GameUtil;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

public final class Jesus extends Module {
   private final EnumSetting<JesusMode> mode = new EnumSetting<>("Mode", "Set your Jesus mode", JesusMode.SOLID);
   public static boolean y;
   private boolean B;
   private int x = 0;
   private boolean G = false;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      if (var1x.getState() == EventState.PRE) {
         switch ((JesusMode)this.mode.getValue()) {
            case DOLPHIN:
               if (GameUtil.getBlockAtPlayer(1.5) instanceof BlockLiquid) {
                  mc.thePlayer.motionY = 0.085F;
               } else if (mc.thePlayer.handleWaterMovement()) {
                  mc.thePlayer.motionY = 0.045F;
               } else if (GameUtil.getBlockAtPlayer(0.3) instanceof BlockLiquid) {
                  mc.thePlayer.motionY = 0.5;
               }
               break;
            case SOLID:
               Block var5 = GameUtil.getBlockAtPlayer();
               if (var5.equals(Blocks.lava)
                  && (
                     mc.gameSettings.keyBindForward.isKeyDown()
                        || mc.gameSettings.keyBindBack.isKeyDown()
                        || mc.gameSettings.keyBindRight.isKeyDown()
                        || mc.gameSettings.keyBindLeft.isKeyDown()
                  )) {
                  mc.thePlayer.sendQueue.u(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
               }

               if (P_boolean()) {
                  if (!mc.theWorld.isAnyLiquid(mc.thePlayer.getEntityBoundingBox())
                     || !mc.thePlayer.isInsideOfMaterial(Material.air)
                     || !this.B
                     || mc.thePlayer.isSneaking()
                     || (mc.thePlayer.isCollidedVertically || mc.thePlayer.fallDistance != 0.0F) && !mc.thePlayer.isInWater()) {
                     if (mc.thePlayer.isInWater() && mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY = 0.085;
                     } else if (mc.thePlayer.isInWater() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.thePlayer.motionY = -0.15;
                     }
                  } else {
                     mc.thePlayer.motionY = 0.085;
                  }
               }

               this.B = GameUtil.isOverLiquid(mc.thePlayer.getEntityBoundingBox()) && P_boolean();
               y = !y;
               if (!mc.thePlayer.isCollidedVertically
                  && mc.thePlayer.ticksExisted % (int)j == 0
                  && GameUtil.getBlockAtPlayer(-1.5) instanceof BlockStaticLiquid) {
                  mc.thePlayer.motionX *= 0.55;
                  mc.thePlayer.motionZ *= 0.55;
               }
               break;
            case FLOAT:
               if (!mc.thePlayer.movementInput.sneak && mc.thePlayer.isInWater() && this.W(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) {
                  mc.thePlayer.motionY = 0.12;
               }
         }
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      if (mc.thePlayer.handleWaterMovement()) {
         switch ((JesusMode)this.mode.getValue()) {
            case DOLPHIN:
               if (!(GameUtil.getBlockAtPlayer(1.0) instanceof BlockLiquid)) {
                  float var5 = 4.0F;
                  if (GameUtil.isInHypixelGame()) {
                     var5 = 3.0F;
                  }

                  var1x.setX(var1x.getX() * var5);
                  var1x.setZ(var1x.getZ() * var5);
               }
         }
      }
   };
   @EventHandler(MotionEvent.class)
   private final Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         if (this.B && !mc.thePlayer.handleWaterMovement()) {
            if (GameUtil.isOnHypixel()) {
               if (mc.thePlayer.fallDistance != 0.0F) {
                  return;
               }

               this.P++;
               if (this.P == 1) {
                  var1x.setMinY(var1x.getMinY() - ThreadLocalRandom.current().nextDouble(0.0155249999999, 0.015625));
               }

               if (this.P == 2) {
                  var1x.setMinY(var1x.getMinY() + ThreadLocalRandom.current().nextDouble(0.0148999999999, 0.015));
               }

               if (this.P == 3) {
                  var1x.setMinY(var1x.getMinY() + ThreadLocalRandom.current().nextDouble(0.0198999999999, 0.02));
               }

               if (this.P >= 4) {
                  var1x.setMinY(var1x.getMinY() + 0.015625);
                  this.P = 0;
               }

               if (this.P % 2 == 0) {
                  var1x.setMinY(var1x.getMinY() - 1.0E-13);
               }

               var1x.setMinY(var1x.getMinY() + 1.0E-13);
               var1x.setOnGround(var1x.getMinY() % 1.0 == 0.015625 || var1x.getMinY() % 1.0 == 0.0625 || var1x.getMinY() % 0.125 == 0.0);
               return;
            }
         } else {
            this.P = 0;
         }
      }
   };
   @EventHandler(LiquidCollisionEvent.class)
   private Listener<LiquidCollisionEvent> onLiquidCollision = var1x -> {
      if (mc.theWorld != null
         && mc.thePlayer != null
         && mc.thePlayer.getEntityBoundingBox() != null
         && mc.thePlayer.fallDistance <= 4.0F
         && !mc.thePlayer.isSneaking()
         && P_boolean()
         && !mc.thePlayer.isInWater()
         && !mc.thePlayer.isInLava()
         && this.mode.getValue() == JesusMode.SOLID) {
         var1x.setBoundingBox(
            new AxisAlignedBB(
               var1x.getPos().getX() + 0.3,
               var1x.getPos().getY(),
               var1x.getPos().getZ() + 0.3,
               var1x.getPos().getX() + 0.7,
               var1x.getPos().getY() + 0.99999,
               var1x.getPos().getZ() + 0.7
            )
         );
      }
   };
   private int P;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING
         && this.B
         && this.mode.getValue() == JesusMode.SOLID
         && var1x.getPacket() instanceof C03PacketPlayer
         && !GameUtil.isOnHypixel()) {
         if (mc.thePlayer.fallDistance <= mc.thePlayer.getMaxFallHeight()) {
            var1x.setPacket(
               new C03PacketPlayer.C06PacketPlayerPosLook(
                  mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true
               )
            );
         }

         if (y && !GameUtil.isOnHypixel()) {
            var1x.setPacket(
               new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5 + Math.random() * 1.0E-5, mc.thePlayer.posZ, false)
            );
         }
      }
   };
   @EventHandler(JumpEvent.class)
   private Listener<JumpEvent> onJump = var1x -> {
      if ((this.B || GameUtil.isOverLiquid(mc.thePlayer.getEntityBoundingBox())) && !y) {
         var1x.cancel();
      }
   };
   private static final long c = 22086836133154L;
   private static final long j = 2049007216203137040L;

   public Jesus(Category var1) {
      super(var1);
      this.addSettings(this.mode);
   }

   private boolean W(double var1, double var3, double var5) {
      try {
         IBlockState var10 = mc.theWorld.getBlockState(new BlockPos(var1, var3, var5));
         if (var10 instanceof BlockLiquid) {
            return (Integer)var10.getProperties().get(BlockLiquid.LEVEL) < 4;
         }
      } catch (Exception var11) {
      }

      return false;
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + JesusMode.R(this.mode.getValue()));
      super.p();
   }

   @Override
   public void onDisable() {
      y = false;
      this.B = false;
      super.onDisable();
   }

   public static boolean P_boolean() {
      double var3 = mc.thePlayer.posX;
      double var5 = mc.thePlayer.getEntityBoundingBox().minY;
      double var7 = mc.thePlayer.posZ;
      IBlockState var9 = mc.theWorld.getBlockState(new BlockPos(var3, var5, var7));
      return var9.getBlock().getMetaFromState(var9) < 5;
   }

   public static boolean A() {
      double var3 = mc.thePlayer.posX;
      double var5 = mc.thePlayer.getEntityBoundingBox().minY;
      double var7 = mc.thePlayer.posZ;
      IBlockState var9 = mc.theWorld.getBlockState(new BlockPos(var3, var5, var7));
      return var9.getBlock().getMetaFromState(var9) < 1;
   }
}
