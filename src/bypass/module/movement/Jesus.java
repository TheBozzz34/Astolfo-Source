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
   public static boolean alternateTick;
   private boolean onLiquidSurface;
   private int unusedCounter = 0;
   private boolean unusedFlag = false;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.updateDisplayName();
      if (updateEvent.getState() == EventState.PRE) {
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
               Block block = GameUtil.getBlockAtPlayer();
               if (block.equals(Blocks.lava)
                  && (
                     mc.gameSettings.keyBindForward.isKeyDown()
                        || mc.gameSettings.keyBindBack.isKeyDown()
                        || mc.gameSettings.keyBindRight.isKeyDown()
                        || mc.gameSettings.keyBindLeft.isKeyDown()
                  )) {
                  mc.thePlayer.sendQueue.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
               }

               if (P_boolean()) {
                  if (!mc.theWorld.isAnyLiquid(mc.thePlayer.getEntityBoundingBox())
                     || !mc.thePlayer.isInsideOfMaterial(Material.air)
                     || !this.onLiquidSurface
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

               this.onLiquidSurface = GameUtil.isOverLiquid(mc.thePlayer.getEntityBoundingBox()) && P_boolean();
               alternateTick = !alternateTick;
               if (!mc.thePlayer.isCollidedVertically && mc.thePlayer.ticksExisted % 16 == 0 && GameUtil.getBlockAtPlayer(-1.5) instanceof BlockStaticLiquid) {
                  mc.thePlayer.motionX *= 0.55;
                  mc.thePlayer.motionZ *= 0.55;
               }
               break;
            case FLOAT:
               if (!mc.thePlayer.movementInput.sneak
                  && mc.thePlayer.isInWater()
                  && this.isShallowLiquid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) {
                  mc.thePlayer.motionY = 0.12;
               }
         }
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      if (mc.thePlayer.handleWaterMovement()) {
         switch ((JesusMode)this.mode.getValue()) {
            case DOLPHIN:
               if (!(GameUtil.getBlockAtPlayer(1.0) instanceof BlockLiquid)) {
                  float f = 4.0F;
                  if (GameUtil.isInHypixelGame()) {
                     f = 3.0F;
                  }

                  moveEvent.setX(moveEvent.getX() * f);
                  moveEvent.setZ(moveEvent.getZ() * f);
               }
         }
      }
   };
   @EventHandler(MotionEvent.class)
   private final Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         if (this.onLiquidSurface && !mc.thePlayer.handleWaterMovement()) {
            if (GameUtil.isOnHypixel()) {
               if (mc.thePlayer.fallDistance != 0.0F) {
                  return;
               }

               this.spoofTicks++;
               if (this.spoofTicks == 1) {
                  motionEvent.setMinY(motionEvent.getMinY() - ThreadLocalRandom.current().nextDouble(0.0155249999999, 0.015625));
               }

               if (this.spoofTicks == 2) {
                  motionEvent.setMinY(motionEvent.getMinY() + ThreadLocalRandom.current().nextDouble(0.0148999999999, 0.015));
               }

               if (this.spoofTicks == 3) {
                  motionEvent.setMinY(motionEvent.getMinY() + ThreadLocalRandom.current().nextDouble(0.0198999999999, 0.02));
               }

               if (this.spoofTicks >= 4) {
                  motionEvent.setMinY(motionEvent.getMinY() + 0.015625);
                  this.spoofTicks = 0;
               }

               if (this.spoofTicks % 2 == 0) {
                  motionEvent.setMinY(motionEvent.getMinY() - 1.0E-13);
               }

               motionEvent.setMinY(motionEvent.getMinY() + 1.0E-13);
               motionEvent.setOnGround(motionEvent.getMinY() % 1.0 == 0.015625 || motionEvent.getMinY() % 1.0 == 0.0625 || motionEvent.getMinY() % 0.125 == 0.0);
               return;
            }
         } else {
            this.spoofTicks = 0;
         }
      }
   };
   @EventHandler(LiquidCollisionEvent.class)
   private Listener<LiquidCollisionEvent> onLiquidCollision = liquidCollisionEvent -> {
      if (mc.theWorld != null
         && mc.thePlayer != null
         && mc.thePlayer.getEntityBoundingBox() != null
         && mc.thePlayer.fallDistance <= 4.0F
         && !mc.thePlayer.isSneaking()
         && P_boolean()
         && !mc.thePlayer.isInWater()
         && !mc.thePlayer.isInLava()
         && this.mode.getValue() == JesusMode.SOLID) {
         liquidCollisionEvent.setBoundingBox(
            new AxisAlignedBB(
               liquidCollisionEvent.getPos().getX() + 0.3,
               liquidCollisionEvent.getPos().getY(),
               liquidCollisionEvent.getPos().getZ() + 0.3,
               liquidCollisionEvent.getPos().getX() + 0.7,
               liquidCollisionEvent.getPos().getY() + 0.99999,
               liquidCollisionEvent.getPos().getZ() + 0.7
            )
         );
      }
   };
   private int spoofTicks;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING
         && this.onLiquidSurface
         && this.mode.getValue() == JesusMode.SOLID
         && packetEvent.getPacket() instanceof C03PacketPlayer
         && !GameUtil.isOnHypixel()) {
         if (mc.thePlayer.fallDistance <= mc.thePlayer.getMaxFallHeight()) {
            packetEvent.setPacket(
               new C03PacketPlayer.C06PacketPlayerPosLook(
                  mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true
               )
            );
         }

         if (alternateTick && !GameUtil.isOnHypixel()) {
            packetEvent.setPacket(
               new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5 + Math.random() * 1.0E-5, mc.thePlayer.posZ, false)
            );
         }
      }
   };
   @EventHandler(JumpEvent.class)
   private Listener<JumpEvent> onJump = jumpEvent -> {
      if ((this.onLiquidSurface || GameUtil.isOverLiquid(mc.thePlayer.getEntityBoundingBox())) && !alternateTick) {
         jumpEvent.cancel();
      }
   };

   public Jesus(Category category) {
      super(category);
      this.addSettings(this.mode);
   }

   private boolean isShallowLiquid(double d, double e, double d2) {
      try {
         IBlockState iBlockState = mc.theWorld.getBlockState(new BlockPos(d, e, d2));
         if (iBlockState instanceof BlockLiquid) {
            return (Integer)iBlockState.getProperties().get(BlockLiquid.LEVEL) < 4;
         }
      } catch (Exception exception) {
      }

      return false;
   }

   @Override
   public void updateDisplayName() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + JesusMode.displayNameOf(this.mode.getValue()));
      super.updateDisplayName();
   }

   @Override
   public void onDisable() {
      alternateTick = false;
      this.onLiquidSurface = false;
      super.onDisable();
   }

   public static boolean P_boolean() {
      double d = mc.thePlayer.posX;
      double e = mc.thePlayer.getEntityBoundingBox().minY;
      double d2 = mc.thePlayer.posZ;
      IBlockState iBlockState = mc.theWorld.getBlockState(new BlockPos(d, e, d2));
      return iBlockState.getBlock().getMetaFromState(iBlockState) < 5;
   }

   public static boolean isLiquidMetaZero() {
      double d = mc.thePlayer.posX;
      double e = mc.thePlayer.getEntityBoundingBox().minY;
      double d2 = mc.thePlayer.posZ;
      IBlockState iBlockState = mc.theWorld.getBlockState(new BlockPos(d, e, d2));
      return iBlockState.getBlock().getMetaFromState(iBlockState) < 1;
   }
}
