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
   private Listener<Block.CollisionBoxEvent> onCollisionBox = var1x -> {
      if (!mc.isSingleplayer()
         && (mc.thePlayer.getEntityBoundingBox().minY <= var1x.getY() || this.mode.getValue() == PhaseMode.VANILLA && this.isInsideBlock(mc.thePlayer))) {
         var1x.setBoundingBox(null);
      }
   };
   @EventHandler(TickEvent.class)
   private Listener<TickEvent> onTick = var1x -> {
      if (var1x.getState() == EventState.PRE && mc.theWorld != null && mc.thePlayer != null && this.mode.getValue() == PhaseMode.VANILLA) {
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

         double var5 = 1.0;
         double var7 = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw));
         double var9 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw));
         double var11 = mc.thePlayer.movementInput.moveForward * var5 * var7 + mc.thePlayer.movementInput.moveStrafe * var5 * var9;
         double var13 = mc.thePlayer.movementInput.moveForward * var5 * var9 - mc.thePlayer.movementInput.moveStrafe * var5 * var7;
         if (mc.thePlayer.isCollidedHorizontally && !this.isInsideBlock(mc.thePlayer) && GameUtil.isMoving(mc.thePlayer)) {
            mc.thePlayer
               .sendQueue
               .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + var11, mc.thePlayer.posY, mc.thePlayer.posZ + var13, true));
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
   private Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      if (this.mode.getValue() == PhaseMode.SKIP && var1x.getState() == EventState.PRE && mc.thePlayer.isSneaking() && mc.thePlayer.isCollidedHorizontally) {
         float var5 = mc.thePlayer.rotationYaw;
         float var6 = 1.2F;
         mc.thePlayer.getEntityBoundingBox().X(var6 * Math.cos(Math.toRadians(var5 + 90.0F)), 0.0, var6 * Math.sin(Math.toRadians(var5 + 90.0F)));
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
   private Listener<MotionEvent> onMotion = var1x -> {
      boolean var5 = this.isInsideBlock(mc.thePlayer);
      switch (var1x.getState()) {
         case PRE:
            switch ((PhaseMode)this.mode.getValue()) {
               case VERTICAL:
                  if (this.teleported || var5) {
                     var1x.setOnGround(false);
                     mc.thePlayer.noClip = true;
                     if (mc.gameSettings.keyBindJump.pressed && mc.thePlayer.posY == (int)mc.thePlayer.posY) {
                        mc.thePlayer.jump();
                     }
                  }

                  this.stage++;
                  if (mc.thePlayer.movementInput.sneak && !GameUtil.isMoving(mc.thePlayer)) {
                     if (isBlockBelow(0.001) && !var5) {
                        var1x.setY(mc.thePlayer.posY - 1.0);
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
                        this.descendTimer.reset();
                        mc.thePlayer.motionY = 0.0;
                     } else if (this.descendTimer.hasElapsed(100.0F) && mc.thePlayer.posY == (int)mc.thePlayer.posY) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.8, mc.thePlayer.posZ);
                     }
                  }

                  if (var5 && this.stage >= 3) {
                     if (this.teleported) {
                        moveForward(0.617);
                        float var23 = (float)Math.sin(this.wobbleTicks) * 0.1F;
                        float var17 = (float)Math.cos(this.wobbleTicks) * 0.1F;
                        mc.thePlayer.rotationYaw += var23;
                        mc.thePlayer.rotationPitch += var17;
                        this.wobbleTicks++;
                     } else {
                        moveForward(0.031);
                     }

                     return;
                  }

                  return;
               case PACKETLESS:
                  double var20 = 0.39;
                  mc.thePlayer.noClip = true;
                  double var21 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                  double var22 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                  double var18 = mc.thePlayer.moveForward * var20 * var21 + mc.thePlayer.moveStrafing * var20 * var22;
                  double var19 = mc.thePlayer.moveForward * var20 * var22 + mc.thePlayer.moveStrafing * var20 * var21;
                  if (var5) {
                     var1x.setOnGround(true);
                     mc.thePlayer.r(mc.thePlayer.B());
                     mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.525 : (mc.thePlayer.movementInput.sneak ? -0.525 : 0.0);
                     mc.thePlayer.setPosition(mc.thePlayer.posX + var18, mc.thePlayer.posY, mc.thePlayer.posZ + var19);
                  }

                  return;
               default:
                  return;
            }
         case POST:
            switch ((PhaseMode)this.mode.getValue()) {
               case FULLBLOCK:
                  double var6 = 0.4;
                  double var8 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                  double var10 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                  double var12 = mc.thePlayer.movementInput.moveForward * var6 * var8 + mc.thePlayer.movementInput.moveStrafe * var6 * var10;
                  double var14 = mc.thePlayer.movementInput.moveForward * var6 * var10 - mc.thePlayer.movementInput.moveStrafe * var6 * var8;
                  if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !var5) {
                     mc.thePlayer
                        .sendQueue
                        .addToSendQueue(
                           new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + var12, mc.thePlayer.posY, mc.thePlayer.posZ + var14, false)
                        );

                     for (int var16 = 1; var16 < 11; var16++) {
                        mc.thePlayer
                           .sendQueue
                           .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, (Double.MAX_VALUE) * var16, mc.thePlayer.posZ, false));
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
                     mc.thePlayer.setPosition(mc.thePlayer.posX + var12, mc.thePlayer.posY, mc.thePlayer.posZ + var14);
                  } else if (var5) {
                     mc.thePlayer.setPosition(mc.thePlayer.posX + var12, mc.thePlayer.posY, mc.thePlayer.posZ + var14);
                  }
            }
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      boolean var5 = this.isInsideBlock(mc.thePlayer);
      switch ((PhaseMode)this.mode.getValue()) {
         case VANILLA:
            if (var5) {
               if (mc.gameSettings.keyBindJump.isKeyDown()) {
                  var1x.setY(mc.thePlayer.motionY = this.speed.getValue().floatValue());
               } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                  var1x.setY(mc.thePlayer.motionY = -this.speed.getValue());
               } else {
                  var1x.setY(mc.thePlayer.motionY = 0.0);
               }

               GameUtil.setMoveSpeed(var1x, this.speed.getValue().floatValue(), 90.0F);
            }
            break;
         case VERTICAL:
            if (var5) {
               var1x.setX(var1x.getX() / 2.0);
               var1x.setZ(var1x.getZ() / 2.0);
            }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      switch (var1x.getDirection()) {
         case OUTGOING:
            if (var1x.getPacket() instanceof C03PacketPlayer) {
               if (this.isInsideBlock(mc.thePlayer)) {
                  return;
               }

               C03PacketPlayer var5 = (C03PacketPlayer)var1x.getPacket();
               double var8 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
               double var10 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
               double var12 = mc.thePlayer.movementInput.moveForward * 0.2 * var8 + mc.thePlayer.movementInput.moveStrafe * 0.2 * var10;
               double var14 = mc.thePlayer.movementInput.moveForward * 0.2 * var10 - mc.thePlayer.movementInput.moveStrafe * 0.2 * var8;
               if (mc.thePlayer.isCollidedHorizontally && this.mode.getValue() == PhaseMode.FULLBLOCK) {
                  this.fullBlockTicks++;
                  if (this.fullBlockTicks >= 5) {
                     var5.x += var12;
                     var5.z += var14;
                     var5.y--;
                     this.fullBlockTicks = 0;
                  }
               }
            }
            break;
         case RECEIVED:
            if (var1x.getPacket() instanceof S08PacketPlayerPosLook && this.mode.getValue() == PhaseMode.VERTICAL) {
               this.teleported = true;
            }

            if (this.mode.getValue() == PhaseMode.VANILLA && var1x.getPacket() instanceof S08PacketPlayerPosLook && this.stage == 2) {
               this.stage = 1;
            }
      }
   };
   private static final long d = 39151312453650L;

   public Phase(Category var1) {
      super(var1);
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

   private boolean isInsideBlock(EntityPlayer var1) {
      for (int var5 = MathHelper.floor_double(var1.getEntityBoundingBox().minX); var5 < MathHelper.floor_double(var1.getEntityBoundingBox().maxX) + 1; var5++) {
         for (int var6 = MathHelper.floor_double(var1.getEntityBoundingBox().minY);
            var6 < MathHelper.floor_double(var1.getEntityBoundingBox().maxY) + 1;
            var6++
         ) {
            for (int var7 = MathHelper.floor_double(var1.getEntityBoundingBox().minZ);
               var7 < MathHelper.floor_double(var1.getEntityBoundingBox().maxZ) + 1;
               var7++
            ) {
               Block var8 = mc.theWorld.getBlockState(new BlockPos(var5, var6, var7)).getBlock();
               if (var8 != null && !(var8 instanceof BlockAir)) {
                  AxisAlignedBB var9 = var8.getCollisionBoundingBox(
                     mc.theWorld, new BlockPos(var5, var6, var7), mc.theWorld.getBlockState(new BlockPos(var5, var6, var7))
                  );
                  if (var8 instanceof BlockHopper) {
                     var9 = new AxisAlignedBB(var5, var6, var7, var5 + 1, var6 + 1, var7 + 1);
                  }

                  if (var9 != null && var1.getEntityBoundingBox().intersectsWith(var9)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static void moveForward(double var0) {
      double var5 = mc.thePlayer.movementInput.moveForward;
      double var7 = mc.thePlayer.movementInput.moveStrafe;
      float var9 = mc.thePlayer.rotationYaw;
      if (var5 != 0.0) {
         if (var7 > 0.0) {
            var9 += var5 > 0.0 ? -45 : 45;
         } else if (var7 < 0.0) {
            var9 += var5 > 0.0 ? 45 : -45;
         }

         var7 = 0.0;
         if (var5 > 0.0) {
            var5 = 1.0;
         } else if (var5 < 0.0) {
            var5 = -1.0;
         }
      }

      double var10 = mc.thePlayer.posX;
      double var12 = mc.thePlayer.posY;
      double var14 = mc.thePlayer.posZ;
      double var16 = var5 * var0 * Math.cos(Math.toRadians(var9 + 88.0F)) + var7 * var0 * Math.sin(Math.toRadians(var9 + 88.0F));
      double var18 = var5 * var0 * Math.sin(Math.toRadians(var9 + 88.0F)) - var7 * var0 * Math.cos(Math.toRadians(var9 + 88.0F));
      mc.thePlayer.setPosition(var10 + var16, var12, var14 + var18);
   }

   public static boolean isBlockBelow(double var0) {
      return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -var0, 0.0)).isEmpty();
   }
}
