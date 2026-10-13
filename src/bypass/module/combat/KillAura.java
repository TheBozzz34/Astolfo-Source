package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.game.TickEvent;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.DeathEvent;
import bypass.event.player.LookEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveWithHeadingEvent;
import bypass.event.player.StepEvent;
import bypass.event.player.StepState;
import bypass.event.player.TeleportEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderLivingEvent;
import bypass.gui.hud.TargetHud;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.RiderGod;
import bypass.module.hidden.BotDetector;
import bypass.module.hidden.HypixelGameTracker;
import bypass.module.hidden.ScoreboardTracker;
import bypass.module.movement.Flight;
import bypass.module.other.AutoHypixel;
import bypass.module.player.Scaffold;
import bypass.module.player.ScaffoldRotationMode;
import bypass.render.ClientColor;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.user.AstolfoUser;
import bypass.user.AstolfoUserStatus;
import bypass.util.GameUtil;
import bypass.util.InventoryUtil;
import bypass.util.MathUtil;
import bypass.util.PathFinder;
import bypass.util.RandomUtils;
import bypass.util.Rotation;
import bypass.util.Rotator;
import bypass.util.Stopwatch;
import bypass.util.Vector3;
import bypass.via.ViaFabric;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.WorldSettings;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class KillAura extends Module {
   private final Rotator rotator = new Rotator(false, 15.0F);
   private final NumberSetting<Integer> ticksExisted = new NumberSetting<>("Ticks-Existed", "Existed ticks of an entity", 15, 0, 150);
   private final NumberSetting<Integer> switchDelay = new NumberSetting<>("Switch-Delay", "Hits between each entity switch", 250, 1, 3000, "ms");
   private final NumberSetting<Integer> maxTargets = new NumberSetting<>("Max-Targets", "Limits aura target list size", 15, 1, 50);
   private final BooleanSetting rayCast = new BooleanSetting("Ray-Cast", "Ray cast", false);
   private final EnumSetting<KillAuraSprintMode> sprint = new EnumSetting<>("Sprint", "Sprint mode", KillAuraSprintMode.ALWAYS);
   private final BooleanSetting particles = new BooleanSetting("Particles", "Shows particles clientsided", true);
   private final BooleanSetting movementLimit = new BooleanSetting("Movement-Limit", "Limits ur movement to the server yaw", false);
   private final BooleanSetting autoDisable = new BooleanSetting("Auto-Disable", "Auto disables KillAura when you die", true);
   private final BooleanSetting inventoryDisable = new BooleanSetting("Inventory-Disable", "Disables KillAura in inventory", false);
   private final NumberSetting<Double> range = new NumberSetting<>("Range", "Distance of blocks", 4.0, 3.0, 6.0);
   private final NumberSetting<Double> wallRange = new NumberSetting<>("Wall-Range", "Distance of blocks killaura will reach through blocks", 3.2, 0.0, 6.0);
   private final NumberSetting<Double> blockRange = new NumberSetting<>("Block-Range", "Distance of blocks til blockin", 7.0, 0.0, 16.0);
   public static final EnumSetting<KillAuraMode> mode = new EnumSetting<>("Mode", "Set your aura mode", KillAuraMode.SWITCH);
   private final EnumSetting<KillAuraAttackMode> attackMode = new EnumSetting<>("Attack-Mode", "Attack mode", KillAuraAttackMode.POST);
   public static final EnumSetting<KillAuraAutoBlockMode> autoBlock = new EnumSetting<>("Auto-Block", "Set your autoblock mode", KillAuraAutoBlockMode.BLOCK);
   private final EnumSetting<KillAuraSwingMode> swing = new EnumSetting<>("Swing", "Swing mode", KillAuraSwingMode.CLIENT);
   private final EnumSetting<KillAuraSortingMode> sorting = new EnumSetting<>("Sorting", "Set your sort modes", KillAuraSortingMode.DISTANCE);
   private final MapSetting entities = new MapSettingBuilder("Entities")
      .ordered()
      .description("killaura entiti")
      .add("Players", new BooleanSetting("Players", "Add players to Entities", true))
      .add("Hostiles", new BooleanSetting("Hostiles", "Add monsters to Entities", false))
      .add("Passives", new BooleanSetting("Passives", "Add passives to Entities", false))
      .add("Neutrals", new BooleanSetting("Neutrals", "Add neutrals to Entities", false))
      .add("Bosses", new BooleanSetting("Bosses", "Add bosses to Entities", false))
      .add("Invisibles", new BooleanSetting("Invisibles", "Add invisibles to Entities", false))
      .add("Friends", new BooleanSetting("Friends", "Add friends to Entities", false))
      .add("Teams", new BooleanSetting("Teams", "Add teams to Entities", false))
      .add("Astolfo-Users", new BooleanSetting("Astolfo-Users", "Hits astolfo users", true))
      .build();
   private final MapSetting delay = new MapSettingBuilder("Delay")
      .ordered()
      .description("Delay stuff")
      .add("Min-APS", new NumberSetting<>("Min-APS", "Attacks per second", 9, 1, 20))
      .add("Max-APS", new NumberSetting<>("Max-APS", "Attacks per second", 13, 1, 20))
      .add("Miss-Chance", new NumberSetting<>("Miss-Chance", "Chance percentage to hit the entity", 0.0F, 0.0F, 100.0F, "%"))
      .build();
   private final MapSetting optimize = new MapSettingBuilder("Optimize")
      .ordered()
      .description("stuff")
      .add("Rotations", new BooleanSetting("Rotations", "Optimize rotations", false))
      .add("Auto-Block", new BooleanSetting("Auto-Block", "Optimizes auto-block to maximize blocking when standing", false))
      .build();
   private final MapSetting strafe = new MapSettingBuilder("Strafe")
      .ordered()
      .description("Strafe stuff")
      .add("Enable", new BooleanSetting("Enable", "Enable strafing", false))
      .add("Range", new NumberSetting<>("Range", "Range agaisnt entity", 1.0, 0.1, 6.0))
      .add("Points", new NumberSetting<>("Points", "Circle points", 40, 3, 30))
      .add("Mode", new EnumSetting<>("Mode", "Enable modes", KillAuraStrafeMode.ON_SPACE))
      .add("Form", new EnumSetting<>("Form", "Type of form", KillAuraStrafeFormMode.CIRCLE))
      .add("Color", new ColorSetting("Color", "color", Color.WHITE))
      .build();
   private final MapSetting render = new MapSettingBuilder("Render")
      .ordered()
      .description("Render stuff")
      .add("HUD", new BooleanSetting("HUD", "should enable target hud", false))
      .add("Visualization", new BooleanSetting("Visualization", "circle that shows reach", false))
      .add("Strafe", new EnumSetting<>("Strafe", "circle that shows strafe", KillAuraRenderStrafeMode.TRUE))
      .add("Pos-X", new NumberSetting<>("Pos-X", "Pos", 150.0F, 1.0F, 50000.0F))
      .add("Pos-Y", new NumberSetting<>("Pos-Y", "Pos", 150.0F, 1.0F, 50000.0F))
      .build();
   private final MapSetting rotations = new MapSettingBuilder("Rotations")
      .ordered()
      .description("rotations stuff")
      .add("FOV", new NumberSetting<>("FOV", "field of view", 360, 0, 360, "º"))
      .add("Enable", new BooleanSetting("Enable", "Should rotate", true))
      .add("Lock-View", new BooleanSetting("Lock-View", "Locks your view", false))
      .add("Hitbox-Check", new BooleanSetting("Hitbox-Check", "Checks if aim inside of entity hitbox", false))
      .add("Smoothing", new NumberSetting<>("Smoothing", "Chance percentage to hit the entity", 20, 0, 100, "%"))
      .add("Lead-Off", new NumberSetting<>("Lead-Off", "Amount of prediction", 0.0F, 0.0F, 10.0F))
      .add("Random", new NumberSetting<>("Random", "randomizes rotations", 5, 0, 50))
      .add("Fix-Delta", new BooleanSetting("Fix-Delta", "Uses mouse delta", true))
      .build();
   public static Entity target;
   private int targetIndex;
   public static int criticalStage;
   private int stepCooldown;
   private boolean incrementalCritReady;
   private boolean movementRotated;
   private boolean blocking;
   public static float serverYaw;
   public static float lastYaw;
   public static float serverPitch;
   public static float lastPitch;
   private ArrayList<EntityLivingBase> targets = new ArrayList<>();
   private final Stopwatch attackTimer = new Stopwatch();
   private final Stopwatch switchTimer = new Stopwatch();
   private final Stopwatch teleportTimer = new Stopwatch();
   private long attackDelay = 0L;
   private TargetHud targetHud;
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = var1x -> this.p();
   private float savedYaw;
   private float savedPitch;
   private float strafeYaw;
   private final List<Vector3d> strafePoints = new ArrayList<>();
   private int strafePointIndex;
   public static final Rotator strafeRotator = new Rotator(false, 0.0F);
   private int unused;
   private boolean strafeReversed;
   @EventHandler(LookEvent.class)
   private final Listener<LookEvent> onLook = var1x -> {
      if (this.rayCast.getValue() && mc.thePlayer != null && target != null) {
         var1x.setVec(mc.thePlayer.getVectorForRotation(serverPitch, serverYaw));
      }
   };
   @EventHandler(TickEvent.class)
   private final Listener<TickEvent> onTick = var1x -> {
      if (var1x.getState() == EventState.PRE && mc.thePlayer != null && mc.theWorld != null) {
         if (!this.rayCast.getValue()) {
            return;
         }

         if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            target = mc.objectMouseOver.entityHit;
            if (target instanceof EntityLivingBase
               && this.canReach(mc.thePlayer, target)
               && this.isValidTarget((EntityLivingBase)target)
               && this.attackTimer.hasElapsed((float)this.attackDelay)) {
               int var5 = this.getMinAps();
               int var6 = this.getMaxAps();
               double var7 = var6 - var5;
               double var9 = 1.0 / (var5 + Math.random() * var7);
               if (var7 == 0.0) {
                  this.attackDelay = 1000 / var5;
               } else {
                  this.attackDelay = (int)Math.floor(var9 * 1000.0);
               }

               mc.clickMouse();
               this.attackTimer.reset();
            }
         }
      }
   };
   private boolean strafeYawSaved;
   private boolean wasMoving;
   @EventHandler(MoveWithHeadingEvent.class)
   private Listener<MoveWithHeadingEvent> onMoveWithHeading = var1x -> {
      long var2 = cb ^ 34002809022621L;
      long var4 = var2 ^ 48967030782639L;
      if (this.movementLimit.getValue() && target != null) {
         switch (var1x.getState()) {
            case PRE:
               this.savedYaw = mc.thePlayer.rotationYaw;
               this.savedPitch = mc.thePlayer.rotationPitch;
               mc.thePlayer.rotationYaw = serverYaw;
               mc.thePlayer.rotationPitch = serverPitch;
               this.movementRotated = true;
               break;
            case POST:
               mc.thePlayer.rotationYaw = this.savedYaw;
               mc.thePlayer.rotationPitch = this.savedPitch;
         }
      } else {
         if (this.strafe.getSetting("Enable").asBoolean()
            && target != null
            && !(mc.currentScreen instanceof GuiChat)
            && !client.getModuleManager().get(Scaffold.class).isEnabled()
            && mc.thePlayer != null
            && mc.theWorld != null
            && !mc.thePlayer.isInWater()
            && !mc.thePlayer.isOnLadder()) {
            KillAuraStrafeMode var7 = (KillAuraStrafeMode)this.strafe.getSetting("Mode").getValue();
            if ((
                  var7 != KillAuraStrafeMode.ON_SPACE
                     || !Keyboard.isKeyDown(57)
                     || !mc.gameSettings.keyBindForward.pressed
                     || mc.gameSettings.keyBindBack.pressed
                     || mc.gameSettings.keyBindRight.pressed
                     || mc.gameSettings.keyBindLeft.pressed
               )
               && var7 != KillAuraStrafeMode.ALWAYS) {
               if (this.strafeYawSaved) {
                  mc.thePlayer.rotationYaw = this.savedYaw;
                  mc.thePlayer.rotationPitch = this.savedPitch;
                  this.strafeYawSaved = false;
               }

               strafing = false;
            } else {
               switch (var1x.getState()) {
                  case PRE:
                     this.strafePoints.clear();
                     int var8 = this.strafe.getSetting("Points").asInt();
                     double var9 = this.strafe.getSetting("Range").asDouble();
                     if (this.optimize.getSetting("Rotations").asBoolean() && var9 > 1.15) {
                        var9 = 0.8;
                     }

                     KillAuraStrafeFormMode var11 = (KillAuraStrafeFormMode)this.strafe.getSetting("Form").getValue();
                     this.traceStrafeForm(target.posX, target.posY, target.posZ, var11, var9, var8, this.strafePoints);
                     Vector3d var12 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                     switch (var11) {
                        case CIRCLE:
                           if (this.strafeReversed) {
                              this.strafePointIndex = this.closestPoint(this.strafePoints) - 1;
                           } else {
                              this.strafePointIndex = this.closestPoint(this.strafePoints) + 1;
                           }
                           break;
                        case INFINITE:
                           if (this.strafePointIndex == 0) {
                              this.strafePointIndex = this.closestPoint(this.strafePoints) + 1;
                           } else {
                              this.strafePointIndex++;
                           }
                     }

                     if (this.strafePointIndex >= this.strafePoints.size()) {
                        this.strafePointIndex = 0;
                     }

                     if (this.strafePointIndex == -1) {
                        if (this.strafeReversed) {
                           this.strafePointIndex = this.strafePoints.size() - 1;
                        } else {
                           this.strafePointIndex = this.closestPoint(this.strafePoints);
                        }
                     }

                     Vector3d var13 = this.strafePoints.get(this.strafePointIndex);
                     Vector3d var14 = this.strafePoints.get(this.closestPoint(this.strafePoints));
                     boolean var15 = false;
                     if (client.getModuleManager().get(Flight.class).isEnabled()) {
                        var15 = true;
                     } else {
                        for (int var16 = 0; var16 < 10; var16++) {
                           double var17 = var13.y - 1.0 - var16;
                           if (!(mc.theWorld.getBlockState(new BlockPos(var13.x, var17, var13.z)).getBlock() instanceof BlockAir)) {
                              var15 = true;
                           }
                        }
                     }

                     if (!(mc.theWorld.getBlockState(new BlockPos(var13.x, Math.floor(var13.y + 2.0), var13.z)).getBlock() instanceof BlockAir)
                        || !(mc.theWorld.getBlockState(new BlockPos(var14.x, Math.floor(var14.y + 1.5), var14.z)).getBlock() instanceof BlockAir)) {
                        this.strafeReversed = !this.strafeReversed;
                     }

                     if (var15 && mc.thePlayer.getDistance(var13.x, var13.y, var13.z) < 0.3) {
                        this.strafePointIndex = this.pointNearStrafeYaw(this.strafePoints, this.strafePointIndex) + 1;
                     }

                     if (var13 != null) {
                        Rotation var19 = strafeRotator.rotationTo(var13, var12);
                        this.savedYaw = mc.thePlayer.rotationYaw;
                        this.savedPitch = mc.thePlayer.rotationPitch;
                        mc.thePlayer.rotationYaw = var19.getYaw();
                        this.strafeYaw = mc.thePlayer.rotationYaw;
                        mc.thePlayer.rotationPitch = 0.0F;
                        if (!var15) {
                           mc.thePlayer.rotationYaw -= 180.0F;
                        }

                        var1x.setForward(1.0F);
                        var1x.setStrafe(0.0F);
                        strafing = this.strafeYawSaved = true;
                     }
                     break;
                  case POST:
                     mc.thePlayer.rotationYaw = this.savedYaw;
                     mc.thePlayer.rotationPitch = this.savedPitch;
                     this.strafeYawSaved = false;
               }
            }
         } else {
            if (this.strafeYawSaved && mc.thePlayer != null) {
               mc.thePlayer.rotationYaw = this.savedYaw;
               mc.thePlayer.rotationPitch = this.savedPitch;
            }

            strafing = this.strafeYawSaved = false;
            this.strafePointIndex = -1;
         }
      }
   };
   @EventHandler(TeleportEvent.class)
   private final Listener<TeleportEvent> onTeleport = var1x -> {
      if (!var1x.y()) {
         if (this.autoDisable.getValue()) {
            var1x.i(this);
         }
      } else {
         this.teleportTimer.reset();
      }
   };
   @EventHandler(DeathEvent.class)
   private final Listener<DeathEvent> onDeath = var1x -> {
      if (this.autoDisable.getValue()) {
         this.setEnabled(false);
      }
   };
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (var1x.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging var5 = (C07PacketPlayerDigging)var1x.getPacket();
            if (var5.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && GameUtil.isHoldingSword(mc.thePlayer)) {
               this.blocking = false;
            }
         }

         if (var1x.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement var6 = (C08PacketPlayerBlockPlacement)var1x.getPacket();
            if (var6.getPlacedBlockDirection() == 255 && GameUtil.isHoldingSword(mc.thePlayer)) {
               if (target != null
                  && (
                     autoBlock.getValue() == KillAuraAutoBlockMode.INTERACT
                        || (autoBlock.getValue() == KillAuraAutoBlockMode.WATCHDOG || autoBlock.getValue() == KillAuraAutoBlockMode.WATCHDOG2)
                           && this.shouldOptimizeBlock()
                  )) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
               }

               this.blocking = true;
            }
         }

         if (var1x.getPacket() instanceof C09PacketHeldItemChange) {
            this.blocking = false;
         }
      }

      if (var1x.getDirection() == PacketDirection.RECEIVED) {
         if (var1x.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook var7 = (S08PacketPlayerPosLook)var1x.getPacket();
            serverYaw = var7.getYaw();
            serverPitch = var7.getPitch();
         }

         if (var1x.getPacket() instanceof S30PacketWindowItems
            && (autoBlock.getValue() == KillAuraAutoBlockMode.WATCHDOG || autoBlock.getValue() == KillAuraAutoBlockMode.WATCHDOG2)) {
            this.blocking = false;
         }
      }
   };
   public static boolean strafing;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGH)
   private Listener<MotionEvent> onMotion = var1x -> {
      long var2 = cb ^ 88967226523239L;
      long var10001 = var2 ^ 40368445748411L;
      int var4 = (int)((var2 ^ 40368445748411L) >>> 56);
      int var5 = (int)((var2 ^ 40368445748411L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      long var7 = var2 ^ 108369151863893L;
      boolean var10 = this.isLockView();
      int var11 = this.getSmoothing();
      float var12 = this.getLeadOff();
      if (AutoHeal.healItem != null && AutoHeal.healItem.getType() == HealItemType.POTION) {
         ItemRenderer.J = false;
         this.blocking = false;
      } else if (mc.currentScreen instanceof GuiInventory && this.inventoryDisable.getValue()) {
         target = null;
         this.stopBlocking();
      } else if (client.getModuleManager().get(Scaffold.class).isEnabled()
         && (Scaffold.rotation.getValue() != ScaffoldRotationMode.EYE_ANGLE || Scaffold.ncpHypixel.getValue())
         && GameUtil.isOnHypixel()) {
         target = null;
         this.stopBlocking();
      } else {
         EntityPlayer var13 = this.findBlockTarget();
         switch (var1x.getState()) {
            case PRE:
               this.targets = this.findTargets();
               target = null;
               this.stopBlocking();
               if (this.wasMoving != GameUtil.isMoving(mc.thePlayer)
                  && GameUtil.isHoldingSword(mc.thePlayer)
                  && GameUtil.isOnHypixel()
                  && this.optimize.getSetting("Auto-Block").asBoolean()
                  && this.blocking) {
                  mc.thePlayer
                     .sendQueue
                     .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
               }

               if (!this.targets.isEmpty()) {
                  if (this.targetIndex >= this.targets.size()) {
                     this.targetIndex = 0;
                  }

                  this.getAdequatedSort();

                  for (EntityLivingBase var15 : this.targets) {
                     if (var15 instanceof EntityWither && ScoreboardTracker.r()) {
                        target = var15;
                        break;
                     }

                     if (var15 instanceof EntityPlayer) {
                        if (client.getRelationManager().getList().isEmpty() && AutoHypixel.bountiedPlayers.isEmpty()) {
                           break;
                        }

                        if (client.getRelationManager().D(var15) || AutoHypixel.bountiedPlayers.contains(var15)) {
                           target = var15;
                           break;
                        }
                     }
                  }

                  if (target == null) {
                     target = this.targets.get(mode.getValue().equals(KillAuraMode.SWITCH) ? this.targetIndex : 0);
                  }

                  if (target != null) {
                     if (this.sprint.getValue() == KillAuraSprintMode.NEVER) {
                        this.stopSprinting();
                     }

                     if (this.isRotationEnabled()) {
                        Vector3d var31 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                        double var32 = this.rotations.getSetting("Random").asFloat();
                        Vector3d var17 = new Vector3d(
                           target.posX + RandomUtils.nextDouble(-var32, var32) / 100.0,
                           target.posY + RandomUtils.nextDouble(-var32, var32) / 100.0 + target.height * 0.5 / 1.0,
                           target.posZ + RandomUtils.nextDouble(-var32, var32) / 100.0
                        );
                        if (var12 > 0.0F) {
                           Vector3d var18 = new Vector3d(target.posX - target.lastTickPosX, 0.0, target.posZ - target.lastTickPosZ);
                           double var19 = this.eyeDistance(mc.thePlayer, target);
                           var17 = this.rotator.predict(var17, var18, target.isSprinting(), var12, var19);
                        }

                        this.rotator.setRandomize(false);
                        this.rotator.setSmoothing(100 - var11);
                        Rotation var33 = this.rotator.rotationTo(var17, var31);
                        Rotation var34 = this.rotator.smooth(var33, new Rotation(var1x.getLastYaw(), var1x.getLastPitch()));
                        float var20 = var11 == 0 ? var33.getYaw() : var34.getYaw();
                        float var21 = var11 == 0 ? var33.getPitch() : var34.getPitch();
                        float var22 = MathHelper.wrapAngleTo180_float(var20 - mc.thePlayer.rotationYaw);
                        var20 = mc.thePlayer.rotationYaw + var22;
                        lastYaw = var1x.getLastYaw();
                        lastPitch = var1x.getLastPitch();
                        if (this.movementRotated || !this.movementLimit.getValue()) {
                           if (this.rotations.getSetting("Fix-Delta").asBoolean()) {
                              float var23 = mc.gameSettings.Zm * 0.6F + 0.2F;
                              float var24 = var23 * var23 * var23 * 1.2F;
                              float var25 = var20 - serverYaw;
                              float var26 = var21 - serverPitch;
                              float var27 = var25 - var25 % var24;
                              float var28 = var26 - var26 % var24;
                              float var29 = serverYaw + var27;
                              float var30 = serverPitch + var28;
                              var20 = var29;
                              var21 = var30;
                           }

                           if (this.movementLimit.getValue()) {
                              var1x.setYaw(serverYaw);
                              var1x.setPitch(serverPitch);
                              serverYaw = var20;
                              serverPitch = var21;
                           } else {
                              if (this.optimize.getSetting("Rotations").asBoolean() && strafing && !target.onGround) {
                                 var1x.setYaw(var1x.getLastYaw());
                              } else {
                                 var1x.setYaw(var10 ? (mc.thePlayer.rotationYaw = var20) : var20);
                              }

                              var1x.setPitch(var10 ? (mc.thePlayer.rotationPitch = var21) : var21);
                              serverYaw = var1x.getYaw();
                              serverPitch = var1x.getPitch();
                           }

                           this.movementRotated = false;
                        }
                     }

                     if (client.getModuleManager().get(Criticals.class).isEnabled() && GameUtil.N() && this.stepCooldown < 3 && !mc.thePlayer.Lr && Criticals.j
                        )
                      {
                        switch ((CriticalsMode)Criticals.mode.getValue()) {
                           case EDIT:
                              this.editCriticals(var1x);
                              break;
                           case INCREMENTAL:
                              this.incrementalCriticals(var1x);
                              break;
                           case WATCHDOG:
                              this.watchdogCriticals(var1x);
                        }
                     }

                     if (this.attackMode.getValue().equals(KillAuraAttackMode.PRE) || this.attackMode.getValue().equals(KillAuraAttackMode.MIX)) {
                        this.attackTargets();
                     }
                  }
               }

               this.stepCooldown--;
               this.wasMoving = GameUtil.isMoving(mc.thePlayer);
               break;
            case POST:
               if (target == null && var13 == null) {
                  this.stopBlocking();
               } else {
                  if ((this.attackMode.getValue().equals(KillAuraAttackMode.POST) || this.attackMode.getValue().equals(KillAuraAttackMode.MIX))
                     && target != null) {
                     this.attackTargets();
                  }

                  if (!GameUtil.isHoldingSword(mc.thePlayer)
                     || autoBlock.getValue().equals(KillAuraAutoBlockMode.NONE)
                     || (!(this.blockRange.asDouble() > 0.0) || var13 == null) && target == null) {
                     ItemRenderer.J = false;
                  } else {
                     this.startBlocking();
                     ItemRenderer.J = true;
                  }
               }
         }
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = var1x -> {
      if (target != null && var1x.getEntity() == mc.thePlayer && this.isRotationEnabled()) {
      }
   };
   @EventHandler(StepEvent.class)
   private Listener<StepEvent> onStep = var1x -> {
      if (var1x.getEntity() == mc.thePlayer && var1x.getState() == StepState.PRE && var1x.getVec().yCoord >= 0.2) {
         this.stepCooldown = 4;
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = var1x -> {
      long var2 = cb ^ 72262859920156L;
      long var4 = var2 ^ 127392508756270L;

      try {
         switch (var1x.getType()) {
            case SCREEN:
               if (this.targetHud != null && this.isHudEnabled()) {
                  NumberSetting var24 = (NumberSetting)this.render.getSetting("Pos-X");
                  NumberSetting var8 = (NumberSetting)this.render.getSetting("Pos-Y");
                  if (Mouse.isButtonDown(0)) {
                     var24.setValue(this.targetHud.M());
                     var8.setValue(this.targetHud.E_float());
                  } else if ((Float)var24.getValue() != this.targetHud.M() || (Float)var8.getValue() != this.targetHud.E_float()) {
                     if (this.targetHud.M() != (Float)var24.getValue()) {
                        this.targetHud.o((Float)var24.getValue());
                     }

                     if (this.targetHud.E_float() != (Float)var8.getValue()) {
                        this.targetHud.q((Float)var8.getValue());
                     }
                  }

                  if ((Float)var24.getValue() > var1x.getResolution().getScaledWidth()) {
                     var24.setValue(1.0F);
                  }

                  if ((Float)var8.getValue() > var1x.getResolution().getScaledHeight()) {
                     var8.setValue(var1x.getResolution().getScaledHeight() / 1.5F);
                  }

                  int var25 = var1x.getResolution().getScaledHeight();
                  int var10 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
                  int var26 = var25 - Mouse.getY() * var25 / mc.displayHeight - 1;
                  if (target instanceof EntityLivingBase) {
                     this.targetHud.z((EntityLivingBase)target);
                     this.targetHud.y(var1x.getResolution().getScaledWidth() / 2.0F);
                     this.targetHud.render(var10, var26);
                  }

                  if (mc.currentScreen instanceof GuiChat) {
                     if (target == null && mc.thePlayer != null) {
                        this.targetHud.z(mc.thePlayer);
                        this.targetHud.y(var1x.getResolution().getScaledWidth() / 2.0F);
                        this.targetHud.render(var10, var26);
                     }

                     if (this.targetHud.u_boolean() && Mouse.isButtonDown(0)) {
                        this.targetHud.T(var10, var26);
                     }
                  }

                  if (Mouse.isButtonDown(0) && this.targetHud.G(var10, var26)) {
                     this.targetHud.F(true, var10, var26);
                  } else {
                     this.targetHud.x(false);
                  }
               }
               break;
            case WORLD:
               double var7 = MathUtil.interpolate(mc.thePlayer.lastTickPosX, mc.thePlayer.posX, var1x.getPartialTicks(), mc.getRenderManager().viewerPosX);
               double var9 = MathUtil.interpolate(mc.thePlayer.lastTickPosY, mc.thePlayer.posY, var1x.getPartialTicks(), mc.getRenderManager().viewerPosY);
               double var11 = MathUtil.interpolate(mc.thePlayer.lastTickPosZ, mc.thePlayer.posZ, var1x.getPartialTicks(), mc.getRenderManager().viewerPosZ);
               long var13 = System.currentTimeMillis();
               double var15 = var13 % 7000L / 7000.0 * 360.0;
               this.drawPentagram(var7, var9, var11, var15);
               if (this.isVisualizationEnabled()) {
                  GL11.glPushMatrix();
                  GL11.glDisable(3553);
                  GL11.glDisable(2896);
                  GL11.glEnable(3042);
                  GL11.glBlendFunc(770, 771);
                  GL11.glDisable(2929);
                  GL11.glEnable(2848);
                  GL11.glDepthMask(false);
                  RenderUtil.drawWorldCircle(
                     var7,
                     var9 + 0.1,
                     var11,
                     this.range.asDouble(),
                     2.0F,
                     target == null
                        ? client.getColorSetting().getValue()
                        : (
                           target instanceof EntityLivingBase && ((EntityLivingBase)target).hurtTime > 0
                              ? ClientColor.RED.getColor()
                              : client.getColorSetting().getValue()
                        )
                  );
                  GL11.glDepthMask(true);
                  GL11.glDisable(2848);
                  GL11.glEnable(2929);
                  GL11.glEnable(2896);
                  GL11.glDisable(3042);
                  GL11.glEnable(3553);
                  GL11.glPopMatrix();
                  RenderUtil.setColor(-1);
               }

               KillAuraRenderStrafeMode var17 = (KillAuraRenderStrafeMode)this.render.getSetting("Strafe").getValue();
               if (var17 == KillAuraRenderStrafeMode.FALSE) {
                  return;
               }

               if (this.strafe.getSetting("Enable").asBoolean()
                  && target != null
                  && !client.getModuleManager().get(Scaffold.class).isEnabled()
                  && !mc.thePlayer.isInWater()
                  && !mc.thePlayer.isOnLadder()) {
                  KillAuraStrafeMode var18 = (KillAuraStrafeMode)this.strafe.getSetting("Mode").getValue();
                  if (var18 == KillAuraStrafeMode.ON_SPACE
                        && Keyboard.isKeyDown(57)
                        && mc.gameSettings.keyBindForward.pressed
                        && !mc.gameSettings.keyBindBack.pressed
                        && !mc.gameSettings.keyBindRight.pressed
                        && !mc.gameSettings.keyBindLeft.pressed
                     || var18 == KillAuraStrafeMode.ALWAYS && !mc.thePlayer.onGround
                     || var17 == KillAuraRenderStrafeMode.ALWAYS) {
                     GL11.glPushMatrix();
                     GL11.glDisable(3553);
                     GL11.glDisable(2896);
                     GL11.glEnable(3042);
                     GL11.glBlendFunc(770, 771);
                     GL11.glDisable(2929);
                     GL11.glEnable(2848);
                     GL11.glDepthMask(false);
                     KillAuraStrafeFormMode var19 = (KillAuraStrafeFormMode)this.strafe.getSetting("Form").getValue();
                     GlStateManager.pushMatrix();
                     long var10000 = 1895894368994852235L ^ var2;
                     GL11.glLineWidth(5.0F);
                     GL11.glBegin(2);
                     double var20 = this.strafe.getSetting("Range").asDouble();
                     int var22 = this.strafe.getSetting("Points").asInt();
                     RenderUtil.setColor(Color.BLACK.getRGB());
                     this.traceStrafeForm(var7, var9, var11, var19, var20, var22, null);
                     GL11.glEnd();
                     GlStateManager.popMatrix();
                     GlStateManager.pushMatrix();
                     GL11.glLineWidth(2.0F);
                     GL11.glBegin(2);
                     RenderUtil.setColor(((ColorSetting)this.strafe.getSetting("Color")).getValue().getRGB());
                     this.traceStrafeForm(var7, var9, var11, var19, var20, var22, null);
                     GL11.glEnd();
                     GlStateManager.popMatrix();
                     GL11.glDepthMask(true);
                     GL11.glDisable(2848);
                     GL11.glEnable(2929);
                     GL11.glEnable(2896);
                     GL11.glDisable(3042);
                     GL11.glEnable(3553);
                     GL11.glPopMatrix();
                     RenderUtil.setColor(-1);
                  }
               }
         }
      } catch (Exception var23) {
      }
   };
   private final double critOffset = RandomUtils.nextDouble(0.001, 0.009);
   private final double unusedOffset = RandomUtils.nextDouble(0.01, 0.02);
   private static final long cb = 5575420850049L;

   public KillAura(Category var1) {
      super(var1);
      this.B(-7733248);
      this.addSettings(
         mode,
         this.entities,
         this.delay,
         this.rotations,
         this.optimize,
         this.strafe,
         this.render,
         this.sorting,
         autoBlock,
         this.attackMode,
         this.swing,
         this.range,
         this.wallRange,
         this.blockRange,
         this.rayCast,
         this.movementLimit,
         this.ticksExisted,
         this.sprint,
         this.switchDelay,
         this.autoDisable,
         this.inventoryDisable,
         this.particles,
         this.maxTargets
      );
   }

   private int pointNearStrafeYaw(List<Vector3d> var1, int var2) {
      float var6 = 30.0F;
      int var7 = var2;
      Vector3d var8 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

      for (int var9 = this.strafePointIndex; var9 < var1.size(); var9++) {
         Vector3d var10 = (Vector3d)var1.get(var9);
         Rotation var11 = strafeRotator.rotationTo(var10, var8);
         float var12 = Math.abs(this.strafeYaw - var11.getYaw());
         if (var12 < var6) {
            var7 = var9;
            var6 = var12;
         }
      }

      return var7;
   }

   private int closestPoint(List<Vector3d> var1) {
      double var5 = 999.0;
      int var7 = 0;

      for (int var8 = 0; var8 < var1.size(); var8++) {
         Vector3d var9 = (Vector3d)var1.get(var8);
         double var10 = mc.thePlayer.getDistance(var9.x, var9.y, var9.z);
         if (var10 < var5) {
            var7 = var8;
            var5 = var10;
         }
      }

      return var7;
   }

   public float getSensitivityStep() {
      double var1 = mc.gameSettings.Zm * 0.6 + 0.2;
      double var3 = var1 * var1 * var1 * 8.0;
      return (float)(var3 * 0.15F);
   }

   public void drawPentagram(double var1, double var3, double var5, double var7) {
      long var9 = cb ^ 31929175839367L;
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glDisable(2896);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(2929);
      GL11.glEnable(2848);
      GL11.glDepthMask(false);
      GL11.glLineWidth(2.0F);
      RenderUtil.setColor(-1);
      GL11.glTranslated(var1, var3, var5);
      GL11.glRotated(var7, 0.0, 1.0, 0.0);
      GL11.glRotated(90.0, 1.0, 0.0, 0.0);
      double[] var12 = new double[10];

      for (int var13 = 0; var13 < 5; var13++) {
         double var14 = (Math.PI * 2) * var13 / 5.0;
         double var16 = Math.cos(var14);
         double var18 = Math.sin(var14);
         var12[var13 * 2] = var16;
         var12[var13 * 2 + 1] = var18;
      }

      GL11.glBegin(1);
      GL11.glVertex3d(var12[0], var12[1], 0.0);
      GL11.glVertex3d(var12[4], var12[5], 0.0);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3d(var12[4], var12[5], 0.0);
      GL11.glVertex3d(var12[8], var12[9], 0.0);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3d(var12[8], var12[9], 0.0);
      GL11.glVertex3d(var12[2], var12[3], 0.0);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3d(var12[2], var12[3], 0.0);
      GL11.glVertex3d(var12[6], var12[7], 0.0);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3d(var12[6], var12[7], 0.0);
      GL11.glVertex3d(var12[0], var12[1], 0.0);
      long var10000 = 1895959716705500176L ^ var9;
      GL11.glEnd();
      GL11.glDepthMask(true);
      GL11.glDisable(2848);
      GL11.glEnable(2929);
      GL11.glEnable(2896);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   private void traceStrafeForm(double var1, double var3, double var5, KillAuraStrafeFormMode var7, double var8, int var10, List<Vector3d> var11) {
      switch (var7) {
         case CIRCLE:
            this.circlePoints(var11, var10, var8, var1, var3, var5);
            break;
         case INFINITE:
            this.lemniscatePoints(var11, var10, var1, var3, var5);
            break;
         case BACK:
            if (target instanceof EntityLivingBase) {
               this.behindPoints(var11, var8, var1, var3, var5, (EntityLivingBase)target);
            }
      }
   }

   @Override
   public void onDisable() {
      if (mc.thePlayer != null) {
         this.stopBlocking();
      }

      this.blocking = false;
      target = null;
      this.targets.clear();
      criticalStage = 0;
      this.targetIndex = 0;
      lastYaw = 0.0F;
      serverYaw = 0.0F;
      lastPitch = 0.0F;
      serverPitch = 0.0F;
      strafing = false;
      this.targetHud = null;
      super.onDisable();
   }

   @Override
   public void onEnable() {
      this.targetHud = new TargetHud(this.render.getSetting("Pos-X").asFloat(), this.render.getSetting("Pos-Y").asFloat(), 150, 50);
      super.onEnable();
   }

   @Override
   public void p() {
      this.setDisplayName("Kill Aura " + EnumChatFormatting.GRAY + KillAuraMode.D(mode.getValue()));
      super.p();
   }

   private void editCriticals(MotionEvent var1) {
      if (criticalStage < 2) {
         var1.setOnGround(false);
         criticalStage++;
      } else {
         var1.setMinY(var1.getMinY() + Criticals.offset.getValue().floatValue());
         criticalStage = 0;
      }
   }

   private void watchdogCriticals(MotionEvent var1) {
      switch (criticalStage) {
         case 0:
            var1.setMinY(var1.getMinY() + 0.0325 + Math.random() / 1000.0);
            var1.setOnGround(false);
            break;
         case 1:
            var1.setMinY(var1.getMinY() + 0.012500048F);
            var1.setOnGround(false);
      }

      criticalStage++;
      if (criticalStage >= 2) {
         criticalStage = 0;
      }
   }

   private void incrementalCriticals(MotionEvent var1) {
      switch (criticalStage) {
         case 0:
            var1.setMinY(var1.getMinY() + 0.07 + this.critOffset);
            var1.setOnGround(false);
            this.incrementalCritReady = false;
         case 1:
            var1.setMinY(var1.getMinY() + 1.1100000088E-5);
            var1.setOnGround(false);
            this.incrementalCritReady = true;
            break;
         case 2:
            var1.setOnGround(true);
            this.incrementalCritReady = false;
      }

      criticalStage++;
      if (criticalStage >= 2) {
         criticalStage = 0;
      }
   }

   private void startBlocking() {
      KillAuraAutoBlockMode var4 = this.shouldOptimizeBlock() ? KillAuraAutoBlockMode.INTERACT : autoBlock.getValue();
      switch (var4) {
         case BLOCK:
         case INTERACT:
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
            break;
         case WATCHDOG2:
            if ((this.teleportTimer.hasElapsed(1500.0F) || !GameUtil.isMoving(mc.thePlayer)) && GameUtil.isOnHypixel()) {
               if (mc.thePlayer.swingProgressInt == 1) {
                  mc.thePlayer
                     .sendQueue
                     .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                  this.blocking = false;
               } else if (mc.thePlayer.swingProgressInt == 2) {
                  mc.thePlayer.sendQueue.u(new C08PacketPlayerBlockPlacement(C08PacketPlayerBlockPlacement.field_179726_a, 255, null, 0.0F, 0.0F, 0.0F));
                  this.blocking = true;
               }
            }
            break;
         case WATCHDOG:
         case ALWAYS:
            if (!this.blocking) {
               mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
            }
      }
   }

   private void stopBlocking() {
      if (GameUtil.isHoldingSword(mc.thePlayer)) {
         KillAuraAutoBlockMode var4 = this.shouldOptimizeBlock() ? KillAuraAutoBlockMode.INTERACT : autoBlock.getValue();
         switch (var4) {
            case INTERACT:
               if (this.blocking) {
                  mc.thePlayer
                     .sendQueue
                     .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
               }
         }
      }

      ItemRenderer.J = false;
   }

   private boolean shouldOptimizeBlock() {
      return this.optimize.getSetting("Auto-Block").asBoolean() && !GameUtil.isMoving(mc.thePlayer) && GameUtil.isOnHypixel();
   }

   private ArrayList<EntityLivingBase> findTargets() {
      long var1 = cb ^ 104502063424041L;
      long var10001 = var1 ^ 47648391227637L;
      int var3 = (int)((var1 ^ 47648391227637L) >>> 56);
      int var4 = (int)((var1 ^ 47648391227637L) << 8 >>> 32);
      int var5 = (int)(var10001 << 40 >>> 40);
      long var6 = var1 ^ 66263561080146L;
      ArrayList var9 = new ArrayList(this.maxTargets.getValue());
      List var10 = mc.theWorld.loadedEntityList;
      int var11 = 0;

      for (int var12 = var10.size(); var11 < var12; var11++) {
         Entity var13 = (Entity)var10.get(var11);
         if (var13 instanceof EntityLivingBase) {
            EntityLivingBase var14 = (EntityLivingBase)var13;
            if (var9.size() > this.maxTargets.getValue()) {
               break;
            }

            if (!BotDetector.w.contains(var14) && (!client.getRelationManager().z(var13) || this.entities.getSetting("Friends").asBoolean())) {
               AstolfoUser var15 = client.getUserManager().g(var13.getName());
               if ((var15 == null || var15.K != AstolfoUserStatus.C) && this.isValidTarget(var14)) {
                  var9.add(var14);
               }
            }
         }
      }

      return var9;
   }

   private void circlePoints(List<Vector3d> var1, int var2, double var3, double var5, double var7, double var9) {
      int var14 = 0;

      while (var14 < 360) {
         double var15 = var14 * Math.PI / 180.0;
         double var17 = var3 * Math.cos(var15);
         double var19 = var3 * Math.sin(var15);
         if (var1 == null) {
            GL11.glVertex3d(var5 + var17, var7, var9 + var19);
         } else {
            var1.add(new Vector3d(var5 + var17, var7, var9 + var19));
         }

         var14 += 360 / var2;
      }
   }

   private void lemniscatePoints(List<Vector3d> var1, int var2, double var3, double var5, double var7) {
      double var12 = 1.0;
      double var14 = var12 * var2;
      double var16 = (Math.PI * 2) / var14;

      for (int var18 = 0; var18 < var14; var18++) {
         double var19 = var18 * var16;
         double var21 = Math.sin(var19);
         double var23 = var12 * Math.sqrt(2.0) * Math.cos(var19) / (Math.pow(var21, 2.0) + 1.0);
         double var25 = var12 * Math.sqrt(2.0) * Math.cos(var19) * Math.sin(var19) / (Math.pow(var21, 2.0) + 1.0);
         if (var1 == null) {
            GL11.glVertex3d(var3 + var25, var5, var7 + var23);
         } else {
            this.strafePoints.add(new Vector3d(target.posX + var25, target.posY, target.posZ + var23));
         }
      }
   }

   private void behindPoints(List<Vector3d> var1, double var2, double var4, double var6, double var8, EntityLivingBase var10) {
      float var14 = var10.rotationYaw + 90.0F + 180.0F;
      double var15 = var2 * Math.cos(Math.toRadians(var14));
      double var17 = var2 * Math.sin(Math.toRadians(var14));
      if (var1 == null) {
         GL11.glVertex3d(var4, var6, var8);
         GL11.glVertex3d(var4 + var15, var6, var8 + var17);
         GL11.glVertex3d(var4, var6, var8);
      } else {
         this.strafePoints.add(new Vector3d(var10.posX + var15, var10.posY, var10.posZ + var17));
      }
   }

   private void attackTargets() {
      if (!this.rayCast.getValue()) {
         if (this.canAttack(target)) {
            if (mode.getValue() == KillAuraMode.MULTI) {
               for (EntityLivingBase var5 : this.targets) {
                  this.attack(var5);
               }
            } else {
               this.attack(target);
            }

            this.attackTimer.reset();
         }
      }
   }

   private void attack(Entity var1) {
      long var2 = cb ^ 133038919118032L;
      long var4 = var2 ^ 82027074305762L;
      float var7 = this.getMissChance();
      boolean var8 = RandomUtils.RANDOM.nextFloat() > (var7 != 0.0F ? var7 / 100.0F : -0.1);
      int var9 = this.getMinAps();
      if (mode.getValue() == KillAuraMode.MULTI && this.targets.size() > 1) {
         var9 = this.getMinAps() / this.targets.size();
      }

      int var10 = this.getMinAps();
      int var11 = this.getMaxAps();
      double var12 = var11 - var10;
      double var14 = 1.0 / (var10 + Math.random() * var12);
      if (var12 == 0.0) {
         this.attackDelay = 1000 / var9;
      } else {
         this.attackDelay = (int)Math.floor(var14 * 1000.0);
      }

      boolean var16 = ViaFabric.INSTANCE.isNative();
      if (var16) {
         this.doSwing();
      }

      if (var8) {
         boolean var17 = client.getModuleManager().get(RiderGod.class).isEnabled();
         ArrayList var18 = null;
         if (mode.getValue() == KillAuraMode.TP && !var17) {
            var18 = PathFinder.i(new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3(var1.posX, var1.posY, var1.posZ), 3.3);

            for (Object var20$raw : var18) {
               Vector3 var20 = (Vector3)var20$raw;
               mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(var20.getX(), var20.getY(), var20.getZ(), true));
            }
         }

         mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(var1, C02PacketUseEntity.Action.ATTACK));
         if (!var16) {
            this.doSwing();
         }

         if (this.sprint.getValue() == KillAuraSprintMode.STOP) {
            this.stopSprinting();
         }

         if (var18 != null) {
            Collections.reverse(var18);

            for (Object var23$raw : var18) {
               Vector3 var23 = (Vector3)var23$raw;
               mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(var23.getX(), var23.getY(), var23.getZ(), true));
            }
         }

         if (this.particles.getValue()) {
            boolean var22 = mc.thePlayer.fallDistance > 0.0F
               && !mc.thePlayer.onGround
               && !mc.thePlayer.isOnLadder()
               && !mc.thePlayer.isInWater()
               && !mc.thePlayer.isPotionActive(Potion.blindness)
               && mc.thePlayer.ridingEntity == null;
            boolean var24 = this.stepCooldown < 3 && !mc.thePlayer.Lr;
            if (GameUtil.N()
                  && client.getModuleManager().get(Criticals.class).isEnabled()
                  && (
                     Criticals.mode.getValue() == CriticalsMode.EDIT && var24
                        || Criticals.mode.getValue() != CriticalsMode.EDIT && Criticals.mode.getValue() != CriticalsMode.INCREMENTAL
                        || Criticals.mode.getValue() == CriticalsMode.INCREMENTAL && var24
                  )
               || var22) {
               mc.thePlayer.L(var1);
            }

            if (mc.thePlayer.getCurrentEquippedItem() != null
               && var1 instanceof EntityLivingBase
               && EnchantmentHelper.getModifierForCreature(mc.thePlayer.getCurrentEquippedItem(), ((EntityLivingBase)var1).getCreatureAttribute()) > 0.0F) {
               mc.thePlayer.x(var1);
            }
         }
      } else {
         this.doSwing();
      }

      if (this.switchTimer.hasElapsed(this.switchDelay.getValue().intValue())) {
         this.targetIndex++;
         this.switchTimer.reset();
      }
   }

   private void doSwing() {
      switch ((KillAuraSwingMode)this.swing.getValue()) {
         case CLIENT:
            mc.thePlayer.swingItem();
            break;
         case SILENT:
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
      }
   }

   private void getAdequatedSort() {
      switch ((KillAuraSortingMode)this.sorting.getValue()) {
         case DISTANCE:
            this.targets.sort((var0, var1) -> Float.compare(mc.thePlayer.getDistanceToEntity(var0), mc.thePlayer.getDistanceToEntity(var1)));
            break;
         case HEALTH:
            this.targets.sort((var0, var1) -> Float.compare(var0.getHealth(), var1.getHealth()));
            break;
         case AIM:
            this.targets.sort((var1, var2) -> {
               Vector3d var3 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
               Vector3d var4 = new Vector3d(var1.posX, var1.posY + var1.height * 0.5 / 1.0, var1.posZ);
               Vector3d var5 = new Vector3d(var2.posX, var2.posY + var2.height * 0.5 / 1.0, var2.posZ);
               Rotation var6 = this.rotator.rotationTo(var4, var3);
               Rotation var7 = this.rotator.rotationTo(var5, var3);
               double var8 = rotationDistance(var6);
               double var10 = rotationDistance(var7);
               return Double.compare(var8, var10);
            });
            break;
         case ARMOR:
            this.targets.sort((var0, var1) -> {
               int var2 = var0.getTotalArmorValue();
               int var3 = var1.getTotalArmorValue();
               return Integer.compare(var2, var3);
            });
            break;
         case DANGEROUS:
            this.targets
               .sort((var0, var1) -> Float.compare(InventoryUtil.getDamageScore(var0.getHeldItem()), InventoryUtil.getDamageScore(var1.getHeldItem())));
      }
   }

   private boolean isValidTarget(EntityLivingBase var1) {
      long var2 = cb ^ 131418877066392L;
      long var4 = var2 ^ 22854311646179L;
      if (mc.thePlayer == null || mc.theWorld == null || var1 == mc.thePlayer || GameUtil.isRemoved(var1)) {
         return false;
      }

      if (mc.thePlayer.isSpectator()) {
         return false;
      }

      if (var1 instanceof EntityPlayer && !mc.thePlayer.canAttackPlayer((EntityPlayer)var1)) {
         return false;
      }

      if (var1.ticksExisted >= this.ticksExisted.getValue()) {
         double var7 = this.eyeDistance(mc.thePlayer, var1);
         if ((this.eyeDistance(mc.thePlayer, var1) <= this.range.asDouble() || mode.getValue() == KillAuraMode.TP)
            && (!GameUtil.isDead(var1) || Double.isNaN(var1.getHealth()))
            && (!GameUtil.S(var1) || this.entities.getSetting("Invisibles").asBoolean())
            && (
               !GameUtil.canSee(var1) && mc.thePlayer.getDistanceToEntity(var1) < this.wallRange.asDouble()
                  || GameUtil.canSee(var1)
                  || mode.getValue() == KillAuraMode.TP
            )) {
            float var9 = this.getFov() / 2.0F;
            if (var9 < 180.0F && var7 > 0.2) {
               Vector3d var10 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
               Vector3d var11 = new Vector3d(var1.posX, var1.posY + var1.height * 0.5 / 1.0, var1.posZ);
               Rotation var12 = this.rotator.rotationTo(var11, var10);
               float var13 = (float)yawDistance(var12);
               if (var13 > var9) {
                  return false;
               }
            }

            if (var1 instanceof EntityPlayer && this.entities.getSetting("Players").asBoolean()) {
               EntityPlayer var15 = (EntityPlayer)var1;
               if (this.isPitTeammate(var1)) {
                  return false;
               }

               if (GameUtil.O(mc.thePlayer, var15) && this.entities.getSetting("Teams").asBoolean()) {
                  return false;
               }

               if (((AbstractClientPlayer)var15).getPlayerInfo() != null
                  && ((AbstractClientPlayer)var15).getPlayerInfo().getGameType() == WorldSettings.GameType.CREATIVE) {
                  return false;
               }

               if (!this.entities.getSetting("Astolfo-Users").asBoolean() && client.getUserManager().g(var1.getName()) != null) {
                  return false;
               }

               return !var15.isPlayerSleeping() && var15.getEntityId() < 1000000000 && var15.getEntityId() > -1;
            }

            if (GameUtil.isBoss(var1) && GameUtil.O(mc.thePlayer, var1) && this.entities.getSetting("Teams").asBoolean()) {
               return false;
            }

            if (HypixelGameTracker.game == 3 && var1 instanceof EntityZombie && GameUtil.isOnHypixel()) {
               ItemStack var14 = var1.getCurrentArmor(3);
               if (var14 != null && var14.getItem() != null && var14.getItem() == Items.skull) {
                  return true;
               }
            }

            return GameUtil.isHostile(var1) && this.entities.getSetting("Hostiles").asBoolean()
               || GameUtil.isNeutral(var1) && this.entities.getSetting("Neutrals").asBoolean()
               || GameUtil.isPassive(var1) && this.entities.getSetting("Passives").asBoolean()
               || GameUtil.isBoss(var1) && this.entities.getSetting("Bosses").asBoolean();
         }
      }

      return false;
   }

   private boolean isPitTeammate(EntityLivingBase var1) {
      if (GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT")) {
         ItemStack var7 = var1.getCurrentArmor(3);
         if (var7 != null) {
            Item var8 = var7.getItem();
            if (var8 instanceof ItemCloth) {
               ItemCloth var9 = (ItemCloth)var8;
               String var10 = var9.getUnlocalizedName(var7);

               for (int var11 = 44; var11 >= 9; var11--) {
                  if (mc.thePlayer.inventoryContainer.getSlot(var11).getHasStack()) {
                     Item var12 = mc.thePlayer.inventoryContainer.getSlot(var11).getStack().getItem();
                     if (var12 instanceof ItemCloth) {
                        ItemStack var6 = mc.thePlayer.inventoryContainer.getSlot(var11).getStack();
                        ItemCloth var5 = (ItemCloth)var12;
                        String var13 = var5.getUnlocalizedName(var6);
                        if (var10.equals(var13)) {
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private double eyeDistance(EntityLivingBase var1, Entity var2) {
      Vec3 var3 = new Vec3(var1.posX, var1.posY + var1.getEyeHeight(), var1.posZ);
      Vec3 var4 = new Vec3(var2.posX, var2.posY + var2.getEyeHeight(), var2.posZ);
      return var4.distanceTo(var3) - var2.width / 2.0F;
   }

   private boolean canAttack(Entity var1) {
      long var2 = cb ^ 7009825976367L;
      long var4 = var2 ^ 57200257802781L;
      return this.canReach(mc.thePlayer, var1)
         && this.attackTimer.hasElapsed((float)this.attackDelay)
         && (
            client.getModuleManager().get(Criticals.class).isEnabled()
                  && (
                     Criticals.mode.getValue() == CriticalsMode.INCREMENTAL && this.incrementalCritReady
                        || Criticals.mode.getValue() != CriticalsMode.INCREMENTAL
                  )
               || !client.getModuleManager().get(Criticals.class).isEnabled()
         );
   }

   private void stopSprinting() {
      if (mc.thePlayer.isSprinting()) {
         mc.thePlayer.motionX *= 0.6;
         mc.thePlayer.motionZ *= 0.6;
         mc.thePlayer.setSprinting(false);
      }
   }

   private boolean canReach(EntityLivingBase var1, Entity var2) {
      double var6 = this.range.asDouble();
      if (!(this.eyeDistance(var1, var2) <= var6) && mode.getValue() != KillAuraMode.TP) {
         return false;
      }

      MovingObjectPosition var8 = GameUtil.A(serverYaw, serverPitch, var6);
      return this.getSmoothing() > 0
            && (
               var8 != null && (var8.entityHit != null || var8.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) && this.isHitboxCheck()
                  || !this.isHitboxCheck()
            )
         || this.getSmoothing() == 0;
   }

   private EntityPlayer findBlockTarget() {
      long var1 = cb ^ 27720525102709L;
      long var10001 = var1 ^ 120308363015337L;
      int var3 = (int)((var1 ^ 120308363015337L) >>> 56);
      int var4 = (int)((var1 ^ 120308363015337L) << 8 >>> 32);
      int var5 = (int)(var10001 << 40 >>> 40);
      double var7 = this.blockRange.asDouble();
      EntityPlayer var9 = null;

      for (EntityPlayer var11 : mc.theWorld.playerEntities) {
         if (var11 != mc.thePlayer
            && !BotDetector.w.contains(var11)
            && (!GameUtil.O(mc.thePlayer, var11) || !this.entities.getSetting("Teams").asBoolean())
            && (!client.getRelationManager().z(var11) || this.entities.getSetting("Friends").asBoolean())) {
            float var12 = mc.thePlayer.getDistanceToEntity(var11);
            if (var12 < var7) {
               var9 = var11;
               var7 = var12;
            }
         }
      }

      return var9;
   }

   public static double rotationDistance(Rotation var0) {
      double var1 = Math.abs(angleDifference(mc.thePlayer.rotationYaw % 360.0F, var0.getYaw()));
      double var3 = Math.abs(angleDifference(mc.thePlayer.rotationPitch, var0.getPitch()));
      return Math.hypot(var1, var3);
   }

   public static double yawDistance(Rotation var0) {
      double var1 = Math.abs(angleDifference(mc.thePlayer.rotationYaw % 360.0F, var0.getYaw()));
      double var3 = Math.abs(angleDifference(1.0, 1.0));
      return Math.hypot(var1, var3);
   }

   public static double angleDifference(double var0, double var2) {
      return ((var0 - var2) % 360.0 + 540.0) % 360.0 - 180.0;
   }

   private int getMinAps() {
      return this.delay.getSetting("Min-APS").asInt();
   }

   private int getMaxAps() {
      return this.delay.getSetting("Max-APS").asInt();
   }

   private float getLeadOff() {
      return this.rotations.getSetting("Lead-Off").asFloat();
   }

   private int getFov() {
      return this.rotations.getSetting("FOV").asInt();
   }

   private boolean isRotationEnabled() {
      return this.rotations.getSetting("Enable").asBoolean();
   }

   private boolean isHitboxCheck() {
      return this.rotations.getSetting("Hitbox-Check").asBoolean();
   }

   private float getMissChance() {
      return this.delay.getSetting("Miss-Chance").asFloat();
   }

   private int getSmoothing() {
      return this.rotations.getSetting("Smoothing").asInt();
   }

   private boolean isHudEnabled() {
      return this.render.getSetting("HUD").asBoolean();
   }

   private boolean isVisualizationEnabled() {
      return this.render.getSetting("Visualization").asBoolean();
   }

   private boolean isLockView() {
      return this.rotations.getSetting("Lock-View").asBoolean();
   }
}
