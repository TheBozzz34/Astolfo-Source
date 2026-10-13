package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.Rotation;
import bypass.util.Rotator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vector3d;
import org.lwjgl.input.Mouse;

public final class AimAssist extends Module {
   private final MapSetting checks = new MapSettingBuilder("Checks")
      .ordered()
      .description("entity checks")
      .add("Invisible", new BooleanSetting("Invisible", "Aims at invisible players", false))
      .add("Teams", new BooleanSetting("Teams", "Doesnt aim at teammates", false))
      .build();
   private final NumberSetting<Double> range = new NumberSetting<>("Range", "Distance of blocks", 4.0, 3.0, 6.0);
   private final NumberSetting<Integer> fov = new NumberSetting<>("FOV", "Field of view", 360, 0, 360);
   private final NumberSetting<Integer> speed = new NumberSetting<>("Speed", "Speed of AimAssist", 1, 0, 10);
   private final BooleanSetting onMouse = new BooleanSetting("On-Mouse", "On mouse only", true);
   private final Rotator c = new Rotator(false, 0.0F);
   private int H;
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.WORLD) {
         if (mc.thePlayer == null) {
            return;
         }

         if (mc.currentScreen != null) {
            return;
         }

         if (this.onMouse.getValue() && !Mouse.isButtonDown(0)) {
            return;
         }

         this.H = 0;
         if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            return;
         }

         AimTarget var5 = this.l();
         if (var5 != null) {
            float[] var6 = this.x(AimTarget.h(var5));
            int var7 = this.speed.getValue();
            int var8 = this.speed.getValue();
            if (var6[0] > 0.0F) {
               var7 = -var7;
            }

            if (var6[1] < 0.0F) {
               var8 = -var8;
            }

            float var9 = mc.gameSettings.Zm * 0.6F + 0.2F;
            float var10 = var9 * var9 * var9 * 8.0F;
            float var11 = var7 * var10;
            float var12 = var8 * var10;
            byte var13 = 1;
            mc.thePlayer.setAngles(var11, Math.abs(var6[1]) > 10.0F ? var12 * var13 : 0.0F);
         }
      }
   };
   private static final long d = 138865496681995L;

   public AimAssist(Category var1) {
      super(var1);
      this.addSettings(this.checks, this.range, this.fov, this.speed, this.onMouse);
   }

   private boolean u(EntityPlayer var1) {
      long var2 = d ^ 89975149020660L;
      long var10001 = var2 ^ 104280557620898L;
      int var4 = (int)((var2 ^ 104280557620898L) >>> 56);
      int var5 = (int)((var2 ^ 104280557620898L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (!mc.thePlayer.canAttackPlayer(var1)) {
         return false;
      } else if (client.getRelationManager().z(var1)) {
         return false;
      } else {
         return GameUtil.O(mc.thePlayer, var1) && this.checks.getSetting("Teams").asBoolean()
            ? false
            : var1 != mc.thePlayer && (!GameUtil.S(var1) || this.checks.getSetting("Invisible").asBoolean());
      }
   }

   public float[] x(Rotation var1) {
      return new float[]{MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - var1.getYaw(), mc.thePlayer.rotationPitch - var1.getPitch()};
   }

   private AimTarget l() {
      AimTarget var4 = null;
      float var5 = this.fov.getValue().intValue() / 2.0F;

      for (EntityPlayer var7 : mc.theWorld.playerEntities) {
         if (this.u(var7)) {
            AimTarget var8 = this.F(var7);
            if (var8 != null && AimTarget.R(var8) < var5) {
               var4 = var8;
               var5 = AimTarget.R(var8);
            }
         }
      }

      return var4;
   }

   private AimTarget F(EntityPlayer var1) {
      double var4 = this.range.asDouble();
      if (mc.thePlayer.getDistanceToEntity(var1) <= var4) {
         Vector3d var6 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
         Vector3d var7 = new Vector3d(var1.posX, var1.posY + var1.height * 0.5 / 1.0, var1.posZ);
         Rotation var8 = this.c.rotationTo(var7, var6);
         return new AimTarget(var1, var8, (float)KillAura.rotationDistance(var8));
      } else {
         return null;
      }
   }
}
