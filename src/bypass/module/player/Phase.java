package bypass.module.player;

import bypass.event.CancellableEvent;
import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.game.TickEvent;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.PushOutOfBlocksEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.BlockOpaqueEvent;
import bypass.event.render.SetupTerrainEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public final class Phase extends Module {
   private final EnumSetting<PhaseMode> mode = new EnumSetting<>("Mode", "Set your phase mode", PhaseMode.VERTICAL);
   private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Speed of vanilla phase", 1.0F, 0.1F, 3.0F);
   private Stopwatch descendTimer = new Stopwatch();
   private int fullBlockTicks;
   private boolean teleported;
   private int stage;
   private int wobbleTicks;
   @EventHandler(SetupTerrainEvent.class)
   private Listener<SetupTerrainEvent> onSetupTerrain = CancellableEvent::cancel;
   @EventHandler(PushOutOfBlocksEvent.class)
   private Listener<PushOutOfBlocksEvent> onPushOutOfBlocks = CancellableEvent::cancel;
   @EventHandler(BlockOpaqueEvent.class)
   private Listener<BlockOpaqueEvent> onBlockOpaque = CancellableEvent::cancel;
   @EventHandler(Block.CollisionBoxEvent.class)
   private Listener<Block.CollisionBoxEvent> onCollisionBox = collisionBoxEvent -> {
      if (!mc.isSingleplayer()
         && (mc.thePlayer.getEntityBoundingBox().minY <= collisionBoxEvent.getY() || this.mode.getValue() == PhaseMode.VANILLA && this.isInsideBlock(mc.thePlayer))) {
         collisionBoxEvent.setBoundingBox(null);
      }
   };
   @EventHandler(TickEvent.class)
   private Listener<TickEvent> onTick = tickEvent -> {
      if (tickEvent.getState() == EventState.PRE && mc.theWorld != null && mc.thePlayer != null && this.mode.getValue() == PhaseMode.VANILLA) {
         if (this.stage == 1) {
            mc.thePlayer
               .sendQueue
               .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2.0, mc.thePlayer.posZ, true));
            mc.thePlayer
               .sendQueue
               .addToSendQueue(
                  new C03PacketPlayer.C04PacketPlayerPosition(
                     mc.thePlayer.posX + 2.147483647E9, mc.thePlayer.posY + 2.147483647E9, mc.thePlayer.posZ + 2.147483647E9, true
                  )
               );
            this.stage = 0;
         }

         double d = 1.0;
         double e = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw));
         double d2 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw));
         double d3 = mc.thePlayer.movementInput.moveForward * d * e + mc.thePlayer.movementInput.moveStrafe * d * d2;
         double d4 = mc.thePlayer.movementInput.moveForward * d * d2 - mc.thePlayer.movementInput.moveStrafe * d * e;
         if (mc.thePlayer.isCollidedHorizontally && !this.isInsideBlock(mc.thePlayer) && GameUtil.isMoving(mc.thePlayer)) {
            mc.thePlayer
               .sendQueue
               .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + d3, mc.thePlayer.posY, mc.thePlayer.posZ + d4, true));
            mc.thePlayer
               .sendQueue
               .addToSendQueue(
                  new C03PacketPlayer.C04PacketPlayerPosition(
                     mc.thePlayer.posX + 2.147483647E9, mc.thePlayer.posY + 2.147483647E9, mc.thePlayer.posZ + 2.147483647E9, true
                  )
               );
         }
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.p();
      if (this.mode.getValue() == PhaseMode.SKIP && updateEvent.getState() == EventState.PRE && mc.thePlayer.isSneaking() && mc.thePlayer.isCollidedHorizontally) {
         float f = mc.thePlayer.rotationYaw;
         float g = 1.2F;
         mc.thePlayer.getEntityBoundingBox().X(g * Math.cos(Math.toRadians(f + 90.0F)), 0.0, g * Math.sin(Math.toRadians(f + 90.0F)));
      }

      if (this.mode.getValue() == PhaseMode.VANILLA && mc.gameSettings.keyBindSneak.h() && !this.isInsideBlock(mc.thePlayer)) {
         mc.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - this.speed.getValue().floatValue(), mc.thePlayer.posZ, true)
            );
         this.stage = 2;
      }
   };
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      boolean bl = this.isInsideBlock(mc.thePlayer);
      switch (motionEvent.getState()) {
         case PRE:
            switch ((PhaseMode)this.mode.getValue()) {
               case VERTICAL:
                  if (this.teleported || bl) {
                     motionEvent.setOnGround(false);
                     mc.thePlayer.noClip = true;
                     if (mc.gameSettings.keyBindJump.pressed && mc.thePlayer.posY == (int)mc.thePlayer.posY) {
                        mc.thePlayer.jump();
                     }
                  }

                  this.stage++;
                  if (mc.thePlayer.movementInput.sneak && !GameUtil.isMoving(mc.thePlayer)) {
                     if (isBlockBelow(0.001) && !bl) {
                        motionEvent.setY(mc.thePlayer.posY - 1.0);
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
                        this.descendTimer.reset();
                        mc.thePlayer.motionY = 0.0;
                     } else if (this.descendTimer.hasElapsed(100.0F) && mc.thePlayer.posY == (int)mc.thePlayer.posY) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.8, mc.thePlayer.posZ);
                     }
                  }

                  if (bl && this.stage >= 3) {
                     if (this.teleported) {
                        moveForward(0.617);
                        float f = (float)Math.sin(this.wobbleTicks) * 0.1F;
                        float g = (float)Math.cos(this.wobbleTicks) * 0.1F;
                        mc.thePlayer.rotationYaw += f;
                        mc.thePlayer.rotationPitch += g;
                        this.wobbleTicks++;
                     } else {
                        moveForward(0.031);
                     }

                     return;
                  }

                  return;
               case PACKETLESS:
                  double d = 0.39;
                  mc.thePlayer.noClip = true;
                  double e = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                  double d2 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                  double d3 = mc.thePlayer.moveForward * d * e + mc.thePlayer.moveStrafing * d * d2;
                  double d4 = mc.thePlayer.moveForward * d * d2 + mc.thePlayer.moveStrafing * d * e;
                  if (bl) {
                     motionEvent.setOnGround(true);
                     mc.thePlayer.r(mc.thePlayer.B());
                     mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.525 : (mc.thePlayer.movementInput.sneak ? -0.525 : 0.0);
                     mc.thePlayer.setPosition(mc.thePlayer.posX + d3, mc.thePlayer.posY, mc.thePlayer.posZ + d4);
                  }

                  return;
               default:
                  return;
            }
         case POST:
            switch ((PhaseMode)this.mode.getValue()) {
               case FULLBLOCK:
                  double d5 = 0.4;
                  double d6 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                  double d7 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                  double d8 = mc.thePlayer.movementInput.moveForward * d5 * d6 + mc.thePlayer.movementInput.moveStrafe * d5 * d7;
                  double d9 = mc.thePlayer.movementInput.moveForward * d5 * d7 - mc.thePlayer.movementInput.moveStrafe * d5 * d6;
                  if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !bl) {
                     mc.thePlayer
                        .sendQueue
                        .addToSendQueue(
                           new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + d8, mc.thePlayer.posY, mc.thePlayer.posZ + d9, false)
                        );

                     for (int i = 1; i < 11; i++) {
                        mc.thePlayer
                           .sendQueue
                           .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, (Double.MAX_VALUE) * i, mc.thePlayer.posZ, false));
                     }

                     mc.thePlayer
                        .sendQueue
                        .addToSendQueue(
                           new C03PacketPlayer.C04PacketPlayerPosition(
                              mc.thePlayer.posX,
                              mc.thePlayer.posY - (GameUtil.isOverLiquid(mc.thePlayer.getEntityBoundingBox()) ? 9000.0 : 0.1),
                              mc.thePlayer.posZ,
                              false
                           )
                        );
                     mc.thePlayer.setPosition(mc.thePlayer.posX + d8, mc.thePlayer.posY, mc.thePlayer.posZ + d9);
                  } else if (bl) {
                     mc.thePlayer.setPosition(mc.thePlayer.posX + d8, mc.thePlayer.posY, mc.thePlayer.posZ + d9);
                  }
            }
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      boolean bl = this.isInsideBlock(mc.thePlayer);
      switch ((PhaseMode)this.mode.getValue()) {
         case VANILLA:
            if (bl) {
               if (mc.gameSettings.keyBindJump.isKeyDown()) {
                  moveEvent.setY(mc.thePlayer.motionY = this.speed.getValue().floatValue());
               } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                  moveEvent.setY(mc.thePlayer.motionY = -this.speed.getValue());
               } else {
                  moveEvent.setY(mc.thePlayer.motionY = 0.0);
               }

               GameUtil.setMoveSpeed(moveEvent, this.speed.getValue().floatValue(), 90.0F);
            }
            break;
         case VERTICAL:
            if (bl) {
               moveEvent.setX(moveEvent.getX() / 2.0);
               moveEvent.setZ(moveEvent.getZ() / 2.0);
            }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      switch (packetEvent.getDirection()) {
         case OUTGOING:
            if (packetEvent.getPacket() instanceof C03PacketPlayer) {
               if (this.isInsideBlock(mc.thePlayer)) {
                  return;
               }

               C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)packetEvent.getPacket();
               double d = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
               double e = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
               double d2 = mc.thePlayer.movementInput.moveForward * 0.2 * d + mc.thePlayer.movementInput.moveStrafe * 0.2 * e;
               double d3 = mc.thePlayer.movementInput.moveForward * 0.2 * e - mc.thePlayer.movementInput.moveStrafe * 0.2 * d;
               if (mc.thePlayer.isCollidedHorizontally && this.mode.getValue() == PhaseMode.FULLBLOCK) {
                  this.fullBlockTicks++;
                  if (this.fullBlockTicks >= 5) {
                     c03PacketPlayer.x += d2;
                     c03PacketPlayer.z += d3;
                     c03PacketPlayer.y--;
                     this.fullBlockTicks = 0;
                  }
               }
            }
            break;
         case RECEIVED:
            if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook && this.mode.getValue() == PhaseMode.VERTICAL) {
               this.teleported = true;
            }

            if (this.mode.getValue() == PhaseMode.VANILLA && packetEvent.getPacket() instanceof S08PacketPlayerPosLook && this.stage == 2) {
               this.stage = 1;
            }
      }
   };
   private static final long d = 39151312453650L;

   public Phase(Category category) {
      super(category);
      this.addSettings(this.mode, this.speed);
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + PhaseMode.G(this.mode.getValue()));
      super.p();
   }

   @Override
   public void onDisable() {
      this.fullBlockTicks = 0;
      this.stage = 0;
      mc.thePlayer.noClip = false;
      this.descendTimer.reset();
      super.onDisable();
   }

   private boolean isInsideBlock(EntityPlayer entityPlayer) {
      for (int i = MathHelper.floor_double(entityPlayer.getEntityBoundingBox().minX);
         i < MathHelper.floor_double(entityPlayer.getEntityBoundingBox().maxX) + 1;
         i++
      ) {
         for (int j = MathHelper.floor_double(entityPlayer.getEntityBoundingBox().minY);
            j < MathHelper.floor_double(entityPlayer.getEntityBoundingBox().maxY) + 1;
            j++
         ) {
            for (int k = MathHelper.floor_double(entityPlayer.getEntityBoundingBox().minZ);
               k < MathHelper.floor_double(entityPlayer.getEntityBoundingBox().maxZ) + 1;
               k++
            ) {
               Block block = mc.theWorld.getBlockState(new BlockPos(i, j, k)).getBlock();
               if (block != null && !(block instanceof BlockAir)) {
                  AxisAlignedBB axisAlignedBB = block.getCollisionBoundingBox(
                     mc.theWorld, new BlockPos(i, j, k), mc.theWorld.getBlockState(new BlockPos(i, j, k))
                  );
                  if (block instanceof BlockHopper) {
                     axisAlignedBB = new AxisAlignedBB(i, j, k, i + 1, j + 1, k + 1);
                  }

                  if (axisAlignedBB != null && entityPlayer.getEntityBoundingBox().intersectsWith(axisAlignedBB)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static void moveForward(double d2) {
      double d = mc.thePlayer.movementInput.moveForward;
      double e = mc.thePlayer.movementInput.moveStrafe;
      float f = mc.thePlayer.rotationYaw;
      if (d != 0.0) {
         if (e > 0.0) {
            f += d > 0.0 ? -45 : 45;
         } else if (e < 0.0) {
            f += d > 0.0 ? 45 : -45;
         }

         e = 0.0;
         if (d > 0.0) {
            d = 1.0;
         } else if (d < 0.0) {
            d = -1.0;
         }
      }

      double d3 = mc.thePlayer.posX;
      double d4 = mc.thePlayer.posY;
      double d5 = mc.thePlayer.posZ;
      double d6 = d * d2 * Math.cos(Math.toRadians(f + 88.0F)) + e * d2 * Math.sin(Math.toRadians(f + 88.0F));
      double d7 = d * d2 * Math.sin(Math.toRadians(f + 88.0F)) - e * d2 * Math.cos(Math.toRadians(f + 88.0F));
      mc.thePlayer.setPosition(d3 + d6, d4, d5 + d7);
   }

   public static boolean isBlockBelow(double d2) {
      return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -d2, 0.0)).isEmpty();
   }
}
