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
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         if (GameUtil.isRemoved(this.B) || this.X.hasElapsed(5000.0F) || this.R > this.swingLimit.getValue()) {
            this.B = null;
            this.R = 0;
         }

         for (Entity var6 : mc.theWorld.loadedEntityList) {
            if (var6 instanceof EntityLargeFireball) {
               EntityLargeFireball var7 = (EntityLargeFireball)var6;
               double var8 = mc.thePlayer.getDistanceToEntity(var6);
               if (!(var8 > this.range.getValue() + 3.0) && this.Y(mc.thePlayer, var6)) {
                  this.B = var7;
                  this.X.reset();
                  break;
               }
            }
         }

         if (this.B != null) {
            if (!this.aim.getValue()) {
               this.O();
            } else {
               Vector3d var11 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
               Vector3d var12 = new Vector3d(this.B.posX, this.B.posY + this.B.height * 0.5 / 1.0, this.B.posZ);
               Rotation var13 = this.A.rotationTo(var12, var11);
               float var14 = var13.getYaw();
               float var9 = var13.getPitch();
               float var10 = MathHelper.wrapAngleTo180_float(var14 - mc.thePlayer.rotationYaw);
               var14 = mc.thePlayer.rotationYaw + var10;
               var1x.setYaw(var14);
               var1x.setPitch(var9);
            }
         }
      } else if (this.aim.getValue() && this.B != null) {
         this.O();
      }
   };
   private static final long d = 86264799445271L;

   public AntiFireball(Category var1) {
      super(var1);
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

   private boolean Y(EntityLivingBase var1, Entity var2) {
      float var6 = this.fov.asFloat() / 2.0F;
      Vector3d var7 = new Vector3d(var1.posX, var1.posY + var1.getEyeHeight(), var1.posZ);
      Vector3d var8 = new Vector3d(var2.posX, var2.posY + var2.height * 0.5 / 1.0, var2.posZ);
      Rotation var9 = this.A.rotationTo(var8, var7);
      float var10 = (float)KillAura.rotationDistance(var9);
      return var10 < var6;
   }
}
