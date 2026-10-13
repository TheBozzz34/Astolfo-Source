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
   private final Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      if (var1x.getState() == EventState.PRE && this.unusedCooldown > 0) {
         this.unusedCooldown--;
      }
   };
   public double speedOffset;
   private double unusedDouble;
   private boolean timedOut;
   private float lastStrafe;
   private float lastForward;
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      long var2 = cb ^ 11481398063088L;
      long var4 = var2 ^ 24814441719509L;

      try {
         if (mc.thePlayer == null || mc.theWorld == null) {
            return;
         }

         double var7 = getBaseSpeed(1.0 + this.iceBooster.getValue(), true);
         boolean var9 = GameUtil.isMoving(mc.thePlayer);
         float var10 = mc.thePlayer.movementInput.moveForward;
         float var11 = mc.thePlayer.movementInput.moveStrafe;
         float var12 = mc.thePlayer.rotationYaw;
         double var13 = 0.4015F;
         if (this.timedOut) {
            return;
         }

         switch (var1x.getState()) {
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
                                 this.moveSpeed = 1.35 * var7 - 0.01;
                              }
                              break label355;
                           case 2:
                              if (mc.thePlayer.isCollidedVertically && (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F)) {
                                 var1x.setY(mc.thePlayer.motionY = var13);
                                 this.moveSpeed = var7 * 2.149;
                              }
                              break label355;
                           case 3:
                              double var23 = 0.66 * (this.lastDistance - var7);
                              this.moveSpeed = this.lastDistance - var23;
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
                     this.moveSpeed = Math.max(this.moveSpeed, var7);
                     if (this.unusedStage == 0) {
                        setMotion(var1x, var11, var10, 1.0F, this.moveSpeed * 0.985);
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
                           var1x.setY(mc.thePlayer.motionY = 0.36);
                           GameUtil.setMoveSpeed(var1x, 0.0, 90.0F);
                           mc.timer.timerSpeed = 6.0F;
                           this.boostSpeed = var7 * (this.boostTimer.hasElapsed(500.0F) ? 2.4 : 1.5);
                        } else {
                           mc.timer.timerSpeed = 1.0F;
                           this.boostSpeed = Math.max(this.boostSpeed -= 0.008, 0.0);
                           GameUtil.setMoveSpeed(var1x, this.boostSpeed, 90.0F);
                        }

                        var1x.setY(mc.thePlayer.motionY += 0.025);
                     }
                     break;
                  case VANILLA_BHOP:
                     if (mc.theWorld == null || !var9 || mc.thePlayer.isInWater() && Jesus.A() || mc.thePlayer.isInLava()) {
                        this.boostSpeed = var7;
                        return;
                     }

                     label360: {
                        switch (this.stage) {
                           case 2:
                              if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && mc.thePlayer.onGround) {
                                 var1x.setY(mc.thePlayer.motionY = mc.thePlayer.isPotionActive(Potion.jump) ? 0.80625F : var13);
                                 mc.thePlayer.triggerAchievement(StatList.jumpStat);
                                 this.moveSpeed = var7 * 1.8;
                              }
                              break label360;
                           case 3:
                              this.moveSpeed = this.lastDistance - 0.6965610249494268 * (this.lastDistance - var7);
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

                     this.moveSpeed = Math.max(this.moveSpeed, var7);
                     if (var10 == 0.0F && var11 == 0.0F) {
                        var1x.setX(0.0);
                        var1x.setZ(0.0);
                     }

                     setMotion(var1x, var11, var10, 1.0F, this.moveSpeed, 1.0);
                     if (mode.getValue() == SpeedMode.VANILLA_BHOP && GameUtil.isMoving(mc.thePlayer)) {
                        GameUtil.setMoveSpeed(var1x, 1.0, 88.3883F);
                     }

                     if (var10 != 0.0F || var11 != 0.0F) {
                        this.stage++;
                     }
                     break;
                  case WATCHDOG:
                     if (mc.theWorld != null && var9) {
                        if (GameUtil.isOverLiquid(mc.thePlayer.getEntityBoundingBox()) && client.getModuleManager().get(Jesus.class).isEnabled()) {
                           this.moveSpeed = var7;
                           return;
                        }

                        label357: {
                           mc.thePlayer.setSprinting(true);
                           switch (this.stage) {
                              case 2:
                                 if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && mc.thePlayer.onGround) {
                                    float var15 = 0.4011F;
                                    if (mc.thePlayer.isPotionActive(Potion.jump)) {
                                       var15 = (float)((double)var15 + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                                    }

                                    var1x.setY(mc.thePlayer.motionY = var15);
                                    this.jumped = true;
                                    mc.thePlayer.triggerAchievement(StatList.jumpStat);
                                    this.moveSpeed = var7 * 2.0356;
                                 }
                                 break label357;
                              case 3:
                                 this.moveSpeed = this.lastDistance
                                    - (this.damageBooster.asDouble() > 0.1 && mc.thePlayer.hurtTime > 0 ? 1.0 - this.damageBooster.asDouble() : 0.73)
                                       * (this.lastDistance - var7);
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
                           this.moveSpeed = var7;
                        }

                        this.moveSpeed = Math.max(this.moveSpeed, var7);
                        if (var10 == 0.0F && var11 == 0.0F) {
                           var1x.setX(0.0);
                           var1x.setZ(0.0);
                        }

                        int var22 = this.watchdog.getSetting("Multiply-Ticks").asInt();
                        float var16 = this.watchdog.getSetting("Multiply-Factor").asFloat();
                        if (!mc.thePlayer.isCollidedVertically && this.stage != 2) {
                           this.jumped = false;
                        }

                        if (this.jumped) {
                           this.tickCounter = var22;
                        }

                        if (this.tickCounter > 0) {
                           this.timerReset = false;
                           if (this.watchdog.getSetting("Multiply-Tick").asBoolean()) {
                              if (mc.thePlayer.ticksExisted % 2 == 0) {
                                 mc.timer.timerSpeed = var16;
                              } else {
                                 mc.timer.timerSpeed = 1.0F;
                              }
                           } else {
                              mc.timer.timerSpeed = var16;
                           }

                           this.tickCounter--;
                        } else if (!this.timerReset) {
                           mc.timer.timerSpeed = 1.0F;
                           this.timerReset = true;
                        }

                        this.strafeTowards(var1x, this.moveSpeed, this.watchdog.getSetting("Strafe-Amount").asFloat());
                        this.lastForward = mc.thePlayer.moveForward;
                        this.lastStrafe = mc.thePlayer.moveStrafing;
                        if (var10 != 0.0F || var11 != 0.0F) {
                           this.stage++;
                        }

                        this.lastX = mc.thePlayer.posX;
                        this.lastY = mc.thePlayer.posY;
                        this.lastZ = mc.thePlayer.posZ;
                        break;
                     }

                     mc.timer.timerSpeed = 1.0F;
                     this.moveSpeed = var7;
                     return;
                  case SENTINEL:
                     if (var9 && mc.thePlayer.onGround && this.boostTimer.hasElapsed(250.0F)) {
                        float var17 = mc.thePlayer.rotationYaw;
                        if (mc.thePlayer.moveForward < 0.0F) {
                           var17 += 180.0F;
                        }

                        float var18 = 1.0F;
                        if (mc.thePlayer.moveForward < 0.0F) {
                           var18 = -0.5F;
                        } else if (mc.thePlayer.moveForward > 0.0F) {
                           var18 = 0.5F;
                        }

                        if (mc.thePlayer.moveStrafing > 0.0F) {
                           var17 -= 90.0F * var18;
                        }

                        if (mc.thePlayer.moveStrafing < 0.0F) {
                           var17 += 90.0F * var18;
                        }

                        double var19 = Math.toRadians(var17);
                        var1x.setX(-Math.sin(var19) * 2.0);
                        var1x.setZ(Math.cos(var19) * 2.0);
                        this.boostTimer.reset();
                     }
                     break;
                  case SQUADS:
                     mc.timer.timerSpeed = 1.0F;
                     if (mc.thePlayer.onGround && var9) {
                        var1x.setY(mc.thePlayer.motionY = 0.4056);
                        mc.timer.timerSpeed = (float)(mc.timer.timerSpeed * 4.0);
                        this.moveSpeed = var7 * 0.9331;
                     }

                     GameUtil.setMoveSpeed(var1x, this.moveSpeed * 1.7F, 90.0F);
               }
         }
      } catch (Exception var21) {
         var21.printStackTrace();
      }
   };
   private int unusedInt;
   private boolean knockedBack;
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         this.lastDistance = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
         float var5 = mc.thePlayer.movementInput.moveForward;
         float var6 = mc.thePlayer.movementInput.moveStrafe;
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
            var1x.setMinY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 == 0 ? this.freeHeightUpTo(0.40325F) : 1.625E-12));
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
               float var7 = mc.thePlayer.motionY <= 0.0 ? RandomUtils.nextFloat(1.002F, 1.0023F) : RandomUtils.nextFloat(1.0059F, 1.0061F);
               mc.thePlayer.motionX *= var7;
               mc.thePlayer.motionZ *= var7;
               moveForward(0.0019);
               mc.thePlayer.motionY -= 0.0149F;
            }
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (var1x.getPacket() instanceof C03PacketPlayer
            && mode.getValue() == SpeedMode.YPORT
            && mc.thePlayer.onGround
            && mc.thePlayer.isCollidedVertically
            && this.tickCounter > 5
            && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ)).getBlock() instanceof BlockAir
            && this.tickCounter > 5) {
            this.yPortGround = !this.yPortGround;
            if (this.yPortGround) {
               var1x.setPacket(
                  new C03PacketPlayer.C06PacketPlayerPosLook(
                     mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true
                  )
               );
            } else {
               var1x.setPacket(
                  new C03PacketPlayer.C06PacketPlayerPosLook(
                     mc.thePlayer.posX, mc.thePlayer.posY + 0.425000023, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false
                  )
               );
            }
         }
      } else if (var1x.getPacket() instanceof C0BPacketEntityAction) {
         C0BPacketEntityAction var5 = (C0BPacketEntityAction)var1x.getPacket();
         if (mode.getValue() == SpeedMode.WATCHDOG
            && GameUtil.isMoving(mc.thePlayer)
            && (var5.getAction() == C0BPacketEntityAction.Action.START_SPRINTING || var5.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)) {
            var1x.cancel();
         }
      }
   };
   @EventHandler(TeleportEvent.class)
   private final Listener<TeleportEvent> onTeleport = var1x -> {
      if (var1x.y()) {
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
   private final Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.SCREEN && this.timedOut) {
         if (this.timeOutTimer.hasElapsed(this.timeOut.getSetting("Delay").asInt())) {
            this.timedOut = false;
            return;
         }

         String var5 = String.format(
            "Speed will be re-enabled in: %ss",
            MathUtil.ONE_DECIMAL.format((this.timeOut.getSetting("Delay").asInt() - this.timeOutTimer.getElapsed()) / 1000.0)
         );
         float var6 = var1x.getResolution().getScaledWidth() / 2.0F;
         float var7 = var6 - mc.fontRendererObj.getStringWidth(var5) / 2.0F;
         float var8 = var6 - 80.0F;
         float var9 = var6 + 80.0F;
         int var10 = (int)(var9 + (var8 - var9) * ((float)this.timeOutTimer.getElapsed() / this.timeOut.getSetting("Delay").asFloat() * 100.0F * 0.01F));
         RenderUtil.drawRect(
            var8 - 3.0F,
            var1x.getResolution().getScaledHeight() - 93,
            var9 + 3.0F,
            var1x.getResolution().getScaledHeight() - 74,
            new Color(1, 1, 1, 120).getRGB()
         );
         RenderUtil.drawRect(
            var10,
            var1x.getResolution().getScaledHeight() - 77,
            var8,
            var1x.getResolution().getScaledHeight() - 80,
            client.getColorSetting().getValue().darker().getRGB()
         );
         RenderUtil.drawRect(
            var10,
            var1x.getResolution().getScaledHeight() - 78.5F,
            var8,
            var1x.getResolution().getScaledHeight() - 80,
            client.getColorSetting().getValue().getRGB()
         );
         mc.fontRendererObj.drawStringWithShadow(var5, var7, var1x.getResolution().getScaledHeight() - 90, client.getColorSetting().getValue().getRGB());
      }
   };
   private static final long cb = 47929494975638L;

   public Speed(Category var1) {
      super(var1);
      this.B(-1551059);
      this.addSettings(this.timeOut, this.watchdog, mode, this.damageBooster, this.iceBooster);
   }

   public double angleBetween(double var1, double var3, double var5, double var7) {
      double var12 = Math.PI * 2;
      double var14 = 180.0 / Math.PI;
      if (var1 == var5 && var3 == var7) {
         return -1.0;
      }

      double var16 = Math.atan2(var5 - var1, var3 - var7);
      var16 += var12 / 2.0;
      var16 = var16 < 0.0 ? var16 + var12 : var16;
      return var14 * var16;
   }

   public double getMovedAngle() {
      return this.angleBetween(this.lastX, this.lastZ, mc.thePlayer.posX, mc.thePlayer.posZ);
   }

   public float getMoveYaw() {
      float var4 = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
      MovementInput var5 = mc.thePlayer.movementInput;
      float var6 = var5.moveStrafe;
      float var7 = var5.moveForward;
      if (var7 != 0.0F) {
         if (var6 < 0.0F) {
            var4 += var7 < 0.0F ? 135.0F : 45.0F;
         } else if (var6 > 0.0F) {
            var4 -= var7 < 0.0F ? 135.0F : 45.0F;
         } else if (var7 < 0.0F) {
            var4 -= 180.0F;
         }
      } else if (var6 < 0.0F) {
         var4 += 90.0F;
      } else if (var6 > 0.0F) {
         var4 -= 90.0F;
      }

      return MathHelper.wrapAngleTo180_float(var4);
   }

   public double getMoveYaw360() {
      double var4 = this.getMoveYaw();

      while (var4 < 0.0) {
         var4 += 360.0;
      }

      return var4;
   }

   public double getMovedDistance() {
      return Math.sqrt(Math.pow(mc.thePlayer.posX - this.lastX, 2.0) + Math.pow(mc.thePlayer.posZ - this.lastZ, 2.0));
   }

   public double[] toVector(double var1, double var3) {
      double var5 = var3 * Math.cos(Math.toRadians(var1 + 90.0));
      double var7 = var3 * Math.sin(Math.toRadians(var1 + 90.0));
      return new double[]{var5, var7};
   }

   public double dot(double[] var1, double[] var2) {
      return var1[0] * var2[0] + var1[1] * var2[1];
   }

   public double[] scale(double[] var1, double var2) {
      return new double[]{var1[0] * var2, var1[1] * var2};
   }

   public double[] divide(double[] var1, double var2) {
      return new double[]{var1[0] / var2, var1[1] / var2};
   }

   public double[] projectOnto(double[] var1, double[] var2) {
      return this.scale(var2, this.dot(var1, var2) / this.dot(var2, var2));
   }

   public double length(double[] var1) {
      return Math.sqrt(Math.pow(var1[0], 2.0) + Math.pow(var1[1], 2.0));
   }

   public double[] subtract(double[] var1, double[] var2) {
      return new double[]{var1[0] - var2[0], var1[1] - var2[1]};
   }

   public double[] add(double[] var1, double[] var2) {
      return new double[]{var1[0] + var2[0], var1[1] + var2[1]};
   }

   public double[] normalize(double[] var1) {
      return this.divide(var1, this.length(var1));
   }

   public double[] withLength(double[] var1, double var2) {
      return this.scale(this.normalize(var1), var2);
   }

   public void strafeTowards(MoveEvent var1, double var2, double var4) {
      double[] var9 = this.toVector(this.getMovedAngle(), this.getMovedDistance());
      double[] var10 = this.toVector(this.getMoveYaw360(), var2);
      double[] var11;
      if (this.length(this.subtract(var9, var10)) > var4) {
         var11 = this.add(var9, this.toVector(this.getMoveYaw360(), var4));
      } else {
         var11 = var10;
      }

      if (this.length(var11) > var2) {
         var11 = this.withLength(var11, var2);
      }

      var1.setX(mc.thePlayer.motionX = var11[0]);
      var1.setZ(mc.thePlayer.motionZ = var11[1]);
   }

   public static void setMotion(MoveEvent var0, float var1, float var2, float var3, double var4) {
      setMotion(var0, var1, var2, var3, var4, 1.0);
   }

   public static void setMotion(MoveEvent var0, float var1, float var2, float var3, double var4, double var6) {
      float var11 = var1 * var1 + var2 * var2;
      if (var11 >= 1.0E-4F) {
         var11 = MathHelper.sqrt_float(var11);
         if (var11 < 1.0F) {
            var11 = 1.0F;
         }

         var11 = var3 / var11;
         var1 *= var11;
         var2 *= var11;
         float var12 = MathHelper.sin(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
         float var13 = MathHelper.cos(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
         double var14 = var1 * var4 * var13 - var2 * var4 * var12;
         double var16 = var2 * var4 * var13 + var1 * var4 * var12;
         var0.setX(var14 * var6);
         var0.setZ(var16 * var6);
      }
   }

   public double[] unusedBoostValues() {
      double var4 = getBaseSpeed(1.0, true);
      if (var4 >= 0.28 && var4 < 0.3) {
         return new double[]{1.36, this.speedOffset, 1.6, 1.85};
      } else if (var4 >= 0.3 && var4 < 0.4) {
         return new double[]{1.249, 70.0 + this.speedOffset, 1.1, 1.45};
      } else {
         return var4 >= 0.4 ? new double[]{1.252, 75.0 + this.speedOffset, 1.25, 1.4} : new double[]{1.0, 1.0, 1.0, 1.0};
      }
   }

   public double unusedSpeedSteps(double var1) {
      double var3 = getBaseSpeed(1.0, true);
      return Math.floor(var3 * 30.0 * var1);
   }

   public double unusedRatio(double var1, double var3) {
      double var5 = getBaseSpeed(1.0, true);
      double var7 = var5 * 10.0 * 2.0;
      double var9 = this.speedOffset * var5 * 1.5;
      return var1 / (var7 + var9 + var3);
   }

   public double unusedRatio2(double var1, double var3, double var5) {
      double var7 = getBaseSpeed(1.0, true);
      double var9 = var7 * 10.0 * 2.0;
      double var11 = this.speedOffset / var3 * var7 * 1.5;
      return var1 / ((var9 + var11) * (var7 * (var5 * 1.4)));
   }

   public static double getNcpBaseSpeed() {
      double var3 = 0.2873;
      if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
         int var5 = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
         var3 *= 1.0 + 0.2 * (var5 + 1);
      }

      return var3;
   }

   public static int getSpeedAmplifier() {
      return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
   }

   public static void setSpeed(double var0) {
      double var5 = mc.thePlayer.movementInput.moveForward;
      double var7 = mc.thePlayer.movementInput.moveStrafe;
      float var9 = mc.thePlayer.rotationYaw;
      if (var5 == 0.0 && var7 == 0.0) {
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
      } else {
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

         mc.thePlayer.motionX = var5 * var0 * Math.cos(Math.toRadians(var9 + 90.0F)) + var7 * var0 * Math.sin(Math.toRadians(var9 + 90.0F));
         mc.thePlayer.motionZ = var5 * var0 * Math.sin(Math.toRadians(var9 + 90.0F)) - var7 * var0 * Math.cos(Math.toRadians(var9 + 90.0F));
      }
   }

   public double freeHeightUpTo(double var1) {
      for (double var6 = 0.0; var6 < var1; var6 += 0.01) {
         for (int var11 : new int[]{-2, -1, 0, 1, 2}) {
            if (mc.theWorld
                  .getCollidingBoundingBoxes(
                     mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(mc.thePlayer.motionX * var11, var6, mc.thePlayer.motionZ * var11)
                  )
                  .size()
               > 0) {
               return var6 - 0.01;
            }
         }
      }

      return var1;
   }

   public static void moveForward(double var0) {
      double var2 = Math.toRadians(mc.thePlayer.rotationYaw);
      mc.thePlayer.setPosition(mc.thePlayer.posX + -Math.sin(var2) * var0, mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(var2) * var0);
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
      for (double var4 = 0.0; var4 < 0.4; var4 += 0.01) {
         for (int var9 : new int[]{-2, -1, 0, 1, 2}) {
            double var10 = mc.thePlayer.motionX;
            double var12 = mc.thePlayer.motionZ;
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(var10 * var9, var4, var12 * var9)).size() > 0) {
               return var4 - 0.01;
            }
         }
      }

      return 0.4;
   }

   public static double getJumpBoostMultiplier() {
      if (mc.thePlayer.isPotionActive(Potion.jump)) {
         int var3 = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
         return 1.0 + 0.2 * (var3 + 1);
      } else {
         return 1.0;
      }
   }

   public static double getBaseSpeed(double var0, boolean var2) {
      if (mc.thePlayer == null) {
         return 0.0;
      }

      PotionEffect var6 = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed);
      BlockPos var7 = new BlockPos(
         MathHelper.floor_double(mc.thePlayer.posX),
         MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1,
         MathHelper.floor_double(mc.thePlayer.posZ)
      );
      double var8 = 0.221;
      if (mc.thePlayer.isInWeb) {
         var8 *= 0.4751131221719457;
      }

      if (mc.thePlayer.isInWater()) {
         var8 *= 0.5203619909502263;
      }

      if (mc.thePlayer.isSneaking()) {
         var8 *= 0.5882352941176471;
      }

      if (mc.thePlayer.isSprinting()) {
         var8 *= 1.3;
      }

      if (var6 != null && var2) {
         var8 *= 1.0 + 0.2 * (var6.getAmplifier() + 1);
      }

      if (mc.theWorld.getBlockState(var7).getBlock() instanceof BlockIce || mc.theWorld.getBlockState(var7).getBlock() instanceof BlockPackedIce) {
         var8 *= var0;
      }

      return var8;
   }

   public static double getBaseSpeed() {
      return getBaseSpeed(2.5, true);
   }
}
