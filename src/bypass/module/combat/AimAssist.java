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
   private final Rotator rotator = new Rotator(false, 0.0F);
   private int resetTicks;
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         if (mc.thePlayer == null) {
            return;
         }

         if (mc.currentScreen != null) {
            return;
         }

         if (this.onMouse.getValue() && !Mouse.isButtonDown(0)) {
            return;
         }

         this.resetTicks = 0;
         if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            return;
         }

         AimTarget aimTarget = this.findBestTarget();
         if (aimTarget != null) {
            float[] floats = this.getRotationDifference(AimTarget.rotationOf(aimTarget));
            int i = this.speed.getValue();
            int j = this.speed.getValue();
            if (floats[0] > 0.0F) {
               i = -i;
            }

            if (floats[1] < 0.0F) {
               j = -j;
            }

            float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float g = f * f * f * 8.0F;
            float h = i * g;
            float f2 = j * g;
            byte b = 1;
            mc.thePlayer.setAngles(h, Math.abs(floats[1]) > 10.0F ? f2 * b : 0.0F);
         }
      }
   };

   public AimAssist(Category category) {
      super(category);
      this.addSettings(this.checks, this.range, this.fov, this.speed, this.onMouse);
   }

   private boolean isValidTarget(EntityPlayer entityPlayer) {
      if (!mc.thePlayer.canAttackPlayer(entityPlayer)) {
         return false;
      } else if (client.getRelationManager().isFriend(entityPlayer)) {
         return false;
      } else {
         return GameUtil.isOnSameTeam(mc.thePlayer, entityPlayer) && this.checks.getSetting("Teams").asBoolean()
            ? false
            : entityPlayer != mc.thePlayer && (!GameUtil.isFullyInvisible(entityPlayer) || this.checks.getSetting("Invisible").asBoolean());
      }
   }

   public float[] getRotationDifference(Rotation rotation) {
      return new float[]{MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - rotation.getYaw(), mc.thePlayer.rotationPitch - rotation.getPitch()};
   }

   private AimTarget findBestTarget() {
      AimTarget aimTarget = null;
      float f = this.fov.getValue().intValue() / 2.0F;

      for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
         if (this.isValidTarget(entityPlayer)) {
            AimTarget aimTarget2 = this.createAimTarget(entityPlayer);
            if (aimTarget2 != null && AimTarget.angleDistanceOf(aimTarget2) < f) {
               aimTarget = aimTarget2;
               f = AimTarget.angleDistanceOf(aimTarget2);
            }
         }
      }

      return aimTarget;
   }

   private AimTarget createAimTarget(EntityPlayer entityPlayer) {
      double d = this.range.asDouble();
      if (mc.thePlayer.getDistanceToEntity(entityPlayer) <= d) {
         Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
         Vector3d vector3d2 = new Vector3d(entityPlayer.posX, entityPlayer.posY + entityPlayer.height * 0.5 / 1.0, entityPlayer.posZ);
         Rotation rotation = this.rotator.rotationTo(vector3d2, vector3d);
         return new AimTarget(entityPlayer, rotation, (float)KillAura.rotationDistance(rotation));
      } else {
         return null;
      }
   }
}
