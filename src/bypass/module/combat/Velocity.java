package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.JumpEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.hidden.BotDetector;
import bypass.module.movement.Flight;
import bypass.module.movement.LongJump;
import bypass.module.other.AutoHypixel;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.MathUtil;
import bypass.util.RandomUtils;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.EnumChatFormatting;

public final class Velocity extends Module {
   private final EnumSetting<VelocityMode> mode = new EnumSetting<>("Mode", "Set your velocity modes", VelocityMode.PACKET);
   private final NumberSetting<Float> horizontal = new NumberSetting<>("Horizontal", "Percentage of horizontal velocity", 0.0F, -100.0F, 100.0F, "%");
   private final NumberSetting<Float> vertical = new NumberSetting<>("Vertical", "Percentage of vertical velocity", 0.0F, -100.0F, 100.0F, "%");
   private final NumberSetting<Integer> chance = new NumberSetting<>("Chance", "Chance of doing velocity", 100, 0, 100, "%");
   public static final BooleanSetting water = new BooleanSetting("Water", "Stops water from pushing you", true);
   private final MapSetting kite = new MapSettingBuilder("Kite")
      .ordered()
      .add("Enable", new BooleanSetting("Enable", "Enable", false))
      .add("Horizontal", new NumberSetting<>("Horizontal", "Percentage of horizontal velocity", 100.0F, 100.0F, 150.0F, "%"))
      .build();
   private final MapSetting hypixelStaff = new MapSettingBuilder("Hypixel-Staff")
      .ordered()
      .description("hypixel")
      .add("Enable", new BooleanSetting("Enable", "Enable", false))
      .add("Take-KB", new BooleanSetting("Take-KB", "Take kb when staff tested", false))
      .build();
   private final MapSetting push = new MapSettingBuilder("Push")
      .ordered()
      .description("push velocity")
      .add("Reduce-Y", new BooleanSetting("Reduce-Y", "Reduces y pos", true))
      .add("Push", new NumberSetting<>("Push", "Push back amount", 2.0F, 1.0F, 3.0F))
      .build();
   private long lastVelocityTime;
   private boolean unusedFlagA;
   private boolean unusedFlagB;
   private Entity nearbyThreat;
   private int threatTicks;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.updateDisplayName();
      if (updateEvent.getState() == EventState.PRE) {
         if (this.isStaffCheckEnabled()) {
            Entity entity = this.findNearbyThreat();
            if (entity != null) {
               this.nearbyThreat = entity;
               this.threatTicks = 20;
            } else if (this.threatTicks-- <= 0) {
               this.nearbyThreat = null;
            }
         }

         if (this.mode.getValue().equals(VelocityMode.PULL)) {
            if (mc.thePlayer.movementInput.jump) {
               return;
            }

            if (this.lastVelocityTime != 0L && System.currentTimeMillis() - this.lastVelocityTime > 80L) {
               this.lastVelocityTime = 0L;
            }

            if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0) {
               mc.thePlayer.onGround = true;
            }

            if (mc.thePlayer.hurtResistantTime > 0 && this.push.getSetting("Reduce-Y").asBoolean()) {
               mc.thePlayer.motionY -= 0.0144;
            }

            if (mc.thePlayer.hurtResistantTime >= 19) {
               double d = this.push.getSetting("Push").asFloat();
               mc.thePlayer.motionX /= d;
               mc.thePlayer.motionZ /= d;
            }
         }

