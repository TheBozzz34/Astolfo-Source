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
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C09PacketHeldItemChange && ncpHypixel.getValue()) {
         packetEvent.cancel();
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (renderLivingEvent.getEntity() == mc.thePlayer && target == null && this.keepRotations.getValue()) {
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN && mc.thePlayer != null && mc.thePlayer.inventoryContainer != null) {
         int i = countBlocks();
         String string = String.format("%s %s", i, i > 1 ? "blocks" : (i <= 0 ? "blocks" : "block"));
         client.getFontRenderer()
            .drawStringWithShadow(
               string,
               renderEvent.getResolution().getScaledWidth() / 2.0F + client.getFontRenderer().getStringWidth(string) / 2.0F,
               renderEvent.getResolution().getScaledHeight() / 2.0F - 3.0F,
               Color.WHITE.getRGB()
            );
      }
   };
   @EventHandler(RenderHotbarEvent.class)
   private Listener<RenderHotbarEvent> onRenderHotbar = renderHotbarEvent -> {
      if (this.placeSlot != -1) {
         renderHotbarEvent.setSlot(this.placeSlot);
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
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (mc.thePlayer.onGround) {
         this.airTicks = 0;
      } else {
         this.airTicks++;
      }

      double d = down.getValue()
            && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
            && mc.thePlayer.motionY < 0.0
            && !mc.thePlayer.movementInput.jump
         ? 1.5
         : 1.0;
      Vec3 vec3 = new Vec3(mc.thePlayer.posX, this.placeY - d, mc.thePlayer.posZ);
      switch (motionEvent.getState()) {
         case PRE:
            this.lastTarget = target;
            target = null;
            float[] floats = null;
            ScaffoldRotationMode scaffoldRotationMode = ncpHypixel.getValue() ? ScaffoldRotationMode.BACKWARD : rotation.getValue();
            if (Disabler.mode.getValue() == DisablerMode.WATCHDOG_TIMER && client.getModuleManager().get(Disabler.class).isEnabled()) {
               boolean bl = true;
            } else {
               boolean bl2 = false;
            }

            if (countBlocks() > 0) {
               if (this.keepRotations.getValue()) {
                  if (scaffoldRotationMode == ScaffoldRotationMode.EYE_ANGLE) {
                     motionEvent.setPitch(RandomUtils.nextFloat(85.0F, 87.0F));
                     this.sentPitch = motionEvent.getPitch();
                     this.sentYaw = mc.thePlayer.rotationYaw;
                  } else if (scaffoldRotationMode == ScaffoldRotationMode.DEFAULT) {
                     Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                     Vector3d vector3d2 = new Vector3d(motionEvent.getLastX(), motionEvent.getLastY(), motionEvent.getLastZ());
                     floats = new float[]{this.rotator.rotationTo(vector3d2, vector3d).getYaw(), 77.0F};
                     motionEvent.setYaw(floats[0]);
                     this.sentYaw = floats[0];
                     this.sentPitch = floats[1];
                  } else {
                     motionEvent.setYaw(this.sentYaw);
                     motionEvent.setPitch(this.sentPitch);
                  }
               }

               if (ncpHypixel.getValue()) {
                  Rotation rotation2 = this.rotator
                     .smooth(new Rotation(mc.thePlayer.rotationYaw - 180.0F, 0.0F), new Rotation(motionEvent.getLastYaw(), motionEvent.getLastPitch()));
                  float f = MathHelper.wrapAngleTo180_float(rotation2.getYaw() - mc.thePlayer.rotationYaw);
                  float g = mc.thePlayer.rotationYaw + f;
                  motionEvent.setYaw(g);
                  this.sentYaw = rotation2.getYaw();
                  this.rotator.setSmoothing(40.0F);
                  floats = new float[]{g, this.sentPitch};
               }
            }

            int i = this.countHotbarBlocks();
            if (i <= 0) {
               if (mc.currentScreen instanceof GuiContainer) {
                  return;
               }

               this.placeY = mc.thePlayer.posY;
               int j = this.findInventoryBlock();
               if (j != -1 && this.hotbarMove.getValue()) {
                  mc.playerController.windowClick(0, j, this.slot.getValue() - 1, 2, mc.thePlayer);
               }
            } else {
               if (!this.placeTimer.hasElapsed(this.delay.getValue().intValue()) && this.delay.getValue() > 20) {
                  return;
               }

               if (i < 10 && this.safeStack.getValue() && this.safeStackTimer.hasElapsed(250.0F) && this.hotbarMove.getValue()) {
                  int k = this.findInventoryBlock();
                  if (k != -1) {
                     mc.playerController.windowClick(0, k, this.safeStackSlot.getValue() - 1, 2, mc.thePlayer);
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
                           PotionEffect potionEffect = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed);
                           if (potionEffect.getAmplifier() == 0) {
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

               BlockPos blockPos = new BlockPos(mc.thePlayer.posX, this.placeY - d, mc.thePlayer.posZ);
               ScaffoldTowerMode scaffoldTowerMode = this.tower.getValue();
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

               if (scaffoldTowerMode == ScaffoldTowerMode.TRUE && GameUtil.isOnHypixel() && mc.gameSettings.keyBindJump.isKeyDown() && !GameUtil.isMoving(mc.thePlayer)) {
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
                  Vec3 vec32 = this.extendPosition(
                     new Vec3(mc.thePlayer.posX, this.placeY - d, mc.thePlayer.posZ),
                     GameUtil.isMoving(mc.thePlayer) ? this.extend.getValue().floatValue() : 0.0
                  );
                  if (this.findTarget(new BlockPos(vec32)) != null) {
                     blockPos = new BlockPos(vec32.xCoord, vec32.yCoord, vec32.zCoord);
                  }
               }

               if (this.isReplaceable(mc.theWorld.getBlockState(blockPos).getBlock())) {
                  boolean bl3 = this.shouldRayCast();
                  if (bl3) {
                     target = new ScaffoldTarget(null, null);
                     this.rayCastTarget(vec3, motionEvent);
                  } else {
                     target = this.findTarget(blockPos);
                  }

                  if (target != null) {
                     Vector3d vector3d3 = new Vector3d((float)mc.thePlayer.posX, (float)mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), (float)mc.thePlayer.posZ);
                     if (!ncpHypixel.getValue()) {
                        switch (scaffoldRotationMode) {
                           case SIDE:
                              if (target == null || target.facing == null || target.pos == null) {
                                 return;
                              }

                              Vector3d vector3d4 = getAimPoint(target.pos, target.facing);
                              Rotation rotation3 = this.rotator.rotationTo(vector3d4, vector3d3);
                              floats = new float[]{rotation3.getYaw(), rotation3.getPitch()};
                              break;
                           case BACKWARD:
                              if (target == null || target.facing == null || target.pos == null) {
                                 return;
                              }

                              floats = getRotations(target.pos, target.facing);
                        }
                     }

                     if (floats != null) {
                        float h = floats[1];
                        float f2 = floats[0];
                        float f3 = floats[0];
                        if (ncpHypixel.getValue()) {
                           h = (float)(80.0 + Math.random());
                        }

                        float f4 = MathHelper.wrapAngleTo180_float(f2 - mc.thePlayer.rotationYaw);
                        f2 = mc.thePlayer.rotationYaw + f4;
                        motionEvent.setYaw(f2);
                        motionEvent.setPitch(h);
                        this.sentYaw = f2;
                        this.sentPitch = h;
                        if (this.rotated && this.placeMode.getValue() == ScaffoldPlaceMode.PRE) {
                           this.place(motionEvent, vec3);
                        }

                        this.rotated = true;
                     }
                  }
               }

               this.lastX = motionEvent.getX();
               this.lastY = motionEvent.getY();
               this.lastZ = motionEvent.getZ();
            }
            break;
         case POST:
            if (this.placeMode.getValue() == ScaffoldPlaceMode.POST) {
               this.place(motionEvent, vec3);
            }
      }
   };
   private List<Double> towerHeights = Arrays.asList(0.419 + Math.random() / 10000.0, 0.753 + Math.random() / 10000.0);
   private int towerStage;
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      if (moveEvent.getState() == EventState.PRE && target != null && this.towering) {
         switch (this.towerStage) {
            case 0:
               moveEvent.setY(mc.thePlayer.motionY = 0.42F);
               break;
            case 1:
               moveEvent.setY(mc.thePlayer.motionY = 0.2F);
               break;
            case 2:
               moveEvent.setY(mc.thePlayer.motionY = 0.3F);
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
   private Listener<SafeWalkEvent> onSafeWalk = safeWalkEvent -> {
      if (this.safeWalk.getValue()
         && mc.thePlayer != null
         && mc.thePlayer.inventoryContainer != null
         && (countBlocks() == 0 || this.countHotbarBlocks() == 0 || target == null && mc.thePlayer.isCollidedVertically)) {
         safeWalkEvent.setCancelled(true);
      }

      if (mc.thePlayer != null && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
         if (down.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && mc.thePlayer.motionY < 0.0 && !mc.thePlayer.movementInput.jump
            )
          {
            return;
         }

         safeWalkEvent.setCancelled(this.safeWalk.getValue());
      }
   };
   private ItemBlock lastBlock;

   public Scaffold(Category category) {
      super(category);
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

   private void place(MotionEvent motionEvent, Vec3 vec3) {
      int i = this.getBlockSlot();
      if (target != null && target.pos != null && i != -1) {
         if (this.edgeCheck.getValue() && !isOnEdge() && mc.thePlayer.onGround) {
            return;
         }

         this.placeSlot = i;
         if (!ncpHypixel.getValue()) {
            if (this.spoof.getValue()) {
               mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i));
            } else {
               mc.thePlayer.inventory.currentItem = i;
               mc.playerController.u();
            }
         } else if (this.serverSlot != i) {
            mc.thePlayer.sendQueue.u(new C09PacketHeldItemChange(i));
            if (GameUtil.isOnHypixel()) {
               for (int j = 0; j < 2; j++) {
                  mc.thePlayer
                     .sendQueue
                     .u(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
               }
            }

            this.serverSlot = i;
         }

         ScaffoldTowerMode scaffoldTowerMode = this.tower.getValue();
         if (mc.playerController
            .K(
               mc.thePlayer,
               mc.theWorld,
               !this.spoof.getValue() && !ncpHypixel.getValue() ? mc.thePlayer.inventory.getCurrentItem() : mc.thePlayer.inventory.getStackInSlot(i),
               target.pos,
               target.facing,
               target.hitVec == null ? this.getHitVec(motionEvent, vec3) : target.hitVec,
               false
            )) {
            this.doSwing();
            this.unusedPlaceValue = 3.0F;
            if (scaffoldTowerMode == ScaffoldTowerMode.SLOW) {
               this.towering = !mc.thePlayer.onGround
                  && mc.thePlayer.motionY > 0.0
                  && !(Math.abs(mc.thePlayer.motionX) > 0.05)
                  && !(Math.abs(mc.thePlayer.motionZ) > 0.05)
                  && mc.gameSettings.keyBindJump.pressed
                  && !mc.thePlayer.isPotionActive(Potion.jump);
            } else if (scaffoldTowerMode == ScaffoldTowerMode.TRUE
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

   public static boolean isGroundBelow(double d) {
      return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -d, 0.0)).isEmpty();
   }

   public void towerSpoof(MotionEvent motionEvent) {
      double d = motionEvent.getMinY() % 1.0;
      double e = floor(motionEvent.getMinY());
      if (d > 0.419 && d < 0.753) {
         motionEvent.setMinY(e + this.towerHeights.get(0));
      } else if (d > 0.753) {
         motionEvent.setMinY(e + this.towerHeights.get(1));
      } else {
         motionEvent.setMinY(e);
         motionEvent.onGround = true;
      }

      if (!GameUtil.isMoving(mc.thePlayer)) {
         mc.thePlayer.r(0.0);
         mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
         motionEvent.setX(
            motionEvent.getX()
               + (
                  mc.thePlayer.ticksExisted % 2 == 0
                     ? ThreadLocalRandom.current().nextDouble(0.06, 0.0625)
                     : -ThreadLocalRandom.current().nextDouble(0.06, 0.0625)
               )
         );
         motionEvent.setZ(
            motionEvent.getZ()
               + (
                  mc.thePlayer.ticksExisted % 2 != 0
                     ? ThreadLocalRandom.current().nextDouble(0.06, 0.0625)
                     : -ThreadLocalRandom.current().nextDouble(0.06, 0.0625)
               )
         );
      }
   }

   public static int floor(double d) {
      int i = (int)d;

      try {
         if (d < i) {
            return i - 1;
         }
      } catch (IllegalArgumentException illegalArgumentException) {
      }

      return i;
   }

   public static boolean isOnEdge() {
      double d = mc.thePlayer.motionX;
      double e = mc.thePlayer.motionY;
      double d2 = mc.thePlayer.motionZ;
      double d3 = d;
      double d4 = d2;
      double d5 = d3;
      double d6 = d4;
      double d7 = 0.05;

      while (d != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(d, -1.0, 0.0)).isEmpty()) {
         if (d < d7 && d >= -d7) {
            d = 0.0;
         } else if (d > 0.0) {
            d -= d7;
         } else {
            d += d7;
         }
      }

      while (d2 != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -1.0, d2)).isEmpty()) {
         if (d2 < d7 && d2 >= -d7) {
            d2 = 0.0;
         } else if (d2 > 0.0) {
            d2 -= d7;
         } else {
            d2 += d7;
         }
      }

      while (
         d != 0.0
            && d2 != 0.0
            && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(d, -1.0, d2)).isEmpty()
      ) {
         if (d < d7 && d >= -d7) {
            d = 0.0;
         } else if (d > 0.0) {
            d -= d7;
         } else {
            d += d7;
         }

         if (d2 < d7 && d2 >= -d7) {
            d2 = 0.0;
         } else if (d2 > 0.0) {
            d2 -= d7;
         } else {
            d2 += d7;
         }
      }

      return d5 != d || d6 != d2;
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
      int i = 0;

      for (int j = 9; j <= 44; j++) {
         if (mc.thePlayer.inventoryContainer.getSlot(j).getHasStack()) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(j).getStack();
            if (isUsableBlock(itemStack)) {
               i += itemStack.stackSize;
            }
         }
      }

      return i;
   }

   public static Vector3d getAimPoint(BlockPos blockPos, EnumFacing enumFacing) {
      return new Vector3d(
         (float)blockPos.getX() - enumFacing.getFrontOffsetX() + 0.5F + enumFacing.getDirectionVec().getX() * 1.5F,
         (float)blockPos.getY() - enumFacing.getFrontOffsetY() + 0.5F + enumFacing.getDirectionVec().getY() * 1.5F,
         (float)blockPos.getZ() - enumFacing.getFrontOffsetZ() + 0.5F + enumFacing.getDirectionVec().getZ() * 1.5F
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

   private void rayCastTarget(Vec3 vec3, MotionEvent motionEvent) {
      double d = mc.playerController.getBlockReachDistance();
      Vec3 vec32 = new Vec3(
         motionEvent.getX() - this.lastX,
         down.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && mc.thePlayer.motionY < 0.0 && !mc.thePlayer.movementInput.jump
            ? -0.25
            : Math.max(motionEvent.getY() - this.lastY, 0.0),
         motionEvent.getZ() - this.lastZ
      );
      Vec3 vec33 = new Vec3(vec32.xCoord * -d, vec32.yCoord * -d, vec32.zCoord * -d);
      vec33 = vec33.l(vec3);
      MovingObjectPosition movingObjectPosition = mc.theWorld.rayTraceBlocks(vec3, vec33);
      if (this.isValidHit(movingObjectPosition)) {
         target.pos = movingObjectPosition.getBlockPos();
         target.hitVec = movingObjectPosition.hitVec;
         target.facing = movingObjectPosition.sideHit;
      }
   }

   private boolean isValidHit(MovingObjectPosition movingObjectPosition) {
      if (movingObjectPosition != null && movingObjectPosition.getBlockPos() != null) {
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         Block block = mc.theWorld.getBlockState(blockPos).getBlock();
         return block.getMaterial() != Material.air || block.getMaterial() != Material.water || block.getMaterial() != Material.lava;
      } else {
         return false;
      }
   }

   private boolean isReplaceable(Block block) {
      return block instanceof BlockAir
         || block instanceof BlockTallGrass
         || block instanceof BlockSnow && block.getBlockState().getBaseState().getValue(BlockSnow.LAYERS) == 1;
   }

   private int getBlockSlot() {
      ItemBlock itemBlock = null;
      int i = -1;
      int j = -1;

      for (int k = 0; k < 9; k++) {
         ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(k);
         if (itemStack != null && isUsableBlock(itemStack) && itemStack.stackSize > j) {
            ItemBlock itemBlock2 = (ItemBlock)itemStack.getItem();
            if (this.lastBlock == null
               || (this.lastBlock.getBlock() != Blocks.redstone_block || itemBlock2.getBlock() != Blocks.tnt)
                  && (this.lastBlock.getBlock() != Blocks.tnt || itemBlock2.getBlock() != Blocks.redstone_block)) {
               j = itemStack.stackSize;
               i = k;
               itemBlock = itemBlock2;
               if (!this.biggestStack.getValue()) {
                  return i;
               }
            }
         }
      }

      if (itemBlock != null) {
         this.lastBlock = itemBlock;
      }

      return i;
   }

   private int findInventoryBlock() {
      for (int i = 9; i <= 35; i++) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (isUsableBlock(itemStack)) {
               return i;
            }
         }
      }

      return -1;
   }

   private static boolean isUsableBlock(ItemStack itemStack) {
      return itemStack == null
         ? false
         : itemStack.getItem() instanceof ItemBlock
            && (itemStack.getDisplayName() == null || !itemStack.getDisplayName().contains("Pumpkinator"))
            && (!GameUtil.scoreboardTitleContains("BED WARS") || ((ItemBlock)itemStack.getItem()).getBlock() != Blocks.tnt)
            && itemStack.stackSize > 0
            && OldInventoryManager.isBuildingBlock((ItemBlock)itemStack.getItem())
            && (HypixelGameTracker.game != 3 || !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockOre));
   }

   private int countHotbarBlocks() {
      int i = 0;

      for (int j = 0; j < 9; j++) {
         ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(j);
         if (itemStack != null && isUsableBlock(itemStack)) {
            i += itemStack.stackSize;
         }
      }

      return i;
   }

   private Vec3 getHitVec(MotionEvent motionEvent, Vec3 vec3) {
      boolean bl = ViaFabric.INSTANCE.isNative();
      if (bl) {
         double d = mc.playerController.getBlockReachDistance();
         Vec3 vec32 = new Vec3(
            motionEvent.getX() - this.lastX,
            down.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && mc.thePlayer.motionY < 0.0 && !mc.thePlayer.movementInput.jump
               ? -0.25
               : Math.max(motionEvent.getY() - this.lastY, 0.0),
            motionEvent.getZ() - this.lastZ
         );
         Vec3 vec33 = new Vec3(vec32.xCoord * -d, vec32.yCoord * -d, vec32.zCoord * -d);
         vec33 = vec33.l(vec3);
         MovingObjectPosition movingObjectPosition = mc.theWorld.rayTraceBlocks(vec3, vec33);
         if (movingObjectPosition != null && movingObjectPosition.getBlockPos() != null) {
            return movingObjectPosition.hitVec;
         }
      }

      return this.randomHitVec();
   }

   private Vec3 randomHitVec() {
      double d = target.pos.getX() + 0.5;
      double e = target.pos.getY() + 0.5;
      double d2 = target.pos.getZ() + 0.5;
      d += target.facing.getFrontOffsetX() / 2.0;
      d2 += target.facing.getFrontOffsetZ() / 2.0;
      e += target.facing.getFrontOffsetY() / 2.0;
      if (target.facing != EnumFacing.UP && target.facing != EnumFacing.DOWN) {
         e += randomBetween(0.49, 0.5);
      } else {
         d += randomBetween(0.3, -0.3);
         d2 += randomBetween(0.3, -0.3);
      }

      if (target.facing == EnumFacing.WEST || target.facing == EnumFacing.EAST) {
         d2 += randomBetween(0.3, -0.3);
      }

      if (target.facing == EnumFacing.SOUTH || target.facing == EnumFacing.NORTH) {
         d += randomBetween(0.3, -0.3);
      }

      return new Vec3(d, e, d2);
   }

   public static float[] getRotations(BlockPos blockPos, EnumFacing enumFacing) {
      double d = 0.0;
      double e = 0.0;
      double d2 = 0.0;
      switch (enumFacing) {
         case UP:
            d = 0.5;
            e = 1.0;
            d2 = 0.5;
            break;
         case NORTH:
            d = 0.5;
            d2 = 1.0;
            e = 0.9;
            break;
         case SOUTH:
            d = 0.5;
            d2 = 0.0;
            e = 0.9;
            break;
         case WEST:
            d = 1.0;
            d2 = 0.5;
            e = 0.9;
            break;
         case EAST:
            d = 0.0;
            d2 = 0.5;
            e = 0.9;
      }

      double d3 = blockPos.getX() + d - mc.thePlayer.posX;
      double d4 = blockPos.getY() + e - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
      double d5 = blockPos.getZ() + d2 - mc.thePlayer.posZ;
      return new float[]{
         (float)(Math.atan2(d5, d3) * 180.0 / Math.PI) - 90.0F, (float)(-(Math.atan2(d4, Math.sqrt(d3 * d3 + d5 * d5)) * 180.0 / Math.PI))
      };
   }

   public static double randomBetween(double d, double d2) {
      return Math.random() * (d - d2) + d2;
   }

   private ScaffoldTarget findTarget(BlockPos blockPos) {
      if (canPlaceAgainst(blockPos.add(0, -1, 0))) {
         return new ScaffoldTarget(blockPos.add(0, -1, 0), EnumFacing.UP);
      } else if (canPlaceAgainst(blockPos.add(-1, 0, 0))) {
         return new ScaffoldTarget(blockPos.add(-1, 0, 0), EnumFacing.EAST);
      } else if (canPlaceAgainst(blockPos.add(1, 0, 0))) {
         return new ScaffoldTarget(blockPos.add(1, 0, 0), EnumFacing.WEST);
      } else if (canPlaceAgainst(blockPos.add(0, 0, 1))) {
         return new ScaffoldTarget(blockPos.add(0, 0, 1), EnumFacing.NORTH);
      } else if (canPlaceAgainst(blockPos.add(0, 0, -1))) {
         return new ScaffoldTarget(blockPos.add(0, 0, -1), EnumFacing.SOUTH);
      } else {
         BlockPos blockPos2 = blockPos.add(-1, 0, 0);
         if (canPlaceAgainst(blockPos2.add(0, -1, 0))) {
            return new ScaffoldTarget(blockPos2.add(0, -1, 0), EnumFacing.UP);
         } else if (canPlaceAgainst(blockPos2.add(-1, 0, 0))) {
            return new ScaffoldTarget(blockPos2.add(-1, 0, 0), EnumFacing.EAST);
         } else if (canPlaceAgainst(blockPos2.add(1, 0, 0))) {
            return new ScaffoldTarget(blockPos2.add(1, 0, 0), EnumFacing.WEST);
         } else if (canPlaceAgainst(blockPos2.add(0, 0, 1))) {
            return new ScaffoldTarget(blockPos2.add(0, 0, 1), EnumFacing.NORTH);
         } else if (canPlaceAgainst(blockPos2.add(0, 0, -1))) {
            return new ScaffoldTarget(blockPos2.add(0, 0, -1), EnumFacing.SOUTH);
         } else {
            BlockPos blockPos3 = blockPos.add(1, 0, 0);
            if (canPlaceAgainst(blockPos3.add(0, -1, 0))) {
               return new ScaffoldTarget(blockPos3.add(0, -1, 0), EnumFacing.UP);
            } else if (canPlaceAgainst(blockPos3.add(-1, 0, 0))) {
               return new ScaffoldTarget(blockPos3.add(-1, 0, 0), EnumFacing.EAST);
            } else if (canPlaceAgainst(blockPos3.add(1, 0, 0))) {
               return new ScaffoldTarget(blockPos3.add(1, 0, 0), EnumFacing.WEST);
            } else if (canPlaceAgainst(blockPos3.add(0, 0, 1))) {
               return new ScaffoldTarget(blockPos3.add(0, 0, 1), EnumFacing.NORTH);
            } else if (canPlaceAgainst(blockPos3.add(0, 0, -1))) {
               return new ScaffoldTarget(blockPos3.add(0, 0, -1), EnumFacing.SOUTH);
            } else {
               BlockPos blockPos4 = blockPos.add(0, 0, 1);
               if (canPlaceAgainst(blockPos4.add(0, -1, 0))) {
                  return new ScaffoldTarget(blockPos4.add(0, -1, 0), EnumFacing.UP);
               } else if (canPlaceAgainst(blockPos4.add(-1, 0, 0))) {
                  return new ScaffoldTarget(blockPos4.add(-1, 0, 0), EnumFacing.EAST);
               } else if (canPlaceAgainst(blockPos4.add(1, 0, 0))) {
                  return new ScaffoldTarget(blockPos4.add(1, 0, 0), EnumFacing.WEST);
               } else if (canPlaceAgainst(blockPos4.add(0, 0, 1))) {
                  return new ScaffoldTarget(blockPos4.add(0, 0, 1), EnumFacing.NORTH);
               } else if (canPlaceAgainst(blockPos4.add(0, 0, -1))) {
                  return new ScaffoldTarget(blockPos4.add(0, 0, -1), EnumFacing.SOUTH);
               } else {
                  BlockPos blockPos5 = blockPos.add(0, 0, -1);
                  if (canPlaceAgainst(blockPos5.add(0, -1, 0))) {
                     return new ScaffoldTarget(blockPos5.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(blockPos5.add(-1, 0, 0))) {
                     return new ScaffoldTarget(blockPos5.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(blockPos5.add(1, 0, 0))) {
                     return new ScaffoldTarget(blockPos5.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(blockPos5.add(0, 0, 1))) {
                     return new ScaffoldTarget(blockPos5.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(blockPos5.add(0, 0, -1))) {
                     return new ScaffoldTarget(blockPos5.add(0, 0, -1), EnumFacing.SOUTH);
                  } else if (canPlaceAgainst(blockPos2.add(0, -1, 0))) {
                     return new ScaffoldTarget(blockPos2.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(blockPos2.add(-1, 0, 0))) {
                     return new ScaffoldTarget(blockPos2.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(blockPos2.add(1, 0, 0))) {
                     return new ScaffoldTarget(blockPos2.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(blockPos2.add(0, 0, 1))) {
                     return new ScaffoldTarget(blockPos2.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(blockPos2.add(0, 0, -1))) {
                     return new ScaffoldTarget(blockPos2.add(0, 0, -1), EnumFacing.SOUTH);
                  } else if (canPlaceAgainst(blockPos3.add(0, -1, 0))) {
                     return new ScaffoldTarget(blockPos3.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(blockPos3.add(-1, 0, 0))) {
                     return new ScaffoldTarget(blockPos3.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(blockPos3.add(1, 0, 0))) {
                     return new ScaffoldTarget(blockPos3.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(blockPos3.add(0, 0, 1))) {
                     return new ScaffoldTarget(blockPos3.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(blockPos3.add(0, 0, -1))) {
                     return new ScaffoldTarget(blockPos3.add(0, 0, -1), EnumFacing.SOUTH);
                  } else if (canPlaceAgainst(blockPos4.add(0, -1, 0))) {
                     return new ScaffoldTarget(blockPos4.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(blockPos4.add(-1, 0, 0))) {
                     return new ScaffoldTarget(blockPos4.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(blockPos4.add(1, 0, 0))) {
                     return new ScaffoldTarget(blockPos4.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(blockPos4.add(0, 0, 1))) {
                     return new ScaffoldTarget(blockPos4.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(blockPos4.add(0, 0, -1))) {
                     return new ScaffoldTarget(blockPos4.add(0, 0, -1), EnumFacing.SOUTH);
                  } else if (canPlaceAgainst(blockPos5.add(0, -1, 0))) {
                     return new ScaffoldTarget(blockPos5.add(0, -1, 0), EnumFacing.UP);
                  } else if (canPlaceAgainst(blockPos5.add(-1, 0, 0))) {
                     return new ScaffoldTarget(blockPos5.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (canPlaceAgainst(blockPos5.add(1, 0, 0))) {
                     return new ScaffoldTarget(blockPos5.add(1, 0, 0), EnumFacing.WEST);
                  } else if (canPlaceAgainst(blockPos5.add(0, 0, 1))) {
                     return new ScaffoldTarget(blockPos5.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (canPlaceAgainst(blockPos5.add(0, 0, -1))) {
                     return new ScaffoldTarget(blockPos5.add(0, 0, -1), EnumFacing.SOUTH);
                  } else {
                     BlockPos blockPos6 = blockPos.add(0, -1, 0);
                     if (canPlaceAgainst(blockPos6.add(0, -1, 0))) {
                        return new ScaffoldTarget(blockPos6.add(0, -1, 0), EnumFacing.UP);
                     } else if (canPlaceAgainst(blockPos6.add(-1, 0, 0))) {
                        return new ScaffoldTarget(blockPos6.add(-1, 0, 0), EnumFacing.EAST);
                     } else if (canPlaceAgainst(blockPos6.add(1, 0, 0))) {
                        return new ScaffoldTarget(blockPos6.add(1, 0, 0), EnumFacing.WEST);
                     } else if (canPlaceAgainst(blockPos6.add(0, 0, 1))) {
                        return new ScaffoldTarget(blockPos6.add(0, 0, 1), EnumFacing.NORTH);
                     } else if (canPlaceAgainst(blockPos6.add(0, 0, -1))) {
                        return new ScaffoldTarget(blockPos6.add(0, 0, -1), EnumFacing.SOUTH);
                     } else {
                        BlockPos blockPos7 = blockPos6.add(1, 0, 0);
                        if (canPlaceAgainst(blockPos7.add(0, -1, 0))) {
                           return new ScaffoldTarget(blockPos7.add(0, -1, 0), EnumFacing.UP);
                        } else if (canPlaceAgainst(blockPos7.add(-1, 0, 0))) {
                           return new ScaffoldTarget(blockPos7.add(-1, 0, 0), EnumFacing.EAST);
                        } else if (canPlaceAgainst(blockPos7.add(1, 0, 0))) {
                           return new ScaffoldTarget(blockPos7.add(1, 0, 0), EnumFacing.WEST);
                        } else if (canPlaceAgainst(blockPos7.add(0, 0, 1))) {
                           return new ScaffoldTarget(blockPos7.add(0, 0, 1), EnumFacing.NORTH);
                        } else if (canPlaceAgainst(blockPos7.add(0, 0, -1))) {
                           return new ScaffoldTarget(blockPos7.add(0, 0, -1), EnumFacing.SOUTH);
                        } else {
                           BlockPos blockPos8 = blockPos6.add(-1, 0, 0);
                           if (canPlaceAgainst(blockPos8.add(0, -1, 0))) {
                              return new ScaffoldTarget(blockPos8.add(0, -1, 0), EnumFacing.UP);
                           } else if (canPlaceAgainst(blockPos8.add(-1, 0, 0))) {
                              return new ScaffoldTarget(blockPos8.add(-1, 0, 0), EnumFacing.EAST);
                           } else if (canPlaceAgainst(blockPos8.add(1, 0, 0))) {
                              return new ScaffoldTarget(blockPos8.add(1, 0, 0), EnumFacing.WEST);
                           } else if (canPlaceAgainst(blockPos8.add(0, 0, 1))) {
                              return new ScaffoldTarget(blockPos8.add(0, 0, 1), EnumFacing.NORTH);
                           } else if (canPlaceAgainst(blockPos8.add(0, 0, -1))) {
                              return new ScaffoldTarget(blockPos8.add(0, 0, -1), EnumFacing.SOUTH);
                           } else {
                              BlockPos blockPos9 = blockPos6.add(0, 0, 1);
                              if (canPlaceAgainst(blockPos9.add(0, -1, 0))) {
                                 return new ScaffoldTarget(blockPos9.add(0, -1, 0), EnumFacing.UP);
                              } else if (canPlaceAgainst(blockPos9.add(-1, 0, 0))) {
                                 return new ScaffoldTarget(blockPos9.add(-1, 0, 0), EnumFacing.EAST);
                              } else if (canPlaceAgainst(blockPos9.add(1, 0, 0))) {
                                 return new ScaffoldTarget(blockPos9.add(1, 0, 0), EnumFacing.WEST);
                              } else if (canPlaceAgainst(blockPos9.add(0, 0, 1))) {
                                 return new ScaffoldTarget(blockPos9.add(0, 0, 1), EnumFacing.NORTH);
                              } else if (canPlaceAgainst(blockPos9.add(0, 0, -1))) {
                                 return new ScaffoldTarget(blockPos9.add(0, 0, -1), EnumFacing.SOUTH);
                              } else {
                                 BlockPos blockPos10 = blockPos6.add(0, 0, -1);
                                 if (canPlaceAgainst(blockPos10.add(0, -1, 0))) {
                                    return new ScaffoldTarget(blockPos10.add(0, -1, 0), EnumFacing.UP);
                                 } else if (canPlaceAgainst(blockPos10.add(-1, 0, 0))) {
                                    return new ScaffoldTarget(blockPos10.add(-1, 0, 0), EnumFacing.EAST);
                                 } else if (canPlaceAgainst(blockPos10.add(1, 0, 0))) {
                                    return new ScaffoldTarget(blockPos10.add(1, 0, 0), EnumFacing.WEST);
                                 } else if (canPlaceAgainst(blockPos10.add(0, 0, 1))) {
                                    return new ScaffoldTarget(blockPos10.add(0, 0, 1), EnumFacing.NORTH);
                                 } else {
                                    return canPlaceAgainst(blockPos10.add(0, 0, -1)) ? new ScaffoldTarget(blockPos10.add(0, 0, -1), EnumFacing.SOUTH) : null;
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

   public static boolean canPlaceAgainst(BlockPos blockPos) {
      Block block = mc.theWorld.getBlockState(blockPos).getBlock();
      return (
            block.getMaterial().isSolid()
               || !block.isTranslucent()
               || block.isFullBlock()
               || block instanceof BlockLadder
               || block instanceof BlockCarpet
               || block instanceof BlockSnow
               || block instanceof BlockSkull
         )
         && !block.getMaterial().isLiquid()
         && !(block instanceof BlockContainer);
   }

   public Vec3 extendPosition(Vec3 vec3, double d) {
      float f = mc.thePlayer.rotationYaw;
      double e = Math.cos((f + 90.0F) * Math.PI / 180.0) * d;
      double d2 = Math.sin((f + 90.0F) * Math.PI / 180.0) * d;
      return vec3.addVector(e, 0.0, d2);
   }

   public BooleanSetting getKeepSprint() {
      return keepSprint;
   }
}
