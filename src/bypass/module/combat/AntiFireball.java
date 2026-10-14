package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.Rotation;
import bypass.util.Rotator;
import bypass.util.Stopwatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vector3d;

public final class AntiFireball extends Module {
   private final Rotator A = new Rotator(false, 0.0F);
   private final NumberSetting<Double> range = new NumberSetting<>("Range", "Distance of blocks", 4.0, 3.0, 6.0);
   private final NumberSetting<Float> fov = new NumberSetting<>("FOV", "Distance of blocks", 120.0F, 0.1F, 360.0F);
   private final BooleanSetting aim = new BooleanSetting("Aim", "Aims to the fireball serversided", true);
   private final NumberSetting<Integer> swingLimit = new NumberSetting<>("Swing-Limit", "Limit of swings until stopping", 12, 1, 20);
   private final Stopwatch X = new Stopwatch();
   private final Stopwatch c = new Stopwatch();
   private EntityLargeFireball B;
   private int R;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGH)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         if (GameUtil.isRemoved(this.B) || this.X.hasElapsed(5000.0F) || this.R > this.swingLimit.getValue()) {
            this.B = null;
            this.R = 0;
         }

         for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLargeFireball) {
               EntityLargeFireball entityLargeFireball = (EntityLargeFireball)entity;
               double d = mc.thePlayer.getDistanceToEntity(entity);
               if (!(d > this.range.getValue() + 3.0) && this.Y(mc.thePlayer, entity)) {
                  this.B = entityLargeFireball;
                  this.X.reset();
                  break;
               }
            }
         }

         if (this.B != null) {
            if (!this.aim.getValue()) {
               this.O();
            } else {
               Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
               Vector3d vector3d2 = new Vector3d(this.B.posX, this.B.posY + this.B.height * 0.5 / 1.0, this.B.posZ);
               Rotation rotation = this.A.rotationTo(vector3d2, vector3d);
               float f = rotation.getYaw();
               float g = rotation.getPitch();
               float h = MathHelper.wrapAngleTo180_float(f - mc.thePlayer.rotationYaw);
               f = mc.thePlayer.rotationYaw + h;
               motionEvent.setYaw(f);
               motionEvent.setPitch(g);
            }
         }
      } else if (this.aim.getValue() && this.B != null) {
         this.O();
      }
   };

   public AntiFireball(Category category) {
      super(category);
      this.addSettings(this.range, this.fov, this.swingLimit, this.aim);
   }

   private void O() {
      if (this.R < this.swingLimit.getValue()) {
         mc.thePlayer.swingItem();
         mc.thePlayer.sendQueue.u(new C02PacketUseEntity(this.B, C02PacketUseEntity.Action.ATTACK));
         this.R++;
      }

      this.X.reset();
   }

   private boolean Y(EntityLivingBase entityLivingBase, Entity entity) {
      float f = this.fov.asFloat() / 2.0F;
      Vector3d vector3d = new Vector3d(entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.getEyeHeight(), entityLivingBase.posZ);
      Vector3d vector3d2 = new Vector3d(entity.posX, entity.posY + entity.height * 0.5 / 1.0, entity.posZ);
      Rotation rotation = this.A.rotationTo(vector3d2, vector3d);
      float g = (float)KillAura.rotationDistance(rotation);
      return g < f;
   }
}