         if (this.mode.getValue() == VelocityMode.LEGIT && mc.thePlayer.hurtTime == 9) {
            mc.thePlayer.motionX = mc.thePlayer.motionX * (this.horizontal.getValue() / 100.0F);
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * (this.horizontal.getValue() / 100.0F);
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         float f = 100 - this.chance.getValue();
         boolean bl = RandomUtils.RANDOM.nextFloat() > (f != 0.0F ? f / 100.0F : -0.1);
         if (bl) {
            if (mc.thePlayer != null && mc.theWorld != null && GameUtil.isOnHypixel() && this.isStaffCheckEnabled()) {
               if (GameUtil.scoreboardTitleContains("PIT") && !GuiIngame.isPitSpireEvent) {
                  if (Math.abs(AutoHypixel.pitSpawnY - mc.thePlayer.posY) < 4.0) {
                     return;
                  }
               } else {
                  if (GameUtil.scoreboardTitleContains("UHC CHAMPIONS") && mc.thePlayer.getMaxHealth() == 20.0F) {
                     return;
                  }

                  if (GameUtil.scoreboardTitleContains("MEGA WALLS") && mc.thePlayer.getMaxHealth() == 20.0F) {
                     return;
                  }
               }
            }

            if (packetEvent.getPacket() instanceof S12PacketEntityVelocity) {
               S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity)packetEvent.getPacket();
               if (mc.thePlayer != null && s12PacketEntityVelocity.getEntityID() == mc.thePlayer.getEntityId()) {
                  this.lastVelocityTime = System.currentTimeMillis();
                  switch ((VelocityMode)this.mode.getValue()) {
                     case PACKET:
                        if (!(Math.abs(this.horizontal.getValue()) > 0.0F) && !(Math.abs(this.vertical.getValue()) > 0.0F)) {
                           packetEvent.cancel();
                        } else {
                           s12PacketEntityVelocity.motionX = (int)(s12PacketEntityVelocity.motionX * (this.horizontal.getValue() / 100.0F));
                           s12PacketEntityVelocity.motionY = (int)(s12PacketEntityVelocity.motionY * (this.vertical.getValue() / 100.0F));
                           s12PacketEntityVelocity.motionZ = (int)(s12PacketEntityVelocity.motionZ * (this.horizontal.getValue() / 100.0F));
                        }
                     case PULL:
                  }
               }
            }

            if (packetEvent.getPacket() instanceof S27PacketExplosion) {
               S27PacketExplosion s27PacketExplosion = (S27PacketExplosion)packetEvent.getPacket();
               if (this.isStaffCheckEnabled()
                  && s27PacketExplosion.field_149153_g > 0.2
                  && mc.thePlayer.hurtTime == 0
                  && mc.thePlayer.hurtResistantTime == 0
                  && this.nearbyThreat == null
                  && !client.getModuleManager().get(Flight.class).isEnabled()) {
                  toasts.warningMessage("Staff test", "A staff possibly tested knockback on you!");
                  if (this.hypixelStaff.getSetting("Take-KB").asBoolean()) {
                     return;
                  }
               }

               if (this.mode.getValue() == VelocityMode.PACKET) {
                  if (!(Math.abs(this.horizontal.getValue()) > 0.0F) && !(Math.abs(this.vertical.getValue()) > 0.0F)) {
                     packetEvent.cancel();
                  } else {
                     s27PacketExplosion.field_149152_f = s27PacketExplosion.field_149152_f * (this.horizontal.getValue() / 100.0F);
                     s27PacketExplosion.field_149153_g = s27PacketExplosion.field_149153_g * (this.vertical.getValue() / 100.0F);
                     s27PacketExplosion.field_149159_h = s27PacketExplosion.field_149159_h * (this.horizontal.getValue() / 100.0F);
                  }
               }
            }
         }
      }
   };
   @EventHandler(JumpEvent.class)
   private Listener<JumpEvent> onJump = jumpEvent -> {
      if (this.mode.getValue() == VelocityMode.PULL) {
         if (mc.thePlayer == null || mc.thePlayer.isInWater()) {
            return;
         }

         if (mc.thePlayer.hurtTime > 0) {
            jumpEvent.cancel();
         }
      }
   };

   public Velocity(Category category) {
      super(category);
      this.setModuleColor(-11111286);
      this.addSettings(this.mode, this.hypixelStaff, this.push, this.horizontal, this.vertical, this.chance, water);
   }

   @Override
   public void updateDisplayName() {
      if (this.mode.getValue() == VelocityMode.PACKET) {
         this.setDisplayName(
            this.getName()
               + " "
               + EnumChatFormatting.GRAY
               + String.format(
                  "%s%%",
                  MathUtil.ONE_DECIMAL.format(this.vertical.getValue()) + String.format("%% %s", MathUtil.ONE_DECIMAL.format(this.horizontal.getValue()))
               )
         );
      } else {
         this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + "Pull");
      }

      super.updateDisplayName();
   }

   private boolean isStaffCheckEnabled() {
      return this.hypixelStaff.getSetting("Enable").asBoolean() && !client.getModuleManager().get(LongJump.class).isEnabled();
   }

   private Entity findNearbyThreat() {
      float f = 10.0F;
      Entity entity = null;

      for (Entity entity2 : mc.theWorld.loadedEntityList) {
         if (entity2 != mc.thePlayer
            && (entity2 instanceof EntityPlayer || entity2 instanceof IProjectile || GameUtil.isHostile(entity2) || GameUtil.isBoss(entity2))
            && (!(entity2 instanceof EntityPlayer) || !BotDetector.bots.contains(entity2))) {
            float g = mc.thePlayer.getDistanceToEntity(entity2);
            if (g < f) {
               entity = entity2;
               f = g;
            }
         }
      }

      return mc.thePlayer.hurtTime <= 0 && !mc.thePlayer.isBurning() ? entity : mc.thePlayer;
   }
}
