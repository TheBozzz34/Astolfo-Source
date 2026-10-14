package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.TeleportEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.player.Scaffold;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.MathUtil;
import bypass.util.RandomUtils;
import bypass.util.Stopwatch;
import java.awt.Color;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public final class Speed extends Module {
   private double lastDistance;
   private int unusedStage;
   private int stage;
   private double moveSpeed;
   private double boostSpeed;
   private int bhopStage;
   private boolean unusedFlag;
   public static final double BASE_SPEED = 0.221;
   private static final double SNEAK_MULTIPLIER = 0.5882352941176471;
   private static final double WATER_MULTIPLIER = 0.5203619909502263;
   private static final double SPRINT_MULTIPLIER = 1.3;
   public static final double WEB_MULTIPLIER = 0.4751131221719457;
   public static final double ICE_MULTIPLIER = 2.5;
   private double unusedMultiplier;
   private int unusedCooldown;
   private final Stopwatch boostTimer = new Stopwatch();
   private final Stopwatch timeOutTimer = new Stopwatch();
   public static final EnumSetting<SpeedMode> mode = new EnumSetting<>("Mode", "Set your speed modes", SpeedMode.BHOP);
   private final NumberSetting<Double> damageBooster = new NumberSetting<>("Damage-Booster", "Amount of boost", 0.0, 0.0, 1.0);
   private final NumberSetting<Double> iceBooster = new NumberSetting<>("Ice-Booster", "Amount of boost", 1.5, 0.0, 1.5);
   private final MapSetting timeOut = new MapSettingBuilder("Time-Out")
      .ordered()
      .description("Delay stuff")
      .add("Delay", new NumberSetting<>("Delay", "Time in ms until reenabling speed", 5000, 1, 10000, "ms"))
      .add("Mode", new EnumSetting<>("Mode", "Select how will speed disable on lag back", SpeedTimeOutMode.NONE))
      .build();
   private final MapSetting watchdog = new MapSettingBuilder("Watchdog")
      .ordered()
      .description("watchdog stuff")
      .add("Multiply-Factor", new NumberSetting<>("Multiply-Factor", "Multiply boost", 1.0F, 1.0F, 3.0F))
      .add("Multiply-Ticks", new NumberSetting<>("Multiply-Ticks", "Ticks to multiply", 0, 0, 10))
      .add("Strafe-Amount", new NumberSetting<>("Strafe-Amount", "Strafe-Amount", 0.2F, 0.0F, 1.0F))
      .add("Multiply-Tick", new BooleanSetting("Multiply-Tick", "s", false))
      .build();
   private boolean wasSprinting;
   private double lastX;
   private double lastY;
   private double lastZ;
   private int tickCounter;
   private boolean yPortGround;
   private boolean jumped;
   private boolean timerReset;
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.p();
      if (updateEvent.getState() == EventState.PRE && this.unusedCooldown > 0) {
         this.unusedCooldown--;
      }
   };
   public double speedOffset;
   private double unusedDouble;
   private boolean timedOut;
   private float lastStrafe;
   private float lastForward;
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      long l = cb ^ 11481398063088L;
      long m = l ^ 24814441719509L;

      try {
         if (mc.thePlayer == null || mc.theWorld == null) {
            return;
         }

         double d = getBaseSpeed(1.0 + this.iceBooster.getValue(), true);
         boolean bl = GameUtil.isMoving(mc.thePlayer);
         float f = mc.thePlayer.movementInput.moveForward;
         float g = mc.thePlayer.movementInput.moveStrafe;
         float h = mc.thePlayer.rotationYaw;
         double e = 0.4015F;
         if (this.timedOut) {
            return;
         }

         switch (moveEvent.getState()) {
            case PRE:
               switch ((SpeedMode)mode.getValue()) {
                  case BHOP:
                     if (mc.thePlayer.onGround) {
                        this.bhopStage = 2;
                     }

                     label355: {
                        switch (this.bhopStage) {
                           case 1:
                              if (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) {
                                 this.moveSpeed = 1.35 * d - 0.01;
                              }
                              break label355;
                           case 2:
                              if (mc.thePlayer.isCollidedVertically && (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F)) {
                                 moveEvent.setY(mc.thePlayer.motionY = e);
                                 this.moveSpeed = d * 2.149;
                              }
                              break label355;
                           case 3:
                              double d2 = 0.66 * (this.lastDistance - d);
                              this.moveSpeed = this.lastDistance - d2;
                              break label355;
                        }

                        if (!mc.theWorld
                              .getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.posY, 0.0))
                              .isEmpty()
                           || mc.thePlayer.onGround) {
                           this.bhopStage = 1;
                        }

                        this.moveSpeed = this.lastDistance - this.lastDistance / 159.0;
                     }

                     this.bhopStage++;
                     this.moveSpeed = Math.max(this.moveSpeed, d);
                     if (this.unusedStage == 0) {
                        setMotion(moveEvent, g, f, 1.0F, this.moveSpeed * 0.985);
                     }

                     mc.thePlayer.stepHeight = 0.6F;
                     break;
                  case GWEN:
                     if (client.getModuleManager().get(Scaffold.class).isEnabled()) {
                        this.boostSpeed -= 0.005;
                     }

                     if (mc.thePlayer.fallDistance > 3.0F) {
                        return;
                     }

                     if (GameUtil.isMoving(mc.thePlayer)) {
                        if (mc.thePlayer.onGround) {
                           moveEvent.setY(mc.thePlayer.motionY = 0.36);
                           GameUtil.setMoveSpeed(moveEvent, 0.0, 90.0F);
                           mc.timer.timerSpeed = 6.0F;
                           this.boostSpeed = d * (this.boostTimer.hasElapsed(500.0F) ? 2.4 : 1.5);
                        } else {
                           mc.timer.timerSpeed = 1.0F;
                           this.boostSpeed = Math.max(this.boostSpeed -= 0.008, 0.0);
                           GameUtil.setMoveSpeed(moveEvent, this.boostSpeed, 90.0F);
                        }

                        moveEvent.setY(mc.thePlayer.motionY += 0.025);
                     }
                     break;
                  case VANILLA_BHOP:
                     if (mc.theWorld == null || !bl || mc.thePlayer.isInWater() && Jesus.A() || mc.thePlayer.isInLava()) {
                        this.boostSpeed = d;
                        return;
                     }

                     label360: {
                        switch (this.stage) {
                           case 2:
                              if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && mc.thePlayer.onGround) {
                                 moveEvent.setY(mc.thePlayer.motionY = mc.thePlayer.isPotionActive(Potion.jump) ? 0.80625F : e);
                                 mc.thePlayer.triggerAchievement(StatList.jumpStat);
                                 this.moveSpeed = d * 1.8;
                              }
                              break label360;
                           case 3:
                              this.moveSpeed = this.lastDistance - 0.6965610249494268 * (this.lastDistance - d);
                              break label360;
                        }

                        if ((
                              mc.theWorld
                                       .getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0))
                                       .size()
                                    > 0
                                 || mc.thePlayer.isCollidedVertically
                           )
                           && this.stage > 0) {
                           this.stage = mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F ? 0 : 1;
                        }

                        this.moveSpeed = this.lastDistance - this.lastDistance / 157.5;
                     }

                     this.moveSpeed = Math.max(this.moveSpeed, d);
                     if (f == 0.0F && g == 0.0F) {
                        moveEvent.setX(0.0);
                        moveEvent.setZ(0.0);
                     }

                     setMotion(moveEvent, g, f, 1.0F, this.moveSpeed, 1.0);
                     if (mode.getValue() == SpeedMode.VANILLA_BHOP && GameUtil.isMoving(mc.thePlayer)) {
                        GameUtil.setMoveSpeed(moveEvent, 1.0, 88.3883F);
                     }

                     if (f != 0.0F || g != 0.0F) {
                        this.stage++;
                     }
                     break;
                  case WATCHDOG:
                     if (mc.theWorld != null && bl) {
                        if (GameUtil.isOverLiquid(mc.thePlayer.getEntityBoundingBox()) && client.getModuleManager().get(Jesus.class).isEnabled()) {
                           this.moveSpeed = d;
                           return;
                        }

                        label357: {
                           mc.thePlayer.setSprinting(true);
                           switch (this.stage) {
                              case 2:
                                 if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && mc.thePlayer.onGround) {
                                    float f2 = 0.4011F;
                                    if (mc.thePlayer.isPotionActive(Potion.jump)) {
                                       f2 = (float)((double)f2 + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                                    }

                                    moveEvent.setY(mc.thePlayer.motionY = f2);
                                    this.jumped = true;
                                    mc.thePlayer.triggerAchievement(StatList.jumpStat);
                                    this.moveSpeed = d * 2.0356;
                                 }
                                 break label357;
                              case 3:
                                 this.moveSpeed = this.lastDistance
                                    - (this.damageBooster.asDouble() > 0.1 && mc.thePlayer.hurtTime > 0 ? 1.0 - this.damageBooster.asDouble() : 0.73)
                                       * (this.lastDistance - d);
                                 break label357;
                           }

                           if ((
                                 mc.theWorld
                                          .getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0))
                                          .size()
                                       > 0
                                    || mc.thePlayer.isCollidedVertically
                              )
                              && this.stage > 0) {
                              this.stage = mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F ? 0 : 1;
                           }

                           this.moveSpeed = this.lastDistance - this.lastDistance / 100.0;
                        }

                        if (this.stage >= 6 && this.wasSprinting && GameUtil.isOnHypixel()) {
                           mc.thePlayer.sendQueue.u(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                           this.wasSprinting = false;
                        }

                        if (this.moveSpeed > 1.1
                           && (GameUtil.getBlockAtPlayer(-0.001) instanceof BlockStairs || GameUtil.getBlockAtPlayer(-0.001) instanceof BlockSlab)) {
                           this.moveSpeed = d;
                        }

                        this.moveSpeed = Math.max(this.moveSpeed, d);
                        if (f == 0.0F && g == 0.0F) {
                           moveEvent.setX(0.0);
                           moveEvent.setZ(0.0);
                        }

                        int i = this.watchdog.getSetting("Multiply-Ticks").asInt();
                        float f3 = this.watchdog.getSetting("Multiply-Factor").asFloat();
                        if (!mc.thePlayer.isCollidedVertically && this.stage != 2) {
                           this.jumped = false;
                        }

                        if (this.jumped) {
                           this.tickCounter = i;
                        }

                        if (this.tickCounter > 0) {
                           this.timerReset = false;
                           if (this.watchdog.getSetting("Multiply-Tick").asBoolean()) {
                              if (mc.thePlayer.ticksExisted % 2 == 0) {
                                 mc.timer.timerSpeed = f3;
                              } else {
                                 mc.timer.timerSpeed = 1.0F;
                              }
                           } else {
                              mc.timer.timerSpeed = f3;
                           }

                           this.tickCounter--;
                        } else if (!this.timerReset) {
                           mc.timer.timerSpeed = 1.0F;
                           this.timerReset = true;
                        }

                        this.strafeTowards(moveEvent, this.moveSpeed, this.watchdog.getSetting("Strafe-Amount").asFloat());
                        this.lastForward = mc.thePlayer.moveForward;
                        this.lastStrafe = mc.thePlayer.moveStrafing;
                        if (f != 0.0F || g != 0.0F) {
                           this.stage++;
                        }

                        this.lastX = mc.thePlayer.posX;
                        this.lastY = mc.thePlayer.posY;
                        this.lastZ = mc.thePlayer.posZ;
                        break;
                     }

                     mc.timer.timerSpeed = 1.0F;
                     this.moveSpeed = d;
                     return;
                  case SENTINEL:
                     if (bl && mc.thePlayer.onGround && this.boostTimer.hasElapsed(250.0F)) {
                        float f4 = mc.thePlayer.rotationYaw;
                        if (mc.thePlayer.moveForward < 0.0F) {
                           f4 += 180.0F;
                        }

                        float f5 = 1.0F;
                        if (mc.thePlayer.moveForward < 0.0F) {
                           f5 = -0.5F;
                        } else if (mc.thePlayer.moveForward > 0.0F) {
                           f5 = 0.5F;
                        }

                        if (mc.thePlayer.moveStrafing > 0.0F) {
                           f4 -= 90.0F * f5;
                        }

                        if (mc.thePlayer.moveStrafing < 0.0F) {
                           f4 += 90.0F * f5;
                        }

                        double d3 = Math.toRadians(f4);
                        moveEvent.setX(-Math.sin(d3) * 2.0);
                        moveEvent.setZ(Math.cos(d3) * 2.0);
                        this.boostTimer.reset();
                     }
                     break;
                  case SQUADS:
                     mc.timer.timerSpeed = 1.0F;
                     if (mc.thePlayer.onGround && bl) {
                        moveEvent.setY(mc.thePlayer.motionY = 0.4056);
                        mc.timer.timerSpeed = (float)(mc.timer.timerSpeed * 4.0);
                        this.moveSpeed = d * 0.9331;
                     }

                     GameUtil.setMoveSpeed(moveEvent, this.moveSpeed * 1.7F, 90.0F);
               }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   };
   private int unusedInt;
   private boolean knockedBack;
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         this.lastDistance = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
         float f = mc.thePlayer.movementInput.moveForward;
         float g = mc.thePlayer.movementInput.moveStrafe;
         if (this.timedOut) {
            return;
         }

         if (mode.getValue() == SpeedMode.TELEPORT && GameUtil.isMoving(mc.thePlayer)) {
            if (mc.thePlayer.ticksExisted % 3 == 0) {
               mc.thePlayer.r(5.0);
            } else {
               mc.thePlayer.r(0.0);
            }
         }

         if (mode.getValue() == SpeedMode.HIGH_JUMP && GameUtil.isMoving(mc.thePlayer)) {
            if (mc.thePlayer.onGround) {
               mc.thePlayer.r(0.5);
               mc.thePlayer.motionY = 0.9;
            } else {
               mc.thePlayer.r(mc.thePlayer.B());
            }
         }

         if (mode.getValue() == SpeedMode.YPORT) {
            if (Math.abs(mc.thePlayer.moveForward) + Math.abs(mc.thePlayer.moveStrafing) > 0.1F
               && !mc.thePlayer.isCollidedHorizontally
               && !(GameUtil.getBlockAtPlayer() instanceof BlockStairs)) {
               mc.thePlayer.motionY = -9.0;
            }

            if (GameUtil.getBlockAtPlayer() instanceof BlockIce || GameUtil.getBlockAtPlayer() instanceof BlockPackedIce) {
               GameUtil.getBlockAtPlayer().slipperiness = 0.65F;
            }

            if ((mc.thePlayer.onGround || mc.thePlayer.isCollidedVertically)
               && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ)).getBlock() instanceof BlockAir) {
               if (GameUtil.isMoving(mc.thePlayer)) {
                  this.tickCounter++;
               } else {
                  this.tickCounter = 0;
               }

               if (this.tickCounter < 4 || !mc.thePlayer.onGround) {
                  mc.timer.timerSpeed = 1.0F;
                  this.yPortGround = true;
               } else if (!this.yPortGround) {
                  mc.thePlayer.motionX *= 3.3F;
                  mc.thePlayer.motionZ *= 3.3F;
               } else {
                  mc.thePlayer.motionX /= 1.4;
                  mc.thePlayer.motionZ /= 1.4;
               }
            } else {
               this.tickCounter = 3;
               mc.thePlayer.motionX /= 1.3F;
               mc.thePlayer.motionZ /= 1.3F;
               mc.timer.timerSpeed = 1.0F;
            }
         }

         if (mode.getValue() == SpeedMode.NCP && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround && GameUtil.isMoving(mc.thePlayer)) {
            motionEvent.setMinY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 == 0 ? this.freeHeightUpTo(0.40325F) : 1.625E-12));
            mc.thePlayer.r(mc.thePlayer.ticksExisted % 2 == 0 ? 0.16 : 0.34);
            mc.timer.timerSpeed = mc.thePlayer.ticksExisted % 4 == 0 ? 1.4F : 1.0F;
         }

         if (mode.getValue() == SpeedMode.AAC_3_BHOP) {
            if (!GameUtil.isMoving(mc.thePlayer) || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isOnLadder() || mc.thePlayer.E()) {
               return;
            }

            if (mc.thePlayer.hurtTime > 0) {
               this.knockedBack = true;
               return;
            }

            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
               mc.thePlayer.jump();
               if (!this.knockedBack) {
                  mc.thePlayer.motionY = 0.41;
               }

               this.knockedBack = false;
            } else if (!mc.thePlayer.isCollidedHorizontally && !this.knockedBack) {
               mc.thePlayer.jumpMovementFactor = 0.027F;
               float h = mc.thePlayer.motionY <= 0.0 ? RandomUtils.nextFloat(1.002F, 1.0023F) : RandomUtils.nextFloat(1.0059F, 1.0061F);
               mc.thePlayer.motionX *= h;
               mc.thePlayer.motionZ *= h;
               moveForward(0.0019);
               mc.thePlayer.motionY -= 0.0149F;
            }
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C03PacketPlayer
            && mode.getValue() == SpeedMode.YPORT
            && mc.thePlayer.onGround
            && mc.thePlayer.isCollidedVertically
            && this.tickCounter > 5
            && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ)).getBlock() instanceof BlockAir
            && this.tickCounter > 5) {
            this.yPortGround = !this.yPortGround;
            if (this.yPortGround) {
               packetEvent.setPacket(
                  new C03PacketPlayer.C06PacketPlayerPosLook(
                     mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true
                  )
               );
            } else {
               packetEvent.setPacket(
                  new C03PacketPlayer.C06PacketPlayerPosLook(
                     mc.thePlayer.posX, mc.thePlayer.posY + 0.425000023, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false
                  )
               );
            }
         }
      } else if (packetEvent.getPacket() instanceof C0BPacketEntityAction) {
         C0BPacketEntityAction c0BPacketEntityAction = (C0BPacketEntityAction)packetEvent.getPacket();
         if (mode.getValue() == SpeedMode.WATCHDOG
            && GameUtil.isMoving(mc.thePlayer)
            && (c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.START_SPRINTING || c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)) {
            packetEvent.cancel();
         }
      }
   };
   @EventHandler(TeleportEvent.class)
   private final Listener<TeleportEvent> onTeleport = teleportEvent -> {
      if (teleportEvent.y()) {
         switch ((SpeedTimeOutMode)this.timeOut.getSetting("Mode").getValue()) {
            case WAIT:
               this.timedOut = true;
               this.timeOutTimer.reset();
               break;
            case DISABLE:
               toasts.A("Lag back", "Disabling Speed due to lag back", 1500);
               this.setEnabled(false);
         }

         this.moveSpeed = this.boostSpeed = 0.0;
         this.stage = 2;
         this.unusedMultiplier = 1.0;
      }
   };
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN && this.timedOut) {
         if (this.timeOutTimer.hasElapsed(this.timeOut.getSetting("Delay").asInt())) {
            this.timedOut = false;
            return;
         }

         String string = String.format(
            "Speed will be re-enabled in: %ss",
            MathUtil.ONE_DECIMAL.format((this.timeOut.getSetting("Delay").asInt() - this.timeOutTimer.getElapsed()) / 1000.0)
         );
         float f = renderEvent.getResolution().getScaledWidth() / 2.0F;
         float g = f - mc.fontRendererObj.getStringWidth(string) / 2.0F;
         float h = f - 80.0F;
         float f2 = f + 80.0F;
         int i = (int)(f2 + (h - f2) * ((float)this.timeOutTimer.getElapsed() / this.timeOut.getSetting("Delay").asFloat() * 100.0F * 0.01F));
         RenderUtil.drawRect(
            h - 3.0F,
            renderEvent.getResolution().getScaledHeight() - 93,
            f2 + 3.0F,
            renderEvent.getResolution().getScaledHeight() - 74,
            new Color(1, 1, 1, 120).getRGB()
         );
         RenderUtil.drawRect(
            i,
            renderEvent.getResolution().getScaledHeight() - 77,
            h,
            renderEvent.getResolution().getScaledHeight() - 80,
            client.getColorSetting().getValue().darker().getRGB()
         );
         RenderUtil.drawRect(
            i,
            renderEvent.getResolution().getScaledHeight() - 78.5F,
            h,
            renderEvent.getResolution().getScaledHeight() - 80,
            client.getColorSetting().getValue().getRGB()
         );
         mc.fontRendererObj.drawStringWithShadow(string, g, renderEvent.getResolution().getScaledHeight() - 90, client.getColorSetting().getValue().getRGB());
      }
   };
   private static final long cb = 47929494975638L;

   public Speed(Category category) {
      super(category);
      this.B(-1551059);
      this.addSettings(this.timeOut, this.watchdog, mode, this.damageBooster, this.iceBooster);
   }

   public double angleBetween(double d, double d2, double d3, double d4) {
      double e = Math.PI * 2;
      double d5 = 180.0 / Math.PI;
      if (d == d3 && d2 == d4) {
         return -1.0;
      }

      double d6 = Math.atan2(d3 - d, d2 - d4);
      d6 += e / 2.0;
      d6 = d6 < 0.0 ? d6 + e : d6;
      return d5 * d6;
   }

   public double getMovedAngle() {
      return this.angleBetween(this.lastX, this.lastZ, mc.thePlayer.posX, mc.thePlayer.posZ);
   }

   public float getMoveYaw() {
      float f = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
      MovementInput movementInput2 = mc.thePlayer.movementInput;
      float g = movementInput2.moveStrafe;
      float h = movementInput2.moveForward;
      if (h != 0.0F) {
         if (g < 0.0F) {
            f += h < 0.0F ? 135.0F : 45.0F;
         } else if (g > 0.0F) {
            f -= h < 0.0F ? 135.0F : 45.0F;
         } else if (h < 0.0F) {
            f -= 180.0F;
         }
      } else if (g < 0.0F) {
         f += 90.0F;
      } else if (g > 0.0F) {
         f -= 90.0F;
      }

      return MathHelper.wrapAngleTo180_float(f);
   }

   public double getMoveYaw360() {
      double d = this.getMoveYaw();

      while (d < 0.0) {
         d += 360.0;
      }

      return d;
   }

   public double getMovedDistance() {
      return Math.sqrt(Math.pow(mc.thePlayer.posX - this.lastX, 2.0) + Math.pow(mc.thePlayer.posZ - this.lastZ, 2.0));
   }

   public double[] toVector(double d, double d2) {
      double e = d2 * Math.cos(Math.toRadians(d + 90.0));
      double d3 = d2 * Math.sin(Math.toRadians(d + 90.0));
      return new double[]{e, d3};
   }

   public double dot(double[] doubles, double[] doubles2) {
      return doubles[0] * doubles2[0] + doubles[1] * doubles2[1];
   }

   public double[] scale(double[] doubles, double d) {
      return new double[]{doubles[0] * d, doubles[1] * d};
   }

   public double[] divide(double[] doubles, double d) {
      return new double[]{doubles[0] / d, doubles[1] / d};
   }

   public double[] projectOnto(double[] doubles, double[] doubles2) {
      return this.scale(doubles2, this.dot(doubles, doubles2) / this.dot(doubles2, doubles2));
   }

   public double length(double[] doubles) {
      return Math.sqrt(Math.pow(doubles[0], 2.0) + Math.pow(doubles[1], 2.0));
   }

   public double[] subtract(double[] doubles, double[] doubles2) {
      return new double[]{doubles[0] - doubles2[0], doubles[1] - doubles2[1]};
   }

   public double[] add(double[] doubles, double[] doubles2) {
      return new double[]{doubles[0] + doubles2[0], doubles[1] + doubles2[1]};
   }

   public double[] normalize(double[] doubles) {
      return this.divide(doubles, this.length(doubles));
   }

   public double[] withLength(double[] doubles, double d) {
      return this.scale(this.normalize(doubles), d);
   }

   public void strafeTowards(MoveEvent moveEvent, double d, double d2) {
      double[] doubles = this.toVector(this.getMovedAngle(), this.getMovedDistance());
      double[] doubles2 = this.toVector(this.getMoveYaw360(), d);
      double[] doubles3;
      if (this.length(this.subtract(doubles, doubles2)) > d2) {
         doubles3 = this.add(doubles, this.toVector(this.getMoveYaw360(), d2));
      } else {
         doubles3 = doubles2;
      }

      if (this.length(doubles3) > d) {
         doubles3 = this.withLength(doubles3, d);
      }

      moveEvent.setX(mc.thePlayer.motionX = doubles3[0]);
      moveEvent.setZ(mc.thePlayer.motionZ = doubles3[1]);
   }

   public static void setMotion(MoveEvent moveEvent, float f, float g, float h, double d) {
      setMotion(moveEvent, f, g, h, d, 1.0);
   }

   public static void setMotion(MoveEvent moveEvent, float f, float g, float h, double d, double d2) {
      float f2 = f * f + g * g;
      if (f2 >= 1.0E-4F) {
         f2 = MathHelper.sqrt_float(f2);
         if (f2 < 1.0F) {
            f2 = 1.0F;
         }

         f2 = h / f2;
         f *= f2;
         g *= f2;
         float f3 = MathHelper.sin(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
         float f4 = MathHelper.cos(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
         double e = f * d * f4 - g * d * f3;
         double d3 = g * d * f4 + f * d * f3;
         moveEvent.setX(e * d2);
         moveEvent.setZ(d3 * d2);
      }
   }

   public double[] unusedBoostValues() {
      double d = getBaseSpeed(1.0, true);
      if (d >= 0.28 && d < 0.3) {
         return new double[]{1.36, this.speedOffset, 1.6, 1.85};
      } else if (d >= 0.3 && d < 0.4) {
         return new double[]{1.249, 70.0 + this.speedOffset, 1.1, 1.45};
      } else {
         return d >= 0.4 ? new double[]{1.252, 75.0 + this.speedOffset, 1.25, 1.4} : new double[]{1.0, 1.0, 1.0, 1.0};
      }
   }

   public double unusedSpeedSteps(double d) {
      double e = getBaseSpeed(1.0, true);
      return Math.floor(e * 30.0 * d);
   }

   public double unusedRatio(double d, double d2) {
      double e = getBaseSpeed(1.0, true);
      double d3 = e * 10.0 * 2.0;
      double d4 = this.speedOffset * e * 1.5;
      return d / (d3 + d4 + d2);
   }

   public double unusedRatio2(double d, double d2, double d3) {
      double e = getBaseSpeed(1.0, true);
      double d4 = e * 10.0 * 2.0;
      double d5 = this.speedOffset / d2 * e * 1.5;
      return d / ((d4 + d5) * (e * (d3 * 1.4)));
   }

   public static double getNcpBaseSpeed() {
      double d = 0.2873;
      if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
         int i = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
         d *= 1.0 + 0.2 * (i + 1);
      }

      return d;
   }

   public static int getSpeedAmplifier() {
      return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
   }

   public static void setSpeed(double d) {
      double e = mc.thePlayer.movementInput.moveForward;
      double d2 = mc.thePlayer.movementInput.moveStrafe;
      float f = mc.thePlayer.rotationYaw;
      if (e == 0.0 && d2 == 0.0) {
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
      } else {
         if (e != 0.0) {
            if (d2 > 0.0) {
               f += e > 0.0 ? -45 : 45;
            } else if (d2 < 0.0) {
               f += e > 0.0 ? 45 : -45;
            }

            d2 = 0.0;
            if (e > 0.0) {
               e = 1.0;
            } else if (e < 0.0) {
               e = -1.0;
            }
         }

         mc.thePlayer.motionX = e * d * Math.cos(Math.toRadians(f + 90.0F)) + d2 * d * Math.sin(Math.toRadians(f + 90.0F));
         mc.thePlayer.motionZ = e * d * Math.sin(Math.toRadians(f + 90.0F)) - d2 * d * Math.cos(Math.toRadians(f + 90.0F));
      }
   }

   public double freeHeightUpTo(double d) {
      for (double e = 0.0; e < d; e += 0.01) {
         for (int i : new int[]{-2, -1, 0, 1, 2}) {
            if (mc.theWorld
                  .getCollidingBoundingBoxes(
                     mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(mc.thePlayer.motionX * i, e, mc.thePlayer.motionZ * i)
                  )
                  .size()
               > 0) {
               return e - 0.01;
            }
         }
      }

      return d;
   }

   public static void moveForward(double d) {
      double e = Math.toRadians(mc.thePlayer.rotationYaw);
      mc.thePlayer.setPosition(mc.thePlayer.posX + -Math.sin(e) * d, mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(e) * d);
   }

   @Override
   public void onEnable() {
      this.lastDistance = 0.0;
      if (mc.thePlayer != null) {
         this.moveSpeed = this.boostSpeed = getBaseSpeed();
         this.lastX = mc.thePlayer.posX;
         this.lastY = mc.thePlayer.posY;
         this.lastZ = mc.thePlayer.posZ;
         this.wasSprinting = mc.thePlayer.isSprinting();
      }

      this.stage = 2;
      this.bhopStage = 0;
      this.timedOut = false;
      this.boostTimer.reset();
      super.onEnable();
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + SpeedMode.K(mode.getValue()));
      super.p();
   }

   @Override
   public void onDisable() {
      mc.timer.timerSpeed = 1.0F;
      if (mc.thePlayer != null && mc.theWorld != null) {
         if (mode.getValue() == SpeedMode.TELEPORT) {
            mc.thePlayer.r(0.0);
         }

         if (mode.getValue() == SpeedMode.WATCHDOG && mc.thePlayer.isSprinting() && GameUtil.isOnHypixel() && !this.wasSprinting) {
            mc.thePlayer.sendQueue.u(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            mc.thePlayer.ej = true;
         }

         mc.thePlayer.stepHeight = 0.6F;
      }

      this.boostSpeed = this.moveSpeed = this.unusedCooldown = 0;
      super.onDisable();
   }

   public double freeHeight() {
      for (double d = 0.0; d < 0.4; d += 0.01) {
         for (int i : new int[]{-2, -1, 0, 1, 2}) {
            double e = mc.thePlayer.motionX;
            double d2 = mc.thePlayer.motionZ;
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(e * i, d, d2 * i)).size() > 0) {
               return d - 0.01;
            }
         }
      }

      return 0.4;
   }

   public static double getJumpBoostMultiplier() {
      if (mc.thePlayer.isPotionActive(Potion.jump)) {
         int i = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
         return 1.0 + 0.2 * (i + 1);
      } else {
         return 1.0;
      }
   }

   public static double getBaseSpeed(double d, boolean bl) {
      if (mc.thePlayer == null) {
         return 0.0;
      }

      PotionEffect potionEffect = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed);
      BlockPos blockPos = new BlockPos(
         MathHelper.floor_double(mc.thePlayer.posX),
         MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1,
         MathHelper.floor_double(mc.thePlayer.posZ)
      );
      double e = 0.221;
      if (mc.thePlayer.isInWeb) {
         e *= 0.4751131221719457;
      }

      if (mc.thePlayer.isInWater()) {
         e *= 0.5203619909502263;
      }

      if (mc.thePlayer.isSneaking()) {
         e *= 0.5882352941176471;
      }

      if (mc.thePlayer.isSprinting()) {
         e *= 1.3;
      }

      if (potionEffect != null && bl) {
         e *= 1.0 + 0.2 * (potionEffect.getAmplifier() + 1);
      }

      if (mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockIce || mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockPackedIce) {
         e *= d;
      }

      return e;
   }

   public static double getBaseSpeed() {
      return getBaseSpeed(2.5, true);
   }
}
