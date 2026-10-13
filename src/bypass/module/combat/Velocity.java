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
   private long f;
   private boolean l;
   private boolean c;
   private Entity h;
   private int y;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      if (var1x.getState() == EventState.PRE) {
         if (this.J()) {
            Entity var5 = this.r();
            if (var5 != null) {
               this.h = var5;
               this.y = 20;
            } else if (this.y-- <= 0) {
               this.h = null;
            }
         }

         if (this.mode.getValue().equals(VelocityMode.PULL)) {
            if (mc.thePlayer.movementInput.jump) {
               return;
            }

            if (this.f != 0L && System.currentTimeMillis() - this.f > 80L) {
               this.f = 0L;
            }

            if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0) {
               mc.thePlayer.onGround = true;
            }

            if (mc.thePlayer.hurtResistantTime > 0 && this.push.getSetting("Reduce-Y").asBoolean()) {
               mc.thePlayer.motionY -= 0.0144;
            }

            if (mc.thePlayer.hurtResistantTime >= 19) {
               double var7 = this.push.getSetting("Push").asFloat();
               mc.thePlayer.motionX /= var7;
               mc.thePlayer.motionZ /= var7;
            }
         }

         if (this.mode.getValue() == VelocityMode.LEGIT && mc.thePlayer.hurtTime == 9) {
            mc.thePlayer.motionX = mc.thePlayer.motionX * (this.horizontal.getValue() / 100.0F);
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * (this.horizontal.getValue() / 100.0F);
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      long var2 = e ^ 33839615652072L;
      long var4 = var2 ^ 122344089519344L;
      if (var1x.getDirection() == PacketDirection.RECEIVED) {
         float var7 = 100 - this.chance.getValue();
         boolean var8 = RandomUtils.RANDOM.nextFloat() > (var7 != 0.0F ? var7 / 100.0F : -0.1);
         if (var8) {
            if (mc.thePlayer != null && mc.theWorld != null && GameUtil.isOnHypixel() && this.J()) {
               if (GameUtil.scoreboardTitleContains("PIT") && !GuiIngame.E) {
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

            if (var1x.getPacket() instanceof S12PacketEntityVelocity) {
               S12PacketEntityVelocity var9 = (S12PacketEntityVelocity)var1x.getPacket();
               if (mc.thePlayer != null && var9.getEntityID() == mc.thePlayer.getEntityId()) {
                  this.f = System.currentTimeMillis();
                  switch ((VelocityMode)this.mode.getValue()) {
                     case PACKET:
                        if (!(Math.abs(this.horizontal.getValue()) > 0.0F) && !(Math.abs(this.vertical.getValue()) > 0.0F)) {
                           var1x.cancel();
                        } else {
                           var9.motionX = (int)(var9.motionX * (this.horizontal.getValue() / 100.0F));
                           var9.motionY = (int)(var9.motionY * (this.vertical.getValue() / 100.0F));
                           var9.motionZ = (int)(var9.motionZ * (this.horizontal.getValue() / 100.0F));
                        }
                     case PULL:
                  }
               }
            }

            if (var1x.getPacket() instanceof S27PacketExplosion) {
               S27PacketExplosion var10 = (S27PacketExplosion)var1x.getPacket();
               if (this.J()
                  && var10.field_149153_g > 0.2
                  && mc.thePlayer.hurtTime == 0
                  && mc.thePlayer.hurtResistantTime == 0
                  && this.h == null
                  && !client.getModuleManager().get(Flight.class).isEnabled()) {
                  toasts.X("Staff test", "A staff possibly tested knockback on you!");
                  if (this.hypixelStaff.getSetting("Take-KB").asBoolean()) {
                     return;
                  }
               }

               if (this.mode.getValue() == VelocityMode.PACKET) {
                  if (!(Math.abs(this.horizontal.getValue()) > 0.0F) && !(Math.abs(this.vertical.getValue()) > 0.0F)) {
                     var1x.cancel();
                  } else {
                     var10.field_149152_f = var10.field_149152_f * (this.horizontal.getValue() / 100.0F);
                     var10.field_149153_g = var10.field_149153_g * (this.vertical.getValue() / 100.0F);
                     var10.field_149159_h = var10.field_149159_h * (this.horizontal.getValue() / 100.0F);
                  }
               }
            }
         }
      }
   };
   @EventHandler(JumpEvent.class)
   private Listener<JumpEvent> onJump = var1x -> {
      if (this.mode.getValue() == VelocityMode.PULL) {
         if (mc.thePlayer == null || mc.thePlayer.isInWater()) {
            return;
         }

         if (mc.thePlayer.hurtTime > 0) {
            var1x.cancel();
         }
      }
   };
   private static final long e = 78015235312043L;

   public Velocity(Category var1) {
      super(var1);
      this.B(-11111286);
      this.addSettings(this.mode, this.hypixelStaff, this.push, this.horizontal, this.vertical, this.chance, water);
   }

   @Override
   public void p() {
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

      super.p();
   }

   private boolean J() {
      long var1 = e ^ 8558731286803L;
      long var3 = var1 ^ 130065192545547L;
      return this.hypixelStaff.getSetting("Enable").asBoolean() && !client.getModuleManager().get(LongJump.class).isEnabled();
   }

   private Entity r() {
      float var4 = 10.0F;
      Entity var5 = null;

      for (Entity var7 : mc.theWorld.loadedEntityList) {
         if (var7 != mc.thePlayer
            && (var7 instanceof EntityPlayer || var7 instanceof IProjectile || GameUtil.isHostile(var7) || GameUtil.isBoss(var7))
            && (!(var7 instanceof EntityPlayer) || !BotDetector.w.contains(var7))) {
            float var8 = mc.thePlayer.getDistanceToEntity(var7);
            if (var8 < var4) {
               var5 = var7;
               var4 = var8;
            }
         }
      }

      return mc.thePlayer.hurtTime <= 0 && !mc.thePlayer.isBurning() ? var5 : mc.thePlayer;
   }
}
