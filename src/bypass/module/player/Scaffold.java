package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.SafeWalkEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderHotbarEvent;
import bypass.event.render.RenderLivingEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.Disabler;
import bypass.module.exploits.DisablerMode;
import bypass.module.hidden.HypixelGameTracker;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.RandomUtils;
import bypass.util.Rotation;
import bypass.util.Rotator;
import bypass.util.Stopwatch;
import bypass.via.ViaFabric;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import org.lwjgl.input.Keyboard;

public final class Scaffold extends Module {
   private final Rotator rotator = new Rotator(false, 15.0F);
   public static ScaffoldTarget target;
   private ScaffoldTarget lastTarget;
   private final NumberSetting<Integer> slot = new NumberSetting<>("Slot", "Slots where the blocks will be on hotbar", 9, 1, 9);
   private final BooleanSetting spoof = new BooleanSetting("Spoof", "Spoofs item", true);
   private final BooleanSetting rayCast = new BooleanSetting("Ray-Cast", "Ray casts to block", false);
   private final EnumSetting<ScaffoldTowerMode> tower = new EnumSetting<>("Tower", "Towers up", ScaffoldTowerMode.TRUE);
   private final BooleanSetting towerCenter = new BooleanSetting("Tower-Center", "Centers when towering", true);
   private final BooleanSetting hotbarMove = new BooleanSetting("Hotbar-Move", "Moves items to hotbar automatically", true);
   private final EnumSetting<ScaffoldPlaceMode> placeMode = new EnumSetting<>("Place-Mode", "Place mode", ScaffoldPlaceMode.POST);
   private final BooleanSetting keepRotations = new BooleanSetting("Keep-Rotations", "Keeps rotations on", false);
   public static final BooleanSetting keepSprint = new BooleanSetting("Keep-Sprint", "Sprint when scaffolding", true);
   private final BooleanSetting slow = new BooleanSetting("Slow", "Goes slow", false);
   private final BooleanSetting safeWalk = new BooleanSetting("Safe-Walk", "Safe walk", false);
   private final BooleanSetting biggestStack = new BooleanSetting("Biggest-Stack", "Uses biggest stack of blocks", true);
   private final BooleanSetting safeStack = new BooleanSetting("Safe-Stack", "Puts another stack of blocks in your hotbar to prevent bugs", true);
   private final BooleanSetting keepY = new BooleanSetting("Keep-Y", "Keeps y position when scaffolding", false);
   private final BooleanSetting stairs = new BooleanSetting("Stairs", "Stairs", true);
   private final BooleanSetting edgeCheck = new BooleanSetting("Edge-Check", "Edge-Check", false);
   public static final BooleanSetting ncpHypixel = new BooleanSetting("NCP-Hypixel", "Fixed lag backs in Hypixel while using new NCP", true);
   private final NumberSetting<Float> extend = new NumberSetting<>("Extend", "Extend block place", 0.0F, 0.0F, 5.0F);
   private final NumberSetting<Integer> safeStackSlot = new NumberSetting<>("Safe-Stack-Slot", "Slots where the safe block will be on hotbar", 9, 1, 9);
   private final Stopwatch placeTimer = new Stopwatch();
   private final Stopwatch safeStackTimer = new Stopwatch();
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay in milliseconds before another block is placed", 0, 1, 1000, "ms");
   public static final BooleanSetting down = new BooleanSetting("Down", "Down", true);
   public static final EnumSetting<ScaffoldRotationMode> rotation = new EnumSetting<>("Rotation", "Rotation mode", ScaffoldRotationMode.DEFAULT);
   private final EnumSetting<ScaffoldSwingMode> swing = new EnumSetting<>("Swing", "Swing mode", ScaffoldSwingMode.CLIENT);
   private float sentYaw;
   private float unusedPlaceValue;
   private float sentPitch;
   private float unusedFloat;
   private double lastX;
   private double lastY;
   private double lastZ;
   private int previousSlot;
   private double placeY;
   private boolean resetMotionYOnDisable;
   private int placeSlot = -1;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var0 -> {
      if (var0.getDirection() == PacketDirection.OUTGOING && var0.getPacket() instanceof C09PacketHeldItemChange && ncpHypixel.getValue()) {
         var0.cancel();
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = var1x -> {
      if (var1x.getEntity() == mc.thePlayer && target == null && this.keepRotations.getValue()) {
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = var0 -> {
      if (var0.getType() == RenderType.SCREEN && mc.thePlayer != null && mc.thePlayer.inventoryContainer != null) {
         int var4 = countBlocks();
         String var5 = String.format("%s %s", var4, var4 > 1 ? "blocks" : (var4 <= 0 ? "blocks" : "block"));
         client.getFontRenderer()
            .drawStringWithShadow(
               var5,
               var0.getResolution().getScaledWidth() / 2.0F + client.getFontRenderer().getStringWidth(var5) / 2.0F,
               var0.getResolution().getScaledHeight() / 2.0F - 3.0F,
               Color.WHITE.getRGB()
            );
      }
   };
   @EventHandler(RenderHotbarEvent.class)
   private Listener<RenderHotbarEvent> onRenderHotbar = var1x -> {
      if (this.placeSlot != -1) {
         var1x.setSlot(this.placeSlot);
      }
   };
   private boolean towering;
   private boolean stopSprintPending;
   private boolean sprintStopped;
   private boolean unusedFlag;
   private int serverSlot = -1;
   private boolean rotated;
   private int airTicks;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.NORMAL)
   private Listener<MotionEvent> onMotion = var1x -> {
      long var2 = cb ^ 75440067652333L;
      long var4 = var2 ^ 99502690093115L;
      if (mc.thePlayer.onGround) {
         this.airTicks = 0;
      } else {
         this.airTicks++;
      }

      double var7 = down.getValue()
            && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
            && mc.thePlayer.motionY < 0.0
            && !mc.thePlayer.movementInput.jump
         ? 1.5
         : 1.0;
      Vec3 var9 = new Vec3(mc.thePlayer.posX, this.placeY - var7, mc.thePlayer.posZ);
      switch (var1x.getState()) {
         case PRE:
            this.lastTarget = target;
            target = null;
            float[] var10 = null;
            ScaffoldRotationMode var11 = ncpHypixel.getValue() ? ScaffoldRotationMode.BACKWARD : rotation.getValue();
            if (Disabler.mode.getValue() == DisablerMode.WATCHDOG_TIMER && client.getModuleManager().get(Disabler.class).isEnabled()) {
               boolean var34 = true;
            } else {
               boolean var10000 = false;
            }

            if (countBlocks() > 0) {
               if (this.keepRotations.getValue()) {
                  if (var11 == ScaffoldRotationMode.EYE_ANGLE) {
                     var1x.setPitch(RandomUtils.nextFloat(85.0F, 87.0F));
                     this.sentPitch = var1x.getPitch();
                     this.sentYaw = mc.thePlayer.rotationYaw;
                  } else if (var11 == ScaffoldRotationMode.DEFAULT) {
                     Vector3d var13 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                     Vector3d var14 = new Vector3d(var1x.getLastX(), var1x.getLastY(), var1x.getLastZ());
                     var10 = new float[]{this.rotator.rotationTo(var14, var13).getYaw(), 77.0F};
                     var1x.setYaw(var10[0]);
                     this.sentYaw = var10[0];
                     this.sentPitch = var10[1];
                  } else {
                     var1x.setYaw(this.sentYaw);
                     var1x.setPitch(this.sentPitch);
                  }
               }

               if (ncpHypixel.getValue()) {
                  Rotation var22 = this.rotator
                     .smooth(new Rotation(mc.thePlayer.rotationYaw - 180.0F, 0.0F), new Rotation(var1x.getLastYaw(), var1x.getLastPitch()));
                  float var24 = MathHelper.wrapAngleTo180_float(var22.getYaw() - mc.thePlayer.rotationYaw);
                  float var15 = mc.thePlayer.rotationYaw + var24;
                  var1x.setYaw(var15);
                  this.sentYaw = var22.getYaw();
                  this.rotator.setSmoothing(40.0F);
                  var10 = new float[]{var15, this.sentPitch};
               }
            }

            int var23 = this.countHotbarBlocks();
            if (var23 <= 0) {
               if (mc.currentScreen instanceof GuiContainer) {
                  return;
               }

               this.placeY = mc.thePlayer.posY;
               int var25 = this.findInventoryBlock();
               if (var25 != -1 && this.hotbarMove.getValue()) {
                  mc.playerController.windowClick(0, var25, this.slot.getValue() - 1, 2, mc.thePlayer);
               }
            } else {
               if (!this.placeTimer.hasElapsed(this.delay.getValue().intValue()) && this.delay.getValue() > 20) {
                  return;
               }

               if (var23 < 10 && this.safeStack.getValue() && this.safeStackTimer.hasElapsed(250.0F) && this.hotbarMove.getValue()) {
                  int var26 = this.findInventoryBlock();
                  if (var26 != -1) {
                     mc.playerController.windowClick(0, var26, this.safeStackSlot.getValue() - 1, 2, mc.thePlayer);
                     this.safeStackTimer.reset();
                  }
               }

               if (ncpHypixel.getValue()) {
                  if (keepSprint.getValue()) {
                     if (mc.thePlayer.onGround) {
                        if (!client.getModuleManager().get(Timer.class).isEnabled()) {
                           mc.timer.timerSpeed = 1.28F;
                        }

                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                           PotionEffect var27 = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed);
                           if (var27.getAmplifier() == 0) {
                              mc.thePlayer.G(mc.thePlayer.B() * 0.43F);
                           } else {
                              mc.thePlayer.G(mc.thePlayer.B() * 0.1F);
                           }
                        } else {
                           mc.thePlayer.G(mc.thePlayer.B() * 0.74F);
                        }
                     } else if (mc.timer.timerSpeed == 1.28F) {
                        mc.timer.timerSpeed = 1.0F;
                     }
                  } else if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && GameUtil.isMoving(mc.thePlayer) && mc.thePlayer.isCollidedVertically) {
                     mc.thePlayer.r(0.0773);
                  }
               } else if (this.slow.getValue()) {
                  mc.thePlayer.motionX *= 0.75;
                  mc.thePlayer.motionZ *= 0.75;
               }

               if (!keepSprint.getValue()) {
                  mc.thePlayer.setSprinting(false);
               }

               if (this.stopSprintPending && GameUtil.isInHypixelGame() && mc.thePlayer.isSprinting()) {
                  mc.thePlayer.sendQueue.u(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                  this.sprintStopped = true;
                  this.stopSprintPending = false;
               }

               if (this.keepY.getValue()) {
                  if (this.placeY == -1.0 && !mc.thePlayer.isCollidedVertically) {
                     this.placeY = Math.floor(mc.thePlayer.posY);
                  }

                  if (this.placeY == -1.0 && mc.thePlayer.isCollidedVertically
                     || Math.abs(this.placeY - mc.thePlayer.posY) >= 0.41 && mc.thePlayer.isCollidedVertically
                     || mc.thePlayer.motionY > 0.0
                        && !(Math.abs(mc.thePlayer.motionX) > 0.05)
                        && !(Math.abs(mc.thePlayer.motionZ) > 0.05)
                        && mc.gameSettings.keyBindJump.pressed
                     || mc.gameSettings.keyBindSneak.getKeyCode() > 0 && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                     this.placeY = mc.thePlayer.posY;
                  }
               } else {
                  this.placeY = mc.thePlayer.posY;
               }

               BlockPos var28 = new BlockPos(mc.thePlayer.posX, this.placeY - var7, mc.thePlayer.posZ);
               ScaffoldTowerMode var29 = this.tower.getValue();
               if (this.stairs.getValue()
                  && mc.gameSettings.keyBindJump.isKeyDown()
                  && GameUtil.isMoving(mc.thePlayer)
                  && !GameUtil.isOnHypixel()
                  && !this.keepY.getValue()) {
                  if (mc.thePlayer.m(0.76) && !mc.thePlayer.m(0.75) && mc.thePlayer.motionY > 0.23 && mc.thePlayer.motionY < 0.25) {
                     mc.thePlayer.motionY = Math.round(mc.thePlayer.posY) - mc.thePlayer.posY;
                  }

                  if (mc.thePlayer.m(1.0E-4)) {
                     mc.thePlayer.motionY = 0.41965F;
                  } else if (mc.thePlayer.posY >= Math.round(mc.thePlayer.posY) - 1.0E-4
                     && mc.thePlayer.posY <= Math.round(mc.thePlayer.posY) + 1.0E-4
                     && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                     mc.thePlayer.motionY = 0.0;
                  }
               }

               if (var29 == ScaffoldTowerMode.TRUE && GameUtil.isOnHypixel() && mc.gameSettings.keyBindJump.isKeyDown() && !GameUtil.isMoving(mc.thePlayer)) {
                  mc.thePlayer.setSprinting(false);
                  if (this.towerCenter.getValue() && Math.floor(mc.thePlayer.posX) + 0.5 - mc.thePlayer.posX != 0.0) {
                     mc.thePlayer.setPosition(Math.floor(mc.thePlayer.posX) + 0.5, mc.thePlayer.posY, Math.floor(mc.thePlayer.posZ) + 0.5);
                  }

                  if (mc.thePlayer.posY % 1.0 <= 0.00153598) {
                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                     mc.thePlayer.motionY = 0.42F - Math.random() / 10000.0;
                  }
               }

               if (this.stairs.getValue() && GameUtil.isOnHypixel() && mc.gameSettings.keyBindJump.isKeyDown() && GameUtil.isMoving(mc.thePlayer)) {
                  mc.thePlayer.setSprinting(false);
                  if (mc.thePlayer.posY % 1.0 <= 0.00153598) {
                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                     mc.thePlayer.motionY = 0.42F - Math.random() / 10000.0;
                  } else if (mc.thePlayer.posY % 1.0 < 0.1 && this.airTicks != 0) {
                     mc.thePlayer.motionY = 0.0;
                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                  }
               }

               if (this.extend.getValue() != 0.0F) {
                  Vec3 var16 = this.extendPosition(
                     new Vec3(mc.thePlayer.posX, this.placeY - var7, mc.thePlayer.posZ),
                     GameUtil.isMoving(mc.thePlayer) ? this.extend.getValue().floatValue() : 0.0
                  );
                  if (this.findTarget(new BlockPos(var16)) != null) {
                     var28 = new BlockPos(var16.xCoord, var16.yCoord, var16.zCoord);
                  }
               }

               if (this.isReplaceable(mc.theWorld.getBlockState(var28).getBlock())) {
                  boolean var30 = this.shouldRayCast();
                  if (var30) {
                     target = new ScaffoldTarget(null, null);
                     this.rayCastTarget(var9, var1x);
                  } else {
                     target = this.findTarget(var28);
                  }

                  if (target != null) {
                     Vector3d var17 = new Vector3d((float)mc.thePlayer.posX, (float)mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), (float)mc.thePlayer.posZ);
                     if (!ncpHypixel.getValue()) {
                        switch (var11) {
                           case SIDE:
                              if (target == null || target.facing == null || target.pos == null) {
                                 return;
                              }

                              Vector3d var18 = getAimPoint(target.pos, target.facing);
                              Rotation var19 = this.rotator.rotationTo(var18, var17);
                              var10 = new float[]{var19.getYaw(), var19.getPitch()};
                              break;
                           case BACKWARD:
                              if (target == null || target.facing == null || target.pos == null) {
                                 return;
                              }

                              var10 = getRotations(target.pos, target.facing);
                        }
                     }

                     if (var10 != null) {
                        float var31 = var10[1];
                        float var32 = var10[0];
                        float var20 = var10[0];
                        if (ncpHypixel.getValue()) {
                           var31 = (float)(80.0 + Math.random());
                        }

                        float var21 = MathHelper.wrapAngleTo180_float(var32 - mc.thePlayer.rotationYaw);
                        var32 = mc.thePlayer.rotationYaw + var21;
                        var1x.setYaw(var32);
                        var1x.setPitch(var31);
                        this.sentYaw = var32;
                        this.sentPitch = var31;
                        if (this.rotated && this.placeMode.getValue() == ScaffoldPlaceMode.PRE) {
                           this.place(var1x, var9);
                        }

                        this.rotated = true;
                     }
                  }
               }

               this.lastX = var1x.getX();
               this.lastY = var1x.getY();
               this.lastZ = var1x.getZ();
            }
            break;
         case POST:
            if (this.placeMode.getValue() == ScaffoldPlaceMode.POST) {
               this.place(var1x, var9);
            }
      }
   };
   private List<Double> towerHeights = Arrays.asList(0.419 + Math.random() / 10000.0, 0.753 + Math.random() / 10000.0);
   private int towerStage;
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      if (var1x.getState() == EventState.PRE && target != null && this.towering) {
         switch (this.towerStage) {
            case 0:
               var1x.setY(mc.thePlayer.motionY = 0.42F);
               break;
            case 1:
               var1x.setY(mc.thePlayer.motionY = 0.2F);
               break;
            case 2:
               var1x.setY(mc.thePlayer.motionY = 0.3F);
               break;
            default:
               this.towerStage = 0;
               this.towering = false;
               return;
         }

         this.towerStage++;
      }
   };
   @EventHandler(SafeWalkEvent.class)
   private Listener<SafeWalkEvent> onSafeWalk = var1x -> {
      if (this.safeWalk.getValue()
         && mc.thePlayer != null
         && mc.thePlayer.inventoryContainer != null
         && (countBlocks() == 0 || this.countHotbarBlocks() == 0 || target == null && mc.thePlayer.isCollidedVertically)) {
         var1x.setCancelled(true);
      }

      if (mc.thePlayer != null && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
         if (down.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && mc.thePlayer.motionY < 0.0 && !mc.thePlayer.movementInput.jump
            )
          {
            return;
         }

         var1x.setCancelled(this.safeWalk.getValue());
      }
   };
   private ItemBlock lastBlock;
   private static final long cb = 45716836261733L;

   public Scaffold(Category var1) {
      super(var1);
      this.addSettings(
         rotation,
         this.swing,
         this.placeMode,
         down,
         this.edgeCheck,
         this.extend,
         this.rayCast,
         this.spoof,
         this.tower,
         this.towerCenter,
         this.keepRotations,
         this.slow,
         keepSprint,
         this.stairs,
         this.safeWalk,
         this.keepY,
         this.slot,
         this.biggestStack,
         this.safeStack,
         this.safeStackSlot,
         ncpHypixel,
         this.delay,
         this.hotbarMove
      );
   }

   private void place(MotionEvent var1, Vec3 var2) {
      int var6 = this.getBlockSlot();
      if (target != null && target.pos != null && var6 != -1) {
         if (this.edgeCheck.getValue() && !isOnEdge() && mc.thePlayer.onGround) {
            return;
         }

         this.placeSlot = var6;
         if (!ncpHypixel.getValue()) {
            if (this.spoof.getValue()) {
               mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(var6));
            } else {
               mc.thePlayer.inventory.currentItem = var6;
               mc.playerController.u();
            }
         } else if (this.serverSlot != var6) {
            mc.thePlayer.sendQueue.u(new C09PacketHeldItemChange(var6));
            if (GameUtil.isOnHypixel()) {
               for (int var7 = 0; var7 < 2; var7++) {
                  mc.thePlayer
                     .sendQueue
                     .u(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
               }
            }

            this.serverSlot = var6;
         }

         ScaffoldTowerMode var8 = this.tower.getValue();
         if (mc.playerController
            .K(
               mc.thePlayer,
               mc.theWorld,
               !this.spoof.getValue() && !ncpHypixel.getValue() ? mc.thePlayer.inventory.getCurrentItem() : mc.thePlayer.inventory.getStackInSlot(var6),
               target.pos,
               target.facing,
               target.hitVec == null ? this.getHitVec(var1, var2) : target.hitVec,
               false
            )) {
            this.doSwing();
            this.unusedPlaceValue = 3.0F;
            if (var8 == ScaffoldTowerMode.SLOW) {
               this.towering = !mc.thePlayer.onGround
                  && mc.thePlayer.motionY > 0.0
                  && !(Math.abs(mc.thePlayer.motionX) > 0.05)
                  && !(Math.abs(mc.thePlayer.motionZ) > 0.05)
                  && mc.gameSettings.keyBindJump.pressed
                  && !mc.thePlayer.isPotionActive(Potion.jump);
            } else if (var8 == ScaffoldTowerMode.TRUE
               && !GameUtil.isOnHypixel()
               && !mc.thePlayer.onGround
               && mc.thePlayer.motionY > 0.0
               && !(Math.abs(mc.thePlayer.motionX) > 0.05)
               && !(Math.abs(mc.thePlayer.motionZ) > 0.05)
               && mc.gameSettings.keyBindJump.pressed
               && !mc.thePlayer.isPotionActive(Potion.jump)) {
               mc.thePlayer.motionY = 0.41986F;
               mc.thePlayer.triggerAchievement(StatList.jumpStat);
               mc.thePlayer.jumpTicks = 0;
               if (this.towerCenter.getValue() && Math.floor(mc.thePlayer.posX) + 0.5 - mc.thePlayer.posX != 0.0) {
                  mc.thePlayer.setPosition(Math.floor(mc.thePlayer.posX) + 0.5, mc.thePlayer.posY, Math.floor(mc.thePlayer.posZ) + 0.5);
               }

               if (mc.thePlayer.movementInput.jump) {
                  mc.thePlayer.onGround = false;
               }
            }

            this.placeTimer.reset();
         }

         if (this.spoof.getValue() && !ncpHypixel.getValue()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
         }
      }
   }

   public static boolean isGroundBelow(double var0) {
      return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -var0, 0.0)).isEmpty();
   }

   public void towerSpoof(MotionEvent var1) {
      double var5 = var1.getMinY() % 1.0;
      double var7 = floor(var1.getMinY());
      if (var5 > 0.419 && var5 < 0.753) {
         var1.setMinY(var7 + this.towerHeights.get(0));
      } else if (var5 > 0.753) {
         var1.setMinY(var7 + this.towerHeights.get(1));
      } else {
         var1.setMinY(var7);
         var1.onGround = true;
      }

      if (!GameUtil.isMoving(mc.thePlayer)) {
         mc.thePlayer.r(0.0);
         mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
         var1.setX(
            var1.getX()
               + (
                  mc.thePlayer.ticksExisted % 2 == 0
                     ? ThreadLocalRandom.current().nextDouble(0.06, 0.0625)
                     : -ThreadLocalRandom.current().nextDouble(0.06, 0.0625)
               )
         );
         var1.setZ(
            var1.getZ()
               + (
                  mc.thePlayer.ticksExisted % 2 != 0
                     ? ThreadLocalRandom.current().nextDouble(0.06, 0.0625)
                     : -ThreadLocalRandom.current().nextDouble(0.06, 0.0625)
               )
         );
      }
   }

   public static int floor(double var0) {
      int var5 = (int)var0;

      try {
         if (var0 < var5) {
            return var5 - 1;
         }
      } catch (IllegalArgumentException var7) {
      }

      return var5;
   }

   public static boolean isOnEdge() {
      double var3 = mc.thePlayer.motionX;
      double var5 = mc.thePlayer.motionY;
      double var7 = mc.thePlayer.motionZ;
      double var9 = var3;
      double var13 = var7;
      double var15 = var9;
      double var17 = var13;
      double var19 = 0.05;

      while (var3 != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(var3, -1.0, 0.0)).isEmpty()) {
         if (var3 < var19 && var3 >= -var19) {
            var3 = 0.0;
         } else if (var3 > 0.0) {
            var3 -= var19;
         } else {
            var3 += var19;
         }
      }

      while (var7 != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -1.0, var7)).isEmpty()) {
         if (var7 < var19 && var7 >= -var19) {
            var7 = 0.0;
         } else if (var7 > 0.0) {
            var7 -= var19;
         } else {
            var7 += var19;
         }
      }

      while (
         var3 != 0.0
            && var7 != 0.0
            && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(var3, -1.0, var7)).isEmpty()
      ) {
         if (var3 < var19 && var3 >= -var19) {
            var3 = 0.0;
         } else if (var3 > 0.0) {
            var3 -= var19;
         } else {
            var3 += var19;
         }

         if (var7 < var19 && var7 >= -var19) {
            var7 = 0.0;
         } else if (var7 > 0.0) {
            var7 -= var19;
         } else {
            var7 += var19;
         }
      }

      return var15 != var3 || var17 != var7;
   }

   @Override
   public void onEnable() {
      if (mc.thePlayer != null) {
         this.previousSlot = mc.thePlayer.inventory.currentItem;
         if (ncpHypixel.getValue() && mc.thePlayer.isSprinting()) {
            this.stopSprintPending = true;
         }
      }

      this.placeY = -1.0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.previousSlot >= 0 && mc.thePlayer != null) {
         mc.thePlayer.inventory.currentItem = this.previousSlot;
         mc.playerController.u();
      }

      if (this.resetMotionYOnDisable && mc.thePlayer != null) {
         mc.thePlayer.motionY = 0.0;
         this.resetMotionYOnDisable = false;
      }

      target = null;
      if (mc.thePlayer != null && this.sprintStopped) {
         mc.thePlayer.sendQueue.u(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
         this.sprintStopped = false;
      }

      if (this.serverSlot != -1 && mc.thePlayer != null) {
         mc.thePlayer.sendQueue.u(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
      }

      this.serverSlot = this.placeSlot = -1;
      this.sentYaw = 0.0F;
      this.sentPitch = 0.0F;
      mc.timer.timerSpeed = 1.0F;
      super.onDisable();
   }

   public static int countBlocks() {
      int var3 = 0;

      for (int var4 = 9; var4 <= 44; var4++) {
         if (mc.thePlayer.inventoryContainer.getSlot(var4).getHasStack()) {
            ItemStack var5 = mc.thePlayer.inventoryContainer.getSlot(var4).getStack();
            if (isUsableBlock(var5)) {
               var3 += var5.stackSize;
            }
         }
      }

      return var3;
   }

   public static Vector3d getAimPoint(BlockPos var0, EnumFacing var1) {
      return new Vector3d(
         (float)var0.getX() - var1.getFrontOffsetX() + 0.5F + var1.getDirectionVec().getX() * 1.5F,
         (float)var0.getY() - var1.getFrontOffsetY() + 0.5F + var1.getDirectionVec().getY() * 1.5F,
         (float)var0.getZ() - var1.getFrontOffsetZ() + 0.5F + var1.getDirectionVec().getZ() * 1.5F
      );
   }

   private void doSwing() {
      switch ((ScaffoldSwingMode)this.swing.getValue()) {
         case CLIENT:
            mc.thePlayer.swingItem();
            break;
         case SILENT:
            mc.thePlayer.sendQueue.u(new C0APacketAnimation());
      }
   }

   private boolean shouldRayCast() {
      return this.rayCast.getValue()
         || down.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && mc.thePlayer.motionY < 0.0 && !mc.thePlayer.movementInput.jump;
   }

   private void rayCastTarget(Vec3 var1, MotionEvent var2) {
      double var6 = mc.playerController.getBlockReachDistance();
      Vec3 var8 = new Vec3(
         var2.getX() - this.lastX,
         down.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && mc.thePlayer.motionY < 0.0 && !mc.thePlayer.movementInput.jump
            ? -0.25
            : Math.max(var2.getY() - this.lastY, 0.0),
         var2.getZ() - this.lastZ
      );
      Vec3 var9 = new Vec3(var8.xCoord * -var6, var8.yCoord * -var6, var8.zCoord * -var6);
      var9 = var9.l(var1);
      MovingObjectPosition var10 = mc.theWorld.rayTraceBlocks(var1, var9);
      if (this.isValidHit(var10)) {
         target.pos = var10.getBlockPos();
         target.hitVec = var10.hitVec;
         target.facing = var10.sideHit;
      }
   }

   private boolean isValidHit(MovingObjectPosition var1) {
      if (var1 != null && var1.getBlockPos() != null) {
         BlockPos var5 = var1.getBlockPos();
         Block var6 = mc.theWorld.getBlockState(var5).getBlock();
         return var6.getMaterial() != Material.air || var6.getMaterial() != Material.water || var6.getMaterial() != Material.lava;
      } else {
         return false;
      }
   }

   private boolean isReplaceable(Block var1) {
      return var1 instanceof BlockAir
         || var1 instanceof BlockTallGrass
         || var1 instanceof BlockSnow && var1.getBlockState().getBaseState().getValue(BlockSnow.LAYERS) == 1;
   }

   private int getBlockSlot() {
      ItemBlock var4 = null;
      int var5 = -1;
      int var6 = -1;

      for (int var7 = 0; var7 < 9; var7++) {
         ItemStack var8 = mc.thePlayer.inventory.getStackInSlot(var7);
         if (var8 != null && isUsableBlock(var8) && var8.stackSize > var6) {
            ItemBlock var9 = (ItemBlock)var8.getItem();
            if (this.lastBlock == null
               || (this.lastBlock.getBlock() != Blocks.redstone_block || var9.getBlock() != Blocks.tnt)
                  && (this.lastBlock.getBlock() != Blocks.tnt || var9.getBlock() != Blocks.redstone_block)) {
               var6 = var8.stackSize;
               var5 = var7;
               var4 = var9;
               if (!this.biggestStack.getValue()) {
                  return var5;
               }
            }
         }
      }

      if (var4 != null) {
         this.lastBlock = var4;
      }

      return var5;
   }

   private int findInventoryBlock() {
      for (int var4 = 9; var4 <= 35; var4++) {
         if (mc.thePlayer.inventoryContainer.getSlot(var4).getHasStack()) {
            ItemStack var5 = mc.thePlayer.inventoryContainer.getSlot(var4).getStack();
            if (isUsableBlock(var5)) {
               return var4;
            }
         }
      }

      return -1;
   }

   private static boolean isUsableBlock(ItemStack var0) {
      return var0 == null
         ? false
         : var0.getItem() instanceof ItemBlock
            && (var0.getDisplayName() == null || !var0.getDisplayName().contains("Pumpkinator"))
            && (!GameUtil.scoreboardTitleContains("BED WARS") || ((ItemBlock)var0.getItem()).getBlock() != Blocks.tnt)
            && var0.stackSize > 0
            && OldInventoryManager.isBuildingBlock((ItemBlock)var0.getItem())
            && (HypixelGameTracker.game != 3 || !(((ItemBlock)var0.getItem()).getBlock() instanceof BlockOre));
   }

   private int countHotbarBlocks() {
      int var4 = 0;

      for (int var5 = 0; var5 < 9; var5++) {
         ItemStack var6 = mc.thePlayer.inventory.getStackInSlot(var5);
         if (var6 != null && isUsableBlock(var6)) {
            var4 += var6.stackSize;
         }
      }

      return var4;
   }

   private Vec3 getHitVec(MotionEvent var1, Vec3 var2) {
      boolean var6 = ViaFabric.INSTANCE.isNative();
      if (var6) {
         double var7 = mc.playerController.getBlockReachDistance();
         Vec3 var9 = new Vec3(
            var1.getX() - this.lastX,
            down.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && mc.thePlayer.motionY < 0.0 && !mc.thePlayer.movementInput.jump
               ? -0.25
               : Math.max(var1.getY() - this.lastY, 0.0),
            var1.getZ() - this.lastZ
         );
         Vec3 var10 = new Vec3(var9.xCoord * -var7, var9.yCoord * -var7, var9.zCoord * -var7);
         var10 = var10.l(var2);
         MovingObjectPosition var11 = mc.theWorld.rayTraceBlocks(var2, var10);
         if (var11 != null && var11.getBlockPos() != null) {
            return var11.hitVec;
         }
      }

      return this.randomHitVec();
   }

   private Vec3 randomHitVec() {
      double var4 = target.pos.getX() + 0.5;
      double var6 = target.pos.getY() + 0.5;
      double var8 = target.pos.getZ() + 0.5;
      var4 += target.facing.getFrontOffsetX() / 2.0;
      var8 += target.facing.getFrontOffsetZ() / 2.0;
      var6 += target.facing.getFrontOffsetY() / 2.0;
      if (target.facing != EnumFacing.UP && target.facing != EnumFacing.DOWN) {
         var6 += randomBetween(0.49, 0.5);
      } else {
         var4 += randomBetween(0.3, -0.3);
         var8 += randomBetween(0.3, -0.3);
      }

      if (target.facing == EnumFacing.WEST || target.facing == EnumFacing.EAST) {
         var8 += randomBetween(0.3, -0.3);
      }

      if (target.facing == EnumFacing.SOUTH || target.facing == EnumFacing.NORTH) {
         var4 += randomBetween(0.3, -0.3);
      }

      return new Vec3(var4, var6, var8);
   }

   public static float[] getRotations(BlockPos var0, EnumFacing var1) {
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = 0.0;
      switch (var1) {
         case UP:
            var5 = 0.5;
            var7 = 1.0;
            var9 = 0.5;
            break;
         case NORTH:
            var5 = 0.5;
            var9 = 1.0;
            var7 = 0.9;
            break;
         case SOUTH:
            var5 = 0.5;
            var9 = 0.0;
            var7 = 0.9;
            break;
         case WEST:
            var5 = 1.0;
            var9 = 0.5;
            var7 = 0.9;
            break;
         case EAST:
            var5 = 0.0;
            var9 = 0.5;
            var7 = 0.9;
      }

      double var11 = var0.getX() + var5 - mc.thePlayer.posX;
      double var13 = var0.getY() + var7 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
      double var15 = var0.getZ() + var9 - mc.thePlayer.posZ;
      return new float[]{
         (float)(Math.atan2(var15, var11) * 180.0 / Math.PI) - 90.0F, (float)(-(Math.atan2(var13, Math.sqrt(var11 * var11 + var15 * var15)) * 180.0 / Math.PI))
      };
   }

   public static double randomBetween(double var0, double var2) {
      return Math.random() * (var0 - var2) + var2;
   }

   private ScaffoldTarget findTarget(BlockPos var1) {
      if (canPlaceAgainst(var1.add(0, -1, 0))) {
         return new ScaffoldTarget(var1.add(0, -1, 0), EnumFacing.UP);
      } else if (canPlaceAgainst(var1.add(-1, 0, 0))) {
         return new ScaffoldTarget(var1.add(-1, 0, 0), EnumFacing.EAST);
      } else if (canPlaceAgainst(var1.add(1, 0, 0))) {
         return new ScaffoldTarget(var1.add(1, 0, 0), EnumFacing.WEST);
      } else if (canPlaceAgainst(var1.add(0, 0, 1))) {
         return new ScaffoldTarget(var1.add(0, 0, 1), EnumFacing.NORTH);
      } else if (canPlaceAgainst(var1.add(0, 0, -1))) {
         return new ScaffoldTarget(var1.add(0, 0, -1), EnumFacing.SOUTH);
      } else {
         BlockPos var5 = var1.add(-1, 0, 0);
         if (canPlaceAgainst(var5.add(0, -1, 0))) {
            return new ScaffoldTarget(var5.add(0, -1, 0), EnumFacing.UP);
         } else if (canPlaceAgainst(var5.add(-1, 0, 0))) {
            return new ScaffoldTarget(var5.add(-1, 0, 0), EnumFacing.EAST);
         } else if (canPlaceAgainst(var5.add(1, 0, 0))) {
            return new ScaffoldTarget(var5.add(1, 0, 0), EnumFacing.WEST);
         } else if (canPlaceAgainst(var5.add(0, 0, 1))) {
            return new ScaffoldTarget(var5.add(0, 0, 1), EnumFacing.NORTH);
         } else if (canPlaceAgainst(var5.add(0, 0, -1))) {
            return new ScaffoldTarget(var5.add(0, 0, -1), EnumFacing.SOUTH);
         } else {
            BlockPos var6 = var1.add(1, 0, 0);
            if (canPlaceAgainst(var6.add(0, -1, 0))) {
               return new ScaffoldTarget(var6.add(0, -1, 0), EnumFacing.UP);
            } else if (canPlaceAgainst(var6.add(-1, 0, 0))) {
               return new ScaffoldTarget(var6.add(-1, 0, 0), EnumFacing.EAST);
            } else if (canPlaceAgainst(var6.add(1, 0, 0))) {
               return new ScaffoldTarget(var6.add(1, 0, 0), EnumFacing.WEST);
            } else if (canPlaceAgainst(var6.add(0, 0, 1))) {
               return new ScaffoldTarget(var6.add(0, 0, 1), EnumFacing.NORTH);
            } else if (canPlaceAgainst(var6.add(0, 0, -1))) {
               return new ScaffoldTarget(var6.add(0, 0, -1), EnumFacing.SOUTH);
            } else {
               BlockPos var7 = var1.add(0, 0, 1);
               if (canPlaceAgainst(var7.add(0, -1, 0))) {
                  return new ScaffoldTarget(var7.add(0, -1, 0), EnumFacing.UP);
               } else if (canPlaceAgainst(var7.add(-1, 0, 0))) {
                  return new ScaffoldTarget(var7.add(-1, 0, 0), EnumFacing.EAST);
               } else if (canPlaceAgainst(var7.add(1, 0, 0))) {
                  return new ScaffoldTarget(var7.add(1, 0, 0), EnumFacing.WEST);
               } else if (canPlaceAgainst(var7.add(0, 0, 1))) {
                  return new ScaffoldTarget(var7.add(0, 0, 1), EnumFacing.NORTH);
               } else if (canPlaceAgainst(var7.add(0, 0, -1))) {
                  return new ScaffoldTarget(var7.add(0, 0, -1), EnumFacing.SOUTH);
               } else {
                  BlockPos var8 = var1.add(0, 0, -1);
                  if (canPlaceAgainst(var8.add(0, -1, 0))) {
                     return new ScaffoldTarget(var8.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(var8.add(-1, 0, 0))) {
                     return new ScaffoldTarget(var8.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(var8.add(1, 0, 0))) {
                     return new ScaffoldTarget(var8.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(var8.add(0, 0, 1))) {
                     return new ScaffoldTarget(var8.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(var8.add(0, 0, -1))) {
                     return new ScaffoldTarget(var8.add(0, 0, -1), EnumFacing.SOUTH);
                  } else if (canPlaceAgainst(var5.add(0, -1, 0))) {
                     return new ScaffoldTarget(var5.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(var5.add(-1, 0, 0))) {
                     return new ScaffoldTarget(var5.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(var5.add(1, 0, 0))) {
                     return new ScaffoldTarget(var5.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(var5.add(0, 0, 1))) {
                     return new ScaffoldTarget(var5.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(var5.add(0, 0, -1))) {
                     return new ScaffoldTarget(var5.add(0, 0, -1), EnumFacing.SOUTH);
                  } else if (canPlaceAgainst(var6.add(0, -1, 0))) {
                     return new ScaffoldTarget(var6.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(var6.add(-1, 0, 0))) {
                     return new ScaffoldTarget(var6.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(var6.add(1, 0, 0))) {
                     return new ScaffoldTarget(var6.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(var6.add(0, 0, 1))) {
                     return new ScaffoldTarget(var6.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(var6.add(0, 0, -1))) {
                     return new ScaffoldTarget(var6.add(0, 0, -1), EnumFacing.SOUTH);
                  } else if (canPlaceAgainst(var7.add(0, -1, 0))) {
                     return new ScaffoldTarget(var7.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(var7.add(-1, 0, 0))) {
                     return new ScaffoldTarget(var7.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(var7.add(1, 0, 0))) {
                     return new ScaffoldTarget(var7.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(var7.add(0, 0, 1))) {
                     return new ScaffoldTarget(var7.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(var7.add(0, 0, -1))) {
                     return new ScaffoldTarget(var7.add(0, 0, -1), EnumFacing.SOUTH);
                  } else if (canPlaceAgainst(var8.add(0, -1, 0))) {
                     return new ScaffoldTarget(var8.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(var8.add(-1, 0, 0))) {
                     return new ScaffoldTarget(var8.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(var8.add(1, 0, 0))) {
                     return new ScaffoldTarget(var8.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(var8.add(0, 0, 1))) {
                     return new ScaffoldTarget(var8.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(var8.add(0, 0, -1))) {
                     return new ScaffoldTarget(var8.add(0, 0, -1), EnumFacing.SOUTH);
                  } else {
                     BlockPos var9 = var1.add(0, -1, 0);
                     if (canPlaceAgainst(var9.add(0, -1, 0))) {
                        return new ScaffoldTarget(var9.add(0, -1, 0), EnumFacing.UP);
                     } else if (canPlaceAgainst(var9.add(-1, 0, 0))) {
                        return new ScaffoldTarget(var9.add(-1, 0, 0), EnumFacing.EAST);
                     } else if (canPlaceAgainst(var9.add(1, 0, 0))) {
                        return new ScaffoldTarget(var9.add(1, 0, 0), EnumFacing.WEST);
                     } else if (canPlaceAgainst(var9.add(0, 0, 1))) {
                        return new ScaffoldTarget(var9.add(0, 0, 1), EnumFacing.NORTH);
                     } else if (canPlaceAgainst(var9.add(0, 0, -1))) {
                        return new ScaffoldTarget(var9.add(0, 0, -1), EnumFacing.SOUTH);
                     } else {
                        BlockPos var10 = var9.add(1, 0, 0);
                        if (canPlaceAgainst(var10.add(0, -1, 0))) {
                           return new ScaffoldTarget(var10.add(0, -1, 0), EnumFacing.UP);
                        } else if (canPlaceAgainst(var10.add(-1, 0, 0))) {
                           return new ScaffoldTarget(var10.add(-1, 0, 0), EnumFacing.EAST);
                        } else if (canPlaceAgainst(var10.add(1, 0, 0))) {
                           return new ScaffoldTarget(var10.add(1, 0, 0), EnumFacing.WEST);
                        } else if (canPlaceAgainst(var10.add(0, 0, 1))) {
                           return new ScaffoldTarget(var10.add(0, 0, 1), EnumFacing.NORTH);
                        } else if (canPlaceAgainst(var10.add(0, 0, -1))) {
                           return new ScaffoldTarget(var10.add(0, 0, -1), EnumFacing.SOUTH);
                        } else {
                           BlockPos var11 = var9.add(-1, 0, 0);
                           if (canPlaceAgainst(var11.add(0, -1, 0))) {
                              return new ScaffoldTarget(var11.add(0, -1, 0), EnumFacing.UP);
                           } else if (canPlaceAgainst(var11.add(-1, 0, 0))) {
                              return new ScaffoldTarget(var11.add(-1, 0, 0), EnumFacing.EAST);
                           } else if (canPlaceAgainst(var11.add(1, 0, 0))) {
                              return new ScaffoldTarget(var11.add(1, 0, 0), EnumFacing.WEST);
                           } else if (canPlaceAgainst(var11.add(0, 0, 1))) {
                              return new ScaffoldTarget(var11.add(0, 0, 1), EnumFacing.NORTH);
                           } else if (canPlaceAgainst(var11.add(0, 0, -1))) {
                              return new ScaffoldTarget(var11.add(0, 0, -1), EnumFacing.SOUTH);
                           } else {
                              BlockPos var12 = var9.add(0, 0, 1);
                              if (canPlaceAgainst(var12.add(0, -1, 0))) {
                                 return new ScaffoldTarget(var12.add(0, -1, 0), EnumFacing.UP);
                              } else if (canPlaceAgainst(var12.add(-1, 0, 0))) {
                                 return new ScaffoldTarget(var12.add(-1, 0, 0), EnumFacing.EAST);
                              } else if (canPlaceAgainst(var12.add(1, 0, 0))) {
                                 return new ScaffoldTarget(var12.add(1, 0, 0), EnumFacing.WEST);
                              } else if (canPlaceAgainst(var12.add(0, 0, 1))) {
                                 return new ScaffoldTarget(var12.add(0, 0, 1), EnumFacing.NORTH);
                              } else if (canPlaceAgainst(var12.add(0, 0, -1))) {
                                 return new ScaffoldTarget(var12.add(0, 0, -1), EnumFacing.SOUTH);
                              } else {
                                 BlockPos var13 = var9.add(0, 0, -1);
                                 if (canPlaceAgainst(var13.add(0, -1, 0))) {
                                    return new ScaffoldTarget(var13.add(0, -1, 0), EnumFacing.UP);
                                 } else if (canPlaceAgainst(var13.add(-1, 0, 0))) {
                                    return new ScaffoldTarget(var13.add(-1, 0, 0), EnumFacing.EAST);
                                 } else if (canPlaceAgainst(var13.add(1, 0, 0))) {
                                    return new ScaffoldTarget(var13.add(1, 0, 0), EnumFacing.WEST);
                                 } else if (canPlaceAgainst(var13.add(0, 0, 1))) {
                                    return new ScaffoldTarget(var13.add(0, 0, 1), EnumFacing.NORTH);
                                 } else {
                                    return canPlaceAgainst(var13.add(0, 0, -1)) ? new ScaffoldTarget(var13.add(0, 0, -1), EnumFacing.SOUTH) : null;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean canPlaceAgainst(BlockPos var0) {
      Block var4 = mc.theWorld.getBlockState(var0).getBlock();
      return (
            var4.getMaterial().isSolid()
               || !var4.isTranslucent()
               || var4.isFullBlock()
               || var4 instanceof BlockLadder
               || var4 instanceof BlockCarpet
               || var4 instanceof BlockSnow
               || var4 instanceof BlockSkull
         )
         && !var4.getMaterial().isLiquid()
         && !(var4 instanceof BlockContainer);
   }

   public Vec3 extendPosition(Vec3 var1, double var2) {
      float var4 = mc.thePlayer.rotationYaw;
      double var5 = Math.cos((var4 + 90.0F) * Math.PI / 180.0) * var2;
      double var7 = Math.sin((var4 + 90.0F) * Math.PI / 180.0) * var2;
      return var1.addVector(var5, 0.0, var7);
   }

   public BooleanSetting getKeepSprint() {
      return keepSprint;
   }
}
