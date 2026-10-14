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
   private final Listener<UpdateEvent> onUpdate = updateEvent -> this.p();
   private float savedYaw;
   private float savedPitch;
   private float strafeYaw;
   private final List<Vector3d> strafePoints = new ArrayList<>();
   private int strafePointIndex;
   public static final Rotator strafeRotator = new Rotator(false, 0.0F);
   private int unused;
   private boolean strafeReversed;
   @EventHandler(LookEvent.class)
   private final Listener<LookEvent> onLook = lookEvent -> {
      if (this.rayCast.getValue() && mc.thePlayer != null && target != null) {
         lookEvent.setVec(mc.thePlayer.getVectorForRotation(serverPitch, serverYaw));
      }
   };
   @EventHandler(TickEvent.class)
   private final Listener<TickEvent> onTick = tickEvent -> {
      if (tickEvent.getState() == EventState.PRE && mc.thePlayer != null && mc.theWorld != null) {
         if (!this.rayCast.getValue()) {
            return;
         }

         if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            target = mc.objectMouseOver.entityHit;
            if (target instanceof EntityLivingBase
               && this.canReach(mc.thePlayer, target)
               && this.isValidTarget((EntityLivingBase)target)
               && this.attackTimer.hasElapsed((float)this.attackDelay)) {
               int i = this.getMinAps();
               int j = this.getMaxAps();
               double d = j - i;
               double e = 1.0 / (i + Math.random() * d);
               if (d == 0.0) {
                  this.attackDelay = 1000 / i;
               } else {
                  this.attackDelay = (int)Math.floor(e * 1000.0);
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
   private Listener<MoveWithHeadingEvent> onMoveWithHeading = moveWithHeadingEvent -> {
      if (this.movementLimit.getValue() && target != null) {
         switch (moveWithHeadingEvent.getState()) {
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
            KillAuraStrafeMode killAuraStrafeMode = (KillAuraStrafeMode)this.strafe.getSetting("Mode").getValue();
            if ((
                  killAuraStrafeMode != KillAuraStrafeMode.ON_SPACE
                     || !Keyboard.isKeyDown(57)
                     || !mc.gameSettings.keyBindForward.pressed
                     || mc.gameSettings.keyBindBack.pressed
                     || mc.gameSettings.keyBindRight.pressed
                     || mc.gameSettings.keyBindLeft.pressed
               )
               && killAuraStrafeMode != KillAuraStrafeMode.ALWAYS) {
               if (this.strafeYawSaved) {
                  mc.thePlayer.rotationYaw = this.savedYaw;
                  mc.thePlayer.rotationPitch = this.savedPitch;
                  this.strafeYawSaved = false;
               }

               strafing = false;
            } else {
               switch (moveWithHeadingEvent.getState()) {
                  case PRE:
                     this.strafePoints.clear();
                     int i = this.strafe.getSetting("Points").asInt();
                     double d = this.strafe.getSetting("Range").asDouble();
                     if (this.optimize.getSetting("Rotations").asBoolean() && d > 1.15) {
                        d = 0.8;
                     }

                     KillAuraStrafeFormMode killAuraStrafeFormMode = (KillAuraStrafeFormMode)this.strafe.getSetting("Form").getValue();
                     this.traceStrafeForm(target.posX, target.posY, target.posZ, killAuraStrafeFormMode, d, i, this.strafePoints);
                     Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                     switch (killAuraStrafeFormMode) {
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

                     Vector3d vector3d2 = this.strafePoints.get(this.strafePointIndex);
                     Vector3d vector3d3 = this.strafePoints.get(this.closestPoint(this.strafePoints));
                     boolean bl = false;
                     if (client.getModuleManager().get(Flight.class).isEnabled()) {
                        bl = true;
                     } else {
                        for (int j = 0; j < 10; j++) {
                           double e = vector3d2.y - 1.0 - j;
                           if (!(mc.theWorld.getBlockState(new BlockPos(vector3d2.x, e, vector3d2.z)).getBlock() instanceof BlockAir)) {
                              bl = true;
                           }
                        }
                     }

                     if (!(mc.theWorld.getBlockState(new BlockPos(vector3d2.x, Math.floor(vector3d2.y + 2.0), vector3d2.z)).getBlock() instanceof BlockAir)
                        || !(mc.theWorld.getBlockState(new BlockPos(vector3d3.x, Math.floor(vector3d3.y + 1.5), vector3d3.z)).getBlock() instanceof BlockAir)) {
                        this.strafeReversed = !this.strafeReversed;
                     }

                     if (bl && mc.thePlayer.getDistance(vector3d2.x, vector3d2.y, vector3d2.z) < 0.3) {
                        this.strafePointIndex = this.pointNearStrafeYaw(this.strafePoints, this.strafePointIndex) + 1;
                     }

                     if (vector3d2 != null) {
                        Rotation rotation = strafeRotator.rotationTo(vector3d2, vector3d);
                        this.savedYaw = mc.thePlayer.rotationYaw;
                        this.savedPitch = mc.thePlayer.rotationPitch;
                        mc.thePlayer.rotationYaw = rotation.getYaw();
                        this.strafeYaw = mc.thePlayer.rotationYaw;
                        mc.thePlayer.rotationPitch = 0.0F;
                        if (!bl) {
                           mc.thePlayer.rotationYaw -= 180.0F;
                        }

                        moveWithHeadingEvent.setForward(1.0F);
                        moveWithHeadingEvent.setStrafe(0.0F);
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
   private final Listener<TeleportEvent> onTeleport = teleportEvent -> {
      if (!teleportEvent.y()) {
         if (this.autoDisable.getValue()) {
            teleportEvent.i(this);
         }
      } else {
         this.teleportTimer.reset();
      }
   };
   @EventHandler(DeathEvent.class)
   private final Listener<DeathEvent> onDeath = deathEvent -> {
      if (this.autoDisable.getValue()) {
         this.setEnabled(false);
      }
   };
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)packetEvent.getPacket();
            if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && GameUtil.isHoldingSword(mc.thePlayer)) {
               this.blocking = false;
            }
         }

         if (packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = (C08PacketPlayerBlockPlacement)packetEvent.getPacket();
            if (c08PacketPlayerBlockPlacement.getPlacedBlockDirection() == 255 && GameUtil.isHoldingSword(mc.thePlayer)) {
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

         if (packetEvent.getPacket() instanceof C09PacketHeldItemChange) {
            this.blocking = false;
         }
      }

      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook)packetEvent.getPacket();
            serverYaw = s08PacketPlayerPosLook.getYaw();
            serverPitch = s08PacketPlayerPosLook.getPitch();
         }

         if (packetEvent.getPacket() instanceof S30PacketWindowItems
            && (autoBlock.getValue() == KillAuraAutoBlockMode.WATCHDOG || autoBlock.getValue() == KillAuraAutoBlockMode.WATCHDOG2)) {
            this.blocking = false;
         }
      }
   };
   public static boolean strafing;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGH)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      boolean bl = this.isLockView();
      int i = this.getSmoothing();
      float f = this.getLeadOff();
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
         EntityPlayer entityPlayer = this.findBlockTarget();
         switch (motionEvent.getState()) {
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

                  for (EntityLivingBase entityLivingBase : this.targets) {
                     if (entityLivingBase instanceof EntityWither && ScoreboardTracker.r()) {
                        target = entityLivingBase;
                        break;
                     }

                     if (entityLivingBase instanceof EntityPlayer) {
                        if (client.getRelationManager().getList().isEmpty() && AutoHypixel.bountiedPlayers.isEmpty()) {
                           break;
                        }

                        if (client.getRelationManager().D(entityLivingBase) || AutoHypixel.bountiedPlayers.contains(entityLivingBase)) {
                           target = entityLivingBase;
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
                        Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                        double d = this.rotations.getSetting("Random").asFloat();
                        Vector3d vector3d2 = new Vector3d(
                           target.posX + RandomUtils.nextDouble(-d, d) / 100.0,
                           target.posY + RandomUtils.nextDouble(-d, d) / 100.0 + target.height * 0.5 / 1.0,
                           target.posZ + RandomUtils.nextDouble(-d, d) / 100.0
                        );
                        if (f > 0.0F) {
                           Vector3d vector3d3 = new Vector3d(target.posX - target.lastTickPosX, 0.0, target.posZ - target.lastTickPosZ);
                           double e = this.eyeDistance(mc.thePlayer, target);
                           vector3d2 = this.rotator.predict(vector3d2, vector3d3, target.isSprinting(), f, e);
                        }

                        this.rotator.setRandomize(false);
                        this.rotator.setSmoothing(100 - i);
                        Rotation rotation2 = this.rotator.rotationTo(vector3d2, vector3d);
                        Rotation rotation3 = this.rotator.smooth(rotation2, new Rotation(motionEvent.getLastYaw(), motionEvent.getLastPitch()));
                        float g = i == 0 ? rotation2.getYaw() : rotation3.getYaw();
                        float h = i == 0 ? rotation2.getPitch() : rotation3.getPitch();
                        float f2 = MathHelper.wrapAngleTo180_float(g - mc.thePlayer.rotationYaw);
                        g = mc.thePlayer.rotationYaw + f2;
                        lastYaw = motionEvent.getLastYaw();
                        lastPitch = motionEvent.getLastPitch();
                        if (this.movementRotated || !this.movementLimit.getValue()) {
                           if (this.rotations.getSetting("Fix-Delta").asBoolean()) {
                              float f3 = mc.gameSettings.Zm * 0.6F + 0.2F;
                              float f4 = f3 * f3 * f3 * 1.2F;
                              float f5 = g - serverYaw;
                              float f6 = h - serverPitch;
                              float f7 = f5 - f5 % f4;
                              float f8 = f6 - f6 % f4;
                              float f9 = serverYaw + f7;
                              float f10 = serverPitch + f8;
                              g = f9;
                              h = f10;
                           }

                           if (this.movementLimit.getValue()) {
                              motionEvent.setYaw(serverYaw);
                              motionEvent.setPitch(serverPitch);
                              serverYaw = g;
                              serverPitch = h;
                           } else {
                              if (this.optimize.getSetting("Rotations").asBoolean() && strafing && !target.onGround) {
                                 motionEvent.setYaw(motionEvent.getLastYaw());
                              } else {
                                 motionEvent.setYaw(bl ? (mc.thePlayer.rotationYaw = g) : g);
                              }

                              motionEvent.setPitch(bl ? (mc.thePlayer.rotationPitch = h) : h);
                              serverYaw = motionEvent.getYaw();
                              serverPitch = motionEvent.getPitch();
                           }

                           this.movementRotated = false;
                        }
                     }

                     if (client.getModuleManager().get(Criticals.class).isEnabled() && GameUtil.N() && this.stepCooldown < 3 && !mc.thePlayer.Lr && Criticals.j
                        )
                      {
                        switch ((CriticalsMode)Criticals.mode.getValue()) {
                           case EDIT:
                              this.editCriticals(motionEvent);
                              break;
                           case INCREMENTAL:
                              this.incrementalCriticals(motionEvent);
                              break;
                           case WATCHDOG:
                              this.watchdogCriticals(motionEvent);
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
               if (target == null && entityPlayer == null) {
                  this.stopBlocking();
               } else {
                  if ((this.attackMode.getValue().equals(KillAuraAttackMode.POST) || this.attackMode.getValue().equals(KillAuraAttackMode.MIX))
                     && target != null) {
                     this.attackTargets();
                  }

                  if (!GameUtil.isHoldingSword(mc.thePlayer)
                     || autoBlock.getValue().equals(KillAuraAutoBlockMode.NONE)
                     || (!(this.blockRange.asDouble() > 0.0) || entityPlayer == null) && target == null) {
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
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (target != null && renderLivingEvent.getEntity() == mc.thePlayer && this.isRotationEnabled()) {
      }
   };
   @EventHandler(StepEvent.class)
   private Listener<StepEvent> onStep = stepEvent -> {
      if (stepEvent.getEntity() == mc.thePlayer && stepEvent.getState() == StepState.PRE && stepEvent.getVec().yCoord >= 0.2) {
         this.stepCooldown = 4;
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      try {
         switch (renderEvent.getType()) {
            case SCREEN:
               if (this.targetHud != null && this.isHudEnabled()) {
                  NumberSetting numberSetting = (NumberSetting)this.render.getSetting("Pos-X");
                  NumberSetting numberSetting2 = (NumberSetting)this.render.getSetting("Pos-Y");
                  if (Mouse.isButtonDown(0)) {
                     numberSetting.setValue(this.targetHud.M());
                     numberSetting2.setValue(this.targetHud.E_float());
                  } else if ((Float)numberSetting.getValue() != this.targetHud.M() || (Float)numberSetting2.getValue() != this.targetHud.E_float()) {
                     if (this.targetHud.M() != (Float)numberSetting.getValue()) {
                        this.targetHud.o((Float)numberSetting.getValue());
                     }

                     if (this.targetHud.E_float() != (Float)numberSetting2.getValue()) {
                        this.targetHud.q((Float)numberSetting2.getValue());
                     }
                  }

                  if ((Float)numberSetting.getValue() > renderEvent.getResolution().getScaledWidth()) {
                     numberSetting.setValue(1.0F);
                  }

                  if ((Float)numberSetting2.getValue() > renderEvent.getResolution().getScaledHeight()) {
                     numberSetting2.setValue(renderEvent.getResolution().getScaledHeight() / 1.5F);
                  }

                  int i = renderEvent.getResolution().getScaledHeight();
                  int j = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
                  int k = i - Mouse.getY() * i / mc.displayHeight - 1;
                  if (target instanceof EntityLivingBase) {
                     this.targetHud.z((EntityLivingBase)target);
                     this.targetHud.y(renderEvent.getResolution().getScaledWidth() / 2.0F);
                     this.targetHud.render(j, k);
                  }

                  if (mc.currentScreen instanceof GuiChat) {
                     if (target == null && mc.thePlayer != null) {
                        this.targetHud.z(mc.thePlayer);
                        this.targetHud.y(renderEvent.getResolution().getScaledWidth() / 2.0F);
                        this.targetHud.render(j, k);
                     }

                     if (this.targetHud.u_boolean() && Mouse.isButtonDown(0)) {
                        this.targetHud.T(j, k);
                     }
                  }

                  if (Mouse.isButtonDown(0) && this.targetHud.G(j, k)) {
                     this.targetHud.F(true, j, k);
                  } else {
                     this.targetHud.x(false);
                  }
               }
               break;
            case WORLD:
               double d = MathUtil.interpolate(mc.thePlayer.lastTickPosX, mc.thePlayer.posX, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosX);
               double e = MathUtil.interpolate(mc.thePlayer.lastTickPosY, mc.thePlayer.posY, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosY);
               double d2 = MathUtil.interpolate(mc.thePlayer.lastTickPosZ, mc.thePlayer.posZ, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosZ);
               long l = System.currentTimeMillis();
               double d3 = l % 7000L / 7000.0 * 360.0;
               this.drawPentagram(d, e, d2, d3);
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
                     d,
                     e + 0.1,
                     d2,
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

               KillAuraRenderStrafeMode killAuraRenderStrafeMode = (KillAuraRenderStrafeMode)this.render.getSetting("Strafe").getValue();
               if (killAuraRenderStrafeMode == KillAuraRenderStrafeMode.FALSE) {
                  return;
               }

               if (this.strafe.getSetting("Enable").asBoolean()
                  && target != null
                  && !client.getModuleManager().get(Scaffold.class).isEnabled()
                  && !mc.thePlayer.isInWater()
                  && !mc.thePlayer.isOnLadder()) {
                  KillAuraStrafeMode killAuraStrafeMode = (KillAuraStrafeMode)this.strafe.getSetting("Mode").getValue();
                  if (killAuraStrafeMode == KillAuraStrafeMode.ON_SPACE
                        && Keyboard.isKeyDown(57)
                        && mc.gameSettings.keyBindForward.pressed
                        && !mc.gameSettings.keyBindBack.pressed
                        && !mc.gameSettings.keyBindRight.pressed
                        && !mc.gameSettings.keyBindLeft.pressed
                     || killAuraStrafeMode == KillAuraStrafeMode.ALWAYS && !mc.thePlayer.onGround
                     || killAuraRenderStrafeMode == KillAuraRenderStrafeMode.ALWAYS) {
                     GL11.glPushMatrix();
                     GL11.glDisable(3553);
                     GL11.glDisable(2896);
                     GL11.glEnable(3042);
                     GL11.glBlendFunc(770, 771);
                     GL11.glDisable(2929);
                     GL11.glEnable(2848);
                     GL11.glDepthMask(false);
                     KillAuraStrafeFormMode killAuraStrafeFormMode = (KillAuraStrafeFormMode)this.strafe.getSetting("Form").getValue();
                     GlStateManager.pushMatrix();
                     GL11.glLineWidth(5.0F);
                     GL11.glBegin(2);
                     double d4 = this.strafe.getSetting("Range").asDouble();
                     int m = this.strafe.getSetting("Points").asInt();
                     RenderUtil.setColor(Color.BLACK.getRGB());
                     this.traceStrafeForm(d, e, d2, killAuraStrafeFormMode, d4, m, null);
                     GL11.glEnd();
                     GlStateManager.popMatrix();
                     GlStateManager.pushMatrix();
                     GL11.glLineWidth(2.0F);
                     GL11.glBegin(2);
                     RenderUtil.setColor(((ColorSetting)this.strafe.getSetting("Color")).getValue().getRGB());
                     this.traceStrafeForm(d, e, d2, killAuraStrafeFormMode, d4, m, null);
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
      } catch (Exception exception) {
      }
   };
   private final double critOffset = RandomUtils.nextDouble(0.001, 0.009);
   private final double unusedOffset = RandomUtils.nextDouble(0.01, 0.02);

   public KillAura(Category category) {
      super(category);
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

   private int pointNearStrafeYaw(List<Vector3d> list, int i) {
      float f = 30.0F;
      int j = i;
      Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

      for (int k = this.strafePointIndex; k < list.size(); k++) {
         Vector3d vector3d2 = (Vector3d)list.get(k);
         Rotation rotation = strafeRotator.rotationTo(vector3d2, vector3d);
         float g = Math.abs(this.strafeYaw - rotation.getYaw());
         if (g < f) {
            j = k;
            f = g;
         }
      }

      return j;
   }

   private int closestPoint(List<Vector3d> list) {
      double d = 999.0;
      int i = 0;

      for (int j = 0; j < list.size(); j++) {
         Vector3d vector3d = (Vector3d)list.get(j);
         double e = mc.thePlayer.getDistance(vector3d.x, vector3d.y, vector3d.z);
         if (e < d) {
            i = j;
            d = e;
         }
      }

      return i;
   }

   public float getSensitivityStep() {
      double d = mc.gameSettings.Zm * 0.6 + 0.2;
      double e = d * d * d * 8.0;
      return (float)(e * 0.15F);
   }

   public void drawPentagram(double d, double d2, double d3, double d4) {
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
      GL11.glTranslated(d, d2, d3);
      GL11.glRotated(d4, 0.0, 1.0, 0.0);
      GL11.glRotated(90.0, 1.0, 0.0, 0.0);
      double[] doubles = new double[10];

      for (int i = 0; i < 5; i++) {
         double e = (Math.PI * 2) * i / 5.0;
         double d5 = Math.cos(e);
         double d6 = Math.sin(e);
         doubles[i * 2] = d5;
         doubles[i * 2 + 1] = d6;
      }

      GL11.glBegin(1);
      GL11.glVertex3d(doubles[0], doubles[1], 0.0);
      GL11.glVertex3d(doubles[4], doubles[5], 0.0);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3d(doubles[4], doubles[5], 0.0);
      GL11.glVertex3d(doubles[8], doubles[9], 0.0);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3d(doubles[8], doubles[9], 0.0);
      GL11.glVertex3d(doubles[2], doubles[3], 0.0);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3d(doubles[2], doubles[3], 0.0);
      GL11.glVertex3d(doubles[6], doubles[7], 0.0);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3d(doubles[6], doubles[7], 0.0);
      GL11.glVertex3d(doubles[0], doubles[1], 0.0);
      GL11.glEnd();
      GL11.glDepthMask(true);
      GL11.glDisable(2848);
      GL11.glEnable(2929);
      GL11.glEnable(2896);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   private void traceStrafeForm(double d, double d2, double d3, KillAuraStrafeFormMode killAuraStrafeFormMode, double d4, int i, List<Vector3d> list) {
      switch (killAuraStrafeFormMode) {
         case CIRCLE:
            this.circlePoints(list, i, d4, d, d2, d3);
            break;
         case INFINITE:
            this.lemniscatePoints(list, i, d, d2, d3);
            break;
         case BACK:
            if (target instanceof EntityLivingBase) {
               this.behindPoints(list, d4, d, d2, d3, (EntityLivingBase)target);
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

   private void editCriticals(MotionEvent motionEvent) {
      if (criticalStage < 2) {
         motionEvent.setOnGround(false);
         criticalStage++;
      } else {
         motionEvent.setMinY(motionEvent.getMinY() + Criticals.offset.getValue().floatValue());
         criticalStage = 0;
      }
   }

   private void watchdogCriticals(MotionEvent motionEvent) {
      switch (criticalStage) {
         case 0:
            motionEvent.setMinY(motionEvent.getMinY() + 0.0325 + Math.random() / 1000.0);
            motionEvent.setOnGround(false);
            break;
         case 1:
            motionEvent.setMinY(motionEvent.getMinY() + 0.012500048F);
            motionEvent.setOnGround(false);
      }

      criticalStage++;
      if (criticalStage >= 2) {
         criticalStage = 0;
      }
   }

   private void incrementalCriticals(MotionEvent motionEvent) {
      switch (criticalStage) {
         case 0:
            motionEvent.setMinY(motionEvent.getMinY() + 0.07 + this.critOffset);
            motionEvent.setOnGround(false);
            this.incrementalCritReady = false;
         case 1:
            motionEvent.setMinY(motionEvent.getMinY() + 1.1100000088E-5);
            motionEvent.setOnGround(false);
            this.incrementalCritReady = true;
            break;
         case 2:
            motionEvent.setOnGround(true);
            this.incrementalCritReady = false;
      }

      criticalStage++;
      if (criticalStage >= 2) {
         criticalStage = 0;
      }
   }

   private void startBlocking() {
      KillAuraAutoBlockMode killAuraAutoBlockMode = this.shouldOptimizeBlock() ? KillAuraAutoBlockMode.INTERACT : autoBlock.getValue();
      switch (killAuraAutoBlockMode) {
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
         KillAuraAutoBlockMode killAuraAutoBlockMode = this.shouldOptimizeBlock() ? KillAuraAutoBlockMode.INTERACT : autoBlock.getValue();
         switch (killAuraAutoBlockMode) {
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
      ArrayList arrayList = new ArrayList(this.maxTargets.getValue());
      List list = mc.theWorld.loadedEntityList;
      int i = 0;

      for (int j = list.size(); i < j; i++) {
         Entity entity = (Entity)list.get(i);
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            if (arrayList.size() > this.maxTargets.getValue()) {
               break;
            }

            if (!BotDetector.w.contains(entityLivingBase) && (!client.getRelationManager().z(entity) || this.entities.getSetting("Friends").asBoolean())) {
               AstolfoUser astolfoUser = client.getUserManager().g(entity.getName());
               if ((astolfoUser == null || astolfoUser.K != AstolfoUserStatus.C) && this.isValidTarget(entityLivingBase)) {
                  arrayList.add(entityLivingBase);
               }
            }
         }
      }

      return arrayList;
   }

   private void circlePoints(List<Vector3d> list, int i, double d, double d2, double d3, double d4) {
      int j = 0;

      while (j < 360) {
         double e = j * Math.PI / 180.0;
         double d5 = d * Math.cos(e);
         double d6 = d * Math.sin(e);
         if (list == null) {
            GL11.glVertex3d(d2 + d5, d3, d4 + d6);
         } else {
            list.add(new Vector3d(d2 + d5, d3, d4 + d6));
         }

         j += 360 / i;
      }
   }

   private void lemniscatePoints(List<Vector3d> list, int i, double d, double d2, double d3) {
      double e = 1.0;
      double d4 = e * i;
      double d5 = (Math.PI * 2) / d4;

      for (int j = 0; j < d4; j++) {
         double d6 = j * d5;
         double d7 = Math.sin(d6);
         double d8 = e * Math.sqrt(2.0) * Math.cos(d6) / (Math.pow(d7, 2.0) + 1.0);
         double d9 = e * Math.sqrt(2.0) * Math.cos(d6) * Math.sin(d6) / (Math.pow(d7, 2.0) + 1.0);
         if (list == null) {
            GL11.glVertex3d(d + d9, d2, d3 + d8);
         } else {
            this.strafePoints.add(new Vector3d(target.posX + d9, target.posY, target.posZ + d8));
         }
      }
   }

   private void behindPoints(List<Vector3d> list, double d, double d2, double d3, double d4, EntityLivingBase entityLivingBase) {
      float f = entityLivingBase.rotationYaw + 90.0F + 180.0F;
      double e = d * Math.cos(Math.toRadians(f));
      double d5 = d * Math.sin(Math.toRadians(f));
      if (list == null) {
         GL11.glVertex3d(d2, d3, d4);
         GL11.glVertex3d(d2 + e, d3, d4 + d5);
         GL11.glVertex3d(d2, d3, d4);
      } else {
         this.strafePoints.add(new Vector3d(entityLivingBase.posX + e, entityLivingBase.posY, entityLivingBase.posZ + d5));
      }
   }

   private void attackTargets() {
      if (!this.rayCast.getValue()) {
         if (this.canAttack(target)) {
            if (mode.getValue() == KillAuraMode.MULTI) {
               for (EntityLivingBase entityLivingBase : this.targets) {
                  this.attack(entityLivingBase);
               }
            } else {
               this.attack(target);
            }

            this.attackTimer.reset();
         }
      }
   }

   private void attack(Entity entity) {
      float f = this.getMissChance();
      boolean bl = RandomUtils.RANDOM.nextFloat() > (f != 0.0F ? f / 100.0F : -0.1);
      int j = this.getMinAps();
      if (mode.getValue() == KillAuraMode.MULTI && this.targets.size() > 1) {
         j = this.getMinAps() / this.targets.size();
      }

      int k = this.getMinAps();
      int l = this.getMaxAps();
      double d = l - k;
      double e = 1.0 / (k + Math.random() * d);
      if (d == 0.0) {
         this.attackDelay = 1000 / j;
      } else {
         this.attackDelay = (int)Math.floor(e * 1000.0);
      }

      boolean bl2 = ViaFabric.INSTANCE.isNative();
      if (bl2) {
         this.doSwing();
      }

      if (bl) {
         boolean bl3 = client.getModuleManager().get(RiderGod.class).isEnabled();
         ArrayList arrayList = null;
         if (mode.getValue() == KillAuraMode.TP && !bl3) {
            arrayList = PathFinder.i(new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3(entity.posX, entity.posY, entity.posZ), 3.3);

            for (Object object : arrayList) {
               Vector3 vector3 = (Vector3)object;
               mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(vector3.getX(), vector3.getY(), vector3.getZ(), true));
            }
         }

         mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
         if (!bl2) {
            this.doSwing();
         }

         if (this.sprint.getValue() == KillAuraSprintMode.STOP) {
            this.stopSprinting();
         }

         if (arrayList != null) {
            Collections.reverse(arrayList);

            for (Object object2 : arrayList) {
               Vector3 vector32 = (Vector3)object2;
               mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(vector32.getX(), vector32.getY(), vector32.getZ(), true));
            }
         }

         if (this.particles.getValue()) {
            boolean bl4 = mc.thePlayer.fallDistance > 0.0F
               && !mc.thePlayer.onGround
               && !mc.thePlayer.isOnLadder()
               && !mc.thePlayer.isInWater()
               && !mc.thePlayer.isPotionActive(Potion.blindness)
               && mc.thePlayer.ridingEntity == null;
            boolean bl5 = this.stepCooldown < 3 && !mc.thePlayer.Lr;
            if (GameUtil.N()
                  && client.getModuleManager().get(Criticals.class).isEnabled()
                  && (
                     Criticals.mode.getValue() == CriticalsMode.EDIT && bl5
                        || Criticals.mode.getValue() != CriticalsMode.EDIT && Criticals.mode.getValue() != CriticalsMode.INCREMENTAL
                        || Criticals.mode.getValue() == CriticalsMode.INCREMENTAL && bl5
                  )
               || bl4) {
               mc.thePlayer.L(entity);
            }

            if (mc.thePlayer.getCurrentEquippedItem() != null
               && entity instanceof EntityLivingBase
               && EnchantmentHelper.getModifierForCreature(mc.thePlayer.getCurrentEquippedItem(), ((EntityLivingBase)entity).getCreatureAttribute()) > 0.0F) {
               mc.thePlayer.x(entity);
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
            this.targets.sort((entityLivingBase, entityLivingBase2) -> Float.compare(mc.thePlayer.getDistanceToEntity(entityLivingBase), mc.thePlayer.getDistanceToEntity(entityLivingBase2)));
            break;
         case HEALTH:
            this.targets.sort((entityLivingBase3, entityLivingBase4) -> Float.compare(entityLivingBase3.getHealth(), entityLivingBase4.getHealth()));
            break;
         case AIM:
            this.targets.sort((entityLivingBase5, entityLivingBase6) -> {
               Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
               Vector3d vector3d2 = new Vector3d(entityLivingBase5.posX, entityLivingBase5.posY + entityLivingBase5.height * 0.5 / 1.0, entityLivingBase5.posZ);
               Vector3d vector3d3 = new Vector3d(entityLivingBase6.posX, entityLivingBase6.posY + entityLivingBase6.height * 0.5 / 1.0, entityLivingBase6.posZ);
               Rotation rotation = this.rotator.rotationTo(vector3d2, vector3d);
               Rotation rotation2 = this.rotator.rotationTo(vector3d3, vector3d);
               double d = rotationDistance(rotation);
               double e = rotationDistance(rotation2);
               return Double.compare(d, e);
            });
            break;
         case ARMOR:
            this.targets.sort((entityLivingBase7, entityLivingBase8) -> {
               int i = entityLivingBase7.getTotalArmorValue();
               int j = entityLivingBase8.getTotalArmorValue();
               return Integer.compare(i, j);
            });
            break;
         case DANGEROUS:
            this.targets
               .sort((entityLivingBase9, entityLivingBase10) -> Float.compare(InventoryUtil.getDamageScore(entityLivingBase9.getHeldItem()), InventoryUtil.getDamageScore(entityLivingBase10.getHeldItem())));
      }
   }

   private boolean isValidTarget(EntityLivingBase entityLivingBase) {
      if (mc.thePlayer == null || mc.theWorld == null || entityLivingBase == mc.thePlayer || GameUtil.isRemoved(entityLivingBase)) {
         return false;
      }

      if (mc.thePlayer.isSpectator()) {
         return false;
      }

      if (entityLivingBase instanceof EntityPlayer && !mc.thePlayer.canAttackPlayer((EntityPlayer)entityLivingBase)) {
         return false;
      }

      if (entityLivingBase.ticksExisted >= this.ticksExisted.getValue()) {
         double d = this.eyeDistance(mc.thePlayer, entityLivingBase);
         if ((this.eyeDistance(mc.thePlayer, entityLivingBase) <= this.range.asDouble() || mode.getValue() == KillAuraMode.TP)
            && (!GameUtil.isDead(entityLivingBase) || Double.isNaN(entityLivingBase.getHealth()))
            && (!GameUtil.S(entityLivingBase) || this.entities.getSetting("Invisibles").asBoolean())
            && (
               !GameUtil.canSee(entityLivingBase) && mc.thePlayer.getDistanceToEntity(entityLivingBase) < this.wallRange.asDouble()
                  || GameUtil.canSee(entityLivingBase)
                  || mode.getValue() == KillAuraMode.TP
            )) {
            float f = this.getFov() / 2.0F;
            if (f < 180.0F && d > 0.2) {
               Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
               Vector3d vector3d2 = new Vector3d(entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.height * 0.5 / 1.0, entityLivingBase.posZ);
               Rotation rotation = this.rotator.rotationTo(vector3d2, vector3d);
               float h = (float)yawDistance(rotation);
               if (h > f) {
                  return false;
               }
            }

            if (entityLivingBase instanceof EntityPlayer && this.entities.getSetting("Players").asBoolean()) {
               EntityPlayer entityPlayer = (EntityPlayer)entityLivingBase;
               if (this.isPitTeammate(entityLivingBase)) {
                  return false;
               }

               if (GameUtil.O(mc.thePlayer, entityPlayer) && this.entities.getSetting("Teams").asBoolean()) {
                  return false;
               }

               if (((AbstractClientPlayer)entityPlayer).getPlayerInfo() != null
                  && ((AbstractClientPlayer)entityPlayer).getPlayerInfo().getGameType() == WorldSettings.GameType.CREATIVE) {
                  return false;
               }

               if (!this.entities.getSetting("Astolfo-Users").asBoolean() && client.getUserManager().g(entityLivingBase.getName()) != null) {
                  return false;
               }

               return !entityPlayer.isPlayerSleeping() && entityPlayer.getEntityId() < 1000000000 && entityPlayer.getEntityId() > -1;
            }

            if (GameUtil.isBoss(entityLivingBase) && GameUtil.O(mc.thePlayer, entityLivingBase) && this.entities.getSetting("Teams").asBoolean()) {
               return false;
            }

            if (HypixelGameTracker.game == 3 && entityLivingBase instanceof EntityZombie && GameUtil.isOnHypixel()) {
               ItemStack itemStack = entityLivingBase.getCurrentArmor(3);
               if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() == Items.skull) {
                  return true;
               }
            }

            return GameUtil.isHostile(entityLivingBase) && this.entities.getSetting("Hostiles").asBoolean()
               || GameUtil.isNeutral(entityLivingBase) && this.entities.getSetting("Neutrals").asBoolean()
               || GameUtil.isPassive(entityLivingBase) && this.entities.getSetting("Passives").asBoolean()
               || GameUtil.isBoss(entityLivingBase) && this.entities.getSetting("Bosses").asBoolean();
         }
      }

      return false;
   }

   private boolean isPitTeammate(EntityLivingBase entityLivingBase) {
      if (GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT")) {
         ItemStack itemStack = entityLivingBase.getCurrentArmor(3);
         if (itemStack != null) {
            Item item = itemStack.getItem();
            if (item instanceof ItemCloth) {
               ItemCloth itemCloth = (ItemCloth)item;
               String string = itemCloth.getUnlocalizedName(itemStack);

               for (int i = 44; i >= 9; i--) {
                  if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                     Item item2 = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                     if (item2 instanceof ItemCloth) {
                        ItemStack itemStack2 = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        ItemCloth itemCloth2 = (ItemCloth)item2;
                        String string2 = itemCloth2.getUnlocalizedName(itemStack2);
                        if (string.equals(string2)) {
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

   private double eyeDistance(EntityLivingBase entityLivingBase, Entity entity) {
      Vec3 vec3 = new Vec3(entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.getEyeHeight(), entityLivingBase.posZ);
      Vec3 vec32 = new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
      return vec32.distanceTo(vec3) - entity.width / 2.0F;
   }

   private boolean canAttack(Entity entity) {
      return this.canReach(mc.thePlayer, entity)
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

   private boolean canReach(EntityLivingBase entityLivingBase, Entity entity) {
      double d = this.range.asDouble();
      if (!(this.eyeDistance(entityLivingBase, entity) <= d) && mode.getValue() != KillAuraMode.TP) {
         return false;
      }

      MovingObjectPosition movingObjectPosition = GameUtil.A(serverYaw, serverPitch, d);
      return this.getSmoothing() > 0
            && (
               movingObjectPosition != null && (movingObjectPosition.entityHit != null || movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) && this.isHitboxCheck()
                  || !this.isHitboxCheck()
            )
         || this.getSmoothing() == 0;
   }

   private EntityPlayer findBlockTarget() {
      double d = this.blockRange.asDouble();
      EntityPlayer entityPlayer = null;

      for (EntityPlayer entityPlayer2 : mc.theWorld.playerEntities) {
         if (entityPlayer2 != mc.thePlayer
            && !BotDetector.w.contains(entityPlayer2)
            && (!GameUtil.O(mc.thePlayer, entityPlayer2) || !this.entities.getSetting("Teams").asBoolean())
            && (!client.getRelationManager().z(entityPlayer2) || this.entities.getSetting("Friends").asBoolean())) {
            float f = mc.thePlayer.getDistanceToEntity(entityPlayer2);
            if (f < d) {
               entityPlayer = entityPlayer2;
               d = f;
            }
         }
      }

      return entityPlayer;
   }

   public static double rotationDistance(Rotation rotation) {
      double d = Math.abs(angleDifference(mc.thePlayer.rotationYaw % 360.0F, rotation.getYaw()));
      double e = Math.abs(angleDifference(mc.thePlayer.rotationPitch, rotation.getPitch()));
      return Math.hypot(d, e);
   }

   public static double yawDistance(Rotation rotation) {
      double d = Math.abs(angleDifference(mc.thePlayer.rotationYaw % 360.0F, rotation.getYaw()));
      double e = Math.abs(angleDifference(1.0, 1.0));
      return Math.hypot(d, e);
   }

   public static double angleDifference(double d, double d2) {
      return ((d - d2) % 360.0 + 540.0) % 360.0 - 180.0;
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
